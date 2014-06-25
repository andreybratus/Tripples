package controllers;

import it.unitn.ripples.impl.SearchEngine;
import models.YoutubeData;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;

/**
 * Controller to handle requests for the Keyword page
 * 
 * @author A. Bratus, E. Bodnari
 *
 */

public class Keyword extends Controller {

	/**
	 * Handle request for an empty page, prior to search
	 * 
	 * @return
	 * @throws Exception
	 */
	
	public static Result keywords() throws Exception {
		YoutubeData data = new YoutubeData();

		return ok(keywords.render(data));
	}

	/**
	 * Handler controller for keyword submition, triggers search on YouTube
	 * based on keyword extracted from the form and returns a list of video
	 * corresponding to the search term.
	 *  
	 * @see {@link SearchEngine}
	 * @return List of found videos based on keyword
	 * @throws Exception
	 * @see {@link YoutubeData}
	 */
	
	public static Result keywordSubmit() throws Exception {
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();

		String keyword = dynamicForm.get("keyword");

		YoutubeData data = new SearchEngine().performSearch(keyword,
				dynamicForm.get("option"));

		return ok(keywords.render(data));
	}

}
