/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.catalog.service.AbstractManager;

/**
 *
 * @author pierregauthier
 */
@Stateless
public class HubManager extends AbstractManager<Hub>{
    
    @PersistenceContext(unitName = "DSProductInventoryPU")
    private EntityManager em;

    /**
     *
     */
    public HubManager() {
        super(Hub.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
}
