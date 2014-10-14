/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.catalog.model;

import java.io.Serializable;
import javax.persistence.Embeddable;
import org.codehaus.jackson.map.annotate.JsonSerialize;
/**
 *
 * @author pierregauthier
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
public class Ref implements Serializable {
    
    String id;
  

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
