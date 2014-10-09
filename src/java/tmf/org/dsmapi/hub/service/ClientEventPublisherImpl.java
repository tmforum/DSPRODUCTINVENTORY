/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import com.sun.jersey.core.util.MultivaluedMapImpl;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.core.MultivaluedMap;
import tmf.org.dsmapi.catalog.model.ProductInventory;
import tmf.org.dsmapi.catalog.service.ProductInventoryManager;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
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
    EventManager eventManager;
    @EJB
    ProductInventoryManager manager;

    @Override
    public void publish(Hub hub, Object event) {

        PostEventClient client = new PostEventClient(hub.getCallback());
        MultivaluedMap<String, String> fieldsCriteria = new MultivaluedMapImpl();

        String query = hub.getQuery();
        if (query != null && query.length() > 0) {
            MultivaluedMap<String, String> queryMap = URIParser.getParameters(query);
            Event hubEvent = (Event) event;
            queryMap.putSingle("id", hubEvent.getId());
            if (queryMap.containsKey("fields")) {
                fieldsCriteria = new MultivaluedMapImpl();
                Set<Map.Entry<String, List<String>>> entrySet = queryMap.entrySet();
                for (Map.Entry<String, List<String>> entry : entrySet) {
                    if (entry.getKey().equalsIgnoreCase("fields")) {
                        fieldsCriteria.put(entry.getKey(), entry.getValue());
                    }
                }
                queryMap.remove("fields");
            }
            List results = eventManager.findByCriteria(queryMap, Event.class);
            if (results != null && !results.isEmpty()) {
                if (!fieldsCriteria.isEmpty()) {
                    try {
                        ProductInventory product = (ProductInventory) manager.findByCriteriaWithFields(fieldsCriteria, hubEvent.getResource());
                        if (null != product) {
                            hubEvent.setResource(product);
                        }
                    } catch (BadUsageException ex) {
                        Logger.getLogger(ClientEventPublisherImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                client.publishEvent(hubEvent);
            }
        } else {
            client.publishEvent(event);
        }
        System.out.println("Sending Event");
    }

//    private ProductInventory findByCriteriaWithFields(MultivaluedMap<String, String> criteria, ProductInventory product) throws BadUsageException {
//
//        // fields to filter view
//        Set<String> fieldSet = FacadeRestUtil.getFieldSet(criteria);
//
//        ProductInventory result = null;
//
//        if (fieldSet.isEmpty() || fieldSet.contains(FacadeRestUtil.ALL_FIELDS)) {
//            result = product;
//        } else {
//            fieldSet.add(FacadeRestUtil.ID_FIELD);
//            ObjectMapper mapper = new ObjectMapper();
//            mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
//            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
//            ObjectNode rootNode = FacadeRestUtil.createNodeViewWithFields(product, fieldSet);
//            try {
//                result = (ProductInventory) mapper.treeToValue(rootNode, ProductInventory.class);
//            } catch (IOException ex) {
//                Logger.getLogger(ClientEventPublisherImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return result;
//    }
}
