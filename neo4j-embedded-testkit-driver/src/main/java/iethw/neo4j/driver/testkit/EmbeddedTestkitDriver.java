package iethw.neo4j.driver.testkit;

import java.io.File;

import org.neo4j.driver.v1.AccessMode;
import org.neo4j.driver.v1.Driver;
import org.neo4j.driver.v1.Session;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.test.TestGraphDatabaseFactory;

/**
 * Driver for testing an embedded Neo4j database through the neo4j-java-driver's
 * Driver interface
 */
public class EmbeddedTestkitDriver implements Driver {

	protected final GraphDatabaseService gds;

	public EmbeddedTestkitDriver() {
		gds = new TestGraphDatabaseFactory().newImpermanentDatabase();
	}

	public EmbeddedTestkitDriver(final GraphDatabaseService gds) {
		if(gds==null) //if we get a null gds, we create a new instance
		this.gds = new TestGraphDatabaseFactory().newImpermanentDatabase();	
		else
		this.gds = gds;
	}

	public EmbeddedTestkitDriver(final File storeDir) {
		if(storeDir==null) //if we get a null file, we create a new instance, like above
		gds = new TestGraphDatabaseFactory().newImpermanentDatabase();	
		else
		gds = new GraphDatabaseFactory().newEmbeddedDatabase(storeDir);
	}

	@Override
	public boolean isEncrypted() {
		return false;
	}

	@Override
	public EmbeddedTestkitSession session() {
		return session(AccessMode.WRITE);
	}

	@Override
	public EmbeddedTestkitSession session(AccessMode mode) {
		return new EmbeddedTestkitSession(gds, mode);
	}

	@Override
	public void close() {
        //Empty on purpose
	}

	@Override
	public Session session(String bookmark) {
		throw new UnsupportedOperationException("Bookmarks not supported.");
	}

	@Override
	public Session session(AccessMode mode, String bookmark) {
		throw new UnsupportedOperationException("Bookmarks not supported.");
	}

	public GraphDatabaseService getUnderlyingDatabaseService() {
		return gds;
	}

}
