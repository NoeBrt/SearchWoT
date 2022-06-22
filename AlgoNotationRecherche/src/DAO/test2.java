package DAO;

import java.io.File;
import java.util.ArrayList;

import org.semanticweb.owlapi.model.OWLException;

public class test2 {

	public static void main(String[] args) throws OWLException {
		// TODO Auto-generated method stub
		OntologieDAO t = new OntologieDAO("WotPriv.owl");
			//System.out.println(t.getClassesName());
		//	t.getClassesHashMap();
			//t.getClassesTreeInverted("PrivacyPolicy");
		File f=new File("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
}


}