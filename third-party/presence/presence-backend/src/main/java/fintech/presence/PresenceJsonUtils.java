package fintech.presence;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.DefaultSerializerProvider;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.util.NameTransformer;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fintech.presence.model.PhoneDescription;
import fintech.presence.model.PhoneRecordsWrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PresenceJsonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.setSerializerProvider(new CustomPresenceSerializerProvider());
        mapper.registerModule(new JavaTimeModule());
    }

    public static class CustomPresenceSerializerProvider extends DefaultSerializerProvider {

        CustomPresenceSerializerProvider() {
            super();
        }

        CustomPresenceSerializerProvider(CustomPresenceSerializerProvider provider, SerializationConfig config, SerializerFactory jsf) {
            super(provider, config, jsf);
        }

        @Override
        public CustomPresenceSerializerProvider createInstance(SerializationConfig config, SerializerFactory jsf) {
            return new CustomPresenceSerializerProvider(this, config, jsf);
        }

        @Override
        public JsonSerializer<Object> findNullValueSerializer(BeanProperty property) throws JsonMappingException {
            if (property.getType().getRawClass().equals(String.class)) {
                return EmptyStringSerializer.INSTANCE;
            } else if (property.getType().getRawClass().equals(Integer.class)) {
                return ZeroIntegerSerializer.INSTANCE;
            } else {
                return super.findNullValueSerializer(property);
            }
        }
    }

    public static class EmptyStringSerializer extends JsonSerializer<Object> {
        static final JsonSerializer<Object> INSTANCE = new EmptyStringSerializer();

        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeString("");
        }
    }

    public static class ZeroIntegerSerializer extends JsonSerializer<Object> {
        static final JsonSerializer<Object> INSTANCE = new ZeroIntegerSerializer();

        @Override
        public void serialize(Object o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeNumber(0);
        }
    }

    public static class PhoneRecordsSerializer extends JsonSerializer<PhoneRecordsWrapper> {
        private final JsonSerializer<PhoneRecordsWrapper> delegate = new UnwrappingPhoneRecordsSerializer();

        @Override
        public void serialize(PhoneRecordsWrapper value, JsonGenerator jsonGenerator, SerializerProvider serializers) throws IOException {
            jsonGenerator.writeStartObject();
            this.delegate.serialize(value, jsonGenerator, serializers);
            jsonGenerator.writeEndObject();
        }

        @Override
        public JsonSerializer<PhoneRecordsWrapper> unwrappingSerializer(NameTransformer unwrapper) {
            return this.delegate;
        }
    }

    public static class UnwrappingPhoneRecordsSerializer extends JsonSerializer<PhoneRecordsWrapper> {
        @Override
        public boolean isUnwrappingSerializer() {
            return true;
        }

        @Override
        public void serialize(PhoneRecordsWrapper request, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            int i = 0;
            for (; i < request.getPhoneRecords().size() && i < 10; i++) {
                PhoneRecord phoneRecord = request.getPhoneRecords().get(i);
                String index = (i == 0) ? "" : String.valueOf((i + 1));
                jsonGenerator.writeStringField("PhoneNumber" + index, phoneRecord.getNumber());
                jsonGenerator.writeNumberField("PhoneDescription" + index, phoneRecord.getDescription().toValue());
                jsonGenerator.writeStringField("PhoneTimeZoneId" + index, "");
            }
            for (int j = i; j < 10; j++) {
                String index = (j == 0) ? "" : String.valueOf((j + 1));
                jsonGenerator.writeStringField("PhoneNumber" + index, "");
                jsonGenerator.writeNumberField("PhoneDescription" + index, PhoneDescription.NOT_SPECIFIED.toValue());
                jsonGenerator.writeStringField("PhoneTimeZoneId" + index, "");
            }
        }

    }

    public static class PhoneRecordsDeserializer extends JsonDeserializer<PhoneRecordsWrapper> {
        private final JsonDeserializer<PhoneRecordsWrapper> delegate = new UnwrappingPhoneRecordsDeserializer();

        @Override
        public JsonDeserializer<PhoneRecordsWrapper> unwrappingDeserializer(NameTransformer unwrapper) {
            return this.delegate;
        }

        @Override
        public PhoneRecordsWrapper deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            return this.delegate.deserialize(jsonParser, ctxt);
        }
    }

    public static class UnwrappingPhoneRecordsDeserializer extends JsonDeserializer<PhoneRecordsWrapper> {

        @Override
        public PhoneRecordsWrapper deserialize(JsonParser jsonParser, DeserializationContext ctxt) throws IOException {
            List<PhoneRecord> phoneRecords = new ArrayList<>();
            JsonNode node = jsonParser.getCodec().readTree(jsonParser);
            for (int i = 1; i <= 10; i++) {
                PhoneRecord phoneRecord = new PhoneRecord();
                String phoneNumber = node.get("PhoneNumber" + i).asText();
                if (phoneNumber.isEmpty()) {
                    continue;
                }
                phoneRecord.setNumber(phoneNumber);
                phoneRecord.setDescription(PhoneDescription.forValue(node.get("PhoneDescription" + i).asInt()));
                phoneRecords.add(phoneRecord);
            }

            return new PhoneRecordsWrapper(phoneRecords);
        }
    }

    public static ObjectMapper getObjectMapper() {
        return mapper;
    }
}
