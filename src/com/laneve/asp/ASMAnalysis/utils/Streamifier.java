package com.laneve.asp.ASMAnalysis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class Streamifier {

	public static InputStream streamify(String filename) throws FileNotFoundException {
		return new FileInputStream(filename);
	}
	
	public static List<InputStream> streamifyDirectory(String directory) {
		List<InputStream> res = new ArrayList<InputStream>();
		
		for (String file : collectClassFiles(directory)) {
			try {
				res.add(streamify(file));
			} catch (FileNotFoundException e) {}
		}
		
		return res;
	}

	
	public static List<String> collectClassFiles(String dir) {
		List<String> files = new ArrayList<String>();
		File f = new File(dir);
		if (!f.isDirectory())
			return files;
		
		for (File file : f.listFiles())
			if (file.getName().endsWith(".class"))
				files.add(file.getAbsolutePath());

		return files;
	}
}
