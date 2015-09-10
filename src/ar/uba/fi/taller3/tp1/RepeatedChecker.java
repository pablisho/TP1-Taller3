package ar.uba.fi.taller3.tp1;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.monitor.events.ChangeRepeatedChecker;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class RepeatedChecker implements Runnable {

	private LinkedBlockingQueue<UrlRequest> mQueueFrom;
	private LinkedBlockingQueue<UrlRequest> mQueueTo;
	private LinkedBlockingQueue<Event> mMonitorQueue;
	
	private boolean finish = false;
	
	private static List<URL> downloadedUrls = new ArrayList<URL>();
	
	public RepeatedChecker(LinkedBlockingQueue<UrlRequest> queueFrom, LinkedBlockingQueue<UrlRequest> queueTo,
			LinkedBlockingQueue<Event> monitorQueue){
		this.mQueueFrom = queueFrom;
		this.mQueueTo = queueTo;
		this.mMonitorQueue = monitorQueue;
	}
	
	@Override
	public void run() {
		UrlRequest request = null;
		URL url = null;
		while(!finish){
			try {
				request = mQueueFrom.take();
				url = request.getUrl();
				mMonitorQueue.put(new ChangeRepeatedChecker(true));
				synchronized (downloadedUrls) {
					if(!downloadedUrls.contains(url)){
						downloadedUrls.add(url);
						mQueueTo.put(request);
					}else{
						//System.out.println("URL: " + url.toString() + " already downloaded.");
					}
				}
				mMonitorQueue.put(new ChangeRepeatedChecker(false));
			} catch (InterruptedException e) {
				System.out.println("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}		
	}

}
