package controller;

import java.net.URL;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapitools.builders.BuilderSymmetricObjectProperty;

import DAO.OntologieDAO;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import model.algoNotationRecherche;

/**
 * @author Noe
 */
public class CtrlView implements Initializable {

	@FXML
	private Button searchButton;
	private HashMap<String, String> resultMap = new HashMap<String, String>();;

	ObservableList<String> Resultlist = FXCollections.observableArrayList(resultMap.keySet());

	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);
	@FXML
	private TextFlow tdDetail;

	@FXML
	TreeView<String> tree = new TreeView<String>();
	static algoNotationRecherche algoSearch;

	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			algoSearch = new algoNotationRecherche("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
			resultView.setFixedCellSize(25);
			resultView.setItems(Resultlist);
			setSelectedResultDisplayDetailTd();
			initTreeView();
			setClikedCellAction();
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initTreeView() throws OWLException {
		// TreeItem<String> root = new TreeItem<String>("Private_Policy");
		// Privacy Policy is the Ontology classe I Use
		TreeItem<String> root = new TreeItem<String>("PrivacyPolicy");
		root.setExpanded(true);
		OntologieDAO ont = new OntologieDAO("WotPriv.owl");
		// hashmap of the ontology
		HashMap<String, ArrayList<String>> hm = ont.getClassesHashMap();
		// work only for my ontology
		for (String s : hm.get(root.getValue())) {
			TreeItem<String> tI = new TreeItem<String>(s);
			root.getChildren().add(tI);
			if (hm.containsKey(s)) {
				for (String s1 : hm.get(s)) {
					TreeItem<String> n = new TreeItem<String>(s1);
					TreeItem<String> tI1 = root.getChildren().get(root.getChildren().indexOf(tI));
					tI1.getChildren().add(n);
					if (hm.containsKey(s1)) {
						for (String s2 : hm.get(s1)) {
							tI1.getChildren().get(tI1.getChildren().indexOf(n)).getChildren()
									.add(new TreeItem<String>(s2));
						}
					}
				}
			}
		}
		tree.setRoot(root);
		tree.setFixedCellSize(25);
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);


	}
	public void setClikedCellAction() {
		tree.setCellFactory(tree -> {
			TreeCell<String> cell = new TreeCell<String>() {
				@Override
				public void updateItem(String item, boolean empty) {
					super.updateItem(item, empty);
					if (empty) {
						setText(null);
					} else {
						setText(item);

					}
				}
			};
			cell.setOnMouseClicked(event -> {
				if (cell.isEmpty()) {
					Platform.runLater(() -> tree.getSelectionModel().clearSelection());
					// do whatever you need with the treeItem...
				} else  {
					if(cell.isSelected()) {
						tree.getSelectionModel().clearSelection(cell.getIndex());
						selectAllSubItemsRec(cell.getTreeItem());
					DisplayResultSearch();}else {
					}
				}
			});
			return cell;
		});

	}
	
	private void DisplayResultSearch() {
		ArrayList<String> SelectedConcepts = new ArrayList<>();
		tree.getSelectionModel().getSelectedItems().forEach(item -> SelectedConcepts.add(item.getValue()));
		resultMap = algoSearch.schearTD(SelectedConcepts);
		Resultlist.setAll(resultMap.keySet());
	}

	private void selectAllSubItemAndParent(TreeItem<String> courant) {
			tree.getSelectionModel().select(courant);
			for (TreeItem<String> e : courant.getChildren()) {
				selectAllSubItemAndParent(e);}

	}
	private void selectAllSubItemsRec(TreeItem<String> courant) {
		if (courant.getChildren().isEmpty()) {
			tree.getSelectionModel().select(courant);
		}else {	
		for (TreeItem<String> e : courant.getChildren()) {
			selectAllSubItemsRec(e);}}
		
	}

	@FXML
	public void setSelectedResultDisplayDetailTd() {
		resultView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> arg0, String arg1, String arg2) {
				Text t1 = new Text(resultMap.get(resultView.getSelectionModel().getSelectedItem()));
				tdDetail.getChildren().setAll(t1);

			}
		});

	}

}