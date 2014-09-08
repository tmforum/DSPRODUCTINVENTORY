/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tmf.org.dsmapi.hub;

import java.io.Serializable;
import java.util.Date;
import javax.mail.Address;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.criteria.Fetch;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonPropertyOrder;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import tmf.org.dsmapi.catalog.ProductInventory;

/**
 *
 * @author pierregauthier
 */
@XmlRootElement
@Entity
@JsonPropertyOrder(value = {"event", "reason", "date", "eventType"})
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Event implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private String id;
    private String reason;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEvent;
    private ProductInventory resource; //checl for object
    @Enumerated(value = EnumType.STRING)
    private ProductInventoryEventTypeEnum eventType;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProductInventoryEventTypeEnum getEventType() {
        return eventType;
    }

    public void setEventType(ProductInventoryEventTypeEnum eventType) {
        this.eventType = eventType;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Date getDate() {
        return dateEvent;
    }

    public void setDate(Date date) {
        this.dateEvent = date;
    }

    public ProductInventory getResource() {
        return resource;
    }

    public void setResource(ProductInventory resource) {
        this.resource = resource;
    }

    @Override
    public String toString() {
        return "Event{" + "id=" + id + ", eventType=" + eventType + ", reason=" + reason + ", dateEvent=" + dateEvent + ", resource=" + resource + "}";
    }
}
