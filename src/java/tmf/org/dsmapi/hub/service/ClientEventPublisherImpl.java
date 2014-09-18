/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.List;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.utils.URIParser;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.hub.Event;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Asynchronous
public class ClientEventPublisherImpl implements IClientEventPublisher {

    @EJB
    EventManager manager;

    @Override
    public void publish(Hub hub, Object event) {

        PostEventClient client = new PostEventClient(hub.getCallback());

        String query = hub.getQuery();
        if (query != null && query.length() > 0) {
            MultivaluedMap<String, String> queryMap = URIParser.getParameters(query);
            Event hubEvent = (Event) event;
            queryMap.putSingle("id", hubEvent.getId());
            List results = manager.findByCriteria(queryMap, Event.class);
            if (results != null && !results.isEmpty()) {
                client.publishEvent(event);
            }
        } else {
            client.publishEvent(event);
        }

        System.out.println("Sending Event");
    }
}
