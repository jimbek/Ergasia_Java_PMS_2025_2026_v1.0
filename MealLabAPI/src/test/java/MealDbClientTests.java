import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class MealDbClientTests {
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v1/1/filter.php?i=tomato")
	void search_valid() {
		MealDbClient client = new MealDbClient("v1", "1");
		
		MealBase[] mealBases = client.search("tomato");
		
		assertTrue(mealBases != null);
		assertTrue(mealBases.length > 0);
	}
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v1/1/filter.php?i=tomatooo")
	void search_invalid_term() {
		MealDbClient client = new MealDbClient("v1", "1");
		
		MealBase[] mealBases = client.search("tomatooo");
		
		assertTrue(mealBases != null);
		assertEquals(mealBases.length, 0);
	}
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v100/1/filter.php?i=tomato")
	void search_invalid_version() {
		MealDbClient client = new MealDbClient("v100", "1");
		
		MealBase[] mealBases = client.search("tomato");
		
		assertTrue(mealBases != null);
		assertEquals(mealBases.length, 0);
	}
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v1/1/lookup.php?i=53063")
	void getRecipe_valid() {
		MealDbClient client = new MealDbClient("v1", "1");
		
		Meal meal = client.getRecipe("53063");
		
		assertTrue(meal != null);
		assertEquals(meal.getIdMeal(), "53063");
	}
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v1/1/lookup.php?i=5306333")
	void getRecipe_invalid_id() {
		MealDbClient client = new MealDbClient("v1", "1");
		
		Meal meal = client.getRecipe("5306333");
		
		assertTrue(meal == null);
	}
	
	@Test
	@DisplayName("https://www.themealdb.com/api/json/v1/1/random.php")
	void getRandomRecipe_valid() {
		MealDbClient client = new MealDbClient("v1", "1");
		
		Meal meal = client.getRandomRecipe();
		
		assertTrue(meal != null);
		assertTrue(meal.getIdMeal() != null);
		assertTrue(meal.getIdMeal() != "");
	}
	
}
