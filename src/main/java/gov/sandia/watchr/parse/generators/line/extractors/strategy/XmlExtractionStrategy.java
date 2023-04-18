/*******************************************************************************
* Watchr
* ------
* Copyright 2022 National Technology & Engineering Solutions of Sandia, LLC (NTESS).
* Under the terms of Contract DE-NA0003525 with NTESS, the U.S. Government retains
* certain rights in this software.
******************************************************************************/
package gov.sandia.watchr.parse.generators.line.extractors.strategy;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Deque;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import gov.sandia.watchr.config.file.IFileReader;
import gov.sandia.watchr.config.schema.Keywords;
import gov.sandia.watchr.log.ILogger;
import gov.sandia.watchr.parse.WatchrParseException;
import gov.sandia.watchr.parse.generators.line.extractors.ExtractionResult;
import gov.sandia.watchr.util.StringUtil;

public class XmlExtractionStrategy extends ExtractionStrategy<Element> {
    
    ////////////
    // FIELDS //
    ////////////

    private String fileAbsPath;
    private String elementPattern = "";
    private String pathAttribute = "";

    /////////////////
    // CONSTRUCTOR //
    /////////////////
    
    protected XmlExtractionStrategy(
            Map<String, String> properties, AmbiguityStrategy strategy,
            ILogger logger, IFileReader fileReader) {
        super(properties, strategy, logger, fileReader);
        this.elementPattern = properties.getOrDefault(Keywords.GET_ELEMENT, "");
        this.pathAttribute  = properties.getOrDefault(Keywords.GET_PATH_ATTRIBUTE, "");
    }

    /////////////
    // GETTERS //
    /////////////

    public String getElementPattern() {
        return elementPattern;
    }

    public String getPathAttribute() {
        return pathAttribute;
    }

    //////////////
    // OVERRIDE //
    //////////////

    @Override
    public List<ExtractionResult> extract(String fileAbsPath) throws WatchrParseException {
        this.fileAbsPath = fileAbsPath;
        String fileContents = fileReader.readFromFile(fileAbsPath);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            DocumentBuilder dBuilder = factory.newDocumentBuilder();
            
            Document document = dBuilder.parse(new ByteArrayInputStream(fileContents.getBytes()));
            Element root = document.getDocumentElement();

            Deque<String> stops = getPathStops();
            return getNextPathStop("", stops, root);
        } catch(Exception e) {
            throw new WatchrParseException(e);
        }
    }

    @Override
    public boolean equals(Object other) {
        boolean equals = super.equals(other);
		if(other == null) {
            return false;
        } else if(other == this) {
            return true;
        } else if(getClass() != other.getClass()) {
            return false;
        } else {
			XmlExtractionStrategy otherExtractor = (XmlExtractionStrategy) other;
            equals = equals && elementPattern.equals(otherExtractor.elementPattern);
            equals = equals && pathAttribute.equals(otherExtractor.pathAttribute);
        }
        return equals;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 31 * (hash + elementPattern.hashCode());
        hash = 31 * (hash + pathAttribute.hashCode());
        return hash;
    }

    @Override
    protected List<ExtractionResult> getNextPathStop(String pathSoFar, Deque<String> remainingStops, Element element) {
        List<ExtractionResult> results = null;
        
        String elementName = element.getNodeName();
        String pathAttributeValue = getXmlAttributeValue(element.getAttributes(), pathAttribute);
        boolean recursive = strategy.shouldRecurseToChildGraphs();

        if((!remainingStops.isEmpty() || recursive) && elementName.matches(elementPattern) && pathAttributeValue != null) {
            String nextStop = "*";
            if(!remainingStops.isEmpty()) {
                nextStop = remainingStops.pop();
            }
            nextStop = StringUtil.convertToRegex(nextStop);
            if(pathAttributeValue.matches(nextStop)) {
                List<ExtractionResult> nextResults = handleNormalStop(pathSoFar, pathAttributeValue, remainingStops, element);
                if(nextResults != null) {
                    results = new ArrayList<>();
                    results.addAll(nextResults);
                } else {
                    remainingStops.push(nextStop);
                }
            } else {
                remainingStops.push(nextStop);
            }
        }
        return results;
    }

    /////////////
    // PRIVATE //
    /////////////

    private List<ExtractionResult> handleNormalStop(String path, String pathAttributeValue, Deque<String> remainingStops, Element element) {
        List<ExtractionResult> results = null;
        if(!remainingStops.isEmpty()) {
            List<ExtractionResult> childResults = handleChildXmlElement(element, path + "/" + pathAttributeValue, remainingStops);
            if(childResults != null) {
                results = new ArrayList<>();
                results.addAll(childResults);
            }
        } else {
            ExtractionResult childResult = handleLastXmlElement(element, path + "/" + pathAttributeValue, remainingStops);
            if(childResult != null) {
                results = new ArrayList<>();
                results.add(childResult);
            }
        }
        return results;
    }

    private List<ExtractionResult> handleChildXmlElement(Element element, String path, Deque<String> remainingStops) {
        List<ExtractionResult> results = null;
        NodeList children = element.getChildNodes();
        boolean firstMatchOnly = strategy.shouldGetFirstMatchOnly();

        if(children.getLength() > 0) {
            for(int i = 0; i < children.getLength(); i++) {
                Node childNode = children.item(i);
                if(childNode instanceof Element) {
                    List<ExtractionResult> childResults = getNextPathStop(path, remainingStops, (Element) childNode);
                    if(childResults != null) {
                        if(results == null) {
                            results = new ArrayList<>();
                        }
                        results.addAll(childResults);
                    }
                    if(firstMatchOnly && results != null && !results.isEmpty()) {
                        break;
                    }
                }
            }
        } else {
            results = new ArrayList<>();
            results.add(handleLastXmlElement(element, path, remainingStops));
        }
        return results;
    }

    private ExtractionResult handleLastXmlElement(Element element, String path, Deque<String> remainingStops) {
        ExtractionResult result = null;
        boolean recursive = strategy.shouldRecurseToChildGraphs();

        if(recursive) {
            result = handleXmlElementForRecursiveChildren(element, path, remainingStops);
        } else {
            String value = getXmlAttributeValue(element.getAttributes(), key);
            if(StringUtils.isNotBlank(value)) {
                result = new ExtractionResult(fileAbsPath, path, key, value);
            }
        }

        return result;
    }    

    private ExtractionResult handleXmlElementForRecursiveChildren(Element element, String path, Deque<String> remainingStops) {
        String value = getXmlAttributeValue(element.getAttributes(), key);
        ExtractionResult result = new ExtractionResult(fileAbsPath, path, key, value);
        List<ExtractionResult> childResults = new ArrayList<>();
        boolean firstMatchOnly = strategy.shouldGetFirstMatchOnly();

        NodeList children = element.getChildNodes();
        if(children.getLength() > 0) {
            for(int i = 0; i < children.getLength(); i++) {
                Node childNode = children.item(i);
                if(childNode instanceof Element) {
                    List<ExtractionResult> nextResults = getNextPathStop(path, remainingStops, (Element) childNode);
                    if(nextResults != null) {
                        childResults.addAll(nextResults);
                    }
                    if(firstMatchOnly) {
                        break;
                    }
                }
            }
        }
        result.getChildren().addAll(childResults);

        return result;
    }

    private String getXmlAttributeValue(NamedNodeMap attributes, String name) {
        if(StringUtils.isNotBlank(name)) {
            for(int i = 0; i < attributes.getLength(); i++) {
                Attr attributeNode = (Attr) attributes.item(i);
                String attributeName = attributeNode.getName();
                if(StringUtils.isNotBlank(attributeName) && attributeName.matches(name)) {
                    return attributeNode.getNodeValue();
                }
            }
        }
        return "";
    }
    
}
