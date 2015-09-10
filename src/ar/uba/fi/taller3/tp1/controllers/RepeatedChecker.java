package ar.uba.fi.taller3.tp1.controllers;

import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.domain.Log;
import ar.uba.fi.taller3.tp1.domain.UrlRepository;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.events.ChangeRepeatedChecker;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

/**
 * Checks for repeated Urls so they are not downloaded again.
 *
 */
public class RepeatedChecker implements Runnable {

	// Queues.
	private LinkedBlockingQueue<UrlRequest> mQueueFrom;
	private LinkedBlockingQueue<UrlRequest> mQueueTo;
	private LinkedBlockingQueue<Event> mMonitorQueue;
	// Url repository.
	private UrlRepository urlRepository;
	// Finish flag.
	private boolean finish = false;

	public RepeatedChecker(LinkedBlockingQueue<UrlRequest> queueFrom,
			LinkedBlockingQueue<UrlRequest> queueTo,
			LinkedBlockingQueue<Event> monitorQueue, UrlRepository urlRepo) {
		this.mQueueFrom = queueFrom;
		this.mQueueTo = queueTo;
		this.mMonitorQueue = monitorQueue;
		this.urlRepository = urlRepo;
	}

	@Override
	public void run() {
		UrlRequest request = null;
		URL url = null;
		while (!finish) {
			try {
				// Take task from queue.
				request = mQueueFrom.take();
				url = request.getUrl();
				mMonitorQueue.put(new ChangeRepeatedChecker(true));
				synchronized (urlRepository) {
					// If not contained in the repository, then add it and
					// schedule download.
					if (!urlRepository.contains(url)) {
						urlRepository.add(url);
						mQueueTo.put(request);
					}
				}
				mMonitorQueue.put(new ChangeRepeatedChecker(false));
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}
	}

}
