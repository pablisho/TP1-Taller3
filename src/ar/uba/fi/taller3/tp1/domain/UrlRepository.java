package ar.uba.fi.taller3.tp1.domain;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;

/**
 * Url repository for already downloaded urls.
 *
 */
public class UrlRepository {
	
	private String fileName;
	
	public UrlRepository(String fileName){
		this.fileName = fileName;
	}
	
	public boolean contains(URL url){
		String urlToCompare = url.toString();
		File f = new File(fileName);
		try (BufferedReader br = new BufferedReader(new FileReader(f))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		       if(line.equals(urlToCompare)){
		    	   return true;
		       }
		    }
		}catch(IOException ex){
			Log.log("Warning: File does not exist");
		}
		return false;
	}
	
	public boolean add(URL url){
		String urlString = url.toString();
		File f = new File(fileName);
		f.getParentFile().mkdirs();
		PrintWriter out;
		try {
			out = new PrintWriter(new BufferedWriter(new FileWriter(fileName, true)));
			out.println(urlString);
			out.close(); 
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
}
