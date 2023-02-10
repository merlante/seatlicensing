package org.acme;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.acme.entities.LicensedTenant;

@Path("/")
public class SeatLicenseResource {

    @Inject
    SeatAssignmentService seatAssignmentService;
    
    @GET
    @Path("tenant/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public LicensedTenant getTenant(String id) {
        LicensedTenant tenant = seatAssignmentService.getTenant(id);

        if(tenant == null) {
            throw new NotFoundException("Tenant not found."); // for 404
        }

        return tenant;
    }

    @POST
    @Path("tenant")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response addTenant(LicensedTenant tenant) {
        seatAssignmentService.addUpdateTenant(tenant.getTenantId(), tenant.getSeatingAllocation());

        return Response.ok().build();
    }

}
