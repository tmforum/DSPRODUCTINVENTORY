package tmf.org.dsmapi.hub.service;

import java.util.List;
import java.util.Set;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.commons.utils.Jackson;
import tmf.org.dsmapi.commons.utils.URIParser;
import tmf.org.dsmapi.hub.model.Event;
import tmf.org.dsmapi.hub.model.Hub;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Asynchronous
public class RESTEventPublisher implements RESTEventPublisherLocal {

    @EJB
    EventFacade eventFacade;

    @EJB
    RESTClient client;

    @Override
    public void publish(Hub hub, Event event) {

        MultivaluedMap<String, String> query = URIParser.getParameters(hub.getQuery());
        query.putSingle("id", event.getId());

        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(query);

        List<Event> resultList = null;
        resultList = eventFacade.findByCriteria(query, Event.class);

        if (resultList != null && !resultList.isEmpty()) {
            if (!fieldSet.isEmpty() && !fieldSet.contains(URIParser.ALL_FIELDS)) {
                Jackson.refineBean(event.getResource(), fieldSet);
            }
            client.publishEvent(hub.getCallback(), event);
        }
    }

}
