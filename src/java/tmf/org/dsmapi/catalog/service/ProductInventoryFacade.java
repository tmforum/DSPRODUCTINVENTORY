/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.catalog.service;

import java.util.Date;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.catalog.ProductInventory;
import tmf.org.dsmapi.catalog.ProductInventoryFields;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.DESCRIPTION;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.ISBUNDLE;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.ISCUSTOMERVISIBLE;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.NAME;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.PRODUCT_CHARACTERISTICS;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.PRODUCT_SERIAL_NUMBER;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.REALIZING_RESOURCES;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.START_DATE;
import static tmf.org.dsmapi.catalog.ProductInventoryFields.STATUS;
import tmf.org.dsmapi.catalog.Status;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.TMFDate;
import tmf.org.dsmapi.hub.service.PublisherLocal;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class ProductInventoryFacade extends AbstractFacade<ProductInventory> {

    @PersistenceContext(unitName = "DSProductInventoryPU")
    private EntityManager em;
    @EJB
    PublisherLocal publisher;

    public ProductInventoryFacade() {
        super(ProductInventory.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void create(ProductInventory entity) throws BadUsageException {
        if (entity.getId() != null) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_GENERIC, "While creating a ProductInventoring id should be null");
        }

        super.create(entity);
    }

    /**
     *
     * @param patchPI
     * @param fields
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */
    public ProductInventory updateAttributes(ProductInventory patchPI, Set<ProductInventoryFields> fields) throws BadUsageException, UnknownResourceException {

        ProductInventory currentPI = this.find(patchPI.getId());

        if (currentPI == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

//        if (fields.contains(STATUS) & !(fields.contains(STATUS_CHANGE_REASON))) {
//            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "While updating 'status', please provide a 'statusChangeReason'");
//        }
        boolean isStatusModified = false;
        for (ProductInventoryFields token : fields) {
            switch (token) {
                case NAME:
                    currentPI.setName(patchPI.getName());
                    break;
                case DESCRIPTION:
                    if (patchPI.getDescription() != null) {
                        currentPI.setDescription(patchPI.getDescription());
                    }
                    break;
                case STATUS:
                    currentPI.setStatus(patchPI.getStatus());
                    isStatusModified = true;
                    break;
                case ISCUSTOMERVISIBLE:
                    currentPI.setIsCustomerVisible(patchPI.getIsCustomerVisible());
                    break;
                case ISBUNDLE:
                    currentPI.setIsBundle(patchPI.getIsBundle());
                    break;
                case PRODUCT_SERIAL_NUMBER:
                    currentPI.setProductSerialNumber(patchPI.getProductSerialNumber());
                    break;
                case PRODUCT_OFFERING:
                    currentPI.setProductOffering(patchPI.getProductOffering());
                    break;
                case PRODUCT_SPECIFICATION:
                    currentPI.setProductSpecification(patchPI.getProductSpecification());
                    break;
                case PRODUCT_CHARACTERISTICS:
                    currentPI.setProductCharacteristics(patchPI.getProductCharacteristics());
                    break;
                case PRODUCT_RELATIONSHIPS:
                    currentPI.setProductRelationships(patchPI.getProductRelationships());
                    break;
                case START_DATE:
                    currentPI.setStartDate(patchPI.getStartDate());
                    break;
                case ORDER_DATE:
                    currentPI.setOrderDate(patchPI.getOrderDate());
                    break;
                case TERMINATION_DATE:
                    currentPI.setTerminationDate(patchPI.getTerminationDate());
                    break;
                case BILLINGACCOUNT:
                    currentPI.setBillingAccount(patchPI.getBillingAccount());  // Replace Notes
                    break;
                case RELATEDPARTIES:
                    currentPI.setRelatedParties(patchPI.getRelatedParties());
                    break;
                case AGREEMENT:
                    currentPI.setAgreement(patchPI.getAgreement());
                    break;
                case PLACE:
                    currentPI.setPlace(patchPI.getPlace());
                    break;
                case REALIZING_RESOURCES:
                    currentPI.setRealizingResources(patchPI.getRealizingResources());
                    break;
                case REALIZING_SERVICES:
                    currentPI.setRealizingServices(patchPI.getRealizingServices());
                    break;
                case PRODUCT_PRICES:
                    if (patchPI.getProductPrices() != null) {
                        currentPI.setProductPrices(patchPI.getProductPrices());
                    }
                    break;
            }
        }
        em.merge(currentPI);
        if (isStatusModified) {
            //Event statut pi changed
            publisher.statusChangedNotification(currentPI, "Product Status Inventory Modified", new Date());
        }
        //Event pi changed
        publisher.valueChangedNotification(currentPI, "Product Inventory Modified", new Date());

        return currentPI;
    }
    /**
     *
     * @param productInventoring
     * @param status
     * @param reason
     * @return
     */
//    public ProductInventory updateStatus(ProductInventory productInventoring, Status status, String reason) {
//        productInventoring.setStatus(status);
//        productInventoring.setStatusChangeDate(TMFDate.toString(new Date()));
//        productInventoring.setStatusChangeReason(reason);
//        em.merge(productInventoring);
//        return productInventoring;
//    }
}
