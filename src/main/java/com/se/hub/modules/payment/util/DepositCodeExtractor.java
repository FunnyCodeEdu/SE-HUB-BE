package com.se.hub.modules.payment.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class DepositCodeExtractor {
    
    private static final Pattern SEHUB_PATTERN = Pattern.compile("SEHUB([A-Za-z0-9]+)", Pattern.CASE_INSENSITIVE);
    
    /**
     * Extract deposit code from transaction description
     * Description format examples:
     * - "110787649577 0937258678 SEHUBwed201DN281.ABC...."
     * - "SEHUBhaind"
     * - "SEHUBhaindqe"
     * 
     * @param description Transaction description
     * @return Deposit code (e.g., "SEHUBwed201DN281", "SEHUBhaind", "SEHUBhaindqe") or null if not found
     */
    public String extractDepositCode(String description) {
        if (description == null || description.trim().isEmpty()) {
            return null;
        }
        
        String normalizedDesc = description.trim();
        
        // Try to find SEHUB pattern
        Matcher matcher = SEHUB_PATTERN.matcher(normalizedDesc);
        
        if (matcher.find()) {
            String code = matcher.group(0); // Full match including "SEHUB"
            log.debug("Extracted deposit code: {} from description: {}", code, description);
            return code.toUpperCase();
        }
        
        log.debug("No SEHUB deposit code found in description: {}", description);
        return null;
    }
    
    /**
     * Check if description contains a SEHUB deposit code
     */
    public boolean containsSEHUBCode(String description) {
        return extractDepositCode(description) != null;
    }
}

