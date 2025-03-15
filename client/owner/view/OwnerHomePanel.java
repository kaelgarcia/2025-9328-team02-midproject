package client.owner.view;

import javax.swing.*;
import java.awt.*;

public class OwnerHomePanel extends JPanel {
    private JLabel pageTitle;
    private JPanel buttonPanel;
    private JPanel contentPanel;
    private Runnable onLogout;

    public OwnerHomePanel() {
        this.initializeUI();
    }

    private void initializeUI() {
        this.setLayout(new BorderLayout());
        this.createComponents();
        this.layoutComponents();
    }

    private void createComponents() {
        this.pageTitle = new JLabel("Owner Home", JLabel.CENTER);
        this.pageTitle.setFont(new Font("Arial", Font.BOLD, 25));

        this.contentPanel = new JPanel(new BorderLayout());
        this.buttonPanel = new JPanel();
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(this.pageTitle, BorderLayout.NORTH);

        this.add(topPanel, BorderLayout.NORTH);
        this.add(this.contentPanel, BorderLayout.CENTER);
        this.add(this.buttonPanel, BorderLayout.SOUTH);
    }

    public void showPanel(JPanel panel) {
        contentPanel.removeAll();
        contentPanel.add(panel, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void setButtonPanel(JPanel buttonPanel) {
        this.remove(this.buttonPanel);
        this.buttonPanel = buttonPanel;
        this.add(this.buttonPanel, BorderLayout.SOUTH);
        this.revalidate();
        this.repaint();
    }


    public void setOnLogout(Runnable onLogout) {
        this.onLogout = onLogout;
    }

    public void triggerLogout() {
        if (onLogout != null) {
            onLogout.run();
        } else {
            System.err.println("Error: Logout action is not set!");
        }
    }
}
