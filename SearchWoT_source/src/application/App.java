
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
	private static String title;

	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader view1 = new FXMLLoader(CtrlView.class.getResource("/controller/mainView.fxml"));
			VBox root = view1.load();
			root.pickOnBoundsProperty();
			Scene scene = new Scene(root, 900, 600);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			title="SearchWoT";
			primaryStage.setTitle(title+" - "+CtrlView.getOntology().getPath());
			primaryStage.getIcons().add(new Image("file:iconApp/icon.png"));
			primaryStage.setOnCloseRequest(e -> {
				Platform.exit();
				System.exit(0);
			});
			CtrlView.CtrlStage=primaryStage;
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	public static String getTitle() {
		return title;
	}

	public static void setTitle(String title) {
		App.title = title;
	}
}
