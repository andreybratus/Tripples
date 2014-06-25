package it.unitn.ripples.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import play.Play;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

/**
 * Abstract class that sets up the YoutuBe Search Engine,
 * Reads the key from the youtube.properites file
 * 
 */


public class AbstractSearch {
	/**
	 * Common behavior of YouTube Search Engines
	 * 
	 */

	/** Global instance of the HTTP transport. */
	protected static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();

	/** Global instance of the JSON factory. */
	protected static final JsonFactory JSON_FACTORY = new JacksonFactory();

	protected static final String PROPERTIES_FILENAME = "youtube.properties";

	protected static long NUMBER_OF_VIDEOS_RETURNED = 10;

	protected static String apiKey = null;
	
	protected static Properties properties;

	protected void getProperties() throws Exception {
		// Read the developer key from youtube.properties file
		properties = new Properties();
		try {
			InputStream in = Play.application().resourceAsStream(
					PROPERTIES_FILENAME);
			properties.load(in);
			NUMBER_OF_VIDEOS_RETURNED = Integer.parseInt(properties
					.getProperty("youtube.videos"));
			apiKey = properties.getProperty("youtube.apikey");
		} catch (IOException e) {

			System.err.println("There was an error reading "
					+ PROPERTIES_FILENAME + ": " + e.getCause() + " : "
					+ e.getMessage());
			throw new RuntimeException("Can't get Search Properties File " + e);
		}
	}

}
