/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tworpus.client.xml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.*;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class XMLFileMerger {
    
    /** Directory conatining all XML files per tweet */
    private File directory;
    
    /** XML file which will contain the merged content */
    private File xmlFile;
    
    /** XML representation of the merged content */
    private Document doc;
    
    /** Root element of the merged document */
    private Element rootElement;
    
    /** List of listeners */
    private List<XMLMergerUpdateListener> updateListeners;

    
    /**
     * 
     * @param dir Direcotry which contains XML files to merge
     * @param xmlFile File which will contain the merged content. The file does
     * not have to exist before. If it does, it will be removed and recreated,
     * @throws NoSuchDirectoryException Throws exception of the directory does not exist
     */
    public XMLFileMerger(File dir, File xmlFile) throws NoSuchDirectoryException {
        directory = dir;
        updateListeners = new LinkedList<XMLMergerUpdateListener>();
        if (!directory.isDirectory()) {
            throw new NoSuchDirectoryException("Directory " + directory.getName() + " does not exist");
        }
    }
    
    public void addUpdateListener(XMLMergerUpdateListener listener) {
        updateListeners.add(listener);
    }

    public void openFile(File file) {
        xmlFile = file;
        if(xmlFile.exists()) xmlFile.delete();
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            doc = docBuilder.newDocument();
            doc.setXmlVersion("1.0");
            doc.setXmlStandalone(true);

            rootElement = doc.createElement("tweets");
            doc.appendChild(rootElement);
            
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(XMLFileMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void raiseProgressUpdateEvent(float percent) {
        for(XMLMergerUpdateListener listener : updateListeners) {
            listener.onXmlMergerProgressChanged(percent);
        }
    }

    public void merge() {
        try {
            DocumentBuilderFactory factory =
            DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
        
        
            PrintWriter writer = new PrintWriter(xmlFile, "UTF-8");
            File[] xmlFiles = directory.listFiles();
            int filesProcessed = 0;
            for (File f : xmlFiles) {
            	System.out.println("open file: " + f.getName());
                Document tweetDoc = builder.parse(f);
                Element tweetElement = tweetDoc.getDocumentElement();
                Node newNode = doc.importNode(tweetElement, true);
                rootElement.appendChild(newNode);
                
                filesProcessed++;
                raiseProgressUpdateEvent((float)filesProcessed / xmlFiles.length);
            }
            FileUtils.deleteDirectory(directory);

        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(XMLFileMerger.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
        }

    }
    
    public void save() {
        try {
            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            //transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            
            DOMSource source = new DOMSource(doc);
            StreamResult result =  new StreamResult(xmlFile);
            transformer.transform(source, result);
        } catch (TransformerConfigurationException ex) {
            Logger.getLogger(XMLFileMerger.class.getName()).log(Level.SEVERE, null, ex);
        } catch (TransformerException ex) {
            Logger.getLogger(XMLFileMerger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
