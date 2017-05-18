package iethw.neo4j.driver.testkit;

import static org.junit.Assert.assertTrue;
import static org.neo4j.driver.v1.Values.parameters;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.neo4j.graphdb.Result;
import org.neo4j.driver.internal.types.InternalTypeSystem;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.Statement;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.exceptions.NoSuchRecordException;
import org.neo4j.driver.v1.types.TypeSystem;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import iethw.neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;
import iethw.neo4j.driver.testkit.data.EmbeddedTestkitStatementResult;
import iethw.neo4j.driver.util.PrettyPrinter;

public class EmbeddedTestkitDriverTest {

	@Test
	public void test() {
		try (Driver driver = new EmbeddedTestkitDriver()) {
			try (Session session = driver.session()) {
				try (Transaction transaction = session.beginTransaction()) {
					session.run("CREATE (n:Label)");
					StatementResult statementResult = session.run("MATCH (n:Label) RETURN n");
					while (statementResult.hasNext()) {
						Record record = statementResult.next();
						System.out.println(PrettyPrinter.toString(record));
					}
				}
			}
		}
	}

	@Test
	public void testNodeList() {
		try (Driver driver = new EmbeddedTestkitDriver()) {
			try (Session session = driver.session()) {
				try (Transaction transaction = session.beginTransaction()) {
					session.run("CREATE (n:Label)");
					StatementResult statementResult = session.run("MATCH (n) RETURN [n]");
					while (statementResult.hasNext()) {
						Record record = statementResult.next();
						System.out.println(PrettyPrinter.toString(record));
					}
				}
			}
		}
	}

	@Test
	public void testScalarList() {
		try (Driver driver = new EmbeddedTestkitDriver()) {
			try (Session session = driver.session()) {
				try (Transaction transaction = session.beginTransaction()) {
					session.run("CREATE (n:Label)");
					StatementResult statementResult = session.run("RETURN [1, 2] AS list");
					while (statementResult.hasNext()) {
						Record record = statementResult.next();
						System.out.println(PrettyPrinter.toString(record));
					}
				}
			}
		}
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionBookmarking1() {
		File f = null;
		Driver drivertest = new EmbeddedTestkitDriver(f); //create with a file
		Session s = drivertest.session("Bookmark");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionBookmarking2() {
		File f = null;
		Driver drivertest = new EmbeddedTestkitDriver(f); //create with a file
		AccessMode am = null;
		Session s = drivertest.session(am,"Bookmark2");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionGetTypeSystem() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		session.typeSystem();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionGetLastBookmark() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		session.lastBookmark();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionReset() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		session.reset();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionReadTransaction() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		session.readTransaction(null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionWriteTransaction() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		session.writeTransaction(null);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testSessionBeginTransactionBookmarked() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		Transaction tx = session.beginTransaction("Bookmark");
	}
	
	@Test
	public void testTransactionGetTypeSystem() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		Transaction tx = session.beginTransaction();
		TypeSystem ts = tx.typeSystem();
		
		assertTrue(ts == InternalTypeSystem.TYPE_SYSTEM);
	}
	
	@Test
	public void test0() { //EmbeddedTestkitDriver tests, EmbeddedTestkitTransaction tests 
		File f = null;
		Driver drivertest = new EmbeddedTestkitDriver(f); //create with a file
		GraphDatabaseService returned=((EmbeddedTestkitDriver) drivertest).getUnderlyingDatabaseService();
		assertTrue(returned!=null); //we created with a null file, but we successfully got a gds
		
		f = new File("DBStore");
		drivertest = new EmbeddedTestkitDriver(f); //create with a file
		returned=((EmbeddedTestkitDriver) drivertest).getUnderlyingDatabaseService();
		assertTrue(returned!=null);
		
		GraphDatabaseService gds = null;
		try (Driver driver1 = new EmbeddedTestkitDriver(gds)) { //create with a gds
			if(!driver1.isEncrypted())
			try (Session session = driver1.session()) {
				try (Transaction transaction = session.beginTransaction()) { //Transaction tests
					if(transaction.isOpen()){
						session.run("CREATE (n:Label)");
						transaction.success(); //if we are here, it was successful
					}
				}
				try (Transaction transaction2 = session.beginTransaction()) { //Transaction tests
					if(transaction2.isOpen()){
						session.run("CREATE (n:Label)");
						transaction2.failure(); //we simulate a failed transaction
					}
				}
			}
		}
			
		gds = new TestGraphDatabaseFactory().newImpermanentDatabase();	
		try (Driver driver2 = new EmbeddedTestkitDriver(gds)) { //create with a non-empty gds
		} //empty on purpose
	}
	
	@Test
	public void testPlainCreateStatement() { //EmbeddedTestkitSession run tests
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		
		assertTrue(session.isOpen());
		
		Transaction tx = session.beginTransaction();
		Statement stat = new Statement("CREATE (a:Person {name:'Bob'})");
		session.run(stat);
	}
	
	@Test
	public void testParametrizedCreateStatementWithRecord() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		
		assertTrue(session.isOpen());
		
		Transaction tx = session.beginTransaction();
		
		Map<String, Object> testElement = new HashMap<>();
		testElement.put("name", "Bob");
		Record rec = EmbeddedTestkitRecordFactory.create(testElement);
		session.run("CREATE (a:Person {name: $name})",rec);
	}
	
	@Test
	public void testParametrizedCreateStatementWithValue() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		
		assertTrue(session.isOpen());
		
		Transaction tx = session.beginTransaction();
		
		Value val = parameters("name", "Bob");
		session.run("CREATE (a:Person {name: $name})",val);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testStatementResultPeek() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		Transaction transaction = session.beginTransaction();
		session.run("CREATE (n1:Label),(n2:Label)");
		EmbeddedTestkitStatementResult sr = (EmbeddedTestkitStatementResult)session.run("MATCH (n:Label) RETURN n"); //Casting from StatementResult
		
		sr.peek();
	}
	
	//Test case for when someone tries to read more records than how many have been returned by running a query statement
	@Test(expected=NoSuchRecordException.class)
	public void testStatementResultOverReading() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		Transaction transaction = session.beginTransaction();
		session.run("CREATE (n1:Label),(n2:Label)");
		EmbeddedTestkitStatementResult sr = (EmbeddedTestkitStatementResult)session.run("MATCH (n:Label) RETURN n"); //Casting from StatementResult
		
		sr.single(); //returns
		sr.single(); //returns
		sr.single(); //should throw an exception
	}
	
	@Test
	public void testCreateStatementResultFromResult() {
		Result r = null;
		EmbeddedTestkitStatementResult srtest = new EmbeddedTestkitStatementResult(r);
	}
	
	@Test
	public void testStatementResultToRecordList() {
		Driver driver = new EmbeddedTestkitDriver();
		Session session = driver.session();
		Transaction transaction = session.beginTransaction();
		session.run("CREATE (n1:Label),(n2:Label)");
		EmbeddedTestkitStatementResult sr = (EmbeddedTestkitStatementResult)session.run("MATCH (n:Label) RETURN n"); //Casting from StatementResult
		
		List<Record> rec = sr.list(); //test listing, this is separate
	}
	
	@Test
	public void testEmbeddedTestkitStatementResultFromQueryResult() {
		try (Driver driver = new EmbeddedTestkitDriver()) {
			try (Session session = driver.session()) {
				try (Transaction transaction = session.beginTransaction()) {
					session.run("CREATE (n1:Label),(n2:Label)"); //two objects
					StatementResult statementResult = session.run("MATCH (n:Label) RETURN n"); //two result sets
					
					EmbeddedTestkitStatementResult sr = (EmbeddedTestkitStatementResult) statementResult;
					assertTrue(sr.keys().size()==1); //it has one column
					assertTrue(sr.hasNext()==true); //it has two nexts
					assertTrue(sr.consume()==null);
					assertTrue(sr.summary()==null);
					assertTrue(sr.list(null).size()==0); //expecting function call, with a zero size list as the return value
				}
			}
		}
	}
	
}
