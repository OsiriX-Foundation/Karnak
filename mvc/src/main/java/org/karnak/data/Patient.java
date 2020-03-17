package org.karnak.data;

public class Patient {

    // ---------------------------------------------------------------
    // Fields  -------------------------------------------------------
    // ---------------------------------------------------------------
    private String pseudonym;
    private String firstname;
    private String lastname;
    private String birthdayname;
    private String dayofbirth;
    private String monthofbirth;
    private String yearofbirth;
    private String postalcode;
    private String city;

    // ---------------------------------------------------------------
    // Getters/Setters  ------------------------------------------------
    // ---------------------------------------------------------------
    public String get_pseudonym() { return this.pseudonym; }
    public String get_firstname() { return this.firstname; }
    public String get_lastname() { return this.lastname; }
    public String get_birthdayname() { return this.birthdayname; }
    public String get_dayofbirth() { return this.dayofbirth; }
    public String get_monthofbirth() { return this.monthofbirth; }
    public String get_yearofbirth() { return this.yearofbirth; }
    public String get_postalcode() { return this.postalcode; }
    public String get_city() { return this.city; }

    public void set_pseudonym(String pseudonym) { this.pseudonym = pseudonym; }
    public void set_firstname(String firstname) { this.firstname = firstname; }
    public void set_lastname(String lastname) { this.lastname = lastname; }
    public void set_birthdayname(String birthdayname) { this.birthdayname = birthdayname; }
    public void set_dayofbirth(String dayofbirth) { this.dayofbirth = dayofbirth; }
    public void set_monthofbirth(String monthofbirth) { this.monthofbirth = monthofbirth; }
    public void set_yearofbirth(String yearofbirth) { this.yearofbirth = yearofbirth; }
    public void set_postalcode(String postalcode) { this.postalcode = postalcode; }
    public void set_city(String city) { this.city = city; }

    // ---------------------------------------------------------------
    // Constructors  ------------------------------------------------
    // ---------------------------------------------------------------

    public Patient(String pseudonym, String firstname, String lastname, String dayofbirth, String monthofbirth, String yearofbirth)
    {
        this.pseudonym = "";
        this.firstname = "";
        this.lastname ="";
        this.dayofbirth= "";
        this.monthofbirth= "";
        this.yearofbirth= "";
    }

    public Patient(String pseudonym, String firstname, String lastname, String birthdayname, String dayofbirth, String monthofbirth, String yearofbirth,
                        String postalcode, String city)
    {
        this.pseudonym = "";
        this.firstname = "";
        this.lastname ="";
        this.birthdayname= "";
        this.dayofbirth= "";
        this.monthofbirth= "";
        this.yearofbirth= "";
        this.postalcode= "";
        this.city= "";
    }

    // ---------------------------------------------------------------
    // Methods  ------------------------------------------------------
    // ---------------------------------------------------------------
}
