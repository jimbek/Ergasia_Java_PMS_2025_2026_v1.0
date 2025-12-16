import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

public class MealDbClient {
	
	private static class JsonResponse<T> {
		
		private T[] meals;

		public T[] getMeals() {
			return meals;
		}
		
	}
	
	private static final String DOMAIN = "https://www.themealdb.com";

	private String version;
	private String apiKey;

	public MealDbClient(String version, String apiKey) {
		this.version = version;
		this.apiKey = apiKey;
	}
	
	public MealBase[] search(String ingredient) {
		// We define the Meal DB search URL.
		// Example: https://www.themealdb.com/api/json/v1/1/filter.php?i=tomato
		ObjectMapper mapper = getMapper();
		
		JavaType javaType = mapper.getTypeFactory().constructParametricType(JsonResponse.class, MealBase.class);
		
		JsonResponse<Meal> results = this.fetchResults("filter", ingredient, mapper, javaType);
		
		return results != null ? results.getMeals() : new Meal[0];
	}
	
	public Meal getRecipe(String idMeal) {
		// We define the Meal DB look-up URL.
		// Example: https://www.themealdb.com/api/json/v1/1/lookup.php?i=53284
		ObjectMapper mapper = getMapper();
		
		JavaType javaType = mapper.getTypeFactory().constructParametricType(JsonResponse.class, Meal.class);
		
		JsonResponse<Meal> results = this.fetchResults("lookup", idMeal, mapper, javaType);
		
		return results != null ? results.getMeals()[0] : null;
	}
	
	public Meal getRandomRecipe() {
		// We define the Meal DB look-up URL.
		// https://www.themealdb.com/api/json/v1/1/random.php
		ObjectMapper mapper = getMapper();
		
		JavaType javaType = mapper.getTypeFactory().constructParametricType(JsonResponse.class, Meal.class);
		
		JsonResponse<Meal> results = this.fetchResults("random", null, mapper, javaType);
		
		return results != null ? results.getMeals()[0] : null;
	}
	
	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	
	private StringBuilder getMealDbURI(String apiCall, String i) {
		StringBuilder uriBuilder = new StringBuilder(DOMAIN);
		
		uriBuilder.append("/api/json/");
		uriBuilder.append(version);
		uriBuilder.append("/");
		uriBuilder.append(apiKey);
		uriBuilder.append("/");
		uriBuilder.append(apiCall);
		uriBuilder.append(".php");
		
		if (i != null) {
			uriBuilder.append("?i=");
			uriBuilder.append(i);
		}
		
		return uriBuilder;
	}
	
	private InputStream getInputStream(StringBuilder uriBuilder) throws URISyntaxException, IOException {
		URI uri = new URI(uriBuilder.toString());
		URL url = uri.toURL();
		
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		
		return connection.getInputStream();
	}
	
	private <T> JsonResponse<T> readResults(InputStream responseStream, ObjectMapper mapper, JavaType javaType) throws StreamReadException, DatabindException, IOException {
		JsonResponse<T> results = mapper.readValue(responseStream, javaType);
		
		return results;
	}
	
	private <T> JsonResponse<T> fetchResults(String apiCall, String i, ObjectMapper mapper, JavaType javaType) {
		StringBuilder uriBuilder = getMealDbURI(apiCall, i);
		
		try {
			InputStream responseStream = this.getInputStream(uriBuilder);
			
			JsonResponse<T> results = this.readResults(responseStream, mapper, javaType);
			
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
		
		return null;
	}
	
}
