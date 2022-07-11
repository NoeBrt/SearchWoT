package DAO;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

import java.util.LinkedHashMap;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.structural.StructuralReasonerFactory;

public class OntologyDAO {
	/**
	 * Object which stores the ontology
	 */
	private OWLOntology ontology;
	/**
	 * Ontology's path
	 */
	private String path;
	/**
	 * Ontology name
	 */
	private String name;

	/**
	 * Constructor of OntologyDAO, instantiate the OWLOntology with the path of the
	 * OWL file
	 * 
	 * @param path
	 * @throws OWLException
	 * @throws IOException
	 */
	public OntologyDAO(String path) throws OWLException, IOException {
		File f = new File(path);
		this.path = f.getPath();
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create(f));
		this.name = "ontology : " + IRI.create((new File(path))).getShortForm();

	}

	/**
	 * @return an ArrayList of all the ontology's classe Name with the method
	 *         getIRI().getShortForm()
	 */
	public ArrayList<String> getClassesName() {
		Collection<OWLClass> classes = ontology.getClassesInSignature();
		ArrayList<String> listofClass = new ArrayList<String>();
		for (OWLClass owlClass : classes) {
			listofClass.add(owlClass.getIRI().getShortForm());
		}
		return listofClass;
	}

	/**
	 * @return an ArrayList of all the ontology's classe Name with the method
	 *         getIRI().getShortForm() order by the number of their superCLasse The
	 *         first element is the class (OWLClass) which have the most
	 *         superClasses sort by comparator numberSuperClassComparator .
	 */
	public ArrayList<OWLClass> getClassesOrderBySuperClasse() {
		ArrayList<OWLClass> classes = new ArrayList<>(ontology.getClassesInSignature());
		classes.sort(numberSuperClassComparator);
		return classes;
	}

	/**
	 * @returnan ArrayList of all the ontology's classe Name with the method
	 *           getIRI().getShortForm() order by the number of their superCLasse
	 *           The first element is the class (OWLClass) which have the most
	 *           subClasses comparator numberSubClassComparator.
	 */
	public ArrayList<OWLClass> getClassesOrderBySubClasse() {
		ArrayList<OWLClass> classes = new ArrayList<>(ontology.getClassesInSignature());
		classes.sort(numberSubClassComparator);
		return classes;
	}

	/**
	 * Comparator who compare OWLClasses by their number of Super classes.
	 * (descending order)
	 */
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

	/**
	 * Comparator who compare OWLClasses by their number of Sub classes. (descending
	 * order)
	 */
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

	/**
	 * @return an LinkedHashMap of classes name in key, and the value is all their
	 *         direct Super Classes (Arraylist) order by their number of
	 *         superClasses first with OWLReasoner, for each owlClass the reasoner
	 *         extract all their direct subClasses and they superclasses are put in
	 *         a set(subClassesSet) and the hasmap hm put all OWLclass ontElement in
	 *         key and this set as value. Class "Thing" will not be put.
	 */
	public LinkedHashMap<String, ArrayList<String>> getSuperClassesHashMap() {
		LinkedHashMap<String, ArrayList<String>> hashOntology = new LinkedHashMap<String, ArrayList<String>>();
		OWLReasoner reasoner = (new StructuralReasonerFactory()).createReasoner(ontology);
		for (OWLClass ontClass : getClassesOrderBySuperClasse()) {
			NodeSet<OWLClass> subClassesNodeSet = reasoner.getSuperClasses(ontClass, true);
			Set<OWLClass> subClassesSet = subClassesNodeSet.getFlattened();
			for (OWLClass subClasse : subClassesSet) {
				if (!subClasse.getIRI().getShortForm().equals("Thing")) {
					if (!hashOntology.containsKey(ontClass.getIRI().getShortForm())) {
						hashOntology.put(ontClass.getIRI().getShortForm(), new ArrayList<String>());
					}
					hashOntology.get(ontClass.getIRI().getShortForm()).add(subClasse.getIRI().getShortForm());
				}

			}
		}
		return hashOntology;
	}

	/**
	 * @return an LinkedHashMap of classes name in key, and the value is all their
	 *         direct Super Classes (Arraylist) order by their number of subClasses.
	 *         first with OWLReasoner, for each owlClass the reasoner extract all
	 *         their direct subClasses and they subclasses are put in a set(clses)
	 *         and the hasmap hashOntology put all OWLclass ontClass in key and this
	 *         set as value. Class "Nothing" will not be put.
	 * 
	 */
	public LinkedHashMap<String, ArrayList<String>> getSubClassesHashMap() {
		LinkedHashMap<String, ArrayList<String>> hashOntology = new LinkedHashMap<String, ArrayList<String>>();
		OWLReasoner reasoner = (new StructuralReasonerFactory()).createReasoner(ontology);
		for (OWLClass ontClass : getClassesOrderBySubClasse()) {
			NodeSet<OWLClass> subClassesNodeSet = reasoner.getSubClasses(ontClass, true);
			Set<OWLClass> subClassesSet = subClassesNodeSet.getFlattened();
			for (OWLClass subClasse : subClassesSet) {
				if (!subClasse.getIRI().getShortForm().equals("Nothing")) {
					if (!hashOntology.containsKey(ontClass.getIRI().getShortForm())) {
						hashOntology.put(ontClass.getIRI().getShortForm(), new ArrayList<String>());
					}
					hashOntology.get(ontClass.getIRI().getShortForm()).add(subClasse.getIRI().getShortForm());
				}

			}
		}
		return hashOntology;
	}

	/**
	 * @return the ontology (OWLOntology) loaded
	 */
	public OWLOntology getOntology() {
		return ontology;
	}

	/**
	 * @param ontology that will be loaded
	 */
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}

	/**
	 * @param path of the Ontology OWL file
	 * @throws OWLOntologyCreationException
	 */
	public void setOntologyByPath(String path) throws OWLOntologyCreationException {
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create((new File(path))));
	}

	/**
	 * @return onotlogy's Path
	 */
	public String getPath() {
		return path;
	}

	/**
	 * @return Name of the ontology
	 */
	public String getName() {
		return name;
	}

}
