package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

/**
 * Change the amount of Resource Downloader threads.
 *
 */
public class ChangeResourceDownloaderEvent implements Event {

	private boolean mAdd;
	
	public ChangeResourceDownloaderEvent(boolean add) {
		this.mAdd = add;
	}
	
	
	@Override
	public void execute(Statistics s) {
		if(mAdd){
			s.addResDownloaderThread();
		}else{
			s.subResDownloaderThread();
		}
	}

}
