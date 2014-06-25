package de.mq32.swingpf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by Felix on 25.06.2014.
 */
public class ResourceCollection {

    private Map<String, Object> resources = new HashMap<String, Object>();

    public void addResource(String name, Object value) {
        resources.put(name, value);
    }

    public Object findResource(String name) {
        if (resources.containsKey(name)) {
            return resources.get(name);
        } else {
            return null;
        }
    }

    public boolean hasResource(String name) {
        return resources.containsKey(name);
    }

    public void printDebugInfo() {
        resources.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {
                if (o == null) {
                    System.out.println("[???] " + s + " => <null>");
                } else {
                    System.out.println("[" + o.getClass().getName() + "] " + s + " => " + o.toString());
                }

            }
        });
    }
}
