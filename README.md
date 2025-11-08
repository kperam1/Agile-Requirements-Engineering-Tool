# IdeaBoard - JavaFX 21 Application

A modern JavaFX 21 application featuring a "Create New Idea" modal form with a clean, professional UI design.

## Features

- **Modern UI Design**: Rounded corners, soft shadows, and blue focus states
- **Responsive Modal Form**: Clean layout with proper spacing and visual hierarchy
- **Validation**: Required field validation with user-friendly error messages
- **Keyboard Support**: Enter key creates idea, Escape cancels
- **Ready for Backend**: Placeholder for POST API integration

## Project Structure

```
idea/
├── src/main/
│   ├── java/
│   │   ├── module-info.java
│   │   └── com/example/ideaboard/
│   │       ├── MainApp.java
│   │       ├── controllers/
│   │       │   └── CreateIdeaController.java
│   │       └── util/
│   │           └── DialogHelper.java
│   └── resources/
│       └── com/example/ideaboard/
│           ├── views/
│           │   └── create_idea.fxml
│           └── styles/
│               └── app.css
└── pom.xml
```

## Requirements

- Java 21 or higher
- Maven 3.8+
- JavaFX 21

## Running the Application

### Using Maven:

```bash
mvn clean javafx:run
```

### Using IDE:
Run the `MainApp.java` class directly from your IDE.

## Opening the Create Idea Dialog Programmatically

### Method 1: Using DialogHelper Utility

```java
import com.example.ideaboard.util.DialogHelper;

// From any button or action
Button createButton = new Button("Create New Idea");
createButton.setOnAction(e -> DialogHelper.openCreateIdeaDialog());
```

### Method 2: Direct Implementation

```java
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public void openCreateIdeaDialog() {
    try {
        FXMLLoader loader = new FXMLLoader(
            getClass().getResource("/com/example/ideaboard/views/create_idea.fxml")
        );
        Parent dialogContent = loader.load();
        
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Create New Idea");
        dialogStage.setResizable(false);
        
        Scene dialogScene = new Scene(dialogContent);
        dialogScene.getStylesheets().add(
            getClass().getResource("/com/example/ideaboard/styles/app.css").toExternalForm()
        );
        
        dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);
        dialogStage.setScene(dialogScene);
        dialogStage.showAndWait();
        
    } catch (Exception e) {
        e.printStackTrace();
    }
}
```

## Form Fields

1. **Title** (TextField) - Required field
2. **Category** (ChoiceBox) - Default: "Product Enhancement"
   - Options: Product Enhancement, New Feature, Bug Fix, Process Improvement, Research, Other
3. **Description** (TextArea) - Optional multi-line description
4. **Status** (ChoiceBox) - Default: "New"
   - Options: New, Under Review, Approved, In Progress, Completed, Rejected
5. **Owner Name** (TextField) - Optional

## Design Specifications

### Spacing
- Card padding: 24px
- Vertical spacing between sections: 24px
- Label to control spacing: 8px
- Stacked inputs spacing: 18px

### Colors
- Background: #F6F7FB
- Card surface: White (#FFFFFF)
- Border: #E5E7EB
- Primary button: #2563EB
- Text: #111827
- Muted text: #6B7280
- Focus ring: #3B82F6 with glow effect

### Typography
- Font family: Inter, Segoe UI, system-ui
- Title: 20px, weight 800
- Subtitle: 13px, gray
- Field labels: 12px, weight 700
- Inputs: 13px

## Next Steps

To integrate with a backend API, update the `createIdea()` method in `CreateIdeaController.java`:

```java
// TODO: POST JSON to http://localhost:8080/api/ideas
// Example using Java 11+ HttpClient:
String json = String.format("""
    {
        "title": "%s",
        "category": "%s",
        "description": "%s",
        "status": "%s",
        "ownerName": "%s"
    }
    """, title, category, description, status, owner);

HttpClient client = HttpClient.newHttpClient();
HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("http://localhost:8080/api/ideas"))
    .header("Content-Type", "application/json")
    .POST(HttpRequest.BodyPublishers.ofString(json))
    .build();

client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
    .thenApply(HttpResponse::statusCode)
    .thenAccept(statusCode -> {
        if (statusCode == 200 || statusCode == 201) {
            showSuccess("Success", "Idea created successfully!");
        }
    });
```

## License

MIT License
