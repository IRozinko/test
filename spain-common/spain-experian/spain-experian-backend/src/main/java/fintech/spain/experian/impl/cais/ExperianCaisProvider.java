package fintech.spain.experian.impl.cais;

import cais.servicios.experian.Informe;
import cais.servicios.experian.PeticionInforme;

public interface ExperianCaisProvider {

    Informe request(PeticionInforme reportRequest);
}
