package org.metaborg.java.conformance.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class Util {
	public static char[] getBytes(String fileName) throws FileNotFoundException, IOException {
		BufferedReader reader = new BufferedReader(new FileReader(fileName));
		try {
			StringBuilder stringBuilder = new StringBuilder();
			String s = reader.readLine();
			while(s != null) {
				stringBuilder.append(s);
				s = reader.readLine();
			}
			return stringBuilder.toString().toCharArray();
		} finally {
			reader.close();
		}
	}
}
