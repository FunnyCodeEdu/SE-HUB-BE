package com.se.hub.modules.exam.utils;

import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Utility class for normalizing and hashing question content for duplicate detection
 */
@Slf4j
public class QuestionHashUtil {
    private QuestionHashUtil(){}
    private static final int WORDS_TO_TAKE_FROM_QUESTION = 10; // Lấy 10 từ đầu của câu hỏi
    private static final int WORDS_TO_TAKE_FROM_OPTION = 5; // Lấy 5 từ đầu của mỗi câu trả lời
    
    /**
     * Normalize text: lowercase, remove special characters, keep Vietnamese characters
     */
    public static String normalizeQuestion(String text) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
//        return text
//                .toLowerCase()
//                .replaceAll("\\s+", " ")          // gộp nhiều space thành 1
//                .replaceAll("[^0-9a-zA-Zà-ỹÀ-Ỹ ]", "") // bỏ ký tự đặc biệt, giữ chữ + số + tiếng Việt
//                .trim();

        return text
                .toLowerCase()
                .replaceAll("\\s+", " ")              // gộp multiple spaces
                .replaceAll("[^\\p{L}\\p{N} ]", "")   // giữ: chữ (mọi ngôn ngữ) + số + space
                .trim();
    }
    
    /**
     * Get first N words from text
     * If text has fewer than N words, returns all words
     * Example: getFirstNWords("A B", 5) returns "A B" (only 2 words available)
     */
    private static String getFirstNWords(String text, int n) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }
        String[] words = text.split("\\s+");
        // Lấy số từ tối đa có thể (nếu text có ít hơn N từ thì lấy hết)
        int wordsToTake = Math.min(n, words.length);
        if (wordsToTake == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < wordsToTake; i++) {
            if (i > 0) sb.append(" ");
            sb.append(words[i]);
        }
        return sb.toString();
    }
    
    /**
     * Build hash content from question content and options
     * Format: first N words of question + first M words of each option
     */
    public static String buildHashContent(String questionContent, List<String> optionContents) {
        StringBuilder sb = new StringBuilder();
        
        // Lấy vài từ đầu của câu hỏi
        String normalizedQuestion = normalizeQuestion(questionContent);
        String questionPrefix = getFirstNWords(normalizedQuestion, WORDS_TO_TAKE_FROM_QUESTION);
        sb.append(questionPrefix);
        
        // Lấy vài từ đầu của mỗi câu trả lời
        if (optionContents != null && !optionContents.isEmpty()) {
            for (String optionContent : optionContents) {
                String normalizedOption = normalizeQuestion(optionContent);
                String optionPrefix = getFirstNWords(normalizedOption, WORDS_TO_TAKE_FROM_OPTION);
                if (!optionPrefix.isEmpty()) {
                    sb.append(" | ").append(optionPrefix);
                }
            }
        }
        
        return sb.toString();
    }
    
    /**
     * Generate SHA-256 hash from text
     */
    public static String generateHash(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(text.getBytes(StandardCharsets.UTF_8));
            
            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            log.error("QuestionHashUtil_generateHash_SHA-256 algorithm not found", e);
            throw new RuntimeException("Hash generation failed", e);
        }
    }
    
    /**
     * Generate content hash for a question with its options
     */
    public static String generateQuestionHash(String questionContent, List<String> optionContents) {
        String hashContent = buildHashContent(questionContent, optionContents);
        return generateHash(hashContent);
    }
}

