package tmf.org.dsmapi.hub.service;

import java.util.Date;
import javax.ejb.Local;
import tmf.org.dsmapi.catalog.model.Product;
import tmf.org.dsmapi.hub.model.Event;


/**
 *
 * @author pierregauthier
 */
@Local
public interface EventPublisherLocal {

    void publish(Event event);

    /**
     *
     * @param pi
     */
    public void createNotification(Product bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void statusChangedNotification(Product bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void valueChangedNotification(Product bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void deletionNotification(Product bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void batchNotification(Product bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void transactionNotification(Product bean, String reason, Date date);
   
    /**
     *
     * @param pi
     */
    public void synchronizationNotification(Product bean, String reason, Date date);
    
}
