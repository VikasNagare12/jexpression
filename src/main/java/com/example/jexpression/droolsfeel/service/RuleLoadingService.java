package com.example.jexpression.droolsfeel.service;

import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.RuleDefinition;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * Service for loading and compiling rules.
 */
@Service
public class RuleLoadingService {

    private static final Logger log = LoggerFactory.getLogger(RuleLoadingService.class);

    private final ObjectMapper objectMapper;
    private final RuleConverter ruleConverter;

    public RuleLoadingService(ObjectMapper objectMapper, RuleConverter ruleConverter) {
        this.objectMapper = objectMapper;
        this.ruleConverter = ruleConverter;
    }

    /**
     * Load rules from classpath resource.
     */
    public List<FeelRule> loadFromClasspath(String resourcePath) {
        log.info("Loading rules from classpath: {}", resourcePath);

        try (InputStream input = new ClassPathResource(resourcePath).getInputStream()) {
            return loadFromStream(input);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to load from classpath: " + resourcePath, e);
        }
    }

    /**
     * Load rules from file system.
     */
    public List<FeelRule> loadFromFile(Path filePath) {
        log.info("Loading rules from file: {}", filePath);

        try (InputStream input = Files.newInputStream(filePath)) {
            return loadFromStream(input);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to load from file: " + filePath, e);
        }
    }

    /**
     * Load rules from JSON string.
     */
    public List<FeelRule> loadFromJson(String jsonContent) {
        log.info("Loading rules from JSON string ({} chars)", jsonContent.length());

        try {
            List<RuleDefinition> definitions = parseJsonString(jsonContent);
            return ruleConverter.convert(definitions);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to parse JSON content", e);
        }
    }

    /**
     * Load rules from input stream.
     */
    public List<FeelRule> loadFromStream(InputStream input) throws IOException {
        List<RuleDefinition> definitions = parseStream(input);
        return ruleConverter.convert(definitions);
    }

    // ─────────────────────────────────────────────────────────────
    // Parsing
    // ─────────────────────────────────────────────────────────────

    private List<RuleDefinition> parseStream(InputStream input) throws IOException {
        Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(
                input, new TypeReference<>() {
                });
        return extractRules(wrapper);
    }

    private List<RuleDefinition> parseJsonString(String json) throws IOException {
        Map<String, List<RuleDefinition>> wrapper = objectMapper.readValue(
                json, new TypeReference<>() {
                });
        return extractRules(wrapper);
    }

    private List<RuleDefinition> extractRules(Map<String, List<RuleDefinition>> wrapper) {
        List<RuleDefinition> rules = wrapper.get("rules");
        if (rules == null) {
            throw new RuleLoadingException("JSON must contain 'rules' array");
        }
        log.debug("Parsed {} rule definitions", rules.size());
        return rules;
    }

    /**
     * Exception thrown when rule loading fails.
     */
    public static class RuleLoadingException extends RuntimeException {
        public RuleLoadingException(String message) {
            super(message);
        }

        public RuleLoadingException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
