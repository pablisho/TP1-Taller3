package ar.uba.fi.taller3.tp1.controllers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.Log;
import ar.uba.fi.taller3.tp1.monitor.events.ChangeFileSaverEvent;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

/**
 * Saves content to a file.
 *
 */
public class FileSaver implements Runnable {

	// Queues.
	private LinkedBlockingQueue<Document> inputQueue;
	private LinkedBlockingQueue<Event> monitorQueue;
	// Folder location
	private String folderLocation;
	// Finish flag.
	private boolean finish = false;

	public FileSaver(LinkedBlockingQueue<Document> inputQueue,
			LinkedBlockingQueue<Event> monitorQueue, String fileLocation) {
		this.inputQueue = inputQueue;
		this.monitorQueue = monitorQueue;
		this.folderLocation = fileLocation;
	}

	@Override
	public void run() {
		Document doc = null;
		PrintWriter writer = null;
		File file = null;
		while (!finish) {
			try {
				try {
					// Take task from queue.
					doc = inputQueue.take();
					monitorQueue.put(new ChangeFileSaverEvent(true));
					String folder = doc.getContentType().replace("/", "");
					String fileName = doc.getName().replace("/", "");
					// Create file.
					file = new File(folderLocation + "/" + folder + "/"
							+ fileName);
					file.getParentFile().mkdirs();
					writer = new PrintWriter(file);
					// Write content.
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
