package neo4j.driver.testkit.data;

import java.lang.Iterable;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import org.neo4j.driver.internal.AsValue;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.v1.types.MapAccessor;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.driver.v1.util.Function;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.google.common.collect.ImmutableMap;

import neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;

public class EmbeddedTestkitRecordFactoryTest {
	enum TestRelationshipTypes implements RelationshipType { CONTAINED_IN, KNOWS }
	
	private class UnsupportedTestClass implements Entity, AsValue
	{
		@Override
		public long getId() { return 0; }

		@Override
		public GraphDatabaseService getGraphDatabase() { return null; }

		@Override
		public boolean hasProperty(String key) { return false; }

		@Override
		public Object getProperty(String key) { return null; }

		@Override
		public Object getProperty(String key, Object defaultValue) { return null; }

		@Override
		public void setProperty(String key, Object value) { }

		@Override
		public Object removeProperty(String key) { return null; }

		@Override
		public Iterable<String> getPropertyKeys() { return new ArrayList<>(); }

		@Override
		public Map<String, Object> getProperties(String... keys) { return new HashMap<>(); }

		@Override
		public Map<String, Object> getAllProperties() { return new HashMap<>(); }
		
		
		@Override
		public Value asValue()
		{
			return Values.value(false);
		}
	}
	
	//Test with a Relationship instance
	@Test
	public void test1() {
		GraphDatabaseService gds = new TestGraphDatabaseFactory().newImpermanentDatabase();
		Transaction tx = gds.beginTx();
		Node node1 = gds.createNode();
		Node node2 = gds.createNode();
		Relationship n1n2rel = node1.createRelationshipTo(node2, TestRelationshipTypes.KNOWS);
		
		Map<String, Object> testElementList = ImmutableMap.of("Rel", n1n2rel);
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
		
		tx.success();
		tx.close();
	}
	
	//Test with an unsupported class
	@Test(expected=UnsupportedOperationException.class)
	public void test2() {
		Map<String, Object> testElementList = ImmutableMap.of("Unsupported", new UnsupportedTestClass());
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
	}
}