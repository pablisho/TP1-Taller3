package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

/**
 * Change the amount of repeat checkers threads.
 *
 */
public class ChangeRepeatedChecker implements Event {

	private boolean mAdd;
	
	public ChangeRepeatedChecker(boolean add){
		this.mAdd = add;
	}
	
	@Override
	public void execute(Statistics s) {
		if(mAdd){
			s.addRepeatedCheckerThread();
		}else{
			s.subRepeatedCheckerThread();
		}
	}
}
