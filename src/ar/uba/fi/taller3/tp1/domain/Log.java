package ar.uba.fi.taller3.tp1.domain;

/**
 * Thread safe log for standard output.
 *
 */
public class Log {
	
	public synchronized static void log(String s){
		System.out.println(s);
	}
	
}
