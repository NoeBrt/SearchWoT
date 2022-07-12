package controller;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * Controller of preferenceView.fxml
 * 
 * @author noebr
 *
 */
public class CtrlPreferenceView {

	/**
	 * current stage
	 */
	private static Stage stage;
	/**
	 * static String value destined to be used in CtrlMainView, store part TD name
	 * wrote in TextField partTdNameLabel
	 */
	private static String valuePartTdNameLabel;
	/**
	 * TextField the user will wrote in the td part name that will be analyze
	 */
	@FXML
	private TextField partTdNameLabel;
	/**
	 * label displayed if the textfield is empty
	 */
	@FXML
	private Label redLabel;

	/**
	 * Initialize the view
	 * 
	 * @throws IOException
	 */
	public static void showPreferenceView() throws IOException {
		stage = new Stage();
		FXMLLoader interfaceConnect = new FXMLLoader(CtrlMainView.class.getResource("/controller/preferenceView.fxml"));
		Parent root = interfaceConnect.load();
		stage.setScene(new Scene(root));
		stage.getIcons().add(new Image("file:iconApp/icon.png"));
		stage.setTitle("Preference");
		stage.sizeToScene();
		stage.setResizable(false);
		stage.setAlwaysOnTop(true);
		stage.showAndWait();
	}

	/**
	 * close stage if cancel button is pressed
	 */
	@FXML
	public void cancelAction() {
		stage.close();
	}

	/**
	 * static valuePartTdNameLabel initialize by content of the text field if it is
	 * not empty, else the red label appears
	 */
	@FXML
	public void applyAction() {
		if (!partTdNameLabel.getText().isBlank()) {
			valuePartTdNameLabel = partTdNameLabel.getText();
			stage.close();
		} else {
			redLabel.setVisible(true);
		}

	}

	/**
	 * hide redLabel
	 */
	public void hideRedLabel() {
		redLabel.setVisible(false);
	}

	/**
	 * @return valuePartTdNameLabel
	 */
	public static String getValuePartTdNameLabel() {
		return valuePartTdNameLabel;
	}

	/**
	 * @return stage
	 */
	public static Stage getStage() {
		return stage;
	}

}
