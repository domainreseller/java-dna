package io.github.domainreseller.dna.contacts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import io.github.domainreseller.dna.TestClient;
import java.util.List;
import org.junit.jupiter.api.Test;

class ContactsClientTest {
    static ContactInfo contact() {
        return new ContactInfo()
                .firstName("Ada").lastName("Lovelace").email("ada@example.com").address("1 Example Street")
                .city("Istanbul").state("Istanbul").postalCode("34000").country("TR")
                .phoneCountryCode("90").phone("5551234567");
    }

    @Test
    void createSendsTheContactUnderContact() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"handle\":\"DNA-12345\",\"isSuccess\":true,\"operationMessage\":\"ok\"}");

        CreateContactResult result = t.client().contacts().create(contact());

        JsonNode body = t.lastRequest().json();
        assertEquals("POST", t.lastRequest().method());
        assertEquals("contacts", t.lastRequest().apiPath());
        assertEquals("Ada", body.path("contact").path("firstName").asText());
        assertEquals("ada@example.com", body.path("contact").path("eMail").asText());
        assertTrue(body.path("contact").path("discloseFlag").isMissingNode());
        assertEquals("DNA-12345", result.handle());
        assertTrue(result.isSuccess());
    }

    @Test
    void createRefusesAnIncompleteContact() {
        TestClient t = new TestClient();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> t.client().contacts().create(contact().phone(null)));

        assertTrue(e.getMessage().contains("phone"));
        assertTrue(t.requests().isEmpty());
    }

    @Test
    void getEscapesTheHandleAndReadsVerifications() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"contact":{"firstName":"Ada","eMail":"ada@example.com","handle":"DNA 1/2"},
                 "verificationObjects":[{"fieldType":"Email","fieldValue":"ada@example.com","isUserVerified":true}]}
                """);

        ContactDetails details = t.client().contacts().get("DNA 1/2");

        assertEquals("contacts/DNA%201%2F2", t.lastRequest().apiPath());
        assertEquals("ada@example.com", details.contact().email());
        assertEquals("DNA 1/2", details.contact().handle());
        assertTrue(details.verifications().get(0).isUserVerified());
    }

    @Test
    void updatePutsTheContactAtTheHandlePath() {
        TestClient t = new TestClient();
        t.http().respondJson("""
                {"handle":"DNA-1","contactUpdateSuccess":true,"stats":{"totalDomainCount":3,"successCount":2,"splitCount":1},
                 "affectedDomains":[{"domainName":"example.com","status":"Split","newHandle":"DNA-2"}]}
                """);

        HandleUpdateResult result = t.client().contacts().update("DNA-1", contact());

        assertEquals("PUT", t.lastRequest().method());
        assertEquals("contacts/DNA-1", t.lastRequest().apiPath());
        assertEquals("Lovelace", t.lastRequest().json().path("contact").path("lastName").asText());
        assertEquals(2, result.stats().successCount());
        assertEquals("DNA-2", result.affectedDomains().get(0).newHandle());
    }

    @Test
    void verificationUsesTheHandleCodeQuery() {
        TestClient t = new TestClient();
        t.http().respondJson("{\"results\":[{\"fieldType\":\"Phone\",\"attemptNumber\":2}]}");

        List<ContactVerification> verifications = t.client().contacts().getVerification("DNA-1");

        assertEquals("contacts/verification/check", t.lastRequest().apiPath());
        assertEquals("DNA-1", t.lastRequest().query("HandleCode"));
        assertEquals(2, verifications.get(0).attemptNumber());
    }

    @Test
    void resendSendsTheHandleCode() {
        TestClient t = new TestClient();
        t.http().respond(200, "");

        t.client().contacts().resendVerification("DNA-1");

        assertEquals("{\"handleCode\":\"DNA-1\"}", t.lastRequest().body());
    }

    @Test
    void forAllRolesCopiesTheContactFourTimes() {
        ContactInfo source = contact();
        List<DomainContact> contacts = DomainContact.forAllRoles(source);
        source.firstName("Changed");

        assertEquals(ContactType.ALL, contacts.stream().map(DomainContact::contactType).collect(java.util.stream.Collectors.toList()));
        assertEquals("Ada", contacts.get(3).firstName(), "the details are copied");
        assertEquals("Billing", ContactHandle.forAllRoles("DNA-1").get(3).contactType().value());
    }
}
