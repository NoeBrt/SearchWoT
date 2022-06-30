package controller;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;
import org.semanticweb.owlapi.model.OWLException;
import DAO.OntologyDAO;
import application.App;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import model.TdModel;
import parser.ParseException;

/**
 * @author Noe
 */
public class CtrlView implements Initializable {
	public static Stage CtrlStage;
	private static OntologyDAO ontology;
	private static TdModel algoSearch;
	@FXML
	private TreeView<String> tree = new TreeView<String>();

	private HashMap<String, String> resultMap = new HashMap<String, String>();;
	private ObservableList<String> Resultlist = FXCollections.observableArrayList(resultMap.keySet());
	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);

	@FXML
	private TextFlow tdDetail;

	@FXML
	private Label leftStatut;
	@FXML
	private Label rightStatut;

	private Deque<MenuItem> dequeRecentOpen = new ArrayDeque<MenuItem>();
	@FXML
	private Menu openRecent;

	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			ontology = new OntologyDAO("Ontology/WotPriv.owl");
			algoSearch = new TdModel("IoT-Devices-Benchmark_ANNOTE","privacyPolicy");
			dequeRecentOpen.addFirst(new MenuItem(algoSearch.getDir().getPath()));
			openRecent.getItems().setAll(dequeRecentOpen);
			leftStatut.setText(algoSearch.getDir().getPath());
			rightStatut.setText(algoSearch.getJsonObjectList().size() + " total");
			rightStatut.setTextFill(Color.web("#008080"));
			resultView.setItems(Resultlist);
			setTreeView(ontology.getName(), ontology.getSuperClassesHashMap());
			setSelectedResultDisplayDetailTd();
			setClikedTreeCellAction();
		} catch (OWLException e) {
			displayOwlError();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setTreeView(String rootName, LinkedHashMap<String, ArrayList<String>> owlClasseMap)
			throws OWLException {
		TreeItem<String> root = new TreeItem<String>(rootName);
		root.setExpanded(true);
		if (!rootName.equals(ontology.getName())) {
			tree.setRoot(treeSubItemsRec(root, owlClasseMap));
		} else {
			tree.setRoot(treeSubItemsRecAutoRoot(root, owlClasseMap));
		}
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}

	public void selectAllSubTreeItemsRec(TreeItem<String> courant) {
		if (courant.getChildren().isEmpty()) {
			tree.getSelectionModel().select(courant);
		} else {
			for (TreeItem<String> e : courant.getChildren()) {
				selectAllSubTreeItemsRec(e);
			}
		}

	}

	private TreeItem<String> treeSubItemsRec(TreeItem<String> courant, LinkedHashMap<String, ArrayList<String>> map) {
		if (map.containsKey(courant.getValue())) {
			for (String s : map.get(courant.getValue())) {
				TreeItem<String> t = new TreeItem<String>(s);
				courant.getChildren().add(t);
				treeSubItemsRec(t, map);
			}
		}

		return courant;
	}

	private TreeItem<String> treeSubItemsRecAutoRoot(TreeItem<String> courant,
			LinkedHashMap<String, ArrayList<String>> map) {
		for (String s : map.keySet()) {
			if (!containInTree(courant, s))
				courant.getChildren().add(treeSubItemsRec(new TreeItem<String>(s), map));
		}
		return courant;

	}

	private boolean containInTree(TreeItem<String> courant, String value) {
		for (TreeItem<String> e : courant.getChildren()) {
			if (containInTree(e, value)) {
				return true;
			}
		}
		return courant.getValue().equals(value);
	}

	public void setClikedTreeCellAction() {
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
					rightStatut.setText(algoSearch.getJsonObjectList().size() + " total");
				} else {
					if (cell.isSelected()) {
						if (!event.isAltDown()) {
							tree.getSelectionModel().clearSelection(cell.getIndex());
							selectAllSubTreeItemsRec(cell.getTreeItem());
						}
						displayResultSearch();
					}
				}
			});

			return cell;
		});

	}

	@FXML
	public void displayResultSearch() {
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

	@FXML
	public void clikedOpenTdsDirectoryMenuItem() throws IOException, ParseException {
		DirectoryChooser dc = new DirectoryChooser();
		File file = dc.showDialog(null);
		if (file != null) {
			setJsonDirectory(file);
		}
	}

	@FXML
	public void loadOntologyMenuItem() throws IOException {
		if (CtrlLoadOntology.stage == null) {
			try {
				CtrlLoadOntology.showInterfaceLoad();
				if (CtrlLoadOntology.ontology != null && !CtrlLoadOntology.valueRootListBox.equals("choose root")) {
					CtrlView.setOntology(CtrlLoadOntology.ontology);
					// setOntology(CtrlLoadOntology.ontology);
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
				displayOwlError();
			} catch (NullPointerException e) {
			}
		} else {
			CtrlLoadOntology.stage.setAlwaysOnTop(true);
		}
	}

	public void clikedOpenRecentMenuItem() {
		for (MenuItem mi : openRecent.getItems())
			mi.setOnAction(a -> {
				try {
					setJsonDirectory(new File(mi.getText()));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			});

	}

	private void setJsonDirectory(File file) throws IOException, ParseException {
		MenuItem miP = getMenuItem(file.getPath());
		algoSearch.setDir(file);
		leftStatut.setText(file.getPath());
		rightStatut.setText(resultMap.size() + " | " + algoSearch.getJsonObjectList().size() + " total");
		if (!tree.getSelectionModel().isEmpty()) {
			displayResultSearch();
		}
		setOpenRecentFileMenu(file, miP);
	}

	private void setOpenRecentFileMenu(File file, MenuItem miP) {
		if (!containMenuItem(file.getPath())) {
			dequeRecentOpen.addFirst(new MenuItem(file.getPath()));
		} else {
			dequeRecentOpen.addFirst(miP);
			dequeRecentOpen.removeLastOccurrence(miP);
		}
		openRecent.getItems().setAll(dequeRecentOpen);
	}

	private boolean containMenuItem(String s) {
		for (MenuItem m : dequeRecentOpen) {
			if (m.getText().equals(s)) {
				return true;
			}
		}
		return false;

	}

	private MenuItem getMenuItem(String s) {
		for (MenuItem m : dequeRecentOpen) {
			if (m.getText().equals(s)) {
				return m;
			}
		}
		return null;

	}

	private void displayOwlError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("OWL Exception");
		alert.setHeaderText("ERROR");
		alert.setContentText("CANT'T LOAD OWL FILE");
		alert.showAndWait();
	}

	@FXML
	public void quitMenuItem() {
		CtrlStage.close();
	}

	public static OntologyDAO getOntology() {
		return ontology;
	}

	public static void setOntology(OntologyDAO ontology) {
		CtrlView.ontology = ontology;
		CtrlStage.setTitle(App.getTitle() + " - " + CtrlView.getOntology().getPath());
	}

}