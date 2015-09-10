package ar.uba.fi.taller3.tp1.domain;

/**
 * Representation of a resource or html with its content type and the request that generated it.
 * @author pablorm
 *
 */
public class Document {

	private UrlRequest request;
	private String contentType;
	private byte[] content;

	public Document(UrlRequest request, String contentType, byte[] content){
		this.request = request;
		this.contentType = contentType;
		this.content = content;
	}
	
	public byte[] getContent(){
		return content;
	}
	
	public String getContentType(){
		return contentType;
	}
	
	public String getName(){
		return request.getUrl().toString();
	}

	public int getDepth() {
		return request.getDepth();
	}
	
}
