## How to Run

1. First, you will need to download the JavaFX SDK. Then create a new folder lib and copy all the files from javafx-sdk-21/lib to this folder.
2. If building in VSCode, copy the configuration below to create a launch.json file and replace 'YOUR_LIB_FILE_PATH' to the path to your JavaFX SDK file path (not the one in this directory).

Configuration: 
```
{
    // Use IntelliSense to learn about possible attributes.
    // Hover to view descriptions of existing attributes.
    // For more information, visit: https://go.microsoft.com/fwlink/?linkid=830387
    "version": "0.2.0",
    "configurations": [
        {
            "type": "java",
            "name": "PantryPal GUI",
            "request": "launch",
            "mainClass": "PantryPal.App",
            "vmArgs": "--module-path 'YOUR_LIB_FILE_PATH' --add-modules javafx.controls,javafx.fxml"
        },
    ]
}
```
