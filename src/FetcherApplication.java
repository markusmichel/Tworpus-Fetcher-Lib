import java.io.File;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.BooleanOptionHandler;

import fetcher.TweetsFetcher;

public class FetcherApplication {
	
	@Option(name="-override", usage="Override existing files", required=false)
	private boolean overrideExistingFiles;
	
	@Option(name="-csv-no-title", usage="Csv file contains title line", required=false)
	private boolean csvStartFirstLine;
	
	@Option(name="-xml-cache-folder", usage="Location of the downloaded (unmerged) xml files", required=true)
	private String cacheFolderName;
	
	@Option(name="-xml-output-folder", usage="Folder where the merged XML file is saved", required=true)
	private String outputFolderName;
	
	@Option(name="-input-file", usage="Location and name of the input CSV file", required=true)
	private String inputCsvFile;
	
	public static void main(String[] args) {
		new FetcherApplication().start(args);		
	}

	private void start(String[] args) {
		CmdLineParser parser = new CmdLineParser(this);
		parser.setUsageWidth(80);
		
		try {
			parser.parseArgument(args);
			
			// Exit if input file does not exist
			if(!new File(inputCsvFile).exists()) {
				System.err.println("File " + inputCsvFile + " does not exist");
				return;
			}
			
		} catch(CmdLineException ex) {
			System.err.println(ex.getMessage());
			System.err.println("java SampleMain [options...] arguments...");
			
            // print the list of available options
            parser.printUsage(System.err);
            System.err.println();
            
            return;
		}
		
		File cacheFolder = new File(cacheFolderName);
		File outputFolder = new File(outputFolderName);
		File tweetsFile = new File(inputCsvFile);
		
		if(!cacheFolder.exists()) {
			cacheFolder.mkdirs();
		}
		
		if(!outputFolder.exists()) {
			outputFolder.mkdirs();
		}
		
		TweetsFetcher fetcher = new TweetsFetcher(cacheFolder, outputFolder, tweetsFile, overrideExistingFiles, csvStartFirstLine);
	}

}
