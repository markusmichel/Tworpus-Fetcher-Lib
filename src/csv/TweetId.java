package csv;

public class TweetId {
	private long tweetId;
	private long userId;
	private String language;

	public TweetId(long tweetId, long userId, String language) {
		this.tweetId = tweetId;
		this.userId = userId;
		this.language = language;
	}
	
	public String getLanguage() {
		return language;
	}

	public long getTweetId() {
		return tweetId;
	}

	public long getUserId() {
		return userId;
	}
}
