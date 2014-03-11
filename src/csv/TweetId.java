package csv;

public class TweetId {
	private long tweetId;
	private long userId;

	public TweetId(long tweetId, long userId) {
		this.tweetId = tweetId;
		this.userId = userId;
	}

	public long getTweetId() {
		return tweetId;
	}

	public long getUserId() {
		return userId;
	}
}
