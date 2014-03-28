package fetcher;

/**
 * Indicator for TweetFetcher.
 * Holds information whether a tweet was successfully fetched
 * from which source it has been retrieved.
 * Defaults to not successfull and web as source.
 */
public class FetcherResult {
	
	private boolean success = false;
	private FetcherSource source = FetcherSource.WEB;
	
	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public FetcherSource getSource() {
		return source;
	}

	public void setSource(FetcherSource source) {
		this.source = source;
	}
	
	public String toString() {
		return "successful: " + isSuccess() + " source " + getSource();
	}

	enum FetcherSource {
		WEB,
		CACHE
	}
}
