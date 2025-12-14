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
	
	public MealBaseResults search(String ingredient) {
		// We define the Meal DB search URL.
		// Example: https://www.themealdb.com/api/json/v1/1/filter.php?i=tomato
		StringBuilder urlBuilder = new StringBuilder(DOMAIN);
		
		urlBuilder.append("/api/json/");
		urlBuilder.append(version);
		urlBuilder.append("/");
		urlBuilder.append(apiKey);
		urlBuilder.append("/filter.php?i=");
		urlBuilder.append(ingredient);
		
		try {
			URI uri = new URI(urlBuilder.toString());
			URL url = uri.toURL();
			
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "application/json");
			
			InputStream responseStream = connection.getInputStream();
			
			ObjectMapper mapper = new ObjectMapper();
			MealBaseResults results = mapper.readValue(responseStream, MealBaseResults.class);
			
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
		
		return new MealBaseResults();
	}
	
}
