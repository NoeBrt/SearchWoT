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

	public static void showPreferenceView() throws IOException {
		stage = new Stage();
		FXMLLoader interfaceConnect = new FXMLLoader(CtrlMainView.class.getResource("/controller/preferenceView.fxml"));
		Parent root = interfaceConnect.load();
		getStage().setScene(new Scene(root));
		getStage().getIcons().add(new Image("file:iconApp/icon.png"));
		getStage().setTitle("Preference");
		getStage().sizeToScene();
		getStage().setResizable(false);
		getStage().setAlwaysOnTop(true);
		getStage().showAndWait();
	}

	@FXML
	public void cancelAction() {
		getStage().close();
	}

	@FXML
	public void applyAction() {
		if(!partTdNameLabel.getText().isBlank()) {
			valuePartTdNameLabel=partTdNameLabel.getText();
			getStage().close();
		}else {
			redLabel.setVisible(true);
		}

	}
	public void hideRedLabel() {
		redLabel.setVisible(false);
	}

	public static String getValuePartTdNameLabel() {
		return valuePartTdNameLabel;
	}

	public static Stage getStage() {
		return stage;
	}

}
