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

public class CtrlPreferenceView {

	private static Stage stage;
	private static String valuePartTdNameLabel;
	@FXML
	private TextField partTdNameLabel;
	@FXML
	private Label redLabel;

	/**
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
	 * 
	 */
	@FXML
	public void cancelAction() {
		stage.close();
	}

	/**
	 * 
	 */
	@FXML
	public void applyAction() {
		if(!partTdNameLabel.getText().isBlank()) {
			valuePartTdNameLabel=partTdNameLabel.getText();
			stage.close();
		}else {
			redLabel.setVisible(true);
		}

	}
	/**
	 * 
	 */
	public void hideRedLabel() {
		redLabel.setVisible(false);
	}

	/**
	 * @return
	 */
	public static String getValuePartTdNameLabel() {
		return valuePartTdNameLabel;
	}

	/**
	 * @return
	 */
	public static Stage getStage() {
		return stage;
	}

}
