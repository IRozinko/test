package fintech.ekomi;

import fintech.ekomi.exception.EKomiException;
import fintech.ekomi.model.EKomiRating;

import java.util.Optional;

public interface EKomiService {


    Optional<EKomiRating> getCompanyRating() throws EKomiException;
}
