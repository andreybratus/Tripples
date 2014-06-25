package models;

import com.mongodb.DBObject;

/**
 * MVC model for additional vide information that is used on the visualization
 * page.
 * 
 */

public class VideoInfo {
	public final String datePublished;
	public final String firstShared;
	public final String name;
	public final String numberOfShares;
	public final String id;
	public final String viewCount;

	/**
	 * Default constructor for this model, initializes final variables.
	 * @param {@link DBObject}
	 */
	
	public VideoInfo(DBObject info) {
		datePublished = (String) info.get("published");
		firstShared = (String) info.get("shared");
		name = (String) info.get("name");
		numberOfShares = (String) info.get("shares");
		id = (String) info.get("videoid");
		viewCount = (String) info.get("views");
	}

	/**
	 * PrettyPrint of Video Information
	 */
	
	public String toString() {
		StringBuilder builder = new StringBuilder("Video has: ");

		builder.append(" " + datePublished);
		builder.append(" " + name);
		builder.append(" " + firstShared);
		builder.append(" " + numberOfShares);
		return builder.toString();
	}

}
