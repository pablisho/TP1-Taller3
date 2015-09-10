package ar.uba.fi.taller3.tp1;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.controllers.Analyzer;
import ar.uba.fi.taller3.tp1.controllers.FileSaver;
import ar.uba.fi.taller3.tp1.controllers.HtmlDownloader;
import ar.uba.fi.taller3.tp1.controllers.RepeatedChecker;
import ar.uba.fi.taller3.tp1.controllers.ResourceDownloader;
import ar.uba.fi.taller3.tp1.domain.Document;
import ar.uba.fi.taller3.tp1.domain.UrlRepository;
import ar.uba.fi.taller3.tp1.domain.UrlRequest;
import ar.uba.fi.taller3.tp1.monitor.Monitor;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class Crawler {

	private static String HTML_AMOUNT = "html_downloaders";
	private static String SRC_AMOUNT = "src_downloaders";
	private static String ANALYZERS_AMOUNT = "analyzers";
	private static String FILE_SAVERS = "file_savers";
	private static String HTML_CHECKERS = "repeat_html_checker";
	private static String SRC_CHECKERS = "repeat_src_checker";
	private static String FILE_LOCATION = "files_location";
	private static String URL_HTML_FILE = "html_urls";
	private static String URL_SRC_FILE = "src_urls";
	private static String MONITOR_PERIOD = "monitor_period";
	
	private int htmlDownloaders;
	private int srcDownloaders;
	private int analyzers;
	private int fileSavers;
	private int htmlCheckers;
	private int srcCheckers;
	private String fileLocation;
	private String htmlUrlLocation;
	private String srcUrlLocation;
	private long monitorPeriod;
	
	private LinkedBlockingQueue<UrlRequest> inputUrlQueue = new LinkedBlockingQueue<UrlRequest>();
	private LinkedBlockingQueue<UrlRequest> downloadUrlQueue = new LinkedBlockingQueue<UrlRequest>();
	private LinkedBlockingQueue<Document> docQueue = new LinkedBlockingQueue<Document>();
	private LinkedBlockingQueue<UrlRequest> inputResQueue = new LinkedBlockingQueue<UrlRequest>();
	private LinkedBlockingQueue<UrlRequest> downloadResQueue = new LinkedBlockingQueue<UrlRequest>();
	private LinkedBlockingQueue<Event> monitorQueue = new LinkedBlockingQueue<Event>();
	private LinkedBlockingQueue<Document> saveQueue = new LinkedBlockingQueue<Document>();
	
	private UrlRepository htmlUrlRepository = new UrlRepository(htmlUrlLocation);
	private UrlRepository srcUrlRepository = new UrlRepository(srcUrlLocation);
	
	private Executor htmlCheckerExecutor;
	private Executor downloaderHtmlExecutor;
	private Executor analyzerExecutor;
	private Executor fileSaversExecutor;
	private Executor downloaderSrcExecutor;
	private Executor srcCheckerExecutor;
	private Thread monitorThread;

	public static void main(String[] args) {
		Log.log("Welcome to crawler");
		Crawler crawler = new Crawler();
		crawler.loadProperties();
		crawler.startThreads();
		crawler.startCrawling();
		crawler.join();
		Log.log("Finishing");
	}
	
	private void loadProperties(){
		Properties properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("./config.properties");
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e1) {
			Log.log("Could not load properties.");
			e1.printStackTrace();
			return;
		} catch (IOException e) {
			e.printStackTrace();
			Log.log("Could not load properties.");
			return;
		}

		htmlDownloaders = Integer.parseInt(properties
				.getProperty(HTML_AMOUNT));
		srcDownloaders = Integer.parseInt(properties
				.getProperty(SRC_AMOUNT));
		analyzers = Integer.parseInt(properties.getProperty(ANALYZERS_AMOUNT));
		fileSavers = Integer.parseInt(properties.getProperty(FILE_SAVERS));
		htmlCheckers = Integer.parseInt(properties.getProperty(HTML_CHECKERS));
		srcCheckers = Integer.parseInt(properties.getProperty(SRC_CHECKERS));
		fileLocation = properties.getProperty(FILE_LOCATION);
		htmlUrlLocation = properties.getProperty(URL_HTML_FILE);
		srcUrlLocation = properties.getProperty(URL_SRC_FILE);
		monitorPeriod = Long.parseLong(properties.getProperty(MONITOR_PERIOD));
	}
	
	private void startThreads(){
		htmlUrlRepository = new UrlRepository(htmlUrlLocation);
		srcUrlRepository = new UrlRepository(srcUrlLocation);
		
		htmlCheckerExecutor = Executors.newFixedThreadPool(htmlCheckers);
		downloaderHtmlExecutor = Executors.newFixedThreadPool(htmlDownloaders);
		analyzerExecutor = Executors.newFixedThreadPool(analyzers);
		fileSaversExecutor = Executors.newFixedThreadPool(fileSavers);
		downloaderSrcExecutor = Executors.newFixedThreadPool(srcDownloaders);
		srcCheckerExecutor = Executors.newFixedThreadPool(srcCheckers);
		
		for (int i=0; i< htmlCheckers; i++){
			htmlCheckerExecutor.execute(new RepeatedChecker(inputUrlQueue,
					downloadUrlQueue, monitorQueue,htmlUrlRepository));
		}
		for (int i= 0; i<analyzers;i++){
			analyzerExecutor.execute(new Analyzer(docQueue, inputUrlQueue,
					inputResQueue, monitorQueue));
		}
		for (int i = 0; i < htmlDownloaders; i++) {
			downloaderHtmlExecutor.execute(new HtmlDownloader(downloadUrlQueue,
					saveQueue, docQueue, monitorQueue));
		}
		for (int i = 0; i < srcDownloaders; i++) {
			downloaderSrcExecutor.execute(new ResourceDownloader(
					downloadResQueue, saveQueue, monitorQueue));
		}
		for(int i=0; i < srcCheckers; i++){
			srcCheckerExecutor.execute(new RepeatedChecker(inputResQueue,
					downloadResQueue, monitorQueue,srcUrlRepository));
		}
		for(int i=0; i< fileSavers; i++){
			fileSaversExecutor.execute(new FileSaver(saveQueue, monitorQueue,fileLocation));
		}
		
		monitorThread = new Thread(new Monitor(monitorQueue,monitorPeriod));
		monitorThread.start();
	}
	
	private void startCrawling(){
		String urlString = "http://www.google.com";
		try {
			inputUrlQueue.add(new UrlRequest(new URL(urlString), 0));
		} catch (MalformedURLException e) {
			Log.log("La URL no fue generada correctamene");
			e.printStackTrace();
		}
	}
	
	private void join(){
		try {
			monitorThread.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	

}
