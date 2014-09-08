/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.hub.Event;
import tmf.org.dsmapi.catalog.service.AbstractFacade;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class EventFacade extends AbstractFacade<Event>{
    
    @PersistenceContext(unitName = "DSProductInventoryPU")
    private EntityManager em;
   

    
    /**
     *
     */
    public EventFacade() {
        super(Event.class);
    }


    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

}
