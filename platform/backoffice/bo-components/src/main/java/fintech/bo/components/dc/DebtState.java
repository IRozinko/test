package fintech.bo.components.dc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public enum DebtState {

    SOFT,
    STOP_COLLECTION,
    LEGAL;

    public static Set<String> states = ImmutableSet.of(SOFT.name(), STOP_COLLECTION.name(), LEGAL.name());
    public static List<String> statesList = ImmutableList.of(SOFT.name(), STOP_COLLECTION.name(), LEGAL.name());
}
