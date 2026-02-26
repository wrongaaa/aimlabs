package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Reaction模式 - 反应：靶标在随机延迟后出现，测量反应时间
 */
public class ReactionMode implements ModeHandler {
    private final List<Target> targets = new ArrayList<>();
    private final Random random = new Random();
    private GameConfig config;
    private int width, height;
    private double waitTimer = 0;
    private double nextDelay;
    private boolean waiting = true;
    private String stateText = "等待中...";

    @Override
    public void init(int width, int height, GameConfig config) {
        this.config = config;
        this.width = width;
        this.height = height;
        reset();
    }

    @Override
    public void update(double dt, int width, int height) {
        this.width = width;
        this.height = height;
        if (waiting) {
            waitTimer += dt;
            if (waitTimer >= nextDelay) {
                waiting = false;
                stateText = "点击!";
                spawnTarget();
            }
        }
    }

    @Override
    public void onMouseClick(double x, double y, GameStats stats) {
        if (waiting) {
            // 提前点击扣分
            stats.recordMiss();
            stateText = "太早了! 等待靶标出现...";
            waitTimer = 0;
            nextDelay = config.getReactionMinDelay() +
                random.nextDouble() * (config.getReactionMaxDelay() - config.getReactionMinDelay());
            return;
        }

        boolean hit = false;
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            if (t.isAlive() && t.contains(x, y)) {
                long reaction = System.currentTimeMillis() - t.getSpawnTime();
                stats.recordHit(reaction);
                stateText = "反应时间: " + reaction + "ms";
                targets.remove(i);
                hit = true;
                break;
            }
        }
        if (!hit && !targets.isEmpty()) {
            stats.recordMiss();
        }

        // 开始下一轮等待
        if (targets.isEmpty()) {
            startWaiting();
        }
    }

    @Override public void onMouseMove(double x, double y, GameStats stats) {}
    @Override public void onMousePress(double x, double y, GameStats stats) {}
    @Override public void onMouseRelease(double x, double y, GameStats stats) {}

    @Override
    public List<Target> getTargets() { return targets; }

    @Override
    public void reset() {
        targets.clear();
        startWaiting();
    }

    @Override
    public String getModeInfo() { return stateText; }

    private void startWaiting() {
        waiting = true;
        waitTimer = 0;
        nextDelay = config.getReactionMinDelay() +
            random.nextDouble() * (config.getReactionMaxDelay() - config.getReactionMinDelay());
        stateText = "等待中...";
    }

    private void spawnTarget() {
        int size = config.getReactionTargetSize();
        int count = config.getReactionTargetCount();
        double minDist = size * config.getTargetDensity() * 0.3;
        double worldW = config.getWorldWidth();
        double worldH = config.getWorldHeight();
        double maxZ = config.getMaxDepth();
        double zCenter = maxZ * 0.5;
        double zRange = maxZ * 0.4 * config.getZSpread();
        for (int i = 0; i < count; i++) {
            double tx, ty, tz;
            int attempts = 0;
            do {
                tx = (random.nextDouble() - 0.5) * 2 * worldW * 0.75;
                ty = (random.nextDouble() - 0.5) * 2 * worldH * 0.75;
                tz = Math.max(maxZ * 0.05, Math.min(maxZ * 0.95,
                    zCenter + (random.nextDouble() - 0.5) * 2 * zRange));
                attempts++;
            } while (isTooClose(tx, ty, tz, minDist) && attempts < 50);
            Target t = new Target(tx, ty, tz, size, new Color(255, 200, 0));
            targets.add(t);
        }
    }

    private boolean isTooClose(double x, double y, double z, double minDist) {
        for (Target t : targets) {
            if (t.distanceTo3D(x, y, z) < minDist) return true;
        }
        return false;
    }
}
