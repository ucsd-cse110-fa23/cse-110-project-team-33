package cse.gradle.Server;

public class ErrorWebPageBuilder implements WebPageBuilder {
    private String errorMessage;

    public ErrorWebPageBuilder(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getWebpage() {
        return "<html><body><h1>Error</h1><p>" + errorMessage + "</p></body></html>";
    }
}