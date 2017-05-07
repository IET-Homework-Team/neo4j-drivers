package iethw.neo4j.driver.reactive.interfaces;

import org.neo4j.driver.v1.Session;

import iethw.neo4j.driver.reactive.data.RecordChangeSet;

public interface ReactiveSession extends Session {

	RecordChangeSetListener registerQuery(String queryName, String querySpecification);

	RecordChangeSet getDeltas(String queryName);

}
