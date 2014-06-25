package de.mq32.swingpf;

import org.w3c.dom.Element;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * Created by Felix on 25.06.2014.
 */
public abstract class AbstractLoader implements ILoader {
    Map<Class, ILoader> loaders;
    ResourceCollection resources;

    public AbstractLoader(Map<Class, ILoader> loaders, ResourceCollection resources) {
        this.loaders = loaders;
        this.resources = resources;
    }

    public abstract void load(Element element, Object target, IElementParser parser) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException, ClassNotFoundException;

    public Object create(Element element, Class clazz) throws InvocationTargetException, IllegalAccessException {
        return createDefault(clazz);
    }

    public static Object createDefault(Class clazz) throws InvocationTargetException, IllegalAccessException {
        try {// Try calling default constructor.
            return clazz.getConstructor().newInstance();
        } catch (NoSuchMethodException e) {
        } catch (InstantiationException e) {
        }
        System.out.println("Default constructor not found for " + clazz.getName());
        return null;
    }
}
