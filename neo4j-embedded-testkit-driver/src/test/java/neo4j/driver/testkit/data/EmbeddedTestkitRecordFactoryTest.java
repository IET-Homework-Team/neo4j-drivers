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
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Function;

import com.google.common.collect.ImmutableMap;

import neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;

public class EmbeddedTestkitRecordFactoryTest {
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
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		Map<String, Object> testElementList = ImmutableMap.of("Rel", rel);
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
	}
	
	//Test with an unsupported class
	@Test(expected=UnsupportedOperationException.class)
	public void test2() {
		Map<String, Object> testElementList = ImmutableMap.of("Unsupported", new UnsupportedTestClass());
		
		Record rec = EmbeddedTestkitRecordFactory.create(testElementList);
	}
}