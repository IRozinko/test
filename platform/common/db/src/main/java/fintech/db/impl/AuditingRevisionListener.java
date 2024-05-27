package fintech.db.impl;

import fintech.db.AuditInfoProvider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.envers.RevisionListener;

@Slf4j
public class AuditingRevisionListener implements RevisionListener {

    static AuditInfoProvider provider;

    @Override
    public void newRevision(Object revisionEntity) {
        if (provider == null) {
            log.warn("Audit info provider not set");
            return;
        }
        AuditedRevisionEntity entity = (AuditedRevisionEntity) revisionEntity;
        AuditInfoProvider.AuditInfo info = provider.getInfo();
        entity.setUserName(info.getUsername());
        entity.setRequestId(info.getRequestId());
        entity.setRequestUri(info.getRequestUri());
        entity.setIpAddress(info.getIpAddress());
    }
}
