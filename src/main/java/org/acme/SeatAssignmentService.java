package org.acme;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
public class SeatAssignmentService {

    @Transactional
    public LicensedTenant getTenant(String tenantId) {
        return LicensedTenant.findByTenantId(tenantId);
    }

    @Transactional
    public int seatsRemaining(String tenantId) {
        LicensedTenant tenant = LicensedTenant.findByTenantId(tenantId);

        if(tenant == null) {
            throw new TenantNotFoundException("No tenant found with that id.");
        }

        int seatsAssigned = tenant.seats != null ? tenant.seats.size() : 0;

        return tenant.getSeatingAllocation() - seatsAssigned;
    }

    @Transactional
    public AssignedSeat getAssignedSeat(String tenantId, String assignedUser) {
        LicensedTenant tenant = LicensedTenant.findByTenantId(tenantId);

        if(tenant == null) {
            throw new TenantNotFoundException("No tenant found with that id.");
        }

        if(tenant.seats == null) {
            return null;
        }

        Optional<AssignedSeat> seatOpt = tenant.seats.stream().filter(
            s -> assignedUser.equals(s.getAssignedUser())
            ).findFirst();

        if(seatOpt.isPresent()) {
            return seatOpt.get();
        } else {
            return null;
        }
    }

    @Transactional
    public void addUpdateTenant(String tenantId, int seatingAllocation) {
        LicensedTenant tenant = LicensedTenant.findByTenantId(tenantId);

        if(tenant != null) {
            int assignedSeats = tenant.seats != null ? tenant.seats.size() : 0;

            if(seatingAllocation < assignedSeats) {
                throw new TenantNotFoundException("Can't modify seat allocation to value that is less than current seats assigned.");
            }
        }

        tenant = new LicensedTenant();
        tenant.setTenantId(tenantId);
        tenant.setSeatingAllocation(seatingAllocation);

        tenant.persist();
    }

    @Transactional
    public boolean addSeat(String tenantId, String assignedUser) {
        LicensedTenant tenant = LicensedTenant.findByTenantId(tenantId);

        if(tenant == null) {
            throw new TenantNotFoundException("No tenant found with that id.");
        }

        int seatAllocation = tenant.getSeatingAllocation();
        int assignedSeats = tenant.seats.size();

        if(assignedSeats >= seatAllocation) {
            return false;
        } else {
            AssignedSeat assignedSeat = new AssignedSeat();
            assignedSeat.setAssignedUser(assignedUser);
            tenant.seats.add(assignedSeat);
            assignedSeat.persist();
            tenant.persist();

            /*
             * Can do outbox transactional stuff here.
             * e.g. Debezium -> Kafka -> SpiceDB
             * (https://debezium.io/documentation/reference/stable/integrations/outbox.html)
             * or just grpc calls.
             * 
             * Can decide not to persist the seat here unless the outbox succeeds.
             */

            return true;
        }
    }

    @Transactional
    public boolean removeSeat(String tenantId, String assignedUser) {
        LicensedTenant tenant = LicensedTenant.findByTenantId(tenantId);

        AssignedSeat seat = getAssignedSeat(tenantId, assignedUser);

        if(seat == null) {
            return false;
        }

        seat.delete();
        tenant.persist();

        /*
         * Can do outbox transactional stuff here.
         * e.g. Debezium -> Kafka -> SpiceDB
         * (https://debezium.io/documentation/reference/stable/integrations/outbox.html)
         * or just grpc calls.
         * 
         * Can decide not to persist the seat here unless the outbox succeeds.
         r*/

        return true;
    }

    public static class TenantNotFoundException extends RuntimeException {

        public TenantNotFoundException(String e) {
            super(e);
        }
    }

}