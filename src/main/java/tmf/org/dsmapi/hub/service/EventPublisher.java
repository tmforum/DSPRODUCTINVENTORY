package tmf.org.dsmapi.hub.service;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import tmf.org.dsmapi.catalog.model.Product;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.hub.model.Event;
import tmf.org.dsmapi.hub.model.EventTypeEnum;
import tmf.org.dsmapi.hub.model.Hub;

/**
 *
 * @author pierregauthier should be async or called with MDB
 */
@Stateless
@Asynchronous
public class EventPublisher implements EventPublisherLocal {

    @EJB
    HubFacade hubFacade;
    @EJB
    EventFacade eventFacade;
    @EJB
    RESTEventPublisherLocal restEventPublisherLocal;

    // Add business logic below. (Right-click in editor and choose
    // "Insert Code > Add Business Method")
    //Access Hubs using callbacks and send to http publisher 
    //(pool should be configured around the RESTEventPublisher bean)
    //Loop into array of Hubs
    //Call RestEventPublisher - Need to implement resend policy plus eviction
    //Filtering is done in RestEventPublisher based on query expression
    @Override
    public void publish(Event event) {
        try {
            eventFacade.create(event);
        } catch (BadUsageException ex) {
            Logger.getLogger(EventPublisher.class.getName()).log(Level.SEVERE, null, ex);
        }

        List<Hub> hubList = hubFacade.findAll();
        Iterator<Hub> it = hubList.iterator();
        while (it.hasNext()) {
            Hub hub = it.next();
            restEventPublisherLocal.publish(hub, event);
        }
    }

    @Override
    public void createNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductCreationNotification);
        publish(event);

    }

    @Override
    public void statusChangedNotification(Product bean, String reason, Date date) {

        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductStatusChangeNotification);
        publish(event);

    }

    @Override
    public void valueChangedNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductValueChangeNotification);
        publish(event);
    }

    @Override
    public void deletionNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductDeletionNotification);
        publish(event);
    }

    @Override
    public void batchNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductBatchNotification);
        publish(event);
    }

    @Override
    public void transactionNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductTransactionNotification);
        publish(event);
    }

    @Override
    public void synchronizationNotification(Product bean, String reason, Date date) {
        Event event = new Event();
        event.setResource(bean);
        event.setDate(date);
        event.setReason(reason);
        event.setEventType(EventTypeEnum.ProductSynchronizationNotification);
        publish(event);
    }
}
