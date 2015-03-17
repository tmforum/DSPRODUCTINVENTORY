package org.tmf.dsmapi.jaxrs.resource.admin;

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
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.tmf.dsmapi.commons.exceptions.BadUsageException;
import org.tmf.dsmapi.commons.exceptions.UnknownResourceException;
import org.tmf.dsmapi.commons.jaxrs.model.Report;
import org.tmf.dsmapi.product.model.Product;
import org.tmf.dsmapi.product.service.ProductFacade;
import org.tmf.dsmapi.product.hub.model.ProductEvent;
import org.tmf.dsmapi.product.hub.service.ProductEventFacade;
import org.tmf.dsmapi.product.hub.service.ProductEventPublisherLocal;

@Stateless
@Path("admin/product")
public class ProductAdminResource {

    @EJB
    ProductFacade productInventoryFacade;
    @EJB
    ProductEventFacade eventFacade;
    @EJB
    ProductEventPublisherLocal publisher;

    @GET
    @Produces({"application/json"})
    public List<Product> findAll() {
        return productInventoryFacade.findAll();
    }

    /**
     *
     * For test purpose only
     * @param entities
     * @return
     */
    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response post(List<Product> entities) {

        if (entities == null) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        int previousRows = productInventoryFacade.count();
        int affectedRows;

        // Try to persist entities
        try {
            affectedRows = productInventoryFacade.create(entities);
            for (Product entitie : entities) {
                publisher.createNotification(entitie, new Date());
            }
        } catch (BadUsageException e) {
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode()).build();
        }

        Report stat = new Report(productInventoryFacade.count());
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 201 OK
        return Response.created(null).
                entity(stat).
                build();
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response update(@PathParam("id") long id, Product entity) throws UnknownResourceException {
        Response response = null;
        Product productInventory = productInventoryFacade.find(id);
        if (productInventory != null) {
            entity.setId(id);
            productInventoryFacade.edit(entity);
            publisher.valueChangedNotification(entity, new Date());
            // 201 OK + location
            response = Response.status(Response.Status.CREATED).entity(entity).build();

        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    /**
     *
     * For test purpose only
     * @return
     * @throws org.tmf.dsmapi.commons.exceptions.UnknownResourceException
     */
    @DELETE
    public Report deleteAll() throws UnknownResourceException {

        eventFacade.removeAll();
        int previousRows = productInventoryFacade.count();
        productInventoryFacade.removeAll();
        List<Product> pis = productInventoryFacade.findAll();
        for (Product pi : pis) {
            delete(pi.getId());
        }

        int currentRows = productInventoryFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        return stat;
    }

    /**
     *
     * For test purpose only
     * @param id
     * @return
     * @throws UnknownResourceException
     */
    @DELETE
    @Path("{id}")
    public Response delete(@PathParam("id") Long id) throws UnknownResourceException {
        try {
            int previousRows = productInventoryFacade.count();
            Product entity = productInventoryFacade.find(id);

            // Event deletion
            publisher.deletionNotification(entity, new Date());
            try {
                //Pause for 4 seconds to finish notification
                Thread.sleep(4000);
            } catch (InterruptedException ex) {
                Logger.getLogger(ProductAdminResource.class.getName()).log(Level.SEVERE, null, ex);
            }
            // remove event(s) binding to the resource
            List<ProductEvent> events = eventFacade.findAll();
            for (ProductEvent event : events) {
                if (event.getEvent().getId().equals(id)) {
                    eventFacade.remove(event.getId());
                }
            }
            //remove resource
            productInventoryFacade.remove(id);

            int affectedRows = 1;
            Report stat = new Report(productInventoryFacade.count());
            stat.setAffectedRows(affectedRows);
            stat.setPreviousRows(previousRows);

            // 200 
            Response response = Response.ok(stat).build();
            return response;
        } catch (UnknownResourceException ex) {
            Logger.getLogger(ProductAdminResource.class.getName()).log(Level.SEVERE, null, ex);
            Response response = Response.status(Response.Status.NOT_FOUND).build();
            return response;
        }
    }

    @GET
    @Produces({"application/json"})
    @Path("event")
    public List<ProductEvent> findAllEvents() {
        return eventFacade.findAll();
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
    public Response deleteEvent(@PathParam("id") String id) throws UnknownResourceException {

        int previousRows = eventFacade.count();
        List<ProductEvent> events = eventFacade.findAll();
        for (ProductEvent event : events) {
            if (event.getEvent().getId().equals(id)) {
                eventFacade.remove(event.getId());

            }
        }
        int currentRows = eventFacade.count();
        int affectedRows = previousRows - currentRows;

        Report stat = new Report(currentRows);
        stat.setAffectedRows(affectedRows);
        stat.setPreviousRows(previousRows);

        // 200 
        Response response = Response.ok(stat).build();
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
        return new Report(productInventoryFacade.count());
    }
}
