package com.aimlabs.config;

import java.awt.Color;
import java.io.*;
import java.util.Properties;

/**
 * 高度自定义的游戏配置
 */
public class GameConfig implements Serializable {
    private static final String CONFIG_FILE = "aimlabs_config.properties";

    // 窗口设置
    private int windowWidth = 1200;
    private int windowHeight = 800;
    private boolean fullscreen = false;

    // 靶标设置
    private int targetMinSize = 20;
    private int targetMaxSize = 60;
    private int targetDefaultSize = 40;
    private Color targetColor = new Color(255, 60, 60);
    private Color targetBorderColor = new Color(200, 40, 40);
    private Color targetHitColor = new Color(60, 255, 60);

    // 准星设置
    private int crosshairSize = 20;
    private int crosshairThickness = 2;
    private Color crosshairColor = Color.WHITE;
    private boolean showCrosshair = true;

    // 背景设置
    private Color backgroundColor = new Color(30, 30, 40);
    private Color gridColor = new Color(50, 50, 65);
    private boolean showGrid = true;

    // 游戏时间设置（秒）
    private int gameDuration = 60;

    // 全局靶标分布密度 (1=紧密 10=稀疏，控制靶标间最小距离倍率)
    private double targetDensity = 5.0;

    // 3D透视设置
    private double fov = 280.0;        // 视野距离(越大越平，越小透视越强)
    private double maxDepth = 1200.0;  // 最大深度
    private double worldWidth = 800.0; // 3D世界宽度(半宽)
    private double worldHeight = 500.0;// 3D世界高度(半高)

    // 各模式专属设置
    // Flick
    private int flickTargetCount = 6;
    private double flickSpawnDelay = 0;

    // Track
    private double trackSpeed = 3.0;
    private int trackTargetSize = 50;
    private int trackTargetCount = 6;

    // Speed
    private double speedTargetLifetime = 1.0;
    private int speedTargetSize = 35;
    private int speedTargetCount = 6;

    // Precision
    private int precisionMinSize = 8;
    private int precisionMaxSize = 15;
    private int precisionTargetCount = 6;

    // Reaction
    private double reactionMinDelay = 0.5;
    private double reactionMaxDelay = 3.0;
    private int reactionTargetSize = 50;
    private int reactionTargetCount = 6;

    // Switch
    private int switchTargetCount = 6;
    private int switchTargetSize = 35;

    // 灵敏度 (鼠标倍率)
    private double sensitivity = 1.0;

    // 音效
    private boolean soundEnabled = true;
    private float soundVolume = 0.7f;

    // 统计显示
    private boolean showAccuracy = true;
    private boolean showScore = true;
    private boolean showTimer = true;

    public GameConfig() {}

    // ====== Getters and Setters ======

    public int getWindowWidth() { return windowWidth; }
    public void setWindowWidth(int v) { this.windowWidth = v; }
    public int getWindowHeight() { return windowHeight; }
    public void setWindowHeight(int v) { this.windowHeight = v; }
    public boolean isFullscreen() { return fullscreen; }
    public void setFullscreen(boolean v) { this.fullscreen = v; }

    public int getTargetMinSize() { return targetMinSize; }
    public void setTargetMinSize(int v) { this.targetMinSize = v; }
    public int getTargetMaxSize() { return targetMaxSize; }
    public void setTargetMaxSize(int v) { this.targetMaxSize = v; }
    public int getTargetDefaultSize() { return targetDefaultSize; }
    public void setTargetDefaultSize(int v) { this.targetDefaultSize = v; }
    public Color getTargetColor() { return targetColor; }
    public void setTargetColor(Color v) { this.targetColor = v; }
    public Color getTargetBorderColor() { return targetBorderColor; }
    public void setTargetBorderColor(Color v) { this.targetBorderColor = v; }
    public Color getTargetHitColor() { return targetHitColor; }
    public void setTargetHitColor(Color v) { this.targetHitColor = v; }

    public int getCrosshairSize() { return crosshairSize; }
    public void setCrosshairSize(int v) { this.crosshairSize = v; }
    public int getCrosshairThickness() { return crosshairThickness; }
    public void setCrosshairThickness(int v) { this.crosshairThickness = v; }
    public Color getCrosshairColor() { return crosshairColor; }
    public void setCrosshairColor(Color v) { this.crosshairColor = v; }
    public boolean isShowCrosshair() { return showCrosshair; }
    public void setShowCrosshair(boolean v) { this.showCrosshair = v; }

    public Color getBackgroundColor() { return backgroundColor; }
    public void setBackgroundColor(Color v) { this.backgroundColor = v; }
    public Color getGridColor() { return gridColor; }
    public void setGridColor(Color v) { this.gridColor = v; }
    public boolean isShowGrid() { return showGrid; }
    public void setShowGrid(boolean v) { this.showGrid = v; }

    public int getGameDuration() { return gameDuration; }
    public void setGameDuration(int v) { this.gameDuration = v; }

    public double getTargetDensity() { return targetDensity; }
    public void setTargetDensity(double v) { this.targetDensity = v; }

    public double getFov() { return fov; }
    public void setFov(double v) { this.fov = v; }
    public double getMaxDepth() { return maxDepth; }
    public void setMaxDepth(double v) { this.maxDepth = v; }
    public double getWorldWidth() { return worldWidth; }
    public void setWorldWidth(double v) { this.worldWidth = v; }
    public double getWorldHeight() { return worldHeight; }
    public void setWorldHeight(double v) { this.worldHeight = v; }

    public int getFlickTargetCount() { return flickTargetCount; }
    public void setFlickTargetCount(int v) { this.flickTargetCount = v; }
    public double getFlickSpawnDelay() { return flickSpawnDelay; }
    public void setFlickSpawnDelay(double v) { this.flickSpawnDelay = v; }

    public double getTrackSpeed() { return trackSpeed; }
    public void setTrackSpeed(double v) { this.trackSpeed = v; }
    public int getTrackTargetSize() { return trackTargetSize; }
    public void setTrackTargetSize(int v) { this.trackTargetSize = v; }
    public int getTrackTargetCount() { return trackTargetCount; }
    public void setTrackTargetCount(int v) { this.trackTargetCount = v; }

    public double getSpeedTargetLifetime() { return speedTargetLifetime; }
    public void setSpeedTargetLifetime(double v) { this.speedTargetLifetime = v; }
    public int getSpeedTargetSize() { return speedTargetSize; }
    public void setSpeedTargetSize(int v) { this.speedTargetSize = v; }
    public int getSpeedTargetCount() { return speedTargetCount; }
    public void setSpeedTargetCount(int v) { this.speedTargetCount = v; }

    public int getPrecisionMinSize() { return precisionMinSize; }
    public void setPrecisionMinSize(int v) { this.precisionMinSize = v; }
    public int getPrecisionMaxSize() { return precisionMaxSize; }
    public void setPrecisionMaxSize(int v) { this.precisionMaxSize = v; }
    public int getPrecisionTargetCount() { return precisionTargetCount; }
    public void setPrecisionTargetCount(int v) { this.precisionTargetCount = v; }

    public double getReactionMinDelay() { return reactionMinDelay; }
    public void setReactionMinDelay(double v) { this.reactionMinDelay = v; }
    public double getReactionMaxDelay() { return reactionMaxDelay; }
    public void setReactionMaxDelay(double v) { this.reactionMaxDelay = v; }
    public int getReactionTargetSize() { return reactionTargetSize; }
    public void setReactionTargetSize(int v) { this.reactionTargetSize = v; }
    public int getReactionTargetCount() { return reactionTargetCount; }
    public void setReactionTargetCount(int v) { this.reactionTargetCount = v; }

    public int getSwitchTargetCount() { return switchTargetCount; }
    public void setSwitchTargetCount(int v) { this.switchTargetCount = v; }
    public int getSwitchTargetSize() { return switchTargetSize; }
    public void setSwitchTargetSize(int v) { this.switchTargetSize = v; }

    public double getSensitivity() { return sensitivity; }
    public void setSensitivity(double v) { this.sensitivity = v; }

    public boolean isSoundEnabled() { return soundEnabled; }
    public void setSoundEnabled(boolean v) { this.soundEnabled = v; }
    public float getSoundVolume() { return soundVolume; }
    public void setSoundVolume(float v) { this.soundVolume = v; }

    public boolean isShowAccuracy() { return showAccuracy; }
    public void setShowAccuracy(boolean v) { this.showAccuracy = v; }
    public boolean isShowScore() { return showScore; }
    public void setShowScore(boolean v) { this.showScore = v; }
    public boolean isShowTimer() { return showTimer; }
    public void setShowTimer(boolean v) { this.showTimer = v; }

    // ====== Save / Load ======

    public void save() {
        Properties props = new Properties();
        props.setProperty("windowWidth", String.valueOf(windowWidth));
        props.setProperty("windowHeight", String.valueOf(windowHeight));
        props.setProperty("fullscreen", String.valueOf(fullscreen));
        props.setProperty("targetMinSize", String.valueOf(targetMinSize));
        props.setProperty("targetMaxSize", String.valueOf(targetMaxSize));
        props.setProperty("targetDefaultSize", String.valueOf(targetDefaultSize));
        props.setProperty("targetColor", colorToHex(targetColor));
        props.setProperty("targetBorderColor", colorToHex(targetBorderColor));
        props.setProperty("targetHitColor", colorToHex(targetHitColor));
        props.setProperty("crosshairSize", String.valueOf(crosshairSize));
        props.setProperty("crosshairThickness", String.valueOf(crosshairThickness));
        props.setProperty("crosshairColor", colorToHex(crosshairColor));
        props.setProperty("showCrosshair", String.valueOf(showCrosshair));
        props.setProperty("backgroundColor", colorToHex(backgroundColor));
        props.setProperty("gridColor", colorToHex(gridColor));
        props.setProperty("showGrid", String.valueOf(showGrid));
        props.setProperty("gameDuration", String.valueOf(gameDuration));
        props.setProperty("targetDensity", String.valueOf(targetDensity));
        props.setProperty("flickTargetCount", String.valueOf(flickTargetCount));
        props.setProperty("flickSpawnDelay", String.valueOf(flickSpawnDelay));
        props.setProperty("trackSpeed", String.valueOf(trackSpeed));
        props.setProperty("trackTargetSize", String.valueOf(trackTargetSize));
        props.setProperty("trackTargetCount", String.valueOf(trackTargetCount));
        props.setProperty("speedTargetLifetime", String.valueOf(speedTargetLifetime));
        props.setProperty("speedTargetSize", String.valueOf(speedTargetSize));
        props.setProperty("speedTargetCount", String.valueOf(speedTargetCount));
        props.setProperty("precisionMinSize", String.valueOf(precisionMinSize));
        props.setProperty("precisionMaxSize", String.valueOf(precisionMaxSize));
        props.setProperty("precisionTargetCount", String.valueOf(precisionTargetCount));
        props.setProperty("reactionMinDelay", String.valueOf(reactionMinDelay));
        props.setProperty("reactionMaxDelay", String.valueOf(reactionMaxDelay));
        props.setProperty("reactionTargetSize", String.valueOf(reactionTargetSize));
        props.setProperty("reactionTargetCount", String.valueOf(reactionTargetCount));
        props.setProperty("switchTargetCount", String.valueOf(switchTargetCount));
        props.setProperty("switchTargetSize", String.valueOf(switchTargetSize));
        props.setProperty("sensitivity", String.valueOf(sensitivity));
        props.setProperty("fov", String.valueOf(fov));
        props.setProperty("maxDepth", String.valueOf(maxDepth));
        props.setProperty("worldWidth", String.valueOf(worldWidth));
        props.setProperty("worldHeight", String.valueOf(worldHeight));
        props.setProperty("soundEnabled", String.valueOf(soundEnabled));
        props.setProperty("soundVolume", String.valueOf(soundVolume));
        props.setProperty("showAccuracy", String.valueOf(showAccuracy));
        props.setProperty("showScore", String.valueOf(showScore));
        props.setProperty("showTimer", String.valueOf(showTimer));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            props.store(fos, "AimLabs Configuration");
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }

    public void load() {
        File file = new File(CONFIG_FILE);
        if (!file.exists()) return;

        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
            windowWidth = Integer.parseInt(props.getProperty("windowWidth", "1200"));
            windowHeight = Integer.parseInt(props.getProperty("windowHeight", "800"));
            fullscreen = Boolean.parseBoolean(props.getProperty("fullscreen", "false"));
            targetMinSize = Integer.parseInt(props.getProperty("targetMinSize", "20"));
            targetMaxSize = Integer.parseInt(props.getProperty("targetMaxSize", "60"));
            targetDefaultSize = Integer.parseInt(props.getProperty("targetDefaultSize", "40"));
            targetColor = hexToColor(props.getProperty("targetColor", "#FF3C3C"));
            targetBorderColor = hexToColor(props.getProperty("targetBorderColor", "#C82828"));
            targetHitColor = hexToColor(props.getProperty("targetHitColor", "#3CFF3C"));
            crosshairSize = Integer.parseInt(props.getProperty("crosshairSize", "20"));
            crosshairThickness = Integer.parseInt(props.getProperty("crosshairThickness", "2"));
            crosshairColor = hexToColor(props.getProperty("crosshairColor", "#FFFFFF"));
            showCrosshair = Boolean.parseBoolean(props.getProperty("showCrosshair", "true"));
            backgroundColor = hexToColor(props.getProperty("backgroundColor", "#1E1E28"));
            gridColor = hexToColor(props.getProperty("gridColor", "#323241"));
            showGrid = Boolean.parseBoolean(props.getProperty("showGrid", "true"));
            gameDuration = Integer.parseInt(props.getProperty("gameDuration", "60"));
            targetDensity = Double.parseDouble(props.getProperty("targetDensity", "5.0"));
            flickTargetCount = Integer.parseInt(props.getProperty("flickTargetCount", "6"));
            flickSpawnDelay = Double.parseDouble(props.getProperty("flickSpawnDelay", "0"));
            trackSpeed = Double.parseDouble(props.getProperty("trackSpeed", "3.0"));
            trackTargetSize = Integer.parseInt(props.getProperty("trackTargetSize", "50"));
            trackTargetCount = Integer.parseInt(props.getProperty("trackTargetCount", "6"));
            speedTargetLifetime = Double.parseDouble(props.getProperty("speedTargetLifetime", "1.0"));
            speedTargetSize = Integer.parseInt(props.getProperty("speedTargetSize", "35"));
            speedTargetCount = Integer.parseInt(props.getProperty("speedTargetCount", "6"));
            precisionMinSize = Integer.parseInt(props.getProperty("precisionMinSize", "8"));
            precisionMaxSize = Integer.parseInt(props.getProperty("precisionMaxSize", "15"));
            precisionTargetCount = Integer.parseInt(props.getProperty("precisionTargetCount", "6"));
            reactionMinDelay = Double.parseDouble(props.getProperty("reactionMinDelay", "0.5"));
            reactionMaxDelay = Double.parseDouble(props.getProperty("reactionMaxDelay", "3.0"));
            reactionTargetSize = Integer.parseInt(props.getProperty("reactionTargetSize", "50"));
            reactionTargetCount = Integer.parseInt(props.getProperty("reactionTargetCount", "6"));
            switchTargetCount = Integer.parseInt(props.getProperty("switchTargetCount", "6"));
            switchTargetSize = Integer.parseInt(props.getProperty("switchTargetSize", "35"));
            sensitivity = Double.parseDouble(props.getProperty("sensitivity", "1.0"));
            fov = Double.parseDouble(props.getProperty("fov", "280.0"));
            maxDepth = Double.parseDouble(props.getProperty("maxDepth", "1200.0"));
            worldWidth = Double.parseDouble(props.getProperty("worldWidth", "800.0"));
            worldHeight = Double.parseDouble(props.getProperty("worldHeight", "500.0"));
            soundEnabled = Boolean.parseBoolean(props.getProperty("soundEnabled", "true"));
            soundVolume = Float.parseFloat(props.getProperty("soundVolume", "0.7"));
            showAccuracy = Boolean.parseBoolean(props.getProperty("showAccuracy", "true"));
            showScore = Boolean.parseBoolean(props.getProperty("showScore", "true"));
            showTimer = Boolean.parseBoolean(props.getProperty("showTimer", "true"));
        } catch (IOException e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }

    private String colorToHex(Color c) {
        return String.format("#%02X%02X%02X", c.getRed(), c.getGreen(), c.getBlue());
    }

    private Color hexToColor(String hex) {
        return Color.decode(hex);
    }
}
