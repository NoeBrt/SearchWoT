package model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import parser.JSONParser;
import parser.ParseException;
import simple.JSONObject;

public class test {
	public static void main(String[] args) throws IOException, ParseException {

		for (File o :listFileRecur(new File("C:\\Users\\noebr\\Documents\\IoT-Devices-Benchmark_ANNOTE"),new ArrayList<File>(),"json")) {
			FileReader reader = new FileReader(o);
			JSONObject ThingDescription = (JSONObject) (new JSONParser()).parse(reader);
			//FileWriter file = new FileWriter(o, true);
			PrintWriter writer = new PrintWriter(o);
			writer.print(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replaceAll("PrivacyPolicy", "privacyPolicy"));
			writer.close();
		//	file.flush();
			//file.close();
		}
		// FileWriter file = new FileWriter("c:\\B\\mycontrol.json", true);

	}
	 private static ArrayList<File> listFileRecur(File rep, ArrayList<File> f,
			 String extentionName) { if (rep.isFile() && !rep.isHidden() &&
			 rep.getName().endsWith(extentionName)) { f.add(rep); return f; } else if
			  (rep.isDirectory() && !rep.isHidden()) { for (File d : rep.listFiles())
			  listFileRecur(d, f, extentionName); } return f; }
	 public static ArrayList<JSONObject> listJsonObjectRecur(File rep, ArrayList<JSONObject> f, String extentionName)
				throws IOException {
			if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
				FileReader reader = new FileReader(rep);
				try {
				JSONObject ThingDescription = (JSONObject) (new JSONParser()).parse(reader);
				f.add(ThingDescription);
				}catch(ParseException pe){
					System.out.println(rep.getName());
				}
				return f;
			} else if (rep.isDirectory() && !rep.isHidden()) {
				for (File d : rep.listFiles())
					listJsonObjectRecur(d, f, extentionName);
			}
			return f;
		}
}
