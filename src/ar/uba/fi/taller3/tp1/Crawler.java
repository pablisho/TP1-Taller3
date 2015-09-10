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

	public static void main(String[] args) {

		Properties properties = new Properties();
		FileInputStream in;
		try {
			in = new FileInputStream("./config.properties");
			properties.load(in);
			in.close();
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			System.out.println("Could not load properties.");
			e1.printStackTrace();
			return;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			System.out.println("Could not load properties.");
			return;
		}

		int htmlDownloaders = Integer.parseInt(properties
				.getProperty(HTML_AMOUNT));
		int srcDownloaders = Integer.parseInt(properties
				.getProperty(SRC_AMOUNT));
		int analyzers = Integer.parseInt(properties.getProperty(ANALYZERS_AMOUNT));
		int fileSavers = Integer.parseInt(properties.getProperty(FILE_SAVERS));
		int htmlCheckers = Integer.parseInt(properties.getProperty(HTML_CHECKERS));
		int srcCheckers = Integer.parseInt(properties.getProperty(SRC_CHECKERS));
		String fileLocation = properties.getProperty(FILE_LOCATION);
		String htmlUrlLocation = properties.getProperty(URL_HTML_FILE);
		String srcUrlLocation = properties.getProperty(URL_SRC_FILE);
		
		LinkedBlockingQueue<UrlRequest> inputUrlQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<UrlRequest> downloadUrlQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<Document> docQueue = new LinkedBlockingQueue<Document>();
		LinkedBlockingQueue<UrlRequest> inputResQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<UrlRequest> downloadResQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<Event> monitorQueue = new LinkedBlockingQueue<Event>();
		LinkedBlockingQueue<Document> saveQueue = new LinkedBlockingQueue<Document>();
		System.out.println("Welcome to crawler");
		
		UrlRepository htmlUrlRepository = new UrlRepository(htmlUrlLocation);
		UrlRepository srcUrlRepository = new UrlRepository(srcUrlLocation);
		
		Executor htmlCheckerExecutor = Executors.newFixedThreadPool(htmlCheckers);
		Executor downloaderHtmlExecutor = Executors.newFixedThreadPool(htmlDownloaders);
		Executor analyzerExecutor = Executors.newFixedThreadPool(analyzers);
		Executor fileSaversExecutor = Executors.newFixedThreadPool(fileSavers);
		Executor downloaderSrcExecutor = Executors.newFixedThreadPool(srcDownloaders);
		Executor srcCheckerExecutor = Executors.newFixedThreadPool(srcCheckers);
		
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
		
		Thread t1 = new Thread(new Monitor(monitorQueue));
		t1.start();
		
		String urlString = "http://www.google.com";
		try {
			inputUrlQueue.add(new UrlRequest(new URL(urlString), 0));
		} catch (MalformedURLException e) {
			System.out.println("La URL no fue generada correctamene");
			e.printStackTrace();
		}
		try {
			t1.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Finishing");
	}

}
