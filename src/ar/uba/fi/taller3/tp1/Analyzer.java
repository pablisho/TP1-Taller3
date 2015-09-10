package ar.uba.fi.taller3.tp1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ar.uba.fi.taller3.tp1.monitor.events.ChangeAnalyzerEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class Analyzer implements Runnable {

	private static final String HTML_HREF_PATTERN = "\\s*(?i)href\\s*=\\s*\"(([^\"]*))\"";
	private static final String HTML_SRC_PATTERN = "\\s*(?i)src\\s*=\\s*\"(([^\"]*))\"";

	private Pattern patternLink;
	private Matcher matcherLink;
	private Pattern patternSrc;
	private Matcher matcherSrc;

	private LinkedBlockingQueue<Document> mDocQueue;
	private LinkedBlockingQueue<UrlRequest> mUrlQueue;
	private LinkedBlockingQueue<UrlRequest> mResQueue;
	private LinkedBlockingQueue<Event> mMonitorQueue;

	private boolean finish = false;

	public Analyzer(LinkedBlockingQueue<Document> docQueue, LinkedBlockingQueue<UrlRequest> urlQueue,
			LinkedBlockingQueue<UrlRequest> resQueue, LinkedBlockingQueue<Event> monitorQueue) {
		this.mDocQueue = docQueue;
		this.mUrlQueue = urlQueue;
		this.mResQueue = resQueue;
		this.mMonitorQueue = monitorQueue;
		patternLink = Pattern.compile(HTML_HREF_PATTERN);
		patternSrc = Pattern.compile(HTML_SRC_PATTERN);
	}

	@Override
	public void run() {
		Document doc = null;
		String text = null;
		String link = null;
		int i = 0;
		while (!finish) {
			try {
				try {
					doc = mDocQueue.take();
					text = doc.getContent();
					mMonitorQueue.put(new ChangeAnalyzerEvent(true));
					// Match Hrefs
					matcherLink = patternLink.matcher(text);
					while (matcherLink.find()) {
						link = matcherLink.group(1);
						if (link != null && !link.isEmpty()) {
							if (link.charAt(0) == '/') {
								String urlBase = doc.getName();
								mUrlQueue.put(new UrlRequest(new URL(urlBase+"/"), 0));
							} else {
								mUrlQueue.put(new UrlRequest(new URL(link),0));
							}
						}
					}
					matcherSrc = patternSrc.matcher(text);
					while (matcherSrc.find()) {
						link = matcherSrc.group(1);
						if (link != null && !link.isEmpty()) {
							if (link.charAt(0) == '/') {
								String urlBase = doc.getName();
								mUrlQueue.put(new UrlRequest(new URL(urlBase+"/"), 0));
							} else {
								mResQueue.put(new UrlRequest(new URL(link),0));
							}
						}
					}
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					System.out.println("MALFORMED URL: " + link);
					e.printStackTrace();
				} finally {
					mMonitorQueue.put(new ChangeAnalyzerEvent(false));
				}
			} catch (InterruptedException e) {
				System.out.println("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}
	}

}
