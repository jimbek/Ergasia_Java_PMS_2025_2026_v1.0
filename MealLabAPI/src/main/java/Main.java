public class Main {
    public static void main(String[] args) throws Exception
    {
        MealDbClient client = new MealDbClient("v1", "1");
        
        
        MealBaseDbResults mealBaseResults = client.search("tomato");
        
        MealBase[] mealBases = mealBaseResults.getMeals();
        
        if (mealBases != null) {
        	for (MealBase mealBase : mealBases) {
        		System.out.println("Id: " + mealBase.getIdMeal() + ", Name: " + mealBase.getStrMeal() + ", Thumb: " + mealBase.getStrMealThumb());
        	}
        }
        
        
        System.out.println(">>>");
        
        
        MealDbResults mealResults = client.getRecipe("53284");
        
        Meal meal = mealResults.getMeals() != null ? mealResults.getMeals()[0] : null;
        
        if (meal != null) {
        	System.out.println("Instructions: " + meal.getStrInstructions() + ", Ingredient 1: " + meal.getStrIngredient1() + ", Measure 1: " + meal.getStrMeasure1());
        }
    }
}