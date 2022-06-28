package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.semanticweb.owlapi.model.OWLException;

import DAO.OntologieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CtrlLoadOntology implements Initializable {
	static Parent root;
	public static OntologieDAO ontology;
	@FXML
	public Label pathOwl;
	@FXML
	ObservableList<String> rootList = FXCollections.observableArrayList();
	@FXML
	private ChoiceBox<String> rootListBox = new ChoiceBox<>(rootList);
	public static String valueRootListBox;

	public static void showInterfaceLoad() throws IOException {
		Stage stage = new Stage();
		FXMLLoader interfaceConnect = new FXMLLoader(CtrlView.class.getResource("/controller/popUpOntoChoose.fxml"));
		root = interfaceConnect.load();
		stage.setScene(new Scene(root));
		stage.getIcons().add(new Image("/application/icon.png"));
		stage.setTitle("Load OWL Ontology");
		stage.sizeToScene();
		stage.setResizable(false);
		stage.showAndWait();
	}

	@FXML
	public void openOWlFile() throws IOException {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("OWL Files", "*.owl"));
		File file = fc.showOpenDialog(null);
		if (file != null) {
			try {
				ontology = new OntologieDAO(file.getAbsolutePath());
				pathOwl.setText(file.getCanonicalPath());
				rootList.setAll(ontology.getClassesName());
				rootListBox.getItems().setAll(rootList);
				// ontology.getClassesName();
			} catch (OWLException e) {
				// TODO Auto-generated catch block
				Alert alert = new Alert(AlertType.ERROR);
				alert.setTitle("OWL Exception");
				alert.setHeaderText("ERROR");
				alert.setContentText("CANT'T LOAD OWL FILE");
				alert.showAndWait();
			}
		}

		else {
			System.out.println("invalide file");
		}
	}
	
	@FXML
	public void enterPressed() {
		  rootListBox.setOnKeyPressed( event -> {
			  if( event.getCode() == KeyCode.ENTER ) {
					if(!rootListBox.getValue().equals("choose root")&&ontology!=null) {
						 Stage stage = (Stage) rootListBox.getScene().getWindow();
						 valueRootListBox= rootListBox.getValue();
					stage.close();}
			  }
			} );
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		rootListBox.setValue("choose root");
		enterPressed();

	}

}
