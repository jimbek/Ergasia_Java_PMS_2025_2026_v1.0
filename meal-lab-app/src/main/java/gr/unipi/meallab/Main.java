package gr.unipi.meallab;
import java.io.File;

import com.fasterxml.jackson.databind.ObjectMapper;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import unipi.meallab.api.Meal;
import unipi.meallab.api.MealBase;
import unipi.meallab.api.MealDbClient;

public class Main extends Application {

    private static final String API_VERSION = "v1";
    private static final String API_KEY = "1";
    
    private final ObservableList<MealBase> favorites = FXCollections.observableArrayList();
    private final ObservableList<MealBase> cooked = FXCollections.observableArrayList();


    @Override
    public void start(Stage stage) {

    	favorites.addAll(loadFavorites());
    	cooked.addAll(loadCooked());
    	
    	TextField ingredientField = new TextField();
        ingredientField.setPromptText("Write a cooking material (example: chicken)");

        Button searchButton = new Button("Search");

        ListView<MealBase> resultsList = new ListView<>();
        resultsList.setPrefHeight(220);
        Label statusLabel = new Label();
        statusLabel.setStyle(
        	    "-fx-font-size: 16px;" +
        	    "-fx-font-weight: bold;"
        	    );
        
        ListView<MealBase> favoritesList = new ListView<>(favorites);
        ListView<MealBase> cookedList = new ListView<>(cooked);

        
        Button randomButton = new Button("Get Random Recipe");
        Button detailsButton = new Button("Show Recipe Details"); 
        detailsButton.setPrefWidth(150);
        detailsButton.setPrefHeight(30);
        detailsButton.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");

        Button addToFavButton = new Button("→ Favorite");
        Button addToCookedFromResultsButton = new Button("→ Cooked");
        Button moveToCookedButton = new Button("→ Cooked");
        Button removeFromFavButton = new Button("Remove Favorite");
        Button removeFromCookedButton = new Button("Remove Cooked");
        Button moveBackToFavButton = new Button("← Back to Favorites");

                
        TextArea detailsArea = new TextArea();
        detailsArea.setEditable(false);
        detailsArea.setWrapText(true);
        detailsArea.setPrefHeight(400);
        
        Label recipeTitle = new Label();
        recipeTitle.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        ImageView recipeImage = new ImageView();
        recipeImage.setFitWidth(150);
        recipeImage.setPreserveRatio(true);
        
        

        searchButton.setOnAction(e -> {

            String ingredient = ingredientField.getText();

            if (ingredient == null || ingredient.isBlank()) {
                statusLabel.setText("Write a cooking material");
                return;
            }

            try {
                MealDbClient client = new MealDbClient(API_VERSION, API_KEY);

                MealBase[] meals = client.search(ingredient);

                resultsList.getItems().clear();
                detailsArea.clear();


                if (meals == null || meals.length == 0) {
                    statusLabel.setText("No recipes found");

                    detailsArea.clear();
                    recipeTitle.setText("");
                    recipeImage.setImage(null);

                    return;
                }

                for (MealBase meal : meals) {
                    resultsList.getItems().add(meal);
                }

                statusLabel.setText( meals.length + " recipes found");

            } catch (Exception ex) {
                statusLabel.setText("Error during search");
            }
        });
                
        randomButton.setOnAction(e -> {
            try {
                MealDbClient client = new MealDbClient(API_VERSION, API_KEY);
                Meal randomMeal = client.getRandomRecipe();

                if (randomMeal == null) {
                    statusLabel.setText("No random recipe found");
                    return;
                }

                resultsList.getItems().clear();

                recipeTitle.setText(randomMeal.getStrMeal());

                recipeImage.setImage(
                    new Image(randomMeal.getStrMealThumb(), true)
                );

                detailsArea.setText(buildMealDetails(randomMeal));


                statusLabel.setText("Random recipe");

            } catch (Exception ex) {
                statusLabel.setText("Error loading random recipe");
            }
        });
        
        detailsButton.setOnAction(e -> {

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

                recipeTitle.setText(fullMeal.getStrMeal());
                recipeImage.setImage(new Image(fullMeal.getStrMealThumb(), true));
                detailsArea.setText(buildMealDetails(fullMeal));

                statusLabel.setText("Details loaded");

            } catch (Exception ex) {
                statusLabel.setText("Error loading details");
            }
        });

        
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
        
        removeFromFavButton.setOnAction(e -> {
            MealBase selected = favoritesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                favorites.remove(selected);
                statusLabel.setText("Removed from Favorites");
            }
        });
        
        removeFromCookedButton.setOnAction(e -> {
            MealBase selected = cookedList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                cooked.remove(selected);
                statusLabel.setText("Removed from Cooked");
            }
        });
        
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
        
        HBox searchButtonsBox = new HBox(10, searchButton, randomButton);
        searchButtonsBox.setAlignment(Pos.CENTER);
        
        VBox detailsBox = new VBox(10,
        	    recipeTitle,
        	    recipeImage,
        	    detailsArea
        	);
        
        HBox detailsButtonBox = new HBox(detailsButton);
        detailsButtonBox.setAlignment(Pos.CENTER);

        VBox searchBox = new VBox(5, new Label("Results"), resultsList, addToFavButton, addToCookedFromResultsButton);
        VBox favBox = new VBox(5, new Label("Favorites"), favoritesList, moveToCookedButton, removeFromFavButton);
        VBox cookedBox = new VBox(5, new Label("Cooked"), cookedList,  moveBackToFavButton, removeFromCookedButton);

        HBox listsBox = new HBox(10, searchBox, favBox, cookedBox);




        VBox layout = new VBox(10, ingredientField, searchButtonsBox, listsBox, detailsButtonBox, detailsBox, statusLabel);
        layout.setPadding(new Insets(15));
        

        stage.setScene(new Scene(layout, 650, 650));
        stage.setTitle("MealLab App");
        stage.setOnCloseRequest(e -> {
            saveFavorites();
            saveCooked();
        });
        stage.show();
    }
    
    
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

    private void addIngredient(StringBuilder sb, String ingredient, String measure) {
        if (ingredient == null || ingredient.isBlank()) return;

        sb.append("- ");
        if (measure != null && !measure.isBlank()) {
            sb.append(measure).append(" ");
        }
        sb.append(ingredient).append("\n");
    }
    
    private void saveFavorites() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File("favorites.json"), favorites);
        } catch (Exception e) {
            System.out.println("Error saving favorites.json");
        }
    }

    private void saveCooked() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.writerWithDefaultPrettyPrinter()
                  .writeValue(new File("cooked.json"), cooked);
        } catch (Exception e) {
            System.out.println("Error saving cooked.json");
        }
    }
    
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




    public static void main(String[] args) {
        launch();
    }
}
