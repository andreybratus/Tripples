package it.unitn.ripples.core;

import java.util.List;

import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public interface IDatabaseController {
	
	DBCollection sendRipplesToDB(List<DBObject> dbObject,String collName);
	void dropDatabase(String dbName);
}
