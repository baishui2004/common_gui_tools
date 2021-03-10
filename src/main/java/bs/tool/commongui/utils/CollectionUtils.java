package bs.tool.commongui.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CollectionUtils {

    /**
     * 仅支持List及Map.
     *
     * @param t
     * @param <T>
     * @return
     */
    public static <T> T reInitItemValue(T t) {
        if (t instanceof List) {
            List list = (List) t;
            int lSize = list.size();
            List nList = new ArrayList();
            for (int i = 0; i < lSize; i++) {
                Object item = list.get(i);
                if (item instanceof List || item instanceof Map) {
                    reInitItemValue(item);
                    nList.add(item);
                } else {
                    nList.add(reInitValue(item));
                }
            }
            return (T) nList;
        } else if (t instanceof Map) {
            Map map = (Map) t;
            for (Object m : map.keySet()) {
                Object item = map.get(m);
                if (item instanceof List || item instanceof Map) {
                    reInitItemValue(item);
                    map.put(m, item);
                } else {
                    map.put(m, reInitValue(item));
                }
            }
            return t;
        }
        return t;
    }

    public static <T> Object reInitValue(T t) {
        Object v = null;
        if (t instanceof Integer) {
            v = 0;
        } else if (t instanceof Float || t instanceof Double) {
            v = 0.0;
        } else if (t instanceof String) {
            v = "";
        }
        return v;
    }

}
