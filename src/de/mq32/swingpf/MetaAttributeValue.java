package de.mq32.swingpf;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Felix on 25.06.2014.
 */
public abstract class MetaAttributeValue {

    public static MetaAttributeValue parse(String text) {
        MetaAttributeValue value = null;

        String metaValueName;
        if (text.contains(" ")) {
            int idx = text.lastIndexOf(" ");
            metaValueName = text.substring(1, idx);
            text = text.substring(idx + 1, text.length() - 1);
        } else {
            metaValueName = text.substring(1, text.length() - 1);
            text = "";
        }

        if (metaValueName.equals("Resource")) {
            value = new ResourceAttributeValue();
        } else if (metaValueName.equals("Binding")) {
            value = new BindingAttributeValue();
        }
        assert (value != null);

        Class clazz = value.getClass();

        if(text.contains("=")) {
            String[] arguments = text.split(",");
            for (String argument : arguments) {
                argument = argument.trim();
                String[] kvp = argument.split("=");
                assert(kvp.length == 2);

                try {
                    Method method = clazz.getMethod("set" + kvp[0], String.class);
                    method.invoke(value, kvp[1]);
                } catch (NoSuchMethodException e) {
                    assert(false);
                } catch (InvocationTargetException e) {
                    assert(false);
                } catch (IllegalAccessException e) {
                    assert(false);
                }

            }
        } else {
            value.setSingleParameter(text);
        }

        return value;
    }

    protected abstract void setSingleParameter(String value);

    /**
     * Applies the meta attribute value to a given object and returns the real data value.
     *
     * @param resources ResourceCollection to find resources
     * @param target Target object of the attribute
     * @return real value
     */
    public abstract Object apply(ResourceCollection resources, String attributeName,  Object target);
}
