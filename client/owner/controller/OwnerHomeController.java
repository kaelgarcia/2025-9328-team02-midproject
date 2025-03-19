package client.owner.controller;

import client.owner.view.OwnerHomePanel;
import client.owner.model.OwnerHomeButtonPanel;
import client.owner.model.OwnerCalendarPanel;

import java.rmi.RemoteException;

public class OwnerHomeController {
    private OwnerHomePanel view;
    private OwnerHomeButtonPanel buttonPanel;

    public OwnerHomeController(OwnerHomePanel view, OwnerHomeButtonPanel buttonPanel) {
        this.view = view;
        this.buttonPanel = buttonPanel;

        this.view.setButtonPanel(buttonPanel);
        this.bindButtons();
    }

    private void bindButtons() {
        this.buttonPanel.setOnLaundryClick(() -> {
            try {
                view.showPanel(buttonPanel.createLaundryPanel());
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        });
        this.buttonPanel.setOnDryerClick(() -> view.showPanel(buttonPanel.createDryerPanel()));
        this.buttonPanel.setOnCalendarClick(() -> view.showPanel(new OwnerCalendarPanel(view))); // Navigate to Owner Calendar
        this.buttonPanel.setOnLogoutClick(view::triggerLogout);
    }
}
