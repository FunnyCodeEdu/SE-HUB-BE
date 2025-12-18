package com.se.hub.modules.profile.constant.follow;

public class FollowConstants {
    //===== TABLE NAME ======
    public static final String TABLE_FOLLOW = "follow";

    //===== COLUMN NAME ======
    public static final String COL_FOLLOWER_ID = "follower_id";
    public static final String COL_FOLLOWING_ID = "following_id";

    //===== INDEX =====
    public static final String INDEX_FOLLOWER = "idx_follow_follower";
    public static final String INDEX_FOLLOWING = "idx_follow_following";

    private FollowConstants() {}
}
