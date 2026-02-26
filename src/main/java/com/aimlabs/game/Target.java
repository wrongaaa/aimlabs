package com.aimlabs.game;

import java.awt.geom.Ellipse2D;
import java.awt.Color;

/**
 * 3D靶标对象 - 支持深度(z)坐标和透视投影
 */
public class Target {
    // 3D世界坐标 (x,y为水平/垂直偏移, z为深度距离)
    private double x, y, z;
    private double size; // 世界空间中的实际大小
    private double velocityX, velocityY, velocityZ;
    private Color color;
    private boolean alive = true;
    private long spawnTime;
    private long lifetime; // ms, 0 = infinite
    private boolean highlighted = false;

    // 投影缓存
    private double screenX, screenY, screenSize;

    public Target(double x, double y, double size, Color color) {
        this(x, y, 0, size, color);
    }

    public Target(double x, double y, double z, double size, Color color) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.size = size;
        this.color = color;
        this.spawnTime = System.currentTimeMillis();
        this.lifetime = 0;
    }

    public Target(double x, double y, double size, Color color, long lifetimeMs) {
        this(x, y, 0, size, color);
        this.lifetime = lifetimeMs;
    }

    public Target(double x, double y, double z, double size, Color color, long lifetimeMs) {
        this(x, y, z, size, color);
        this.lifetime = lifetimeMs;
    }

    /**
     * 透视投影: 将3D世界坐标投影到2D屏幕
     * fov控制视野, z越大越远越小
     * camX/camY为相机世界坐标偏移(FPS视角)
     */
    public void project(int screenW, int screenH, double fov, double camX, double camY) {
        double centerX = screenW / 2.0;
        double centerY = screenH / 2.0 + 30; // 偏移HUD
        double scale = fov / (fov + z);
        screenX = centerX + (x - camX) * scale;
        screenY = centerY + (y - camY) * scale;
        screenSize = size * scale;
    }

    /** 兼容旧调用 */
    public void project(int screenW, int screenH, double fov) {
        project(screenW, screenH, fov, 0, 0);
    }

    public void update3D(double dt, double worldW, double worldH, double maxZ) {
        x += velocityX * dt;
        y += velocityY * dt;
        z += velocityZ * dt;

        // 3D边界反弹
        double halfW = worldW / 2;
        double halfH = worldH / 2;
        if (x < -halfW) { x = -halfW; velocityX = Math.abs(velocityX); }
        if (x > halfW) { x = halfW; velocityX = -Math.abs(velocityX); }
        if (y < -halfH) { y = -halfH; velocityY = Math.abs(velocityY); }
        if (y > halfH) { y = halfH; velocityY = -Math.abs(velocityY); }
        if (z < 0) { z = 0; velocityZ = Math.abs(velocityZ); }
        if (z > maxZ) { z = maxZ; velocityZ = -Math.abs(velocityZ); }

        // 生命周期
        if (lifetime > 0 && System.currentTimeMillis() - spawnTime > lifetime) {
            alive = false;
        }
    }

    // 旧的2D update保持兼容
    public void update(double dt, int areaWidth, int areaHeight) {
        update3D(dt, areaWidth, areaHeight, 0);
    }

    /** 基于投影后的屏幕坐标判定点击 */
    public boolean containsScreen(double px, double py) {
        double dx = px - screenX;
        double dy = py - screenY;
        double r = screenSize / 2;
        return dx * dx + dy * dy <= r * r;
    }

    // 旧的contains保持兼容(用投影坐标)
    public boolean contains(double px, double py) {
        return containsScreen(px, py);
    }

    public Ellipse2D.Double getScreenShape() {
        return new Ellipse2D.Double(screenX - screenSize / 2, screenY - screenSize / 2, screenSize, screenSize);
    }

    public Ellipse2D.Double getShape() {
        return getScreenShape();
    }

    /** 3D世界空间距离 */
    public double distanceTo3D(double ox, double oy, double oz) {
        double dx = x - ox, dy = y - oy, dz = z - oz;
        return Math.sqrt(dx*dx + dy*dy + dz*dz);
    }

    public double distanceTo(double px, double py) {
        double dx = x - px;
        double dy = y - py;
        return Math.sqrt(dx * dx + dy * dy);
    }

    /** 根据深度计算暗化系数 (0~1, 越远越暗) */
    public float getDepthDim(double maxZ) {
        if (maxZ <= 0) return 1.0f;
        float ratio = (float)(z / maxZ);
        // 用平方曲线让远处暗得更快
        return Math.max(0.15f, 1.0f - ratio * ratio * 0.85f);
    }

    /** 获取深度调暗后的颜色 */
    public Color getDepthColor(double maxZ) {
        float dim = getDepthDim(maxZ);
        return new Color(
            Math.max(0, Math.min(255, (int)(color.getRed() * dim))),
            Math.max(0, Math.min(255, (int)(color.getGreen() * dim))),
            Math.max(0, Math.min(255, (int)(color.getBlue() * dim)))
        );
    }

    // Getters & Setters
    public double getX() { return x; }
    public void setX(double x) { this.x = x; }
    public double getY() { return y; }
    public void setY(double y) { this.y = y; }
    public double getZ() { return z; }
    public void setZ(double z) { this.z = z; }
    public double getSize() { return size; }
    public void setSize(double size) { this.size = size; }
    public double getScreenX() { return screenX; }
    public double getScreenY() { return screenY; }
    public double getScreenSize() { return screenSize; }
    public double getVelocityX() { return velocityX; }
    public void setVelocityX(double vx) { this.velocityX = vx; }
    public double getVelocityY() { return velocityY; }
    public void setVelocityY(double vy) { this.velocityY = vy; }
    public double getVelocityZ() { return velocityZ; }
    public void setVelocityZ(double vz) { this.velocityZ = vz; }
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
