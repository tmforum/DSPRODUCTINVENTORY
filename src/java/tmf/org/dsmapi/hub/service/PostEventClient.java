/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub.service;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.api.json.JSONConfiguration;
import java.util.HashMap;
import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.jaxrs.JacksonJsonProvider;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jersey REST client generated for REST resource:HubFacadeREST
 * [tmf.org.dsmapi.hub.hub]<br>
 * USAGE:
 * <pre>
 *        PostEventClient client = new PostEventClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author pierregauthier
 */
public class PostEventClient {

    private WebResource webResource;
    private Client client;

    public PostEventClient(String callbackURL) {
        com.sun.jersey.api.client.config.ClientConfig config = new com.sun.jersey.api.client.config.DefaultClientConfig();
        client = Client.create(config);
        client.setConnectTimeout(new Integer("3000"));
        client.setReadTimeout(new Integer("3000"));
        webResource = client.resource(callbackURL).path("");
    }

    public void publishEvent(Object requestEntity) throws UniformInterfaceException {
        System.out.println("publishEvent " + requestEntity);
        webResource.path("").type(javax.ws.rs.core.MediaType.APPLICATION_JSON).post(requestEntity);
    }

    public void close() {
        client.destroy();
    }
}
