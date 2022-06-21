package DAO;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologieDAO {
	private OWLOntology ontology;

	OntologieDAO(String path) throws OWLException {
		// OWLOntologyDocumentSource source = new
		// StreamDocumentSource(getClass().getResourceAsStream("WotPriv.owl"));
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		this.ontology = manager.loadOntologyFromOntologyDocument(IRI.create((new File(path))));
		// Collection<OWLClass> classes = ontology.getClassesInSignature();
		/*
		 * ArrayList<String> listofClass = new ArrayList<String>(); //getting class
		 * names in the ontology for (OWLClass owlClass : classes) {
		 * System.out.println(owlClass.getIRI().getShortForm());
		 * listofClass.add(owlClass.getIRI().getShortForm()); } return ontology;
		 */
		// Set<OWLClass> classes = pressInnovOntology.getClassesInSignature();
//System.out.println(classes);
	}

	public ArrayList<String> getClassesName() {
		Collection<OWLClass> classes = ontology.getClassesInSignature();
		ArrayList<String> listofClass = new ArrayList<String>();
		// getting class names in the ontology
		for (OWLClass owlClass : classes) {
			listofClass.add(owlClass.getIRI().getShortForm());
		}
		return listofClass;
		// Set<OWLClass> classes = pressInnovOntology.getClassesInSignature();

	}

}
