/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.jaxrs.resource;

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
import tmf.org.dsmapi.catalog.model.Price;
import tmf.org.dsmapi.catalog.model.Product;
import tmf.org.dsmapi.catalog.model.ProductFields;
import tmf.org.dsmapi.catalog.model.ProductPrice;
import tmf.org.dsmapi.catalog.model.RefInfo;
import tmf.org.dsmapi.catalog.model.TimeRange;
import tmf.org.dsmapi.catalog.service.ProductFacade;
import tmf.org.dsmapi.catalog.service.ProductFacade;
import tmf.org.dsmapi.commons.exceptions.BadUsageException;
import tmf.org.dsmapi.commons.exceptions.TechnicalException;
import tmf.org.dsmapi.commons.exceptions.UnknownResourceException;
import tmf.org.dsmapi.commons.utils.Jackson;
import tmf.org.dsmapi.commons.utils.PATCH;
import tmf.org.dsmapi.commons.utils.URIParser;
import tmf.org.dsmapi.hub.service.EventPublisherLocal;

@Stateless
@Path("product")
public class ProductResource {

    @EJB
    ProductFacade productFacade;
    @EJB
    EventPublisherLocal publisher;

    public ProductResource() {
    }

    @POST
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response create(Product entity) throws BadUsageException {
        productFacade.create(entity);
        publisher.createNotification(entity, "New Product", new Date());
        // 201
        Response response = Response.status(Response.Status.CREATED).entity(entity).build();
        return response;
    }

    @PUT
    @Path("{id}")
    @Consumes({"application/json"})
    @Produces({"application/json"})
    public Response update(@PathParam("id") String id, Product entity) throws UnknownResourceException {
        Response response = null;
        Product product = productFacade.find(id);
        if (product != null) {
            entity.setId(id);
            productFacade.edit(entity);
            publisher.valueChangedNotification(entity, "Product Inventory Modified", new Date());
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

        Set<ProductFields> fields = new HashSet<ProductFields>();

        // Iterate over first level to set received tokens
        Iterator<String> it = jsonNode.getFieldNames();
        while (it.hasNext()) {
            fields.add(ProductFields.fromString(it.next()));
        }

        ObjectMapper mapper = new ObjectMapper();
        Product partialPI;
        try {
            partialPI = mapper.readValue(jsonNode, Product.class);
        } catch (Exception ex) {
            Logger.getLogger(ProductResource.class.getName()).log(Level.SEVERE, null, ex);
            throw new TechnicalException();
        }

        // id is in URL
        partialPI.setId(id);

        Product fullPI = productFacade.updateAttributes(partialPI, fields);

        // 201
        Response response = Response.status(Response.Status.CREATED).entity(fullPI).build();
        return response;
    }

    @GET
    @Produces({"application/json"})
    public Response find(@Context UriInfo info) throws BadUsageException {

        // search queryParameters
        MultivaluedMap<String, String> queryParameters = info.getQueryParameters();
        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(queryParameters);

        Set<Product> resultList = findByCriteria(queryParameters);

        Response response;
        if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
            response = Response.ok(resultList).build();
        } else {
            fieldSet.add(URIParser.ID_FIELD);
            List<ObjectNode> nodeList = Jackson.createNodes(resultList, fieldSet);
            response = Response.ok(nodeList).build();
        }
        return response;
    }

    // return Set of unique elements to avoid List with same elements in case of join
    private Set<Product> findByCriteria(MultivaluedMap<String, String> criteria) throws BadUsageException {
        
        List<Product> resultList = null;
        if (criteria != null && !criteria.isEmpty()) {
            resultList = productFacade.findByCriteria(criteria, Product.class);
        } else {
            resultList = productFacade.findAll();
        }
        if (resultList == null) {
            return new LinkedHashSet<Product>();
        } else {
            return new LinkedHashSet<Product>(resultList);
        }
    }

    @GET
    @Path("{id}")
    @Produces({"application/json"})
    public Response get(@PathParam("id") String id, @Context UriInfo info) throws UnknownResourceException {
        
        // fields to filter view
        Set<String> fieldSet = URIParser.getFieldsSelection(info.getQueryParameters());

        Product product = productFacade.find(id);
        Response response;
        if (product != null) {
            // 200
            if (fieldSet.isEmpty() || fieldSet.contains(URIParser.ALL_FIELDS)) {
                response = Response.ok(product).build();
            } else {
                fieldSet.add(URIParser.ID_FIELD);
                ObjectNode node = Jackson.createNode(product, fieldSet);
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
    public Product proto() {
        Product product = new Product();
        RefInfo[] bundledProduct = new RefInfo[1];

        RefInfo refInfo = new RefInfo();
        refInfo.setId("id");
        refInfo.setName("name");
        bundledProduct[0] = refInfo;

        product.setDescription("Product");
        product.setId("id");
        product.setIsBundle(Boolean.TRUE);
        product.setName("PI");

        TimeRange vf = new TimeRange();
        vf.setEndDateTime(new Date());
        vf.setStartDateTime(new Date());

        ProductPrice[] productPrices = new ProductPrice[1];
        ProductPrice productPrice = new ProductPrice();
        productPrice.setName("name");
        Price price = new Price();
        price.setAmount("amount");
        price.setCurrency("currency");

        productPrice.setPrice(price);

        productPrice.setRecurringChargePeriod("period");
        productPrice.setUnitOfMeasure(null);
        productPrice.setValidFor(vf);

        productPrices[0] = productPrice;

        return product;
    }
}
