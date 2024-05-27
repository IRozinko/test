package fintech.spain.experian.impl.cais;

import cais.servicios.experian.Informe;
import cais.servicios.experian.PeticionInforme;
import fintech.spain.experian.impl.ObjectSerializer;
import lombok.Cleanup;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import javax.xml.bind.JAXBElement;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component(MockExperianCaisProvider.NAME)
public class MockExperianCaisProvider implements ExperianCaisProvider {

    static final String NAME = "mock-spain-experian-cais-provider";

    public static final String RESUMEN_RESPONSE_NOT_FOUND = "experian/cais-response-resumen-not-found.xml";
    public static final String OPERACIONES_RESPONSE_NOT_FOUND = "experian/cais-response-operaciones-not-found.xml";

    @Autowired
    private ObjectSerializer objectSerializer;

    private String resumenResponseResource = RESUMEN_RESPONSE_NOT_FOUND;

    private String listOperacionesResponseSource = OPERACIONES_RESPONSE_NOT_FOUND;

    private boolean throwError;

    @SneakyThrows
    @Override
    public Informe request(PeticionInforme request) {
        if (throwError) {
            throw new RuntimeException("Simulating error");
        }

        String source = ExperianCaisConfiguration.RESUMEN.getType().equals(request.getTipoInforme()) ? resumenResponseResource : listOperacionesResponseSource;

        @Cleanup
        InputStream inputStream = new ClassPathResource(source).getInputStream();

        JAXBElement object = (JAXBElement) objectSerializer.unmarshal(inputStream);
        List<JAXBElement> content = new ArrayList<>();
        content.add(object);

        Informe informe = new Informe();
        Field contentField = ReflectionUtils.findField(Informe.class, "content");
        contentField.setAccessible(true);
        ReflectionUtils.setField(contentField, informe, content);
        return informe;
    }

    public void setResumenResponseResource(String resumenResponseResource) {
        this.resumenResponseResource = resumenResponseResource;
    }

    public void setListOperacionesResponseSource(String listOperacionesResponseSource) {
        this.listOperacionesResponseSource = listOperacionesResponseSource;
    }

    public void setThrowError(boolean throwError) {
        this.throwError = throwError;
    }
}
