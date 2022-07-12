package controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;
import model.TdModel;
import parser.ParseException;

/**Controller of mainView.fxml
 * @author noebr
 *
 */
public class CtrlMainView implements Initializable {
	/**
	 * stage of the view
	 */
	private static Stage CtrlStage;
	/**
	 * OntologyDAO for managing the ontology
	 */
	private static OntologyDAO ontology;
	/**
	 * for managing all the search request
	 */
	private static TdModel algoSearch;
	/**
	 * HasmMap of all result of the request, a list of Td name as key and content of
	 * the TD as value
	 */
	private HashMap<String, String> resultMap = new HashMap<String, String>();
	/**
	 * ObservableList of the Td's name, which actualiste value who appear in
	 * ListView (list of name td)
	 */
	private ObservableList<String> Resultlist = FXCollections.observableArrayList(resultMap.keySet());
	/**
	 * 
	 */
	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);
	/**
	 * treeview who represent the ontology
	 */
	@FXML
	private TreeView<String> tree = new TreeView<String>();
	/**
	 * right part of the application, who display content of the td selected in
	 * result section (middle part)
	 */
	@FXML
	private TextFlow tdDetail;
	/**
	 * Bottom left label who display the Td directory path and the part of the td
	 * which are analyzed
	 */
	@FXML
	private Label leftStatus;
	/**
	 * Bottom right label who display the total td reviewed and the number of td
	 * selected after a request
	 */
	@FXML
	private Label rightStatus;
	/**
	 * Deque who store recent td path open
	 */
	private Deque<MenuItem> dequeRecentOpen = new ArrayDeque<MenuItem>();
	/**
	 * menu for open recent td directory
	 */
	@FXML
	private Menu openRecent;

	/**
	 * Instantiate the starter ontology (Ontology/WotPriv.owl) and the TdModel
	 * (IoT-Devices-Benchmark_ANNOTE) with the part "privacyPolicy" to analyze
	 * Initialize rightStatus, tree and resultView. set the ActionListener of
	 * treeview and resultview when a user click on a cell
	 */
	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			ontology = new OntologyDAO("Ontology/WotPriv.owl");
			algoSearch = new TdModel("IoT-Devices-Benchmark_ANNOTE", "privacyPolicy");
			dequeRecentOpen.addFirst(new MenuItem(algoSearch.getDir().getPath()));
			openRecent.getItems().setAll(dequeRecentOpen);
			setLeftStatus();
			rightStatus.setText(algoSearch.getJsonObjectList().size() + " total");
			rightStatus.setTextFill(Color.web("#008080"));
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

	/**
	 * if the root name is not equal to the name of the ontology, the method
	 * treeSubItem will load the ontology or a part with a classes name at a root ,
	 * else the method treeSubItemsRecAutoRoot will load automatically all the
	 * ontology
	 * 
	 * @param root    Name of the ontology we want to put in the treeview,
	 * @param Hashmap of the ontology (classes name as key and super or sub classes
	 *                as value
	 * @throws OWLException
	 */
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

	/**
	 * Recursive method who select all the childrens of a treeitem
	 * 
	 * @param treeitem courant
	 */
	public void selectAllSubTreeItemsRec(TreeItem<String> courant) {
		if (courant.getChildren().isEmpty()) {
			tree.getSelectionModel().select(courant);
		} else {
			for (TreeItem<String> e : courant.getChildren()) {
				selectAllSubTreeItemsRec(e);
			}
		}

	}

	/**
	 * Instantiate the TreeItem courant with the ontology's hashmap recursively if
	 * the hashmap contain as key the value of the courant, all the childrens store
	 * in the hashmap will be put in the treeview
	 * 
	 * @param tree    item courant to instantiate
	 * @param hashmap map of the ontology
	 * @return tree item loaded
	 */
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

	/**
	 * use the method treeSubItemsRec for instantiate automatically hashmap of the
	 * ontology in a treeitem
	 * 
	 * @param tree    item courant to instantiate
	 * @param hashmap map of the ontology
	 * @return tree item loaded
	 */
	private TreeItem<String> treeSubItemsRecAutoRoot(TreeItem<String> courant,
			LinkedHashMap<String, ArrayList<String>> map) {
		for (String s : map.keySet()) {
			if (!containInTree(courant, s))
				courant.getChildren().add(treeSubItemsRec(new TreeItem<String>(s), map));
		}
		return courant;

	}

	/**
	 * Check recursively if a value is in a treeItem and their children
	 * 
	 * @param courant
	 * @param value   t
	 * @return true if the value is in the treeIteam false else
	 */
	private boolean containInTree(TreeItem<String> courant, String value) {
		for (TreeItem<String> e : courant.getChildren()) {
			if (containInTree(e, value)) {
				return true;
			}
		}
		return courant.getValue().equals(value);
	}

	/**
	 * set mouse clicked in Tree cells, when a cell is clicked, all their children
	 * is selected, if a cell don't have children, it will be selected. All the td
	 * which contain the concepts of the cell cliked/selected (cell String value)
	 * display in the middle frame with displayResultSearch()
	 */
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
					Resultlist.clear();
					rightStatus.setText(algoSearch.getJsonObjectList().size() + " total");
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

	/**
	 * set the Resultlist with the TDs which contain the concept selected by the
	 * user on the treeview rightStatus will be set with the number of TDs displayed
	 */
	@FXML
	public void displayResultSearch() {
		if (!tree.getSelectionModel().isEmpty()) {
			ArrayList<String> SelectedConcepts = new ArrayList<>();
			tree.getSelectionModel().getSelectedItems().forEach(item -> SelectedConcepts.add(item.getValue()));
			resultMap = algoSearch.schearTD(SelectedConcepts);
			Resultlist.setAll(resultMap.keySet());
			rightStatus.setText(resultMap.size() + " | " + algoSearch.getJsonObjectList().size() + " total");
		}

	}

	/**
	 * set a listener in resultView, if a cell is selected the content of the td
	 * will be displayed in the right frame
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

	/**
	 * method who permit to choose a TDs directory the Open Td directory menu item
	 * is clicked
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@FXML
	public void clikedOpenTdsDirectoryMenuItem() throws IOException, ParseException {
		DirectoryChooser dc = new DirectoryChooser();
		File file = dc.showDialog(null);
		if (file != null) {
			setJsonDirectory(file);
		}
	}

	/**
	 * when the button "load new ontology" is clicked, loadOntologyView.fxml will be
	 * charged and managed by CtrlLoadOntology, with this frame, the user select the
	 * owl file of the ontology, if the ontology will be auto loaded or if the user
	 * select a particular root when the frame will be closed the method will get
	 * the value with static getter. if the ontology selected is not nul, and the
	 * value of a root is not "choose root" and if the auto button is checked,
	 * ontology will be loaded with the name of ontology as first root with
	 * setTreeView, so with treeSubItemsRecAutoRoot. else with the name of a root,
	 * setTreeView will be exxecuted with the root selected in parameter. if the
	 * button inverted is clicked, there will be ontology.getSuperClassesHashMap()
	 * as second parameter of setTreeView, else ontology.getSubCassesHashMap()
	 * 
	 * @throws IOException
	 */
	@FXML
	public void loadOntologyMenuItem() throws IOException {
		if (CtrlLoadOntology.getStage() == null || !CtrlLoadOntology.getStage().isShowing()) {
			try {
				CtrlLoadOntology.showInterfaceLoad();
				if (CtrlLoadOntology.getOntology() != null
						&& !CtrlLoadOntology.getValueRootListBox().equals("choose root")) {
					CtrlMainView.setOntology(CtrlLoadOntology.getOntology());
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
							setTreeView(CtrlLoadOntology.getValueRootListBox(), ontology.getSuperClassesHashMap());
						} else {
							setTreeView(CtrlLoadOntology.getValueRootListBox(), ontology.getSubClassesHashMap());
						}

					}
					Resultlist.clear();
				}
			} catch (OWLException e) {
				displayOwlError();
			} catch (NullPointerException e) {
			}
		} else {
			CtrlLoadOntology.getStage().toFront();
		}
	}

	/**
	 * 
	 */
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

	/**
	 * method who define the things description directory and actualize bottom right
	 * label. Actualize the open recent menu.
	 * 
	 * @param file which represent the td directory
	 * @throws IOException
	 * @throws ParseException
	 */
	private void setJsonDirectory(File file) throws IOException, ParseException {
		algoSearch.setDir(file);
		setLeftStatus();
		rightStatus.setText(resultMap.size() + " | " + algoSearch.getJsonObjectList().size() + " total");
		if (!tree.getSelectionModel().isEmpty()) {
			displayResultSearch();
		}
		setOpenRecentFileMenu(file);
	}

	/**
	 * add a file path in the dequeRecentOpen like a File, if a path is cliked, it
	 * will be add in top of the file.
	 * 
	 * @param file to add in the open recent Menu
	 */
	private void setOpenRecentFileMenu(File file) {
		MenuItem newMenuItem = getMenuItem(file.getPath());
		if (!containMenuItem(file.getPath())) {
			dequeRecentOpen.addFirst(new MenuItem(file.getPath()));
		} else {
			dequeRecentOpen.addFirst(newMenuItem);
			dequeRecentOpen.removeLastOccurrence(newMenuItem);
		}
		openRecent.getItems().setAll(dequeRecentOpen);
	}

	/**
	 * check if a string if it contained in the dequeRecentOpen (as a value of one
	 * menu item store in this deck)
	 * 
	 * @param nameMenuItem string we check
	 * @return true if nameMenuItem is contained false else
	 */
	private boolean containMenuItem(String nameMenuItem) {
		for (MenuItem m : dequeRecentOpen) {
			if (m.getText().equals(nameMenuItem)) {
				return true;
			}
		}
		return false;

	}

	/**
	 * @param nameMenuItem string
	 * @return the menu item stored in dequeRecentOpen which have the value of the
	 *         string in parameter, return null value else
	 */
	private MenuItem getMenuItem(String nameMenuItem) {
		for (MenuItem m : dequeRecentOpen) {
			if (m.getText().equals(nameMenuItem)) {
				return m;
			}
		}
		return null;

	}

	/**
	 * an alert frame which are displayed if a OWL Exception is catch
	 */
	private void displayOwlError() {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("OWL Exception");
		alert.setHeaderText("ERROR");
		alert.setContentText("CANT'T LOAD OWL FILE");
		alert.showAndWait();
	}

	/**
	 * when preference menu is selected, this method is launched.
	 * CtrlPreferenceView.showPreferenceView() will display preferenceView.fxml
	 * controlled by CtrlPreferenceView, this frame is used for change the td Part
	 * to analyse. when a td name part is wrote and the frame closed,
	 * algoSearch.setTdPartToAnalyse is used with the value wrote as parameter.
	 * 
	 * @throws IOException
	 * @throws ParseException
	 */
	@FXML
	public void preferencesAction() throws IOException, ParseException {
		if (CtrlPreferenceView.getStage() == null || !CtrlPreferenceView.getStage().isShowing()) {
			CtrlPreferenceView.showPreferenceView();
			if (CtrlPreferenceView.getValuePartTdNameLabel() != null) {
				algoSearch.setTdPartToAnalyse(CtrlPreferenceView.getValuePartTdNameLabel());
				setLeftStatus();
				displayResultSearch();
			}
		} else {
			CtrlPreferenceView.getStage().toFront();
		}
	}

	/**
	 * when a Td in ResultView is selected and save As menu is clicked, the user can
	 * download the json file.
	 * 
	 * @throws FileNotFoundException
	 */
	@FXML
	public void saveAsAction() throws FileNotFoundException {
		if (!resultView.getSelectionModel().isEmpty()) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialFileName(resultView.getSelectionModel().getSelectedItem() + ".json");
			fileChooser.getExtensionFilters().add(new ExtensionFilter("JSON", "json"));
			File f = fileChooser.showSaveDialog(null);
			PrintWriter writer = new PrintWriter(f);
			writer.write(resultMap.get(resultView.getSelectionModel().getSelectedItem()));
			writer.close();
		}

	}

	/**
	 * actualise the LeftLabel
	 */
	private void setLeftStatus() {
		leftStatus.setText(algoSearch.getDir().getPath() + " | (" + algoSearch.getTdPartToAnalyse() + ")");
	}

	/**
	 * close the application when menu item close is clicked
	 */
	@FXML
	public void quitMenuItem() {
		CtrlStage.close();
	}

	/**
	 * select all ontology tree
	 */
	@FXML
	public void selectAllAction() {
		tree.getSelectionModel().selectAll();
		displayResultSearch();
	}

	/**
	 * unselect all ontology tree
	 */
	@FXML
	public void unSelectAllAction() {
		tree.getSelectionModel().clearSelection();
		displayResultSearch();

	}

	/**
	 * @return OntologyDao loaded
	 */
	public static OntologyDAO getOntology() {
		return ontology;
	}

	/**
	 * set the ontology to load and change the App title with the ontology Path
	 * 
	 * @param ontology to set
	 */
	public static void setOntology(OntologyDAO ontology) {
		CtrlMainView.ontology = ontology;
		CtrlStage.setTitle(App.getTitle() + " - " + CtrlMainView.getOntology().getPath());
	}

	/**
	 * @return current Stage
	 */
	public static Stage getCtrlStage() {
		return CtrlStage;
	}

	/**
	 * @param ctrlStage
	 */
	public static void setCtrlStage(Stage ctrlStage) {
		CtrlStage = ctrlStage;
	}

	/**
	 * @return current TdModel used
	 */
	public static TdModel getAlgoSearch() {
		return algoSearch;
	}

}