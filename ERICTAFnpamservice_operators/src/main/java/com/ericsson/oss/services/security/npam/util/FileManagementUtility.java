/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.oss.services.security.npam.util;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ericsson.cifwk.taf.TestContext;
import com.ericsson.cifwk.taf.utils.FileFinder;

public class FileManagementUtility {

    @Inject
    TestContext testContext;

    private static Logger logger = LoggerFactory.getLogger(FileManagementUtility.class);

    /**
     * return the file name from the command string
     *
     * @param command
     *            to be executed
     * @return file name in the command
     */
    public static String getFileName(final String command) {
        return command.substring(command.lastIndexOf(":") + 1);
    }

    public String generateNameWithTimeStamp(final String name) {
        final Date date = new Date();
        final long timeStamp = date.getTime();
        final String modifiedName = name + timeStamp;
        logger.debug("generateNameWithTimeStamp, modifiedName : " + modifiedName);
        return modifiedName;
    }

    public String extractTimestamp(final String str) {
        final String timeStamp = "" + new Date().getTime();
        final int len = str.length();
        return str.substring(len - timeStamp.length(), len);
    }

    /**
     * @param fileName
     *            name of file
     * @param tagName
     *            tag name
     * @param name
     *            name tag
     * @param keySize
     *            keySize tag
     * @param value
     *            value in tag
     * @return profileName before the xml was modified
     */
    public String modifyChildElements(final String fileName, final String tagName, final String name, final String keySize, final String value) {
        String profileName = null;
        try {
            final File file = getFileFromFileFinder(fileName);
            final Document doc = createDocument(file);

            // Get the element by tag name directly

            final NodeList tagElements = doc.getElementsByTagName(tagName);
            for (int i = 0; i < tagElements.getLength(); i++) {
                final NodeList childList = tagElements.item(i).getChildNodes();
                for (int j = 0; j < childList.getLength(); j++) {
                    final Node childNode = childList.item(j);
                    if (name != null && "Name".equals(childNode.getNodeName())) {
                        profileName = childList.item(j).getTextContent();
                        childList.item(j).setTextContent(name);
                    } else if (keySize != null && "KeySize".equals(childNode.getNodeName())) {
                        profileName = childList.item(j).getTextContent();
                        childList.item(j).setTextContent(keySize);
                    } else if (value != null && "Id".equals(childNode.getNodeName())) {
                        childList.item(j).setTextContent(value);
                    }
                }
            }
            transform(file, doc);
        } catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (final TransformerException tfe) {
            tfe.printStackTrace();
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } catch (final SAXException sae) {
            sae.printStackTrace();
        }
        return profileName;
    }

    public String modifyName(final String fileName, final String tagName, final String tagValue) {
        return modifyName(fileName, tagName, tagValue, null, null);
    }

    /**
     * @param fileName
     *            name of file
     * @param tagName
     *            tag name
     * @param tagValue
     *            tag value
     * @param attributeName
     *            name of attribute
     * @param attributeValue
     *            value of attribute
     * @return profileName before the xml was modified
     */
    public String modifyName(final String fileName, final String tagName, final String tagValue, final String attributeName,
            final String attributeValue) {
        String profileName = null;
        try {
            final File file = getFileFromFileFinder(fileName);
            final Document doc = createDocument(file);

            // Get the element by tag name directly
            final NodeList allTagElements = doc.getElementsByTagName(tagName);
            if (!(allTagElements.getLength() < 1)) {
                for (int i = 0; i < allTagElements.getLength(); i++) {

                    final Node tagElements = doc.getElementsByTagName(tagName).item(i);
                    if (attributeName != null) {
                        final NamedNodeMap attr = tagElements.getAttributes();
                        final Node nodeAttr = attr.getNamedItem(attributeName);
                        profileName = nodeAttr.getTextContent();
                        nodeAttr.setTextContent(attributeValue);
                    } else {
                        profileName = tagElements.getTextContent();
                        tagElements.setTextContent(tagValue);
                    }
                }
            } else {
                logger.info("There are no elements present with tagname : " + tagName + " in xml : " + fileName);
            }
            transform(file, doc);

        } catch (final ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } catch (final SAXException sae) {
            sae.printStackTrace();
        } catch (final TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (final TransformerException e) {
            e.printStackTrace();
        }
        return profileName;
    }

    /**
     * @param fileName
     *            file name for loading
     * @return the file from file finder
     */
    public static File getFileFromFileFinder(final String fileName) {
        File file = null;
        final Iterator<String> it = FileFinder.findFile(fileName).iterator();
        while (it.hasNext()) {
            final String strfileName = it.next();

            final Path p = Paths.get(strfileName);
            if (p.getFileName().toString().equals(fileName)) {
                file = new File(strfileName);

                // I usually have only 1 <fileName> in the list
                // but if I run TAF in a local environment I have multiple
                // instances
                // In this case I return the one present under target directory
                if (strfileName.contains("target")) {
                    break;
                }
            }

        }
        return file;
    }

    /**
     * Load template file to a hashmap.
     *
     * @param fileName
     *            file name for loading
     * @return a map of file name and file path
     */
    public static Map<String, Object> templateLoader(final String fileName) {
        final Map<String, Object> properties = new HashMap<>();
        final String absolutePath = new File(fileName).getAbsolutePath();
        properties.put("fileName", fileName);
        properties.put("filePath", absolutePath);
        return Collections.unmodifiableMap(properties);
    }

    public Map<String, Object> getProperties(final String command) {
        final String fileName = FileManagementUtility.getFileName(command);
        final Map<String, Object> properties = FileManagementUtility.templateLoader(fileName);
        return properties;
    }

    public String getTagValue(final String fileName) {

        String certificateValidity = null;
        try {
            final File file = getFileFromFileFinder(fileName);
            final Document doc = createDocument(file);

            final Element docEle = doc.getDocumentElement();
            final NodeList nl = docEle.getChildNodes();
            if (nl != null && nl.getLength() > 0) {
                for (int i = 0; i < nl.getLength(); i++) {
                    if (nl.item(i).getNodeType() == Node.ELEMENT_NODE) {
                        final Element el = (Element) nl.item(i);
                        if (el.getNodeName().contains("CertificateProfile")) {
                            certificateValidity = el.getElementsByTagName("CertificateValidity").item(0).getTextContent();
                        }
                    }
                }
            }

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } catch (final ParserConfigurationException e) {
            e.printStackTrace();
        } catch (final SAXException e) {
            e.printStackTrace();
        }
        return certificateValidity;
    }

    /**
     * @param fileName
     *            name of file
     * @param tagName
     *            tag name
     * @param fieldToChange
     *            field to be changed
     * @param newValue
     *            new value of attribute
     * @return oldValue before the xml was modified
     */
    public String modifyChildElementsTypeValue(final String fileName,
            final String tagName, final String fieldToChange, final String newValue) {
        String oldValue = null;
        try {
            final File file = getFileFromFileFinder(fileName);
            final Document doc = createDocument(file);

            // Get the element by tag name directly

            final NodeList tagElements = doc.getElementsByTagName(tagName);
            for (int i = 0; i < tagElements.getLength(); i++) {
                final Node node = tagElements.item(i);
                final NodeList childNodes = node.getChildNodes();
                for (int j = 0; j < childNodes.getLength(); j++) {
                    final Node childNode = childNodes.item(j);
                    if (!fieldToChange.equals(childNode.getTextContent())) {
                        continue;
                    }
                    if (j + 2 >= childNodes.getLength()) {
                        continue;
                    }
                    final Node childNodeValue = childNodes.item(j + 2);
                    oldValue = childNodeValue.getTextContent();
                    childNodeValue.setTextContent(newValue);
                }
            }

            transform(file, doc);
        } catch (final ParserConfigurationException | TransformerException | IOException | SAXException exc) {
            exc.printStackTrace();
        }
        return oldValue;
    }

    private Document createDocument(final File file) throws ParserConfigurationException, IOException, SAXException {
        final DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        return docBuilder.parse(file);
    }

    private static void transform(final File file, final Document doc) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource source = new DOMSource(doc);
        final StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
    }

    private static String transformToString(final Document doc) throws TransformerException {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        final Transformer transformer = transformerFactory.newTransformer();
        final DOMSource source = new DOMSource(doc);
        final StringWriter out = new StringWriter();
        transformer.transform(source, new StreamResult(out));
        return out.toString();
    }

}
