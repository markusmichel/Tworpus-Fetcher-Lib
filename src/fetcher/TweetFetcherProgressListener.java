package fetcher;

import twitter.Tweet;

public interface TweetFetcherProgressListener {

	public void onProgressUpdate();
	public void onStartDownloading();
	public void onStopDownloading(FetcherResult result);
	public void onTweetNotExsitingError(Tweet tweet);
	
}
