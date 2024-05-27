package fintech.spain.alfa.web.controllers.web;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import fintech.spain.alfa.product.db.Entities;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AddressApi {

    @Autowired
    private JPAQueryFactory queryFactory;

    @GetMapping("/api/public/web/address-catalog/cities")
    public List<String> getCities(@RequestParam(name = "postalCode", required = false) String postalCode) {
        JPAQuery<String> query = queryFactory.selectDistinct(Entities.address.city)
            .from(Entities.address);
        if (!StringUtils.isBlank(postalCode)) {
            query = query.where(Entities.address.postalCode.eq(postalCode));
        }
        query = query.orderBy(Entities.address.city.asc());
        return query.fetch();
    }
}
