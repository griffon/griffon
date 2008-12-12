package greet;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Danno.Ferrin
 * Date: Dec 11, 2008
 * Time: 8:25:49 PM
 */
public class CacheMap extends LinkedHashMap {
    int maxSize;
    public CacheMap(int newMax) {
        maxSize = newMax;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry eldest) {
        return size() > maxSize;
    }
}
