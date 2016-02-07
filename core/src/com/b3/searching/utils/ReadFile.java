package com.b3.searching.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Class for reading a text file
 */
public class ReadFile {
	private String path;
	
	/**
	 * Initialises the reader
	 * @param _path The file path
	 */
	public ReadFile(String _path) {
		path = _path;
	}
	
	/**
	 * Read all the lines in the specified file
	 * @return An array of strings (lines)
	 * @throws IOException Exception raising when opening or reading the file
	 */
	public String[] readLines() throws IOException {
		FileReader fr = new FileReader(path);
		BufferedReader bf = new BufferedReader(fr);
		
		String line;
		ArrayList<String> lines = new ArrayList<String>();
		
		while ((line = bf.readLine()) != null) {
			lines.add(line);
		}
		
		bf.close();
		
		return lines.toArray(new String[lines.size()]);
	}
}
