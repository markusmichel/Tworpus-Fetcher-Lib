package fetcher;

import java.io.IOException;
import java.net.SocketTimeoutException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import twitter.Tweet;
import csv.FileManager;
import csv.TweetId;
import fetcher.FetcherResult.FetcherSource;

public class TweetFetcher implements Runnable {

	private FileManager fileManager;
	private TweetFetcherProgressListener listener;
	
	// @TODO: perhaps pass tweet in constructor instatead of passing line number and fetching the line manually
	private String lineStr;
	private boolean overrideExistingFile;
	
	private FetcherResult result;
	
	private int timeoutCounter = 0;

	public TweetFetcher(TweetFetcherProgressListener listener, FileManager fileManager, String lineStr, boolean overrideExistingFile) {
		this.fileManager = fileManager;
		this.listener = listener;
		this.lineStr = lineStr;
		this.overrideExistingFile = overrideExistingFile;
		
		result = new FetcherResult();
        result.setSuccess(false);
	}

	public void run() {
		listener.onStartDownloading();
		saveTweet();
		listener.onStopDownloading(result);
	}

	private void saveTweet() {
		Tweet tweet = null;
		try {
			tweet = getTweetFromLine(lineStr);
			
			// Stop working if tweet already exists and fetcher shall not override
			if(!overrideExistingFile && fileManager.tweetExists(tweet.getId(), tweet.getUser_id())) {
				result.setSource(FetcherSource.CACHE);
				result.setSuccess(true);
				return;
			}
			
			tweet = updateTweetFromWeb(tweet);

			fileManager.saveTweet(tweet);
			listener.onProgressUpdate();
			result.setSuccess(true);
			
		} catch(SocketTimeoutException ex) {
			if(++timeoutCounter <= 5) saveTweet();
			else listener.onTweetNotExsitingError(tweet);
		} catch(IOException ex) {
			listener.onTweetNotExsitingError(tweet);
		} catch (Exception e) {
			listener.onTweetNotExsitingError(tweet);
		}
	}
	
	private Tweet getTweetFromLine(String line) throws Exception {
		TweetId tweetId	= FileManager.createTweetId(line);
		return new Tweet(tweetId.getTweetId(), tweetId.getUserId(), tweetId.getLanguage());
	}

	private Tweet updateTweetFromWeb(Tweet tweet) throws IOException {
		result.setSource(FetcherSource.WEB);
		Document doc = null;

		doc = Jsoup.connect(tweet.getUrl()).get();

		tweet.setDate(TweetDocHelper.getDate(doc));
		tweet.setText(TweetDocHelper.getText(doc));
		tweet.setCharCount(tweet.getText().length());
		tweet.setWordCount(tweet.getText().split(" ").length);
		tweet.setRetweets(TweetDocHelper.getRetweets(doc));
		tweet.setFavoured(TweetDocHelper.getFavourites(doc));
		tweet.setFullname(TweetDocHelper.getUserFullname(doc));
		tweet.setScreeenname(TweetDocHelper.getUserScreenname(doc));
		
		System.out.println(tweet.getText());

		return tweet;
	}
}
