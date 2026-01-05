package unipi.meallab.api;
/**
 * MealBase is the base class that represents
 * the details of a meal from <a href="https://www.themealdb.com">TheMealDB API</a>.
 */
public class MealBase {

	protected String idMeal;
	protected String strMeal;
	protected String strMealThumb;
	
	public MealBase() { }
	
	public MealBase(String idMeal, String strMeal, String strMealThumb) {
		this.idMeal = idMeal;
		this.strMeal = strMeal;
		this.strMealThumb = strMealThumb;
	}

	public String getIdMeal() {
		return idMeal;
	}

	public void setIdMeal(String idMeal) {
		this.idMeal = idMeal;
	}

	public String getStrMeal() {
		return strMeal;
	}

	public void setStrMeal(String strMeal) {
		this.strMeal = strMeal;
	}

	public String getStrMealThumb() {
		return strMealThumb;
	}

	public void setStrMealThumb(String strMealThumb) {
		this.strMealThumb = strMealThumb;
	}
	
	@Override
	public String toString() {
	    return strMeal;
	}

}
