package twitter;

import org.apache.commons.lang3.StringEscapeUtils;

public class Tweet {

	private long id = -1;
	private long user_id = -1;
	private String screeenname;
	private String fullname;
	private int retweets = 0;
	private int favoured = 0;
	private int charCount = 0;
	private int wordCount = 0;
	private String tweetlang = "";
	private String userlang = "";
	private String date = "";
	private int mentionedEntities = 0;	
	private String text = "";
	
	public final static String XML_HEADER = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" standalone=\"yes\"?>";

	public Tweet(long id, long user_id, String tweetlang) {
		this.id = id;
		this.user_id = user_id;
		this.tweetlang = tweetlang;
	}

	public String getScreeenname() {
		return screeenname;
	}

	public void setScreeenname(String screeenname) {
		this.screeenname = screeenname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getTweetlang() {
		return tweetlang;
	}

	public void setTweetlang(String tweetlang) {
		this.tweetlang = tweetlang;
	}

	public String getUserlang() {
		return userlang;
	}

	public void setUserlang(String userlang) {
		this.userlang = userlang;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getMentionedEntities() {
		return mentionedEntities;
	}

	public void setMentionedEntities(int mentionedEntities) {
		this.mentionedEntities = mentionedEntities;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public int getRetweets() {
		return retweets;
	}

	public void setRetweets(int retweets) {
		this.retweets = retweets;
	}

	public int getFavoured() {
		return favoured;
	}

	public void setFavoured(int favoured) {
		this.favoured = favoured;
	}

	public int getCharCount() {
		return charCount;
	}

	public void setCharCount(int charCount) {
		this.charCount = charCount;
	}

	public int getWordCount() {
		return wordCount;
	}

	public void setWordCount(int wordCount) {
		this.wordCount = wordCount;
	}

	public String getUrl() {
		return createUrl(getId(), getUser_id());
	}
	
	public static String createUrl(long tweetId, long userId) {
		return "http://twitter.com/" + userId + "/status/" + tweetId;
	}
	
	public String toString() {
		return "["+id+"]: " + text + "(retweets: " + retweets + ")";
	}
	
}
