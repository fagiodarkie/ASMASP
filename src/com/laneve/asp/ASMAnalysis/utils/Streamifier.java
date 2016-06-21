package com.laneve.asp.ASMAnalysis.utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.filechooser.FileNameExtensionFilter;

public class Streamifier {

	public static InputStream streamify(String filename) throws FileNotFoundException {
		return new FileInputStream(filename);
	}
	
	public static List<InputStream> streamifyDirectory(String directory) {
		List<InputStream> res = new ArrayList<InputStream>();
		
		File f = new File(directory);
		if (!f.isDirectory())
			return res;
		
		for (File file : f.listFiles()) {
			if (file.getName().endsWith(".class")) {
				try {
					res.add(streamify(file.getAbsolutePath()));
				} catch (FileNotFoundException e) {
					// ignored.
				}
			}
		}
		
		return res;
	}

}
