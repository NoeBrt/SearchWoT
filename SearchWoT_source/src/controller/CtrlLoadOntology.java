package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import org.semanticweb.owlapi.model.OWLException;

import DAO.OntologyDAO;
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
	private static Stage stage;
	private static OntologyDAO ontology;
	private static boolean isInvertedButtonSelected;
	private static boolean isAutoButtonSelected;
	private static String valueRootListBox;
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

	/**
	 * @throws IOException
	 */
	public static void showInterfaceLoad() throws IOException {
		stage = new Stage();
		FXMLLoader interfaceConnect = new FXMLLoader(
				CtrlMainView.class.getResource("/controller/loadOntologyView.fxml"));
		Parent root = interfaceConnect.load();
		stage.setScene(new Scene(root));
		stage.getIcons().add(new Image("file:iconApp/icon.png"));
		stage.setTitle("Load OWL Ontology");
		stage.sizeToScene();
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.showAndWait();

	}

	/**
	 * @throws IOException
	 */
	@FXML
	public void openOWlFile() throws IOException {
		FileChooser fc = new FileChooser();
		fc.getExtensionFilters().add(new ExtensionFilter("OWL Files", "*.owl"));
		stage.setAlwaysOnTop(false);
		File file = fc.showOpenDialog(null);
		stage.setAlwaysOnTop(true);
		if (file != null) {
			try {
				setOntology(new OntologyDAO(file.getCanonicalPath()));
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

	/**
	 * @param onto
	 * @throws OWLException
	 * @throws IOException
	 */
	public void setOntology(OntologyDAO onto) throws OWLException, IOException {
		ontology = onto;
		pathOwl.setText(ontology.getPath());
		rootList.setAll(ontology.getClassesName());
		rootListBox.getItems().setAll(rootList);
	}

	/**
	 * 
	 */
	@FXML
	public void loadButtonPressed() {
		Stage stage = (Stage) rootListBox.getScene().getWindow();
		if (ontology != null) {
			if (rootListBox.getValue().equals("choose a root") && !autoButton.selectedProperty().get()) {
				instructionRedLabel.setVisible(true);
			} else {
				valueRootListBox = rootListBox.getValue();
				isAutoButtonSelected = autoButton.selectedProperty().get();
				isInvertedButtonSelected = invertedButton.selectedProperty().get();
				stage.close();
			}
		}
	}

	/**
	 * 
	 */
	@FXML
	public void hideChoiceBoxWhenAutoCheked() {
		if (autoButton.selectedProperty().get()) {
			instructionRedLabel.setVisible(false);
			rootListBox.setDisable(true);

		} else {
			rootListBox.setDisable(false);

		}

	}

	/**
	 *
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		rootListBox.setValue("choose a root");
		try {
			setOntology(CtrlMainView.getOntology());
		} catch (OWLException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 */
	@FXML
	public void eventHideRedLabel() {
		instructionRedLabel.setVisible(false);
	}

	/**
	 * @return
	 */
	public static boolean isInvertedButtonSelected() {
		return isInvertedButtonSelected;
	}

	/**
	 * @return
	 */
	public static boolean isAutoButtonSelected() {
		return isAutoButtonSelected;
	}

	/**
	 * @return
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * @return
	 */
	public static String getValueRootListBox() {
		return valueRootListBox;
	}

	/**
	 * @return
	 */
	public static OntologyDAO getOntology() {
		return ontology;
	}

}
