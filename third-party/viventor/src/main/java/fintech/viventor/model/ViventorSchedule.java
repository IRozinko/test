package fintech.viventor.model;

import lombok.Data;
import lombok.NonNull;

@Data
public class ViventorSchedule {

    public ViventorSchedule(@NonNull ViventorCustomSchedule custom) {
        this.custom = custom;
    }

    public ViventorSchedule(@NonNull ViventorPaydaySchedule payday) {
        this.payday = payday;
    }

    private ViventorCustomSchedule custom;

    private ViventorPaydaySchedule payday;

}
