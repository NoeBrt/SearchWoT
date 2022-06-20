package model;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.OWLOntologyDocumentSource;
import org.semanticweb.owlapi.io.StreamDocumentSource;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLImportsDeclaration;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.SimpleIRIMapper;

public class testalgo {

	void createOntology() throws OWLException {
	//	OWLOntologyDocumentSource source = new StreamDocumentSource(getClass().getResourceAsStream("WotPriv.owl"));
		final OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
		final OWLOntology ontology = manager.loadOntologyFromOntologyDocument(IRI.create((new File("C:\\Users\\noebr\\OneDrive - Université de Tours\\stage\\WSstage\\search-engine-wot\\AlgoNotationRecherche\\src\\model\\WotPriv.owl")).toURI()));
		//om.getIRIMappers().add(new SimpleIRIMapper(IRI.create("http://www.semanticweb.org/noebr/ontologies/2022/5/WoT"),IRI.create("file:/AlgoNotationRecherche/src/model/WotPriv.owl")));
		System.out.println(ontology.getAxioms());
		ArrayList<Object> test1 = new ArrayList<Object>();
		test1.addAll(ontology.getAxioms());
		test1.get(0);
		
		//Set<OWLClass> classes = pressInnovOntology.getClassesInSignature();
//System.out.println(classes);

	}

}
