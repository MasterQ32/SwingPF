package de.mq32.swingpf;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Felix on 25.06.2014.
 */
public class ResourceAttributeValue extends MetaAttributeValue {

    private String resourceName;

    @Override
    protected void setSingleParameter(String value) {
        this.resourceName = value;
    }

    public void setResource(String value) {
        this.resourceName = value;
    }

    @Override
    public Object apply(ResourceCollection resources, String attributeName, Object target) {
        if (!resources.hasResource(resourceName)) {
            throw new IllegalStateException("Resource " + resourceName + " not found!");
        }
        return resources.findResource(this.resourceName);
    }
}
