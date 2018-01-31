package utils;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * 
 * @author ftp://ftp-developpez.com/java/sources/ProfileReader.zip
 * 
 */
public class ProfileReader {

	private Hashtable<String, Hashtable<String, String>> _sections;

	/**
	 * Creates a new ProfileReader
	 */
	public ProfileReader() {
		_sections = new Hashtable<String, Hashtable<String, String>>();
	}

	/**
	 * Load the current objet with the data found in the given stream
	 * 
	 * @param aStream
	 *            the stream that represent the INI file.
	 * @throws Exception
	 *             in case of problems.
	 */
	public void load(InputStream aStream) throws Exception {
		if (null == aStream) {
			return;
		}

		BufferedReader reader = new BufferedReader(new InputStreamReader(aStream));
		String line = null;
		String sectionName = null;
		Hashtable<String, String> section = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			// All the data should be in a section
			if (null == sectionName) {
				if ((!line.startsWith("[")) || (!line.endsWith("]"))) {
					throw new Exception("Invalid format: data found outside section");
				}

				sectionName = line.substring(1, line.length() - 1).trim();
				addSection(sectionName);
				section = getSection(sectionName);
			} else {
				if (line.startsWith("[")) {
					if (!line.endsWith("]")) {
						throw new Exception("Invalid format: no ending ] for section name");
					}
					sectionName = line.substring(1, line.length() - 1).trim();
					addSection(sectionName);
					section = getSection(sectionName);
				} else {
					addLineToSection(line, section);
				}
			}
		}
	}

	/**
	 * Return the value of the given key in the given section
	 * 
	 * @param aSectionName
	 *            the name of the section
	 * @param aKey
	 *            the key
	 * @return the value if found or null.
	 */
	public String getProperty(String aSectionName, String aKey) {
		Hashtable<String, String> section = getSection(aSectionName);
		if (null == section) {
			return null;
		}

		return (String) section.get(aKey);
	}

	private void addLineToSection(String aLine, Hashtable<String, String> aSection) throws Exception {
		if (null == aLine) {
			return;
		}

		if (null == aSection) {
			throw new Exception("No section found to add data");
		}

		aLine = aLine.trim();

		// lines that starts with ; are comments
		if (aLine.startsWith(";")) {
			return;
		}

		// Avoid the empty lines
		if (aLine.length() == 0) {
			return;
		}

		if (aLine.endsWith("=")) {

		}

		// The format of a line of data is: key = value
		StringTokenizer st = new StringTokenizer(aLine, "=");
		String key = "";
		String value = "";
		// The value is empty
		if (aLine.endsWith("=")) {
			key = st.nextToken().trim();
		} else {
			// the value is defining
			if (st.countTokens() != 2) {
				// throw new Exception("Invalid format of data: " + aLine);
			}

			key = st.nextToken().trim();
			// a key should not contain spaces
			for (int index = 0; index < key.length(); index++) {
				if (Character.isWhitespace(key.charAt(index))) {
					throw new Exception("Invalid format of data: " + aLine);
				}
			}

			//value = st.nextToken().trim();
			value = aLine.substring(key.length() + 1);
		}
		aSection.put(key, value);
	}

	private void addSection(String aSectionName) {
		if (null == aSectionName) {
			return;
		}

		Hashtable<String, String> section = getSection(aSectionName);
		if (null == section) {
			section = new Hashtable<String, String>();
			_sections.put(aSectionName, section);
		}
	}

	Hashtable<String, String> getSection(String aSectionName) {
		return (Hashtable<String, String>) _sections.get(aSectionName);
	}

}