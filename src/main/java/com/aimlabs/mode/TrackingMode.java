package com.aimlabs.mode;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Tracking模式 - 追踪：靶标持续移动，按住鼠标追踪靶标
 */
public class TrackingMode implements ModeHandler {
    private final List<Target> targets = new ArrayList<>();
    private final Random random = new Random();
    private GameConfig config;
    private int width, height;
    private boolean mouseDown = false;
    private double mouseX, mouseY;

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
        for (Target t : targets) {
            t.update(dt * config.getTrackSpeed() * 60, width, height);
        }
    }

    @Override
    public void onMouseClick(double x, double y, GameStats stats) {}

    @Override
    public void onMouseMove(double x, double y, GameStats stats) {
        mouseX = x;
        mouseY = y;
        if (mouseDown) {
            boolean onTarget = false;
            for (Target t : targets) {
                if (t.contains(x, y)) {
                    onTarget = true;
                    break;
                }
            }
            stats.addTrackTime(0.016, onTarget);
            if (onTarget) stats.setScore(stats.getScore() + 1);
        }
    }

    @Override
    public void onMousePress(double x, double y, GameStats stats) {
        mouseDown = true;
        mouseX = x;
        mouseY = y;
    }

    @Override
    public void onMouseRelease(double x, double y, GameStats stats) {
        mouseDown = false;
    }

    @Override
    public List<Target> getTargets() { return targets; }

    @Override
    public void reset() {
        targets.clear();
        mouseDown = false;
        int size = config.getTrackTargetSize();
        double tx = width / 2.0;
        double ty = height / 2.0;
        Target t = new Target(tx, ty, size, config.getTargetColor());
        double angle = random.nextDouble() * Math.PI * 2;
        double speed = 2 + random.nextDouble() * 2;
        t.setVelocityX(Math.cos(angle) * speed);
        t.setVelocityY(Math.sin(angle) * speed);
        targets.add(t);
    }

    @Override
    public String getModeInfo() {
        return "按住鼠标追踪靶标 | 速度: " + String.format("%.1f", config.getTrackSpeed());
    }

    public boolean isMouseDown() { return mouseDown; }
    public double getMouseX() { return mouseX; }
    public double getMouseY() { return mouseY; }
}
