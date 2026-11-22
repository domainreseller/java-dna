package io.github.domainreseller.dna.contacts;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The details of a contact. Every field except the company name, the fax fields and the disclose flag is required
 * when you create or update one; {@link #ensureComplete(String)} checks that before anything is sent.
 *
 * <pre>{@code
 * ContactInfo contact = new ContactInfo()
 *         .firstName("Ada").lastName("Lovelace").email("ada@example.com")
 *         .address("1 Example Street").city("Istanbul").state("Istanbul").postalCode("34000")
 *         .country("TR").phoneCountryCode("90").phone("5551234567");
 * }</pre>
 */
public class ContactInfo {
    @JsonProperty("firstName")
    private String firstName;
    @JsonProperty("lastName")
    private String lastName;
    @JsonProperty("companyName")
    private String companyName;
    @JsonProperty("eMail")
    private String email;
    @JsonProperty("address")
    private String address;
    @JsonProperty("phoneCountryCode")
    private String phoneCountryCode;
    @JsonProperty("phone")
    private String phone;
    @JsonProperty("faxCountryCode")
    private String faxCountryCode;
    @JsonProperty("fax")
    private String fax;
    @JsonProperty("postalCode")
    private String postalCode;
    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("discloseFlag")
    private Boolean discloseFlag;

    /** Creates an empty contact. */
    public ContactInfo() {
    }

    /**
     * Copies the fields of another contact.
     *
     * @param other the contact to copy
     */
    public ContactInfo(ContactInfo other) {
        Objects.requireNonNull(other, "other");
        firstName = other.firstName;
        lastName = other.lastName;
        companyName = other.companyName;
        email = other.email;
        address = other.address;
        phoneCountryCode = other.phoneCountryCode;
        phone = other.phone;
        faxCountryCode = other.faxCountryCode;
        fax = other.fax;
        postalCode = other.postalCode;
        country = other.country;
        city = other.city;
        state = other.state;
        discloseFlag = other.discloseFlag;
    }

    /**
     * Returns the first name.
     *
     * @return the first name, or null
     */
    public String firstName() {
        return firstName;
    }

    /**
     * Sets the first name (at least 2 letters).
     *
     * @param firstName the first name
     * @return this contact
     */
    public ContactInfo firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    /**
     * Returns the last name.
     *
     * @return the last name, or null
     */
    public String lastName() {
        return lastName;
    }

    /**
     * Sets the last name (at least 2 letters).
     *
     * @param lastName the last name
     * @return this contact
     */
    public ContactInfo lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    /**
     * Returns the company name.
     *
     * @return the company name, or null
     */
    public String companyName() {
        return companyName;
    }

    /**
     * Sets the company name. The API uses the first and last name when it is empty.
     *
     * @param companyName the company name
     * @return this contact
     */
    public ContactInfo companyName(String companyName) {
        this.companyName = companyName;
        return this;
    }

    /**
     * Returns the e-mail address.
     *
     * @return the e-mail address, or null
     */
    public String email() {
        return email;
    }

    /**
     * Sets the e-mail address (the API's {@code eMail}).
     *
     * @param email the e-mail address
     * @return this contact
     */
    public ContactInfo email(String email) {
        this.email = email;
        return this;
    }

    /**
     * Returns the street address.
     *
     * @return the address, or null
     */
    public String address() {
        return address;
    }

    /**
     * Sets the street address.
     *
     * @param address the address
     * @return this contact
     */
    public ContactInfo address(String address) {
        this.address = address;
        return this;
    }

    /**
     * Returns the country calling code.
     *
     * @return the code, or null
     */
    public String phoneCountryCode() {
        return phoneCountryCode;
    }

    /**
     * Sets the country calling code, digits only without {@code +}, for example {@code 90}.
     *
     * @param phoneCountryCode the code
     * @return this contact
     */
    public ContactInfo phoneCountryCode(String phoneCountryCode) {
        this.phoneCountryCode = phoneCountryCode;
        return this;
    }

    /**
     * Returns the phone number.
     *
     * @return the number, or null
     */
    public String phone() {
        return phone;
    }

    /**
     * Sets the phone number, digits only, for example {@code 5551234567}.
     *
     * @param phone the number
     * @return this contact
     */
    public ContactInfo phone(String phone) {
        this.phone = phone;
        return this;
    }

    /**
     * Returns the fax country calling code.
     *
     * @return the code, or null
     */
    public String faxCountryCode() {
        return faxCountryCode;
    }

    /**
     * Sets the fax country calling code, digits only.
     *
     * @param faxCountryCode the code
     * @return this contact
     */
    public ContactInfo faxCountryCode(String faxCountryCode) {
        this.faxCountryCode = faxCountryCode;
        return this;
    }

    /**
     * Returns the fax number.
     *
     * @return the number, or null
     */
    public String fax() {
        return fax;
    }

    /**
     * Sets the fax number, digits only.
     *
     * @param fax the number
     * @return this contact
     */
    public ContactInfo fax(String fax) {
        this.fax = fax;
        return this;
    }

    /**
     * Returns the postal code.
     *
     * @return the postal code, or null
     */
    public String postalCode() {
        return postalCode;
    }

    /**
     * Sets the postal code.
     *
     * @param postalCode the postal code
     * @return this contact
     */
    public ContactInfo postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * Returns the country.
     *
     * @return the two-letter ISO country code, or null
     */
    public String country() {
        return country;
    }

    /**
     * Sets the two-letter ISO country code, for example {@code TR} or {@code US}.
     *
     * @param country the country code
     * @return this contact
     */
    public ContactInfo country(String country) {
        this.country = country;
        return this;
    }

    /**
     * Returns the city.
     *
     * @return the city, or null
     */
    public String city() {
        return city;
    }

    /**
     * Sets the city.
     *
     * @param city the city
     * @return this contact
     */
    public ContactInfo city(String city) {
        this.city = city;
        return this;
    }

    /**
     * Returns the state or province.
     *
     * @return the state, or null
     */
    public String state() {
        return state;
    }

    /**
     * Sets the state or province.
     *
     * @param state the state
     * @return this contact
     */
    public ContactInfo state(String state) {
        this.state = state;
        return this;
    }

    /**
     * Returns whether the contact is published in WHOIS.
     *
     * @return the flag, or null for the registry's default
     */
    public Boolean discloseFlag() {
        return discloseFlag;
    }

    /**
     * Sets whether to publish the contact in WHOIS.
     *
     * @param discloseFlag the flag, or null to keep the registry's default
     * @return this contact
     */
    public ContactInfo discloseFlag(Boolean discloseFlag) {
        this.discloseFlag = discloseFlag;
        return this;
    }

    /**
     * Throws an {@link IllegalArgumentException} naming every missing required field.
     *
     * @param name the name of the contact in the message, for example {@code contact}
     */
    public void ensureComplete(String name) {
        List<String> missing = new ArrayList<>();
        check(missing, firstName, "firstName");
        check(missing, lastName, "lastName");
        check(missing, email, "email");
        check(missing, address, "address");
        check(missing, city, "city");
        check(missing, state, "state");
        check(missing, postalCode, "postalCode");
        check(missing, country, "country");
        check(missing, phoneCountryCode, "phoneCountryCode");
        check(missing, phone, "phone");
        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(name + " is missing required fields: " + String.join(", ", missing) + ".");
        }
    }

    private static void check(List<String> missing, String value, String field) {
        if (value == null || value.isBlank()) {
            missing.add(field);
        }
    }

    /**
     * Returns the name and e-mail address, for debugging.
     *
     * @return a short description
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + firstName + " " + lastName + " <" + email + ">]";
    }
}
