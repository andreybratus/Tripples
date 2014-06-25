package controllers;

import it.unitn.ripples.impl.DatabaseController;
import it.unitn.ripples.impl.RipplesEngine;

import java.io.IOException;

import models.VideoInfo;
import play.mvc.*;
import views.html.*;
/**
 * Class that handles requests arriving to the web server, which are then routed
 * to controllers using the routes specified in: conf/routes file
 * 
 * this class encorporates Application-wide controllers, which we didn't
 * want to move to a specific class of controllers.
 * 
 * @author A. Bratus, E. Bodnari
 *
 */
public class Application extends Controller {

	/**
	 * Renders an index.scala.html view
	 * 
	 * @return play.mvc.Result
	 * 
	 */
	
	public static Result index() {

		return ok(index.render("Hi"));

	}

	/**
	 * This controller is called whenever a request for
	 * visualization page is issued. 
	 * The model passed to the view is {@link VideoInfo}
	 * that contains additional information about the video.
	 * 
	 * Renders the visualize.scala.html
	 * 
	 * @param id of the video
	 * @return play.mvc.Result
	 * @throws Exception 
	 */
	
	public static Result visualize(String id) throws Exception {

		DatabaseController dbCtrl = new DatabaseController();
		
		VideoInfo info = dbCtrl.getVideoInfo(id);
		
		if (info == null) {
			dbCtrl.removeCollection(id);
			RipplesEngine engine = new RipplesEngine();
			engine.makeRipples(id);
		}
		
		dbCtrl.close();

		return ok(visualize2.render(info));
	}

	/**
	 * Request issued using AJAX method, to prepare Ripples Data for visualization,
	 * triggers generation of RipplesData.
	 * 
	 * @return numberOfShares 
	 * @throws Exception 
	 */
	
	
	public static Result aripple() throws Exception {
		String id = ctx().request().getQueryString("id");
		
		RipplesEngine engine = new RipplesEngine();
		
		Integer shares = engine.makeRipples(id);
		
		if (shares == null) {
			return internalServerError("Please Retry");
		}
		return ok(shares.toString());
	}

	
	/**
	 * Method replying to javascripts D3js library request, for a
	 * data structure (tree-like) ready for visualization.
	 * @param id
	 * @return Data prepared for visualization
	 * @throws IOException
	 */
	
	public static Result sayHello(String id) throws IOException {
		System.err.println(id);
		// String tt =
		// FileUtils.readFileToString(Play.application().getFile("flare.json"));
		response().setHeader("Access-Control-Allow-Origin", "*");
		DatabaseController dbCtrl = new DatabaseController();
		String flare = dbCtrl.getFlare(id);
		dbCtrl.close();

		return ok(flare);
	}


}
