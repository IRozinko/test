package fintech.crm.logins.db;

import fintech.db.BaseRepository;

public interface EmailLoginRepository extends BaseRepository<EmailLoginEntity, Long> {

    void deleteByEmail(String email);

    void deleteByClientId(Long clientId);
}
