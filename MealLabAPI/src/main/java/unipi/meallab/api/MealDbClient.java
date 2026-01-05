package unipi.meallab.api;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * MealDbClient is responsible for retrieving data from <a href="https://www.themealdb.com">TheMealDB API</a>.
 */
public class MealDbClient {
	
	/**
	 * JsonResponse is a generic wrapper for each response.
	 * @param <T> an object of {@link MealBase} or {@link Meal}
	 */
	private static class JsonResponse<T> {
		
		private T[] meals;

		public T[] getMeals() {
			return meals;
		}
		
	}
	
	/**
	 * Instance of log4j logging library.
	 */
	private static final Logger LOGGER = LogManager.getLogger(MealDbClient.class.getName());
	
	/**
	 * The domain of <a href="https://www.themealdb.com">TheMealDB API</a>.
	 */
	private static final String DOMAIN = "https://www.themealdb.com";

	/**
	 * The selected version of <a href="https://www.themealdb.com">TheMealDB API</a>.
	 */
	private String version;
	
	/**
	 * The developer's API key to access <a href="https://www.themealdb.com">TheMealDB API</a>.
	 */
	private String apiKey;

	/**
	 * Creates a new instance of {@link MealDbClient}.
	 * @param version the version of <a href="https://www.themealdb.com">TheMealDB API</a> that will be used
	 * @param apiKey the API key needed to let the app be authorized from <a href="https://www.themealdb.com">TheMealDB API</a>
	 */
	public MealDbClient(String version, String apiKey) {
		this.version = version;
		this.apiKey = apiKey;
	}
	
	/**
	 * Returns an array of {@link MealBase}.
	 * @param ingredient examples <code>tomato</code>, <code>rice</code> etc
	 * @return array of {@link MealBase}
	 */
	public MealBase[] search(String ingredient) {
		JsonResponse<MealBase> results = fetchResults("filter", ingredient, MealBase.class);
		return results != null && results.getMeals() != null ? results.getMeals() : new MealBase[0];
	}
	
	/**
	 * Returns a new instance of {@link Meal}.
	 * @param idMeal unique identifier of a meal
	 * @return instance of {@link Meal}
	 */
	public Meal getRecipe(String idMeal) {
		JsonResponse<Meal> results = fetchResults("lookup", idMeal, Meal.class);
		return results != null && results.getMeals() != null ? results.getMeals()[0] : null;
	}
	
	/**
	 * Returns a random instance of {@link Meal}.
	 * @return instance of {@link Meal}
	 */
	public Meal getRandomRecipe() {
		JsonResponse<Meal> results = fetchResults("random", null, Meal.class);
		return results != null && results.getMeals() != null ? results.getMeals()[0] : null;
	}
	
	/**
	 * Returns a new instance of {@link URL} representing an HTTP call to <a href="https://www.themealdb.com">TheMealDB API</a>.
	 * Examples of HTTP calls:
	 * <ul>
	 * 	<li><a href="https://www.themealdb.com/api/json/v1/1/filter.php?i=tomato">Filter</a>
	 * 	<li><a href="https://www.themealdb.com/api/json/v1/1/lookup.php?i=53284">Lookup</a>
	 * 	<li><a href="https://www.themealdb.com/api/json/v1/1/random.php">Random</a>
	 * </ul>
	 * @param apiCall <code>filter</code>, <code>lookup</code> or <code>random</code>
	 * @param i search field, set to <code>null</code> if an HTTP call does not expect it
	 * @return instance of {@link URL}
	 * @throws URISyntaxException
	 * @throws MalformedURLException
	 */
	private URL getMealDbURL(String apiCall, String i) throws URISyntaxException, MalformedURLException {
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
		
		URI uri = new URI(uriBuilder.toString());
		
		return uri.toURL();
	}
	
	/**
	 * Returns a new instance of {@link InputStream} with data from <a href="https://www.themealdb.com">TheMealDB API</a>.
	 * @param url instance of {@link URL} representing an HTTP call
	 * @return instance of {@link InputStream}
	 * @throws IOException
	 */
	private InputStream getInputStream(URL url) throws IOException {
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("accept", "application/json");
		
		return connection.getInputStream();
	}
	
	/**
	 * Returns a new instance of {@link ObjectMapper} that ignores unknown properties.
	 * @return instance of {@link ObjectMapper}
	 */
	private ObjectMapper getMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return mapper;
	}
	
	/**
	 * Returns a new instance of {@link JavaType} in order to map <a href="https://www.themealdb.com">TheMealDB API</a>'s data with {@link MealBase} or {@link Meal}.
	 * @param <X> <code>{@link MealBase}</code> or <code>{@link Meal}</code>
	 * @param mapper instance of {@link ObjectMapper}
	 * @param x instance of <code>Class<{@link MealBase}></code> or <code>Class<{@link Meal}></code>
	 * @return instance of {@link JavaType}
	 */
	private <X> JavaType getJavaType(ObjectMapper mapper, Class<X> x) {
		return mapper
				.getTypeFactory()
				.constructParametricType(JsonResponse.class, x);
	}
	
	/**
	 * Returns a new instance of {@link JsonResponse} binding the data collected from <a href="https://www.themealdb.com">TheMealDB API</a>.
	 * @param <T> type of the fetched data
	 * @param responseStream data as {@link InputStream}
	 * @param mapper instance of {@link ObjectMapper}
	 * @param javaType instance of {@link JavaType} to define the POJO class
	 * @return instance of {@link JsonResponse}
	 * @throws StreamReadException
	 * @throws DatabindException
	 * @throws IOException
	 */
	private <T> JsonResponse<T> readResults(InputStream responseStream, ObjectMapper mapper, JavaType javaType) throws StreamReadException, DatabindException, IOException {
		return mapper.readValue(responseStream, javaType);
	}
	
	/**
	 * Returns an exception's stack trace as {@link String}.
	 * @param e an occurred {@link Exception}
	 * @return an exception's stack trace as string
	 */
	private String getStackTrace(Exception e) {
		StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
	}
	
	/**
	 * Deserializes  the data from <a href="https://www.themealdb.com">TheMealDB API</a>.
	 * @param <T> <code>{@link MealBase}</code> or <code>{@link Meal}</code>
	 * @param apiCall <code>filter</code>, <code>lookup</code> or <code>random</code>
	 * @param i search field, set to <code>null</code> if an HTTP call does not expect it
	 * @param t instance of <code>Class<{@link MealBase}></code> or <code>Class<{@link Meal}></code>
	 * @return an instance of {@link JsonResponse}<{@link MealBase}> or {@link JsonResponse}<{@link Meal}>
	 */
	private <T> JsonResponse<T> fetchResults(String apiCall, String i, Class<T> t) {
		try {
			URL uriAsString = getMealDbURL(apiCall, i);
			InputStream responseStream = getInputStream(uriAsString);
			
			ObjectMapper mapper = getMapper();
			JavaType javaType = getJavaType(mapper, t);
			
			return readResults(responseStream, mapper, javaType);
		} catch (URISyntaxException e) {
			LOGGER.error("URISyntaxException with stack: {}", getStackTrace(e));
		} catch (MalformedURLException e) {
			LOGGER.error("MalformedURLException with stack: {}", getStackTrace(e));
		} catch (IOException e) {
			LOGGER.error("IOException with stack: {}", getStackTrace(e));
		}
		
		return null;
	}
	
}
