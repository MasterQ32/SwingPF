package de.mq32.swingpf;

import org.w3c.dom.*;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import javax.swing.*;
import javax.swing.text.html.parser.AttributeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 25.06.2014.
 */
public class XmlLoader implements IElementParser {

    private final DocumentBuilder db;
    private ResourceCollection resources;
    private AttributeParser attributeParser;
    private LoaderCollection loaders;

    public XmlLoader() throws UnsupportedEncodingException, ParserConfigurationException {

        this.resources = new ResourceCollection();
        this.loaders = new LoaderCollection(this.resources);
        this.attributeParser = new AttributeParser(this.resources, this.loaders);

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        db = dbf.newDocumentBuilder();
        OutputStreamWriter errorWriter = new OutputStreamWriter(System.err, "utf-8");
        //db.setErrorHandler(new MyErrorHandler(new PrintWriter(errorWriter, true)));
    }

    public Object load(String fileName) throws IOException, SAXException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        Document doc = db.parse(new File(fileName));

        Element root = doc.getDocumentElement();

        return this.parseElement(root);
    }

    public Object parseElement(Element element) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        String tagName = element.getLocalName();
        String nameSpace = element.getNamespaceURI();

        Class definition;
        if (nameSpace != null) {
            definition = Class.forName(nameSpace + "." + tagName);
        } else {
            definition = Class.forName("javax.swing." + tagName);
        }

        System.out.println("Parsing " + definition.getName() + "...");

        Object target;

        ILoader loader = this.loaders.getLoader(definition);
        if (loader != null) {
            target = loader.create(element, definition);
        } else {
            target = AbstractLoader.createDefault(definition);
        }
        if(target == null) {
            throw new IllegalStateException("Construction for " + definition.getName() + " failed!");
        }

        // Load all attributes and element attributes
        this.attributeParser.parseAttributes(element, target);

        // Load the element with all fitting class loaders
        this.loaders.load(element, target, this);

        this.attributeParser.parseAttributeElements(element, target, this);

        return target;
    }

    public ResourceCollection getResources() {
        return resources;
    }
}
