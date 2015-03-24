package org.tmf.dsmapi.product;

import org.tmf.dsmapi.commons.workflow.StateModelBase;
import org.tmf.dsmapi.product.model.State;

/**
 *
 * @author maig7313
 */
public class StateModelImpl extends StateModelBase<State> {
    
    /**
     *
     */
    public StateModelImpl() {
        super(State.class);
    }    

    /**
     *
     */
    @Override
    protected void draw() {
        // First
        fromFirst(State.CREATED).to(
                State.PENDING_ACTIVE,
                State.ACTIVE);

        // Somewhere
        from(State.PENDING_ACTIVE).to(
                State.ABORTED,
                State.CANCELLED);
        from(State.ACTIVE).to(
                State.SUSPENDED,
                State.TERMINATED,
                State.PENDING_TERMINATE);       
        from(State.SUSPENDED).to(
                State.ACTIVE);
        from(State.PENDING_TERMINATE).to(
                State.TERMINATED);

        // Final
        from(State.ABORTED);
        from(State.CANCELLED);
        from(State.TERMINATED);
    }
}
