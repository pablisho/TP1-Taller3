package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

/**
 * Change the amount of analyzer threads.
 *
 */
public class ChangeAnalyzerEvent implements Event {

	private boolean mAdd;
	
	public ChangeAnalyzerEvent(boolean mAdd){
		this.mAdd = mAdd;
	}
	
	@Override
	public void execute(Statistics s) {
		if(mAdd){
			s.addAnalyzerThread();
		}else{
			s.subAnalyzerThread();
		}
	}
}
