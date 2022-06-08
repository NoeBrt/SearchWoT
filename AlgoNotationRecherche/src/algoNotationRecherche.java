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

public class algoNotationRecherche {
	public static void main(String[] args) {
		HashMap<String, String> hm = new HashMap<>();
		hm.put("legal", "mid");
		hm.put("NeedToKnow", "hight");
		System.out.println(schearTD(hm,"C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark\\anotation_exemple"));
		
	}

	private static ArrayList<String> schearTD(HashMap<String, String> Hm, String path) {
		JSONParser jsonParser = new JSONParser();
		File dir = new File(path);
		File[] liste = dir.listFiles();
		ArrayList<String> s= new ArrayList<>();
		for (File item : liste) {
			if (item.isFile()) {
				try (FileReader reader = new FileReader(item)) {
					// Read JSON file
					Object obj = jsonParser.parse(reader);
					JSONObject ThingDescription = (JSONObject) obj;
					if (containConcept(ThingDescription,Hm)) {
						s.add(item.getName());
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

		return s;
	}

	private static boolean containConcept(JSONObject ThingDescription, HashMap<String, String> hm) {
		Object PrivacyPolicy = ThingDescription.get("privacyPolicy");
		JSONArray PrivacyPolicy2 = ((JSONArray) PrivacyPolicy);
		boolean b = false;
		for (String concept : hm.keySet()) {
			for (Object e : PrivacyPolicy2) {
				if (((JSONObject) e).get("concept").toString().contains(concept)) {
					if (((JSONObject) e).get("levelPrivacy").toString().equals(hm.get(concept))) {
						b = true;

					} else {
						return false;
					}
				}
			}
		}
		return b;
	}

}
