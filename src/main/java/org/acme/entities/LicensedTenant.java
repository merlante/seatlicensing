package org.acme;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlRootElement;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@XmlRootElement
public class LicensedTenant extends PanacheEntity {

    public String tenantId;
    public int seatingAllocation;

    @OneToMany
    public List<AssignedSeat> seats;

    public String getTenantId() {
        return tenantId;
    }

    public static LicensedTenant findByTenantId(String tenantId) {
        return find("tenantId", tenantId).firstResult();
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public int getSeatingAllocation() {
        return seatingAllocation;
    }

    public void setSeatingAllocation(int seatingAllocation) {
        this.seatingAllocation = seatingAllocation;
    }



}