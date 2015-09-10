package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

public interface Event {
	public void execute(Statistics s);
}
