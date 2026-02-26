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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
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

    // FPS视角: 相机世界坐标偏移，准星固定屏幕中心
    private double cameraX, cameraY;
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
                // FPS: 点击始终在屏幕中心(准星位置)
                int cx = getWidth() / 2;
                int cy = getHeight() / 2 + 30;
                currentMode.onMousePress(cx, cy, stats);
                currentMode.onMouseClick(cx, cy, stats);
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (!running || currentMode == null) return;
                int cx = getWidth() / 2;
                int cy = getHeight() / 2 + 30;
                currentMode.onMouseRelease(cx, cy, stats);
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                updateCamera(e.getX(), e.getY());
                if (running && currentMode != null) {
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2 + 30;
                    currentMode.onMouseMove(cx, cy, stats);
                }
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                updateCamera(e.getX(), e.getY());
                if (running && currentMode != null) {
                    int cx = getWidth() / 2;
                    int cy = getHeight() / 2 + 30;
                    currentMode.onMouseMove(cx, cy, stats);
                }
                repaint();
            }
        });
    }

    private void updateCamera(int rawX, int rawY) {
        if (!running || !hasLastRaw) {
            lastRawX = rawX;
            lastRawY = rawY;
            hasLastRaw = true;
            return;
        }
        double sens = config.getSensitivity();
        // 鼠标delta转换为相机世界坐标偏移
        cameraX += (rawX - lastRawX) * sens;
        cameraY += (rawY - lastRawY) * sens;
        lastRawX = rawX;
        lastRawY = rawY;
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
        this.cameraX = 0;
        this.cameraY = 0;
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

        // 背景 - 径向渐变增强深度感
        Color bg = config.getBackgroundColor();
        g2d.setColor(bg);
        g2d.fillRect(0, 0, w, h);
        // 中心亮边缘暗的径向渐变
        double cx = w / 2.0;
        double cy = h / 2.0 + 30;
        float radius = Math.max(w, h) * 0.8f;
        Color centerGlow = new Color(
            Math.min(255, bg.getRed() + 25),
            Math.min(255, bg.getGreen() + 25),
            Math.min(255, bg.getBlue() + 35));
        Color edgeDark = new Color(
            Math.max(0, bg.getRed() - 15),
            Math.max(0, bg.getGreen() - 15),
            Math.max(0, bg.getBlue() - 10));
        RadialGradientPaint bgGrad = new RadialGradientPaint(
            new Point2D.Double(cx, cy), radius,
            new float[]{0f, 0.5f, 1f},
            new Color[]{centerGlow, bg, edgeDark});
        g2d.setPaint(bgGrad);
        g2d.fillRect(0, 0, w, h);

        // 3D透视网格
        if (config.isShowGrid()) {
            drawPerspectiveGrid(g2d, w, h);
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

            // 靶标 - 3D投影 + 深度排序
            List<Target> targets = currentMode.getTargets();
            double fov = config.getFov();
            double maxZ = config.getMaxDepth();

            // 投影所有靶标(带相机偏移)
            for (Target t : targets) {
                t.project(w, h, fov, cameraX, cameraY);
            }

            // 按深度排序(远的先画)
            List<Target> sorted = new ArrayList<>(targets);
            sorted.sort(Comparator.comparingDouble(Target::getZ).reversed());

            // 画阴影
            for (Target t : sorted) {
                drawTargetShadow(g2d, t, maxZ);
            }

            // 画靶标
            for (Target t : sorted) {
                drawTarget(g2d, t, maxZ);
            }

            // 准星 - 固定屏幕中心(FPS风格)
            if (config.isShowCrosshair()) {
                drawCrosshair(g2d, w / 2, h / 2 + 30);
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

    private void drawTargetShadow(Graphics2D g2d, Target t, double maxZ) {
        double sx = t.getScreenX();
        double sy = t.getScreenY();
        double ss = t.getScreenSize();
        float dim = t.getDepthDim(maxZ);
        // 远处阴影更大更模糊，近处更紧凑
        double depthRatio = t.getZ() / Math.max(1, maxZ);
        double shadowOffX = 3 + depthRatio * 8;
        double shadowOffY = 3 + depthRatio * 8;
        double shadowScale = 1.2 + depthRatio * 0.5;
        int alpha = (int)(60 * dim);
        double shadowSize = ss * shadowScale;
        // 模糊阴影用渐变
        RadialGradientPaint shadowPaint = new RadialGradientPaint(
            new Point2D.Double(sx + shadowOffX, sy + shadowOffY),
            (float)(shadowSize / 2 + 2),
            new float[]{0f, 0.6f, 1f},
            new Color[]{
                new Color(0, 0, 0, Math.min(255, Math.max(5, alpha))),
                new Color(0, 0, 0, Math.min(255, Math.max(2, alpha / 2))),
                new Color(0, 0, 0, 0)});
        g2d.setPaint(shadowPaint);
        g2d.fill(new Ellipse2D.Double(
            sx + shadowOffX - shadowSize/2, sy + shadowOffY - shadowSize/2,
            shadowSize, shadowSize));
    }

    private void drawTarget(Graphics2D g2d, Target t, double maxZ) {
        double sx = t.getScreenX();
        double sy = t.getScreenY();
        double ss = t.getScreenSize();
        float dim = t.getDepthDim(maxZ);
        double r = ss / 2.0;

        // 外圈光晕 (高亮靶标)
        if (t.isHighlighted()) {
            int glowAlpha = (int)(70 * dim);
            RadialGradientPaint glowPaint = new RadialGradientPaint(
                new Point2D.Double(sx, sy), (float)(r * 1.8),
                new float[]{0.4f, 0.7f, 1f},
                new Color[]{
                    new Color(255, 200, 0, Math.max(5, glowAlpha)),
                    new Color(255, 150, 0, Math.max(2, glowAlpha / 3)),
                    new Color(255, 100, 0, 0)});
            g2d.setPaint(glowPaint);
            double glowSize = ss * 1.8;
            g2d.fill(new Ellipse2D.Double(sx - glowSize/2, sy - glowSize/2, glowSize, glowSize));
        }

        // === 立体球体渲染 ===
        Color baseColor = t.getDepthColor(maxZ);
        int bR = baseColor.getRed(), bG = baseColor.getGreen(), bB = baseColor.getBlue();

        // 球体暗面颜色
        Color darkSide = new Color(
            Math.max(0, (int)(bR * 0.25)),
            Math.max(0, (int)(bG * 0.25)),
            Math.max(0, (int)(bB * 0.25)));
        // 球体亮面颜色
        Color lightSide = new Color(
            Math.min(255, (int)(bR * 1.3)),
            Math.min(255, (int)(bG * 1.3)),
            Math.min(255, (int)(bB * 1.3)));

        // 主体球体 - 径向渐变模拟光照(光源左上)
        float lightOffX = (float)(-r * 0.3);
        float lightOffY = (float)(-r * 0.3);
        RadialGradientPaint spherePaint = new RadialGradientPaint(
            new Point2D.Double(sx + lightOffX, sy + lightOffY),
            (float)(r * 1.1),
            new float[]{0f, 0.5f, 0.85f, 1f},
            new Color[]{lightSide, baseColor, darkSide,
                new Color(Math.max(0, (int)(bR*0.15)),
                           Math.max(0, (int)(bG*0.15)),
                           Math.max(0, (int)(bB*0.15)))});
        g2d.setPaint(spherePaint);
        Ellipse2D.Double shape = t.getScreenShape();
        g2d.fill(shape);

        // 高光点 (specular) - 左上方白色亮点
        if (ss > 8) {
            float specR = (float)(r * 0.35);
            double specX = sx - r * 0.28;
            double specY = sy - r * 0.28;
            int specAlpha = (int)(200 * dim);
            RadialGradientPaint specPaint = new RadialGradientPaint(
                new Point2D.Double(specX, specY), specR,
                new float[]{0f, 0.4f, 1f},
                new Color[]{
                    new Color(255, 255, 255, Math.min(255, Math.max(10, specAlpha))),
                    new Color(255, 255, 255, Math.min(255, Math.max(5, specAlpha / 3))),
                    new Color(255, 255, 255, 0)});
            g2d.setPaint(specPaint);
            g2d.fill(new Ellipse2D.Double(specX - specR, specY - specR, specR*2, specR*2));
        }

        // 边缘rim light (底部右侧微光)
        if (ss > 14 && dim > 0.3f) {
            int rimAlpha = (int)(50 * dim);
            double rimX = sx + r * 0.15;
            double rimY = sy + r * 0.15;
            float rimR = (float)(r * 0.9);
            RadialGradientPaint rimPaint = new RadialGradientPaint(
                new Point2D.Double(rimX, rimY), rimR,
                new float[]{0.7f, 0.9f, 1f},
                new Color[]{
                    new Color(255, 255, 255, 0),
                    new Color(200, 220, 255, Math.max(3, rimAlpha / 2)),
                    new Color(200, 220, 255, 0)});
            g2d.setPaint(rimPaint);
            g2d.fill(shape);
        }

        // 边框 - 细微暗边增强立体
        if (t.isHighlighted()) {
            g2d.setColor(new Color(
                (int)(255 * dim), (int)(220 * dim), (int)(50 * dim)));
        } else {
            g2d.setColor(new Color(
                Math.max(0, (int)(bR * 0.4)),
                Math.max(0, (int)(bG * 0.4)),
                Math.max(0, (int)(bB * 0.4)), (int)(180 * dim)));
        }
        float strokeW = Math.max(0.5f, 1.5f * dim);
        g2d.setStroke(new BasicStroke(strokeW));
        g2d.draw(shape);

        // 内圈靶心 (近处才显示)
        if (ss > 18 && dim > 0.4f) {
            double innerR = r * 0.22;
            int innerAlpha = (int)(100 * dim);
            RadialGradientPaint innerPaint = new RadialGradientPaint(
                new Point2D.Double(sx, sy), (float)(innerR + 1),
                new float[]{0f, 0.6f, 1f},
                new Color[]{
                    new Color(255, 255, 255, Math.min(255, Math.max(5, innerAlpha))),
                    new Color(255, 255, 255, Math.max(3, innerAlpha / 3)),
                    new Color(255, 255, 255, 0)});
            g2d.setPaint(innerPaint);
            g2d.fill(new Ellipse2D.Double(sx - innerR, sy - innerR, innerR*2, innerR*2));
        }

        // 生命周期指示器
        if (t.getLifetime() > 0) {
            long elapsed = System.currentTimeMillis() - t.getSpawnTime();
            double ratio = 1.0 - (double) elapsed / t.getLifetime();
            ratio = Math.max(0, Math.min(1, ratio));
            int arcAlpha = (int)(180 * dim);
            g2d.setColor(new Color(255, 255, 255, Math.min(255, Math.max(10, arcAlpha))));
            g2d.setStroke(new BasicStroke(Math.max(1.5f, 3 * dim)));
            int arcAngle = (int) (360 * ratio);
            g2d.drawArc((int)(sx - r - 4), (int)(sy - r - 4),
                (int)(ss + 8), (int)(ss + 8), 90, arcAngle);
        }

        g2d.setStroke(new BasicStroke(1));
    }

    private void drawPerspectiveGrid(Graphics2D g2d, int w, int h) {
        double fov = config.getFov();
        double maxZ = config.getMaxDepth();
        double cx = w / 2.0;
        double cy = h / 2.0 + 30;
        double wW = config.getWorldWidth();
        double wH = config.getWorldHeight();

        // 深度层矩形框 (从远到近，越远越小越暗)
        int layers = 12;
        for (int i = layers; i >= 0; i--) {
            double z = maxZ * i / layers;
            double scale = fov / (fov + z);
            float dim = Math.max(0.08f, 1.0f - (float)(z / maxZ));

            Color gc = config.getGridColor();
            int cr = Math.max(0, (int)(gc.getRed() * dim));
            int cg = Math.max(0, (int)(gc.getGreen() * dim));
            int cb = Math.max(0, (int)(gc.getBlue() * dim));
            g2d.setColor(new Color(cr, cg, cb));
            g2d.setStroke(new BasicStroke(i == 0 ? 1.5f : 0.8f));

            double halfW = wW * scale;
            double halfH = wH * scale;
            g2d.drawRect((int)(cx - halfW), (int)(cy - halfH), (int)(halfW*2), (int)(halfH*2));
        }

        // 汇聚线 - 从近处四角连到远处四角
        double nearScale = fov / fov;
        double farScale = fov / (fov + maxZ);
        double nW = wW * nearScale, nH = wH * nearScale;
        double fW = wW * farScale, fH = wH * farScale;

        Color gc = config.getGridColor();
        g2d.setColor(new Color(gc.getRed()/2, gc.getGreen()/2, gc.getBlue()/2));
        g2d.setStroke(new BasicStroke(0.7f));
        g2d.drawLine((int)(cx-nW),(int)(cy-nH),(int)(cx-fW),(int)(cy-fH));
        g2d.drawLine((int)(cx+nW),(int)(cy-nH),(int)(cx+fW),(int)(cy-fH));
        g2d.drawLine((int)(cx-nW),(int)(cy+nH),(int)(cx-fW),(int)(cy+fH));
        g2d.drawLine((int)(cx+nW),(int)(cy+nH),(int)(cx+fW),(int)(cy+fH));

        // 中间十字汇聚线
        g2d.drawLine((int)cx, (int)(cy-nH), (int)cx, (int)(cy-fH));
        g2d.drawLine((int)cx, (int)(cy+nH), (int)cx, (int)(cy+fH));
        g2d.drawLine((int)(cx-nW), (int)cy, (int)(cx-fW), (int)cy);
        g2d.drawLine((int)(cx+nW), (int)cy, (int)(cx+fW), (int)cy);

        // 对角汇聚线
        g2d.setColor(new Color(gc.getRed()/3, gc.getGreen()/3, gc.getBlue()/3));
        double midNW = nW/2, midNH = nH/2;
        double midFW = fW/2, midFH = fH/2;
        g2d.drawLine((int)(cx-midNW),(int)(cy-nH),(int)(cx-midFW),(int)(cy-fH));
        g2d.drawLine((int)(cx+midNW),(int)(cy-nH),(int)(cx+midFW),(int)(cy-fH));
        g2d.drawLine((int)(cx-midNW),(int)(cy+nH),(int)(cx-midFW),(int)(cy+fH));
        g2d.drawLine((int)(cx+midNW),(int)(cy+nH),(int)(cx+midFW),(int)(cy+fH));
        g2d.drawLine((int)(cx-nW),(int)(cy-midNH),(int)(cx-fW),(int)(cy-midFH));
        g2d.drawLine((int)(cx+nW),(int)(cy-midNH),(int)(cx+fW),(int)(cy-midFH));
        g2d.drawLine((int)(cx-nW),(int)(cy+midNH),(int)(cx-fW),(int)(cy+midFH));
        g2d.drawLine((int)(cx+nW),(int)(cy+midNH),(int)(cx+fW),(int)(cy+midFH));

        g2d.setStroke(new BasicStroke(1));
    }

    private void drawCrosshair(Graphics2D g2d, int mx, int my) {
        int len = config.getCrosshairSize();
        int thick = config.getCrosshairThickness();
        int gap = config.getCrosshairGap();
        int outline = config.getCrosshairOutline();
        Color color = config.getCrosshairColor();

        // CS风格准星: 4条短线段 + 中心间隙 + 可选描边 + 可选中心点
        // 描边(黑色外框让准星在任何背景上都清晰)
        if (outline > 0) {
            g2d.setColor(new Color(0, 0, 0, 200));
            g2d.setStroke(new BasicStroke(thick + outline * 2, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
            g2d.drawLine(mx - gap - len, my, mx - gap, my);
            g2d.drawLine(mx + gap, my, mx + gap + len, my);
            g2d.drawLine(mx, my - gap - len, mx, my - gap);
            g2d.drawLine(mx, my + gap, mx, my + gap + len);
        }

        // 主体线段
        g2d.setColor(color);
        g2d.setStroke(new BasicStroke(thick, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
        g2d.drawLine(mx - gap - len, my, mx - gap, my);
        g2d.drawLine(mx + gap, my, mx + gap + len, my);
        g2d.drawLine(mx, my - gap - len, mx, my - gap);
        g2d.drawLine(mx, my + gap, mx, my + gap + len);

        // 中心点
        if (config.isCrosshairDot()) {
            if (outline > 0) {
                g2d.setColor(new Color(0, 0, 0, 200));
                g2d.fillRect(mx - 1 - outline, my - 1 - outline, 2 + outline*2, 2 + outline*2);
            }
            g2d.setColor(color);
            g2d.fillRect(mx - 1, my - 1, 2, 2);
        }

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
