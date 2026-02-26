package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Precision模式 - 精准：非常小的靶标，考验精准度
 */
public class PrecisionMode implements ModeHandler {
    private final List<Target> targets = new ArrayList<>();
    private final Random random = new Random();
    private GameConfig config;
    private int width, height;

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
    }

    @Override
    public void onMouseClick(double x, double y, GameStats stats) {
        boolean hit = false;
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            if (t.isAlive() && t.contains(x, y)) {
                long reaction = System.currentTimeMillis() - t.getSpawnTime();
                stats.recordHit(reaction);
                // 越小的靶标分数越高
                int bonus = (int) (30.0 / t.getSize() * 100);
                stats.setScore(stats.getScore() + bonus);
                targets.remove(i);
                hit = true;
                break;
            }
        }
        if (!hit) {
            stats.recordMiss();
        }
        while (targets.size() < config.getPrecisionTargetCount()) {
            spawnTarget();
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
        for (int i = 0; i < config.getPrecisionTargetCount(); i++) {
            spawnTarget();
        }
    }

    @Override
    public String getModeInfo() {
        return "精准点击 | 大小: " + config.getPrecisionMinSize() + "-" + config.getPrecisionMaxSize() + "px | 靶标: " + config.getPrecisionTargetCount();
    }

    private void spawnTarget() {
        int minSize = config.getPrecisionMinSize();
        int maxSize = config.getPrecisionMaxSize();
        int size = minSize + random.nextInt(maxSize - minSize + 1);
        double minDist = size * config.getTargetDensity() * 0.4;
        double worldW = config.getWorldWidth();
        double worldH = config.getWorldHeight();
        double maxZ = config.getMaxDepth();
        double zCenter = maxZ * 0.5;
        double zRange = maxZ * 0.4 * config.getZSpread();
        double tx, ty, tz;
        int attempts = 0;
        do {
            tx = (random.nextDouble() - 0.5) * 2 * worldW * 0.75;
            ty = (random.nextDouble() - 0.5) * 2 * worldH * 0.75;
            tz = Math.max(maxZ * 0.05, Math.min(maxZ * 0.95,
                zCenter + (random.nextDouble() - 0.5) * 2 * zRange));
            attempts++;
        } while (isTooClose(tx, ty, tz, minDist) && attempts < 50);
        float ratio = (float)(size - minSize) / Math.max(1, maxSize - minSize);
        Color color = new Color(
            (int)(255 * (1 - ratio * 0.5)),
            (int)(60 + ratio * 195),
            60
        );
        Target t = new Target(tx, ty, tz, size, color);
        targets.add(t);
    }

    private boolean isTooClose(double x, double y, double z, double minDist) {
        for (Target t : targets) {
            if (t.distanceTo3D(x, y, z) < minDist) return true;
        }
        return false;
    }
}
