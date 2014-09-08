package tmf.org.dsmapi.catalog.service;

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
import tmf.org.dsmapi.catalog.ProductInventory;
import tmf.org.dsmapi.catalog.Report;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.hub.Event;
import tmf.org.dsmapi.hub.service.EventFacade;
import tmf.org.dsmapi.hub.service.HubFacade;
import tmf.org.dsmapi.hub.service.PublisherLocal;

/**
 *
 * @author maig7313
 */
@Stateless
@Path("productInventory/admin")
public class AdminFacadeREST {

    @EJB
    ProductInventoryFacade manager;
    @EJB
    EventFacade eventManager;
    @EJB
    HubFacade hubManager;
    @EJB
    PublisherLocal publisher;

    /**
     *
     * @param entities
     * @return
     */
    @POST
    @Path("")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<ProductInventory> entities) {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = manager.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = manager.create(entities);
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(manager.count());
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
     */
    @DELETE
    @Path("")
    public Report deleteAll() throws UnknownResourceException {

        eventManager.removeAll();
        int previousRows = manager.count();
        manager.removeAll();
        List<ProductInventory> pis = manager.findAll();
        for (ProductInventory pi : pis) {
            delete(pi.getId());
        }
        int currentRows = manager.count();
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
            // int previousRows = manager.count();
            ProductInventory entity = manager.find(id);

            // Event deletion
            publisher.deletionNotification(entity, "Product Inventory Deleted", new Date());
            try {
                //Pause for 4 seconds to finish notification
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(AdminFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove event(s) binding to the resource
            List<Event> events = eventManager.findAll();
            for (Event event : events) {
                if (event.getResource().getId().equals(id)) {
                    eventManager.remove(event.getId());
                }
            }
            //remove resource
            manager.remove(id);

            //        int affectedRows = 1;
            //        Report stat = new Report(manager.count());
            //        stat.setAffectedRows(affectedRows);
            //        stat.setPreviousRows(previousRows);
            //        return stat;
            // 200 
            Response response = Response.ok(entity).build();
            return response;
        } catch (UnknownResourceException ex) {
            Logger.getLogger(AdminFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
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

        int previousRows = hubManager.count();
        hubManager.removeAll();
        int currentRows = hubManager.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    @DELETE
    @Path("event")
    public Report deleteAllEvent() {

        int previousRows = eventManager.count();
        eventManager.removeAll();
        int currentRows = eventManager.count();
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

//        int previousRows = eventManager.count();
        List<Event> events = eventManager.findAll();
        Event oneEvent = null;
        for (Event event : events) {
            if (event.getResource().getId().equals(id)) {
                oneEvent = event;
                eventManager.remove(event.getId());

            }
        }
//        int currentRows = eventManager.count();
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
        return new Report(manager.count());
    }
//    /**
//     *
//     */
//    @DELETE
//    @Path("productInventory/cache")
//    public void clearCache() {
//        manager.clearCache();
//    }
//
//    @PUT
//    @Path("productInventory/wf/delay/{value}")
//    public void patchDelay(@PathParam("value") long value) {
//        manager.setDelay(value);
//    }
}
