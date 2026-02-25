package com.aimlabs.game;

import java.awt.geom.Ellipse2D;
import java.awt.Color;

/**
 * 靶标对象
 */
public class Target {
    private double x, y;
    private double size;
    private double velocityX, velocityY;
    private Color color;
    private boolean alive = true;
    private long spawnTime;
    private long lifetime; // ms, 0 = infinite
    private boolean highlighted = false;

    public Target(double x, double y, double size, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;
        this.spawnTime = System.currentTimeMillis();
        this.lifetime = 0;
    }

    public Target(double x, double y, double size, Color color, long lifetimeMs) {
        this(x, y, size, color);
        this.lifetime = lifetimeMs;
    }

    public void update(double dt, int areaWidth, int areaHeight) {
        x += velocityX * dt;
        y += velocityY * dt;

        // 边界反弹
        if (x - size / 2 < 0) { x = size / 2; velocityX = Math.abs(velocityX); }
        if (x + size / 2 > areaWidth) { x = areaWidth - size / 2; velocityX = -Math.abs(velocityX); }
        if (y - size / 2 < 60) { y = 60 + size / 2; velocityY = Math.abs(velocityY); }
        if (y + size / 2 > areaHeight) { y = areaHeight - size / 2; velocityY = -Math.abs(velocityY); }

        // 生命周期检查
        if (lifetime > 0 && System.currentTimeMillis() - spawnTime > lifetime) {
            alive = false;
        }
    }

    public boolean contains(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return dx * dx + dy * dy <= (size / 2) * (size / 2);
    }

    public Ellipse2D.Double getShape() {
        return new Ellipse2D.Double(x - size / 2, y - size / 2, size, size);
    }

    public double distanceTo(double px, double py) {
        double dx = px - x;
        double dy = py - y;
        return Math.sqrt(dx * dx + dy * dy);
    }

    // Getters & Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }
    public double getVelocityX() { return velocityX; }
    public void setVelocityX(double vx) { this.velocityX = vx; }
    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double vy) { this.velocityY = vy; }
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public boolean isAlive() { return alive; }
    public void setAlive(boolean alive) { this.alive = alive; }
    public long getSpawnTime() { return spawnTime; }
    public void resetSpawnTime() { this.spawnTime = System.currentTimeMillis(); }
    public long getLifetime() { return lifetime; }
    public void setLifetime(long lifetime) { this.lifetime = lifetime; }
    public boolean isHighlighted() { return highlighted; }
    public void setHighlighted(boolean highlighted) { this.highlighted = highlighted; }
}
