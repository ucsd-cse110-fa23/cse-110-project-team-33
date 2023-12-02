package cse.gradle;

public class MockModel extends Model {

    public MockModel() {
        // call the super constructor with the test server url
        super("http://localhost:8100"); 
        // hard code the user id to be the test user
        this.userId = "92d0e388-1303-444b-bf19-11d3488f45a0";
    }
}
