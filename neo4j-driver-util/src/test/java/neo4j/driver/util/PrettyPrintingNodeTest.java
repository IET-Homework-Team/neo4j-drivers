package neo4j.driver.util;

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
		System.out.println(PrettyPrinter.toString(node));

		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		System.out.println(PrettyPrinter.toString(rel));
	}
	
	//Test with Node list
	@Test
	public void test0() {
		List<String> labels = ImmutableList.of("Person");
		Map<String, Value> nodeProperties = ImmutableMap.of("name", Values.value("John Doe"));
		Node node = new InternalNode(1, labels, nodeProperties);
		List<Entity> testEntityList = ImmutableList.of(node);
		System.out.println(PrettyPrinter.toString(testEntityList));
	}
	
	//Test with Relationship list
	@Test
	public void test1() {
		Map<String, Value> relationshipProperties = ImmutableMap.of("weight", Values.value(2));
		Relationship rel = new InternalRelationship(5, 1, 2, "REL", relationshipProperties);
		List<Entity> testEntityList = ImmutableList.of(rel);
		System.out.println(PrettyPrinter.toString(testEntityList));
	}
	
	//Test with Record
	@Test
	public void test2() {
		Map<String, Object> testElement = ImmutableMap.of("name", "Bob");
		Record rec = EmbeddedTestkitRecordFactory.create(testElement);
		System.out.println(PrettyPrinter.toString(rec));
	}
}
