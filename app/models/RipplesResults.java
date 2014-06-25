package models;

import org.json.JSONArray;

/**
 * Model used in MVC to send results of Ripples Request to the Client page
 * 
 * @author maddy
 * 
 */

public class RipplesResults {
	public final JSONArray ripples;
	public final String numbOfShares;
	public String videoName;

	/**
	 * Default constructor to initialize the final members.
	 * 
	 * @param ripples
	 * @param numbOfShares
	 */
	
	public RipplesResults(JSONArray ripples, String numbOfShares) {
		this.ripples = ripples;
		this.numbOfShares = numbOfShares;
	}
}
