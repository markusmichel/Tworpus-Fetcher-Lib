package csv;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;

public class CsvFileIterator<type> implements Iterator<String> {

	private FileManager fileManager;
	private LineIterator lineIterator;

	public CsvFileIterator(FileManager fileManager) {
		this.fileManager = fileManager;
		
		try {
			lineIterator = IOUtils.lineIterator(new BufferedReader(
			        new FileReader(fileManager.getInputFile())));
			
			if(!fileManager.csvHasTitleLine() && lineIterator.hasNext()) lineIterator.next();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean hasNext() {
		return lineIterator.hasNext();
	}

	@Override
	public String next() {
		return lineIterator.next();
	}

	@Override
	public void remove() {
		lineIterator.remove();
	}
	
	

}
