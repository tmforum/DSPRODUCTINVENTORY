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

        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.convertValue(partialProduct, JsonNode.class);
        boolean isStatusModified = false;
        String token = "status";
        if (BeanUtils.verify(partialProduct, node, token)) {
            stateModel.checkTransition(currentProduct.getStatus(), partialProduct.getStatus());
            isStatusModified = true;
        }

        partialProduct.setId(id);
        if (BeanUtils.patch(currentProduct, partialProduct, node)) {
            publisher.valueChangedNotification(currentProduct, new Date());
                if(isStatusModified){
                    publisher.statusChangedNotification(currentProduct, new Date());
                }
        }

        return currentProduct;
    }
}
