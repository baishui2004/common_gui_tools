package bs.util.io;

import java.util.*;

/**
 * copyright: https://stackoverflow.com/questions/1312383/pulling-values-from-a-java-properties-file-in-order
 */
public class LinkedProperties extends Properties {
    private final HashSet<Object> keys = new LinkedHashSet<Object>();

    public LinkedProperties() {
    }

    public Iterable<Object> orderedKeys() {
        return Collections.list(keys());
    }

    @Override
    public Enumeration<Object> keys() {
        return Collections.<Object>enumeration(keys);
    }

    @Override
    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
}
