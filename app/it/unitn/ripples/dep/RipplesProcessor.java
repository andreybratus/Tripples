package it.unitn.ripples.dep;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * <h2>Actual class that performs all the job of figuring out the Dependencies
 * between the sharers.</h2>
 * 
 * @see {@link RipplesEngine}
 * @author A. Bratus
 * 
 */

public class RipplesProcessor {

	/**
	 * Method performing all the transformation of the plain data obtained
	 * from the Google+ service, to the Tree Structure, prepared for visualization
	 * using D3js library
	 * 
	 * @param collection {@link DBCollection} of the content to be processed
	 * @return fin {@link JSONObject} tree strurcture ready for visualization
	 * @see {@link RipplesEngine}
	 */
	
	public static JSONObject buildDependencies(DBCollection collection) {
		List<JSONObject> originators = findOriginators(collection);
		List<JSONObject> result = new ArrayList<JSONObject>();

		for (int i = 0; i < originators.size(); i++) {
			JSONObject parent = originators.get(i);

			if (checkIfHasChildren(collection, parent.getString("postid"))) {
				parent = appendWithChildren(collection, parent);
			}

			result.add(parent);
		}
		JSONArray allChildren = new JSONArray();
		for (int i = 0; i < result.size(); i++) {

			JSONObject element = new JSONObject();
			element.put("name", result.get(i).getString("name"));

			// TODO set size depending on amount of children and color if
			// children exist

			int sizeCoef = ((result.get(i).optJSONArray("children") != null) ? result
					.get(i).optJSONArray("children").length()
					: 0);

			if (result.get(i).has("children")) {
				element.put("children", result.get(i).optJSONArray("children"));
			}
			element.put("size", sizeFormula(4, sizeCoef));
			element.put("src", result.get(i).getString("src"));
			allChildren.put(element);
		}

		JSONObject fin = new JSONObject();
		fin.put("name", "");
		fin.put("logo", "https://i.ytimg.com/vi/" + collection.getName()
				+ "/default.jpg");
		fin.put("logowidth", 35);
		fin.put("logoheight", 25);
		fin.put("children", allChildren);
		DBObject flare = (DBObject) JSON.parse(fin.toString());
		collection.insert(flare);

		return fin;
	}

	/**
	 * Version to build smaller visualization tree, by omiting standalone posts,
	 * that don't inherit from anybody.
	 * 
	 * @see {@link RipplesEngine}
	 * @param collection
	 * @return
	 */
	
	public static JSONObject buildDependenciesReduced(DBCollection collection) {
		List<JSONObject> originators = findOriginators(collection);
		List<JSONObject> result = new ArrayList<JSONObject>();

		for (int i = 0; i < originators.size(); i++) {
			JSONObject parent = originators.get(i);

			if (checkIfHasChildren(collection, parent.getString("postid"))) {
				JSONObject newparent = appendWithChildren(collection, parent);
				result.add(newparent);
			}

			// result.add(parent);
		}

		JSONArray allChildren = new JSONArray();
		for (int i = 0; i < result.size(); i++) {
			int sizeCoef = ((result.get(i).optJSONArray("children") != null) ? result
					.get(i).optJSONArray("children").length()
					: 1);
			JSONObject element = new JSONObject();
			element.put("name", result.get(i).getString("name"));
			element.put("children", result.get(i).optJSONArray("children"));
			element.put("size", sizeFormula(4, sizeCoef));
			element.put("src", result.get(i).getString("src"));
			allChildren.put(element);
		}

		JSONObject fin = new JSONObject();
		fin.put("name", "");
		fin.put("logo", "https://i.ytimg.com/vi/" + collection.getName()
				+ "/default.jpg");
		fin.put("logowidth", 35);
		fin.put("logoheight", 25);
		fin.put("children", allChildren);
		DBObject flare = (DBObject) JSON.parse(fin.toString());
		collection.insert(flare);
		return fin;
	}

	/**
	 * Helping function for figuring out dependencies of users sharing the post.
	 * <p>Takes a users postId and checks through all the sharers of the content
	 * if anybody has this postId, as its source.<p> <i> In other words, we want
	 * to build a tree structure, by adding next nodes to the node and recursively
	 * checking if there are still nodes until we arrive to the leave.
	 * <br><br>
	 * <b>Pay atention that this method is recursively calling itself.</b>
	 * @param collection {@link DBCollection} related to this content
	 * @param  identity {@link String} postId we want to build a tree from
	 * @return {@link JSONObject} Originator with all its children.
	 */
	
	
	public static JSONObject appendWithChildren(DBCollection collection,
			JSONObject identity) {

		BasicDBObject query = new BasicDBObject();
		query.put("src", identity.get("postid"));
		DBCursor cursor = collection.find(query);
		JSONArray children = new JSONArray();

		while (cursor.hasNext()) {

			JSONObject child = new JSONObject();
			JSONObject dbChild = new JSONObject(cursor.next().toString());
			if (checkIfHasChildren(collection, dbChild.getString("postid"))) {
				dbChild = appendWithChildren(collection, dbChild);
			}

			int sizeCoef = ((dbChild.optJSONArray("children") != null) ? dbChild
					.optJSONArray("children").length() : 0);

			child.put("name", dbChild.get("name"));
			child.put("children", dbChild.optJSONArray("children"));
			child.put("size", sizeFormula(3, sizeCoef));
			child.put("src", dbChild.get("src"));
			children.put(child);
		}

		identity.put("children", children);
		return identity;
	}

	/**
	 * Provided a Database collection containing information about a given content
	 * returns a list of users that shared the post originally.
	 * <br> Meaning they shared a given YouTube video to <b>Google+</b> using unique youtube URL.
	 *  <br><br><i>We mention them as <b>Originators</b></i>
	 * @param {@link DBCollection} Corresponding to this video
	 * @return <p> A List of {@link JSONObject} users originally published a given content </p>
	 */
	
	public static List<JSONObject> findOriginators(DBCollection collection) {
		BasicDBObject query = new BasicDBObject();
		query.put("originator", true);
		DBCursor cursor = collection.find(query);
		List<JSONObject> roots = new ArrayList<JSONObject>();

		while (cursor.hasNext()) {
			DBObject obj = cursor.next();
			JSONObject identity = new JSONObject(obj.toString());
			roots.add(identity);
		}
		return roots;
	}

	/**
	 * Takes an id of the post an checks whether somebody reshared it.
	 * <p> -Returns true if this post had been reshared by somebody</p>
	 * <p> -false otherwise</p>
	 * 
	 * @param <p>{@link DBColllection} corresponding to this video </p>
	 * @param <p>{@link String} postId of the interest</p> 
	 * @return boolean
	 */
	
	public static boolean checkIfHasChildren(DBCollection collection,
			String postId) {
		BasicDBObject query = new BasicDBObject();
		query.put("src", postId);
		DBCursor cursor = collection.find(query);

		if (cursor.size() == 0)
			return false;
		else
			return true;
	}

	/**
	 * Formula used to adaptively set the Bubble size on the visualization
	 * 
	 * Size increases linearly until 10 shares <br>
	 * Than starts increasing logarithmically until 50 shares <br>
	 * For more than 50 shares, the size is 50 px
	 * 
	 * @param min
	 * @param coeff
	 * @return Size of the buble
	 */

	public static double sizeFormula(int min, int coeff) {

		if (coeff == 0)
			return min + 2;
		if (coeff == 1)
			return min * 1.8;
		if (coeff > 1 && coeff <= 10)
			return min * coeff;
		if (coeff > 10 && coeff <= 50)
			return 6.5 * (Math.log(min * coeff) / Math.log(2));

		return 50;
	}

}
