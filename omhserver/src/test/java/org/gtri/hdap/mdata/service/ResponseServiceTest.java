package org.gtri.hdap.mdata.service;

import org.gtri.hdap.mdata.controller.PatientDataController;
import org.gtri.hdap.mdata.jpa.entity.ApplicationUser;
import org.gtri.hdap.mdata.jpa.entity.ApplicationUserId;
import org.gtri.hdap.mdata.jpa.entity.ShimmerData;
import org.gtri.hdap.mdata.jpa.repository.ApplicationUserRepository;
import org.gtri.hdap.mdata.jpa.repository.ShimmerDataRepository;
import org.gtri.hdap.mdata.util.ShimmerUtil;
import org.hl7.fhir.dstu3.model.DocumentReference;
import org.hl7.fhir.dstu3.model.Enumerations;
import org.hl7.fhir.dstu3.model.Observation;
import org.hl7.fhir.dstu3.model.Resource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Created by es130 on 9/14/2018.
 */
@RunWith(SpringRunner.class)
public class ResponseServiceTest {

    @TestConfiguration
    static class ResponseServiceTestContextConfiguration{
        @Bean
        public ResponseService responseService(){
            return new ResponseService();
        }
    }

    @Autowired
    private ResponseService responseService;

    @MockBean
    private ApplicationUserRepository applicationUserRepository;
    @MockBean
    private ShimmerDataRepository shimmerDataRepository;

    private Logger logger = LoggerFactory.getLogger(ResponseServiceTest.class);

    @Test
    public void testParseDate() throws Exception{
        logger.debug("========== Entering testParseDate==========");
        String expected = "2018-09-18T18:00:00.000Z";
        String nomillis = "2018-09-18T18:00:00Z";
        String notExpected = "2018-09-18";

        Date date = responseService.parseDate(expected);
        assertTrue(date != null);
        date = responseService.parseDate(nomillis);
        assertTrue(date != null);
        boolean hadError = false;
        try {
            date = responseService.parseDate(notExpected);
        }
        catch(ParseException pe){
            hadError = true;
        }
        assertTrue(hadError == true);
        logger.debug("========== Exiting testParseDate==========");
    }

    @Test
    public void testGenerateDocumentReference() throws Exception{
        logger.debug("========== Entering testGenerateDocumentReference ==========");
        DocumentReference documentReference = responseService.generateDocumentReference("doc123", "fitbit");

        assertTrue(documentReference != null);
        assertTrue(documentReference.getStatus() == Enumerations.DocumentReferenceStatus.CURRENT);

        logger.debug("Codeable concept: " + documentReference.getType().getText());
        assertTrue(documentReference.getType().getText().equals("OMH fitbit data"));
        assertTrue(documentReference.getIndexed() != null);
        assertTrue(documentReference.getContent().size() == 1);
        assertTrue(documentReference.getContent().get(0).getAttachment().getUrl().equals("Binary/doc123"));
        assertTrue(documentReference.getContent().get(0).getAttachment().getContentType().equals("application/json"));
        assertTrue(documentReference.getContent().get(0).getAttachment().getTitle().equals("OMH fitbit data"));
        assertTrue(documentReference.getContent().get(0).getAttachment().getCreation() != null);

        logger.debug("========== Exiting testGenerateDocumentReference ==========");
    }

    @Test
    public void testGenerateObservation() throws Exception{
        logger.debug("========== Entering testGenerateObservation ==========");
        StringBuilder sb = new StringBuilder();
        //{
        //    "shim": "googlefit",
        //    "timeStamp": 1534251049,
        //    "body": [
        //    {
        //        "header": {
        //            "id": "3b9b68a2-e0fd-4bdd-ba85-4127a4e8bcee",
        //            "creation_date_time": "2018-08-14T12:50:49.383Z",
        //            "acquisition_provenance": {
        //                "source_name": "Google Fit API",
        //                "source_origin_id": "raw:com.google.step_count.cumulative:Google:Pixel 2 XL:5f9e1b9964be5834:LSM6DSM Step Counter"
        //            },
        //            "schema_id": {
        //                "namespace": "omh",
        //                "name": "step-count",
        //                "version": "2.0"
        //            }
        //        },
        //        "body": {
        //            "effective_time_frame": {
        //                "time_interval": {
        //                    "start_date_time": "2018-08-14T00:00:17.805Z",
        //                    "end_date_time": "2018-08-14T00:01:17.805Z"
        //                }
        //            },
        //            "step_count": 7
        //        }
        //    },
        // ...
        //    ]
        //}
        sb.append("{")
                .append("\"shim\": \"googlefit\",")
                .append("\"timeStamp\": 1534251049,")
                .append("\"body\": [")
                .append("{")
                .append(    "\"header\": {")
                .append(        "\"id\": \"3b9b68a2-e0fd-4bdd-ba85-4127a4e8bcee\",")
                .append(        "\"creation_date_time\": \"2018-08-14T12:50:49.383Z\",")
                .append(        "\"acquisition_provenance\": {")
                .append(            "\"source_name\": \"some source\",")
                .append(            "\"source_origin_id\": \"some step counter\"")
                .append(        "},")
                .append(        "\"schema_id\": {")
                .append(            "\"namespace\": \"omh\",")
                .append(            "\"name\": \"step-count\",")
                .append(            "\"version\": \"2.0\"")
                .append(        "}")
                .append(    "},")
                .append(    "\"body\": {")
                .append(        "\"effective_time_frame\": {")
                .append(            "\"time_interval\": {")
                .append(                "\"start_date_time\": \"2018-08-14T00:00:17.805Z\",")
                .append(                "\"end_date_time\": \"2018-08-14T00:01:17.805Z\"")
                .append(            "}")
                .append(        "},")
                .append(        "\"step_count\": 7")
                .append(    "}")
                .append("},")
                .append("{")
                .append(    "\"header\": {")
                .append(        "\"id\": \"3b9b68a2-e0fd-4bdd-ba85-4127a4e8bcff\",")
                .append(        "\"creation_date_time\": \"2018-08-14T12:50:49.383Z\",")
                .append(        "\"acquisition_provenance\": {")
                .append(            "\"source_name\": \"some source\",")
                .append(            "\"source_origin_id\": \"some step counter\"")
                .append(        "},")
                .append(        "\"schema_id\": {")
                .append(            "\"namespace\": \"omh\",")
                .append(            "\"name\": \"step-count\",")
                .append(            "\"version\": \"2.0\"")
                .append(        "}")
                .append(    "},")
                .append(    "\"body\": {")
                .append(        "\"effective_time_frame\": {")
                .append(            "\"time_interval\": {")
                .append(                "\"start_date_time\": \"2018-08-14T00:02:17.805Z\",")
                .append(                "\"end_date_time\": \"2018-08-14T00:03:17.805Z\"")
                .append(            "}")
                .append(        "},")
                .append(        "\"step_count\": 27")
                .append(    "}")
                .append("}")
                .append("]")
                .append("}");
        List<Resource> observationList = responseService.generateObservationList("123456", sb.toString());
        assertTrue(observationList.size() == 2);

        assertTrue(((Observation)observationList.get(0)).getComponent().get(0).getValueQuantity().getValue().intValue() == 7 );
        assertTrue(((Observation)observationList.get(0)).getId() != null );
        assertTrue(((Observation)observationList.get(0)).getContained().get(0).getId().equals(ShimmerUtil.PATIENT_RESOURCE_ID) );
        assertTrue(((Observation)observationList.get(0)).getIdentifier().get(0).getSystem().equals(ShimmerUtil.PATIENT_IDENTIFIER_SYSTEM) );
        assertTrue(((Observation)observationList.get(0)).getIdentifier().get(0).getValue().equals("3b9b68a2-e0fd-4bdd-ba85-4127a4e8bcee") );
        assertTrue(((Observation)observationList.get(0)).getStatus().equals(Observation.ObservationStatus.UNKNOWN) );
        assertTrue(((Observation)observationList.get(0)).getCategory().get(0).getCoding().get(0).getCode().equals(ShimmerUtil.OBSERVATION_CATEGORY_CODE) );
        assertTrue(((Observation)observationList.get(0)).getCategory().get(0).getCoding().get(0).getSystem().equals(ShimmerUtil.OBSERVATION_CATEGORY_SYSTEM) );
        assertTrue(((Observation)observationList.get(0)).getCategory().get(0).getCoding().get(0).getDisplay().equals(ShimmerUtil.OBSERVATION_CATEGORY_DISPLAY) );
        assertTrue(((Observation)observationList.get(0)).getCode().getCoding().get(0).getCode().equals(ShimmerUtil.OBSERVATION_CODE_CODE) );
        assertTrue(((Observation)observationList.get(0)).getCode().getCoding().get(0).getSystem().equals(ShimmerUtil.OBSERVATION_CODE_SYSTEM) );
        assertTrue(((Observation)observationList.get(0)).getCode().getCoding().get(0).getDisplay().equals(ShimmerUtil.OBSERVATION_CODE_DISPLAY) );
        assertTrue(((Observation)observationList.get(0)).getSubject().getReference() != null );
        assertTrue(((Observation)observationList.get(0)).getEffectivePeriod().getStart() != null );
        assertTrue(((Observation)observationList.get(0)).getEffectivePeriod().getEnd() != null );
        assertTrue(((Observation)observationList.get(0)).getIssued() != null );
        assertTrue(((Observation)observationList.get(0)).getDevice().getDisplay().equals("some source,some step counter,1534251049") );

        assertTrue(((Observation)observationList.get(1)).getComponent().get(0).getValueQuantity().getValue().intValue() == 27);
        assertTrue(((Observation)observationList.get(1)).getId() != null );
        assertTrue(((Observation)observationList.get(1)).getContained().get(0).getId().equals(ShimmerUtil.PATIENT_RESOURCE_ID) );
        assertTrue(((Observation)observationList.get(1)).getIdentifier().get(0).getSystem().equals(ShimmerUtil.PATIENT_IDENTIFIER_SYSTEM) );
        assertTrue(((Observation)observationList.get(1)).getIdentifier().get(0).getValue().equals("3b9b68a2-e0fd-4bdd-ba85-4127a4e8bcff") );
        assertTrue(((Observation)observationList.get(1)).getStatus().equals(Observation.ObservationStatus.UNKNOWN) );
        assertTrue(((Observation)observationList.get(1)).getCategory().get(0).getCoding().get(0).getCode().equals(ShimmerUtil.OBSERVATION_CATEGORY_CODE) );
        assertTrue(((Observation)observationList.get(1)).getCategory().get(0).getCoding().get(0).getSystem().equals(ShimmerUtil.OBSERVATION_CATEGORY_SYSTEM) );
        assertTrue(((Observation)observationList.get(1)).getCategory().get(0).getCoding().get(0).getDisplay().equals(ShimmerUtil.OBSERVATION_CATEGORY_DISPLAY) );
        assertTrue(((Observation)observationList.get(1)).getCode().getCoding().get(0).getCode().equals(ShimmerUtil.OBSERVATION_CODE_CODE) );
        assertTrue(((Observation)observationList.get(1)).getCode().getCoding().get(0).getSystem().equals(ShimmerUtil.OBSERVATION_CODE_SYSTEM) );
        assertTrue(((Observation)observationList.get(1)).getCode().getCoding().get(0).getDisplay().equals(ShimmerUtil.OBSERVATION_CODE_DISPLAY) );
        assertTrue(((Observation)observationList.get(1)).getSubject().getReference() != null );
        assertTrue(((Observation)observationList.get(1)).getEffectivePeriod().getStart() != null );
        assertTrue(((Observation)observationList.get(1)).getEffectivePeriod().getEnd() != null );
        assertTrue(((Observation)observationList.get(1)).getIssued() != null );
        logger.debug("========== Exiting testGenerateObservation ==========");
    }
}
