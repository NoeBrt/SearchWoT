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
	/**
	 * current stage
	 */
	private static Stage stage;
	/**
	 * ontologyDAO loaded
	 */
	private static OntologyDAO ontology;
	/**
	 * static value destined to be used in CtrlMainView, store boolean value if
	 * invertedButton is selected
	 */
	private static boolean isInvertedButtonSelected;
	/**
	 * static value destined to be used in CtrlMainView, store boolean value if
	 * autoButton is selected
	 */
	private static boolean isAutoButtonSelected;
	/**
	 * static value destined to be used in CtrlMainView, store value selected in
	 * choicebox rootListBox.
	 */
	private static String valueRootListBox;
	/**
	 * label destined to display path of the ontoloy
	 */
	@FXML
	private Label pathOwl;
	/**
	 * ObservableList displayed in choicebox, contain all ontology classes name
	 */
	@FXML
	private ObservableList<String> rootList = FXCollections.observableArrayList();
	/**
	 * 
	 */
	@FXML
	private ChoiceBox<String> rootListBox = new ChoiceBox<>(rootList);
	/**
	 * determine if the ontology will be loaded inverted or not in CtrlMainView
	 */
	@FXML
	private CheckBox invertedButton;
	/**
	 * determine if the ontology will be loaded whitout user root selection
	 */
	@FXML
	private CheckBox autoButton;
	/**
	 * display if the load button is clicked without ontology choose
	 */
	@FXML
	private Label instructionRedLabel;

	/**
	 * set the ontologyDAO value by the static getter from CtrlMainView
	 */
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		rootListBox.setValue("choose a root");
		try {
			setOntology(CtrlMainView.getOntology());
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	 * Initialize the view
	 * 
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
	 * when open(.owl) button is cliked, user can selected an owl file and set the
	 * ontolgyDAO with this file. the label pathOwl will be set with the owl file
	 * path
	 * 
	 * @throws IOException and a alert frame display
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
	 * set the ontology and pathOwl label with his path, set the rootlist with all
	 * classes of the loaded ontology
	 * 
	 * @param ontologyDAO to load
	 * @throws OWLException
	 * @throws IOException
	 */
	public void setOntology(OntologyDAO onto) throws OWLException {
		ontology = onto;
		pathOwl.setText(ontology.getPath());
		rootList.setAll(ontology.getClassesName());
		rootListBox.getItems().setAll(rootList);
	}

	/**
	 * when the load button is pressed. if a owl file was selected, value of
	 * rootListBox, auto and inverted check box are store in static variable before
	 * closing the stage, else a red label appear
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
	 * when auto check box is selected, rootListBox is disabled
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
	 * hide red Label
	 */
	@FXML
	public void eventHideRedLabel() {
		instructionRedLabel.setVisible(false);
	}

	/**
	 * @return static value isInvertedButtonSelected, true if InvertedButton is
	 *         selected, false else
	 */
	public static boolean isInvertedButtonSelected() {
		return isInvertedButtonSelected;
	}

	/**
	 * @return static value isAutoButtonSelected, true if AutoButtonSelected is
	 *         selected, false else
	 */
	public static boolean isAutoButtonSelected() {
		return isAutoButtonSelected;
	}

	/**
	 * @return current stage
	 */
	public static Stage getStage() {
		return stage;
	}

	/**
	 * @return value of RootListBox
	 */
	public static String getValueRootListBox() {
		return valueRootListBox;
	}

	/**
	 * @return ontologyDAO loaded
	 */
	public static OntologyDAO getOntology() {
		return ontology;
	}

}
