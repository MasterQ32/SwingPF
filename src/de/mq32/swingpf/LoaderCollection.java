package de.mq32.swingpf;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Felix on 25.06.2014.
 */
public class LoaderCollection {
    private Map<Class, ILoader> loaders;
    private ResourceCollection resources;

    public LoaderCollection(ResourceCollection resources) {
        this.resources = resources;

        this.loaders = new HashMap<Class, ILoader>();
        this.createDefaultLoaders();
    }

    private void createDefaultLoaders() {
        this.loaders.put(String.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {

            }

            @Override
            public Object create(Element element, Class clazz) throws InvocationTargetException, IllegalAccessException {
                if(clazz != String.class) {
                    throw new IllegalStateException();
                }
                return element.getTextContent();
            }
        });
        this.loaders.put(ImageIcon.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException {

            }

            public Object create(Element element, Class clazz) {
                if (clazz != ImageIcon.class) {
                    throw new IllegalStateException();
                }
                ImageIcon icon = new ImageIcon(element.getTextContent());
                return icon;
            }
        });
        this.loaders.put(Color.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException {

            }

            public Object create(Element element, Class clazz) {
                if (clazz != Color.class) {
                    throw new IllegalStateException();
                }
                return Color.decode(element.getTextContent());
            }
        });
        this.loaders.put(Container.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
                Container container = (Container) target;
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    if (!(node instanceof Element)) {
                        continue;
                    }
                    if (AttributeParser.checkAttributeNode(target.getClass(), (Element) node)) {
                        continue; // Element is attribute element
                    }
                    Object child = parser.parseElement((Element) node);
                    if (child instanceof Component) {
                        container.add((Component) child);
                    }
                }
            }
        });
        /*
        this.loaders.put(JMenuBar.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
                JMenuBar menuBar = (JMenuBar) target;
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    System.out.println("Scan: " + node.getClass().getName() + " -> " + node.getLocalName());
                    if (!(node instanceof Element)) {
                        continue;
                    }
                    if (AttributeParser.checkAttributeNode(target.getClass(), (Element) node)) {
                        continue; // Element is attribute element
                    }
                    Object child = parser.parseElement((Element) node);
                    if (child instanceof JMenu) {
                        menuBar.add((JMenu) child);
                        System.out.println("Oh wonder?");
                    }
                }
            }
        });
        this.loaders.put(JMenuItem.class, new AbstractLoader(this.loaders, this.resources) {
            @Override
            public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
                JMenuItem menuItem = (JMenuItem) target;
                NodeList children = element.getChildNodes();
                for (int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    System.out.println("Scan Sub: " + node.getClass().getName() + " -> " + node.getLocalName());
                    if (!(node instanceof Element)) {
                        continue;
                    }
                    if (AttributeParser.checkAttributeNode(target.getClass(), (Element) node)) {
                        continue; // Element is attribute element
                    }
                    Object child = parser.parseElement((Element) node);
                    if (child instanceof JMenuItem) {
                        menuItem.add((JMenuItem) child);
                        System.out.println("Oh wonder what happens?");
                    }
                }
            }
        });*/
    }

    /**
     * Gets the ILoader for the given class.
     *
     * @param clazz Class to be loaded.
     * @return ILoader or null if none.
     */
    public ILoader getLoader(Class clazz) {
        if (this.loaders.containsKey(clazz)) {
            return this.loaders.get(clazz);
        } else {
            return null;
        }
    }


    /**
     * Loads an object with every loader in reverse hierarchical order.
     *
     * @param element Source of data.
     * @param target  Target to be loaded.
     * @return ILoader or null if none.
     */
    public void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException, InstantiationException {
        List<Class> ll = new ArrayList<Class>();
        Class it = target.getClass();
        while (it != null) {
            ll.add(it);
            it = it.getSuperclass();
        }
        for (int i = ll.size() - 1; i >= 0; i--) {
            Class c = ll.get(i);
            if (this.loaders.containsKey(c)) {
                this.loaders.get(c).load(element, target, parser);
            }
        }
    }
}
