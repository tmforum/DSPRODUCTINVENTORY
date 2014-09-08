/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.Date;
import javax.ejb.Local;
import tmf.org.dsmapi.catalog.ProductInventory;


/**
 *
 * @author pierregauthier
 */
@Local
public interface PublisherLocal {

    void publish(Object event);

    /**
     *
     * @param pi
     */
    public void createNotification(ProductInventory bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void statusChangedNotification(ProductInventory bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void valueChangedNotification(ProductInventory bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void deletionNotification(ProductInventory bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void batchNotification(ProductInventory bean, String reason, Date date);

    /**
     *
     * @param pi
     */
    public void transactionNotification(ProductInventory bean, String reason, Date date);
   
    /**
     *
     * @param pi
     */
    public void synchronizationNotification(ProductInventory bean, String reason, Date date);
    
}
