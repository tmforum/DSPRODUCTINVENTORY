/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.catalog.model.ProductInventory;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.JSONMarshaller;
import tmf.org.dsmapi.hub.Hub;
import tmf.org.dsmapi.hub.Event;
import tmf.org.dsmapi.commons.utils.URIParser;
import tmf.org.dsmapi.hub.ProductInventoryEventTypeEnum;

/**
 *
 * @author pierregauthier
 */
@Stateless
@Path("productInventory/hub")
public class HubResource {

    @EJB
    HubManager manager;
    @EJB
    EventManager eventManager;

    public HubResource() {
    }

    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(Hub entity) throws BadUsageException {
        entity.setId(null);
        manager.create(entity);
        Response response = Response.ok(entity).build();
        return response;
    }

    @DELETE
    @Path("{id}")
    public void remove(@PathParam("id") String id) throws UnknownResourceException {
        manager.remove(id);
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response findById(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {
        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(info.getQueryParameters());

        Hub entity = manager.find(id);
        Response response;
        if (entity != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
                response = Response.ok(entity).build();
            } else {
                fieldSet.add(URIParser.ID_FIELD);
                ObjectNode node = JSONMarshaller.createNode(entity, fieldSet);
                response = Response.ok(node).build();
            }
        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Produces({"application/json"})
    public List<Hub> findAll() {
        return manager.findAll();
    }

    @POST
    @Path("listener")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public void publishEvent(Event event) {

        System.out.println("HubEvent =" + event);
        System.out.println("Event = " + event.getResource());
        System.out.println("Event type = " + event.getEventType());

    }


    @GET
    @Path("listener")
    @Produces({"application/json"})
    public Response findEvents(@Context UriInfo info) {

        // search criteria
        MultivaluedMap<String, String> parameters = URIParser.getParameters(info);
        // fields to filter view
        Set<String> fieldsSelection = URIParser.getFieldsSelection(parameters);

        Set<Event> resultList = findByCriteria(parameters);

        Response response;
        if (fieldsSelection.isEmpty() || fieldsSelection.contains(URIParser.ALL_FIELDS)) {
            response = Response.ok(resultList).build();
        } else {
            fieldsSelection.add(URIParser.ID_FIELD);
            List<ObjectNode> nodeList = JSONMarshaller.createNodes(resultList, fieldsSelection);
            response = Response.ok(nodeList).build();
        }
        return response;
    }

    // return Set of unique elements to avoid List with same elements in case of join
    private Set<Event> findByCriteria(MultivaluedMap<String, String> criteria) {
        List<Event> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = eventManager.findByCriteria(criteria, Event.class);
        } else {
            resultList = eventManager.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<Event>();
        } else {
            return new LinkedHashSet<Event>(resultList);
        }
    }

    @GET
    @Path("proto")
    @Produces({"application/json"})
    public Hub hubProto() {
        Hub hub = new Hub();
        hub.setCallback("callback");
        hub.setQuery("queryString");
        hub.setId("id");
        return hub;
    }

    @GET
    @Path("eventProto")
    @Produces({"application/json"})
    public Event eventProto() {
        Event event = new Event();
        event.setResource(proto());
        event.setEventType(ProductInventoryEventTypeEnum.ProductCreationNotification);
        System.out.println("Event = " + event.getResource().toString());
        System.out.println("Event type = " + event.getEventType().getText());
        return event;
    }

    /**
     *
     * @return
     */
    public ProductInventory proto() {
        ProductInventory pi = new ProductInventory();
        return pi;
    }
}
