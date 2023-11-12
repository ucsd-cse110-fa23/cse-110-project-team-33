package cse.gradle;

import java.util.ArrayList;
import java.util.List;

public interface Database {

    public void createToDatabase(Recipe recipe);

    public List<Recipe> readDatabase();

    public void updateDatabase(List<Recipe> recipes);

    public void deleteFromDatabase(Recipe recipe);

}