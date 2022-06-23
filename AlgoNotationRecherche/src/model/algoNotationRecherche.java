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
	 * path of the TDs folder
	 */
	// "C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE\\anotation_exemple"
	File dir;
	public algoNotationRecherche(String path) {
		this.dir=new File(path);
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
		for (File item : listFileRecur(dir,".json")) {
				try (FileReader reader = new FileReader(item)) {
					JSONObject ThingDescription = (JSONObject) jsonParser.parse(reader);
					if (containConcept(ThingDescription, concepts)) {
						resultTD.put(ThingDescription.get("title").toString(),
								formatJSONStr(ThingDescription.toJSONString().replace("\\/", "/"), 4));}
					

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
	
	private static ArrayList<File>  listFileRecur(File rep,String extentionName) {
		return listFileRecur(rep,new ArrayList<File>(),extentionName);
	}
	
	private static ArrayList<File>  listFileRecur(File rep,ArrayList<File> f,String extentionName) {
		if (rep.isFile()&&!rep.isHidden()&&rep.getName().endsWith(extentionName)) {
			f.add(rep);
			return f;
		}else if (rep.isDirectory()&&!rep.isHidden()) {
			for (File d:rep.listFiles())
				listFileRecur(d,f,extentionName);
		}
		return f;
	}

	public HashMap<String, String> schearTD(ArrayList<String> concepts) {
		JSONParser jsonParser = new JSONParser();
		HashMap<String, String> resultTD = new HashMap<>();
		for (File item : listFileRecur(dir,".json")) {
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
	}

	public void setPath(String path) {
		this.dir=new File(path);
	}

}
