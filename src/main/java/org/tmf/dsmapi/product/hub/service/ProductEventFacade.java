package org.tmf.dsmapi.product.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.tmf.dsmapi.commons.facade.AbstractFacade;
import org.tmf.dsmapi.product.hub.model.ProductEvent;

@Stateless
public class ProductEventFacade extends AbstractFacade<ProductEvent>{
    
    @PersistenceContext(unitName = "DSProductPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public ProductEventFacade() {
        super(ProductEvent.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
