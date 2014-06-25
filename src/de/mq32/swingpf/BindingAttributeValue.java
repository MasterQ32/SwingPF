package de.mq32.swingpf;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by Felix on 25.06.2014.
 */
public class BindingAttributeValue extends MetaAttributeValue {

    private DataSource source;
    private String pathName;
    private String attributeName;
    private PropertyChangeListener listenerDataSource;
    private PropertyChangeListener listenerData;

    public BindingAttributeValue() {
        this.listenerDataSource = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                // Rebind
                // Prepare new value for binding
                if(evt.getPropertyName().equals("Value")) {
                    prepareBinding(source.getValue());
                }
            }
        };
        this.listenerData = new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {

                // Copy bound value to target
                System.out.println(evt.getPropertyName() + " changed!");
                if(evt.getPropertyName().toLowerCase().equals(pathName.toLowerCase())) {
                    copyBinding();
                }
            }
        };
    }

    @Override
    protected void setSingleParameter(String value) {
        this.pathName = value;
    }

    public void setPath(String path) {
        this.pathName = path;
    }

    @Override
    public Object apply(ResourceCollection resources, String attributeName, Object target) {

        this.source = DataSource.get(target);
        this.source.addPropertyChangeListener(this.listenerDataSource);

        this.attributeName = attributeName;


        Object value = this.source.getValue();
        prepareBinding(value);


        return readBinding(value);
    }

    private void prepareBinding(Object target) {
        if (target == null) {
            return; // Nothing to prepare...
        }
        try {
            Method method = target.getClass().getMethod("addPropertyChangeListener", PropertyChangeListener.class);
            method.invoke(target, this.listenerData);
        } catch (NoSuchMethodException e) {
            System.out.println("Binding may fail for " + attributeName + ": addPropertyChangeListener missing in bound value.");
        } catch (InvocationTargetException e) {
            e.printStackTrace();
            assert (false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            assert (false);
        }

        copyBinding();
    }

    private void copyBinding() {
        // Read value from data source
        Object value = readBinding(this.source.getValue());

        // Write value to data binding
        AttributeParser.setValue(this.source.getTarget(), attributeName, value);
    }

    private Object readBinding(Object source) {
        try {
            if (source == null) {
                return null;
            }
            if (this.pathName == ".") {
                return source;
            } else {
                return source.getClass().getMethod("get" + this.pathName).invoke(source);
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
