package org.karnak.kheops;

import org.dcm4che6.data.DicomObject;
import org.dcm4che6.data.Tag;
import org.dcm4che6.data.VR;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.karnak.expression.ExprConditionKheops;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExprConditionKheopsTest {
    private static ExprConditionKheops exprConditionKheops;

    @BeforeAll
    protected static void setUpBeforeClass() throws Exception {
        final DicomObject dataset = DicomObject.newDicomObject();
        dataset.setString(Tag.StudyDate, VR.DA, "20180209");
        dataset.setString(Tag.PatientAge, VR.AS, "043Y");
        dataset.setString(Tag.SeriesInstanceUID, VR.UI, "2.25.1234567890123456");
        dataset.setString(Tag.ClinicalTrialSiteName, VR.LO, "Unicorn Land");
        dataset.setString(Tag.SmokingStatus, VR.CS, "YES");
        dataset.setString(Tag.Modality, VR.CS, "CT");

        exprConditionKheops = new ExprConditionKheops(dataset);
    }

    @ParameterizedTest
    @MethodSource("providerIsPresent")
    void tagValueIsPresent(int tag, String input){
        assertTrue(exprConditionKheops.tagValueIsPresent(tag, input));
    }

    private static Stream<Arguments> providerIsPresent() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20180209"),
                Arguments.of(Tag.PatientAge, "043Y"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123456"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn Land"),
                Arguments.of(Tag.SmokingStatus, "YES"),
                Arguments.of(Tag.Modality, "CT")
        );
    }

    @ParameterizedTest
    @MethodSource("providerIsNotPresent")
    void tagValueIsNotPresent(int tag, String input){
        assertFalse(exprConditionKheops.tagValueIsPresent(tag, input));
    }

    private static Stream<Arguments> providerIsNotPresent() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "2018"),
                Arguments.of(Tag.StudyDate, "201802"),
                Arguments.of(Tag.StudyDate, "0209"),
                Arguments.of(Tag.StudyDate, "09"),
                Arguments.of(Tag.StudyDate, "02"),
                Arguments.of(Tag.StudyDate, ""),
                Arguments.of(Tag.PatientAge, "043"),
                Arguments.of(Tag.PatientAge, "43"),
                Arguments.of(Tag.PatientAge, "Y"),
                Arguments.of(Tag.PatientAge, "43Y"),
                Arguments.of(Tag.PatientAge, "43y"),
                Arguments.of(Tag.PatientAge, "043y"),
                Arguments.of(Tag.PatientAge, ""),
                Arguments.of(Tag.SeriesInstanceUID, "2.25"),
                Arguments.of(Tag.SeriesInstanceUID, "1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.123456789012345"),
                Arguments.of(Tag.SeriesInstanceUID, ""),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn land"),
                Arguments.of(Tag.ClinicalTrialSiteName, ""),
                Arguments.of(Tag.SmokingStatus, "NO"),
                Arguments.of(Tag.SmokingStatus, "UNKNWON"),
                Arguments.of(Tag.SmokingStatus, ""),
                Arguments.of(Tag.Modality, "XR"),
                Arguments.of(Tag.Modality, "AU"),
                Arguments.of(Tag.Modality, "CR"),
                Arguments.of(Tag.Modality, ""),
                Arguments.of(Tag.PatientName, "Hugo"),
                Arguments.of(Tag.PatientName, "")
        );
    }

    @ParameterizedTest
    @MethodSource("providerContains")
    void tagValueContains(int tag, String input){
        assertTrue(exprConditionKheops.tagValueContains(tag, input));
    }

    private static Stream<Arguments> providerContains() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20180209"),
                Arguments.of(Tag.StudyDate, "201802"),
                Arguments.of(Tag.StudyDate, "2018"),
                Arguments.of(Tag.StudyDate, "09"),
                Arguments.of(Tag.StudyDate, "0209"),
                Arguments.of(Tag.StudyDate, "02"),
                Arguments.of(Tag.StudyDate, ""),
                Arguments.of(Tag.PatientAge, "043Y"),
                Arguments.of(Tag.PatientAge, "43Y"),
                Arguments.of(Tag.PatientAge, "43"),
                Arguments.of(Tag.PatientAge, ""),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.12345"),
                Arguments.of(Tag.SeriesInstanceUID, "1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "7890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "7890123"),
                Arguments.of(Tag.SeriesInstanceUID, ""),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, ""),
                Arguments.of(Tag.SmokingStatus, "YES"),
                Arguments.of(Tag.SmokingStatus, ""),
                Arguments.of(Tag.Modality, "CT"),
                Arguments.of(Tag.Modality, "")
        );
    }

    @ParameterizedTest
    @MethodSource("providerNotContains")
    void tagValueNotContains(int tag, String input){
        assertFalse(exprConditionKheops.tagValueContains(tag, input));
    }

    private static Stream<Arguments> providerNotContains() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20190209"),
                Arguments.of(Tag.StudyDate, "0309"),
                Arguments.of(Tag.StudyDate, "0210"),
                Arguments.of(Tag.StudyDate, "20210417"),
                Arguments.of(Tag.PatientAge, "044Y"),
                Arguments.of(Tag.PatientAge, "44Y"),
                Arguments.of(Tag.PatientAge, "043y"),
                Arguments.of(Tag.PatientAge, "43y"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123457"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.2234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "1234567891123456"),
                Arguments.of(Tag.SeriesInstanceUID, "3457"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, "hospital"),
                Arguments.of(Tag.SmokingStatus, "NO"),
                Arguments.of(Tag.SmokingStatus, "UNKNOWN"),
                Arguments.of(Tag.Modality, "XR"),
                Arguments.of(Tag.Modality, "CR"),
                Arguments.of(Tag.Modality, "DOC"),
                Arguments.of(Tag.PatientName, "Hugo"),
                Arguments.of(Tag.PatientName, "")
        );
    }

    @ParameterizedTest
    @MethodSource("providerBeginWith")
    void tagValueBeginWith(int tag, String input){
        assertTrue(exprConditionKheops.tagValueBeginsWith(tag, input));
    }

    private static Stream<Arguments> providerBeginWith() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20180209"),
                Arguments.of(Tag.StudyDate, "201802"),
                Arguments.of(Tag.StudyDate, "2018"),
                Arguments.of(Tag.StudyDate, ""),
                Arguments.of(Tag.PatientAge, "043Y"),
                Arguments.of(Tag.PatientAge, "043"),
                Arguments.of(Tag.PatientAge, ""),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.123456789"),
                Arguments.of(Tag.SeriesInstanceUID, ""),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, ""),
                Arguments.of(Tag.SmokingStatus, "YES"),
                Arguments.of(Tag.SmokingStatus, ""),
                Arguments.of(Tag.Modality, "CT"),
                Arguments.of(Tag.Modality, "")
        );
    }

    @ParameterizedTest
    @MethodSource("providerNotBeginWith")
    void tagValueNotBeginWith(int tag, String input){
        assertFalse(exprConditionKheops.tagValueBeginsWith(tag, input));
    }

    private static Stream<Arguments> providerNotBeginWith() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "09"),
                Arguments.of(Tag.StudyDate, "0209"),
                Arguments.of(Tag.StudyDate, "02"),
                Arguments.of(Tag.StudyDate, "20190209"),
                Arguments.of(Tag.StudyDate, "20180309"),
                Arguments.of(Tag.StudyDate, "20180210"),
                Arguments.of(Tag.PatientAge, "43Y"),
                Arguments.of(Tag.PatientAge, "043y"),
                Arguments.of(Tag.PatientAge, "43y"),
                Arguments.of(Tag.PatientAge, "044Y"),
                Arguments.of(Tag.SeriesInstanceUID, "1.2.1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.7234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123458"),
                Arguments.of(Tag.SeriesInstanceUID, "123456"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn land"),
                Arguments.of(Tag.SmokingStatus, "NO"),
                Arguments.of(Tag.SmokingStatus, "UNKNWON"),
                Arguments.of(Tag.Modality, "XR"),
                Arguments.of(Tag.Modality, "SM"),
                Arguments.of(Tag.Modality, "DOC"),
                Arguments.of(Tag.PatientName, "Hugo"),
                Arguments.of(Tag.PatientName, "")
        );
    }

    @ParameterizedTest
    @MethodSource("providerEndWith")
    void tagValueEndWith(int tag, String input){
        assertTrue(exprConditionKheops.tagValueEndsWith(tag, input));
    }

    private static Stream<Arguments> providerEndWith() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20180209"),
                Arguments.of(Tag.StudyDate, "180209"),
                Arguments.of(Tag.StudyDate, "0209"),
                Arguments.of(Tag.StudyDate, "09"),
                Arguments.of(Tag.StudyDate, ""),
                Arguments.of(Tag.PatientAge, "043Y"),
                Arguments.of(Tag.PatientAge, "43Y"),
                Arguments.of(Tag.PatientAge, "Y"),
                Arguments.of(Tag.PatientAge, ""),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "1234567890123456"),
                Arguments.of(Tag.SeriesInstanceUID, "0123456"),
                Arguments.of(Tag.SeriesInstanceUID, ""),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Land"),
                Arguments.of(Tag.SmokingStatus, "YES"),
                Arguments.of(Tag.Modality, "CT")
        );
    }

    @ParameterizedTest
    @MethodSource("providerNotEndWith")
    void tagValueNotEndWith(int tag, String input){
        assertFalse(exprConditionKheops.tagValueEndsWith(tag, input));
    }

    private static Stream<Arguments> providerNotEndWith() {
        return Stream.of(
                Arguments.of(Tag.StudyDate, "20190209"),
                Arguments.of(Tag.StudyDate, "20180309"),
                Arguments.of(Tag.StudyDate, "20180210"),
                Arguments.of(Tag.StudyDate, "201802"),
                Arguments.of(Tag.StudyDate, "2018"),
                Arguments.of(Tag.StudyDate, "02"),
                Arguments.of(Tag.PatientAge, "043y"),
                Arguments.of(Tag.PatientAge, "043"),
                Arguments.of(Tag.PatientAge, "43"),
                Arguments.of(Tag.PatientAge, "044Y"),
                Arguments.of(Tag.PatientAge, "44Y"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.123456789"),
                Arguments.of(Tag.SeriesInstanceUID, "123456789"),
                Arguments.of(Tag.SeriesInstanceUID, "2.25.1234567890123457"),
                Arguments.of(Tag.SeriesInstanceUID, "1.2.123456789"),
                Arguments.of(Tag.ClinicalTrialSiteName, "unicorn Land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn"),
                Arguments.of(Tag.ClinicalTrialSiteName, "Unicorn land"),
                Arguments.of(Tag.ClinicalTrialSiteName, "land"),
                Arguments.of(Tag.SmokingStatus, "NO"),
                Arguments.of(Tag.SmokingStatus, "UNKNOWN"),
                Arguments.of(Tag.Modality, "XR"),
                Arguments.of(Tag.Modality, "SM"),
                Arguments.of(Tag.Modality, "DOC"),
                Arguments.of(Tag.PatientName, "Hugo"),
                Arguments.of(Tag.PatientName, "")
        );
    }

    /*
    @Test
    void validateCondition() {
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent(Tag.PatientName)"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent((Tag.PatientName))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent((0000,0000))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent((00000000))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent(0000,0000)"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent(00000000)"));

        assertTrue(exprConditionKheops.validateCondition("tagValueContains((0000,0000))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueBeginWith((0000,0000))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueEndWith((0000,0000))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueIsPresent((FFFF,FFFF))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueContains((FFFF,FFFF))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueBeginWith((FFFF,FFFF))"));
        assertTrue(exprConditionKheops.validateCondition("tagValueEndWith((FFFF,FFFF))"));
    }
     */
}