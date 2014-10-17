/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tmf.dsmapi.catalog.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.tmf.dsmapi.commons.utils.CustomJsonDateSerializer;

@Entity
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class Product implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String id;

    @NotNull
    String name;

    String description;

    @NotNull
    @Enumerated(value = EnumType.STRING)    
    Status status;

    @NotNull
    Boolean isBundle;

    Boolean isCustomerVisible;

    String productSerialNumber;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date orderDate;

    @Temporal(TemporalType.TIMESTAMP)
    @JsonSerialize(using = CustomJsonDateSerializer.class)
    private Date terminationDate;
    
    @NotNull
    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "productOfferId")),
        @AttributeOverride(name = "name", column = @Column(name = "productOfferName"))
    })
    RefInfo productOffering;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "id", column = @Column(name = "productSpecId")),
    })
    Ref productSpecification;

    @ElementCollection
    @CollectionTable(
            name = "PI_PRODUCT_CHARACTERISTICS",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<ProductCharacteristic> productCharacteristics;

    @ElementCollection
    @CollectionTable(
            name = "PI_PRODUCT_RELATION_SHIPS",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<ProductRelationship> productRelationships;

    @ElementCollection
    @CollectionTable(
            name = "PI_BILLING_ACCOUNT_REF",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<RefInfo> billingAccount;

    @ElementCollection
    @CollectionTable(
            name = "PI_RELATED_PARTIES_REF",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<RefParty> relatedParties;
    
    @ElementCollection
    @CollectionTable(
            name = "PI_AGREMENT_REF",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<Ref> agreement;
    
    String place;

    @ElementCollection
    @CollectionTable(
            name = "PI_REALIZING_RESOURCES_REF",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<Ref> realizingResources;
    
    @ElementCollection
    @CollectionTable(
            name = "PI_REALIZING_SERVICE_REF",
            joinColumns =
            @JoinColumn(name = "PRODUCT_INVENTORY_ID"))
    List<Ref> realizingServices;
    
    @ElementCollection
    @CollectionTable(
            name = "PI_PRODUCT_PRICE",
            joinColumns =
            @JoinColumn(name = "PRODUCT_ID"))
    List<ProductPrice> productPrices;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getIsBundle() {
        return isBundle;
    }

    public void setIsBundle(Boolean isBundle) {
        this.isBundle = isBundle;
    }

    public String getStatus() {
        return status.getText();
    }

    public void setStatus(String status) {
        this.status = Status.fromString(status);
    }

    public Boolean getIsCustomerVisible() {
        return isCustomerVisible;
    }

    public void setIsCustomerVisible(Boolean isCustomerVisible) {
        this.isCustomerVisible = isCustomerVisible;
    }

    public String getProductSerialNumber() {
        return productSerialNumber;
    }

    public void setProductSerialNumber(String productSerialNumber) {
        this.productSerialNumber = productSerialNumber;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Date getTerminationDate() {
        return terminationDate;
    }

    public void setTerminationDate(Date terminationDate) {
        this.terminationDate = terminationDate;
    }

    public RefInfo getProductOffering() {
        return productOffering;
    }

    public void setProductOffering(RefInfo productOffering) {
        this.productOffering = productOffering;
    }

    public Ref getProductSpecification() {
        return productSpecification;
    }

    public void setProductSpecification(Ref productSpecification) {
        this.productSpecification = productSpecification;
    }

    public List<ProductCharacteristic> getProductCharacteristics() {
        return productCharacteristics;
    }

    public void setProductCharacteristics(List<ProductCharacteristic> productCharacteristics) {
        this.productCharacteristics = productCharacteristics;
    }

    public List<ProductRelationship> getProductRelationships() {
        return productRelationships;
    }

    public void setProductRelationships(List<ProductRelationship> productRelationships) {
        this.productRelationships = productRelationships;
    }

    public List<RefInfo> getBillingAccount() {
        return billingAccount;
    }

    public void setBillingAccount(List<RefInfo> billingAccount) {
        this.billingAccount = billingAccount;
    }

    public List<RefParty> getRelatedParties() {
        return relatedParties;
    }

    public void setRelatedParties(List<RefParty> relatedParties) {
        this.relatedParties = relatedParties;
    }

    public List<Ref> getAgreement() {
        return agreement;
    }

    public void setAgreement(List<Ref> agreement) {
        this.agreement = agreement;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public List<Ref> getRealizingResources() {
        return realizingResources;
    }

    public void setRealizingResources(List<Ref> realizingResources) {
        this.realizingResources = realizingResources;
    }

    public List<Ref> getRealizingServices() {
        return realizingServices;
    }

    public void setRealizingServices(List<Ref> realizingServices) {
        this.realizingServices = realizingServices;
    }

    public List<ProductPrice> getProductPrices() {
        return productPrices;
    }

    public void setProductPrices(List<ProductPrice> productPrices) {
        this.productPrices = productPrices;
    }


    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + (this.id != null ? this.id.hashCode() : 0);
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
        final Product other = (Product) obj;
        if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
            return false;
        }
        if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
            return false;
        }
        if ((this.description == null) ? (other.description != null) : !this.description.equals(other.description)) {
            return false;
        }
        if (this.isBundle != other.isBundle && (this.isBundle == null || !this.isBundle.equals(other.isBundle))) {
            return false;
        }
        
        return true;
    }


}
