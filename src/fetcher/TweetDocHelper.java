package fetcher;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class TweetDocHelper {
	
	public static String getText(Document doc) {
		Elements element = doc.select(".js-tweet-text.tweet-text");
		if (element.size() > 0) {
			return element.get(0).text();
		} else {
			return null;
		}
	}
	
	public static String getUserFullname(Document doc) {
		Elements element = doc.select(".fullname.js-action-profile-name");

		if (element.size() > 0) {
			return element.get(0).text();
		} else {
			return null;
		}
	}
	
	public static String getUserScreenname(Document doc) {
		Elements element = doc.select(".username.js-action-profile-name");
		if (element.size() > 0) {
			return element.get(0).text();
		} else {
			return null;
		}
	}
	

	public static int getRetweets(Document doc) {
		Elements element = doc.select(".request-retweeted-popup");
		if(element == null) return 0;
		if (element.size() > 0) {
			try {
				String tmp = element.attr("data-activity-popup-title");
				int start = 0;
				int end = 0;
				while (!Character.isDigit(tmp.charAt(start)))
					start++;
				tmp = tmp.substring(start);
				end = tmp.indexOf(" ");
				return Integer.valueOf(tmp.substring(0, end).replaceAll(", ","").replaceAll(".", ""));
			} catch (Exception e) {
				return 0;
			}

		}
		return 0;
	}

	
	public static int getFavourites(Document doc) {
		Elements element = doc.select(".request-favorited-popup");
		if(element == null) return 0;
		if (element.size() > 0) {
			try {
				String tmp = element.attr("data-activity-popup-title");
				int start = 0;
				int end = 0;
				while (!Character.isDigit(tmp.charAt(start)))
					start++;
				tmp = tmp.substring(start);
				end = tmp.indexOf(" ");
				return Integer.valueOf(tmp.substring(0, end).replaceAll(", ","").replaceAll(".", ""));
			} catch (Exception e) {
				return 0;
			}

		}
		return 0;
	}
	
	public static String getDate(Document doc) {
		Element element = doc.select(".tweet-timestamp ").first();
		
		if (element != null) {
			try {
				String tmp = element.attr("title").replace("-", "").replace("  ", " ");
				return tmp;
			} catch (Exception e) {
				e.printStackTrace();
				return "";
			}

		}
		return "";
	}

}
