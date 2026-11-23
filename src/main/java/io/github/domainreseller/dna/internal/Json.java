package io.github.domainreseller.dna.internal;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.CoercionAction;
import com.fasterxml.jackson.databind.cfg.CoercionInputShape;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import io.github.domainreseller.dna.CodedEnum;
import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

/**
 * The JSON settings used for request bodies and responses. Not part of the public API.
 *
 * <p>Reading is forgiving, because a loosely typed API should not fail a whole answer over one field: unknown
 * properties are ignored, names match in any case, {@code null} and {@code ""} become null (or 0/false for
 * primitives), numbers may come as text, booleans as {@code 1}/{@code "yes"}, dates with or without an offset, and
 * coded enums as numbers or names. Writing leaves out null values and writes coded enums as numbers.
 */
public final class Json {
    /** The shared, thread-safe mapper. */
    public static final ObjectMapper MAPPER = create();

    private Json() {
    }

    /**
     * Writes a value as JSON.
     *
     * @param value the value
     * @return the JSON text
     */
    public static String write(Object value) {
        try {
            return MAPPER.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("The request body could not be written as JSON: " + e.getOriginalMessage(), e);
        }
    }

    /**
     * Returns the type an answer is decoded into: the override when there is one, otherwise the type argument of
     * {@code BaseRequest} in the declaration of the request class.
     *
     * @param requestClass the request class
     * @param baseClass    the {@code BaseRequest} class
     * @param override     the type the request asked for, or null
     * @return the answer type
     */
    public static JavaType responseType(Class<?> requestClass, Class<?> baseClass, Type override) {
        if (override != null) {
            return MAPPER.getTypeFactory().constructType(override);
        }
        JavaType base = MAPPER.getTypeFactory().constructType(requestClass).findSuperType(baseClass);
        if (base == null || base.containedTypeCount() != 1) {
            throw new IllegalStateException(requestClass.getName() + " does not declare its answer type; extend "
                    + "BaseRequest<Answer> or override responseType().");
        }
        return base.containedType(0);
    }

    /**
     * Reads a date and time the way the API writes them: with an offset, without one (read as UTC), or a date alone.
     * Empty, unreadable values and the year-1 placeholder become null.
     *
     * @param text the text
     * @return the date and time, or null
     */
    public static OffsetDateTime parseDateTime(String text) {
        String s = text == null ? "" : text.trim();
        if (s.isEmpty()) {
            return null;
        }
        OffsetDateTime value;
        try {
            value = OffsetDateTime.parse(s, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        } catch (DateTimeParseException notOffset) {
            try {
                value = LocalDateTime.parse(s, DateTimeFormatter.ISO_LOCAL_DATE_TIME).atOffset(ZoneOffset.UTC);
            } catch (DateTimeParseException notLocal) {
                try {
                    value = LocalDate.parse(s, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay().atOffset(ZoneOffset.UTC);
                } catch (DateTimeParseException notDate) {
                    return null;
                }
            }
        }
        return value.getYear() <= 1 ? null : value;
    }

    private static ObjectMapper create() {
        SimpleDeserializers deserializers = new SimpleDeserializers() {
            private static final long serialVersionUID = 1L;

            @Override
            public JsonDeserializer<?> findEnumDeserializer(Class<?> type, DeserializationConfig config, BeanDescription beanDesc)
                    throws JsonMappingException {
                if (CodedEnum.class.isAssignableFrom(type)) {
                    return new CodedEnumDeserializer(type);
                }
                return super.findEnumDeserializer(type, config, beanDesc);
            }
        };
        deserializers.addDeserializer(OffsetDateTime.class, new DateTimeDeserializer());
        deserializers.addDeserializer(Boolean.class, new LenientBooleanDeserializer(null));
        deserializers.addDeserializer(Boolean.TYPE, new LenientBooleanDeserializer(Boolean.FALSE));

        SimpleModule module = new SimpleModule("domainnameapi");
        module.setDeserializers(deserializers);
        module.addSerializer(CodedEnum.class, new CodedEnumSerializer());
        module.addSerializer(OffsetDateTime.class, new DateTimeSerializer());

        return JsonMapper.builder()
                .addModule(module)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .defaultPropertyInclusion(JsonInclude.Value.construct(JsonInclude.Include.NON_NULL, JsonInclude.Include.NON_NULL))
                .withCoercionConfigDefaults(config -> config
                        .setCoercion(CoercionInputShape.EmptyString, CoercionAction.AsNull)
                        .setAcceptBlankAsEmpty(Boolean.TRUE))
                .build();
    }

    /** Reads a coded enum from its number (also as text) or its name; unknown numbers become UNKNOWN. */
    static final class CodedEnumDeserializer extends JsonDeserializer<Object> {
        private final Class<?> type;

        CodedEnumDeserializer(Class<?> type) {
            this.type = type;
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonToken token = p.currentToken();
            if (token == JsonToken.VALUE_NUMBER_INT) {
                return byCode(p.getLongValue());
            }
            if (token == JsonToken.VALUE_STRING) {
                String text = p.getText().trim();
                if (text.isEmpty()) {
                    return null;
                }
                try {
                    return byCode(Long.parseLong(text));
                } catch (NumberFormatException notNumber) {
                    Object named = byName(text);
                    if (named != null) {
                        return named;
                    }
                    return ctxt.handleWeirdStringValue(type, text, "not a %s value", type.getSimpleName());
                }
            }
            return ctxt.handleUnexpectedToken(type, p);
        }

        private Object byCode(long code) {
            Object unknown = null;
            for (Object constant : type.getEnumConstants()) {
                if (((CodedEnum) constant).code() == code) {
                    return constant;
                }
                if (((Enum<?>) constant).name().equals("UNKNOWN")) {
                    unknown = constant;
                }
            }
            return unknown;
        }

        private Object byName(String text) {
            String wanted = text.replace("_", "").replace("-", "").toLowerCase(Locale.ROOT);
            for (Object constant : type.getEnumConstants()) {
                String name = ((Enum<?>) constant).name().replace("_", "").toLowerCase(Locale.ROOT);
                if (name.equals(wanted)) {
                    return constant;
                }
            }
            return null;
        }
    }

    /** Writes a coded enum as its number. */
    static final class CodedEnumSerializer extends JsonSerializer<CodedEnum> {
        @Override
        public void serialize(CodedEnum value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeNumber(value.code());
        }
    }

    /** Reads dates with {@link #parseDateTime(String)}; anything that is not text becomes null. */
    static final class DateTimeDeserializer extends JsonDeserializer<OffsetDateTime> {
        @Override
        public OffsetDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            if (p.currentToken() == JsonToken.VALUE_STRING) {
                return parseDateTime(p.getText());
            }
            p.skipChildren();
            return null;
        }
    }

    /** Writes dates as ISO 8601 text with their offset. */
    static final class DateTimeSerializer extends JsonSerializer<OffsetDateTime> {
        @Override
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(DateTimeFormatter.ISO_OFFSET_DATE_TIME.format(value));
        }
    }

    /** Reads true/false, numbers (0 is false) and the texts true/false, 1/0, yes/no and on/off. */
    static final class LenientBooleanDeserializer extends JsonDeserializer<Boolean> {
        private final Boolean nullValue;

        LenientBooleanDeserializer(Boolean nullValue) {
            this.nullValue = nullValue;
        }

        @Override
        public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            switch (p.currentToken()) {
                case VALUE_TRUE:
                    return Boolean.TRUE;
                case VALUE_FALSE:
                    return Boolean.FALSE;
                case VALUE_NUMBER_INT:
                case VALUE_NUMBER_FLOAT:
                    return p.getDecimalValue().signum() != 0;
                case VALUE_STRING:
                    switch (p.getText().trim().toLowerCase(Locale.ROOT)) {
                        case "":
                            return nullValue;
                        case "true":
                        case "1":
                        case "yes":
                        case "on":
                            return Boolean.TRUE;
                        case "false":
                        case "0":
                        case "no":
                        case "off":
                            return Boolean.FALSE;
                        default:
                            return (Boolean) ctxt.handleWeirdStringValue(Boolean.class, p.getText(), "not a boolean");
                    }
                default:
                    return (Boolean) ctxt.handleUnexpectedToken(Boolean.class, p);
            }
        }

        @Override
        public Boolean getNullValue(DeserializationContext ctxt) {
            return nullValue;
        }
    }

    /**
     * Reads registry attributes as text: an object's values of any type become strings; the empty array or null the
     * API sometimes sends instead of an empty object become an empty map.
     */
    public static final class LenientStringMapDeserializer extends JsonDeserializer<Map<String, String>> {
        /** Creates the deserializer. */
        public LenientStringMapDeserializer() {
        }

        @Override
        public Map<String, String> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            JsonNode node = p.readValueAsTree();
            Map<String, String> result = new LinkedHashMap<>();
            if (node != null && node.isObject()) {
                for (Map.Entry<String, JsonNode> field : node.properties()) {
                    JsonNode value = field.getValue();
                    if (value == null || value.isNull()) {
                        result.put(field.getKey(), "");
                    } else {
                        result.put(field.getKey(), value.isValueNode() ? value.asText() : value.toString());
                    }
                }
            }
            return Values.map(result);
        }

        @Override
        public Map<String, String> getNullValue(DeserializationContext ctxt) {
            return Map.of();
        }
    }
}
