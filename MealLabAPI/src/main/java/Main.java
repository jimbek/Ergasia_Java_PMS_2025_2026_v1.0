public class Main {
	
    public static void main(String[] args) throws Exception
    {
        MealDbClient client = new MealDbClient("v1", "1");
        
        
        MealBase[] mealBases = client.search("tomato");
        
        if (mealBases != null) {
        	for (MealBase mealBase : mealBases) {
        		System.out.println("Id: " + mealBase.getIdMeal() + ", Name: " + mealBase.getStrMeal() + ", Thumb: " + mealBase.getStrMealThumb());
        	}
        }
        
        
        System.out.println(">>>");
        
        
        Meal meal = client.getRecipe("53284");
        
        if (meal != null) {
        	System.out.println("Image: " + meal.getStrMealThumb() + ", Ingredient 1: " + meal.getStrIngredient1() + ", Measure 1: " + meal.getStrMeasure1());
        }
        
        
        System.out.println(">>>");
        
        
        Meal randomMeal = client.getRandomRecipe();
        
        if (randomMeal != null) {
        	System.out.println("Image: " + randomMeal.getStrMealThumb() + ", Ingredient 1: " + randomMeal.getStrIngredient1() + ", Measure 1: " + randomMeal.getStrMeasure1());
        }
    }
    
}