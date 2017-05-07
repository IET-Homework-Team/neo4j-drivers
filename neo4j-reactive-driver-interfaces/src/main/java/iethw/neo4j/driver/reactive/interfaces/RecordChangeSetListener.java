package iethw.neo4j.driver.reactive.interfaces;

import iethw.neo4j.driver.reactive.data.RecordChangeSet;

public interface RecordChangeSetListener {

	void notify(RecordChangeSet rcs);

}
