package com.laneve.asp.ASMAnalysis.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Streamifier {

	public static InputStream streamify(String filename) throws FileNotFoundException {
		return new FileInputStream(filename);
	}
	
	public static List<InputStream> streamifyDirectory(Path directory) {
		List<InputStream> res = new ArrayList<InputStream>();
		
		for (String file : collectClassFiles(directory)) {
			try {
				res.add(streamify(file));
			} catch (FileNotFoundException e) {}
		}
		
		return res;
	}

	
	public static List<String> collectClassFiles(Path directory) {
		List<String> files = new ArrayList<String>();
		
		for (File file : directory.toFile().listFiles())
			if (file.getName().endsWith(".class"))
				files.add(file.getAbsolutePath());

		return files;
	}
}
