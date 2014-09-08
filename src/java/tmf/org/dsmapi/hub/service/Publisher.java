/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import tmf.org.dsmapi.catalog.ProductInventory;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.hub.Event;
import tmf.org.dsmapi.hub.ProductInventoryEventTypeEnum;

/**
 *
 * @author pierregauthier should be async or called with MDB
 */
@Stateless
//@Asynchronous bug in 7.3
@Asynchronous
public class Publisher implements PublisherLocal {

    @EJB
    HubFacade hubFacade;
    @EJB
    EventFacade hubEventFacade;
    @EJB
    RESTEventPublisherLocal restEventPublisher;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //Access Hubs using callbacks and send to http publisher 
    //(pool should be configured around the RESTEventPublisher bean)
    //Loop into array of Hubs
    //Call RestEventPublisher - Need to implement resend policy plus eviction
    //Filtering is done in RestEventPublisher based on query expression
    @Override
    public void publish(Object event) {
        System.out.println("Sending Event");

        String id = null;

        if (event instanceof Event) {
            try {
                Event hubEvent = (Event)event;
                hubEvent.setId(null);
                hubEventFacade.create(hubEvent);
                id = hubEvent.getId();
            } catch (BadUsageException ex) {
                Logger.getLogger(Publisher.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        List<Hub> hubList = hubFacade.findAll();
        Iterator<Hub> it = hubList.iterator();
        while (it.hasNext()) {
            Hub hub = it.next();
            restEventPublisher.publish(hub, event);
        }
        System.out.println("Sending Event After, id of event : "+id);
    }

    @Override
    public void createNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);        
        event.setEventType(ProductInventoryEventTypeEnum.ProductCreationNotification);
        publish(event);

    }

    @Override
    public void statusChangedNotification(ProductInventory bean, String reason, Date date) {

        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductStatusChangeNotification);
        publish(event);

    }

    @Override
    public void valueChangedNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductValueChangeNotification);
        publish(event);
    }

    @Override
    public void deletionNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductDeletionNotification);
        publish(event);
    }

    @Override
    public void batchNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductBatchNotification);
        publish(event);
    }

    @Override
    public void transactionNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductTransactionNotification);
        publish(event);
    }

    @Override
    public void synchronizationNotification(ProductInventory bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(ProductInventoryEventTypeEnum.ProductSynchronizationNotification);
        publish(event);
    }

}
