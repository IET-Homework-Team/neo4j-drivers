package neo4j.driver.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Entity;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;
import neo4j.driver.util.PrettyPrinter;

public class PrettyPrintingNodeTest {

	@Test
	public void testName() throws Exception {
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		
		final String result1 = PrettyPrinter.toString(node);
		
		System.out.println(result1);
		
		assertTrue(result1.equals("(:Person {name: \"John Doe\"})"));
		

		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		final String result2 = PrettyPrinter.toString(rel);
		
		System.out.println(result2);
		
		assertTrue(result2.equals("(1)-[:REL {weight: 2}]-(2)"));
		
		
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
		final String resultSingleNode = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultSingleNode);
		
		assertTrue(resultSingleNode.equals("[(:Person {name: \"John \"Escaped Quotes\" Doe\"})]"));
		
		//With multiple elements
		testEntityList.add(node);
		
		final String resultMultipleNodes = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultMultipleNodes);
		
		assertTrue(resultMultipleNodes.equals("[(:Person {name: \"John \"Escaped Quotes\" Doe\"}),(:Person {name: \"John \"Escaped Quotes\" Doe\"})]"));
	}
	
	//Test with Relationship list
	@Test
	public void testWithRelationshipList() {
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		List<Entity> testEntityList = new ArrayList<>();
		testEntityList.add(rel);
		
		//With a single list element
		final String resultSingleRelationship = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultSingleRelationship);
		
		assertTrue(resultSingleRelationship.equals("[(1)-[:REL {weight: 2}]-(2)]"));
		
		//With multiple elements
		testEntityList.add(rel);
		
		final String resultMultipleRelationships = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultMultipleRelationships);
		
		assertTrue(resultMultipleRelationships.equals("[(1)-[:REL {weight: 2}]-(2),(1)-[:REL {weight: 2}]-(2)]"));
	}
	
	//Test with Record
	@Test
	public void testWithRecord() {
		Map<String, Object> testElement = ImmutableMap.of("name", "Bob");
		Record rec = EmbeddedTestkitRecordFactory.create(testElement);
		
		final String result = PrettyPrinter.toString(rec);
		
		System.out.println(result);
		
		assertTrue(result.equals("<name=\"Bob\">"));
	}
	
	//Tests with different Value classes
	@Test
	public void testWithValue() {
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		Iterable<Value> values = node.values();
		Value value = values.iterator().next(); //NodeValue
		
		final String resultForNodeValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForNodeValue);
		
		
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		values = rel.values();
		value = values.iterator().next(); //RelationshipValue
		
		final String resultForRelationshipValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForRelationshipValue);
		
		
		//TODO: Missing test for the PathValue type
	}
	
	//Test with a Relationship instance without properties
	@Test
	public void testWithEmptyRelationshipProperties() {
		Map<String, Value> relationshipProperties = ImmutableMap.of();
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		final String result = PrettyPrinter.toString(rel);
		
		System.out.println(result);
		
		assertTrue(result.equals("(1)-[:REL]-(2)"));
	}
}
