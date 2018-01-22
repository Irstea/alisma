package import_export;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
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

	public ImportXml(String filename) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			File f = new File(filename);

			doc = builder.parse(f);
			doc.getDocumentElement().normalize();
			if (doc.hasChildNodes()) {
				nodeList = doc.getChildNodes();
				nbNode = nodeList.getLength();
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException | IOException e) {
			message = Langue.getString("filenotfound");
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
			return nodeList.item(i);
		} else {
			return null;
		}
	}

	/**
	 * Fonction transformant un noeud en Hashtable.
	 * Le hashtable contient soit une chaine, soit une autre hashtable avec les valeurs imbriquees
	 * A utiliser pour extraire les valeurs d'un item du fichier xml
	 * @param node
	 * @return Hashtable<String, Object>
	 */
	public Hashtable<String, Object> getNodeContent(Node node) {
		Hashtable<String, Object> content = new Hashtable<String, Object>();
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			if (node.hasChildNodes()) {
				NodeList nl = node.getChildNodes();
				 for (int count = 0; count < nl.getLength(); count++) {
					 Node ntemp = nl.item(count);
					 content.put(ntemp.getNodeName(),getNodeContent(ntemp));
				 }
			} else {
				content.put(node.getNodeName(), (String)node.getNodeValue());
			}
		}
		return content;
	}

}
