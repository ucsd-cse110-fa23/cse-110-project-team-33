package cse.project.team;

import java.util.List;

public interface Database {

    public List<Recipe> readDatabase();
    public void saveToDatabase(List<Recipe> recipes);

}
