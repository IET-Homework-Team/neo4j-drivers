package neo4j.driver.util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.internal.value.NodeValue;
import org.neo4j.driver.internal.value.RelationshipValue;
import org.neo4j.driver.v1.Record;
import org.neo4j.driver.v1.Value;
import org.neo4j.driver.v1.Values;
import org.neo4j.driver.v1.types.Entity;
import org.neo4j.driver.v1.types.Node;
import org.neo4j.driver.v1.types.Relationship;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.test.TestGraphDatabaseFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import neo4j.driver.testkit.data.EmbeddedTestkitRecordFactory;
import neo4j.driver.util.PrettyPrinter;

public class PrettyPrintingNodeTest {
	@Test
	public void testName() throws Exception {
		//Node
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		
		final String resultForNode = PrettyPrinter.toString(node);
		
		System.out.println(resultForNode);
		
		assertTrue(resultForNode.equals("(:Person {name: \"John Doe\"})"));
		
		
		//Empty Node
		nodeProperties = ImmutableMap.of();
		node = new InternalNode(1, labels, nodeProperties);
		
		final String resultForEmptyNode = PrettyPrinter.toString(node);
		
		System.out.println(resultForEmptyNode);
		
		assertTrue(resultForEmptyNode.equals("(:Person)"));
		
		
		//Relationship
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		
		final String resultForRelationship = PrettyPrinter.toString(rel);
		
		System.out.println(resultForRelationship);
		
		assertTrue(resultForRelationship.equals("(1)-[:REL {weight: 2}]-(2)"));
		
		
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
		
		assertTrue(resultForSingleNode.equals("[(:Person {name: \"John \"Escaped Quotes\" Doe\"})]"));
		
		//With multiple elements
		testEntityList.add(node);
		
		final String resultForMultipleNodes = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForMultipleNodes);
		
		assertTrue(resultForMultipleNodes.equals("[(:Person {name: \"John \"Escaped Quotes\" Doe\"}),(:Person {name: \"John \"Escaped Quotes\" Doe\"})]"));
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
		
		assertTrue(resultForSingleRelationship.equals("[(1)-[:REL {weight: 2}]-(2)]"));
		
		//With multiple elements
		testEntityList.add(rel);
		
		final String resultForMultipleRelationships = PrettyPrinter.toString(testEntityList);
		
		System.out.println(resultForMultipleRelationships);
		
		assertTrue(resultForMultipleRelationships.equals("[(1)-[:REL {weight: 2}]-(2),(1)-[:REL {weight: 2}]-(2)]"));
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
	public void testWithValueClasses() {
		//NodeValue
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		Value value = EmbeddedTestkitRecordFactory.convert(node);
		
		final String resultForNodeValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForNodeValue);
		
		//RelationshipValue
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		value = EmbeddedTestkitRecordFactory.convert(rel);
		
		final String resultForRelationshipValue = PrettyPrinter.toString(value);
		
		System.out.println(resultForRelationshipValue);
		
		
		//Missing test for the PathValue type
		//...
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
