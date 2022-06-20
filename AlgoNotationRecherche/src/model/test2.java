package model;

import org.semanticweb.owlapi.model.OWLException;

public class test2 {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		testalgo t = new testalgo();
		try {
			t.createOntology();
		} catch (OWLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
