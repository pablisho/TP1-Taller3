package ar.uba.fi.taller3.tp1.monitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.Log;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class Monitor implements Runnable {

	private static final long MONITOR_INTERVAL_MS = 1000;
	private static final String LOG_FILENAME = "statistics.log";
	
	// Evente queue.
	private LinkedBlockingQueue<Event> mEventQueue;
	
	private Statistics mStatistics = new Statistics();
	private PrintWriter mPrintWriter;
	
	private boolean finish = false;
	private long lastRenderedTimestamp = 0;
	
	public Monitor(LinkedBlockingQueue<Event> eventQueue){
		this.mEventQueue = eventQueue;
		try {
			mPrintWriter = new PrintWriter(new File(LOG_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Event event = null;
		while(!finish){
			try {
				event = mEventQueue.take();
				event.execute(mStatistics);
				long currentTimeStamp = System.currentTimeMillis();
				if(currentTimeStamp - lastRenderedTimestamp > MONITOR_INTERVAL_MS){
					mPrintWriter.println(mStatistics.toString());
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}		
	}

}
