package fetcher;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

import twitter.Tweet;

public class FileManager {

    private File file;
    //private FileManagerUpdateListener listener;

    public String RESULT_DIR = "tweets";
    public static String RESULT_DIR_XML = "xml";
    public static String RESULT_DIR_PLAINTEXT = "text";
    public static String RESULT_LOG = "log.txt";

    public static final int ADD = 0;
    public static final int OVERWRITE = 1;

    private String corpus_id = "";

    public FileManager(File file, FileManagerUpdateListener listener) {
        this(file, listener, file.getName());
    }

    public FileManager(File file, FileManagerUpdateListener listener, String outputFolder) {
        this.file = file;
        corpus_id = outputFolder;
        RESULT_DIR = outputFolder;
        //this.listener = listener;
        
        createFolder(RESULT_DIR);
        if (createFolder(RESULT_DIR)) {
            boolean result = 
            		createFolder(RESULT_DIR + File.separator + RESULT_DIR_PLAINTEXT) & 
            		createFolder(RESULT_DIR + File.separator + RESULT_DIR_XML);
            
            if (!result) {
                listener.onFileManagerError("Can not create folder structure");
            }
        } else {
            listener.onFileManagerError("Can not create folder structure");
        }
    }

    public String getLogString() {
        File f = new File(RESULT_DIR + File.separator + corpus_id + File.separator + RESULT_LOG);
        if (f.exists()) {
            try {
                return getLineAtPosition(1, f);
            } catch (FileNotFoundException e) {
                return "1";
            }
        } else {
            return "1";
        }
    }

    public long getLineCount() {
        if (file == null) {
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
                new FileReader(file)));
        for (int lineNumber = 1; it.hasNext(); lineNumber++) {
            String result = (String) it.next();
            if (lineNumber == line) {
                return result;
            }
        }
        return "";
    }

    private String getLineAtPosition(long line, File file) throws FileNotFoundException {
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

    private long count() throws IOException {
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

    public void saveTweet(Tweet tweet, boolean xml, boolean plaintext) {
        if (xml) {
            saveTweetAsXML(tweet);
        }
        if (plaintext) {
            saveTweetAsPLainText(tweet);
        }
    }

    private void saveTweetAsPLainText(Tweet tweet) {
        writeStringToFile(RESULT_DIR + File.separator + RESULT_DIR_PLAINTEXT + File.separator + tweet.getId() + ".txt", tweet.getText(), OVERWRITE);
    }

    private void saveTweetAsXML(Tweet tweet) {
        writeStringToFile(RESULT_DIR + File.separator + RESULT_DIR_XML + File.separator + tweet.getId() + ".xml", tweet.getXML(), OVERWRITE);
    }

    public void writeLogString(String log) {
        writeStringToFile(RESULT_DIR + File.separator + RESULT_LOG, log, OVERWRITE);
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
        try {
            FileWriter fstream;
            fstream = new FileWriter(filename);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(text);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
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

}
