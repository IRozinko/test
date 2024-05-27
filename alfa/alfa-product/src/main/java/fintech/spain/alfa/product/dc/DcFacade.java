package fintech.spain.alfa.product.dc;

import fintech.spain.alfa.product.dc.commands.ReassignDebtCommand;
import fintech.spain.dc.AbstractDcFacade;

public interface DcFacade extends AbstractDcFacade {

    void reassign(ReassignDebtCommand command);

}
