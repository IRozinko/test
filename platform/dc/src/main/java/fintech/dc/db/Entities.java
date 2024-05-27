package fintech.dc.db;

public class Entities {

    public static final String SCHEMA = "dc";

    public static final QDebtEntity debt = QDebtEntity.debtEntity;
    public static final QDebtActionEntity action = QDebtActionEntity.debtActionEntity;
    public static final QDcAgentEntity agent = QDcAgentEntity.dcAgentEntity;
    public static final QDcAgentAbsenceEntity agentAbsence = QDcAgentAbsenceEntity.dcAgentAbsenceEntity;
}
