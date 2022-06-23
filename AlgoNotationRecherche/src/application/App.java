
package application;

import controller.CtrlView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class App extends Application {
	public static Stage primaryStage;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader view1 = new FXMLLoader(CtrlView.class.getResource("/controller/viewWoTImp.fxml"));
			VBox root = view1.load();

			root.pickOnBoundsProperty();
			Scene scene = new Scene(root, 800, 600);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			primaryStage.setTitle("WoT");
			primaryStage.getIcons().add(new Image("application/icon.png"));
			// primaryStage.getIcons().add(new Image("/IconApp/icon.jpg"));
			primaryStage.show();
	        scene.getStylesheets().add("/controller/cssViewWoTImp.css");
			primaryStage.setOnCloseRequest(e -> {
				Platform.exit();
				System.exit(0);
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}
