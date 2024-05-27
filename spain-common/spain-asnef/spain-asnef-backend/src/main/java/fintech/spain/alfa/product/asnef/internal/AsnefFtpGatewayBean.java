package fintech.spain.alfa.product.asnef.internal;

import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import fintech.DateUtils;
import fintech.spain.asnef.AsnefFtpGateway;
import fintech.spain.asnef.AsnefFtpProperties;
import fintech.spain.asnef.AsnefGatewayResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.PrintCommandListener;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPSClient;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

@Slf4j
@Component(AsnefFtpGatewayBean.NAME)
class AsnefFtpGatewayBean implements AsnefFtpGateway {

    static final String NAME = "ftp-gateway";

    @Override
    public void upload(AsnefFtpProperties.Config config, InputStream input) {
        FTPSClient client = new FTPSClient();
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));
        Stopwatch stopwatch = Stopwatch.createStarted();
        try {
            connect(config, client);

            client.cwd(config.getInDirectory());
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.storeFile(config.getFilenameZip(), zip(config.getFilenameTxt(), input));

            disconnect(client);
        } catch (Exception e) {
            log.error("Unable to upload file to equifax ftp");
            throw new IllegalStateException(e);
        } finally {
            log.info("Completed Asnef Ftp upload: in {} ms", stopwatch.elapsed(TimeUnit.MILLISECONDS));
        }
    }

    @Override
    public AsnefGatewayResponse download(AsnefFtpProperties.Config config, LocalDate when) {
        FTPSClient client = new FTPSClient();
        client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out), true));

        try {
            connect(config, client);

            client.cwd(config.getOutDirectory());

            FTPFile[] ftpFiles = client.listFiles(config.getOutDirectory(), file -> DateUtils.goe(file.getTimestamp().getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate(), when));

            List<AsnefGatewayResponse.Entry> entries = Lists.newArrayList();

            for (FTPFile ftpFile : ftpFiles) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                client.retrieveFile(ftpFile.getName(), output);
                output.flush();

                ZipInputStream input = new ZipInputStream(output.toInputStream());
                input.getNextEntry();

                String content = Joiner.on(IOUtils.LINE_SEPARATOR_WINDOWS).join(IOUtils.readLines(input, StandardCharsets.US_ASCII));

                input.close();
                output.close();

                entries.add(new AsnefGatewayResponse.Entry(ftpFile.getName(), content));
            }

            disconnect(client);

            return new AsnefGatewayResponse(entries);
        } catch (IOException e) {
            log.error("Unable to download file from equifax ftp");

            throw new IllegalStateException(e);
        }
    }

    private void connect(AsnefFtpProperties.Config config, FTPSClient client) throws IOException {
        client.connect(config.getHost());
        client.login(config.getUsername(), config.getPassword());
        client.execPBSZ(0);
        client.execPROT("P");
        client.enterLocalPassiveMode();
    }

    private void disconnect(FTPSClient client) throws IOException {
        client.logout();
        client.disconnect();
    }

    private InputStream zip(String filename, InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        ZipOutputStream zipOutput = new ZipOutputStream(output);
        zipOutput.putNextEntry(new ZipEntry(filename));

        IOUtils.copy(input, zipOutput);

        zipOutput.closeEntry();
        zipOutput.close();

        return new ByteArrayInputStream(output.toByteArray());
    }
}
