/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.catalog.model;

import java.io.Serializable;
import java.util.logging.Logger;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Embedded;
import org.codehaus.jackson.map.annotate.JsonSerialize;

public @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
@Embeddable
class ProductRelationship implements Serializable{
    
    private static final Logger LOG = Logger.getLogger(ProductRelationship.class.getName());
    
    String type;
    
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column =
                @Column(name = "productRelShipId")),
    })
    Ref product;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Ref getProduct() {
        return product;
    }

    public void setProduct(Ref product) {
        this.product = product;
    }


    @Override
    public int hashCode() {
        int hash = 3;
        hash = 19 * hash + (this.type != null ? this.type.hashCode() : 0);
        hash = 19 * hash + (this.product != null ? this.product.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ProductRelationship other = (ProductRelationship) obj;
        if ((this.type == null) ? (other.type != null) : !this.type.equals(other.type)) {
            return false;
        }
        if (this.product != other.product && (this.product == null || !this.product.equals(other.product))) {
            return false;
        }
        return true;
    }
   
    

    
   
  
}
