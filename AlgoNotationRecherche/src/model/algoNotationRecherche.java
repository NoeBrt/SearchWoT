package model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

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
	 * path of the TDs folder
	 */
	// "C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple"
	private File dir;
	private ArrayList<File> JsonFileList;
	private ArrayList<JSONObject> JsonObjectList;

	public ArrayList<JSONObject> getJsonObjectList() {
		return JsonObjectList;
	}

	public void setJsonObjectList(ArrayList<JSONObject> jsonObjectList) {
		JsonObjectList = jsonObjectList;
	}

	public algoNotationRecherche(String path) throws IOException, ParseException {
		this.dir = new File(path);
		JsonFileList = listFileRecur(dir, ".json");
		JsonObjectList = listJsonObjectRecur(dir, ".json");
		sortByConceptNumber(JsonObjectList);

	}

	public algoNotationRecherche(File dir) throws IOException, ParseException {
		this.dir = dir;
		// JsonFileList = listFileRecur(dir, ".json");
		JsonObjectList = listJsonObjectRecur(dir, ".json");
		sortByConceptNumber(JsonObjectList);

	}

	/**
	 * find TDs whose matches with level of privacy of one or multiple concept in an
	 * HashMap
	 * 
	 * @param concepts : HasMap of concepts and their level of privacy, key: concept
	 *                 name / value: level privacy : hight/mid/low
	 * @return
	 * @return an ArrayList of String of name of TD whose Level of Privacy in
	 *         required Concept match with the HashMap
	 */
	/*
	 * public HashMap<String, String> schearTD(HashMap<String, String> concepts) {
	 * JSONParser jsonParser = new JSONParser(); HashMap<String, String> resultTD =
	 * new HashMap<>(); for (File item : JsonFileList) { try (FileReader reader =
	 * new FileReader(item)) { JSONObject ThingDescription = (JSONObject)
	 * jsonParser.parse(reader); if (containConcept(ThingDescription, concepts)) {
	 * resultTD.put(ThingDescription.get("title").toString(),
	 * formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4)); }
	 * 
	 * } catch (FileNotFoundException e) { e.printStackTrace(); } catch (IOException
	 * e) { e.printStackTrace(); } catch (ParseException e) { e.printStackTrace(); }
	 * 
	 * } return resultTD; }
	 */

	public List<JSONObject> sortByConceptNumber(List<JSONObject> a) {
		a.sort(lengthComparator);
		return a;
	}

	private Comparator<JSONObject> lengthComparator = new Comparator<JSONObject>() {
		public int compare(JSONObject a, JSONObject b) {
			JSONArray PrivacyPolicy1 = ((JSONArray) a.get("privacyPolicy"));
			JSONArray PrivacyPolicy2 = ((JSONArray) b.get("privacyPolicy"));
			return PrivacyPolicy2.size() - PrivacyPolicy1.size();
			// size() is always nonnegative, so this won't have crazy overflow bugs
		}
	};

	private static ArrayList<JSONObject> listJsonObjectRecur(File rep, String extentionName)
			throws IOException, ParseException {
		return listJsonObjectRecur(rep, new ArrayList<JSONObject>(), extentionName);
	}

	public static ArrayList<JSONObject> listJsonObjectRecur(File rep, ArrayList<JSONObject> f, String extentionName)
			throws IOException, ParseException {
		if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
			FileReader reader = new FileReader(rep);
			JSONObject ThingDescription = (JSONObject) (new JSONParser()).parse(reader);
			f.add(ThingDescription);
			return f;
		} else if (rep.isDirectory() && !rep.isHidden()) {
			for (File d : rep.listFiles())
				listJsonObjectRecur(d, f, extentionName);
		}
		return f;
	}

	private static ArrayList<File> listFileRecur(File rep, String extentionName) {
		return listFileRecur(rep, new ArrayList<File>(), extentionName);
	}

	/*
	 * public LinkedList<File> shortList(ArrayList<File> List) throws IOException,
	 * ParseException { JSONParser jsonParser = new JSONParser(); for (File f :
	 * List) { FileReader reader = new FileReader(f); JSONObject ThingDescription =
	 * (JSONObject) jsonParser.parse(reader); JSONArray PrivacyPolicy2 =
	 * ((JSONArray) ThingDescription.get("privacyPolicy")); if
	 * (PrivacyPolicy2.size()) { } }
	 * 
	 * return null;
	 * 
	 * }
	 */

	private static ArrayList<File> listFileRecur(File rep, ArrayList<File> f, String extentionName) {
		if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
			f.add(rep);
			return f;
		} else if (rep.isDirectory() && !rep.isHidden()) {
			for (File d : rep.listFiles())
				listFileRecur(d, f, extentionName);
		}
		return f;
	}

	public LinkedHashMap<String, String> schearTD(ArrayList<String> concepts) {
		LinkedList<JSONObject> a = new LinkedList<>();
		LinkedHashMap<String, String> resultTD = new LinkedHashMap<>();
		getListTdContainConcept(concepts, a);
		for (JSONObject ThingDescription : a) {
			String textJson = formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 5);
			if (!resultTD.containsKey(ThingDescription.get("title").toString())
					|| textJson.equals(resultTD.get(ThingDescription.get("title").toString()))) {
				resultTD.put(ThingDescription.get("title").toString(), textJson);
			} else {
				resultTD.put(ThingDescription.get("title").toString() + " ("
						+ nbString(resultTD, ThingDescription.get("title").toString()) + ")", textJson);
			}
		}
		return resultTD;
	}

	private int nbString(LinkedHashMap<String, String> resultTD, String s) {
		int i = 0;
		for (String k : resultTD.keySet()) {
			if (k.contains(s)) {
				i++;
			}
		}
		return i;

	}

	private void getListTdContainConcept(ArrayList<String> concepts, LinkedList<JSONObject> a) {
		for (JSONObject ThingDescription : JsonObjectList) {
			if (containConcepts(ThingDescription, concepts)) {
				a.add(ThingDescription);
			}
		}
	}

	/**
	 * @param ThingDescription : JSON object of the TD
	 * @param concepts         : ArrayList<String> of concepts
	 * @return true if all the concepts is the List is written in the TD, false else
	 */
	private boolean containConcepts(JSONObject ThingDescription, ArrayList<String> concepts) {
		Object PrivacyPolicy = ThingDescription.get("privacyPolicy");
		JSONArray PrivacyPolicy2 = ((JSONArray) PrivacyPolicy);
		boolean b = true;
		for (String concept : concepts) {
			if (!containConcept(PrivacyPolicy2, concept)) {
				b = false;
			}
		}
		return b;
	}

	private boolean containConcept(JSONArray PrivacyPolicy2, String concept) {
		for (Object e : PrivacyPolicy2) {
			String conceptText = ((JSONObject) e).get("concept").toString();
			if (conceptText.substring(conceptText.indexOf(":") + 1).equals(concept)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param ThingDescription : JSON object of the TD
	 * @param concepts         : HasMap of concepts and their level of privacy, key:
	 *                         concept name / value: level privacy : hight/mid/low
	 * @return true if a concept is written in the TD, false else
	 */
	/*
	 * private boolean containConcept(JSONObject ThingDescription, HashMap<String,
	 * String> concepts) { Object PrivacyPolicy =
	 * ThingDescription.get("privacyPolicy"); JSONArray PrivacyPolicy2 =
	 * ((JSONArray) PrivacyPolicy); boolean b = false; for (String concept :
	 * concepts.keySet()) { for (Object e : PrivacyPolicy2) { if
	 * (containConcept(PrivacyPolicy2, concept)) { b = true; if (!((JSONObject)
	 * e).get("levelPrivacy").toString().equals(concepts.get(concept))) { return
	 * false; } } } } return b; }
	 */

	public ArrayList<File> getJsonFileList() {
		return JsonFileList;
	}

	public void setJsonFileList(ArrayList<File> jsonFileList) {
		JsonFileList = jsonFileList;
	}

	public static String formatJSONStr(final String json_str, final int indent_width) {
		final char[] chars = json_str.toCharArray();
		final String newline = System.lineSeparator();

		String ret = "";
		boolean begin_quotes = false;

		for (int i = 0, indent = 0; i < chars.length; i++) {
			char c = chars[i];

			if (c == '\"') {
				ret += c;
				begin_quotes = !begin_quotes;
				continue;
			}

			if (!begin_quotes) {
				switch (c) {
				case '{':
				case '[':
					ret += c + newline + String.format("%" + (indent += indent_width) + "s", "");
					continue;
				case '}':
				case ']':
					ret += newline + ((indent -= indent_width) > 0 ? String.format("%" + indent + "s", "") : "") + c;
					continue;
				case ':':
					ret += c + " ";
					continue;
				case ',':
					ret += c + newline + (indent > 0 ? String.format("%" + indent + "s", "") : "");
					continue;
				default:
					if (Character.isWhitespace(c))
						continue;
				}
			}

			ret += c + (c == '\\' ? "" + chars[++i] : "");
		}

		return ret;
	}

	public File getDir() {
		return dir;
	}

	public void setDir(File dir) throws IOException, ParseException {
		this.dir = dir;
		JsonObjectList = listJsonObjectRecur(dir, ".json");
	}

	public void setPath(String path) throws IOException, ParseException {
		this.dir = new File(path);
		JsonObjectList = listJsonObjectRecur(dir, ".json");
	}

}
