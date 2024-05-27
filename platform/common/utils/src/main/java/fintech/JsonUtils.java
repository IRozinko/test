package fintech;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Map;

public class JsonUtils {

    private static final ObjectMapper mapper;

    static {
        mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.registerModule(new JavaTimeModule());
    }

    public static <T> T readValue(String json, Class<T> targetClass) {
        try {
            return mapper.readValue(json, targetClass);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to parse to class %s json: %s", targetClass, json), e);
        }
    }

    public static <T> T readValue(String json, TypeReference<T> type) {
        try {
            return mapper.readValue(json, type);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to parse to class %s json: %s", type, json), e);
        }
    }

    public static JsonNode readTree(String json) {
        try {
            return mapper.readTree(json);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to parse to JsonNode json: %s", json), e);
        }
    }

    public static JsonNode readTree(Object val) {
        return mapper.valueToTree(val);
    }

    public static <T> T treeToValue(JsonNode node, Class<T> tClass) {
        try {
            return mapper.treeToValue(node, tClass);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(String.format("Failed to convert  JsonNode to POJO class %s : %s", tClass, node), e);
        }
    }

    public static Map<String, ?> readValueAsMap(String json) {
        return readValue(json, new TypeReference<Map<String, Object>>() {
        });
    }

    public static String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(String.format("Failed to write value to json: %s", value), e);
        }
    }

    public static boolean isJsonValid(String jsonText) {
        try {
            final ObjectMapper mapper = new ObjectMapper();
            mapper.readTree(jsonText);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static <T> T convert(Map<String, Object> source, Class<T> type) {
        return mapper.convertValue(source, type);
    }

    @SneakyThrows
    public static String formatJson(String input) {
        Object object = mapper.readValue(input, Object.class);
        return mapper.writeValueAsString(object);
    }

    public static ObjectMapper getMapper() {
        return mapper;
    }

    public static JsonNode toJsonNode(Object value) {
        return mapper.valueToTree(value);
    }

    @SneakyThrows
    public static JsonNode toJsonNode(String value) {
        return mapper.readTree(value);
    }

    @SneakyThrows
    public static <T> T readValue(JsonNode data, Class<T> modelClass) {
        return mapper.treeToValue(data, modelClass);
    }
    public static <T> T readValue(JsonNode node, TypeReference<T> type) {
        try {
            return mapper.readerFor(type).readValue(node);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to parse to class %s json: %s", type, node), e);
        }
    }
}
