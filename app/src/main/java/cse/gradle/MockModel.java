package cse.gradle;

public class MockModel extends Model {

    public final static String mockUserId = "92d0e388-1303-444b-bf19-11d3488f45a0";
    public final static String mockUsername = "test_user";
    public final static String mockPassword = "password";
    public final static String mockUrlString = "http://localhost:8100";

    public MockModel() {
        // call the super constructor with the test server url
        super(mockUrlString);
        // hard code the user id to be the test user
        this.userId = mockUserId;
    }
}
