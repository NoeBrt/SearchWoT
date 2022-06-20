
package application;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import controller.CtrlView;
import javafx.application.Application;

import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Scene;

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
		//	primaryStage.getIcons().add(new Image("/IconApp/icon.jpg"));
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
