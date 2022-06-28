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
		algoNotationRecherche a = new algoNotationRecherche("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");

		for (File o : a.getJsonFileList()) {
			FileReader reader = new FileReader(o);
			JSONObject ThingDescription = (JSONObject) (new JSONParser()).parse(reader);
			//FileWriter file = new FileWriter(o, true);
			PrintWriter writer = new PrintWriter(o);
			//System.out.println(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replace("saref", "pp"));
			writer.print(algoNotationRecherche.formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4).replace("saref", "privp"));
			writer.close();
		//	file.flush();
			//file.close();
		}
		// FileWriter file = new FileWriter("c:\\B\\mycontrol.json", true);

	}
}