package fetcher;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import twitter.Tweet;

public class TweetFetcher {

	private FileManager fileManager;
	private TweetFetcherProgressListener listener;

	private Thread fetcherThread;

	private long startWith = 1;
	private long stopWith = 10000;
	private long step = 10000;
	private boolean xml = true;
	private boolean plaintext = true;
	private long delay = 100;

	private int id;

	public TweetFetcher(TweetFetcherProgressListener listener,
			FileManager fileManager, int id) {
		this.fileManager = fileManager;
		this.listener = listener;
		this.id = id;
	}

	public void setParams(long startWith, long stopWith, long step,
			boolean xml, boolean plaintext, long delay) {
		this.startWith = startWith;
		this.stopWith = stopWith;
		this.step = step;
		this.xml = xml;
		this.plaintext = plaintext;
		this.delay = delay;
	}

	public void run() {
		if (fetcherThread == null) {
			fetcherThread = new Thread(new FetcherThread());
		}
		listener.onStartDownloading();
		fetcherThread.start();

	}

	class FetcherThread implements Runnable {

		@Override
		public void run() {
			for (long i = startWith; i <= stopWith; i++) {
				try {
					Tweet tweet = getTweetFromLine(i);
					if (tweet == null) {
						listener.onTweetNotExsitingError(null);
						continue;
					}
					fileManager.saveTweet(tweet, xml, plaintext);
					listener.onProgressUpdate(i - (id * step), stopWith, id);
					try {
						Thread.sleep(delay);
					} catch (InterruptedException ex) {
						Thread.currentThread().interrupt();
					}
				} catch (Exception e) {
					listener.onTweetNotExsitingError(null);
				}
			}
			listener.onStopDownloading();
		}

		private Tweet getTweetFromLine(long lineNumber) throws Exception {
			String line = fileManager.getLine(lineNumber);
			String[] values = line.split(",");
			for(int i=0; i<values.length; i++) {
				values[i] = values[i].replace("\"", "");
			}
			long id = 0;
			long user_id = 0;
			try {
				id = Long.valueOf(values[1]);
				user_id = Long.valueOf(values[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			String tweetlang = values[2];
			Tweet tweet = updateTweetFromWeb(new Tweet(id, user_id, tweetlang));
			return tweet;
		}

		private Tweet updateTweetFromWeb(Tweet tweet) {
			Document doc = null;

			try {
				doc = Jsoup.connect(tweet.getUrl()).get();
			} catch (IOException e) {
				listener.onTweetNotExsitingError(tweet);
				return null;
			}

			tweet.setDate(TweetDocHelper.getDate(doc));
			tweet.setText(TweetDocHelper.getText(doc));
			tweet.setCharCount(tweet.getText().length());
			tweet.setWordCount(tweet.getText().split(" ").length);
			tweet.setRetweets(TweetDocHelper.getRetweets(doc));
			tweet.setFavoured(TweetDocHelper.getFavourites(doc));
			tweet.setFullname(TweetDocHelper.getUserFullname(doc));
			tweet.setScreeenname(TweetDocHelper.getUserScreenname(doc));

			return tweet;
		}

	}

}
