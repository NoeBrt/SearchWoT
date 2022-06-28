package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import org.semanticweb.owlapi.model.OWLException;

import DAO.OntologieDAO;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;

import model.algoNotationRecherche;
import parser.ParseException;

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
	public Label leftStatut;
	public Label rightStatut;
	

	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			algoSearch = new algoNotationRecherche("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
			algoSearch.getJsonObjectList().forEach(object -> System.out.println(object.get("title")));
			leftStatut.setText("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
			rightStatut.setText(algoSearch.getJsonObjectList().size() + " total");
			rightStatut.setTextFill(Color.web("#008080"));
			resultView.setFixedCellSize(25);
			resultView.setItems(Resultlist);
			setSelectedResultDisplayDetailTd();
			setTreeView("PrivacyPolicy", new OntologieDAO("WotPriv.owl"));
			setClikedCellAction();
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setTreeView(String rootName,OntologieDAO onto) throws OWLException {
		TreeItem<String> root = new TreeItem<String>(rootName);
		root.setExpanded(true);
		OntologieDAO ont = onto;
		HashMap<String, ArrayList<String>> hm = ont.getClassesHashMap();
		tree.setRoot(TreeSubItemsRec(root, hm));
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
				} else {
					if (cell.isSelected()) {
						if (!event.isAltDown()) {
							tree.getSelectionModel().clearSelection(cell.getIndex());
							selectAllSubItemsRec(cell.getTreeItem());
						}
						DisplayResultSearch();
					}
				}
			});

			return cell;
		});

	}

	public void DisplayResultSearch() {
		ArrayList<String> SelectedConcepts = new ArrayList<>();
		tree.getSelectionModel().getSelectedItems().forEach(item -> SelectedConcepts.add(item.getValue()));
		resultMap = algoSearch.schearTD(SelectedConcepts);
		Resultlist.setAll(resultMap.keySet());
		rightStatut.setText(resultMap.size() + " | " + algoSearch.getJsonObjectList().size() + " total");

	}
	/*
	 * private void selectAllSubItemAndParent(TreeItem<String> courant) {
	 * tree.getSelectionModel().select(courant); for (TreeItem<String> e :
	 * courant.getChildren()) { selectAllSubItemAndParent(e); }
	 * 
	 * }
	 */

	private void selectAllSubItemsRec(TreeItem<String> courant) {
		if (courant.getChildren().isEmpty()) {
			tree.getSelectionModel().select(courant);
		} else {
			for (TreeItem<String> e : courant.getChildren()) {
				selectAllSubItemsRec(e);
			}
		}

	}

	public TreeItem<String> TreeSubItemsRec(TreeItem<String> courant, HashMap<String, ArrayList<String>> map) {
		if (map.containsKey(courant.getValue())) {
			for (String s : map.get(courant.getValue())) {
				TreeItem<String> t = new TreeItem<String>(s);
				courant.getChildren().add(t);
				TreeSubItemsRec(t, map);
			}
		}

		return courant;

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

	public void menuButtonClikedOpenTd() throws IOException, ParseException {
		DirectoryChooser dc = new DirectoryChooser();
		File file = dc.showDialog(null);

		if (file != null) {
			algoSearch.setDir(file);
			if (!tree.getSelectionModel().isEmpty()) {
				DisplayResultSearch();
			}
			leftStatut.setText(file.getAbsolutePath());
			rightStatut.setText(resultMap.size() + " | " + algoSearch.getJsonObjectList().size() + " total");
		}

		else {
			System.out.println("invalide file");
		}
	}

	public void loadOntologyAction() throws IOException {
				CtrlLoadOntology.showInterfaceLoad();
				try {
					setTreeView(CtrlLoadOntology.valueRootListBox,CtrlLoadOntology.ontology);
				} catch (OWLException e) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setTitle("OWL Exception");
					alert.setHeaderText("ERROR");
					alert.setContentText("CANT'T LOAD OWL FILE");
					alert.showAndWait();
				}
		
	}

}