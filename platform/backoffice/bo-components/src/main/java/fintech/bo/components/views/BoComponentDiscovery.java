package fintech.bo.components.views;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class BoComponentDiscovery {

    @Autowired(required = false)
    private List<BoComponentProvider> providers = new ArrayList<>();

    public List<BoComponentProvider> find(BoComponentMetadata metadata) {
        return providers.stream().filter(p -> p.metadata().matches(metadata)).collect(Collectors.toList());
    }
}
