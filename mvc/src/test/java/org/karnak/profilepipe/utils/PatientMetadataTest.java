package org.karnak.profilepipe.utils;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.karnak.cache.CachedPatient;
import org.karnak.cache.MainzellistePatient;
import org.karnak.cache.PseudonymPatient;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class PatientMetadataTest {
    static PatientMetadata patientMetadata;
    static PatientMetadata patientMetadataDicomEmptyWithIssuer;
    static PatientMetadata patientMetadataWithNotValidPatientSex;

    @BeforeAll
    protected static void setUpBeforeClass() {
        DicomObject dataset = DicomObject.newDicomObject();
        dataset.setString(Tag.PatientID, VR.LO, "");
        dataset.setString(Tag.PatientName, VR.PN, "");
        dataset.setString(Tag.PatientBirthDate, VR.DA, "");
        dataset.setString(Tag.PatientSex, VR.CS, "");

        DicomObject datasetWithNotValidPatientSex = DicomObject.newDicomObject();
        datasetWithNotValidPatientSex.setString(Tag.PatientID, VR.LO, "EREN");
        datasetWithNotValidPatientSex.setString(Tag.PatientName, VR.PN, "Patient^Name");
        datasetWithNotValidPatientSex.setString(Tag.PatientBirthDate, VR.DA, "19930216");
        datasetWithNotValidPatientSex.setString(Tag.PatientSex, VR.CS, "X");
        datasetWithNotValidPatientSex.setString(Tag.IssuerOfPatientID, VR.LO, "PDA");

        patientMetadata = new PatientMetadata(dataset, "");
        patientMetadataDicomEmptyWithIssuer = new PatientMetadata(DicomObject.newDicomObject(), "PDA");
        patientMetadataWithNotValidPatientSex = new PatientMetadata(datasetWithNotValidPatientSex, "");
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientID")
    void getPatientID(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientID(), output);
    }

    private static Stream<Arguments> providerGetPatientID() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, ""),
                Arguments.of(patientMetadataWithNotValidPatientSex, "EREN")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientName")
    void getPatientName(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientName(), output);
    }

    private static Stream<Arguments> providerGetPatientName() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, ""),
                Arguments.of(patientMetadataWithNotValidPatientSex, "Patient^Name")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientLastName")
    void getPatientLastName(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientLastName(), output);
    }

    private static Stream<Arguments> providerGetPatientLastName() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, ""),
                Arguments.of(patientMetadataWithNotValidPatientSex, "Patient")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientFirstName")
    void getPatientFirstName(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientFirstName(), output);
    }

    private static Stream<Arguments> providerGetPatientFirstName() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, ""),
                Arguments.of(patientMetadataWithNotValidPatientSex, "Name")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientBirthDate")
    void getPatientBirthDate(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientBirthDate(), output);
    }

    private static Stream<Arguments> providerGetPatientBirthDate() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, ""),
                Arguments.of(patientMetadataWithNotValidPatientSex, "19930216")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetLocalDatePatientBirthDate")
    void getLocalDatePatientBirthDate(PatientMetadata patientMetadata, LocalDate output){
        if (output == null) {
            assertEquals(patientMetadata.getLocalDatePatientBirthDate(), output);
        } else {
            assertEquals(
              patientMetadata.getLocalDatePatientBirthDate().getDayOfMonth(), output.getDayOfMonth());
            assertEquals(patientMetadata.getLocalDatePatientBirthDate().getMonth(), output.getMonth());
            assertEquals(patientMetadata.getLocalDatePatientBirthDate().getYear(), output.getYear());
        }
    }

    private static Stream<Arguments> providerGetLocalDatePatientBirthDate() {
        return Stream.of(
                Arguments.of(patientMetadata, null),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, null),
                Arguments.of(patientMetadataWithNotValidPatientSex, LocalDate.of(1993,2,16))
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetIssuerOfPatientID")
    void getIssuerOfPatientID(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getIssuerOfPatientID(), output);
    }

    private static Stream<Arguments> providerGetIssuerOfPatientID() {
        return Stream.of(
                Arguments.of(patientMetadata, ""),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, "PDA"),
                Arguments.of(patientMetadataWithNotValidPatientSex, "PDA")
        );
    }

    @ParameterizedTest
    @MethodSource("providerGetPatientSex")
    void getPatientSex(PatientMetadata patientMetadata, String output){
        assertEquals(patientMetadata.getPatientSex(), output);
    }

    private static Stream<Arguments> providerGetPatientSex() {
        return Stream.of(
                Arguments.of(patientMetadata, "O"),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, "O"),
                Arguments.of(patientMetadataWithNotValidPatientSex, "O")
        );
    }

    @ParameterizedTest
    @MethodSource("providerCompareCachedPatient")
    void compareCachedPatient(PatientMetadata patientMetadata, PseudonymPatient pseudonymPatient){
        assertTrue(patientMetadata.compareCachedPatient(pseudonymPatient));
    }

    private static Stream<Arguments> providerCompareCachedPatient() {
        return Stream.of(
                Arguments.of(patientMetadata, new CachedPatient("TEST", "", "", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "", "", null, "O", "")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new CachedPatient("TEST", "", "", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "", "", null, "O", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new CachedPatient("TEST", "EREN", "Patient^Name", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "EREN", "Name", "Patient", LocalDate.of(1993, 2, 16), "O", "PDA"))
        );
    }

    @ParameterizedTest
    @MethodSource("providerCompareCachedPatientFalse")
    void compareCachedPatientFalse(PatientMetadata patientMetadata, PseudonymPatient pseudonymPatient){
        assertFalse(patientMetadata.compareCachedPatient(pseudonymPatient));
    }

    private static Stream<Arguments> providerCompareCachedPatientFalse() {
        return Stream.of(
                Arguments.of(patientMetadata, new CachedPatient("TEST", "1", "", "")),
                Arguments.of(patientMetadata, new CachedPatient("TEST", "", "1", "")),
                Arguments.of(patientMetadata, new CachedPatient("TEST", "", "", "1")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "1", "", "", null, "O", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "1", "", null, "O", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "", "1", null, "O", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "", "", LocalDate.of(1940, 1, 1), "O", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "", "", null, "F", "")),
                Arguments.of(patientMetadata, new MainzellistePatient("TEST", "", "", "", null, "O", "1")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new CachedPatient("TEST", "1", "", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new CachedPatient("TEST", "", "1", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new CachedPatient("TEST", "", "", "")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "1", "", "", null, "O", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "1", "", null, "O", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "", "1", null, "O", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "", "", LocalDate.of(1940, 1, 1), "O", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "", "", null, "F", "PDA")),
                Arguments.of(patientMetadataDicomEmptyWithIssuer, new MainzellistePatient("TEST", "", "", "", null, "O", "")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new CachedPatient("TEST", "", "Patient^Name", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new CachedPatient("TEST", "EREN", "Patient", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new CachedPatient("TEST", "", "^Name", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new CachedPatient("TEST", "EREN", "Patient^Name", "")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "", "Name", "Patient", LocalDate.of(1993, 2, 16), "O", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "EREN", "", "Patient", LocalDate.of(1993, 2, 16), "O", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "EREN", "Name", "", LocalDate.of(1994, 2, 16), "O", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "EREN", "Name", "Patient", LocalDate.of(1993, 2, 16), "F", "PDA")),
                Arguments.of(patientMetadataWithNotValidPatientSex, new MainzellistePatient("TEST", "EREN", "Name", "Patient", LocalDate.of(1993, 2, 16), "O", ""))
        );
    }
}