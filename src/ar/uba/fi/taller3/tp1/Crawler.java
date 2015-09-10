package ar.uba.fi.taller3.tp1;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import ar.uba.fi.taller3.tp1.monitor.Monitor;
import ar.uba.fi.taller3.tp1.monitor.events.Event;

public class Crawler {

	private static int DOWNLOADERS_AMOUNT = 30;
	private static int SRC_AMOUNT = 2;
	
	public static void main(String[] args) {
		
		LinkedBlockingQueue<UrlRequest> inputUrlQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<UrlRequest> downloadUrlQueue =  new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<Document> docQueue = new LinkedBlockingQueue<Document>();
		LinkedBlockingQueue<UrlRequest> inputResQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<UrlRequest> downloadResQueue = new LinkedBlockingQueue<UrlRequest>();
		LinkedBlockingQueue<Event> monitorQueue = new LinkedBlockingQueue<Event>();
		LinkedBlockingQueue<Document> saveQueue = new LinkedBlockingQueue<Document>();
		System.out.println("Welcome to crawler");
		Executor repeatedExecutor = Executors.newFixedThreadPool(5);
		
		Thread t1 = new Thread(new RepeatedChecker(inputUrlQueue, downloadUrlQueue, monitorQueue));
		//Thread t2 = new Thread(new Downloader(downloadUrlQueue, docQueue, monitorQueue));
		Thread t3 =  new Thread(new Analyzer(docQueue, inputUrlQueue, inputResQueue, monitorQueue));
		Thread t4 =  new Thread(new Monitor(monitorQueue));
		Executor downloaderHtmlExecutor = Executors.newFixedThreadPool(DOWNLOADERS_AMOUNT);
		for(int i=0; i<DOWNLOADERS_AMOUNT; i++){
			downloaderHtmlExecutor.execute(new HtmlDownloader(downloadUrlQueue, saveQueue,docQueue, monitorQueue));
		}
		Executor downloaderSrcExecutor = Executors.newFixedThreadPool(SRC_AMOUNT);
		for (int i = 0; i< SRC_AMOUNT;i++){
			downloaderSrcExecutor.execute(new ResourceDownloader(downloadResQueue, saveQueue, monitorQueue));
		}
		Thread t5 = new Thread(new RepeatedChecker(inputResQueue, downloadResQueue, monitorQueue));
		Thread t6 = new Thread(new FileSaver(saveQueue, monitorQueue));
		t1.start();
		//t2.start();
		t3.start();
		t4.start();
		t5.start();
		t6.start();
		String urlString = "http://www.google.com";
		try {
			inputUrlQueue.add(new UrlRequest(new URL(urlString),0));
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
