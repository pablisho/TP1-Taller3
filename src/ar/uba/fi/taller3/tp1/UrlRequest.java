package ar.uba.fi.taller3.tp1;

import java.net.URL;

public class UrlRequest {
	private URL urlToDownload;
	private int depth;
	
	public UrlRequest(URL url, int depth){
		this.urlToDownload = url;
		this.depth = depth;
	}
	
	public URL getUrl(){
		return this.urlToDownload;
	}
	
	public int getDepth(){
		return depth;
	}
	
}
