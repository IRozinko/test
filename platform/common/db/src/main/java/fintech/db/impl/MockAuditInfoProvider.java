package fintech.db.impl;

import fintech.db.AuditInfoProvider;
import org.springframework.stereotype.Component;


@Component
public class MockAuditInfoProvider implements AuditInfoProvider {
    @Override
    public AuditInfo getInfo() {
        return new AuditInfo("mock", "mock", "mock", "127.0.0.1", "referer");
    }
}
