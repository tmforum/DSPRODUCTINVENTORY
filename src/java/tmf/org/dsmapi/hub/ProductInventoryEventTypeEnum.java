/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub;

public enum ProductInventoryEventTypeEnum {

    ProductCreationNotification("ProductCreationNotification"),
    ProductValueChangeNotification("ProductValueChangeNotification"),
    ProductStatusChangeNotification("ProductStatusChangeNotification"),
    ProductDeletionNotification("ProductDeletionNotification"),
    ProductBatchNotification("ProductBatchNotification"),
    ProductTransactionNotification("ProductTransactionNotification"),
    ProductSynchronizationNotification("ProductSynchronizationNotification");
    
    private String text;

    ProductInventoryEventTypeEnum(String text) {
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
    public static tmf.org.dsmapi.hub.ProductInventoryEventTypeEnum fromString(String text) {
        if (text != null) {
            for (ProductInventoryEventTypeEnum b : ProductInventoryEventTypeEnum.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }
}