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
        
        
        Meal meal = client.getRecipe("53284") != null ? client.getRecipe("53284")[0] : null;
        
        if (meal != null) {
        	System.out.println("Instructions: " + meal.getStrInstructions() + ", Ingredient 1: " + meal.getStrIngredient1() + ", Measure 1: " + meal.getStrMeasure1());
        }
        
        
        System.out.println(">>>");
        
        
        Meal randomMeal = client.getRandomRecipe() != null ? client.getRandomRecipe()[0] : null;
        
        if (randomMeal != null) {
        	System.out.println("Instructions: " + randomMeal.getStrInstructions() + ", Ingredient 1: " + randomMeal.getStrIngredient1() + ", Measure 1: " + randomMeal.getStrMeasure1());
        }
    }
    
}