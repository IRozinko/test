package fintech.spain.alfa.product.scoring

import fintech.crm.bankaccount.ClientBankAccountService
import fintech.crm.client.Client
import fintech.crm.client.ClientService
import fintech.crm.contacts.PhoneContactService
import org.codehaus.jackson.map.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.stereotype.Component

import javax.sql.DataSource

@Component
class AffiliateRequestScoringValuesProvider extends SqlScoringProvider {

    @Autowired
    private ClientService clientService

    @Autowired
    private PhoneContactService phoneContactService

    @Autowired
    private ClientBankAccountService clientBankAccountService

    private final ObjectMapper objectMapper = new ObjectMapper()

    private final String query = """SELECT json_build_object('raw', COALESCE(json_agg(s), '[]'::json)) as json_result FROM
        (select created_at,
        created_at::date as created_on,
        created_by,
        request_type,
        request
        from affiliate.affiliate_request
        where created_at > current_date - 180
        and request_type in ('step1', 'step1V1')
        and (request ->>'id_doc_number' = :dni OR request ->>'phone' = :phone OR request ->>'IBAN' = :iban or request ->>'email' = :email)
        order by created_at desc limit 50) s """

    AffiliateRequestScoringValuesProvider(DataSource dataSource) {
        super(dataSource)
    }

    @Override
    Properties provide(long clientId) {
        Client client = clientService.get(clientId);
        String iban = clientBankAccountService.findPrimaryByClientId(clientId).map { a -> a.getAccountNumber() }.orElse(null)
        String phone = phoneContactService.findPrimaryPhone(clientId).map { p -> p.phoneNumber }.orElse(null)
        HashMap jsonData = jdbcTemplate.queryForObject(query,
            new MapSqlParameterSource()
                .addValue("email", client.email)
                .addValue("dni", client.documentNumber)
                .addValue("iban", iban)
                .addValue("phone", phone),
            { rs, rn -> objectMapper.readValue(rs.getString("json_result"), HashMap.class) }
        )
        Properties properties = new Properties()
        properties.put("affiliate_array_raw_json", jsonData);
        return properties
    }
}
