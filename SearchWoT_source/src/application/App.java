
package application;

import controller.CtrlMainView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class App extends Application {
	/**
	 * app title
	 */
	private static String title;

	/**
	 * load mainView.fxml controlled by CtrlMainView 
	 */
	@Override
	public void start(Stage primaryStage) {
		try {
			FXMLLoader view1 = new FXMLLoader(CtrlMainView.class.getResource("/controller/mainView.fxml"));
			VBox root = view1.load();
			root.pickOnBoundsProperty();
			Scene scene = new Scene(root, 900, 600);
			primaryStage.setScene(scene);
			primaryStage.sizeToScene();
			title="SearchWoT";
			primaryStage.setTitle(title+" - "+CtrlMainView.getOntology().getPath());
			primaryStage.getIcons().add(new Image("file:iconApp/icon.png"));
			primaryStage.setOnCloseRequest(e -> {
				Platform.exit();
				System.exit(0);
			});
			CtrlMainView.setCtrlStage(primaryStage);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		launch(args);
	}

	/**
	 * @return title of the app
	 */
	public static String getTitle() {
		return title;
	}

}
