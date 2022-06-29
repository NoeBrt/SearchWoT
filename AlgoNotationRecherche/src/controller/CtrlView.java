package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
	static OntologieDAO ontology;

	static algoNotationRecherche algoSearch;
	public Label leftStatut;
	public Label rightStatut;

	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			ontology = new OntologieDAO("WotPriv.owl");
			algoSearch = new algoNotationRecherche("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
			algoSearch.getJsonObjectList().forEach(object -> System.out.println(object.get("title")));
			leftStatut.setText("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
			rightStatut.setText(algoSearch.getJsonObjectList().size() + " total");
			rightStatut.setTextFill(Color.web("#008080"));
			resultView.setItems(Resultlist);
			setTreeView(ontology.getName(), ontology.getSuperClassesHashMap());
			setSelectedResultDisplayDetailTd();
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

	private void setTreeView(String rootName, LinkedHashMap<String, ArrayList<String>> owlClasseMap)
			throws OWLException {
		TreeItem<String> root = new TreeItem<String>(rootName);
		root.setExpanded(true);
		if (!rootName.equals(ontology.getName())) {
			tree.setRoot(TreeSubItemsRec(root, owlClasseMap));
		} else {
			tree.setRoot(TreeSubItemsRec2(root, owlClasseMap));
		}
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

	public TreeItem<String> TreeSubItemsRec(TreeItem<String> courant, LinkedHashMap<String, ArrayList<String>> map) {
		if (map.containsKey(courant.getValue())) {
			for (String s : map.get(courant.getValue())) {
				TreeItem<String> t = new TreeItem<String>(s);
				courant.getChildren().add(t);
				TreeSubItemsRec(t, map);
			}
		}

		return courant;
	}

	public TreeItem<String> TreeSubItemsRec2(TreeItem<String> courant, LinkedHashMap<String, ArrayList<String>> map) {
		for (String s : map.keySet()) {
			if (!contain(courant, s))
				courant.getChildren().add(TreeSubItemsRec(new TreeItem<String>(s), map));
		}
		return courant;

	}

	public boolean contain(TreeItem<String> courant, String value) {
		for (TreeItem<String> e : courant.getChildren()) {
			if (contain(e, value)) {
				return true;
			}
		}
		return courant.getValue().equals(value);
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
			if (CtrlLoadOntology.ontology != null && !CtrlLoadOntology.valueRootListBox.equals("choose root")) {
				ontology = CtrlLoadOntology.ontology;
				if (CtrlLoadOntology.isAutoButtonSelected()) {
					tree.setShowRoot(false);
					if (CtrlLoadOntology.isInvertedButtonSelected()) {
						setTreeView(ontology.getName(), ontology.getSuperClassesHashMap());
					} else {
						setTreeView(ontology.getName(), ontology.getSubClassesHashMap());
					}
				} else {
					tree.setShowRoot(true);
					if (CtrlLoadOntology.isInvertedButtonSelected()) {
						setTreeView(CtrlLoadOntology.valueRootListBox, ontology.getSuperClassesHashMap());
					} else {
						setTreeView(CtrlLoadOntology.valueRootListBox, ontology.getSubClassesHashMap());
					}

				}
			}
		} catch (OWLException e) {
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("OWL Exception");
			alert.setHeaderText("ERROR");
			alert.setContentText("CANT'T LOAD OWL FILE");
			alert.showAndWait();
		}

	}

	public static OntologieDAO getOntology() {
		return ontology;
	}

	public static void setOntology(OntologieDAO ontology) {
		CtrlView.ontology = ontology;
	}

}