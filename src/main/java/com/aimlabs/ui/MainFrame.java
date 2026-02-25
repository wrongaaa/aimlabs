package com.aimlabs.ui;

import com.aimlabs.config.GameConfig;
import com.aimlabs.game.GameMode;

import javax.swing.*;
import java.awt.*;

/**
 * 主窗口
 */
public class MainFrame extends JFrame {
    private final GameConfig config;
    private final GamePanel gamePanel;

    public MainFrame() {
        config = new GameConfig();
        config.load();

        setTitle("AimLabs - 瞄准训练器");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(config.getWindowWidth(), config.getWindowHeight());
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // 左侧菜单
        JPanel sidebar = createSidebar();

        // 游戏面板
        gamePanel = new GamePanel(config);
        gamePanel.setOnGameEnd(() -> {
            // 游戏结束后重新启用按钮
        });

        // 布局
        setLayout(new BorderLayout());
        add(sidebar, BorderLayout.WEST);
        add(gamePanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setPreferredSize(new Dimension(180, 0));
        sidebar.setBackground(new Color(25, 25, 35));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 标题
        JLabel title = new JLabel("AimLabs");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(255, 200, 0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(title);

        JLabel subtitle = new JLabel("瞄准训练器");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitle.setForeground(new Color(150, 150, 170));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(subtitle);
        sidebar.add(Box.createVerticalStrut(20));

        // 分隔线
        JSeparator sep = new JSeparator();
        sep.setMaximumSize(new Dimension(160, 2));
        sep.setForeground(new Color(60, 60, 80));
        sidebar.add(sep);
        sidebar.add(Box.createVerticalStrut(15));

        // 模式标签
        JLabel modeLabel = new JLabel("训练模式");
        modeLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        modeLabel.setForeground(new Color(180, 180, 200));
        modeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        sidebar.add(modeLabel);
        sidebar.add(Box.createVerticalStrut(10));

        // 模式按钮
        Color[] modeColors = {
            new Color(255, 80, 80),   // Flick
            new Color(80, 180, 255),  // Track
            new Color(255, 180, 0),   // Speed
            new Color(0, 220, 120),   // Precision
            new Color(200, 100, 255), // Reaction
            new Color(255, 120, 200), // Switch
        };

        for (int i = 0; i < GameMode.values().length; i++) {
            GameMode mode = GameMode.values()[i];
            JButton btn = createModeButton(mode.getDisplayName(), modeColors[i], mode);
            sidebar.add(btn);
            sidebar.add(Box.createVerticalStrut(8));
        }

        sidebar.add(Box.createVerticalGlue());

        // 底部按钮
        JSeparator sep2 = new JSeparator();
        sep2.setMaximumSize(new Dimension(160, 2));
        sep2.setForeground(new Color(60, 60, 80));
        sidebar.add(sep2);
        sidebar.add(Box.createVerticalStrut(10));

        JButton settingsBtn = new JButton("设置");
        settingsBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        settingsBtn.setMaximumSize(new Dimension(160, 35));
        settingsBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        settingsBtn.setBackground(new Color(60, 60, 80));
        settingsBtn.setForeground(Color.WHITE);
        settingsBtn.setFocusPainted(false);
        settingsBtn.setBorderPainted(false);
        settingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        settingsBtn.addActionListener(e -> {
            if (gamePanel.isRunning()) {
                gamePanel.stopGame();
            }
            SettingsDialog dialog = new SettingsDialog(this, config);
            dialog.setVisible(true);
        });
        sidebar.add(settingsBtn);
        sidebar.add(Box.createVerticalStrut(5));

        JButton quitBtn = new JButton("退出");
        quitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        quitBtn.setMaximumSize(new Dimension(160, 35));
        quitBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        quitBtn.setBackground(new Color(80, 40, 40));
        quitBtn.setForeground(Color.WHITE);
        quitBtn.setFocusPainted(false);
        quitBtn.setBorderPainted(false);
        quitBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        quitBtn.addActionListener(e -> {
            config.save();
            System.exit(0);
        });
        sidebar.add(quitBtn);
        sidebar.add(Box.createVerticalStrut(5));

        return sidebar;
    }

    private JButton createModeButton(String text, Color accentColor, GameMode mode) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2d.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 60));
                } else {
                    g2d.setColor(new Color(45, 45, 60));
                }
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                // 左侧色条
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, 4, getHeight(), 4, 4);

                // 文字
                g2d.setColor(Color.WHITE);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                g2d.drawString(getText(), 12, (getHeight() + fm.getAscent() - fm.getDescent()) / 2);
            }
        };
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(160, 38));
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setToolTipText(mode.getDescription());
        btn.addActionListener(e -> {
            if (gamePanel.isRunning()) {
                gamePanel.stopGame();
            }
            gamePanel.startGame(mode);
        });
        return btn;
    }
}
