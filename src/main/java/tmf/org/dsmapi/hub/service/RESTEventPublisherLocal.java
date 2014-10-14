package tmf.org.dsmapi.hub.service;

import javax.ejb.Local;
import tmf.org.dsmapi.hub.model.Event;
import tmf.org.dsmapi.hub.model.Hub;

/**
 *
 * @author pierregauthier
 */
@Local
public interface RESTEventPublisherLocal {

    public void publish(Hub hub, Event event);
    
}
