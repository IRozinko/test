package fintech.spain.inglobaly.impl;

import com.global.info.ws.soap.ListadoDomiciliosTelefonos;
import com.global.info.ws.soap.ObjectFactory;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.base.Throwables;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.StringBuilderWriter;

import javax.xml.bind.*;
import javax.xml.namespace.QName;
import java.io.InputStream;
import java.util.ArrayList;

@Slf4j
public class ObjectSerializer {

    private static final Supplier<JAXBContext> jaxbContext = Suppliers.memoize(() -> {
        try {
            return JAXBContext.newInstance(
                ObjectFactory.class,
                ListadoDomiciliosTelefonos.class,
                ArrayList.class
            );
        } catch (JAXBException e) {
            throw Throwables.propagate(e);
        }
    });

    @SneakyThrows
    public static String marshal(Object object) {
        try (StringBuilderWriter resultWriter = new StringBuilderWriter()) {
            Marshaller marshaller = jaxbContext.get().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(object, resultWriter);
            resultWriter.flush();
            StringBuilder resultBuilder = resultWriter.getBuilder();
            return resultBuilder.toString();
        }
    }

    @SneakyThrows
    public static Object unmarshal(InputStream is) {
        Unmarshaller unmarshaller = jaxbContext.get().createUnmarshaller();
        Object object = unmarshaller.unmarshal(is);
        return object;
    }

    public static JAXBElement wrapInRootElement(String namespace, String qname, Object value) {
        return new JAXBElement(new QName(namespace, qname), value.getClass(), value);
    }
}
