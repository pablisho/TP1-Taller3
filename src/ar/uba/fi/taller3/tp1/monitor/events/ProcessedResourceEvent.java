package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

/**
 * Change the amount of downloaded resources.
 *
 */
public class ProcessedResourceEvent implements Event {

	@Override
	public void execute(Statistics s) {
		s.addDownloadedResource();
	}

}
