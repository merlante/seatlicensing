package org.acme;

import javax.persistence.Entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
public class AssignedSeat extends PanacheEntity {

    public String assignedUser;

    public String getAssignedUser() {
        return assignedUser;
    }

    public void setAssignedUser(String user) {
        this.assignedUser = user;
    }
}
