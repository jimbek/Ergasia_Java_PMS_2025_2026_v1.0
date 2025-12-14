public class MealBaseDbResults {

	private MealBase[] meals;
	
	public MealBaseDbResults() {
		this(null);
	}

	public MealBaseDbResults(MealBase[] meals) {
		this.meals = meals;
	}

	public MealBase[] getMeals() {
		return meals;
	}

	public void setMeals(MealBase[] meals) {
		this.meals = meals;
	}

}
