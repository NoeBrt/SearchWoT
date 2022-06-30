package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.semanticweb.owlapi.model.OWLException;

import DAO.OntologieDAO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

public class CtrlLoadOntology implements Initializable {
	public static Stage stage;
	public static OntologieDAO ontology;
	private static boolean isInvertedButtonSelected;
	private static boolean isAutoButtonSelected;
	public static String valueRootListBox;
	@FXML
	private Label pathOwl;
	@FXML
	private ObservableList<String> rootList = FXCollections.observableArrayList();
	@FXML
	private ChoiceBox<String> rootListBox = new ChoiceBox<>(rootList);
	@FXML
	private CheckBox invertedButton;
	@FXML
	private CheckBox autoButton;
	@FXML
	private Label instructionRedLabel;


	public static void showInterfaceLoad() throws IOException {
		stage = new Stage();
		FXMLLoader interfaceConnect = new FXMLLoader(CtrlView.class.getResource("/controller/popUpOntoChoose.fxml"));
		Parent root = interfaceConnect.load();
		stage.setScene(new Scene(root));
		stage.getIcons().add(new Image("/application/icon.png"));
		stage.setTitle("Load OWL Ontology");
		stage.sizeToScene();
		stage.setResizable(false);
		stage.showAndWait();
		stage.setOnCloseRequest(e -> {
			Platform.exit();
			System.exit(0);
		});
		
	}

	@FXML
	public void openOWlFile() throws IOException {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("OWL Files", "*.owl"));
		File file = fc.showOpenDialog(null);
		if (file != null) {
			try {
				setOntology(new OntologieDAO(file.getCanonicalPath()));
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
	}

	public void setOntology(OntologieDAO onto) throws OWLException, IOException {
		ontology = onto;
		pathOwl.setText(ontology.getPath());
		rootList.setAll(ontology.getClassesName());
		rootListBox.getItems().setAll(rootList);
	}

	@FXML
	public void loadButtonPressed() {
		Stage stage = (Stage) rootListBox.getScene().getWindow();
				if (ontology != null) {
					if(rootListBox.getValue().equals("choose a root")&&!autoButton.selectedProperty().get()) {
						instructionRedLabel.setVisible(true);
					}else {
					valueRootListBox = rootListBox.getValue();
					setAutoButtonSelected(autoButton.selectedProperty().get());
					setInvertedButtonSelected(invertedButton.selectedProperty().get());
					stage.close();
				}}
	}
	
	@FXML
	public void hideChoiceBoxWhenAutoCheked()
	{
		if (autoButton.selectedProperty().get()) {
			instructionRedLabel.setVisible(false);
			rootListBox.setDisable(true); 

		}else {
			rootListBox.setDisable(false); 

		}
		
	}
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		rootListBox.setValue("choose a root");
		try {
			setOntology(CtrlView.getOntology());
		} catch (OWLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void eventHideRedLabel() {
		instructionRedLabel.setVisible(false);
	}

	public static boolean isInvertedButtonSelected() {
		return isInvertedButtonSelected;
	}

	public static void setInvertedButtonSelected(boolean isInvertedButtonSelected) {
		CtrlLoadOntology.isInvertedButtonSelected = isInvertedButtonSelected;
	}

	public static boolean isAutoButtonSelected() {
		return isAutoButtonSelected;
	}

	public static void setAutoButtonSelected(boolean isAutoButtonSelected) {
		CtrlLoadOntology.isAutoButtonSelected = isAutoButtonSelected;
	}

}
