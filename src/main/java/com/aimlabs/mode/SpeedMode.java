package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Speed模式 - 速度：靶标有生命周期，需要在消失前点击
 */
public class SpeedMode implements ModeHandler {
    private final List<Target> targets = new ArrayList<>();
    private final Random random = new Random();
    private GameConfig config;
    private int width, height;
    private double spawnTimer = 0;
    private static final double SPAWN_INTERVAL = 0.8;

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

        // 生成新靶标
        spawnTimer += dt;
        if (spawnTimer >= SPAWN_INTERVAL) {
            spawnTimer = 0;
            spawnTarget();
        }

        // 更新并移除过期靶标
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            t.update(dt, width, height);
            if (!t.isAlive()) {
                targets.remove(i);
            }
        }
    }

    @Override
    public void onMouseClick(double x, double y, GameStats stats) {
        boolean hit = false;
        for (int i = targets.size() - 1; i >= 0; i--) {
            Target t = targets.get(i);
            if (t.isAlive() && t.contains(x, y)) {
                long reaction = System.currentTimeMillis() - t.getSpawnTime();
                stats.recordHit(reaction);
                // 越快点击分数越高
                double lifeRatio = 1.0 - (double) reaction / (config.getSpeedTargetLifetime() * 1000);
                int bonus = (int) (lifeRatio * 50);
                stats.setScore(stats.getScore() + bonus);
                targets.remove(i);
                hit = true;
                break;
            }
        }
        if (!hit) {
            stats.recordMiss();
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
        spawnTimer = 0;
        spawnTarget();
    }

    @Override
    public String getModeInfo() {
        return "快速点击 | 存活: " + String.format("%.1fs", config.getSpeedTargetLifetime());
    }

    private void spawnTarget() {
        int size = config.getSpeedTargetSize();
        double tx = size / 2.0 + random.nextDouble() * (width - size);
        double ty = 80 + random.nextDouble() * (height - size - 80);
        long lifetime = (long) (config.getSpeedTargetLifetime() * 1000);
        Target t = new Target(tx, ty, size, config.getTargetColor(), lifetime);
        targets.add(t);
    }
}
