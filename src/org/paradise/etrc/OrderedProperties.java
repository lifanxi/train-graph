
package org.paradise.etrc;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

// 增加这一个按顺序排序属性的文件， 来源：http://www.cnblogs.com/xiongjinpeng/p/3864987.html
public class OrderedProperties extends Properties {
    private static final long serialVersionUID = -4627607243846121965L;
    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

    public Enumeration<Object> keys() {
        return Collections.<Object> enumeration(keys);
    }

    public Object put(Object key, Object value) {
        keys.add(key);
        return super.put(key, value);
    }
    
    public synchronized Object remove(Object key) {
        keys.remove(key);
        return super.remove(key);
    }

    public Set<Object> keySet() {
        return keys;
    }

    public Set<String> stringPropertyNames() {
        Set<String> set = new LinkedHashSet<String>();
        for (Object key : this.keys) {
            set.add((String) key);
        }
        return set;

    }
}