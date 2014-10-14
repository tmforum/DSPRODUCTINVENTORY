package tmf.org.dsmapi.jaxrs.resource;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import tmf.org.dsmapi.catalog.model.Product;
import tmf.org.dsmapi.catalog.model.Report;
import tmf.org.dsmapi.catalog.service.ProductFacade;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.hub.model.Event;
import tmf.org.dsmapi.hub.service.EventFacade;
import tmf.org.dsmapi.hub.service.EventPublisherLocal;
import tmf.org.dsmapi.hub.service.HubFacade;

/**
 *
 * @author maig7313
 */
@Stateless
@Path("admin")
public class AdminResource {

    @EJB
    ProductFacade productFacade;
    @EJB
    EventFacade eventFacade;
    @EJB
    HubFacade hubFacade;
    @EJB
    EventPublisherLocal publisher;

    /**
     *
     * @param entities
     * @return
     */
    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<Product> entities) {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = productFacade.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = productFacade.create(entities);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(productFacade.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 201 OK
        return Response.created(null).
                entity(stat).
                build();
    }

    /**
     *
     * @return
     * @throws tmf.org.dsmapi.commons.exceptions.UnknownResourceException
     */
    @DELETE
    @Path("")
    public Report deleteAll() throws UnknownResourceException {

        eventFacade.removeAll();
        int previousRows = productFacade.count();
        productFacade.removeAll();
        List<Product> pis = productFacade.findAll();
        for (Product pi : pis) {
            delete(pi.getId());
        }
        int currentRows = productFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    @DELETE
    @Path("{id}")
//    public Report delete(@PathParam("id") String id) throws UnknownResourceException {
    public Response delete(@PathParam("id") String id) {
        try {
            // int previousRows = productFacade.count();
            Product entity = productFacade.find(id);

            // Event deletion
            publisher.deletionNotification(entity, "Product Inventory Deleted", new Date());
            try {
                //Pause for 4 seconds to finish notification
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AdminResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove event(s) binding to the resource
            List<Event> events = eventFacade.findAll();
            for (Event event : events) {
                if (event.getResource().getId().equals(id)) {
                    eventFacade.remove(event.getId());
                }
            }
            //remove resource
            productFacade.remove(id);

            //        int affectedRows = 1;
            //        Report stat = new Report(productFacade.count());
            //        stat.setAffectedRows(affectedRows);
            //        stat.setPreviousRows(previousRows);
            //        return stat;
            // 200 
            Response response = Response.ok(entity).build();
            return response;
        } catch (UnknownResourceException ex) {
            Logger.getLogger(AdminResource.class.getName()).log(Level.SEVERE, null, ex);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            return response;
        }
    }

    /**
     *
     * @return
     */
    @DELETE
    @Path("hub")
    public Report deleteAllHub() {

        int previousRows = hubFacade.count();
        hubFacade.removeAll();
        int currentRows = hubFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event")
    public Report deleteAllEvent() {

        int previousRows = eventFacade.count();
        eventFacade.removeAll();
        int currentRows = eventFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event/{id}")
//    public Report deleteEvent(@PathParam("id") String id) throws UnknownResourceException {
    public Response deleteEvent(@PathParam("id") String id) throws UnknownResourceException {

//        int previousRows = eventFacade.count();
        List<Event> events = eventFacade.findAll();
        Event oneEvent = null;
        for (Event event : events) {
            if (event.getResource().getId().equals(id)) {
                oneEvent = event;
                eventFacade.remove(event.getId());

            }
        }
//        int currentRows = eventFacade.count();
//        int affectedRows = previousRows - currentRows;
//
//        Report stat = new Report(currentRows);
//        stat.setAffectedRows(affectedRows);
//        stat.setPreviousRows(previousRows);
//
//        return stat;
        // 200 
        Response response = Response.ok(oneEvent).build();
        return response;
    }

    /**
     *
     * @return
     */
    @GET
    @Path("count")
    @Produces({"application/json"})
    public Report count() {
        return new Report(productFacade.count());
    }
//    /**
//     *
//     */
//    @DELETE
//    @Path("productInventory/cache")
//    public void clearCache() {
//        productFacade.clearCache();
//    }
//
//    @PUT
//    @Path("productInventory/wf/delay/{value}")
//    public void patchDelay(@PathParam("value") long value) {
//        productFacade.setDelay(value);
//    }
}
