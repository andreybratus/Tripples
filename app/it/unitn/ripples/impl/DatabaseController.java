package it.unitn.ripples.impl;

import it.unitn.ripples.core.IDatabaseController;

import java.io.Closeable;
import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import models.VideoInfo;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.services.youtube.model.Video;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.util.JSON;

/**
 * Class fully responsible for the interaction with the MongoDB
 * database. provides all the necessary methods to work with the database.
 *
 * @author A. Bratus, E. Bodnari
 *
 */


public class DatabaseController implements IDatabaseController, Closeable {

	private final static Logger logger = LoggerFactory
			.getLogger(DatabaseController.class);
	private MongoClient sMongo;
	public DB db;

	/**
	 * Default constructor for the {@link DatabaseController}
	 * uses JVM environmental variables to set the HOST:PORT adress of
	 * the MongoDB. 
	 * If such env variables are not set, it defaults to:
	 * HOST {@code localhost}, PORT {@code 27017}
	 * 
	 */
	
	
	public DatabaseController() {
		String host = System.getProperty("db.host", "mule.mine.nu");
		Integer port = Integer.parseInt(System.getProperty("db.port", "27017"));
		try {
			sMongo = new MongoClient(host, port);
			logger.info("Database controller started on:" + host + ":" + port);
			db = sMongo.getDB("RipplesDB");
		} catch (UnknownHostException e) {
			logger.error("Can't connect ot Databse" + host + port + "\n" + e);
		}
	}


	
	/**
	 * Method sending Ripples Data to the database,
	 * creating a collection that is named {@code videoId}
	 * and documents corresponding to each sharer
	 * 
	 * @param ripplesData, String videoId
	 * @return {@code DBCollection} inserted collection
	 * 
	 */
	
	
	public DBCollection sendRipplesToDB(List<DBObject> dbObject, String videoId) {
		DBCollection collection = db.getCollection(videoId);

		try {
			collection.insert(dbObject);
		} catch (Exception e) {
			logger.error("Exception DB:", e);
			System.err.println("DB PRoblem!!!" + e);
		}
		return collection;
	}

	
	/**
	 * Method used to drop all the contend from the database
	 * @param dbName
	 * 
	 */
	
	public void dropDatabase(String dbName) {
		try {
			sMongo.dropDatabase(dbName);
			logger.info("Database Dropped: " + dbName);
		} catch (MongoException e) {
			logger.error("Failde to drop DB: " + dbName, e);
		}
	}

	
	/**
	 * Data from Google+ ripples often comes duplicated
	 * this method is used to remove duplicate entities
	 * if the same postId is met more than once.
	 * 
	 * @param jsonRipples
	 * @return cleanjsonRipples
	 */
	
	public JSONArray eliminateDuplciates(JSONArray jsonRipples) {

		List<String> postids = new ArrayList<String>();

		for (int i = 0; i < jsonRipples.length(); i++) {
			String postid = jsonRipples.getJSONArray(i).getString(2);
			if (!postids.contains(postid))
				postids.add(postid);
			else {
				jsonRipples.remove(i);
				i--;
			}
		}
		return jsonRipples;
	}

	
	/**
	 * Prepare RipplesDAta in a way to be inserted to the database.
	 * 
	 * @param inRipples
	 * @param id of the Video
	 * @return List of {@link DBObject} to be furhther insereted to DB.
	 */
	
	public List<DBObject> genDbData(JSONArray inRipples,Video videoData, String id) {
		List<DBObject> dbObject = new ArrayList<DBObject>();
		JSONArray jsonRipples = eliminateDuplciates(inRipples);
		Integer lowestUnixTime = jsonRipples.getJSONArray(0).getInt(3);
		for (int i = 0; i < jsonRipples.length(); i++) {
			String check = (String) jsonRipples.getJSONArray(i).get(4);
			Integer unixTime = jsonRipples.getJSONArray(i).getInt(3);

			if (unixTime < lowestUnixTime)
				lowestUnixTime = unixTime;
			if (check.equals("novalue") || check.contains(":http")) {
				jsonRipples.getJSONArray(i).put(4, "novalue");
				jsonRipples.getJSONArray(i).put(6, true);
			} else {
				jsonRipples.getJSONArray(i).put(6, false);
			}
			JSONArray x = jsonRipples.getJSONArray(i);
			JSONObject ripp = x.toJSONObject(new JSONArray(Arrays.asList(
					"name", "id", "postid", "time", "src", "language",
					"originator")));
			dbObject.add((DBObject) JSON.parse(ripp.toString()));
		}

		Date utt = new Date((long) lowestUnixTime * 1000L);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		String date = sdf.format(utt);
		Integer numberOfShares = dbObject.size();
		DBObject additionalInfo = new BasicDBObject();
		
		
		// Generate information about this video
		additionalInfo.put("videoid", id);
		additionalInfo.put("published", videoData.getSnippet().getPublishedAt().toString().substring(0, 10));
		additionalInfo.put("name", videoData.getSnippet().getTitle());
		additionalInfo.put("shared", date);
		additionalInfo.put("shares", numberOfShares.toString());
		additionalInfo.put("views",videoData.getStatistics().getViewCount().toString());
		dbObject.add(additionalInfo);
		
		return dbObject;
	}

	/**
	 * Query the Database to give a number of shares for a given
	 * YouTube Video
	 * 
	 * @param id
	 * @return numberOfShares
	 */
	
	public Integer getNumberofShares(String id) {
		Integer result = null;
		try {
			DBCollection collection = db.getCollection(id);
			BasicDBObject query = new BasicDBObject();
			query.put(id, "size");
			DBObject size = collection.findOne(query);

			result = (Integer) size.get("shares");
		} catch (Exception e) {
			logger.error("Error getting Number of Shares: for " + id, e);
			return null;
		}
		return result;
	}

	/**
	 * Get Data prepared for visualization for a given youtube Video.
	 * 
	 * @param id of the video.
	 * @return {@link String } flare
	 */
	
	public String getFlare(String id) {
		DBCollection collection = db.getCollection(id);
		BasicDBObject query = new BasicDBObject();
		query.put(id, "result");
		String flare = collection.findOne(query).get("flare").toString();
		return flare;

	}

	
	/**
	 * Remove a collection related to a given Video Id.
	 * 
	 * @param id
	 */
	
	public void removeCollection(String id) {
		db.getCollection(id).drop();
	}

	
	

	/**
	 * Update additional information about the video
	 * If a given key already exists its value will be update
	 * otherwise a new key value pair will be added to the collection.
	 * 
	 * 
	 * @param {@link String} key of the information 
	 * @param {@link String} value
	 * @param {@link String} videoId
	 */
	

	/**
	 * Retrieve additional inforamtion about the video from DB.
	 * 
	 * 
	 * @param {@link String} id of the video
	 * @return {@link VideoInfo} model
	 */
	
	
	public VideoInfo getVideoInfo(String id) {

		DBCollection collection = db.getCollection(id);

		DBObject search = new BasicDBObject();
		search.put("videoid", id);
		DBObject info = collection.findOne(search);
		if (info == null) return null;
		VideoInfo inf = new VideoInfo(info);
		return inf;
	}

	
	/**
	 * Method to close the {@link DatabaseController}
	 * It has to be always closed, to prevent resource leakage.
	 * 
	 * @see {@link Closeable}
	 */
	
	@Override
	public void close() throws IOException {
		sMongo.close();
		logger.info("Database client closed");
	}
}
