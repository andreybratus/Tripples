package it.unitn.ripples.impl;

import it.unitn.ripples.Utils;

import java.io.Closeable;
import java.io.IOException;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client issuing requests to the Google+ server, intercepts the response
 * and parses only the necessary data from html content.
 * 
 * implements {@link Closeable} interface. Has to be always closed, to
 * prevent resource leakage.
 * 
 * @author A. Bratus, E. Bodnari
 */

public class RipplesRequest implements Closeable {

	private final static Logger fLogger = LoggerFactory
			.getLogger(RipplesRequest.class);

	private CloseableHttpClient httpclient;

	/**
	 * Default constructor for {@code RipplesRequest} class,
	 * that instantiates {@code CloseableHttpClient} instance.
	 * 
	 */
	
	public RipplesRequest() {
		httpclient = HttpClients.createDefault();
	}

	/**
	 * Method that sends a request to the Google+ server
	 * using a youtube videoId and parses response from it.
	 * 
	 * @param id of the video
	 * @return {@link JSONArray} ripples
	 * @throws IOException
	 */
	
	public JSONArray requestRipples(String id) throws IOException {
		CloseableHttpResponse response = null;
		String body;
		String url = "http://plus.google.com/ripples/details?url=www.youtube.com/watch?v="
				+ id;
		JSONArray jsonRipples = null;
		HttpGet httpGetRequest = new HttpGet(url);
		
		try {
			ResponseHandler<String> handler = new BasicResponseHandler();
			response = httpclient.execute(httpGetRequest);
			body = handler.handleResponse(response);

			int startIndex = body.lastIndexOf("[\"orr.rcd\",[\"orr.c\",,") + 21;

			if (body.substring(startIndex, startIndex + 2).equals("[]")) {
				fLogger.debug("No ripples exist for: ", id);
				return null;
			}

			int lastIndex = body.indexOf("\"]\n]") + 5;

			String newString = body.substring(startIndex, lastIndex).replace(
					",,", ",\"novalue\",");

			jsonRipples = new JSONArray(newString);
		} catch (Exception e) {
			fLogger.error("Error performing Ripples request", e);
			return null;
		} finally {
			if (response != null)
				Utils.safeClose(response, false);
		}
		return jsonRipples;
	}

	/**
	 * Method performing request to Google + server base on full URL.
	 * @param URL
	 * @return {@link JSONArray} containing ripples data. 
	 * @throws IOException
	 */
	
	public JSONArray requestRipplesFromURL(String id) throws IOException {
		CloseableHttpResponse response = null;
		String body;
		String url = "http://plus.google.com/ripples/details?url="
				+ id;
		JSONArray jsonRipples = null;
		HttpGet httpGetRequest = new HttpGet(url);
		
		try {
			ResponseHandler<String> handler = new BasicResponseHandler();
			response = httpclient.execute(httpGetRequest);
			body = handler.handleResponse(response);

			int startIndex = body.lastIndexOf("[\"orr.rcd\",[\"orr.c\",,") + 21;

			if (body.substring(startIndex, startIndex + 2).equals("[]")) {
				fLogger.debug("No ripples exist for: ", id);
				return null;
			}

			int lastIndex = body.indexOf("\"]\n]") + 5;

			String newString = body.substring(startIndex, lastIndex).replace(
					",,", ",\"novalue\",");

			jsonRipples = new JSONArray(newString);
		} catch (Exception e) {
			fLogger.error("Error performing Ripples request", e);
			return null;
		} finally {
			if (response != null)
				Utils.safeClose(response, false);
		}
		return jsonRipples;
	}
	
	/**
	 * 
	 * Always close the {@link RipplesRequest} to prevent
	 * resource leakage.
	 * 
	 */
	
	@Override
	public void close() throws IOException {
		httpclient.close();
	}

}
