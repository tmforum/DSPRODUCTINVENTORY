package org.tmf.dsmapi.product.hub.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
//import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.product.model.Product;
import org.tmf.dsmapi.product.hub.model.ProductEvent;
import org.tmf.dsmapi.product.hub.model.ProductEventTypeEnum;
import org.tmf.dsmapi.product.hub.service.ProductRESTEventPublisherLocal;
import org.tmf.dsmapi.hub.model.Hub;
import org.tmf.dsmapi.hub.service.HubFacade;

/**
 *
 * Should be async or called with MDB
 */
@Stateless
//@Asynchronous
public class ProductEventPublisher implements ProductEventPublisherLocal {

    @EJB
    HubFacade hubFacade;
    @EJB
    ProductEventFacade eventFacade;
    @EJB
    ProductRESTEventPublisherLocal restEventPublisherLocal;

    /** 
     * Add business logic below. (Right-click in editor and choose
     * "Insert Code > Add Business Method")
     * Access Hubs using callbacks and send to http publisher 
     *(pool should be configured around the RESTEventPublisher bean)
     * Loop into array of Hubs
     * Call RestEventPublisher - Need to implement resend policy plus eviction
     * Filtering is done in RestEventPublisher based on query expression
    */ 
    @Override
    public synchronized void publish(ProductEvent event) {
        try {
            eventFacade.create(event);
        } catch (BadUsageException ex) {
            Logger.getLogger(ProductEventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Hub> hubList = hubFacade.findAll();
        Iterator<Hub> it = hubList.iterator();
        while (it.hasNext()) {
            Hub hub = it.next();
            restEventPublisherLocal.publish(hub, event);
        }
    }

    @Override
    public void createNotification(Product bean, Date date) {
        ProductEvent event = new ProductEvent();
        event.setEventTime(date);
        event.setEventType(ProductEventTypeEnum.ProductCreationNotification);
        event.setEvent(bean);
        publish(event);

    }

    @Override
    public void deletionNotification(Product bean, Date date) {
        ProductEvent event = new ProductEvent();
        event.setEventTime(date);
        event.setEventType(ProductEventTypeEnum.ProductDeletionNotification);
        event.setEvent(bean);
        publish(event);
    }
	
    @Override
    public void updateNotification(Product bean, Date date) {
        ProductEvent event = new ProductEvent();
        event.setEventTime(date);
        event.setEventType(ProductEventTypeEnum.ProductUpdateNotification);
        event.setEvent(bean);
        publish(event);
    }

    @Override
    public void valueChangedNotification(Product bean, Date date) {
        ProductEvent event = new ProductEvent();
        event.setEventTime(date);
        event.setEventType(ProductEventTypeEnum.ProductValueChangeNotification);
        event.setEvent(bean);
        publish(event);
    }
}
