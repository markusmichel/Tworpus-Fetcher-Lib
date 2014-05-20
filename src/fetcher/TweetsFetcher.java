/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package fetcher;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import csv.FileManager;
import csv.FileManagerUpdateListener;
import twitter.Tweet;
import tworpus.client.xml.NoSuchDirectoryException;
import tworpus.client.xml.XMLFileMerger;

public class TweetsFetcher  implements FileManagerUpdateListener, TweetFetcherProgressListener {

    private int max_threads = 30;
    
    private File cacheFolder;
    private File outputFolder;
    private ExecutorService executor;
    
    private long tweetsToFetch = 0;
    private int tweetsFetched = 0;
    private int tweetsFailed = 0;
	private FileManager fileManager;
    
    public static final String MERGED_XML_FILE_NAME = "tweets.xml";

    
    public TweetsFetcher(File cacheFolder, File outputFolder, File tweetsFile) {
    	this(cacheFolder, outputFolder, tweetsFile, false, false);
    }
    
    public TweetsFetcher(File cacheFolder, File outputFolder, File tweetsFile, boolean overrideExistingFiles, boolean csvStartFirstLine) {
    	fileManager = new FileManager(tweetsFile, this, cacheFolder.getAbsolutePath(), outputFolder.getAbsolutePath(), csvStartFirstLine);
    	executor = Executors.newFixedThreadPool(max_threads);
    	
    	tweetsToFetch = fileManager.getLineCount();
        this.cacheFolder = cacheFolder;
        this.outputFolder = outputFolder;
        
    	for(String lineStr : fileManager) {
    		TweetFetcher fetcher = new TweetFetcher(this, fileManager, lineStr, overrideExistingFiles);
    		executor.execute(fetcher);
//    		fetcher.run();
    	}
    }

    @Override
    public void onFileManagerError(String error) {
        System.out.println("File manager error");
    }

    @Override
    public void onProgressUpdate() {
    }

    @Override
    public void onStartDownloading() {
    }

    @Override
    public synchronized void onStopDownloading(FetcherResult result) {
        if(result.isSuccess()) tweetsFetched++; else tweetsFailed++;
        
      	System.out.println(createStatusMessage(result));
        
        // All threads are finished --> download finished
        if(tweetsToFetch == tweetsFetched+tweetsFailed) {
            mergeXmlFiles();
            
            // @TODO: really exit hard?
            System.exit(0);
        }
    }
    
    @Override
    public void onTweetNotExsitingError(Tweet tweet) {
        if(tweet == null) return;
//        executor.execute(new TweetUnavailableNotifier(tweet));
    }
    
    private String createStatusMessage(FetcherResult result) {
    	String message;
    	if(result.isSuccess()) {
    		message = "success";
    	}
    	else {
    		message = "error";
    	}
    	
    	StringBuilder builder = new StringBuilder();
    	builder
    		.append("Fetch:")
    		.append("result=").append(message)
    		.append(",succeeded=").append(tweetsFetched)
    		.append(",failed=").append(tweetsFailed)
    		.append(",source=").append(result.getSource())
    		.append(",total=").append(tweetsToFetch);
    	return builder.toString();
    }

	private void mergeXmlFiles() {
		File xmlDir = cacheFolder;
		File newFile = new File(outputFolder.getAbsolutePath() + File.separator + MERGED_XML_FILE_NAME);
		
		try {
		    XMLFileMerger merger = new XMLFileMerger(xmlDir, null, fileManager);
		    merger.openFile(newFile);
		    merger.merge();
		    merger.save();
		    
		    System.out.println("finished merging");

		} catch (NoSuchDirectoryException ex) {
		    ex.printStackTrace();
		}
	}
    
    
    class TweetUnavailableNotifier implements Runnable {
    	
    	private Tweet tweet;

		public TweetUnavailableNotifier(Tweet tweet) {
    		this.tweet = tweet;
    	}
    	
		@Override
		public void run() {
	        try {
				URL url = new URL("http://132.199.139.24:3000/api/v1/unavailable?tweetid=" + tweet.getId() + "&userid=" + tweet.getUser_id());
				HttpURLConnection con = (HttpURLConnection) url.openConnection();
				con.getInputStream();
				
			} catch(MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
    }
}