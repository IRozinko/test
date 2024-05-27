package fintech.spain.alfa.web.controllers.web;


import fintech.ekomi.EKomiService;
import fintech.ekomi.exception.EKomiException;
import fintech.spain.alfa.web.models.RatingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingApi {

    @Autowired
    private EKomiService eKomiService;

    @GetMapping("/api/public/web/rating")
    public RatingResponse getRating() throws EKomiException {
        return eKomiService.getCompanyRating()
            .map(r -> new RatingResponse()
                .setAverage(r.getAverage())
                .setCount(r.getCount())
            ).orElse(new RatingResponse());
    }
}
