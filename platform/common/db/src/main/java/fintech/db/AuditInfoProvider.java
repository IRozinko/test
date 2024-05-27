package fintech.db;


import lombok.Getter;

public interface AuditInfoProvider {

    AuditInfo getInfo();

    @Getter
    class AuditInfo {
        private final String username;
        private final String requestId;
        private final String requestUri;
        private final String ipAddress;
        private final String referer;

        public AuditInfo(String username, String requestId, String requestUri, String ipAddress, String referer) {
            if (username == null) {
                throw new IllegalArgumentException("Null username");
            }
            this.username = username;
            this.requestId = requestId;
            this.requestUri = requestUri;
            this.ipAddress = ipAddress;
            this.referer = referer;
        }
    }
}
