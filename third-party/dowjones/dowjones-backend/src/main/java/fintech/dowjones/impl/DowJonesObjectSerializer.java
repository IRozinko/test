package fintech.dowjones.impl;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import fintech.dowjones.model.search.name.DowJonesObjectFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.InputStream;
import java.util.ArrayList;

@Slf4j
@Component
public class DowJonesObjectSerializer {

    private final Supplier<JAXBContext> jaxbContext = Suppliers.memoize(this::initJaxbContext);

    private JAXBContext initJaxbContext() {
        try {
            return JAXBContext.newInstance(
                DowJonesObjectFactory.class,
                ArrayList.class
            );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @SneakyThrows
    public Object unmarshal(InputStream is) {
        Unmarshaller unmarshaller = jaxbContext.get().createUnmarshaller();
        Object object = unmarshaller.unmarshal(is);
        return object;
    }

}
