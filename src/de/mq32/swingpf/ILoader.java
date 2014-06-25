package de.mq32.swingpf;

import org.w3c.dom.Element;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Felix on 25.06.2014.
 */
public interface ILoader {
    void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException;

    Object create(Element element, Class clazz) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException;

}
