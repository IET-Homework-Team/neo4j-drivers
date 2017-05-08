package iethw.neo4j.driver.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.neo4j.driver.internal.AsValue;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Entity;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Path;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.driver.v1.util.Function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import iethw.neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;
import iethw.neo4j.driver.util.PrettyPrinter;

public class PrettyPrintingNodeTest {
	private class UnsupportedTestEntity implements Entity, AsValue
	{
		@Override
		public long id() { return 0; }

		@Override
		public Iterable<String> keys() { return null; }

		@Override
		public boolean containsKey(String key) { return false; }

		@Override
		public Value get(String key) { return null; }

		@Override
		public int size() { return 0; }

		@Override
		public Iterable<Value> values() { return null; }

		@Override
		public <T> Iterable<T> values(Function<Value, T> mapFunction) { return null; }

		@Override
		public Map<String, Object> asMap() { return null; }

		@Override
		public <T> Map<String, T> asMap(Function<Value, T> mapFunction) { return null; }
		
		
		@Override
		public Value asValue()
		{
			return Values.value(false);
		}
	}
	
	@Test
	public void testName() throws Exception {
		//Node
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		
		final String resultForNode = PrettyPrinter.toString(node);
		
		System.out.println(resultForNode);
		
		assertTrue("(:Person {name: \"John Doe\"})".equals(resultForNode));
		
		
		//Empty Node
		nodeProperties = ImmutableMap.of();
		node = new InternalNode(1, labels, nodeProperties);
		
		final String resultForEmptyNode = PrettyPrinter.toString(node);
		
		System.out.println(resultForEmptyNode);
		
		assertTrue("(:Person)".equals(resultForEmptyNode));
		
		
		//Relationship
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		final String resultForRelationship = PrettyPrinter.toString(rel);
		
		System.out.println(resultForRelationship);
		
		assertTrue("(1)-[:REL {weight: 2}]-(2)".equals(resultForRelationship));
	}
	
	//Test with Node list
	@Test
	public void testWithNodeList() {
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John \"Escaped Quotes\" Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		List<Entity> testEntityList = new ArrayList<>();
		testEntityList.add(node);
		
		//With a single list element
		final String resultForSingleNode = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForSingleNode);
		
		assertTrue("[(:Person {name: \"John \"Escaped Quotes\" Doe\"})]".equals(resultForSingleNode));
		
		//With multiple elements in a way that the second element has no properties and the third one has no labels and no properties
		nodeProperties = ImmutableMap.of();
		node = new InternalNode(1, labels, nodeProperties);
		testEntityList.add(node);
		
		labels = ImmutableList.of();
		node = new InternalNode(1, labels, nodeProperties);
		testEntityList.add(node);
		
		final String resultForMultipleNodes = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForMultipleNodes);
		
		assertTrue("[(:Person {name: \"John \"Escaped Quotes\" Doe\"}),(:Person),()]".equals(resultForMultipleNodes));
	}
	
	//Test with Relationship list
	@Test
	public void testWithRelationshipList() {
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		List<Entity> testEntityList = new ArrayList<>();
		testEntityList.add(rel);
		
		//With a single list element
		final String resultForSingleRelationship = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForSingleRelationship);
		
		assertTrue("[(1)-[:REL {weight: 2}]-(2)]".equals(resultForSingleRelationship));
		
		//With multiple elements
		testEntityList.add(rel);
		
		final String resultForMultipleRelationships = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForMultipleRelationships);
		
		assertTrue("[(1)-[:REL {weight: 2}]-(2),(1)-[:REL {weight: 2}]-(2)]".equals(resultForMultipleRelationships));
	}
	
	//Test with UnsupportedTestEntity list
	@Test
	public void testWithUnsupportedEntityList() {
		UnsupportedTestEntity entity = new UnsupportedTestEntity();
		List<Entity> testEntityList = new ArrayList<>();
		testEntityList.add(entity);
		
		//With a single list element
		final String resultForSingleUnsupportedEntity = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForSingleUnsupportedEntity);
		
		assertTrue("[]".equals(resultForSingleUnsupportedEntity));
		
		//With multiple elements
		testEntityList.add(entity);
		
		final String resultForMultipleUnsupportedEntities = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForMultipleUnsupportedEntities);
		
		assertTrue("[]".equals(resultForMultipleUnsupportedEntities));
	}
	
	//Test with Record
	@Test
	public void testWithRecord() {
		Map<String, Object> testElement = ImmutableMap.of("name", "Bob");
		Record rec = EmbeddedTestkitRecordFactory.create(testElement);
		
		final String result = PrettyPrinter.toString(rec);
		
		System.out.println(result);
		
		assertTrue("<name=\"Bob\">".equals(result));
	}
	
	//Test with Path (!not a real path, just a substitution with null, because we expect it to throw an exception)
	@Test(expected=UnsupportedOperationException.class)
	public void testWithPath() {
		Path nullPath = null;
		PrettyPrinter.toString(nullPath);
	}
	
	//Tests with different Value classes
	@Test
	public void testWithValueClasses() {
		//NodeValue
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		Value value = new NodeValue(node);
		
		final String resultForNodeValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForNodeValue);
		
		//RelationshipValue
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		value = new RelationshipValue(rel);
		
		final String resultForRelationshipValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForRelationshipValue);
		
		
		//Missing test case for PathValue
		//...
	}
	
	//Test with a Relationship instance without properties
	@Test
	public void testWithEmptyRelationshipProperties() {
		Map<String, Value> relationshipProperties = ImmutableMap.of();
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		final String result = PrettyPrinter.toString(rel);
		
		System.out.println(result);
		
		assertTrue("(1)-[:REL]-(2)".equals(result));
	}
}
