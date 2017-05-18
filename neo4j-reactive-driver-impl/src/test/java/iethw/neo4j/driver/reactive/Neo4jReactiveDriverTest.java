package iethw.neo4j.driver.reactive;

import static org.junit.Assert.*;
import static org.neo4j.driver.v1.Values.parameters;

import java.util.Map;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.TransactionWork;
import org.neo4j.driver.v1.Value;

import iethw.neo4j.driver.reactive.impl.Neo4jReactiveDriver;
import iethw.neo4j.driver.reactive.impl.Neo4jReactiveTransaction;
import iethw.neo4j.driver.reactive.interfaces.ReactiveDriver;
import iethw.neo4j.driver.reactive.interfaces.ReactiveSession;
import iethw.neo4j.driver.testkit.EmbeddedTestkitDriver;
import iethw.neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;

import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.Record;

public class Neo4jReactiveDriverTest {
	private static final String PERSONS_QUERY = "persons";
	
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
	public void testStatementCreateAndDelete() throws Exception {
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				parameters("name", "Arthur", "title", "King"));
		runUpdate(session, "CREATE (a:Person {name: $name, title: $title})",
				parameters("name", "Arthur", "title", "King"));
		runUpdate(session, "MATCH (a:Person {name: $name, title: $title}) DELETE a",
				parameters("name", "Arthur", "title", "King"));
	}

	@Test
	public void testStatementCreate1() throws Exception {
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Alice"));
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Bob"));
	}

	@Test
	public void testStatementCreate2() throws Exception {
		try (Transaction tx = session.beginTransaction()) {
			tx.run("CREATE (a:Person {name: $name})", parameters("name", "Alice"));
			tx.success();
		}

		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		runUpdate(session, "CREATE (a:Person {name: $name})", parameters("name", "Bob"));
	}
	
	@Test(expected=IllegalStateException.class)
	public void testRegisterAlreadyRegisteredQuery() throws Exception { //catch exception on existing query, if session is open
		session.reset();
		
		assertTrue(session.isOpen());
		
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");
		session.registerQuery(PERSONS_QUERY, "MATCH (a:Person) RETURN a");

		session.reset(); //We can't get here, if everything goes as expected
	}
	
	@Test
	public void testPlainStatement() {
		Transaction tx = session.beginTransaction("Used a statement.");
		Statement stat = new Statement("CREATE (a:Person {name:'Bob'})");
		session.run(stat);
		tx.success();
		if(tx.isOpen()) tx.close();
		assertTrue(session.lastBookmark().equals("Used a statement."));
	}
	
	@Test
	public void testParametrizedStatementWithRecord() {
		Map<String, Object> testElement = new HashMap<>();
		testElement.put("name", "Bob");
		Transaction tx = session.beginTransaction("Used a record.");
		Record rec = EmbeddedTestkitRecordFactory.create(testElement);
		session.run("CREATE (a:Person {name: $name})",rec);
		tx.success();
		if(tx.isOpen()) tx.close();
		assertTrue(session.lastBookmark().equals("Used a record."));
	}
	
	@Test
	public void testParametrizedStatementWithValue() {
		Transaction tx = session.beginTransaction("Used a value.");
		Value val = parameters("name", "Bob");
		session.run("CREATE (a:Person {name: $name})",val);
		if(tx.isOpen()) tx.close();
		assertTrue(session.lastBookmark().equals("Used a value."));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testReadTransaction() {
		TransactionWork tw = null;
		
		session.readTransaction(tw);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testWriteTransaction() {
		TransactionWork tw = null;
		
		session.writeTransaction(tw);
	}
	
	@Test
	public void testForceTransactionFailure() {
		Transaction internalTransaction = session.beginTransaction();
		Neo4jReactiveTransaction t = new Neo4jReactiveTransaction(session, internalTransaction);
		
		t.failure();
	}
	
	@Test
	public void testSessionClose() {
		session.close();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testNeo4jReactiveSessionGetTypeSystem() {
		session.typeSystem();
	}
}
