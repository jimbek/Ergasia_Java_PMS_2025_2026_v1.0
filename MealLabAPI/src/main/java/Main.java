public class Main {
    public static void main(String[] args) throws Exception
    {
        MealDbClient client = new MealDbClient("v1", "1");
        
        MealBaseResults results = client.search("tomato");
        
        MealBase[] meals = results.getMeals();
        
        if (meals != null) {
        	for (MealBase meal : meals) {
        		System.out.println("Id: " + meal.getIdMeal() + ", Name: " + meal.getStrMeal() + ", Thumb: " + meal.getStrMealThumb());
        	}
        }
    }
}