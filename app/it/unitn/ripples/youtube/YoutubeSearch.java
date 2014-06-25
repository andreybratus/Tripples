package it.unitn.ripples.youtube;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.YouTube.Videos;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;
import com.google.api.services.youtube.model.Video;

import it.unitn.ripples.core.IYouTubeSearch;

/**
 * Actual class to perform search over Youtube videos.
 * 
 */

public class YoutubeSearch extends AbstractSearch implements IYouTubeSearch {

	// initialize logger

	private final static Logger fLogger = LoggerFactory
			.getLogger(YoutubeSearch.class);
	YouTube.Search.List search;
	YouTube YouTubeEntity;

	/**
	 * Default constructor, instantiates a Search Client Object
	 * 
	 * @throws Exception
	 */

	public YoutubeSearch() throws Exception {

		YouTubeEntity = new YouTube.Builder(HTTP_TRANSPORT, JSON_FACTORY,
				new HttpRequestInitializer() {
					public void initialize(HttpRequest request)
							throws IOException {
					}
				}).setApplicationName("YouRipples").build();
		getProperties();
		search = YouTubeEntity.search().list("id,snippet");
		search.setKey(apiKey);
		search.setType("video");
		search.setFields("items(id/videoId,snippet/title),nextPageToken");
		search.setMaxResults(NUMBER_OF_VIDEOS_RETURNED);
	}

	/**
	 * This method is used to search YoutubeData using a videoId.
	 * Information about the video is returned.
	 * <p>Multiple videoIds can be put into a single request by separating
	 * ids with comas</p>
	 * 
	 * @param id
	 * @return Information about the video 
	 * @throws IOException
	 */

	public Video searchSingleId(String id) throws IOException {
		Video videoInfo = null;
		Videos videos = YouTubeEntity.videos();
		YouTube.Videos.List videoSearch = videos.list("id,snippet,statistics");
		videoSearch.setId(id);
		videoSearch.setKey(apiKey);
		try {
		
			videoInfo = videoSearch.execute().getItems().get(0);
		
		} catch (Exception e) {
			fLogger.error("Error performing VideoSearch" + e);
		}

		return videoInfo;
	}
	
	/**
	 * Search by Keyword method. Number of results is controlled with number of
	 * pages, 10 results per one page.
	 * 
	 * @param {@link String}
	 * @param {@link String}
	 * 
	 * @return a list of {@link SearchResult}
	 * @throws IOException
	 */
	public List<SearchResult> searchByKeyword(String queryTerm, int numOfPages) {
		List<SearchResult> searchResultListLocal;
		List<SearchResult> searchResultList = new ArrayList<SearchResult>();
		search.setQ(queryTerm);
		String nextToken = null;
		int i = 0;
		try {
			do {
				if (i != 0)
					search.setPageToken(nextToken);
				SearchListResponse searchResponse = search.execute();
				nextToken = searchResponse.getNextPageToken();
				searchResultListLocal = searchResponse.getItems();
				i++;
				if (searchResultListLocal != null) {
					searchResultList.addAll(searchResultListLocal);
				}
			} while (i != numOfPages);
		} catch (Exception e) {
			fLogger.error("Error in KeywordSearch", e);
		}
		return searchResultList;
	}

	/**
	 * Search by Keyword and Topic method. Topic Id is obtained from FreeBase
	 * Library using {@link FreeBaseTopic} Number of results is controlled with
	 * number of pages, 10 results per one page.
	 * 
	 * @param topicsId
	 * @param {@link String}
	 * @param {@link String}
	 * 
	 * @return a list of {@link SearchResult}
	 */

	public List<SearchResult> searchByTopic(String keyword, String topicsId,
			int numOfPages) {
		List<SearchResult> searchResultListLocal;
		List<SearchResult> searchResultList = new ArrayList<SearchResult>();
		search.setQ(keyword);
		search.setTopicId(topicsId);
		String nextToken = null;
		int i = 0;
		try {
			do {
				if (i != 0)
					search.setPageToken(nextToken);
				SearchListResponse searchResponse = search.execute();
				nextToken = searchResponse.getNextPageToken();
				searchResultListLocal = searchResponse.getItems();
				i++;
				if (searchResultListLocal != null) {
					searchResultList.addAll(searchResultListLocal);
				}
			} while (i != numOfPages);
		} catch (Exception e) {
			fLogger.error("Error in Keyword + Topic Search", e);
		}
		return searchResultList;
	}

}
