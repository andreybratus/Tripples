package it.unitn.ripples.core;

import java.io.IOException;
import java.util.List;

import com.google.api.services.youtube.model.SearchResult;

public interface IYouTubeSearch {
	public List<SearchResult> searchByKeyword(String keyword, int numberOfPages) throws IOException;
	public List<SearchResult> searchByTopic(String topic, String keyword, int numberOfPages);
}
