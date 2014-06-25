package it.unitn.ripples.impl;

import it.unitn.ripples.Utils;
import it.unitn.ripples.dep.RipplesProcessor;
import it.unitn.ripples.youtube.YoutubeSearch;


import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.services.youtube.model.Video;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

/**
 * Basic engine to control the Ripples: - Acquisition, - Processing - insertion
 * to the Database.
 * 
 * This class is instantiated for each Ripples Request coming from the client.
 */

public class RipplesEngine {

	/**
	 * Main method for {@link RipplesEngine} class, performs all the necessary steps to prepare
	 * data for a given video: <br>
	 *  - Check if data about this video is already in the database <br>
	 *  - Get Ripples Data for new videos <br>
	 *  - Get Video Information from Youtube (viewCount, date published) <br>
	 * 
	 * <p><i>due to visualization scalability issue, in case numberOfShares is
			 larger than 1000, build visualization in a way that only users that
			 have been reposted are shown. </i></p>
	 * 
	 * @param id
	 * @return numberOfShares
	 * @throws Exception 
	 * @author A. Bratus, E. Bodnari
	 */

	public Integer makeRipples(String id) throws Exception {
		
		DatabaseController dbCtrl = new DatabaseController();
		
		YoutubeSearch youtube = new YoutubeSearch();
		
		RipplesRequest client;

		Integer numberOfShares = null;

		if (!dbCtrl.db.collectionExists(id)) {
			
			client = new RipplesRequest();
			JSONArray jsonRipples = client.requestRipples(id);

			if (jsonRipples == null) {
				Utils.safeClose(client, false);
				Utils.safeClose(dbCtrl, false);
				return 0;
			}
			
			//request additional information about the video
			Video videoData = youtube.searchSingleId(id);

			List<DBObject> dbData = dbCtrl.genDbData(jsonRipples, videoData, id);
			
			DBCollection collection = dbCtrl.sendRipplesToDB(dbData, id);
			
			
			
			JSONObject flare;

			// due to visualization scalability issue, in case numberOfShares is
			// larger than 1000, build visualization in a way that only users that
			// have been reposted are shown.
			
			if (jsonRipples.length() > 1000) {

				flare = RipplesProcessor.buildDependenciesReduced(collection);

			} else {

				flare = RipplesProcessor.buildDependencies(collection);

			}
			
			BasicDBObject result = new BasicDBObject();
			result.put(id, "result");
			result.put("flare", flare.toString());
			
			BasicDBObject size = new BasicDBObject();
			
			numberOfShares = dbData.size() - 1;
			size.put("shares", numberOfShares);
			size.put(id, "size");
			
			collection.insert(result, size);
			
			Utils.safeClose(client, false);
			Utils.safeClose(dbCtrl, false);
		
		} else {
			numberOfShares = dbCtrl.getNumberofShares(id);

			if (numberOfShares == null) {
				dbCtrl.removeCollection(id);
			}

			dbCtrl.close();

			return numberOfShares;
		}

		return numberOfShares;
	}
	
}
