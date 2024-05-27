package fintech.spain.experian.impl;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.output.StringBuilderWriter;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ObjectSerializer {

    private final Supplier<JAXBContext> jaxbContext = Suppliers.memoize(this::initJaxbContext);

    private JAXBContext initJaxbContext() {
        try {
            return JAXBContext.newInstance(
                v1.cais.servicios.experian.ObjectFactory.class,
                v2.cais.servicios.experian.ObjectFactory.class,
                cais.servicios.experian.Informe.class,
                v1.concursales.servicios.experian.ObjectFactory.class,
                concursales.servicios.experian.Informe.class,
                concursales.servicios.experian.GenerarInformeResponse.class,
                ArrayList.class
            );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public String marshal(List<?> objectsToMarshal) {
        try (StringBuilderWriter resultWriter = new StringBuilderWriter()) {
            Marshaller marshaller = jaxbContext.get().createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            for (Object obj : objectsToMarshal) {
                marshaller.marshal(obj, resultWriter);
                resultWriter.write("\n\n");
            }
            resultWriter.flush();
            StringBuilder resultBuilder = resultWriter.getBuilder();
            return resultBuilder.toString();
        }
    }

    @SneakyThrows
    public Object unmarshal(InputStream is) {
        Unmarshaller unmarshaller = jaxbContext.get().createUnmarshaller();
        Object object = unmarshaller.unmarshal(is);
        return object;
    }

}
