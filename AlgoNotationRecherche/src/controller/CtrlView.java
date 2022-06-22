package controller;

import java.net.URL;
import java.nio.file.spi.FileSystemProvider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.function.Consumer;

import org.semanticweb.owlapi.model.OWLException;

import DAO.OntologieDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
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
	static ObservableList<String> Resultlist;
	@FXML
	private ListView<String> resultView = new ListView<>(Resultlist);
	@FXML
	private TextFlow tdDetail;

	private HashMap<String, String> resultMap;

	@FXML
	TreeView<String> tree = new TreeView<String>();

	public void initialize(URL arg0, ResourceBundle arg1) {
		try {
			initTreeView();
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initTreeView() throws OWLException {
		// TreeItem<String> root = new TreeItem<String>("Private_Policy");
		tree.setFixedCellSize(25);
		// Privacy Policy is the Ontology classe I Use
		TreeItem<String> root = new TreeItem<String>("PrivacyPolicy");
		root.setExpanded(true);
		OntologieDAO ont = new OntologieDAO("WotPriv.owl");
		// hashmap of the ontology
		HashMap<String, ArrayList<String>> hm = ont.getClassesHashMap();
		TreeItem<String> children = new TreeItem<String>("");
		System.out.println(hm);
		int i = 0;
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
		tree.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
	}


	@FXML
	public void resultSchearch() {
		if (!tree.getSelectionModel().isEmpty()) {
			algoNotationRecherche a = new algoNotationRecherche(
					"C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple");
			ArrayList<String> SelectedConcepts = new ArrayList<>();
			tree.getSelectionModel().getSelectedItems().forEach(item -> SelectedConcepts.add(item.getValue()));
			resultMap = a.schearTD(SelectedConcepts);
			Resultlist = FXCollections.observableArrayList(resultMap.keySet());
			resultView.setFixedCellSize(25);
			resultView.setItems(Resultlist);
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

}