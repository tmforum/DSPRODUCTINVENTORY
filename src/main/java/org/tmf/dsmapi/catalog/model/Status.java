/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.catalog.model;

/**
 *
 * @author ecus6396
 */
public enum Status {

    Created("created"),
    Aborted("aborted"),
    Cancelled("cancelled"),
    Active("active"),
    Terminated("terminated"),
    Suspended("suspended"),
    PendingActive("pending active"),
    PendingTerminate("pending terminate");
    
    private String text;

    Status(String text) {
        this.text = text;
    }

    /**
     *
     * @return
     */
    public String getText() {
        return this.text;
    }

    /**
     *
     * @param text
     * @return
     */
    public static Status fromString(String text) {
        if (text != null) {
            for (Status b : Status.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}
