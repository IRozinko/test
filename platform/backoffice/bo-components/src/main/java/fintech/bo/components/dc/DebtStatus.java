package fintech.bo.components.dc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import java.util.List;
import java.util.Set;

public enum DebtStatus {

    IN_WORK, CLOSED, RECOMPRA, CONCURSO, DESESTIMADO;

    public static Set<String> statuses = ImmutableSet.of(IN_WORK.name(),
        CLOSED.name(), RECOMPRA.name(), CONCURSO.name(), DESESTIMADO.name());

    public static List<String> statusesList = ImmutableList.of(IN_WORK.name(),
        CLOSED.name(), RECOMPRA.name(), CONCURSO.name(), DESESTIMADO.name());
}
