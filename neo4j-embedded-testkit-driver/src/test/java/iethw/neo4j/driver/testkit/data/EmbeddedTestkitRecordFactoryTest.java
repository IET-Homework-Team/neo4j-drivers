package iethw.neo4j.driver.testkit.data;

import static org.junit.Assert.*;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import org.junit.Test;
import org.neo4j.driver.internal.AsValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.graphdb.Entity;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.google.common.collect.ImmutableMap;

import iethw.neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;

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
		public void setProperty(String key, Object value) { /*Empty on purspose*/ }

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
	
	@Test
	public void testCreateFromEntityMap() {
		GraphDatabaseService gds = new TestGraphDatabaseFactory().newImpermanentDatabase();
		Transaction tx = gds.beginTx();
		
		
		Node node1 = gds.createNode();
		node1.setProperty("Alma", true);
		Node node2 = gds.createNode();
		node2.setProperty("Beta", false);
		Relationship n1n2rel = node1.createRelationshipTo(node2, TestRelationshipTypes.KNOWS);
		
		Map<String, Object> testElementList = ImmutableMap.of("Node1", node1, "Node2", node2, "Rel", n1n2rel);
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
		
		
		List<String> keys = rec.keys();
		
		assertTrue("Node1".equals(keys.get(0)));
		assertTrue("Node2".equals(keys.get(1)));
		assertTrue("Rel".equals(keys.get(2)));
		
		
		List<Value> values = rec.values();
		
		org.neo4j.driver.v1.types.Node n = values.get(0).asNode();
		Map<String, Object> m = n.asMap();
		assertTrue(m.get("Alma").equals(true));
		
		n = values.get(1).asNode();
		m = n.asMap();
		assertTrue(m.get("Beta").equals(false));
		
		org.neo4j.driver.v1.types.Relationship r = values.get(2).asRelationship();
		assertTrue(r.startNodeId() == node1.getId());
		assertTrue(r.endNodeId() == node2.getId());
		
		
		tx.success();
		tx.close();
	}
	
	//Test with an unsupported class
	@Test(expected=UnsupportedOperationException.class)
	public void testCreateFromEntityMapWithUnsupportedEntity() {
		Map<String, Object> testElementList = ImmutableMap.of("Unsupported", new UnsupportedTestClass());
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
	}
}