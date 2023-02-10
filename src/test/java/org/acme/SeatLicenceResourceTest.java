package org.acme;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
public class SeatLicenceResourceTest {

    @Test
    public void testGetNullTenant() {
        given()
          .when().get("/tenant/aspian")
          .then()
             .statusCode(404);
    }

    @Test
    public void addTenant() {
        LicensedTenant tenant = new LicensedTenant();
        tenant.setTenantId("aspian");
        tenant.setSeatingAllocation(20);

        given()
          .header("Content-type", "application/json")
          .and()
          .body(tenant)
          .when().post("/tenant")
          .then()
             .statusCode(200);
    }

    @Test
    public void getAddedTenant() {
        LicensedTenant tenant = new LicensedTenant();
        tenant.setTenantId("aspian");
        tenant.setSeatingAllocation(20);

        given()
          .header("Content-type", "application/json")
          .and()
          .body(tenant)
          .when().post("/tenant")
          .then()
             .statusCode(200);
    }

    /*@Test
    public void addSeat() {
        LicensedTenant tenant = new LicensedTenant();
        tenant.setTenantId("aspian");
        tenant.setSeatingAllocation(20);

        AssignedSeat seat = new AssignedSeat();
        seat.setAssignedUser("bob");

        given()
          .header("Content-type", "application/json")
          .and()
          .body(tenant)
          .when().post("/tenant/")
          .then()
             .statusCode(200);
    }*/



}