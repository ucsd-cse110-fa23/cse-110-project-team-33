package cse.gradle;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class Feature20Tests extends HTTPServerTests{
    /*
     * --------------------------------- UNIT TESTS ---------------------------------
     */

    // Login to test user
    @Test
    public void testLoginEndpoint() {
        Model model = new MockModel();
        String retrievedUserId = model.performLoginRequest("test_user", "password");
        assertEquals(MockModel.mockUserId, retrievedUserId);
    }

}
