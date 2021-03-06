package ar.uba.fi.taller3.tp1.controllers;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.Log;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.events.ChangeAnalyzerEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

/**
 * Analyzes Htmls searching for hrefs and srcs.
 *
 */
public class Analyzer implements Runnable {

	// Regular expressions.
	private static final String HTML_HREF_PATTERN = "\\s*(?i)href\\s*=\\s*\"(([^\"]*))\"";
	private static final String HTML_SRC_PATTERN = "\\s*(?i)src\\s*=\\s*\"(([^\"]*))\"";

	// Patterns and matchers.
	private Pattern patternLink;
	private Matcher matcherLink;
	private Pattern patternSrc;
	private Matcher matcherSrc;

	// Queues.
	private LinkedBlockingQueue<Document> mDocQueue;
	private LinkedBlockingQueue<UrlRequest> mUrlQueue;
	private LinkedBlockingQueue<UrlRequest> mResQueue;
	private LinkedBlockingQueue<Event> mMonitorQueue;

	// Finish flag.
	private boolean finish = false;
	
	// Max depth for re scheduling pages.
	private int maxDepth;

	public Analyzer(LinkedBlockingQueue<Document> docQueue, LinkedBlockingQueue<UrlRequest> urlQueue,
			LinkedBlockingQueue<UrlRequest> resQueue, LinkedBlockingQueue<Event> monitorQueue, int maxDepth) {
		this.mDocQueue = docQueue;
		this.mUrlQueue = urlQueue;
		this.mResQueue = resQueue;
		this.mMonitorQueue = monitorQueue;
		patternLink = Pattern.compile(HTML_HREF_PATTERN);
		patternSrc = Pattern.compile(HTML_SRC_PATTERN);
		this.maxDepth = maxDepth;
	}

	@Override
	public void run() {
		Document doc = null;
		String text = null;
		String link = null;
		while (!finish) {
			try {
				try {
					// Take task from queue.
					doc = mDocQueue.take();
					mMonitorQueue.put(new ChangeAnalyzerEvent(true));
					// Check for max depth iteration in pages
					int depth = doc.getDepth();
					if(depth < maxDepth){
						text = new String(doc.getContent());
						// Match Hrefs
						matcherLink = patternLink.matcher(text);
						while (matcherLink.find()) {
							link = matcherLink.group(1);
							if (link != null && !link.isEmpty()) {
								if (link.charAt(0) == '/') {
									String urlBase = doc.getName();
									// Schedule download
									mUrlQueue.put(new UrlRequest(new URL(urlBase+"/"), depth+1));
								} else {
									// Schedule download
									mUrlQueue.put(new UrlRequest(new URL(link),depth+1));
								}
							}
						}
						// Match Srcs
						matcherSrc = patternSrc.matcher(text);
						while (matcherSrc.find()) {
							link = matcherSrc.group(1);
							if (link != null && !link.isEmpty()) {
								if (link.charAt(0) == '/') {
									String urlBase = doc.getName();
									// Schedule download
									mUrlQueue.put(new UrlRequest(new URL(urlBase+"/"), depth+1));
								} else {
									// Schedule download
									mResQueue.put(new UrlRequest(new URL(link),depth+1));
								}
							}
						}
					}
				} catch (MalformedURLException e) {
					Log.log("Wrong URL: " + link);
				} finally {
					mMonitorQueue.put(new ChangeAnalyzerEvent(false));
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}
	}

}
