package fintech.spain.experian.impl.concursales;


import concursales.servicios.experian.GenerarInformeResponse;
import concursales.servicios.experian.PeticionInforme;

interface ExperianConcursalesProvider {

    GenerarInformeResponse request(PeticionInforme reportRequest);
}
