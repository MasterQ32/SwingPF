package de.mq32.swingpf;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * Created by Felix on 25.06.2014.
 */
public class DataSource {

    static Map<Object, DataSource> sources = new HashMap<Object, DataSource>();
    private final Object target;
    private Object value = null;
    private Vector<PropertyChangeListener> propertyChangeListenerVector = new Vector<PropertyChangeListener>();

    private DataSource(Object target) {
        this.target = target;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeListenerVector.add(listener);
    }

    /**
     * Gets the data source for a given object.
     *
     * @param target Object for which the data source should be.
     * @return DataSource for target.
     */
    public static DataSource get(Object target) {
        if (target == null) {
            throw new IllegalArgumentException("target");
        }

        if (!sources.containsKey(target)) {
            sources.put(target, new DataSource(target));

            //System.out.println("Created DataSource for " + target.toString());
        }
        return sources.get(target);
    }

    /**
     * Gets the data reference.
     *
     * @return
     */
    public Object getValue() {
        return value;
    }

    /**
     * Sets the data reference.
     *
     * @param value New data reference.
     */
    public void setValue(Object value)
    {
        boolean trigger = this.value != value;
        Object old = this.value;
        this.value = value;

        if (trigger) {
            for (PropertyChangeListener propertyChangeListener : propertyChangeListenerVector) {
                propertyChangeListener.propertyChange(new PropertyChangeEvent(this, "Value", old, value));
            }
        }
    }

    /**
     * Gets the target for this data source.
     * @return
     */
    public Object getTarget() {
        return target;
    }
}
