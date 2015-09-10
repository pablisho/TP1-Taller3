package ar.uba.fi.taller3.tp1.monitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.domain.Log;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

/**
 * Monitor that registers the state of the system and writes it to a file at a desired rate.
 *
 */
public class Monitor implements Runnable {
	
	// Evente queue.
	private LinkedBlockingQueue<Event> mEventQueue;
	// Statistics object.
	private Statistics mStatistics = new Statistics();
	// Writer.
	private PrintWriter mPrintWriter;
	// Finish flag.
	private boolean finish = false;
	// Monitor period.
	private long monitorPeriod;
	// Output file name.
	private String fileName;
	
	// Helper task to write to the file at the desired rate.
	private TimerTask task = new TimerTask() {
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		@Override
		public void run() {
			String statistics;
			synchronized (mStatistics) {
				statistics = mStatistics.toString();
			}
			Date date = Calendar.getInstance().getTime();
			mPrintWriter.println(df.format(date));
			mPrintWriter.println(statistics);
			mPrintWriter.flush();
		}
	};
	
	public Monitor(LinkedBlockingQueue<Event> eventQueue,String filename, long monitorPeriod){
		this.mEventQueue = eventQueue;
		this.monitorPeriod = monitorPeriod;
		this.fileName = filename;
		try {
			mPrintWriter = new PrintWriter(new File(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Event event = null;
		Timer timer =  new Timer();
		Log.log("Monitor period " + monitorPeriod);
		// Schedule the writing task at the desired rate.
		timer.scheduleAtFixedRate(task, 1, monitorPeriod);
		while(!finish){
			try {
				// Receive event from the queue.
				event = mEventQueue.take();
				// Update the statistics object.
				synchronized (mStatistics) {
					event.execute(mStatistics);
				}
			} catch (InterruptedException e) {
				Log.log("Interrupted. Finishing..");
				finish = true;
				e.printStackTrace();
			}
		}		
	}

}
