package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

	public algoNotationRecherche(String path) {
		this.dir = new File(path);
		JsonFileList = listFileRecur(dir, ".json");
	}

	public algoNotationRecherche(File dir) {
		this.dir = dir;
		JsonFileList = listFileRecur(dir, ".json");
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
	public HashMap<String, String> schearTD(HashMap<String, String> concepts) {
		JSONParser jsonParser = new JSONParser();
		HashMap<String, String> resultTD = new HashMap<>();
		for (File item : JsonFileList) {
			try (FileReader reader = new FileReader(item)) {
				JSONObject ThingDescription = (JSONObject) jsonParser.parse(reader);
				if (containConcept(ThingDescription, concepts)) {
					resultTD.put(ThingDescription.get("title").toString(),
							formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4));
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ParseException e) {
				e.printStackTrace();
			}

		}
		return resultTD;
	}

	public LinkedHashMap<File, ArrayList<String>> sortByValue(HashMap<File, ArrayList<String>> map) {
		return map.entrySet().stream().sorted(Map.Entry.comparingByValue(lengthComparator))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
	}
	private Comparator<List<String>> lengthComparator = new Comparator<List<String>>() {
		  public int compare(List<String> a, List<String> b) {
		    return a.size() - b.size(); 
		    // size() is always nonnegative, so this won't have crazy overflow bugs
		  }
		};

	public HashMap<File, ArrayList<String>> listFileRecur(File rep, HashMap<File, ArrayList<String>> f, String extentionName)
			throws IOException, ParseException {
		if (rep.isFile() && !rep.isHidden() && rep.getName().endsWith(extentionName)) {
			JSONParser jsonParser = new JSONParser();
			FileReader reader = new FileReader(rep);
			JSONObject ThingDescription = (JSONObject) jsonParser.parse(reader);
			JSONArray PrivacyPolicy2 = ((JSONArray) ThingDescription.get("privacyPolicy"));
			PrivacyPolicy2.toArray();
			f.put(rep,new ArrayList<String>(PrivacyPolicy2));
			return f;
		} else if (rep.isDirectory() && !rep.isHidden()) {
			for (File d : rep.listFiles())
				listFileRecur(d, f, extentionName);
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

	public HashMap<String, String> schearTD(ArrayList<String> concepts) {
		JSONParser jsonParser = new JSONParser();
		HashMap<String, String> resultTD = new HashMap<>();
		for (File item : listFileRecur(dir, ".json")) {
			if (item.isFile()) {
				try (FileReader reader = new FileReader(item)) {
					JSONObject ThingDescription = (JSONObject) jsonParser.parse(reader);
					if (containConcepts(ThingDescription, concepts)) {
						resultTD.put(ThingDescription.get("title").toString(),
								formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4));

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
	private boolean containConcept(JSONObject ThingDescription, HashMap<String, String> concepts) {
		Object PrivacyPolicy = ThingDescription.get("privacyPolicy");
		JSONArray PrivacyPolicy2 = ((JSONArray) PrivacyPolicy);
		boolean b = false;
		for (String concept : concepts.keySet()) {
			for (Object e : PrivacyPolicy2) {
				if (containConcept(PrivacyPolicy2, concept)) {
					b = true;
					if (!((JSONObject) e).get("levelPrivacy").toString().equals(concepts.get(concept))) {
						return false;
					}
				}
			}
		}
		return b;
	}

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

	public void setDir(File dir) {
		this.dir = dir;
		JsonFileList = listFileRecur(dir, ".json");
	}

	public void setPath(String path) {
		this.dir = new File(path);
		JsonFileList = listFileRecur(dir, ".json");
	}

	public static void triFusion(int tableau[]) {
		int longueur = tableau.length;
		if (longueur > 0) {
			triFusion(tableau, 0, longueur - 1);
		}
	}

	private static void triFusion(int tableau[], int deb, int fin) {
		if (deb != fin) {
			int milieu = (fin + deb) / 2;
			triFusion(tableau, deb, milieu);
			triFusion(tableau, milieu + 1, fin);
			fusion(tableau, deb, milieu, fin);
		}
	}

	private static void fusion(int tableau[], int deb1, int fin1, int fin2) {
		int deb2 = fin1 + 1;

		// on recopie les éléments du début du tableau
		int table1[] = new int[fin1 - deb1 + 1];
		for (int i = deb1; i <= fin1; i++) {
			table1[i - deb1] = tableau[i];
		}

		int compt1 = deb1;
		int compt2 = deb2;

		for (int i = deb1; i <= fin2; i++) {
			if (compt1 == deb2) // c'est que tous les éléments du premier tableau ont été utilisés
			{
				break; // tous les éléments ont donc été classés
			} else if (compt2 == (fin2 + 1)) // c'est que tous les éléments du second tableau ont été utilisés
			{
				tableau[i] = table1[compt1 - deb1]; // on ajoute les éléments restants du premier tableau
				compt1++;
			} else if (table1[compt1 - deb1] < tableau[compt2]) {
				tableau[i] = table1[compt1 - deb1]; // on ajoute un élément du premier tableau
				compt1++;
			} else {
				tableau[i] = tableau[compt2]; // on ajoute un élément du second tableau
				compt2++;
			}
		}
	}
}
