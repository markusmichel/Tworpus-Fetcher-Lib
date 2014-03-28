package csv;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import twitter.Tweet;

public class FileManager implements Iterable<String> {

	private File inputFile;
	// private FileManagerUpdateListener listener;

	public static final int ADD = 0;
	public static final int OVERWRITE = 1;

	private String cacheFolderName;
	private String outputFolderName;
	private boolean csvStartFirstLine;

	public FileManager(File file, FileManagerUpdateListener listener,
			String cacheFolder, String outputFolder, boolean csvStartFirstLine) {
		this.cacheFolderName = cacheFolder;
		this.outputFolderName = outputFolder;
		this.inputFile = file;
		this.csvStartFirstLine = csvStartFirstLine;
	}

	public static TweetId createTweetId(String line) {
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i].replace("\"", "");
		}
		long id = 0;
		long user_id = 0;
		try {
			id = Long.valueOf(values[0]);
			user_id = Long.valueOf(values[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		String tweetlang = values[2];

		return new TweetId(id, user_id, tweetlang);
	}

	public boolean csvHasTitleLine() {
		return csvStartFirstLine;
	}

	public File getInputFile() {
		return inputFile;
	}

	public String getCacheFolderName() {
		return cacheFolderName;
	}

	public String getOutputFolderName() {
		return outputFolderName;
	}

	public boolean tweetExists(long tweetId, long userId) {
		return tweetExists(String.valueOf(tweetId), String.valueOf(userId));
	}

	public boolean tweetExists(String tweetId, String userId) {
		File folder = new File(cacheFolderName);
		File fileToSearch = new File(folder.getAbsolutePath() + File.separator
				+ tweetId + ".xml");

		return fileToSearch.exists();
	}

	public long getLineCount() {
		if (inputFile == null) {
			return -1;
		}
		try {
			return count();
		} catch (IOException e) {
			return -1;

		}
	}

	public String getLine(long position) {
		try {
			return getLineAtPosition(position);
		} catch (FileNotFoundException e) {
			return "";
		}
	}

	private String getLineAtPosition(long line) throws FileNotFoundException {
		LineIterator it = IOUtils.lineIterator(new BufferedReader(
				new FileReader(inputFile)));

		for (int lineNumber = 0; it.hasNext(); lineNumber++) {
			String result = (String) it.next();
			if (lineNumber == line) {
				return result;
			}
		}
		return "";
	}

	private long count() throws IOException {
		long i = 0;
		for (String s : this)
			i++;

		return i;
	}

	public void saveTweet(Tweet tweet) {
		saveTweetAsXML(tweet);
	}

	private void saveTweetAsXML(Tweet tweet) {
		String filename = cacheFolderName + File.separator + tweet.getId() + ".xml";		
		
        try {
        	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			Document doc = builder.newDocument();
			
			// Root node
			Element tweetNode = doc.createElement("tweet");
			tweetNode.setAttribute("id", String.valueOf(tweet.getId()));
			
			// User node
			Element userNode = doc.createElement("user");
			userNode.setAttribute("id", String.valueOf(tweet.getUser_id()));
			
			Element screenNameNode = doc.createElement("screenname");
			screenNameNode.setTextContent(tweet.getScreeenname());
			
			Element fullNameNode = doc.createElement("fullname");
			fullNameNode.setTextContent(tweet.getFullname());
			
			userNode.appendChild(screenNameNode).appendChild(fullNameNode);
			
			// Date node
			Element dateNode = doc.createElement("date");
			dateNode.setTextContent(tweet.getDate());
			
			// Retweets node
			Element retweetsNode = doc.createElement("retweets");
			retweetsNode.setTextContent(String.valueOf(tweet.getRetweets()));
			
			// Favoured node
			Element favouredNode = doc.createElement("favoured");
			favouredNode.setTextContent(String.valueOf(tweet.getFavoured()));
			
			// Text node
			Element textNode = doc.createElement("text");
			textNode.setTextContent(tweet.getText());
			
			
			// Append nodes
			tweetNode.appendChild(userNode).appendChild(dateNode).appendChild(retweetsNode).appendChild(favouredNode).appendChild(textNode);
			doc.appendChild(tweetNode);
			
			
			// Save document
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
			DOMSource source = new DOMSource(doc);

			StreamResult result =  new StreamResult(new File(filename));
			transformer.transform(source, result);
			
			
		} catch (ParserConfigurationException | TransformerException e) {
			e.printStackTrace();
		}
        
        
		
	}

	private void writeStringToFile(String filename, String text, int mode) {
		switch (mode) {
		case OVERWRITE:
			writeToNewFile(filename, text);
			break;
		case ADD:
			break;
		default:
			break;
		}
	}

	private void writeToNewFile(String filename, String text) {
//		try {
//			FileWriter fstream;
//			fstream = new FileWriter(filename);
//			BufferedWriter out = new BufferedWriter(fstream);
//			out.write(text);
//			out.close();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}

		Writer out = null;
		try {
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
			out.write(text);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private boolean createFolder(String folder) {
		File theDir = new File(folder);
		if (!theDir.exists()) {
			boolean result = theDir.mkdir();
			return result;
		} else {
			return true;
		}
	}

	@Override
	public Iterator iterator() {
		return new CsvFileIterator<String>(this);
	}

}
