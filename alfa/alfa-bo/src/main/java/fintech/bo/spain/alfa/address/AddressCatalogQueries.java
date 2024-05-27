package fintech.bo.spain.alfa.address;

import lombok.Data;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

import static fintech.bo.spain.alfa.db.jooq.alfa.Tables.ADDRESS;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Component
public class AddressCatalogQueries {

    @Autowired
    private DSLContext db;

    public List<String> listPostalCodes() {
        return db.select()
            .distinctOn(ADDRESS.POSTAL_CODE)
            .from(ADDRESS)
            .fetch(ADDRESS.POSTAL_CODE);
    }

    public List<String> listProvinces(AddressQuery query) {
        return db.select()
            .distinctOn(ADDRESS.PROVINCE)
            .from(ADDRESS).where(buildWhere(query)).fetch(ADDRESS.PROVINCE);
    }

    public List<String> listCities(AddressQuery query) {
        return db.select()
            .distinctOn(ADDRESS.CITY)
            .from(ADDRESS)
            .where(buildWhere(query))
            .fetch(ADDRESS.CITY);
    }

    private Condition buildWhere(AddressQuery query) {
        Condition where = ADDRESS.POSTAL_CODE.isNotNull();
        if (isNotBlank(query.getPostalCode())) {
            where = where.and(ADDRESS.POSTAL_CODE.eq(query.getPostalCode()));
        }
        if (isNotBlank(query.getCity())) {
            where = where.and(ADDRESS.CITY.eq(query.getCity()));
        }
        if (isNotBlank(query.getProvince())) {
            where = where.and(ADDRESS.PROVINCE.eq(query.getProvince()));
        }

        return where;
    }

    @Data
    public static class AddressQuery {
        private String postalCode;
        private String province;
        private String city;
    }

}
