/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package tworpus.client.xml;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import twitter.Tweet;
import fetcher.FileManager;
import fetcher.FileManagerUpdateListener;
import fetcher.TweetFetcher;
import fetcher.TweetFetcherProgressListener;

public class TweetsFetcher  implements FileManagerUpdateListener, TweetFetcherProgressListener {

    private int max_threads = 30;
    private TweetFetcher[] fetchers = new TweetFetcher[max_threads];
    private int startedThreads = 0;
    private int finishedThreads = 0;
    private File outFolder;

    
    public TweetsFetcher(File outFolder, File tweetsFile) {
    	FileManager manager = new FileManager(tweetsFile, this, outFolder.getAbsolutePath());
    	long start_with = 1;
        long tweets_to_fetch = manager.getLineCount();
        this.outFolder = outFolder;

        long step = (tweets_to_fetch - start_with) / max_threads;

        for (int i = 1; i < max_threads; i++) {
            fetchers[i] = new TweetFetcher(this, manager, i);
            fetchers[i].setParams(i * step, i * step + step, step, true, true, 0);
            fetchers[i].run();
        }
        
        ExecutorService executor = Executors.newCachedThreadPool();
    }
    
    public TweetsFetcher(String outFolderPath, String tweetsFilePath) {
    	File tweetsFile = new File(tweetsFilePath);
    	File outFolder = new File(outFolderPath);
    }

    @Override
    public void onFileManagerError(String error) {
        System.out.println("File manager error");
    }

    @Override
    public void onProgressUpdate(long currentTweets, long allTweets, int id) {
        System.out.println("progress update");
    }

    @Override
    public void onStartDownloading() {
        System.out.println("Start Downloading");
        startedThreads++;
    }

    @Override
    public void onStopDownloading() {
        System.out.println("Stop Downloading");
        finishedThreads++;
        
        // All threads are finished --> download finished
        if(finishedThreads == startedThreads) {
            System.out.println("FINISHED DOWNLOAD --> merge");
            mergeXmlFiles();
        }
    }

	private void mergeXmlFiles() {
		File projectDir = outFolder;
		File xmlDir = new File(projectDir.getAbsolutePath() + File.separator + "xml");
		File newFile = new File(projectDir.getAbsolutePath() + File.separator + "tweets.xml");
		
		try {
		    XMLFileMerger merger = new XMLFileMerger(xmlDir, null);
		    File[] xmlFiles = xmlDir.listFiles();

		    //newFile.getParentFile().mkdirs();
		    //newFile.createNewFile();

		    merger.openFile(newFile);
		    merger.merge();
		    merger.save();
		    
		    System.out.println("finished merging");

		} catch (NoSuchDirectoryException ex) {
		    ex.printStackTrace();
		}
	}

    @Override
    public void onTweetNotExsitingError(Tweet tweet) {
        System.out.println("NO TWEET EXISTS");
    }
}