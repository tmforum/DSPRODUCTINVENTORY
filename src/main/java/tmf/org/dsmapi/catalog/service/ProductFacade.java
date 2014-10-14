package tmf.org.dsmapi.catalog.service;

import tmf.org.dsmapi.commons.facade.AbstractFacade;
import java.util.Date;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.catalog.model.Product;
import tmf.org.dsmapi.catalog.model.ProductFields;
import static tmf.org.dsmapi.catalog.model.ProductFields.DESCRIPTION;
import static tmf.org.dsmapi.catalog.model.ProductFields.ISBUNDLE;
import static tmf.org.dsmapi.catalog.model.ProductFields.ISCUSTOMERVISIBLE;
import static tmf.org.dsmapi.catalog.model.ProductFields.NAME;
import static tmf.org.dsmapi.catalog.model.ProductFields.PRODUCT_CHARACTERISTICS;
import static tmf.org.dsmapi.catalog.model.ProductFields.PRODUCT_SERIAL_NUMBER;
import static tmf.org.dsmapi.catalog.model.ProductFields.REALIZING_RESOURCES;
import static tmf.org.dsmapi.catalog.model.ProductFields.START_DATE;
import static tmf.org.dsmapi.catalog.model.ProductFields.STATUS;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.ExceptionType;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.hub.service.EventPublisherLocal;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class ProductFacade extends AbstractFacade<Product> {

    @PersistenceContext(unitName = "DSProductInventoryPU")
    private EntityManager em;
    @EJB
    EventPublisherLocal publisher;

    public ProductFacade() {
        super(Product.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public void create(Product entity) throws BadUsageException {
        if (entity.getId() != null) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_GENERIC, "While creating Product, id must be null");
        }

        super.create(entity);
    }

    /**
     *
     * @param patch
     * @param fields
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */
    public Product updateAttributes(Product patch, Set<ProductFields> fields) throws BadUsageException, UnknownResourceException {

        Product current = this.find(patch.getId());

        if (current == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }

        boolean isStatusModified = false;
        for (ProductFields token : fields) {
            switch (token) {
                case NAME:
                    current.setName(patch.getName());
                    break;
                case DESCRIPTION:
                    if (patch.getDescription() != null) {
                        current.setDescription(patch.getDescription());
                    }
                    break;
                case STATUS:
                    current.setStatus(patch.getStatus());
                    isStatusModified = true;
                    break;
                case ISCUSTOMERVISIBLE:
                    current.setIsCustomerVisible(patch.getIsCustomerVisible());
                    break;
                case ISBUNDLE:
                    current.setIsBundle(patch.getIsBundle());
                    break;
                case PRODUCT_SERIAL_NUMBER:
                    current.setProductSerialNumber(patch.getProductSerialNumber());
                    break;
                case PRODUCT_OFFERING:
                    current.setProductOffering(patch.getProductOffering());
                    break;
                case PRODUCT_SPECIFICATION:
                    current.setProductSpecification(patch.getProductSpecification());
                    break;
                case PRODUCT_CHARACTERISTICS:
                    current.setProductCharacteristics(patch.getProductCharacteristics());
                    break;
                case PRODUCT_RELATIONSHIPS:
                    current.setProductRelationships(patch.getProductRelationships());
                    break;
                case START_DATE:
                    current.setStartDate(patch.getStartDate());
                    break;
                case ORDER_DATE:
                    current.setOrderDate(patch.getOrderDate());
                    break;
                case TERMINATION_DATE:
                    current.setTerminationDate(patch.getTerminationDate());
                    break;
                case BILLINGACCOUNT:
                    current.setBillingAccount(patch.getBillingAccount());  // Replace Notes
                    break;
                case RELATEDPARTIES:
                    current.setRelatedParties(patch.getRelatedParties());
                    break;
                case AGREEMENT:
                    current.setAgreement(patch.getAgreement());
                    break;
                case PLACE:
                    current.setPlace(patch.getPlace());
                    break;
                case REALIZING_RESOURCES:
                    current.setRealizingResources(patch.getRealizingResources());
                    break;
                case REALIZING_SERVICES:
                    current.setRealizingServices(patch.getRealizingServices());
                    break;
                case PRODUCT_PRICES:
                    if (patch.getProductPrices() != null) {
                        current.setProductPrices(patch.getProductPrices());
                    }
                    break;
            }
        }
        em.merge(current);
        if (isStatusModified) {
            //Event status changed
            publisher.statusChangedNotification(current, "Product Status Modified", new Date());
        }
        //Event changed
        publisher.valueChangedNotification(current, "Product Modified", new Date());

        return current;
    }

}
