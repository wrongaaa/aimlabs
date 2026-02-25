package com.aimlabs.game;

/**
 * 游戏模式枚举
 */
public enum GameMode {
    FLICK("Flick 甩枪", "快速瞄准并点击出现的靶标"),
    TRACKING("Tracking 追踪", "持续追踪移动中的靶标"),
    SPEED("Speed 速度", "在靶标消失前尽快点击"),
    PRECISION("Precision 精准", "点击极小的靶标，考验精度"),
    REACTION("Reaction 反应", "靶标随机出现，测试反应速度"),
    SWITCH("Switch 切换", "在多个靶标间快速切换点击");

    private final String displayName;
    private final String description;

    GameMode(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() { return displayName; }
    public String getDescription() { return description; }
}
