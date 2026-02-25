package com.aimlabs.ui;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameMode;
import com.aimlabs.game.GameStats;
import com.aimlabs.game.Target;
import com.aimlabs.mode.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 游戏主面板 - 渲染和交互
 */
public class GamePanel extends JPanel implements ActionListener {
    private final GameConfig config;
    private final GameStats stats;
    private ModeHandler currentMode;
    private GameMode currentGameMode;
    private final Timer gameTimer;
    private final Timer countdownTimer;
    private boolean running = false;
    private int timeRemaining;
    private long lastUpdateTime;
    private Runnable onGameEnd;

    // 虚拟光标 (灵敏度控制)
    private double virtualX, virtualY;
    private int lastRawX, lastRawY;
    private boolean hasLastRaw = false;

    public GamePanel(GameConfig config) {
        this.config = config;
        this.stats = new GameStats();
        this.gameTimer = new Timer(16, this); // ~60 FPS
        this.countdownTimer = new Timer(1000, e -> {
            if (timeRemaining > 0) {
                timeRemaining--;
            } else {
                stopGame();
            }
        });

        setBackground(config.getBackgroundColor());
        setFocusable(true);
        setCursor(createBlankCursor());

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (!running || currentMode == null) return;
                int vx = (int) virtualX;
                int vy = (int) virtualY;
                currentMode.onMousePress(vx, vy, stats);
                currentMode.onMouseClick(vx, vy, stats);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!running || currentMode == null) return;
                currentMode.onMouseRelease((int) virtualX, (int) virtualY, stats);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateVirtualCursor(e.getX(), e.getY());
                if (running && currentMode != null) {
                    currentMode.onMouseMove((int) virtualX, (int) virtualY, stats);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateVirtualCursor(e.getX(), e.getY());
                if (running && currentMode != null) {
                    currentMode.onMouseMove((int) virtualX, (int) virtualY, stats);
                }
                repaint();
            }
        });
    }

    private int mouseX, mouseY;

    private void updateVirtualCursor(int rawX, int rawY) {
        if (!running || !hasLastRaw) {
            // 非游戏状态或首次移动，直接同步
            virtualX = rawX;
            virtualY = rawY;
            lastRawX = rawX;
            lastRawY = rawY;
            hasLastRaw = true;
            mouseX = (int) virtualX;
            mouseY = (int) virtualY;
            return;
        }
        double sens = config.getSensitivity();
        double dx = (rawX - lastRawX) * sens;
        double dy = (rawY - lastRawY) * sens;
        virtualX = Math.max(0, Math.min(getWidth(), virtualX + dx));
        virtualY = Math.max(0, Math.min(getHeight(), virtualY + dy));
        lastRawX = rawX;
        lastRawY = rawY;
        mouseX = (int) virtualX;
        mouseY = (int) virtualY;
    }

    private Cursor createBlankCursor() {
        if (config.isShowCrosshair()) {
            BufferedImage cursorImg = new java.awt.image.BufferedImage(16, 16, java.awt.image.BufferedImage.TYPE_INT_ARGB);
            return Toolkit.getDefaultToolkit().createCustomCursor(cursorImg, new Point(0, 0), "blank");
        }
        return Cursor.getDefaultCursor();
    }

    public void startGame(GameMode mode) {
        this.currentGameMode = mode;
        this.currentMode = createModeHandler(mode);
        this.currentMode.init(getWidth(), getHeight(), config);
        this.stats.reset();
        this.timeRemaining = config.getGameDuration();
        this.running = true;
        this.lastUpdateTime = System.nanoTime();
        this.hasLastRaw = false;
        this.virtualX = getWidth() / 2.0;
        this.virtualY = getHeight() / 2.0;
        setCursor(createBlankCursor());
        gameTimer.start();
        countdownTimer.start();
        requestFocusInWindow();
    }

    public void stopGame() {
        running = false;
        gameTimer.stop();
        countdownTimer.stop();
        setCursor(Cursor.getDefaultCursor());
        if (onGameEnd != null) onGameEnd.run();
        repaint();
    }

    public void setOnGameEnd(Runnable callback) {
        this.onGameEnd = callback;
    }

    private ModeHandler createModeHandler(GameMode mode) {
        return switch (mode) {
            case FLICK -> new FlickMode();
            case TRACKING -> new TrackingMode();
            case SPEED -> new SpeedMode();
            case PRECISION -> new PrecisionMode();
            case REACTION -> new ReactionMode();
            case SWITCH -> new SwitchMode();
        };
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running || currentMode == null) return;
        long now = System.nanoTime();
        double dt = (now - lastUpdateTime) / 1_000_000_000.0;
        lastUpdateTime = now;
        currentMode.update(dt, getWidth(), getHeight());
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth();
        int h = getHeight();

        // 背景
        g2d.setColor(config.getBackgroundColor());
        g2d.fillRect(0, 0, w, h);

        // 网格
        if (config.isShowGrid()) {
            g2d.setColor(config.getGridColor());
            for (int x = 0; x < w; x += 50) g2d.drawLine(x, 60, x, h);
            for (int y = 60; y < h; y += 50) g2d.drawLine(0, y, w, y);
        }

        // HUD 栏
        g2d.setColor(new Color(20, 20, 30));
        g2d.fillRect(0, 0, w, 55);
        g2d.setColor(new Color(60, 60, 80));
        g2d.drawLine(0, 55, w, 55);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 16));

        if (running && currentMode != null) {
            // 模式信息
            g2d.setColor(new Color(180, 180, 200));
            g2d.drawString(currentGameMode.getDisplayName() + "  |  " + currentMode.getModeInfo(), 15, 25);

            // 分数
            if (config.isShowScore()) {
                g2d.setColor(new Color(255, 200, 0));
                String scoreText = "分数: " + stats.getScore();
                g2d.drawString(scoreText, 15, 47);
            }

            // 命中率
            if (config.isShowAccuracy()) {
                g2d.setColor(new Color(100, 255, 100));
                String accText;
                if (currentGameMode == GameMode.TRACKING) {
                    accText = String.format("追踪精度: %.1f%%", stats.getTrackAccuracy());
                } else {
                    accText = String.format("命中率: %.1f%%", stats.getAccuracy());
                }
                g2d.drawString(accText, 200, 47);
            }

            // 计时器
            if (config.isShowTimer()) {
                g2d.setColor(timeRemaining <= 10 ? new Color(255, 80, 80) : Color.WHITE);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 22));
                String timeText = String.format("%d:%02d", timeRemaining / 60, timeRemaining % 60);
                int tw = g2d.getFontMetrics().stringWidth(timeText);
                g2d.drawString(timeText, w - tw - 20, 38);
            }

            // 靶标
            List<Target> targets = currentMode.getTargets();
            for (Target t : targets) {
                drawTarget(g2d, t);
            }

            // 准星
            if (config.isShowCrosshair()) {
                drawCrosshair(g2d, mouseX, mouseY);
            }
        } else if (!running && stats.getTotalShots() > 0) {
            // 结算画面
            drawResults(g2d, w, h);
        } else {
            g2d.setColor(new Color(180, 180, 200));
            g2d.setFont(new Font("SansSerif", Font.PLAIN, 18));
            String hint = "选择一个模式开始训练";
            int tw = g2d.getFontMetrics().stringWidth(hint);
            g2d.drawString(hint, (w - tw) / 2, h / 2);
        }
    }

    private void drawTarget(Graphics2D g2d, Target t) {
        double x = t.getX();
        double y = t.getY();
        double size = t.getSize();

        // 外圈光晕
        if (t.isHighlighted()) {
            g2d.setColor(new Color(255, 200, 0, 50));
            g2d.fill(new Ellipse2D.Double(x - size * 0.8, y - size * 0.8, size * 1.6, size * 1.6));
        }

        // 主体
        g2d.setColor(t.getColor());
        g2d.fill(t.getShape());

        // 边框
        Color borderColor = t.isHighlighted() ? new Color(255, 255, 100) : config.getTargetBorderColor();
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(2));
        g2d.draw(t.getShape());

        // 内圈
        if (size > 15) {
            g2d.setColor(new Color(255, 255, 255, 80));
            double innerSize = size * 0.4;
            g2d.fill(new Ellipse2D.Double(x - innerSize / 2, y - innerSize / 2, innerSize, innerSize));
        }

        // 生命周期指示器
        if (t.getLifetime() > 0) {
            long elapsed = System.currentTimeMillis() - t.getSpawnTime();
            double ratio = 1.0 - (double) elapsed / t.getLifetime();
            ratio = Math.max(0, Math.min(1, ratio));
            g2d.setColor(new Color(255, 255, 255, 150));
            g2d.setStroke(new BasicStroke(3));
            int arcAngle = (int) (360 * ratio);
            g2d.drawArc((int)(x - size/2 - 3), (int)(y - size/2 - 3),
                (int)(size + 6), (int)(size + 6), 90, arcAngle);
        }

        g2d.setStroke(new BasicStroke(1));
    }

    private void drawCrosshair(Graphics2D g2d, int mx, int my) {
        int size = config.getCrosshairSize();
        int thick = config.getCrosshairThickness();
        g2d.setColor(config.getCrosshairColor());
        g2d.setStroke(new BasicStroke(thick));
        // 十字线（中间留空）
        int gap = 4;
        g2d.drawLine(mx - size, my, mx - gap, my);
        g2d.drawLine(mx + gap, my, mx + size, my);
        g2d.drawLine(mx, my - size, mx, my - gap);
        g2d.drawLine(mx, my + gap, mx, my + size);
        // 中心点
        g2d.fillOval(mx - 1, my - 1, 2, 2);
        g2d.setStroke(new BasicStroke(1));
    }

    private void drawResults(Graphics2D g2d, int w, int h) {
        // 半透明背景
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 55, w, h - 55);

        int cx = w / 2;
        int startY = 120;

        // 标题
        g2d.setFont(new Font("SansSerif", Font.BOLD, 32));
        g2d.setColor(new Color(255, 200, 0));
        String title = "训练结束";
        g2d.drawString(title, cx - g2d.getFontMetrics().stringWidth(title) / 2, startY);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 20));
        startY += 50;

        // 模式
        drawResultLine(g2d, cx, startY, "模式", currentGameMode.getDisplayName());
        startY += 35;

        // 分数
        g2d.setColor(new Color(255, 200, 0));
        drawResultLine(g2d, cx, startY, "最终分数", String.valueOf(stats.getScore()));
        startY += 35;

        // 命中率
        if (currentGameMode == GameMode.TRACKING) {
            g2d.setColor(new Color(100, 255, 100));
            drawResultLine(g2d, cx, startY, "追踪精度", String.format("%.1f%%", stats.getTrackAccuracy()));
        } else {
            g2d.setColor(new Color(100, 255, 100));
            drawResultLine(g2d, cx, startY, "命中率", String.format("%.1f%% (%d/%d)", stats.getAccuracy(), stats.getHits(), stats.getTotalShots()));
        }
        startY += 35;

        // 平均反应
        if (stats.getAverageReactionTime() > 0) {
            g2d.setColor(new Color(100, 200, 255));
            drawResultLine(g2d, cx, startY, "平均反应", stats.getAverageReactionTime() + "ms");
            startY += 35;
        }

        // 最佳反应
        if (stats.getBestReactionTime() > 0) {
            g2d.setColor(new Color(200, 100, 255));
            drawResultLine(g2d, cx, startY, "最快反应", stats.getBestReactionTime() + "ms");
            startY += 35;
        }

        // 提示
        startY += 20;
        g2d.setFont(new Font("SansSerif", Font.ITALIC, 16));
        g2d.setColor(new Color(150, 150, 170));
        String hint = "点击左侧模式按钮开始新训练";
        g2d.drawString(hint, cx - g2d.getFontMetrics().stringWidth(hint) / 2, startY);
    }

    private void drawResultLine(Graphics2D g2d, int cx, int y, String label, String value) {
        String line = label + ": " + value;
        int tw = g2d.getFontMetrics().stringWidth(line);
        g2d.drawString(line, cx - tw / 2, y);
    }

    public boolean isRunning() { return running; }
    public GameStats getStats() { return stats; }
    public GameMode getCurrentGameMode() { return currentGameMode; }
}
