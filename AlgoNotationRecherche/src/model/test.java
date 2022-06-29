package model;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import parser.JSONParser;
import parser.ParseException;
import simple.JSONObject;

public class test {
	public static void main(String[] args) throws IOException, ParseException {

		for (File o :listFileRecur(new File("C:\\Users\\noebr\\Desktop\\anotation_exemple - Copie"),new ArrayList<File>(),"json")) {
			FileReader reader = new FileReader(o);
			//FileWriter file = new FileWriter(o, true);
			PrintWriter writer = new PrintWriter(o);
			//System.out.println(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replace("saref", "pp"));
			writer.print(reader.toString().replaceAll("\"levelPrivacy\": \"hight\"", ""));
			writer.print(reader.toString().replaceAll("\"levelPrivacy\": \"low\"", ""));
			writer.print(reader.toString().replaceAll("\"levelPrivacy\": \"mid\"", ""));
		//	writer.print(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replaceAll("\"levelPrivacy\": \"low\"", ""));
			//writer.print(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replaceAll("\"levelPrivacy\": \"mid\"", ""));

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
}