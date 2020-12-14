package org.karnak.cache;

import java.time.LocalDate;

public interface PseudonymPatient {
    String getPseudonym();

    String getPatientId();

    String getPatientName();

    String getIssuerOfPatientId();

    LocalDate getPatientBirthDate();

    String getPatientSex();
}
