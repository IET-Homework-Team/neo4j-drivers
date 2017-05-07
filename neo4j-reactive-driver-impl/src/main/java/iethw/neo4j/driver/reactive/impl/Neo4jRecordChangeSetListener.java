package iethw.neo4j.driver.reactive.impl;

import iethw.neo4j.driver.reactive.data.RecordChangeSet;
import iethw.neo4j.driver.reactive.interfaces.RecordChangeSetListener;

public class Neo4jRecordChangeSetListener implements RecordChangeSetListener {

	protected String queryName;

	public Neo4jRecordChangeSetListener(String queryName) {
		this.queryName = queryName;
	}

	@Override
	public void notify(RecordChangeSet rcs) {
		System.out.println("A new changeSet appeared for listener '" + queryName + "':");
		System.out.println(rcs);
	}

}
