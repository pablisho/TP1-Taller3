package ar.uba.fi.taller3.tp1.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.Log;
import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.monitor.events.ChangeFileSaverEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class FileSaver implements Runnable {

	private LinkedBlockingQueue<Document> inputQueue;
	private LinkedBlockingQueue<Event> monitorQueue;
	private String fileLocation;
	private boolean finish = false;

	public FileSaver(LinkedBlockingQueue<Document> inputQueue,
			LinkedBlockingQueue<Event> monitorQueue, String fileLocation) {
		this.inputQueue = inputQueue;
		this.monitorQueue = monitorQueue;
		this.fileLocation =fileLocation;
	}

	@Override
	public void run() {
		Document doc = null;
		PrintWriter writer = null;
		File file = null;
		while (!finish) {
			try {
				try {
					doc = inputQueue.take();
					monitorQueue.put(new ChangeFileSaverEvent(true));
					String folder = doc.getContentType().replace("/", "");
					String fileName = doc.getName().replace("/", "");
					file = new File(fileLocation + "/" + folder + "/"+ fileName);
					file.getParentFile().mkdirs();
					writer = new PrintWriter(file);
					writer.print(doc.getContent());
					writer.close();
				} catch (IOException e) {
					Log.log("ERROR WRITING FILE");
					e.printStackTrace();
				} finally {
					monitorQueue.put(new ChangeFileSaverEvent(false));
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}

		}
	}

}
