package ar.uba.fi.taller3.tp1.controllers;

import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.events.ChangeHtmlDownloaderEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;
import ar.uba.fi.taller3.tp1.monitor.events.ProcessedHtmlEvent;

/**
 * Downloader implementation for Html links.
 *
 */
public class HtmlDownloader extends Downloader {

	private LinkedBlockingQueue<Document> analyzerQueue;
	
	public HtmlDownloader(LinkedBlockingQueue<UrlRequest> queueFrom,
			LinkedBlockingQueue<Document> queueTo,
			LinkedBlockingQueue<Document> analyzerQueue,
			LinkedBlockingQueue<Event> monitorQueue) {
		super(queueFrom, queueTo, monitorQueue);
		this.analyzerQueue = analyzerQueue;
	}

	@Override
	protected void start() throws InterruptedException {
		mMonitorQueue.put(new ChangeHtmlDownloaderEvent(true));
	}

	@Override
	protected void end() throws InterruptedException{
		mMonitorQueue.put(new ChangeHtmlDownloaderEvent(false));
	}

	@Override
	protected void sendResponse(Document doc) throws InterruptedException{
		if(mQueueTo != null){
			mQueueTo.put(doc);
		}
		if(analyzerQueue != null){
			analyzerQueue.put(doc);
		}
		if(mMonitorQueue != null){
			mMonitorQueue.put(new ProcessedHtmlEvent());
		}
	}

}
