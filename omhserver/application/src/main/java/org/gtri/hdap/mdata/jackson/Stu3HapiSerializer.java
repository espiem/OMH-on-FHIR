package org.gtri.hdap.mdata.jackson;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.parser.IParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by es130 on 7/20/2018.
 */
public class Stu3HapiSerializer extends StdSerializer<IBaseResource> {

    Logger logger = LoggerFactory.getLogger(Stu3HapiSerializer.class);

    public Stu3HapiSerializer(Class<IBaseResource> t) {
        super(t);
    }

    @Override
    public void serialize(IBaseResource iBaseResource, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        IParser jsonParser = FhirContext.forDstu3().newJsonParser();
        String json = jsonParser.encodeResourceToString(iBaseResource);
        logger.debug("Parsed HAPI DSTU3 Object");
        logger.debug(json);
        jsonGenerator.writeRaw(json);
    }
}
