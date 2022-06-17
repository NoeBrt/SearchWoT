package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.swing.JTextField;

import javafx.beans.binding.Bindings;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.algoNotationRecherche;

/**
 * @author Noe
 */
public class CtrlView implements Initializable {

	ObservableList<Concept> conceptsList = FXCollections.observableArrayList();
	@FXML
	private Button searchButton;
	@FXML
	private TableView<Concept> tableView;
	@FXML
	TableColumn<Concept, TextField> conceptName;
	@FXML
	TableColumn<Concept, StringProperty> levelPrivacy;
	@FXML
	private RadioButton and;
	@FXML
	private RadioButton or;
	static ObservableList<String> Resultlist;
	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);
	@FXML
	private TextFlow tdDetail;

	@FXML
	public void add() {
		conceptsList.add(new Concept("test"));
		System.out.println(conceptsList.get(0).toString());
		System.out.println(levelPrivacy.getCellData(1));
		System.out.println(levelPrivacy.getCellObservableValue(0));
	}

	private HashMap<String, String> resultMap;

	@FXML
	public void resultSchearch() {
		algoNotationRecherche a = new algoNotationRecherche(
				"C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple");
		HashMap<String, String> hm = new HashMap<>();
		hm.put("Disclose", "mid");
		resultMap = a.schearTD(hm);
		Resultlist = FXCollections.observableArrayList(resultMap.keySet());
		resultView.setFixedCellSize(25);
		resultView.setItems(Resultlist);
		System.out.println(resultView.getItems());
	}

	@FXML
	public void displayDetailTd() {
		tdDetail.getChildren().clear();
		System.out.println(resultMap.get(resultView.getSelectionModel().getSelectedItem()));
		Text t1 = new Text(resultMap.get(resultView.getSelectionModel().getSelectedItem()));
		tdDetail.getChildren().add(t1);

	}

	@FXML
	public void remove() {
		if (!conceptsList.isEmpty())
			conceptsList.remove(conceptsList.size() - 1);
	}

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		conceptsList = FXCollections.observableArrayList(new Concept("test"));
		tableView.setEditable(true);
		tableView.setFixedCellSize(30);
		ObservableList<String> level = FXCollections.observableArrayList("hight", "mid", "low");

		// TODO Auto-generated method stub
		conceptName.setCellValueFactory(new PropertyValueFactory<>("name"));
		// levelPrivacy.setCellFactory(ComboBoxTableCell.forTableColumn(ancestors));
		levelPrivacy.setCellValueFactory(i -> {
			final StringProperty value = i.getValue().getlevelPrivacy();
			// binding to constant value
			return Bindings.createObjectBinding(() -> value);
		});
		levelPrivacy.setCellFactory(col -> {
			TableCell<Concept, StringProperty> c = new TableCell<>();
			final ComboBox<String> comboBox = new ComboBox<>(level);
			comboBox.setValue("choose");

			c.itemProperty().addListener((observable, oldValue, newValue) -> {
				if (oldValue != null) {
					comboBox.valueProperty().unbindBidirectional(oldValue);
				}
				if (newValue != null) {
					comboBox.valueProperty().unbindBidirectional(newValue);
					conceptsList.get(0).setlevelPrivacy(newValue.get());

				}
			});
			c.graphicProperty().bind(Bindings.when(c.emptyProperty()).then((Node) null).otherwise(comboBox));
			return c;
		});
		System.out.println(levelPrivacy.getCellObservableValue(0));

		// levelPrivacy.setCellFactory(ComboBoxTableCell.forTableColumn("Friends",
		// "Family", "Work Contacts"));

		tableView.setItems(conceptsList);
		System.out.println(levelPrivacy.getText());

		// levelPrivacy.setCellValueFactory(new PropertyValueFactory<>());
	}

}