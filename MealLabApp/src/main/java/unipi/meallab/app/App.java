package unipi.meallab.app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;


import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

// Classes from the MealLab API project
import unipi.meallab.api.Meal;
import unipi.meallab.api.MealBase;
import unipi.meallab.api.MealDbClient;

public class App extends Application {

    // API version and key used to access TheMealDB API
    private static final String API_VERSION = "v1";
    private static final String API_KEY = "1";
    
    // List that stores favorite recipes
    private final ObservableList<MealBase> favorites = FXCollections.observableArrayList();
    
    // List that stores cooked recipes
    private final ObservableList<MealBase> cooked = FXCollections.observableArrayList();

    @Override
    public void start(Stage stage) {

        // Load saved favorites and cooked recipes from files
        favorites.addAll(loadFavorites());
        cooked.addAll(loadCooked());
    	
        // Text field where the user types the ingredient
        TextField ingredientField = new TextField();
        ingredientField.setPromptText("Write a cooking material (example: chicken)");

        // Button for searching recipes
        Button searchButton = new Button("Search");

        // List that shows search results
        ListView<MealBase> resultsList = new ListView<>();
        resultsList.setPrefHeight(220);

        // Label used to show messages to the user
        Label statusLabel = new Label();
        statusLabel.setStyle(
        	    "-fx-font-size: 16px;" +
        	    "-fx-font-weight: bold;"
        	    );
        
        // List that shows favorite recipes
        ListView<MealBase> favoritesList = new ListView<>(favorites);
        
        // List that shows cooked recipes
        ListView<MealBase> cookedList = new ListView<>(cooked);

        // When clicking on search results, clear selection from Favorites and Cooked lists
        // so that only one recipe is selected at a time
        resultsList.setOnMouseClicked(e -> {
            favoritesList.getSelectionModel().clearSelection();
            cookedList.getSelectionModel().clearSelection();
        });

        // When clicking on Favorites list, clear selection from Results and Cooked lists
        // to avoid multiple recipes being selected at the same time
        favoritesList.setOnMouseClicked(e -> {
            resultsList.getSelectionModel().clearSelection();
            cookedList.getSelectionModel().clearSelection();
        });

        // When clicking on Cooked list, clear selection from Results and Favorites lists
        // ensuring that details are shown for only one selected recipe
        cookedList.setOnMouseClicked(e -> {
            resultsList.getSelectionModel().clearSelection();
            favoritesList.getSelectionModel().clearSelection();
        });


        // Button that loads a random recipe
        Button randomButton = new Button("Get Random Recipe");

        // Button that shows recipe details only when clicked
        Button detailsButton = new Button("Show Recipe Details"); 
        detailsButton.setPrefWidth(150);
        detailsButton.setPrefHeight(30);
        detailsButton.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        // Buttons for managing lists
        Button addToFavButton = new Button("→ Favorite");
        Button addToCookedFromResultsButton = new Button("→ Cooked");
        Button moveToCookedButton = new Button("→ Cooked");
        Button removeFromFavButton = new Button("Remove Favorite");
        Button removeFromCookedButton = new Button("Remove Cooked");
        Button moveBackToFavButton = new Button("← Back to Favorites");

        // Text area that shows ingredients and instructions
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefHeight(400);
        
        // Label that shows the recipe title
        Label recipeTitle = new Label();
        recipeTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // ImageView that shows the recipe image
        ImageView recipeImage = new ImageView();
        recipeImage.setFitWidth(150);
        recipeImage.setPreserveRatio(true);

        // Action when the Search button is clicked
        searchButton.setOnAction(e -> {

            // Get text from input field
            String ingredient = ingredientField.getText();

            // Check if the input is empty
            if (ingredient == null || ingredient.isBlank()) {
                statusLabel.setText("Write a cooking material");
                return;
            }

            try {
                // Create API client
                MealDbClient client = new MealDbClient(API_VERSION, API_KEY);

                // Search recipes by ingredient
                MealBase[] meals = client.search(ingredient);

                // Clear previous results and details
                resultsList.getItems().clear();
                detailsArea.clear();

                // If no recipes are found
                if (meals == null || meals.length == 0) {
                    statusLabel.setText("No recipes found");

                    detailsArea.clear();
                    recipeTitle.setText("");
                    recipeImage.setImage(null);

                    return;
                }

                // Add results to the list
                for (MealBase meal : meals) {
                    resultsList.getItems().add(meal);
                }

                // Show number of found recipes
                statusLabel.setText(meals.length + " recipes found");

            } catch (Exception ex) {
                statusLabel.setText("Error during search");
            }
        });

        // Action for random recipe button
        randomButton.setOnAction(e -> {
            try {
                MealDbClient client = new MealDbClient(API_VERSION, API_KEY);
                Meal randomMeal = client.getRandomRecipe();

                if (randomMeal == null) {
                    statusLabel.setText("No random recipe found");
                    return;
                }

                // Clear results list
                resultsList.getItems().clear();

                // Show recipe title, image and details
                recipeTitle.setText(randomMeal.getStrMeal());
                recipeImage.setImage(new Image(randomMeal.getStrMealThumb(), true));
                detailsArea.setText(buildMealDetails(randomMeal));

                statusLabel.setText("Random recipe");

            } catch (Exception ex) {
                statusLabel.setText("Error loading random recipe");
            }
        });

        // Action for Show Recipe Details button
        detailsButton.setOnAction(e -> {

            // The selected recipe (from any list)
            MealBase selected = null;

            if (resultsList.getSelectionModel().getSelectedItem() != null) {
                selected = resultsList.getSelectionModel().getSelectedItem();
            } 
            else if (favoritesList.getSelectionModel().getSelectedItem() != null) {
                selected = favoritesList.getSelectionModel().getSelectedItem();
            } 
            else if (cookedList.getSelectionModel().getSelectedItem() != null) {
                selected = cookedList.getSelectionModel().getSelectedItem();
            }

            if (selected == null) {
                statusLabel.setText("Select a recipe first");
                return;
            }

            try {
                MealDbClient client = new MealDbClient(API_VERSION, API_KEY);
                Meal fullMeal = client.getRecipe(selected.getIdMeal());

                if (fullMeal == null) {
                    statusLabel.setText("No details found");
                    return;
                }

                // Display recipe information
                recipeTitle.setText(fullMeal.getStrMeal());
                recipeImage.setImage(new Image(fullMeal.getStrMealThumb(), true));
                detailsArea.setText(buildMealDetails(fullMeal));

                statusLabel.setText("Details loaded");

            } catch (Exception ex) {
                statusLabel.setText("Error loading details");
            }
        });

        // Add selected result to favorites
        addToFavButton.setOnAction(e -> {
            MealBase selected = resultsList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Select a recipe first");
                return;
            }
            if (!favorites.contains(selected)) {
                favorites.add(selected);
                statusLabel.setText("Added to Favorites");
            }
        });

        // Add selected result to cooked list
        addToCookedFromResultsButton.setOnAction(e -> {
            MealBase selected = resultsList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                statusLabel.setText("Select a recipe first");
                return;
            }

            if (!cooked.contains(selected)) {
                cooked.add(selected);
                statusLabel.setText("Moved to Cooked");
            }
        });

        // Move recipe from favorites to cooked
        moveToCookedButton.setOnAction(e -> {
            MealBase selected = favoritesList.getSelectionModel().getSelectedItem();
            if (selected == null) {
                statusLabel.setText("Select favorite recipe first");
                return;
            }
            favorites.remove(selected);
            cooked.add(selected);
            statusLabel.setText("Marked as Cooked");
        });

        // Remove recipe from favorites
        removeFromFavButton.setOnAction(e -> {
            MealBase selected = favoritesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                favorites.remove(selected);
                statusLabel.setText("Removed from Favorites");
            }
        });

        // Remove recipe from cooked list
        removeFromCookedButton.setOnAction(e -> {
            MealBase selected = cookedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cooked.remove(selected);
                statusLabel.setText("Removed from Cooked");
            }
        });

        // Move recipe from cooked back to favorites
        moveBackToFavButton.setOnAction(e -> {
            MealBase selected = cookedList.getSelectionModel().getSelectedItem();

            if (selected == null) {
                statusLabel.setText("Select cooked recipe first");
                return;
            }

            cooked.remove(selected);

            if (!favorites.contains(selected)) {
                favorites.add(selected);
            }

            statusLabel.setText("Moved back to Favorites");
        }); 

        // Layout containers for UI
        HBox searchButtonsBox = new HBox(10, searchButton, randomButton);
        searchButtonsBox.setAlignment(Pos.CENTER);

        VBox detailsBox = new VBox(10, recipeTitle, recipeImage, detailsArea);

        HBox detailsButtonBox = new HBox(detailsButton);
        detailsButtonBox.setAlignment(Pos.CENTER);

        VBox searchBox = new VBox(5, new Label("Results"), resultsList, addToFavButton, addToCookedFromResultsButton);
        VBox favBox = new VBox(5, new Label("Favorites"), favoritesList, moveToCookedButton, removeFromFavButton);
        VBox cookedBox = new VBox(5, new Label("Cooked"), cookedList, moveBackToFavButton, removeFromCookedButton);

        HBox listsBox = new HBox(10, searchBox, favBox, cookedBox);

        // Main layout of the window
        VBox layout = new VBox(10, ingredientField, searchButtonsBox, listsBox, detailsButtonBox, detailsBox, statusLabel);
        layout.setPadding(new Insets(15));

        // Set window properties
        stage.setScene(new Scene(layout, 650, 650));
        stage.setTitle("MealLab App");

        // Save data when the app closes
        stage.setOnCloseRequest(e -> {
            saveFavorites();
            saveCooked();
        });

        stage.show();
    }

    // Builds the full recipe text (ingredients + instructions)
    private String buildMealDetails(Meal meal) {
        StringBuilder sb = new StringBuilder();

        sb.append("Ingredients & Measures:\n\n");

        addIngredient(sb, meal.getStrIngredient1(), meal.getStrMeasure1());
        addIngredient(sb, meal.getStrIngredient2(), meal.getStrMeasure2());
        addIngredient(sb, meal.getStrIngredient3(), meal.getStrMeasure3());
        addIngredient(sb, meal.getStrIngredient4(), meal.getStrMeasure4());
        addIngredient(sb, meal.getStrIngredient5(), meal.getStrMeasure5());
        addIngredient(sb, meal.getStrIngredient6(), meal.getStrMeasure6());
        addIngredient(sb, meal.getStrIngredient7(), meal.getStrMeasure7());
        addIngredient(sb, meal.getStrIngredient8(), meal.getStrMeasure8());
        addIngredient(sb, meal.getStrIngredient9(), meal.getStrMeasure9());
        addIngredient(sb, meal.getStrIngredient10(), meal.getStrMeasure10());
        addIngredient(sb, meal.getStrIngredient11(), meal.getStrMeasure11());
        addIngredient(sb, meal.getStrIngredient12(), meal.getStrMeasure12());
        addIngredient(sb, meal.getStrIngredient13(), meal.getStrMeasure13());
        addIngredient(sb, meal.getStrIngredient14(), meal.getStrMeasure14());
        addIngredient(sb, meal.getStrIngredient15(), meal.getStrMeasure15());
        addIngredient(sb, meal.getStrIngredient16(), meal.getStrMeasure16());
        addIngredient(sb, meal.getStrIngredient17(), meal.getStrMeasure17());
        addIngredient(sb, meal.getStrIngredient18(), meal.getStrMeasure18());
        addIngredient(sb, meal.getStrIngredient19(), meal.getStrMeasure19());
        addIngredient(sb, meal.getStrIngredient20(), meal.getStrMeasure20());

        sb.append("\n📋 Instructions:\n\n");
        sb.append(meal.getStrInstructions());

        return sb.toString();
    }

    // Helper method that adds one ingredient line
    private void addIngredient(StringBuilder sb, String ingredient, String measure) {
        if (ingredient == null || ingredient.isBlank()) return;

        sb.append("- ");
        if (measure != null && !measure.isBlank()) {
            sb.append(measure).append(" ");
        }
        sb.append(ingredient).append("\n");
    }

    // Save favorites list to JSON file
    private void saveFavorites() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File("favorites.json"), favorites);
        } catch (Exception e) {
            System.out.println("Error saving favorites.json");
        }
    }

    // Save cooked list to JSON file
    private void saveCooked() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File("cooked.json"), cooked);
        } catch (Exception e) {
            System.out.println("Error saving cooked.json");
        }
    }

    // Load favorites from JSON file
    private ObservableList<MealBase> loadFavorites() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("favorites.json");

            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }

            MealBase[] data = mapper.readValue(file, MealBase[].class);
            return FXCollections.observableArrayList(data);

        } catch (Exception e) {
            System.out.println("Error loading favorites.json");
            return FXCollections.observableArrayList();
        }
    }

    // Load cooked recipes from JSON file
    private ObservableList<MealBase> loadCooked() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("cooked.json");

            if (!file.exists()) {
                return FXCollections.observableArrayList();
            }

            MealBase[] data = mapper.readValue(file, MealBase[].class);
            return FXCollections.observableArrayList(data);

        } catch (Exception e) {
            System.out.println("Error loading cooked.json");
            return FXCollections.observableArrayList();
        }
    }

    // Main method that launches the JavaFX app
    public static void main(String[] args) {
        launch();
    }
}
