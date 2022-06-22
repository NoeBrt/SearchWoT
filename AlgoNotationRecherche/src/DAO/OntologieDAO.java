package DAO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

public class OntologieDAO {
	private OWLOntology ontology;
	String path;

	public OntologieDAO(String path) throws OWLException {
		this.path = path;
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create((new File(path))));

	}

	public ArrayList<String> getClassesName() {
		Collection<OWLClass> classes = ontology.getClassesInSignature();
		ArrayList<String> listofClass = new ArrayList<String>();
		for (OWLClass owlClass : classes) {
			listofClass.add(owlClass.getIRI().getShortForm());
		}
		return listofClass;
	}

	public HashMap<String, ArrayList<String>> getClassesHashMap() {
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();
		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
		OWLDataFactory fac3 = ontology.getOWLOntologyManager().getOWLDataFactory();
		for (String s : getClassesName()) {
			IRI docIRI = IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + s);
			OWLClass pizza = fac3.getOWLClass(docIRI);
			NodeSet<OWLClass> subClses = reasonerFactory3.getSuperClasses(pizza, true);
			Set<OWLClass> clses = subClses.getFlattened();
			System.out.println("Subclasses of " + s + "  : ");
			// instanciate the hashmap
			for (OWLClass cls1 : clses) {
				// cls1.getIRI().getShortForm() is the name of an ontology classes in String
				// all the classe is a key, and their superclasses is the value
				if (!cls1.getIRI().getShortForm().equals("Thing")) {
					if (!hm.containsKey(s)) {
						hm.put(s, new ArrayList<String>());
					}
					hm.get(s).add(cls1.getIRI().getShortForm());
					System.out.println(" " + cls1.getIRI().getShortForm());
				}
				System.out.println();
			}
		}
		// Tree<String> res = new Tree<String>("");

		return hm;
	}

	public MyType<String> getClassesTreeInverted(String root) {
		HashMap<String, ArrayList<String>> hm = new HashMap<String, ArrayList<String>>();

		OWLReasonerFactory reasonerFactory = new StructuralReasonerFactory();
		// OWLReasoner reasoner = reasonerFactory.createReasoner(ontology, config);
		// reasoner.precomputeInferences();
		OWLReasoner reasonerFactory3 = reasonerFactory.createReasoner(ontology);
		OWLDataFactory fac3 = ontology.getOWLOntologyManager().getOWLDataFactory();

		IRI docIRI = IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" + root);
		OWLClass pizza = fac3.getOWLClass(docIRI);
		NodeSet<OWLClass> RootSub = reasonerFactory3.getSuperClasses(pizza, true);

		for (org.semanticweb.owlapi.reasoner.Node<OWLClass> s : RootSub.getNodes()) {
			// docIRI = IRI.create(ontology.getOntologyID().getOntologyIRI().get() + "#" +
			// s);
			// pizza = fac3.getOWLClass(docIRI);
			// NodeSet<OWLClass> subClses = reasonerFactory3.getSuperClasses(pizza, true);
			// Set<OWLClass> clses = subClses.getFlattened();
			// System.out.println("Subclasses of " + s + " : ");

			// System.out.println(subClses);
		}
		// Tree<String> res = new Tree<String>("");

		return new MyType<String>("test");
	}

	private TreeItem<MyType<String>> buildSubtree(MyType<String> root) {
		TreeItem<MyType<String>> result = new TreeItem<>(root);

		if (root.getChildren() != null) {
			for (MyType<String> child : root.getChildren()) {
				result.getChildren().add(buildSubtree(child));
			}
		}

		return result;
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

}
