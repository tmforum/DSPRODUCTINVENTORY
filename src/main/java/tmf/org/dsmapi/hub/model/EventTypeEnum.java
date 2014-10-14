/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.model;

public enum EventTypeEnum {

    ProductCreationNotification("ProductCreationNotification"),
    ProductValueChangeNotification("ProductValueChangeNotification"),
    ProductStatusChangeNotification("ProductStatusChangeNotification"),
    ProductDeletionNotification("ProductDeletionNotification"),
    ProductBatchNotification("ProductBatchNotification"),
    ProductTransactionNotification("ProductTransactionNotification"),
    ProductSynchronizationNotification("ProductSynchronizationNotification");
    
    private String text;

    EventTypeEnum(String text) {
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
    public static tmf.org.dsmapi.hub.model.EventTypeEnum fromString(String text) {
        if (text != null) {
            for (EventTypeEnum b : EventTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}