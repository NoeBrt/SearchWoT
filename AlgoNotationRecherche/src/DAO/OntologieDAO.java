package DAO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.ConsoleProgressMonitor;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

import javafx.scene.control.TreeItem;
import model.Tree;
import model.Tree.Node;
import simple.JSONArray;
import simple.JSONObject;

public class OntologieDAO {
	private OWLOntology ontology;
	private String path;
	private String name;

	public OntologieDAO(String path) throws OWLException {
		File f = new File(path);
		this.path=f.getAbsolutePath();
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create(f));
		this.name="ontology : "+IRI.create((new File(path))).getShortForm();

	}

	public ArrayList<String> getClassesName() {
		Collection<OWLClass> classes = ontology.getClassesInSignature();
		ArrayList<String> listofClass = new ArrayList<String>();
		for (OWLClass owlClass : classes) {
			listofClass.add(owlClass.getIRI().getShortForm());
		}
		return listofClass;
	}

	public ArrayList<OWLClass> getClassesOrderBySuperClasse() {
		ArrayList<OWLClass> classes = new ArrayList<>(ontology.getClassesInSignature());
		classes.sort(numberSuperClassComparator);
		return classes;
	}

	public ArrayList<OWLClass> getClassesOrderBySubClasse() {
		ArrayList<OWLClass> classes = new ArrayList<>(ontology.getClassesInSignature());
		classes.sort(numberSubClassComparator);
		return classes;
	}

	private Comparator<OWLClass> numberSuperClassComparator = new Comparator<OWLClass>() {
		public int compare(OWLClass a, OWLClass b) {
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
			NodeSet<OWLClass> subClses = reasonerFactory3.getSuperClasses(a, false);
			NodeSet<OWLClass> subClses1 = reasonerFactory3.getSuperClasses(b, false);
			return subClses1.getFlattened().size() - subClses.getFlattened().size();
			// size() is always nonnegative, so this won't have crazy overflow bugs
		}
	};
	private Comparator<OWLClass> numberSubClassComparator = new Comparator<OWLClass>() {
		public int compare(OWLClass a, OWLClass b) {
			OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
			OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
			NodeSet<OWLClass> subClses = reasonerFactory3.getSubClasses(a, false);
			NodeSet<OWLClass> subClses1 = reasonerFactory3.getSubClasses(b, false);
			return subClses1.getFlattened().size() - subClses.getFlattened().size();
			// size() is always nonnegative, so this won't have crazy overflow bugs
		}
	};

	public LinkedHashMap<String, ArrayList<String>> getSuperClassesHashMap() {
		LinkedHashMap<String, ArrayList<String>> hm = new LinkedHashMap<String, ArrayList<String>>();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
		for (OWLClass ontElement : getClassesOrderBySuperClasse()) {
			NodeSet<OWLClass> subClses = reasonerFactory3.getSuperClasses(ontElement, true);
			Set<OWLClass> clses = subClses.getFlattened();
			for (OWLClass cls1 : clses) {
				if (!cls1.getIRI().getShortForm().equals("Thing")) {
					if (!hm.containsKey(ontElement.getIRI().getShortForm())) {
						hm.put(ontElement.getIRI().getShortForm(), new ArrayList<String>());
					}
					hm.get(ontElement.getIRI().getShortForm()).add(cls1.getIRI().getShortForm());
				}
				System.out.println();
			}
		}
		// Tree<String> res = new Tree<String>("");
		return hm;
	}

	public LinkedHashMap<String, ArrayList<String>> getSubClassesHashMap() {
		LinkedHashMap<String, ArrayList<String>> hm = new LinkedHashMap<String, ArrayList<String>>();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
		for (OWLClass ontElement : getClassesOrderBySubClasse()) {
			NodeSet<OWLClass> subClses = reasonerFactory3.getSubClasses(ontElement, true);
			Set<OWLClass> clses = subClses.getFlattened();
			for (OWLClass cls1 : clses) {
				if (!cls1.getIRI().getShortForm().equals("Nothing")) {
					if (!hm.containsKey(ontElement.getIRI().getShortForm())) {
						hm.put(ontElement.getIRI().getShortForm(), new ArrayList<String>());
					}
					hm.get(ontElement.getIRI().getShortForm()).add(cls1.getIRI().getShortForm());
				}
				System.out.println();
			}
		}
		return hm;
	}

	public Node<String> recur() {
		return null;

	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	public void setOntologyByPath(String path) throws OWLOntologyCreationException {
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create((new File(path))));
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
