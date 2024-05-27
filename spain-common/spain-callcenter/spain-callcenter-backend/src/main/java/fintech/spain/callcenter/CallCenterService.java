package fintech.spain.callcenter;

public interface CallCenterService {
    void addPhoneRecordsToCallList(Long clientId) throws CallCenterException;

    void removePhoneRecordsFromCallList(Long clientId) throws CallCenterException;

    void updatePhoneRecordsFromProvider();
}
