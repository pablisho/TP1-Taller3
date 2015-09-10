package ar.uba.fi.taller3.tp1.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.Log;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

/**
 * Downloads an Url via Http connection.
 *
 */
public abstract class Downloader implements Runnable {

	// Http constants.
	private static final int RESPONSE_OK = 200;
	private static final String USER_AGENT = "Mozilla/5.0";

	// Queues.
	private LinkedBlockingQueue<UrlRequest> mQueueFrom;
	protected LinkedBlockingQueue<Document> mQueueTo;
	protected LinkedBlockingQueue<Event> mMonitorQueue;

	// Finish flag.
	private boolean finish = false;

	public Downloader(LinkedBlockingQueue<UrlRequest> queueFrom,
			LinkedBlockingQueue<Document> queueTo,
			LinkedBlockingQueue<Event> monitorQueue) {
		this.mQueueFrom = queueFrom;
		this.mQueueTo = queueTo;
		this.mMonitorQueue = monitorQueue;
	}

	@Override
	public void run() {
		UrlRequest request = null;
		URL url = null;
		while (!finish) {
			try {
				try {
					// take task from queue.
					request = mQueueFrom.take();
					url = request.getUrl();
					start();
					// Connect.
					HttpURLConnection conn = (HttpURLConnection) url
							.openConnection();
					conn.setRequestProperty("User-Agent", USER_AGENT);
					int responseCode = conn.getResponseCode();
					if (responseCode == RESPONSE_OK) {
						String contentType = conn
								.getHeaderField("Content-Type");
						InputStream  input  = conn.getInputStream();
					    byte[]       buffer = new byte[4096];
					    int          n      = -1;
					    ByteArrayOutputStream output = new ByteArrayOutputStream();
					    while ((n = input.read(buffer)) != -1) {
					    	output.write( buffer, 0, n );
					    }
						output.toByteArray();
						// Redirect output.
						Document doc = new Document(request, contentType,
								output.toByteArray());
						sendResponse(doc);
						 output.close();
					}

				} catch (IOException e) {
					Log.log("Could not open conecction to " + url.toString());
				} finally {
					end();
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
			}
		}
	}

	protected abstract void start() throws InterruptedException;

	protected abstract void end() throws InterruptedException;

	protected abstract void sendResponse(Document doc)
			throws InterruptedException;
}
