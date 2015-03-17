package org.tmf.dsmapi.product.hub.service;

import javax.ejb.Local;
import org.tmf.dsmapi.product.hub.model.ProductEvent;
import org.tmf.dsmapi.hub.model.Hub;

@Local
public interface ProductRESTEventPublisherLocal {

    public void publish(Hub hub, ProductEvent event);
    
}
