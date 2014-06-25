package de.mq32.swingpf;

import org.w3c.dom.Element;

import java.lang.reflect.InvocationTargetException;

/**
 * Created by Felix on 25.06.2014.
 */
public interface IElementParser {
    Object parseElement(Element element) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException ;
}
