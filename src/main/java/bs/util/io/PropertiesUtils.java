package bs.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.LinkedHashMap;

/**
 * 读取属性文件属性.
 *
 * @author Baishui2004
 * @version 1.0
 */
public class PropertiesUtils {

    /**
     * 获取.properties属性文件所有属性.
     *
     * @param path 属性文件路径
     * @return <code>Properties</code> 属性
     * @throws IOException IO Exception
     */
    public static LinkedProperties getProperties(String path) throws IOException {
        LinkedProperties properties = new LinkedProperties();
        InputStream in = null;
        try {
            in = new FileInputStream(new File(path));
            properties = getProperties(in);
        } finally {
            if (in != null) {
                in.close();
            }
        }
        return properties;
    }

    /**
     * 获取所有属性.
     *
     * @param in InputStream
     * @return <code>Properties</code> 属性
     * @throws IOException IO Exception
     */
    public static LinkedProperties getProperties(InputStream in) throws IOException {
        LinkedProperties properties = new LinkedProperties();
        properties.load(in);
        return properties;
    }

    /**
     * 获取.properties属性文件所有属性的Map集合.
     *
     * @param path 属性文件路径
     * @return <code>Map<String, String></code> 属性的Map集合
     * @throws IOException IO Exception
     */
    public static LinkedHashMap<String, String> getPropertiesMap(String path) throws IOException {
        LinkedProperties properties = getProperties(path);
        return getPropertiesMap(properties);
    }

    /**
     * 获取所有属性.
     *
     * @param in InputStream
     * @return <code>Map<String, String></code> 属性的Map集合
     * @throws IOException IO Exception
     */
    public static LinkedHashMap<String, String> getPropertiesMap(InputStream in) throws IOException {
        LinkedProperties properties = getProperties(in);
        return getPropertiesMap(properties);
    }

    /**
     * 获取properties属性的Map集合.
     *
     * @param properties 属性
     * @return <code>Map<String, String></code> 属性的Map集合
     */
    public static LinkedHashMap<String, String> getPropertiesMap(LinkedProperties properties) {
        Enumeration<Object> keys = properties.keys();
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            map.put(key, properties.getProperty(key));
        }
        return map;
    }

    /**
     * 获取.properties属性单个属性的方法，获取两个或两个以上属性值使用getPropertiesMap(path)方法.
     *
     * @param path     属性文件路径
     * @param property 属性key
     * @return <code>String</code> 属性值
     * @throws IOException IO Exception
     * @see #getPropertiesMap(String)
     */
    public static String getProperty(String path, String property) throws IOException {
        return getPropertiesMap(path).get(property);
    }

}
