package it.unitn.ripples.impl;

import it.unitn.ripples.Utils;
import it.unitn.ripples.youtube.YoutubeSearch;

import java.util.List;

import models.YoutubeData;

import com.google.api.services.youtube.model.SearchResult;

/**
 * High level engine to perform search on YouTube.
 * Controls all the necessary operations.
 * 
 * @author A. Bratus E. Bodnari
 *
 */


public class SearchEngine {
//	private static final Logger sLogger = LoggerFactory.getLogger(Engine.class);
	
	/**
	 * Method to perform a search over Youtube using the keyword
	 * 
	 * Number of results per page: 10; Number of results is controlled with number of pages.
	 * 
	 * @param keyword
	 * @param pages
	 * @return {@link YoutubeData}
	 * @throws Exception
	 */
	
	
	public YoutubeData performSearch(String keyword, String pages)
			throws Exception {
		DatabaseController dbCtrl = new DatabaseController();
		List<SearchResult> list = new YoutubeSearch().searchByKeyword(keyword,
				Integer.parseInt(pages));
		
		Utils.safeClose(dbCtrl, false);
		return new YoutubeData(list);
	}

	/**
	 * Method performing search for YoutubeVideos based on topic
	 * 
	 * Topic Id is taken from the FreeBase.
	 * 
	 * @param keyword
	 * @param topicId
	 * @param pages
	 * @return {@link YoutubeData}
	 * @throws Exception
	 */
	
	
	public YoutubeData performSearch(String keyword, String topicId,
			String pages) throws Exception {
		DatabaseController dbCtrl = new DatabaseController();
		List<SearchResult> list = new YoutubeSearch().searchByTopic(keyword,
				topicId, Integer.parseInt(pages));
		
		Utils.safeClose(dbCtrl, false);
		return new YoutubeData(list);
	}


}
