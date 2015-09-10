package ar.uba.fi.taller3.tp1.monitor.events;

import ar.uba.fi.taller3.tp1.monitor.Statistics;

public class ChangeFileSaverEvent implements Event {

	private boolean mAdd;
	
	public ChangeFileSaverEvent(boolean mAdd){
		this.mAdd = mAdd;
	}
	
	@Override
	public void execute(Statistics s) {
		if(mAdd){
			s.addFileSaverThread();
		}else{
			s.subFileSaverThread();
		}
	}

}
