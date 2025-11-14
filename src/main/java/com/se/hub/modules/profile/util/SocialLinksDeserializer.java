package com.se.hub.modules.profile.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * Custom deserializer for socialLinks field
 * Accepts both JSON string and JSON object, converts to JSON string
 */
public class SocialLinksDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);
        
        if (node == null || node.isNull()) {
            return null;
        }
        
        // If already a string, return as is
        if (node.isTextual()) {
            return node.asText();
        }
        
        // If it's an object or array, convert to JSON string
        if (node.isObject() || node.isArray()) {
            ObjectMapper mapper = (ObjectMapper) p.getCodec();
            return mapper.writeValueAsString(node);
        }
        
        // For other types (number, boolean), convert to string
        return node.asText();
    }
}

