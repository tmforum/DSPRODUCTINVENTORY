/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.catalog.service;

import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import tmf.org.dsmapi.catalog.Price;
import tmf.org.dsmapi.catalog.RefInfo;
import tmf.org.dsmapi.catalog.ProductInventory;
import tmf.org.dsmapi.catalog.ProductInventoryFields;
import tmf.org.dsmapi.catalog.ProductPrice;
import tmf.org.dsmapi.catalog.Report;
import tmf.org.dsmapi.catalog.TimeRange;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.TechnicalException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.PATCH;
import tmf.org.dsmapi.hub.service.PublisherLocal;

@Stateless
@Path("productInventory")
public class ProductInventoryFacadeREST {

    @EJB
    ProductInventoryFacade manager;
    @EJB
    PublisherLocal publisher;

    public ProductInventoryFacadeREST() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(ProductInventory entity) throws BadUsageException {
        entity.setId(null);
        manager.create(entity);
        publisher.createNotification(entity, "New Product Inventory", new Date());
        // 201
        Response response = Response.status(Response.Status.CREATED).entity(entity).build();
        return response;
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response edit(@PathParam("id") String id, ProductInventory entity) throws UnknownResourceException {
        Response response = null;
        ProductInventory productInventory = manager.find(id);
        if (productInventory != null) {
            entity.setId(id);
            manager.edit(entity);
            publisher.valueChangedNotification(entity, "Product Inventory Modified", new Date());
            // 200 
            // response = Response.ok(entity).build();
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
     * @param id
     * @param jsonNode
     * @return
     * @throws BadUsageException
     * @throws UnknownResourceException
     */
    @PATCH
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response patch(@PathParam("id") String id, JsonNode jsonNode) throws BadUsageException, UnknownResourceException {

        Set<ProductInventoryFields> fields = new HashSet<ProductInventoryFields>();

        // Iterate over first level to set received tokens
        Iterator<String> it = jsonNode.getFieldNames();
        while (it.hasNext()) {
            fields.add(ProductInventoryFields.fromString(it.next()));
        }

        ObjectMapper mapper = new ObjectMapper();
        ProductInventory partialPI;
        try {
            partialPI = mapper.readValue(jsonNode, ProductInventory.class);
        } catch (Exception ex) {
            Logger.getLogger(ProductInventoryFacadeREST.class.getName()).log(Level.SEVERE, null, ex);
            throw new TechnicalException();
        }

        // id is in URL
        partialPI.setId(id);

        ProductInventory fullPI = manager.updateAttributes(partialPI, fields);

        // 201
        Response response = Response.status(Response.Status.CREATED).entity(fullPI).build();
        return response;
    }

    @GET
    @Produces({"application/json"})
    public Response findByCriteriaWithFields(@Context UriInfo info) throws BadUsageException {

        // search criteria
        MultivaluedMap<String, String> criteria = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = FacadeRestUtil.getFieldSet(criteria);

        Set<ProductInventory> resultList = findByCriteria(criteria);

        Response response;
        if (fieldSet.isEmpty() || fieldSet.contains(FacadeRestUtil.ALL_FIELDS)) {
            response = Response.ok(resultList).build();
        } else {
            fieldSet.add(FacadeRestUtil.ID_FIELD);
            List<ObjectNode> nodeList = FacadeRestUtil.createNodeListViewWithFields(resultList, fieldSet);
            response = Response.ok(nodeList).build();
        }
        return response;
    }

    // return Set of unique elements to avoid List with same elements in case of join
    private Set<ProductInventory> findByCriteria(MultivaluedMap<String, String> criteria) throws BadUsageException {
        List<ProductInventory> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = manager.findByCriteria(criteria, ProductInventory.class);
        } else {
            resultList = manager.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<ProductInventory>();
        } else {
            return new LinkedHashSet<ProductInventory>(resultList);
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response findById(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {
        // fields to filter view
        Set<String> fieldSet = FacadeRestUtil.getFieldSet(info.getQueryParameters());

        ProductInventory p = manager.find(id);
        Response response;
        if (p != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(FacadeRestUtil.ALL_FIELDS)) {
                response = Response.ok(p).build();
            } else {
                fieldSet.add(FacadeRestUtil.ID_FIELD);
                ObjectNode node = FacadeRestUtil.createNodeViewWithFields(p, fieldSet);
                response = Response.ok(node).build();
            }
        } else {
            // 404 not found
            response = Response.status(Response.Status.NOT_FOUND).build();
        }
        return response;
    }

    @GET
    @Path("proto")
    @Produces({"application/json"})
    public ProductInventory proto() {
        ProductInventory pi = new ProductInventory();
        RefInfo[] bundledProductInventory = new RefInfo[1];

        RefInfo refInfo = new RefInfo();
        refInfo.setId("id");
        refInfo.setName("name");
        bundledProductInventory[0] = refInfo;

        pi.setDescription("ProductInventory");
        pi.setId("id");
        pi.setIsBundle(Boolean.TRUE);
        pi.setName("PI");


        TimeRange vf = new TimeRange();
        vf.setEndDateTime(new Date());
        vf.setStartDateTime(new Date());


        ProductPrice[] productInventoryPrices = new ProductPrice[1];
        ProductPrice pip = new ProductPrice();
        pip.setName("name");
        Price price = new Price();
        price.setAmount("amount");
        price.setCurrency("currency");

        pip.setPrice(price);

        pip.setRecurringChargePeriod("period");
        pip.setUnitOfMeasure(null);
        pip.setValidFor(vf);

        productInventoryPrices[0] = pip;
//        pi.setProductInventoryPrices(Arrays.asList(productInventoryPrices));
//
//        pi.setProductSpecification(refInfo);
//
//        pi.setValidFor(vf);

        return pi;
    }
}
