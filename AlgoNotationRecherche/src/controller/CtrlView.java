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
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
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
	static ObservableList<String> Resultlist;
	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);
	@FXML
	private TextFlow tdDetail;

	private HashMap<String, String> resultMap;

	@FXML
	TreeView<String> tree = new TreeView<String>();

	public void initialize(URL arg0, ResourceBundle arg1) {
		TreeItem<String> root = new TreeItem<String>("Private_Policy");
		tree.setFixedCellSize(25);
		root.setExpanded(true);
		TreeItem<String> itemJava = new TreeItem<String>("Confidentality");
		TreeItem<String> itemJSP = new TreeItem<String>("Notice");
		TreeItem<String> itemSpring = new TreeItem<String>("record");
		tree.setRoot(root);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		itemJava.getChildren().add(itemJSP);
		itemJava.getChildren().add(itemSpring);
		root.getChildren().add(itemJava);
	}
	

	@FXML
	public void resultSchearch() {
		if (!tree.getSelectionModel().isEmpty()) {
			algoNotationRecherche a = new algoNotationRecherche(
					"C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple");
			HashMap<String, String> hm = new HashMap<>();
			for (TreeItem<String> it : tree.getSelectionModel().getSelectedItems()) {
				hm.put(it.getValue(), "mid");
			}
			System.out.println(tree.getSelectionModel().getSelectedItem().getValue());
			resultMap = a.schearTD(hm);
			Resultlist = FXCollections.observableArrayList(resultMap.keySet());
			resultView.setFixedCellSize(25);
			resultView.setItems(Resultlist);
			System.out.println(resultView.getItems());
		}
	}

	@FXML
	public void displayDetailTd() {
		if (!resultView.getSelectionModel().isEmpty()) {
			tdDetail.getChildren().clear();
			Text t1 = new Text(resultMap.get(resultView.getSelectionModel().getSelectedItem()));
			tdDetail.getChildren().add(t1);
		}

	}

	@FXML
	public void remove() {
		if (!conceptsList.isEmpty())
			conceptsList.remove(conceptsList.size() - 1);
	}

}