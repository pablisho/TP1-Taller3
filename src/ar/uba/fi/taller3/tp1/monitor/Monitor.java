package ar.uba.fi.taller3.tp1.monitor;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.Log;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class Monitor implements Runnable {

	private static final String LOG_FILENAME = "statistics.log";
	
	// Evente queue.
	private LinkedBlockingQueue<Event> mEventQueue;
	
	private Statistics mStatistics = new Statistics();
	private PrintWriter mPrintWriter;
	
	private boolean finish = false;
	private long monitorPeriod;
	
	private TimerTask task = new TimerTask() {
		@Override
		public void run() {
			Log.log("TASK");
			String statistics;
			synchronized (mStatistics) {
				statistics = mStatistics.toString();
			}
			mPrintWriter.println(statistics);
			mPrintWriter.flush();
		}
	};
	
	public Monitor(LinkedBlockingQueue<Event> eventQueue, long monitorPeriod){
		this.mEventQueue = eventQueue;
		this.monitorPeriod = monitorPeriod;
		try {
			mPrintWriter = new PrintWriter(new File(LOG_FILENAME));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void run() {
		Event event = null;
		Timer timer =  new Timer();
		Log.log("Monitor period " + monitorPeriod);
		timer.scheduleAtFixedRate(task, 1, monitorPeriod);
		while(!finish){
			try {
				event = mEventQueue.take();
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
