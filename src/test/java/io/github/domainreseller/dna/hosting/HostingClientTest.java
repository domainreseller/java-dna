package io.github.domainreseller.dna.hosting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.domainreseller.dna.TestClient;
import io.github.domainreseller.dna.orders.Order;
import io.github.domainreseller.dna.orders.OrderLineStatus;
import io.github.domainreseller.dna.orders.OrderStatus;
import io.github.domainreseller.dna.OrderType;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class HostingClientTest {
    @Test
    void packagesCreateAndLookups() {
        TestClient t = new TestClient();
        t.http().respondJson("[{\"name\":\"Starter\",\"requiredMinLeasing\":1,\"servicePlans\":[{\"servicePlan\":\"starter-linux\",\"panelType\":\"Plesk\"}]}]")
                .respondJson("{\"serviceId\":\"svc-1\",\"status\":\"Provisioning\",\"token\":\"tok\",\"password\":\"secret\"}")
                .respondJson("{\"serviceId\":\"svc-1\",\"status\":\"Active\",\"panelAddress\":\"https://panel\"}")
                .respondJson("{\"domainName\":\"example.com\",\"expiredOn\":\"2027-01-01T00:00:00Z\"}")
                .respondJson("{\"serviceId\":\"svc-1\"}")
                .respondJson("[{\"name\":\"starter\",\"diskQuotaMb\":1024,\"maxDomains\":null,\"hasShellAccess\":true}]");

        List<HostingPackage> packages = t.client().hosting().listPackages();
        assertEquals("starter-linux", packages.get(0).servicePlans().get(0).servicePlan());

        HostingCreateResult created = t.client().hosting().create("example.com", "starter-linux");
        assertEquals("{\"domainName\":\"example.com\",\"servicePlan\":\"starter-linux\"}", t.lastRequest().body());
        assertEquals("tok", created.token());
        assertFalse(created.toString().contains("secret"));

        HostingStatus status = t.client().hosting().getStatus("example.com");
        assertEquals("hostings/check-status", t.lastRequest().apiPath());
        assertEquals("example.com", t.lastRequest().query("DomainName"));
        assertEquals("https://panel", status.panelAddress());

        HostingService hosting = t.client().hosting().get("example.com");
        assertEquals("example.com", t.lastRequest().query("domainName"));
        assertEquals(2027, hosting.expiresOn().getYear());

        t.client().hosting().getByToken("tok");
        assertEquals("tok", t.lastRequest().query("token"));

        UUID hostingId = UUID.randomUUID();
        List<ServicePlan> plans = t.client().hosting().listPlans(hostingId);
        assertEquals(hostingId.toString(), t.lastRequest().query("hostingId"));
        assertEquals(1024L, plans.get(0).diskQuotaMb());
        assertNull(plans.get(0).maxDomains());
    }

    @Test
    void getNeedsADomainOrAServiceId() {
        TestClient t = new TestClient();
        assertThrows(IllegalArgumentException.class, () -> t.client().hosting().get(" "));
        assertThrows(IllegalArgumentException.class, () -> t.client().hosting().getStatusByServiceId(null));
    }

    @Test
    void getReadsTheOrderLines() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"orderCode":"ORD 1","status":1,"amount":19.98,"amountCurrency":"USD",
                 "products":[{"productCode":"com","orderType":2,"status":1,"price":9.99,"period":1}]}
                """);

        Order order = t.client().orders().get("ORD 1");

        assertEquals("order/ORD%201", t.lastRequest().apiPath());
        assertEquals(OrderStatus.ACTIVE, order.status());
        assertEquals(new BigDecimal("19.98"), order.amount());
        assertEquals(OrderType.RENEW, order.lines().get(0).orderType());
        assertEquals(OrderLineStatus.ORDERED, order.lines().get(0).status());
    }
}
