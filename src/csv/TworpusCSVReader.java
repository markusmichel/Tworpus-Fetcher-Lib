package csv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class TworpusCSVReader {
	
	/**
	 * Reads a csv file, extracts tweetIds and userIds (first two columns of each line)
	 * and returns them as a list. 
	 * @param file Input csv file
	 * @return
	 * @throws IOException If file not found
	 */
	public static List<TweetId> getIds(File file) throws IOException {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String line;
		String[] values;
		LinkedList<TweetId> ids = new LinkedList<>();
		TweetId tweetId;
		String splitBy = ",";
		
		while((line = reader.readLine()) != null) {
			System.out.println("Line: " + line);
			values = line.split(splitBy);
			
			tweetId = new TweetId(Long.parseLong(values[0]), Long.parseLong(values[1]));
			ids.add(tweetId);
		}
		
		return ids;
	}
	
	public static long countLines(File file) throws IOException {
        InputStream is = new BufferedInputStream(new FileInputStream(file));
        try {
            byte[] c = new byte[1024];
            long count = 0;
            int readChars = 0;
            boolean empty = true;
            while ((readChars = is.read(c)) != -1) {
                empty = false;
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return (count == 0 && !empty) ? 1 : count;
        } finally {
            is.close();
        }
    }
	
	public static String getLineAtPosition(long line, File file) throws FileNotFoundException {
        LineIterator it = IOUtils.lineIterator(new BufferedReader(
                new FileReader(file)));
        for (int lineNumber = 1; it.hasNext(); lineNumber++) {
            String result = (String) it.next();
            if (lineNumber == line) {
                return result;
            }
        }
        return "";
    }
}
