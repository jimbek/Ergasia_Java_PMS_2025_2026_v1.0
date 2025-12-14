public class MealBaseResults {

	private MealBase[] meals;
	
	public MealBaseResults() {
		this(null);
	}

	public MealBaseResults(MealBase[] meals) {
		this.meals = meals;
	}

	public MealBase[] getMeals() {
		return meals;
	}

	public void setMeals(MealBase[] meals) {
		this.meals = meals;
	}

}
