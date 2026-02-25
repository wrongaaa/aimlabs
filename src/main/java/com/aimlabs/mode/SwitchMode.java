package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Switch模式 - 切换：多个靶标同时存在，高亮的靶标需要按顺序点击
 */
public class SwitchMode implements ModeHandler {
    private final List<Target> targets = new ArrayList<>();
    private final Random random = new Random();
    private GameConfig config;
    private int width, height;
    private int currentIndex = 0;

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
        if (targets.isEmpty()) return;

        // 只能点击高亮的靶标
        Target highlighted = null;
        int highlightedIdx = -1;
        for (int i = 0; i < targets.size(); i++) {
            if (targets.get(i).isHighlighted()) {
                highlighted = targets.get(i);
                highlightedIdx = i;
                break;
            }
        }

        if (highlighted != null && highlighted.contains(x, y)) {
            long reaction = System.currentTimeMillis() - highlighted.getSpawnTime();
            stats.recordHit(reaction);

            // 移除被点击的靶标，重新生成一个新的
            targets.remove(highlightedIdx);
            spawnSingleTarget(false);

            // 随机选一个新的高亮
            highlightNext();
        } else {
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
        for (int i = 0; i < config.getSwitchTargetCount(); i++) {
            spawnSingleTarget(false);
        }
        if (!targets.isEmpty()) {
            highlightNext();
        }
    }

    @Override
    public String getModeInfo() {
        return "点击高亮靶标 | 靶标数: " + config.getSwitchTargetCount();
    }

    private void spawnSingleTarget(boolean highlight) {
        int size = config.getSwitchTargetSize();
        double worldW = config.getWorldWidth();
        double worldH = config.getWorldHeight();
        double maxZ = config.getMaxDepth();
        double zCenter = maxZ * 0.4;
        double zRange = maxZ * config.getZSpread();
        double tx, ty, tz;
        boolean overlapping;
        int attempts = 0;
        do {
            tx = -worldW + random.nextDouble() * (2 * worldW);
            ty = -worldH + random.nextDouble() * (2 * worldH);
            tz = Math.max(0, Math.min(maxZ, zCenter + (random.nextDouble() - 0.5) * 2 * zRange));
            overlapping = false;
            for (Target t : targets) {
                if (t.distanceTo3D(tx, ty, tz) < size * config.getTargetDensity() * 0.3) {
                    overlapping = true;
                    break;
                }
            }
            attempts++;
        } while (overlapping && attempts < 50);

        Color color = new Color(100, 100, 120);
        Target t = new Target(tx, ty, tz, size, color);
        t.setHighlighted(highlight);
        if (highlight) {
            t.setColor(new Color(255, 200, 0));
            t.resetSpawnTime();
        }
        targets.add(t);
    }

    private void highlightNext() {
        // 取消所有高亮
        for (Target t : targets) {
            t.setHighlighted(false);
            t.setColor(new Color(100, 100, 120));
        }
        // 随机选一个
        if (!targets.isEmpty()) {
            int idx = random.nextInt(targets.size());
            targets.get(idx).setHighlighted(true);
            targets.get(idx).setColor(new Color(255, 200, 0));
            targets.get(idx).resetSpawnTime();
        }
    }
}
