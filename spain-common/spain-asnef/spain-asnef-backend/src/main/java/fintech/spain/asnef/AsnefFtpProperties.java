package fintech.spain.asnef;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "asnef.ftp")
public class AsnefFtpProperties {

    private Config notificaRp;

    private Config fotoaltas;

    public AsnefFtpProperties(ReportingEntityProvider reportingEntityProvider) {
        this.notificaRp = new Config(AsnefConstants.Rp.getFilenameTxt(reportingEntityProvider.getRpNotificaReportingEntity()), AsnefConstants.Rp.getFilenameZip(reportingEntityProvider.getRpNotificaReportingEntity()));
        this.fotoaltas = new Config(AsnefConstants.Fotoaltas.getFilenameTxt(reportingEntityProvider.getFotoaltasReportingEntity()), AsnefConstants.Fotoaltas.getFilenameZip(reportingEntityProvider.getFotoaltasReportingEntity()));
    }

    public Config get(LogType type) {
        if (type == LogType.NOTIFICA_RP) {
            return notificaRp;
        }

        if (type == LogType.FOTOALTAS) {
            return fotoaltas;
        }

        throw new IllegalStateException("Unable to resolve asnef ftp config for log type: " + type);
    }

    @Data
    public static class Config {

        private final String filenameTxt;

        private final String filenameZip;

        private String host;

        private String username;

        private String password;

        private String outDirectory;

        private String inDirectory;

        public Config(String filenameTxt, String filenameZip) {
            this.filenameTxt = filenameTxt;
            this.filenameZip = filenameZip;
        }
    }
}
