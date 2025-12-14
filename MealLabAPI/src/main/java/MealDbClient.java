import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.databind.ObjectMapper;

public class MealDbClient {
	
	private static String DOMAIN = "https://www.themealdb.com";

	private String version;
	private String apiKey;

	public MealDbClient(String version, String apiKey) {
		this.version = version;
		this.apiKey = apiKey;
	}
	
	public MealBaseDbResults search(String ingredient) {
		// We define the Meal DB search URL.
		// Example: https://www.themealdb.com/api/json/v1/1/filter.php?i=tomato
		StringBuilder uriBuilder = getMealDbURI("filter", ingredient);
		
		try {
			URI uri = new URI(uriBuilder.toString());
			URL url = uri.toURL();
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "application/json");
			
			InputStream responseStream = connection.getInputStream();
			
			ObjectMapper mapper = new ObjectMapper();
			
			@SuppressWarnings("unchecked")
			MealBaseDbResults results = mapper.readValue(responseStream, MealBaseDbResults.class);
			
			return results;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new MealBaseDbResults();
	}
	
	public MealDbResults getRecipe(String idMeal) {
		// We define the Meal DB look-up URL.
		// Example: https://www.themealdb.com/api/json/v1/1/lookup.php?i=53284
		StringBuilder uriBuilder = getMealDbURI("lookup", idMeal);
		
		try {
			URI uri = new URI(uriBuilder.toString());
			URL url = uri.toURL();
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "application/json");
			
			InputStream responseStream = connection.getInputStream();
			
			ObjectMapper mapper = new ObjectMapper();
			
			@SuppressWarnings("unchecked")
			MealDbResults results = mapper.readValue(responseStream, MealDbResults.class);
			
			return results;
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return new MealDbResults();
	}
	
	private StringBuilder getMealDbURI(String apiCall, String i) {
		StringBuilder uriBuilder = new StringBuilder(DOMAIN);
		
		uriBuilder.append("/api/json/");
		uriBuilder.append(version);
		uriBuilder.append("/");
		uriBuilder.append(apiKey);
		uriBuilder.append("/");
		uriBuilder.append(apiCall);
		uriBuilder.append(".php?i=");
		uriBuilder.append(i);
		
		return uriBuilder;
	}
	
}
