package ar.uba.fi.taller3.tp1;

import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.monitor.events.ChangeResourceDownloaderEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class ResourceDownloader extends Downloader {

	public ResourceDownloader(LinkedBlockingQueue<UrlRequest> queueFrom,
			LinkedBlockingQueue<Document> queueTo,
			LinkedBlockingQueue<Event> monitorQueue) {
		super(queueFrom, queueTo, monitorQueue);
	}

	@Override
	protected void start() throws InterruptedException {
		mMonitorQueue.put(new ChangeResourceDownloaderEvent(true));
	}

	@Override
	protected void end() throws InterruptedException {
		mMonitorQueue.put(new ChangeResourceDownloaderEvent(false));
	}

	@Override
	protected void sendResponse(Document doc) throws InterruptedException {
		if(mQueueTo != null){
			mQueueTo.put(doc);
		}
	}

}
