package neo4j.driver.reactive;

import static org.junit.Assert.*;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;

import neo4j.driver.reactive.impl.Neo4jReactiveDriver;
import neo4j.driver.reactive.interfaces.ReactiveDriver;
import neo4j.driver.reactive.interfaces.ReactiveSession;
import neo4j.driver.testkit.EmbeddedTestkitDriver;

import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.internal.InternalRecord;
import org.neo4j.driver.internal.value.InternalValue;

public class Neo4jReactiveDriverTest {

	protected Driver embeddedTestkitDriver;
	protected ReactiveDriver driver;
	protected ReactiveSession session;

	private void runUpdate(ReactiveSession session, String statementTemplate, Value parameters) {
		System.out.println("Running query: " + statementTemplate);
		System.out.println("With parameters: " + parameters);
		System.out.println();

		try (Transaction tx = session.beginTransaction()) {
			tx.run(statementTemplate, parameters);
			tx.success();
		}

		System.out.println();
	}

	@Before
	public void before() {
		embeddedTestkitDriver = new EmbeddedTestkitDriver();
		driver = new Neo4jReactiveDriver(embeddedTestkitDriver);
		session = driver.session();
	}

	@Test
	public void test1() throws Exception {
		final String PERSONS_QUERY = "persons";

		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				parameters("name", "Arthur", "title", "King"));
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				parameters("name", "Arthur", "title", "King"));
		runUpdate(session, "MATCH (a:Person {name: $name, title: $title}) DELETE a",
				parameters("name", "Arthur", "title", "King"));
	}

	@Test
	public void test2() throws Exception {
		final String PERSONS_QUERY = "persons";

		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Alice"));
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Bob"));
	}

	@Test
	public void test3() throws Exception {
		try (Transaction tx = session.beginTransaction()) {
			tx.run("CREATE (a:Person {name: $name})", parameters("name", "Alice"));
			tx.success();
		}

		final String PERSONS_QUERY = "persons";

		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Bob"));
	}
	
	@Test
	public void test4() throws Exception { //catch exception on existing query, if session is open
		session.reset();
		if (session.isOpen())
		try{
			final String PERSONS_QUERY = "persons";
			session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
			session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		} catch (IllegalStateException e) {}
		session.reset();
	}
	
	@Test
	public void test5() throws Exception { //test different run options than map
		try {
			Transaction tx = session.beginTransaction("Used a statement.");
			Statement stat = new Statement("CREATE (a:Person {name:'Bob'}) RETURN a.name AS name");
			StatementResult result = session.run(stat);
			tx.success();
			if(tx.isOpen()) tx.close();
			assertTrue(session.lastBookmark().equals("Used a statement."));
			
			//note, this is the record
			tx = session.beginTransaction("Used a record.");
			Record rec = result.next();
			session.run("CREATE (a:Person {name:$name})",rec);
			tx.success();
			if(tx.isOpen()) tx.close();
			assertTrue(session.lastBookmark().equals("Used a record."));
			
			/*//OVERWRITE THIS, value not finsihed
			tx = session.beginTransaction("Used a value.");
			Value val = rec.get(0);  //this returns with a Value
			session.run("CREATE (a:Person {name: $name})",val);
			if(tx.isOpen()) tx.close();
			assertTrue(session.lastBookmark().equals("Used a value."));
			*/
			
			tx = session.beginTransaction("This is not empty.");
			session.reset(); //Simulate that the session somehow reset.
			if(session.lastBookmark().equals(""))
			tx.failure();
			
			tx.typeSystem(); //To end the test, we throw an exception
		} catch (UnsupportedOperationException e) {}
		session.close();
	}
	
	@Test
	public void test6() throws Exception { //test exceptions on the transaction	
		TransactionWork tw = null;
		try{
			session.readTransaction(tw);	
		} catch(UnsupportedOperationException e){}
		try{
			session.writeTransaction(tw);	
		} catch(UnsupportedOperationException e){}
	}
}
