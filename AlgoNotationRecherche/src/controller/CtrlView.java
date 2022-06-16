package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javax.swing.JTextField;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

/**
 * @author Noe,Quentin,Alex
 */
public class CtrlView implements Initializable {
	
	ObservableList<Concept>conceptsList;
	@FXML
	private Button searchButton;
	@FXML
	private TableView<Concept> tableView;
	@FXML
	TableColumn<Concept,JTextField> conceptName;
	@FXML
	TableColumn<Concept,String> levelPrivacy;
	
	@FXML
	public void add() {
	    tableView.setFixedCellSize(25);
        ComboBox<String> combo = new ComboBox<>();
        combo.getItems().addAll("hight","mid","low");
		tableView.getItems().add(new Concept("test",combo));
	}
	
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		ChoiceBox<String> box = new ChoiceBox<>();
		ObservableList<String> ancestors = FXCollections.observableArrayList("hight","mid","low");
		//levelPrivacy.setCellValueFactory(new PropertyValueFactory<Concept, String>("levelPrivacy"));
		levelPrivacy.setCellFactory(ComboBoxTableCell.forTableColumn(ancestors));
		//levelPrivacy.setCellValueFactory(new PropertyValueFactory<>());
	}

	
	
	
}