import java.io.File;

import tworpus.client.xml.TweetsFetcher;


public class FetcherApplication {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Start fetcher");
		
		if(args.length < 1) {
			System.out.println("Insufficient arguments");
			System.exit(0);
		}
		
		String strFilename = args[0];
		String strOutFolder = "";
		if(args.length >= 2) strOutFolder = args[1];
		
		System.out.println("Filename: " + strFilename);
		System.out.println("outputFolder: " + strOutFolder);
		
		
		File outFolder = new File(strOutFolder);
		File tweetsFile = new File(strFilename);
		
		if(!tweetsFile.exists()) {
			System.out.println("File does not exist");
			System.exit(0);
		}
		
		if(!outFolder.exists()) {
			outFolder.mkdirs();
		}
		
		TweetsFetcher fetcher = new TweetsFetcher(outFolder, tweetsFile);
		
	}

}
