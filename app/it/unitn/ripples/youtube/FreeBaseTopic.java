package it.unitn.ripples.youtube;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Client to Search for topic ids based on keyword
 * from the FreeBase library.
 * 
 */


public class FreeBaseTopic {
	private static final Logger fLogger = LoggerFactory
			.getLogger(FreeBaseTopic.class);
	private static final long NUMBER_OF_TOPICS_RETURNED = 5;
	private static final String serviceURL = "https://www.googleapis.com/freebase/v1/search";

	/**
	 * Actual method performing search on FreeBase
	 * Returns a map of <Topic Name, Topic Id>
	 * @param topicQuery
	 * @return {@link Map} Topic Name, Topic Id
	 * @throws IOException
	 */
	
	
	public static Map<String, String> getTopicId(String topicQuery)
			throws IOException {
		ArrayNode arrayNodeResults = null;
		Map<String, String> map = null;
		CloseableHttpClient httpclient = HttpClients.createDefault();

		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("query", topicQuery));
		params.add(new BasicNameValuePair("limit", Long
				.toString(NUMBER_OF_TOPICS_RETURNED)));

		String url = serviceURL + "?" + URLEncodedUtils.format(params, "UTF-8");
		try {
			HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
			HttpEntity entity = httpResponse.getEntity();

			if (entity != null) {
				InputStream instream = entity.getContent();

				// Convert the JSON to an object. This code does not do an
				// exact map from JSON to POJO (Plain Old Java object), but
				// you could create additional classes and use them with the
				// mapper.readValue() function to get that exact mapping.
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readValue(instream, JsonNode.class);

				// Confirm that the HTTP request was handled successfully by
				// checking the API response's HTTP response code.
				if (rootNode.get("status").asText().equals("200 OK")) {
					// In the API response, the "result" field contains the
					// list of needed results.
					arrayNodeResults = (ArrayNode) rootNode.get("result");
					// Prompt the user to select a topic from the list of API
					// results.
				}
			}
		} catch (Exception e) {
			fLogger.error("Search topic FAILED", e);
		} finally {
			if (httpclient != null)
				httpclient.close();
		}

		map = new HashMap<String, String>();
		String value;
		for (int i = 0; i < arrayNodeResults.size(); i++) {
			StringBuilder key = new StringBuilder();
			JsonNode node = arrayNodeResults.get(i);

			key.append(node.get("name").asText());

			if (node.get("notable") != null) {
				key.append(" (" + node.get("notable").get("name").asText()
						+ ")");
			}
			value = node.get("mid").asText();
			map.put(key.toString(), value);
		}
		return map;
	}

}
