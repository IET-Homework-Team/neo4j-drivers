package neo4j.driver.testkit;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;
import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Session;
import org.neo4j.driver.v1.StatementResult;
import org.neo4j.driver.v1.Transaction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.test.TestGraphDatabaseFactory;

import neo4j.driver.util.PrettyPrinter;

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
	
	@Test
	public void test0() { //EmbeddedTestkitDriver tests, EmbeddedTestkitTransaction tests 
		File f = null;
		Driver drivertest = new EmbeddedTestkitDriver(f); //create with a file
		GraphDatabaseService returned=((EmbeddedTestkitDriver) drivertest).getUnderlyingDatabaseService();
		assertTrue(returned!=null); //we created with a null file, but we successfully got a gds
		try (Session session1 = drivertest.session("Bookmark")) {
		} catch(UnsupportedOperationException e){} //catch one exception
		AccessMode am = null;
		try (Session session2 = drivertest.session(am,"Bookmark2")) {
		} catch(UnsupportedOperationException e){} //catch another exception
		
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
	public void test1() { //EmbeddedTestkitSession UnsupportedOperationExceptions
		try (Driver driver = new EmbeddedTestkitDriver()) {
			try (Session session = driver.session()) {
				try (Transaction tx = session.beginTransaction("Bookmark")) {
				} catch(UnsupportedOperationException e) {}
				try (Transaction tx = session.beginTransaction()) { //only this creates a transaction
					try{ 
						tx.typeSystem(); //note
					} catch(UnsupportedOperationException e){}
					try{ 
						session.typeSystem(); //note, check these, didn't work in the other test!
					} catch(UnsupportedOperationException e){}
					try{
						session.lastBookmark();
					} catch(UnsupportedOperationException e){}
					try{
						session.reset();
					} catch(UnsupportedOperationException e){}
					try{
						session.readTransaction(null);
					} catch(UnsupportedOperationException e){}
					try{
						session.writeTransaction(null);
					} catch(UnsupportedOperationException e){}
				}
			}
		}
	}
	
	
	
	
}
