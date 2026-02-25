package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.awt.Graphics2D;
import java.util.List;

/**
 * 游戏模式接口
 */
public interface ModeHandler {
    void init(int width, int height, GameConfig config);
    void update(double dt, int width, int height);
    void onMouseClick(double x, double y, GameStats stats);
    void onMouseMove(double x, double y, GameStats stats);
    void onMousePress(double x, double y, GameStats stats);
    void onMouseRelease(double x, double y, GameStats stats);
    List<Target> getTargets();
    void reset();
    String getModeInfo();
}
