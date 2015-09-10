package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

public class ProcessedUrlEvent implements Event {

	@Override
	public void execute(Statistics s) {
		s.addDownloadedPage();
	}

}
