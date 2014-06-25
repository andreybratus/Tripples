package models;

import java.util.ArrayList;
import java.util.List;

import com.google.api.services.youtube.model.SearchResult;

/**
 * Model Sent to a search page of the client to show the search results - Video
 * Name - Video Id
 * 
 * 
 */

public class YoutubeData {

	public ArrayList<String> ids;
	public ArrayList<String> names;

	/**
	 * Constructor Generates a model form a {@link List} of {@link SearchResult}
	 * 
	 * @param a
	 *            {@link List} of {@link SearchResult}
	 * @see {@link YoutubeSearch}
	 */

	public YoutubeData(List<SearchResult> result) {
		ids = new ArrayList<String>();
		names = new ArrayList<String>();
		for (int i = 0; i < result.size(); i++) {
			names.add(result.get(i).getSnippet().getTitle());
			ids.add(result.get(i).getId().getVideoId());
		}
	}

	/**
	 * Default constructor to create an empty model, in case of no
	 * searchresults.
	 */

	public YoutubeData() {
		ids = new ArrayList<String>();
		names = new ArrayList<String>();
	}

	/**
	 * Method to clean the data in the model
	 * 
	 */

	public void clearData() {
		ids.clear();
		names.clear();
	}
}
