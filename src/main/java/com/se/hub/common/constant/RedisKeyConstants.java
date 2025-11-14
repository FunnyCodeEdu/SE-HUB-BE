package com.se.hub.common.constant;

/**
 * Redis Key Constants
 * 
 * Centralized documentation of Redis key naming conventions across all modules
 * 
 * Key Naming Pattern: module:type:identifier
 * 
 * Examples:
 * - notif:unread:user:userId
 * - chat:session:user:userId
 * - cache:blog:blogId
 * 
 * Best Practices:
 * - Use hierarchical naming with module prefix
 * - Use colons (:) as separators
 * - Keep keys descriptive and consistent
 * - Document all key patterns in this file
 */
public class RedisKeyConstants {
    
    // Module prefixes
    public static final String MODULE_NOTIFICATION = "notif";
    public static final String MODULE_CHAT = "chat";
    public static final String MODULE_CACHE = "cache";
    
    // Notification module keys (defined in NotificationConstants)
    // Pattern: notif:type:identifier
    // - notif:unread:user:userId
    // - notif:recent:user:userId
    // - notif:channel:user:userId
    // - notif:agg:aggregationKey
    
    // Chat module keys (defined in RedisKeys)
    // Pattern: module:type:identifier
    // - user:session:userId (session management)
    // - user:session:heartbeat:userId:sessionId (heartbeat tracking)
    // - conversation:room:conversationId (conversation rooms)
    
    // Blog Cache keys (managed by Spring Cache)
    // Pattern: cache:cacheName:key
    // - cache:blog:blogId
    // - cache:blogs:page_size_sort
    // - cache:blogsByAuthor:authorId_page_size_sort
    // - cache:popularBlogs:page_size_sort
    // - cache:likedBlogs:page_size_sort
    // - cache:latestBlogs:page_size_sort
    
    private RedisKeyConstants() {
        // Prevent instantiation
    }
}

