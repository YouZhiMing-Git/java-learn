package xml;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName:    JAXBCache
 * @Author:       szx
 * @Description:  xml 基础工具类
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
public final class JAXBCache {

    private static final JAXBCache instance = new JAXBCache();
    private final ConcurrentMap<String, JAXBContext> contextCache = new ConcurrentHashMap<String, JAXBContext>();
    private JAXBCache() {
    }
    public static JAXBCache instance() {
        return instance;
    }
    JAXBContext getJAXBContext(Class<?> clazz) throws JAXBException {
        JAXBContext context = contextCache.get(clazz.getName());
        if ( context == null ) {
            context = JAXBContext.newInstance(clazz);
            contextCache.putIfAbsent(clazz.getName(), context);
        }
        return context;
    }
}