package fetcher;

import twitter.Tweet;

public interface TweetFetcherProgressListener {

	public void onProgressUpdate(long currentTweets, long allTweets, int id);
	public void onStartDownloading();
	public void onStopDownloading();
	public void onTweetNotExsitingError(Tweet tweet);
	
}
