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
	public void test0() { //EmbeddedTestkitDriver tests
		GraphDatabaseService gds = null;
		File f = null;
		Driver drivertest = new EmbeddedTestkitDriver(f); //create with a file
		GraphDatabaseService returned=((EmbeddedTestkitDriver) drivertest).getUnderlyingDatabaseService();
		assertTrue(returned!=null); //we created with a null file, but we successfully got a gds
		try (Session session1 = drivertest.session("Bookmark")) {
		} catch(UnsupportedOperationException e){} //catch one exception
		AccessMode am = null;
		try (Session session2 = drivertest.session(am,"Bookmark2")) {
		} catch(UnsupportedOperationException e){} //catch another exception
		
		try (Driver driver = new EmbeddedTestkitDriver(gds)) { //create with a gds
			if(!driver.isEncrypted())
			try (Session session = driver.session()) { //create a session, other functions already tested
			}
		}
	}
	
	
	
}
