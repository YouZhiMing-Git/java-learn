package xml;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * @ClassName:    XmlUtil
 * @Author:       szx
 * @Description:  xml 工具类
 * @Date:         2021/2/4
 * @Version:      [v1.0]
 */
public class XmlUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(XmlUtil.class);
    /*
     * @Author szx
     * @Description XML 转为 bean
     * @Date 9:49 2021/2/7
     * @Param
     * @return
     **/
    public static <T> T xml2Bean(String xmlStr, Class<T> c){

        try{
            JAXBContext context = JAXBContext.newInstance(c);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            T t = (T) unmarshaller.unmarshal(new StringReader(xmlStr));
            return t;
        } catch (JAXBException e) {
            e.printStackTrace();  return null;
        }
    }


    /*
     * @Author szx
     * @Description Bean 转为 XML
     * @Date 9:50 2021/2/7
     * @Param [obj]
     * @return java.lang.String
     **/
    public static String bean2Xml(Object obj){
        String result = "";
        try {
            StringWriter sw = new StringWriter();
            JAXBContext context = JAXBCache.instance().getJAXBContext(obj.getClass());
            Marshaller m = context.createMarshaller();
//            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.setProperty(Marshaller.JAXB_FRAGMENT, true);// 是否省略xm头声明信息
            m.marshal(obj, sw);
            result = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw.toString();
        } catch(JAXBException e) {
            e.printStackTrace();
            LOGGER.error("bean2Xml ex {}" ,e.fillInStackTrace());
        }
        return result;
    }



}