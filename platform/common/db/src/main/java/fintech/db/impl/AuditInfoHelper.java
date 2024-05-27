package fintech.db.impl;

import fintech.db.AuditInfoProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class AuditInfoHelper {

    @Autowired
    AuditInfoProvider auditInfoProvider;

    @PostConstruct
    public void init() {
        AuditingRevisionListener.provider = auditInfoProvider;
    }
}
