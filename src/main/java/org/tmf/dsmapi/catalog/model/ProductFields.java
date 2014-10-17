package org.tmf.dsmapi.catalog.model;

import java.util.HashSet;
import java.util.Set;

public enum ProductFields {

    ALL("all"),
    ID("id"),
    NAME("name"),
    DESCRIPTION("description"),
    STATUS("status"),
    ISCUSTOMERVISIBLE("isCustomerVisible"),
    ISBUNDLE("isBundle"),
    PRODUCT_SERIAL_NUMBER("productSerialNumber"),
    START_DATE("startDate"),
    ORDER_DATE("orderDate"),
    TERMINATION_DATE("terminationDate"),
    PRODUCT_OFFERING("productOffering"),
    PRODUCT_SPECIFICATION("productSpecification"),
    PRODUCT_CHARACTERISTICS("productCharacteristics"),
    PRODUCT_RELATIONSHIPS("productRelationships"),
    BILLINGACCOUNT("billingAccount"),
    RELATEDPARTIES("relatedParties"),
    AGREEMENT("agreement"),
    PLACE("place"),
    REALIZING_RESOURCES("realizingResources"),
    REALIZING_SERVICES("realizingServices"),
    PRODUCT_PRICES("productPrices");
    
    private String text;

    ProductFields(String text) {
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
    public static ProductFields fromString(String text) {
        if (text != null) {
            for (ProductFields b : ProductFields.values()) {
                if (text.equalsIgnoreCase(b.text)) {
                    return b;
                }
            }
        }
        return null;
    }

    /**
     *
     * @param fields
     * @return
     */
    public static Set<ProductFields> fromStringToSet(String fields) {
        // Convert fields parameter to a set of TroubleTicketField
        Set<ProductFields> fieldsSet = new HashSet<ProductFields>();
        ProductFields fieldName;
        if (fields != null) {
            String[] tokenArray = fields.split(",");
            for (int i = 0; i < tokenArray.length; i++) {
                fieldName = ProductFields.fromString(tokenArray[i]);
                // Avoid to add null when fieldName doesn't exist
                if (fieldName != null) {
                    fieldsSet.add(fieldName);
                }
            }
        } else {
            // ALL
            fieldsSet.add(ProductFields.ALL);
        }

        return fieldsSet;
    }
}
