package ar.uba.fi.taller3.tp1.monitor;

/**
 * Statistics object.
 *
 */
public class Statistics {
	private int numberOfDownloadedPages;
	private int numberOfDownloadedResources;
	private int numberOfActiveHtmlDownloaderThreads;
	private int numberOfActiveResDownloaderThreads;
	private int numberOfActiveAnalyzerThreads;
	private int numberOfActiveRepeatedCheckerThreads;
	private int numberOfActiveFileSaverThreads;
	
	public int getNumberOfDownloadedPages() {
		return numberOfDownloadedPages;
	}
	public int getNumberOfActiveHtmlDownloaderThreads() {
		return numberOfActiveHtmlDownloaderThreads;
	}
	public int getNumberOfActiveAnalyzerThreads() {
		return numberOfActiveAnalyzerThreads;
	}
	public int getNumberOfActiveRepeatedCheckerThreads() {
		return numberOfActiveRepeatedCheckerThreads;
	}
	
	public void addDownloadedPage(){
		numberOfDownloadedPages++;
	}
	
	public void addDownloadedResource(){
		numberOfDownloadedResources++;
	}
	
	public void subDownloadedPage(){
		numberOfDownloadedPages--;
	}
	
	public void addHtmlDownloaderThread(){
		numberOfActiveHtmlDownloaderThreads++;
	}
	
	public void subHtmlDownloaderThread(){
		numberOfActiveHtmlDownloaderThreads--;
	}
	
	public void addResDownloaderThread(){
		numberOfActiveResDownloaderThreads++;
	}
	
	public void subResDownloaderThread(){
		numberOfActiveResDownloaderThreads--;
	}
	
	public void addAnalyzerThread(){
		numberOfActiveAnalyzerThreads++;
	}
	
	public void subAnalyzerThread(){
		numberOfActiveAnalyzerThreads--;
	}
	
	public void addRepeatedCheckerThread(){
		numberOfActiveRepeatedCheckerThreads++;
	}
	
	public void subRepeatedCheckerThread(){
		numberOfActiveRepeatedCheckerThreads--;
	}
	
	@Override
	public String toString(){
		return 	"ESTADISTICAS: \n" + 
				"CANTIDAD DE PAG BAJADAS: " + numberOfDownloadedPages + "\n" +
				"CANTIDAD DE RESOURCES BAJADOS: " + numberOfDownloadedResources + "\n" +
				"CANTIDAD DE HTML DOWNLOADERS ACTIVOS " + numberOfActiveHtmlDownloaderThreads + "\n"+
				"CANTIDAD DE RES DOWNLOADERS ACTIVOS " + numberOfActiveResDownloaderThreads + "\n"+
				"CANTIDAD DE ANALYZERS ACTIVOS " + numberOfActiveAnalyzerThreads + "\n" +
				"CANTIDAD DE REPEAT CHECKERS ACTIVOS " + numberOfActiveRepeatedCheckerThreads + "\n" +
				"CANTIDAD DE FILE SAVERS ACTIVOS " + numberOfActiveFileSaverThreads + "\n";
	}
	
	public void addFileSaverThread() {
		numberOfActiveFileSaverThreads++;
	}
	public void subFileSaverThread() {
		numberOfActiveFileSaverThreads--;
	}
	
}
