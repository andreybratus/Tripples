package controllers;

import models.DirectData;
import models.VideoInfo;
import it.unitn.ripples.Utils;
import it.unitn.ripples.impl.DatabaseController;
import it.unitn.ripples.impl.RipplesEngine;
import play.data.DynamicForm;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.*;
/**
 * Class containing controllers related to Direct video Search
 * 
 * @author A. Bratus, E. Bodnari
 */
public class Direct extends Controller {

	/**
	 * Controller that is triggered whenever a request
	 * for Direct page comes.
	 * 
	 * @return play.mvc.Result renders direct.scala.html
	 * 
	 */
	
	public static Result direct() {
		return ok(direct.render(null));
	}

	/**
	 * Controller handles request for searching a video provided
	 * by the URL. URL is extracted from a Dynamic form 
	 * 
	 * @return play.mvc.Result renders direct.scala.html
	 * @throws Exception
	 */
	
	public static Result directSearch() throws Exception {
		DatabaseController dbCtrl = null;
		DynamicForm dynamicForm = DynamicForm.form().bindFromRequest();
		String url = dynamicForm.get("direct").trim();
		if (url.length() > 11)
		url = url.substring(url.length()-11, url.length());
		RipplesEngine engine = new RipplesEngine();
		if(url.length() < 11)
		return internalServerError( url + " Bad Request");
		String numberOfShares = engine.makeRipples(url).toString();
		try {
		 dbCtrl = new DatabaseController();
		VideoInfo info = dbCtrl.getVideoInfo(url.toString());
		return ok(direct.render(new DirectData(numberOfShares, url,info.name)));
		} catch(Exception e){
			return internalServerError( url + " Bad Request");
		} finally{
			Utils.safeClose(dbCtrl, false);
		}
	}

}
