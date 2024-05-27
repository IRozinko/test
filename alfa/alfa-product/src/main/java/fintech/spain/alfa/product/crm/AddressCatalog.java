package fintech.spain.alfa.product.crm;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.querydsl.jpa.JPQLQueryFactory;
import fintech.TimeMachine;
import fintech.filestorage.CloudFile;
import fintech.filestorage.FileStorageService;
import fintech.filestorage.SaveFileCommand;
import fintech.spain.alfa.product.db.AddressEntity;
import fintech.spain.alfa.product.db.AddressRepository;
import fintech.spain.alfa.product.db.Entities;
import lombok.Cleanup;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.supercsv.io.CsvBeanReader;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@Transactional
public class AddressCatalog {

    @Autowired
    private JPQLQueryFactory queryFactory;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private FileStorageService fileStorageService;

    public Optional<String> findProvince(String postalCode, String city) {
        String province = queryFactory.select(Entities.address.province)
            .from(Entities.address)
            .where(Entities.address.city.equalsIgnoreCase(city).and(Entities.address.postalCode.eq(postalCode)))
            .limit(1)
            .fetchFirst();
        return Optional.ofNullable(province);
    }

    public Long saveAddress(String postalCode, String city, String province, String state) {
        AddressEntity entity = new AddressEntity();
        entity.setPostalCode(postalCode);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setState(state);
        return addressRepository.saveAndFlush(entity).getId();
    }

    public long count() {
        return addressRepository.count();
    }

    public Long editAddress(Long id, String postalCode, String city, String province, String state) {
        AddressEntity entity = addressRepository.getRequired(id);
        entity.setPostalCode(postalCode);
        entity.setCity(city);
        entity.setProvince(province);
        entity.setState(state);
        return addressRepository.saveAndFlush(entity).getId();
    }

    public void deleteAddress(Long id) {
        addressRepository.delete(id);
    }

    public CloudFile exportAddressCatalog() {
        List<Address> addresses = addressRepository.findAll().stream()
            .map(address -> new Address().setPostalCode(address.getPostalCode()).setCity(address.getCity()).setProvince(address.getProvince()).setState(address.getState()))
            .collect(Collectors.toList());

        File tempFile = null;
        CsvBeanWriter csvBeanWriter;
        try {
            tempFile = File.createTempFile("address_catalog", "export");

            String[] header = {"postalCode", "city", "province", "state"};

            csvBeanWriter = new CsvBeanWriter(new FileWriter(tempFile), CsvPreference.STANDARD_PREFERENCE);
            csvBeanWriter.writeHeader(header);

            for (Address address : addresses) {
                csvBeanWriter.write(address, header);
            }

            csvBeanWriter.close();

            @Cleanup FileInputStream fis = new FileInputStream(tempFile);

            SaveFileCommand saveFileCommand = new SaveFileCommand();
            saveFileCommand.setOriginalFileName(String.format("address_catalog_%s.csv", TimeMachine.today()));
            saveFileCommand.setDirectory("address-catalog-export");
            saveFileCommand.setInputStream(fis);
            saveFileCommand.setContentType("application/csv");
            return fileStorageService.save(saveFileCommand);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } finally {
            FileUtils.deleteQuietly(tempFile);
        }
    }

    public void importAddressCatalog(Long fileId) {
        addressRepository.deleteAll();

        fileStorageService.readContents(fileId, input -> {
            CsvBeanReader csvBeanReader = new CsvBeanReader(new InputStreamReader(input), CsvPreference.STANDARD_PREFERENCE);

            String[] header = {"postalCode", "city", "province", "state"};
            try {
                csvBeanReader.getHeader(true);

                Address address;
                List<AddressEntity> entities = Lists.newArrayList();
                while ((address = csvBeanReader.read(Address.class, header)) != null) {
                    AddressEntity entity = new AddressEntity();
                    entity.setPostalCode(MoreObjects.firstNonNull(address.getPostalCode(), ""));
                    entity.setCity(MoreObjects.firstNonNull(address.getCity(), ""));
                    entity.setProvince(MoreObjects.firstNonNull(address.getProvince(), ""));
                    entity.setState(MoreObjects.firstNonNull(address.getState(), ""));

                    entities.add(entity);
                }

                csvBeanReader.close();

                addressRepository.save(entities);
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        });
    }
}
