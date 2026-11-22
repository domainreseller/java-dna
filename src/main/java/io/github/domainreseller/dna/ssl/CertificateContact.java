package io.github.domainreseller.dna.ssl;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;

/**
 * The contact of a certificate: sent when configuring, returned with the details. First and last name, company,
 * e-mail, address, phone, country, city and postal code are required when configuring.
 */
public final class CertificateContact {
    @JsonProperty("title")
    private String title;
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
    @JsonProperty("country")
    private String country;
    @JsonProperty("city")
    private String city;
    @JsonProperty("state")
    private String state;
    @JsonProperty("postalCode")
    private String postalCode;
    @JsonProperty(value = "type", access = JsonProperty.Access.WRITE_ONLY)
    private String type;

    /** Creates an empty contact. */
    public CertificateContact() {
    }

    /**
     * Returns the job title.
     *
     * @return the title, or null
     */
    public String title() {
        return title;
    }

    /**
     * Sets the job title.
     *
     * @param title the title
     * @return this contact
     */
    public CertificateContact title(String title) {
        this.title = title;
        return this;
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
     * Sets the first name.
     *
     * @param firstName the first name
     * @return this contact
     */
    public CertificateContact firstName(String firstName) {
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
     * Sets the last name.
     *
     * @param lastName the last name
     * @return this contact
     */
    public CertificateContact lastName(String lastName) {
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
     * Sets the company name.
     *
     * @param companyName the company name
     * @return this contact
     */
    public CertificateContact companyName(String companyName) {
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
    public CertificateContact email(String email) {
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
    public CertificateContact address(String address) {
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
     * Sets the country calling code, digits only.
     *
     * @param phoneCountryCode the code
     * @return this contact
     */
    public CertificateContact phoneCountryCode(String phoneCountryCode) {
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
     * Sets the phone number, digits only.
     *
     * @param phone the number
     * @return this contact
     */
    public CertificateContact phone(String phone) {
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
     * Sets the fax country calling code.
     *
     * @param faxCountryCode the code
     * @return this contact
     */
    public CertificateContact faxCountryCode(String faxCountryCode) {
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
     * Sets the fax number.
     *
     * @param fax the number
     * @return this contact
     */
    public CertificateContact fax(String fax) {
        this.fax = fax;
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
     * Sets the two-letter ISO country code.
     *
     * @param country the country code
     * @return this contact
     */
    public CertificateContact country(String country) {
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
    public CertificateContact city(String city) {
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
    public CertificateContact state(String state) {
        this.state = state;
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
    public CertificateContact postalCode(String postalCode) {
        this.postalCode = postalCode;
        return this;
    }

    /**
     * Returns the contact's role, as returned with the certificate details. It is never sent.
     *
     * @return the role, or null
     */
    public String type() {
        return type;
    }

    /**
     * Throws an {@link IllegalArgumentException} naming every field the API requires when configuring a certificate.
     *
     * @param name the name of the contact in the message
     */
    public void ensureComplete(String name) {
        List<String> missing = new ArrayList<>();
        check(missing, firstName, "firstName");
        check(missing, lastName, "lastName");
        check(missing, companyName, "companyName");
        check(missing, email, "email");
        check(missing, address, "address");
        check(missing, city, "city");
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
        return "CertificateContact[" + firstName + " " + lastName + " <" + email + ">]";
    }
}
