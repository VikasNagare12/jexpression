package com.example.jexpression.droolsfeel;

import com.example.jexpression.droolsfeel.converter.RuleConverter;
import com.example.jexpression.droolsfeel.model.FeelRule;
import com.example.jexpression.droolsfeel.model.ValidationRule;
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
 * Service for loading and compiling validation rules.
 * 
 * <p>
 * Supports loading from:
 * <ul>
 * <li>Classpath resources</li>
 * <li>File system paths</li>
 * <li>JSON strings</li>
 * </ul>
 * 
 * <p>
 * Rules are compiled at load time for fail-fast validation.
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

    // ─────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────

    /**
     * Load rules from classpath resource.
     * 
     * @param resourcePath Path relative to classpath (e.g.,
     *                     "rules/validation.json")
     * @return List of compiled FEEL rules
     * @throws RuleLoadingException if loading fails
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
            List<ValidationRule> rawRules = parseJsonString(jsonContent);
            return ruleConverter.convert(rawRules);
        } catch (IOException e) {
            throw new RuleLoadingException("Failed to parse JSON content", e);
        }
    }

    /**
     * Load rules from input stream.
     */
    public List<FeelRule> loadFromStream(InputStream input) throws IOException {
        List<ValidationRule> rawRules = parseStream(input);
        return ruleConverter.convert(rawRules);
    }

    // ─────────────────────────────────────────────────────────────
    // Parsing
    // ─────────────────────────────────────────────────────────────

    private List<ValidationRule> parseStream(InputStream input) throws IOException {
        Map<String, List<ValidationRule>> wrapper = objectMapper.readValue(
                input,
                new TypeReference<Map<String, List<ValidationRule>>>() {
                });
        return extractRules(wrapper);
    }

    private List<ValidationRule> parseJsonString(String json) throws IOException {
        Map<String, List<ValidationRule>> wrapper = objectMapper.readValue(
                json,
                new TypeReference<Map<String, List<ValidationRule>>>() {
                });
        return extractRules(wrapper);
    }

    private List<ValidationRule> extractRules(Map<String, List<ValidationRule>> wrapper) {
        List<ValidationRule> rules = wrapper.get("rules");
        if (rules == null) {
            throw new RuleLoadingException("JSON must contain 'rules' array");
        }
        log.debug("Parsed {} raw rules", rules.size());
        return rules;
    }

    // ─────────────────────────────────────────────────────────────
    // Exception
    // ─────────────────────────────────────────────────────────────

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
