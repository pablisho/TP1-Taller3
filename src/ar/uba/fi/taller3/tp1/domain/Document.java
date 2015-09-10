package ar.uba.fi.taller3.tp1.domain;

/**
 * Representation of a resource or html with its content type and the request that generated it.
 * @author pablorm
 *
 */
public class Document {

	private UrlRequest request;
	private String contentType;
	private String content;

	public Document(UrlRequest request, String contentType, String content){
		this.request = request;
		this.contentType = contentType;
		this.content = content;
	}
	
	public String getContent(){
		return content;
	}
	
	public String getContentType(){
		return contentType;
	}
	
	public String getName(){
		return request.getUrl().toString();
	}
	
}
