package client.registration.view;

import client.customer.controller.CustomerHomeController;
import client.customer.view.CustomerHomePage;
import client.owner.controller.OwnerHomeController;
import client.owner.model.OwnerHomeButtonPanel;
import client.owner.view.OwnerHomePanel;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class RegistrationUIManager {

    public static void openOwnerHomePanel(ILoginView loginView) {
        loginView.setVisible(false);
        OwnerHomePanel ownerPanel = new OwnerHomePanel();
        OwnerHomeButtonPanel buttonPanel = new OwnerHomeButtonPanel(ownerPanel);
        new OwnerHomeController(ownerPanel, buttonPanel);
        ownerPanel.setOnLogout(() -> {
            ownerPanel.setVisible(false);
            loginView.setVisible(true);
        });
        JFrame frame = new JFrame("Owner Panel");
        frame.setContentPane(ownerPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void openCustomerHomePage(ILoginView loginView, String username) {
        String userFilePath = "userData/" + username + ".xml";
        loginView.setVisible(false);
        File userFile = new File(userFilePath);
        if (!userFile.exists()) {
            JOptionPane.showMessageDialog(null, "User file not found: " + userFilePath, "Error", JOptionPane.ERROR_MESSAGE);
            return; // Exit the method if the file does not exist
        }
        CustomerHomePage homePage = new CustomerHomePage(username, userFile);

        new CustomerHomeController(homePage, username, userFilePath);
        homePage.setVisible(true);
    }
}
