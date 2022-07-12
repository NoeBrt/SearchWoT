package model;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import parser.JSONParser;
import parser.ParseException;
import simple.JSONArray;
import simple.JSONObject;

/**
 * Class used for manage research on Thing Description JSON file in directory
 * with JSON SIMPLE API
 * 
 * @author Noe Breton
 *
 */
public class TdModel {

	/**
	 * Folder of Tds
	 */
	private File dir;
	/**
	 * List of all File in the File dir
	 */
	private ArrayList<File> JsonFileList;
	/**
	 * List of all JSONObject in the FILE dir
	 */
	private ArrayList<JSONObject> JsonObjectList;
	/**
	 * Name of the Json class to analyse, here we use "Privacy Policy"
	 */
	private String tdPartToAnalyse;

	/**
	 * Constructor of TdModel
	 * 
	 * @param path of the TDs directory
	 * @param TD   part to analyse and make the research
	 * @throws IOException
	 * @throws ParseException
	 */
	public TdModel(String path, String TDpart) throws IOException, ParseException {
		this.dir = new File(path);
		this.setTdPartToAnalyse(TDpart);
		JsonFileList = listFileRecur(dir, ".json");
		JsonObjectList = listJsonObjectRecur(dir, ".json");
		sortByConceptNumber(JsonObjectList);

	}

	/**
	 * Constructor of TdModel with a File instead of a String path
	 * 
	 * @param Tds directory
	 * @param TD  part to analyse and make the research
	 * @throws IOException
	 * @throws ParseException
	 */
	public TdModel(File dir, String TDpart) throws IOException, ParseException {
		this.dir = dir;
		this.setTdPartToAnalyse(TDpart);
		JsonFileList = listFileRecur(dir, ".json");
		JsonObjectList = listJsonObjectRecur(dir, ".json");
		sortByConceptNumber(JsonObjectList);

	}

	/**
	 * @param a JsonObject List
	 * @return a JsonObject List shorted descending by the number of concept they
	 *         have in the part define by tdPartToAnalyse
	 */
	public List<JSONObject> sortByConceptNumber(List<JSONObject> JsonObjectList) {
		JsonObjectList.sort(lengthComparator);
		return JsonObjectList;
	}

	/**
	 * Comparator who compare two JSONObject by the number of concept in their
	 * PrivacyPolicy Class
	 */
	private Comparator<JSONObject> lengthComparator = new Comparator<JSONObject>() {
		public int compare(JSONObject a, JSONObject b) {
			JSONArray PrivacyPolicy1 = ((JSONArray) a.get(tdPartToAnalyse));
			JSONArray PrivacyPolicy2 = ((JSONArray) b.get(tdPartToAnalyse));
			return PrivacyPolicy2.size() - PrivacyPolicy1.size();
			// size() is always nonnegative, so this won't have crazy overflow bugs
		}
	};

	/**
	 * @param repetory      of the td the user want to extract all the file
	 * @param extentionName name of the file extention we want to get, here JSon
	 * @return List of JSONObject who are in the repertory
	 * @throws IOException
	 * @throws ParseException
	 */
	private ArrayList<JSONObject> listJsonObjectRecur(File rep, String extentionName)
			throws IOException, ParseException {
		return listJsonObjectRecur(rep, new ArrayList<JSONObject>(), extentionName);
	}

	/**
	 * Extract recursively all the Files in a repertory, and convert it to a
	 * JsonObject List
	 * 
	 * @param repetory      of the td the user want to extract all the file
	 * @param List          in who stock JSON Objects
	 * @param extentionName
	 * @return ArrayList which containt all the JSON Objects
	 * @throws IOException
	 */
	public ArrayList<JSONObject> listJsonObjectRecur(File rep, ArrayList<JSONObject> f, String extentionName)
			throws IOException {
		if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
			FileReader reader = new FileReader(rep);
			try {
				JSONObject ThingDescription = (JSONObject) (new JSONParser()).parse(reader);
				if (ThingDescription.get(tdPartToAnalyse) != null) {
					f.add(ThingDescription);
				}
			} catch (ParseException pe) {
				System.out.println(rep.getName());
			}
			return f;
		} else if (rep.isDirectory() && !rep.isHidden()) {
			for (File d : rep.listFiles())
				listJsonObjectRecur(d, f, extentionName);
		}
		return f;
	}

	/**
	 * @param repetory      of the td the user want to extract all the file
	 * @param extentionName name of the file extention we want to get, here JSon
	 * @return List of File who are in the repertory
	 * @throws IOException
	 * @throws ParseException
	 */
	private ArrayList<File> listFileRecur(File rep, String extentionName) {
		return listFileRecur(rep, new ArrayList<File>(), extentionName);
	}

	/**
	 * Extract recursively all the Files in a repertory
	 * 
	 * @param repetory      of the td the user want to extract all the file
	 * @param List          in who stock File
	 * @param extentionName
	 * @return ArrayList which containt all the File
	 * @throws IOException
	 */
	private ArrayList<File> listFileRecur(File rep, ArrayList<File> f, String extentionName) {
		if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
			f.add(rep);
			return f;
		} else if (rep.isDirectory() && !rep.isHidden()) {
			for (File d : rep.listFiles())
				listFileRecur(d, f, extentionName);
		}
		return f;
	}

	/*
	 * method who sorted all td from the repertory and return all the td who contain
	 * all the concepts in parameterer, for each thing description in the list, if
	 * it contain all the concepts in parameter, it will be add at the LinkedHashmap
	 * (title in key an all the text in value) if 2 td have the same same, the name
	 * of the 2nd td will have a number next to it like this -> "thingname" (n)
	 * 
	 * @param Arraylist of Concept name that we want to find in Things decription
	 * 
	 * @return LinkedHashMap which have the title of a TD in key and the JSON
	 * content in value
	 */
	public LinkedHashMap<String, String> schearTD(ArrayList<String> concepts) {
		LinkedHashMap<String, String> resultTD = new LinkedHashMap<>();
		for (JSONObject ThingDescription : JsonObjectList) {
			if (containConcepts(ThingDescription, concepts)) {
				String textJson = formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 5);
				if (!resultTD.containsKey(ThingDescription.get("title").toString())
						|| textJson.equals(resultTD.get(ThingDescription.get("title").toString()))) {
					resultTD.put(ThingDescription.get("title").toString(), textJson);
				} else {
					resultTD.put(ThingDescription.get("title").toString() + " ("
							+ nbString(resultTD, ThingDescription.get("title").toString()) + ")", textJson);
				}
			}
		}
		return resultTD;
	}

	/**
	 * @param LinkedHashMap which are a result of a search
	 * @param Td's          Title
	 * @return number of td witb the same name or a similar name
	 */
	private int nbString(LinkedHashMap<String, String> resultTD, String s) {
		int i = 0;
		for (String k : resultTD.keySet()) {
			if (k.contains(s)) {
				i++;
			}
		}
		return i;
	}

	/**
	 * @param a    Thin gDescription
	 * @param List of concepts
	 * @return true if a thing description contain all the concepts in parameter,
	 *         false else
	 */
	private boolean containConcepts(JSONObject ThingDescription, ArrayList<String> concepts) {
		Object PrivacyPolicy = ThingDescription.get(tdPartToAnalyse);
		try {
			JSONArray PrivacyPolicy2 = ((JSONArray) PrivacyPolicy);

			boolean b = true;
			for (String concept : concepts) {
				if (!containConcept(PrivacyPolicy2, concept)) {
					b = false;
				}
			}
			return b;
		} catch (Exception e1) {
			return false;
		}
	}

	/**
	 * @param PrivacyPolicy2 JSONArray of the section define by tdPartToAnalyse
	 * @param concept        that the user want to verify the presence
	 * @return true if the concept is here, false else
	 */
	private boolean containConcept(JSONArray PrivacyPolicy2, String concept) {
		for (Object e : PrivacyPolicy2) {
			try {
				String conceptText = ((JSONObject) e).get("concept").toString();
				if (conceptText.substring(conceptText.indexOf(":") + 1).equals(concept)) {
					return true;
				}
			} catch (Exception e1) {
				return false;
			}
		}
		return false;
	}

	/**
	 * @return list of json file in directory
	 */
	public ArrayList<File> getJsonFileList() {
		return JsonFileList;
	}

	/**
	 * @param set jsonFileList
	 */
	public void setJsonFileList(ArrayList<File> jsonFileList) {
		JsonFileList = jsonFileList;
	}

	/**
	 * @param json_str
	 * @param indent_width
	 * @return a string of the content of a jsonFile format like a JSON
	 */
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

	/**
	 * @return File which represent the directory
	 */
	public File getDir() {
		return dir;
	}

	/**
	 * set the directory
	 * 
	 * @param directory of the TD
	 * @throws IOException
	 * @throws ParseException
	 */
	public void setDir(File dir) throws IOException, ParseException {
		this.dir = dir;
		JsonObjectList = listJsonObjectRecur(dir, ".json");
	}

	/**
	 * set the directory with the path
	 * 
	 * @param path to the directory
	 * @throws IOException
	 * @throws ParseException
	 */
	public void setPath(String path) throws IOException, ParseException {
		this.dir = new File(path);
		JsonObjectList = listJsonObjectRecur(dir, ".json");
	}

	/**
	 * @return the JsonObject List
	 */
	public ArrayList<JSONObject> getJsonObjectList() {
		return JsonObjectList;
	}

	/**
	 * set JsonObjectList with a ArrayList<JSONObject> in parameter
	 * 
	 * @param jsonObjectList
	 */
	public void setJsonObjectList(ArrayList<JSONObject> jsonObjectList) {
		JsonObjectList = jsonObjectList;
	}

	/**
	 * @return the name of Td to analyse
	 */
	public String getTdPartToAnalyse() {
		return tdPartToAnalyse;
	}

	/**
	 * set the part to analyse in td and reload the list of jsonObject with this new
	 * part
	 * 
	 * @param tdPartToAnalyse
	 * @throws IOException
	 * @throws ParseException
	 */
	public void setTdPartToAnalyse(String tdPartToAnalyse) throws IOException, ParseException {
		this.tdPartToAnalyse = tdPartToAnalyse;
		JsonFileList = listFileRecur(dir, ".json");
		JsonObjectList = listJsonObjectRecur(dir, ".json");
		sortByConceptNumber(JsonObjectList);
	}

}
