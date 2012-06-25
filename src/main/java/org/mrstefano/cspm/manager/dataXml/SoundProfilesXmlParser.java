package org.mrstefano.cspm.manager.dataXml;

import java.io.Reader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.mrstefano.cspm.model.SoundProfilesData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class SoundProfilesXmlParser {

	private XMLReader initializeReader() throws ParserConfigurationException, SAXException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		// create a parser
		SAXParser parser = factory.newSAXParser();
		// create the reader (scanner)
		XMLReader xmlreader = parser.getXMLReader();
		return xmlreader;
	}

	public SoundProfilesData parse(Reader reader) {
		try {
			XMLReader xmlreader = initializeReader();
			SoundProfilesDataHandler handler = new SoundProfilesDataHandler();
			// assign our handler
			xmlreader.setContentHandler(handler);
			// perform the synchronous parse
			xmlreader.parse(new InputSource(reader));
			return handler.retrieveData();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
