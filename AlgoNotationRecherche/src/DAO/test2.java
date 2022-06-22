package DAO;

import org.semanticweb.owlapi.model.OWLException;

public class test2 {

	public static void main(String[] args) throws OWLException {
		// TODO Auto-generated method stub
		OntologieDAO t = new OntologieDAO("WotPriv.owl");
			//System.out.println(t.getClassesName());
		//	t.getClassesHashMap();
			t.getClassesTreeInverted("PrivacyPolicy");
	}

}
