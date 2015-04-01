package org.tmf.dsmapi.product;

import java.util.Date;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.ExceptionType;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.utils.BeanUtils;
import org.tmf.dsmapi.product.model.Product;
import org.tmf.dsmapi.product.event.ProductEventPublisherLocal;
import org.tmf.dsmapi.product.model.State;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class ProductFacade extends AbstractFacade<Product> {

    @PersistenceContext(unitName = "DSProductPU")
    private EntityManager em;
    @EJB
    ProductEventPublisherLocal publisher;
    StateModelImpl stateModel = new StateModelImpl();

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

    public Product updateAttributs(long id, Product partialProduct) throws UnknownResourceException, BadUsageException {
        Product currentProduct = this.find(id);

        if (currentProduct == null) {
            throw new UnknownResourceException(ExceptionType.UNKNOWN_RESOURCE);
        }
        
        verifyStatus(currentProduct, partialProduct);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(partialProduct, JsonNode.class);
        partialProduct.setId(id);
        if (BeanUtils.patch(currentProduct, partialProduct, node)) {
            publisher.valueChangedNotification(currentProduct, new Date());
        }

        return currentProduct;
    }
    
    public void verifyStatus(Product currentProduct, Product partialProduct) throws BadUsageException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(partialProduct, JsonNode.class);
        if (BeanUtils.verify(partialProduct, node, "status")) {
            stateModel.checkTransition(currentProduct.getStatus(), partialProduct.getStatus());
            publisher.statusChangedNotification(currentProduct, new Date());
//        } else {
//            throw new BadUsageException(ExceptionType.BAD_USAGE_MANDATORY_FIELDS, "state" + " is not found");
        }
    }

    public void verifyFirstStatus(State newState) throws BadUsageException {
        
        if ( ! newState.name().equalsIgnoreCase(State.CREATED.name())) {
            throw new BadUsageException(ExceptionType.BAD_USAGE_FLOW_TRANSITION, "status " + newState.value() +" is not the first state, attempt : "+State.CREATED.value());
        }
    }

}
