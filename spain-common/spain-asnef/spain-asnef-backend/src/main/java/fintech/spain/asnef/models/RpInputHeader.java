package fintech.spain.asnef.models;

public interface RpInputHeader {

    RpType getType();

    enum RpType {

        ERROR, DEVO
    }
}
