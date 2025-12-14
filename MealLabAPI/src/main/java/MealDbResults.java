public class MealDbResults {

	private Meal[] meals;
	
	public MealDbResults() {
		this(null);
	}

	public MealDbResults(Meal[] meals) {
		this.meals = meals;
	}

	public Meal[] getMeals() {
		return meals;
	}

	public void setMeals(Meal[] meals) {
		this.meals = meals;
	}

}
