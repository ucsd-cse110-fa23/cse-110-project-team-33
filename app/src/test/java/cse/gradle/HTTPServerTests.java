package cse.gradle;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import cse.gradle.Server.Server;

public abstract class HTTPServerTests {

    @BeforeAll
    public static void setUpBeforeClass() throws Exception {
        Server.startServer();
        System.out.println("Server started before running tests.");
    }

    @AfterAll
    public static void tearDownAfterClass() throws Exception {
        Server.stopServer();
        System.out.println("Server stopped after running tests.");
    }
    
}
