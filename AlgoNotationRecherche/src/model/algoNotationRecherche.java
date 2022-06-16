package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import parser.JSONParser;
import parser.ParseException;
import simple.JSONArray;
import simple.JSONObject;

/**
 * @author Noe Breton
 *
 */
public class algoNotationRecherche {

	/**
	 * 	path of the TDs folder
	 */
	private static String path;
	// "C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple"
	
	public algoNotationRecherche(String path) {
		algoNotationRecherche.path = path;
	}

	public static void main(String[] args) {
		HashMap<String, String> hm = new HashMap<>();
		hm.put("legal", "mid");
		hm.put("NeedToKnow", "high");
		System.out.println(schearTD(hm));

	}

	/**
	 * find TDs whose matches with level of privacy of one or multiple concept in an
	 * HashMap
	 * 
	 * @param concepts : HasMap of concepts and their level of privacy, key: concept
	 *                 name / value: level privacy : hight/mid/low
	 * @return an ArrayList of String of name of TD whose Level of Privacy in
	 *         required Concept match with the HashMap
	 */
	private static ArrayList<String> schearTD(HashMap<String, String> concepts) {
		JSONParser jsonParser = new JSONParser();
		File dir = new File(path);
		File[] liste = dir.listFiles();
		ArrayList<String> resultTD = new ArrayList<>();
		for (File item : liste) {
			if (item.isFile()) {
				try (FileReader reader = new FileReader(item)) {
					JSONObject ThingDescription = (JSONObject) jsonParser.parse(reader);
					if (containConcept(ThingDescription, concepts)) {
						resultTD.add(item.getName());
					}

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}

			}
		}

		return resultTD;
	}

	/**
	 * @param ThingDescription : JSON object of the TD
	 * @param concepts         : HasMap of concepts and their level of privacy, key:
	 *                         concept name / value: level privacy : hight/mid/low
	 * @return true if a concept is written in the TD, false else
	 */
	private static boolean containConcept(JSONObject ThingDescription, HashMap<String, String> concepts) {
		Object PrivacyPolicy = ThingDescription.get("privacyPolicy");
		JSONArray PrivacyPolicy2 = ((JSONArray) PrivacyPolicy);
		boolean b = false;
		for (String concept : concepts.keySet()) {
			for (Object e : PrivacyPolicy2) {
				if (((JSONObject) e).get("concept").toString().contains(concept)) {
					if (((JSONObject) e).get("levelPrivacy").toString().equals(concepts.get(concept))) {
						b = true;

					} else {
						return false;
					}
				}
			}
		}
		return b;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		algoNotationRecherche.path = path;
	}

}
