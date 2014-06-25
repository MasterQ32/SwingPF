package de.mq32.swingpf;

import org.w3c.dom.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felix on 25.06.2014.
 */
public class AttributeParser {

    private Map<Class, IConverter> converters;
    private LoaderCollection loaders;
    private ResourceCollection resources;

    public AttributeParser(ResourceCollection resources, LoaderCollection loaders) {
        this.resources = resources;
        this.loaders = loaders;

        this.converters = new HashMap<Class, IConverter>();
        this.createDefaultConverters();
    }

    private void createDefaultConverters() {
        this.converters.put(String.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return value;
            }
        });
        this.converters.put(int.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Integer.parseInt(value);
            }
        });
        this.converters.put(Integer.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Integer.parseInt(value);
            }
        });
        this.converters.put(float.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Float.parseFloat(value);
            }
        });
        this.converters.put(Float.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Float.parseFloat(value);
            }
        });
        this.converters.put(double.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Double.parseDouble(value);
            }
        });
        this.converters.put(Double.class, new IConverter() {
            @Override
            public Object convert(String value) {
                return Double.parseDouble(value);
            }
        });


        this.converters.put(Dimension.class, new IConverter() {
            @Override
            public Object convert(String value) {
                String[] items = value.split(",");
                return new Dimension(
                        Integer.parseInt(items[0].trim()),
                        Integer.parseInt(items[1].trim()));
            }
        });
        this.converters.put(Rectangle.class, new IConverter() {
            @Override
            public Object convert(String value) {
                String[] items = value.split(",");
                return new Rectangle(
                        Integer.parseInt(items[0].trim()),
                        Integer.parseInt(items[1].trim()),
                        Integer.parseInt(items[2].trim()),
                        Integer.parseInt(items[3].trim()));
            }
        });
        this.converters.put(Color.class, new IConverter() {
            @Override
            public Object convert(String value) {
               return Color.decode(value);
            }
        });
    }

    public IConverter getConverter(Class clazz) {
        if (!this.converters.containsKey(clazz)) {
            return null;
        }
        return this.converters.get(clazz);
    }

    public void parseAttributeElements(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
        Class clazz = target.getClass();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (!(node instanceof Element)) {
                continue;
            }
            Element child = (Element) node;
            if (!checkAttributeNode(clazz, child)) {
                continue;
            }

            String attributeName = child.getTagName().substring(clazz.getSimpleName().length() + 1);

            System.out.println(attributeName);

            if (child.hasChildNodes()) {
                // Assert we only have one child.
                assert (child.getFirstChild() == child.getLastChild());

                Node valueNode = child.getFirstChild();
                if (valueNode instanceof Element) {
                    // Load element node (use direct setter)
                    Object value = parser.parseElement((Element) child.getFirstChild());
                    parseAttribute(target, clazz, attributeName, true, value);
                }
                if (valueNode instanceof Text) {
                    // Load text content (use IConverter)
                    parseAttribute(target, clazz, attributeName, false, child.getTextContent());
                } else {
                    throw new IllegalStateException();
                }
            } else {
                parseAttribute(target, clazz, attributeName, true, null);
            }
        }
    }

    public static boolean checkAttributeNode(Class clazz, Element child) {
        return child.getLocalName().startsWith(clazz.getSimpleName() + ".");
    }

    public void parseAttributes(Element element, Object target) throws InvocationTargetException, IllegalAccessException {
        Class clazz = target.getClass();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            Node node = attributes.item(i);
            if (!(node instanceof Attr)) {
                continue;
            }
            Attr attr = (Attr) node;

            if (attr.getName().startsWith("xmlns")) {
                continue;
            }

            parseAttribute(target, clazz, attr.getName(), false, attr.getValue());
        }
    }

    private void parseAttribute(Object target, Class clazz, String attrName, boolean isConverted, Object attrValue) throws IllegalAccessException, InvocationTargetException {
        boolean setValue = false;

        assert (isConverted && attrValue instanceof String);


        // Check if we have x:Key as attribute.
        if (attrName.equals("x:Key")) {
            assert (isConverted == false);
            // If yes, add the object to the resource collection
            this.resources.addResource((String) attrValue, target);
            return;
        }

        // Check for bindings, resources, ...
        if (!isConverted) {
            String meta = (String) attrValue;
            if (meta.startsWith("{") && meta.endsWith("}")) {


                if (meta.startsWith("{Binding ")) {
                    throw new NotImplementedException();
                }
                if (meta.startsWith("{Resource ")) {
                    // Get resource name
                    String resourceName = meta.substring(
                            meta.indexOf(" ") + 1,
                            meta.lastIndexOf("}"));

                    // Check for resource
                    if (!this.resources.hasResource(resourceName)) {
                        throw new IllegalStateException("Resource " + resourceName + " not found!");
                    }

                    // Load the resource value into the attribute instead of the text.
                    attrValue = this.resources.findResource(resourceName);
                    isConverted = true;
                }
            }
        }

        for (Method method : clazz.getMethods()) {
            if (!method.getName().equals("set" + attrName)) {
                continue;
            }
            if (method.getParameterCount() != 1) {
                continue;
            }
            Parameter param = method.getParameters()[0];

            if (isConverted) {
                try {
                    method.invoke(target, attrValue);
                    setValue = true;
                    break;
                } catch (Exception ex) {
                    System.out.println("Failed to set value of type:");
                    System.out.println("Source type: " + attrValue.getClass().getName());
                    System.out.println("Target type: " + param.getType().getName());
                    ex.printStackTrace();
                }
            } else {
                if (param.getType() != String.class && attrValue.equals("null")) {
                    method.invoke(target, new Object[]{null});
                    setValue = true;
                    break;
                }

                IConverter converter = this.getConverter(param.getType());
                if (converter == null) {
                    System.out.println("Converter for " + param.getType().getName() + " not found.");
                    continue;
                }
                try {
                    Object value = converter.convert((String) attrValue);
                    method.invoke(target, value);
                    setValue = true;
                    break;
                } catch (Exception ex) {
                    System.out.println("Failed to convert value:");
                    ex.printStackTrace();
                }
            }
        }
        if (!setValue) {
            throw new IllegalStateException("Setter set" + attrName + " not found!");
        }
    }
}
