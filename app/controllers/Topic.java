package controllers;

import it.unitn.ripples.impl.SearchEngine;
import it.unitn.ripples.youtube.FreeBaseTopic;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import views.html.*;
import models.YoutubeData;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;

/**
 * Controller handling all requests for Keyword page
 * 
 *
 */

public class Topic extends Controller {
	
	/**
	 * Handles request for a Topic page
	 * @return
	 */
	
	public static Result topic() {
		Map<String, String> map = new HashMap<String, String>();
		return ok(topic.render(map, new YoutubeData()));

	}

	/**
	 * Handler for topic search.
	 * @return List of topics corresponding the search term
	 * @throws IOException
	 */
	
	public static Result getTopic() throws IOException {
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		String topicQuery = dynamicForm.get("topic");
		Map<String, String> map = FreeBaseTopic.getTopicId(topicQuery);

		return ok(topic.render(map, new YoutubeData()));
	}

	/**
	 * Handler to perform search on YouTube based on keyword and topic
	 * Topic is extracted from dropdown selection
	 * Keyword is extracted from the form.
	 * 
	 * @return {@link YoutubeData} list of videos
	 * @throws Exception
	 */
	
	public static Result getVideoTopic() throws Exception {
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		String keyword = dynamicForm.get("keyword");
		YoutubeData data = new SearchEngine().performSearch(keyword,
				dynamicForm.get("optiontopic"), "1");

		return ok(topic.render(null, data));
	}
}
