package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

public class ChangeHtmlDownloaderEvent implements Event{

	private boolean mAdd;
	
	public ChangeHtmlDownloaderEvent(boolean add) {
		this.mAdd = add;
	}
	
	@Override
	public void execute(Statistics s) {
		if(mAdd){
			s.addHtmlDownloaderThread();
		}else{
			s.subHtmlDownloaderThread();
		}
	}
}
