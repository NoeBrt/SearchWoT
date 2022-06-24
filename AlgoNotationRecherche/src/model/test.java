package model;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import parser.ParseException;

public class test {
public static void main(String[] args) throws IOException, ParseException {
	algoNotationRecherche a = new algoNotationRecherche("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE");
	System.out.println(a.listFileRecur(new File("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE"),new HashMap<File,ArrayList<String>>(),"json"));
System.out.println(a.sortByValue(a.listFileRecur(new File("C:\\Users\\noebr\\Desktop\\IoT-Devices-Benchmark_ANNOTE"),new HashMap<File,ArrayList<String>>(),"json")));
}
}
