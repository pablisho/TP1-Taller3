package ar.uba.fi.taller3.tp1.controllers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.Log;
import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.events.Event;
import ar.uba.fi.taller3.tp1.monitor.events.ProcessedUrlEvent;

public abstract class Downloader implements Runnable {

	private static final int RESPONSE_OK = 200;
	private static final String USER_AGENT = "Mozilla/5.0";

	private LinkedBlockingQueue<UrlRequest> mQueueFrom;
	protected LinkedBlockingQueue<Document> mQueueTo;
	protected LinkedBlockingQueue<Event> mMonitorQueue;
	private boolean finish = false;

	public Downloader(LinkedBlockingQueue<UrlRequest> queueFrom, LinkedBlockingQueue<Document> queueTo,
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
					request = mQueueFrom.take();
					url = request.getUrl();
					start();
					
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setRequestProperty("User-Agent", USER_AGENT);
					int responseCode = conn.getResponseCode();
					if (responseCode == RESPONSE_OK) {
						String contentType = conn.getHeaderField("Content-Type");
						BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
						String inputLine;
						StringBuffer response = new StringBuffer();

						while ((inputLine = in.readLine()) != null) {
							response.append(inputLine);
						}
						in.close();
						Document doc = new Document(request, contentType, response.toString());
						sendResponse(doc);
						mMonitorQueue.put(new ProcessedUrlEvent());
					}

				} catch (IOException e) {
					Log.log("Could not open conecction to " + url.toString());
					e.printStackTrace();
				} finally {
					end();
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}
	}
	
	protected abstract void start() throws InterruptedException;
	protected abstract void end() throws InterruptedException;
	protected abstract void sendResponse(Document doc) throws InterruptedException;
}
