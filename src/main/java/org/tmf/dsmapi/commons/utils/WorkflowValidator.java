package org.tmf.dsmapi.commons.utils;

import org.tmf.dsmapi.product.model.State;


public class WorkflowValidator {

    public static boolean isCorrect(State current, State next) {
        

        boolean valid = false;

        switch (current) {
            case CREATED:
                switch (next) {
                    case PENDING_ACTIVE:
                    case ACTIVE:
                        valid = true;
                        break;
                }
                break;
            case ACTIVE:
                switch (next) {
                    case SUSPENDED:
                    case PENDING_TERMINATE:
                    case TERMINATED:
                        valid = true;
                        break;
                }
                break;
            case PENDING_ACTIVE:
                switch (next) {
                    case ABORTED:
                    case ACTIVE:
                    case CANCELLED:
                        valid = true;
                        break;
                }
                break;
            case PENDING_TERMINATE:
                switch (next) {
                    case TERMINATED:
                        valid = true;
                        break;
                }
                break;
        }
        return valid;
    }

}
