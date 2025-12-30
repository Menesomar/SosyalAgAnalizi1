package gui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ModernButton extends JButton {

    private Color normalColor;
    private Color hoverColor;

    public ModernButton(String text, Color baseColor) {
        super(text);
        this.normalColor = baseColor;
        this.hoverColor = baseColor.brighter();
        
        setContentAreaFilled(false);
        setFocusPainted(false); // Odaklanınca çıkan o çirkin çizgiyi kaldır
        setBorderPainted(false);
        setForeground(Color.WHITE);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        setCursor(new Cursor(Cursor.HAND_CURSOR)); // Üzerine gelince el işareti çıksın

        // Hover Efekti (Fare üzerine gelince renk değişsin)
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                setBackground(hoverColor);
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setBackground(normalColor);
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Arka planı yuvarlak köşeli çiz
        if (getModel().isRollover()) {
            g2.setColor(hoverColor);
        } else {
            g2.setColor(normalColor);
        }
        
        // 20px yuvarlaklık
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

        // Yazıyı çiz
        super.paintComponent(g);
        g2.dispose();
    }
}
