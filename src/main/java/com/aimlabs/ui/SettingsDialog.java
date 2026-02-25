package com.aimlabs.ui;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameMode;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.*;

/**
 * 设置面板 - 高度自定义化
 */
public class SettingsDialog extends JDialog {
    private final GameConfig config;
    private boolean saved = false;

    public SettingsDialog(JFrame parent, GameConfig config) {
        super(parent, "游戏设置", true);
        this.config = config;
        initUI();
    }

    private void initUI() {
        setSize(550, 650);
        setLocationRelativeTo(getParent());
        setResizable(false);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 13));

        tabs.addTab("通用", createGeneralPanel());
        tabs.addTab("靶标", createTargetPanel());
        tabs.addTab("准星", createCrosshairPanel());
        tabs.addTab("外观", createAppearancePanel());
        tabs.addTab("3D透视", create3DPanel());
        tabs.addTab("Flick", createFlickPanel());
        tabs.addTab("Track", createTrackPanel());
        tabs.addTab("Speed", createSpeedPanel());
        tabs.addTab("Precision", createPrecisionPanel());
        tabs.addTab("Reaction", createReactionPanel());
        tabs.addTab("Switch", createSwitchPanel());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveBtn = new JButton("保存");
        JButton cancelBtn = new JButton("取消");
        JButton resetBtn = new JButton("恢复默认");

        saveBtn.addActionListener(e -> { saved = true; config.save(); dispose(); });
        cancelBtn.addActionListener(e -> { config.load(); dispose(); });
        resetBtn.addActionListener(e -> {
            GameConfig def = new GameConfig();
            copyConfig(def, config);
            dispose();
            new SettingsDialog((JFrame) getParent(), config).setVisible(true);
        });

        bottomPanel.add(resetBtn);
        bottomPanel.add(cancelBtn);
        bottomPanel.add(saveBtn);

        setLayout(new BorderLayout());
        add(tabs, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private JPanel createGeneralPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "游戏时长(秒)", 10, 300, config.getGameDuration(), v -> config.setGameDuration(v));
        addDoubleSlider(p, "鼠标灵敏度", 0.1, 5.0, config.getSensitivity(), v -> config.setSensitivity(v));
        addSlider(p, "窗口宽度", 800, 1920, config.getWindowWidth(), v -> config.setWindowWidth(v));
        addSlider(p, "窗口高度", 600, 1080, config.getWindowHeight(), v -> config.setWindowHeight(v));
        addCheckbox(p, "全屏模式", config.isFullscreen(), v -> config.setFullscreen(v));
        addCheckbox(p, "显示分数", config.isShowScore(), v -> config.setShowScore(v));
        addCheckbox(p, "显示命中率", config.isShowAccuracy(), v -> config.setShowAccuracy(v));
        addCheckbox(p, "显示计时器", config.isShowTimer(), v -> config.setShowTimer(v));
        addCheckbox(p, "音效", config.isSoundEnabled(), v -> config.setSoundEnabled(v));
        return wrapScroll(p);
    }

    private JPanel createTargetPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "默认大小", 10, 100, config.getTargetDefaultSize(), v -> config.setTargetDefaultSize(v));
        addSlider(p, "最小大小", 5, 50, config.getTargetMinSize(), v -> config.setTargetMinSize(v));
        addSlider(p, "最大大小", 20, 150, config.getTargetMaxSize(), v -> config.setTargetMaxSize(v));
        addDoubleSlider(p, "分布密度", 1.0, 10.0, config.getTargetDensity(), v -> config.setTargetDensity(v));
        addColorPicker(p, "靶标颜色", config.getTargetColor(), c -> config.setTargetColor(c));
        addColorPicker(p, "边框颜色", config.getTargetBorderColor(), c -> config.setTargetBorderColor(c));
        addColorPicker(p, "命中颜色", config.getTargetHitColor(), c -> config.setTargetHitColor(c));
        return wrapScroll(p);
    }

    private JPanel createCrosshairPanel() {
        JPanel p = createFormPanel();
        addCheckbox(p, "显示准星", config.isShowCrosshair(), v -> config.setShowCrosshair(v));
        addSlider(p, "准星大小", 5, 50, config.getCrosshairSize(), v -> config.setCrosshairSize(v));
        addSlider(p, "准星粗细", 1, 5, config.getCrosshairThickness(), v -> config.setCrosshairThickness(v));
        addColorPicker(p, "准星颜色", config.getCrosshairColor(), c -> config.setCrosshairColor(c));
        return wrapScroll(p);
    }

    private JPanel createAppearancePanel() {
        JPanel p = createFormPanel();
        addColorPicker(p, "背景颜色", config.getBackgroundColor(), c -> config.setBackgroundColor(c));
        addColorPicker(p, "网格颜色", config.getGridColor(), c -> config.setGridColor(c));
        addCheckbox(p, "显示网格", config.isShowGrid(), v -> config.setShowGrid(v));
        return wrapScroll(p);
    }

    private JPanel create3DPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "视野(FOV)", 200, 1200, (int)config.getFov(), v -> config.setFov(v));
        addSlider(p, "最大深度", 200, 2000, (int)config.getMaxDepth(), v -> config.setMaxDepth(v));
        addSlider(p, "世界宽度", 200, 1200, (int)config.getWorldWidth(), v -> config.setWorldWidth(v));
        addSlider(p, "世界高度", 200, 800, (int)config.getWorldHeight(), v -> config.setWorldHeight(v));
        return wrapScroll(p);
    }

    private JPanel createFlickPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "同时靶标数", 1, 10, config.getFlickTargetCount(), v -> config.setFlickTargetCount(v));
        return wrapScroll(p);
    }

    private JPanel createTrackPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "靶标数量", 1, 15, config.getTrackTargetCount(), v -> config.setTrackTargetCount(v));
        addDoubleSlider(p, "移动速度", 0.5, 10.0, config.getTrackSpeed(), v -> config.setTrackSpeed(v));
        addSlider(p, "靶标大小", 20, 100, config.getTrackTargetSize(), v -> config.setTrackTargetSize(v));
        return wrapScroll(p);
    }

    private JPanel createSpeedPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "靶标数量", 1, 15, config.getSpeedTargetCount(), v -> config.setSpeedTargetCount(v));
        addDoubleSlider(p, "存活时间(秒)", 0.3, 5.0, config.getSpeedTargetLifetime(), v -> config.setSpeedTargetLifetime(v));
        addSlider(p, "靶标大小", 15, 80, config.getSpeedTargetSize(), v -> config.setSpeedTargetSize(v));
        return wrapScroll(p);
    }

    private JPanel createPrecisionPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "靶标数量", 1, 15, config.getPrecisionTargetCount(), v -> config.setPrecisionTargetCount(v));
        addSlider(p, "最小大小", 3, 20, config.getPrecisionMinSize(), v -> config.setPrecisionMinSize(v));
        addSlider(p, "最大大小", 10, 40, config.getPrecisionMaxSize(), v -> config.setPrecisionMaxSize(v));
        return wrapScroll(p);
    }

    private JPanel createReactionPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "靶标数量", 1, 15, config.getReactionTargetCount(), v -> config.setReactionTargetCount(v));
        addDoubleSlider(p, "最短延迟(秒)", 0.1, 3.0, config.getReactionMinDelay(), v -> config.setReactionMinDelay(v));
        addDoubleSlider(p, "最长延迟(秒)", 1.0, 10.0, config.getReactionMaxDelay(), v -> config.setReactionMaxDelay(v));
        addSlider(p, "靶标大小", 20, 100, config.getReactionTargetSize(), v -> config.setReactionTargetSize(v));
        return wrapScroll(p);
    }

    private JPanel createSwitchPanel() {
        JPanel p = createFormPanel();
        addSlider(p, "靶标数量", 2, 15, config.getSwitchTargetCount(), v -> config.setSwitchTargetCount(v));
        addSlider(p, "靶标大小", 15, 80, config.getSwitchTargetSize(), v -> config.setSwitchTargetSize(v));
        return wrapScroll(p);
    }

    // ====== UI Helpers ======

    private JPanel createFormPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return p;
    }

    private JPanel wrapScroll(JPanel p) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(new JScrollPane(p), BorderLayout.CENTER);
        return wrapper;
    }

    private void addSlider(JPanel parent, String label, int min, int max, int value, java.util.function.IntConsumer onChange) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(500, 50));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 25));
        JSlider slider = new JSlider(min, max, value);
        JLabel valLabel = new JLabel(String.valueOf(value));
        valLabel.setPreferredSize(new Dimension(40, 25));
        slider.addChangeListener(e -> {
            valLabel.setText(String.valueOf(slider.getValue()));
            onChange.accept(slider.getValue());
        });
        row.add(lbl, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valLabel, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(5));
    }

    private void addDoubleSlider(JPanel parent, String label, double min, double max, double value, java.util.function.DoubleConsumer onChange) {
        JPanel row = new JPanel(new BorderLayout(10, 0));
        row.setMaximumSize(new Dimension(500, 50));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 25));
        int iMin = (int)(min * 10);
        int iMax = (int)(max * 10);
        int iVal = (int)(value * 10);
        JSlider slider = new JSlider(iMin, iMax, iVal);
        JLabel valLabel = new JLabel(String.format("%.1f", value));
        valLabel.setPreferredSize(new Dimension(40, 25));
        slider.addChangeListener(e -> {
            double v = slider.getValue() / 10.0;
            valLabel.setText(String.format("%.1f", v));
            onChange.accept(v);
        });
        row.add(lbl, BorderLayout.WEST);
        row.add(slider, BorderLayout.CENTER);
        row.add(valLabel, BorderLayout.EAST);
        parent.add(row);
        parent.add(Box.createVerticalStrut(5));
    }

    private void addCheckbox(JPanel parent, String label, boolean value, java.util.function.Consumer<Boolean> onChange) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.setMaximumSize(new Dimension(500, 35));
        JCheckBox cb = new JCheckBox(label, value);
        cb.addActionListener(e -> onChange.accept(cb.isSelected()));
        row.add(cb);
        parent.add(row);
        parent.add(Box.createVerticalStrut(5));
    }

    private void addColorPicker(JPanel parent, String label, Color value, java.util.function.Consumer<Color> onChange) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        row.setMaximumSize(new Dimension(500, 40));
        JLabel lbl = new JLabel(label);
        lbl.setPreferredSize(new Dimension(120, 25));
        JButton colorBtn = new JButton();
        colorBtn.setPreferredSize(new Dimension(60, 25));
        colorBtn.setBackground(value);
        colorBtn.setOpaque(true);
        colorBtn.addActionListener(e -> {
            Color c = JColorChooser.showDialog(this, "选择 " + label, colorBtn.getBackground());
            if (c != null) {
                colorBtn.setBackground(c);
                onChange.accept(c);
            }
        });
        row.add(lbl);
        row.add(colorBtn);
        parent.add(row);
        parent.add(Box.createVerticalStrut(5));
    }

    private void copyConfig(GameConfig from, GameConfig to) {
        to.setGameDuration(from.getGameDuration());
        to.setWindowWidth(from.getWindowWidth());
        to.setWindowHeight(from.getWindowHeight());
        to.setTargetDefaultSize(from.getTargetDefaultSize());
        to.setTargetMinSize(from.getTargetMinSize());
        to.setTargetMaxSize(from.getTargetMaxSize());
        to.setTargetColor(from.getTargetColor());
        to.setCrosshairSize(from.getCrosshairSize());
        to.setCrosshairThickness(from.getCrosshairThickness());
        to.setCrosshairColor(from.getCrosshairColor());
        to.setShowCrosshair(from.isShowCrosshair());
        to.setBackgroundColor(from.getBackgroundColor());
        to.setShowGrid(from.isShowGrid());
        to.setFov(from.getFov());
        to.setMaxDepth(from.getMaxDepth());
        to.setWorldWidth(from.getWorldWidth());
        to.setWorldHeight(from.getWorldHeight());
    }

    public boolean isSaved() { return saved; }
}
