package import_export;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import utils.Langue;

public class ImportXml {

	Hashtable<?, ?> data;
	String message = "";
	Document doc;
	NodeList nodeList;
	int nbNode = 0;
	Node nodeRacine;
	static Logger logger = Logger.getLogger(ImportXml.class);

	public ImportXml(String filename, String elementName) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(filename);

			doc = builder.parse(f);
			doc.getDocumentElement().normalize();
			nodeList = doc.getElementsByTagName(elementName);
			nbNode = nodeList.getLength();
			logger.debug("Nombre d'elements " + elementName + ":" + nbNode);
		} catch (SAXException | IOException e) {
			message = Langue.getString("filenotfound");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retourne le nombre de noeuds du fichier xml
	 * 
	 * @return int
	 */
	public int getNbNode() {
		return nbNode;
	}

	/**
	 * Retourne le noeud de rang i
	 * 
	 * @param i
	 * @return Node
	 */
	public Node getNode(int i) {
		if (i < nbNode) {
			logger.debug("Noeud " + i + " : " + nodeList.item(i).getNodeName());
			return nodeList.item(i);
		} else {
			return null;
		}
	}

	/**
	 * Fonction transformant un noeud en Hashtable. Le hashtable contient soit une
	 * chaine, soit une autre hashtable avec les valeurs imbriquees A utiliser pour
	 * extraire les valeurs d'un item du fichier xml
	 * 
	 * @param node
	 * @return Hashtable<String, Object>
	 */
	public Hashtable<String, Object> getNodeContent(Node node) {
		Hashtable<String, Object> content = new Hashtable<String, Object>();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			logger.debug(node.getNodeName());
			if (node.hasChildNodes()) {
				logger.debug(node.getNodeName() + " has child nodes");
				NodeList nl = node.getChildNodes();
				for (int count = 0; count < nl.getLength(); count++) {
					if (nl.item(count) instanceof Element == false) {
						continue;
					}
					Node ntemp = nl.item(count);
					content.put(ntemp.getNodeName(), getNodeContent(ntemp));
				}
			} else {
				Element element = (Element) node;
				logger.debug(node.getNodeName() + ":" + element.getNodeValue());
				if (node.getTextContent() != null) {
					content.put(node.getNodeName(), element.getNodeValue());
				}
			}
		}
		return content;
	}

}
