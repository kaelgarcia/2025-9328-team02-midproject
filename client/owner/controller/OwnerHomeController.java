package client.owner.controller;

import client.owner.view.OwnerHomePanel;
import client.owner.model.OwnerHomeButtonPanel;

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
        this.buttonPanel.setOnLaundryClick(() -> view.showPanel(buttonPanel.createLaundryPanel()));
        this.buttonPanel.setOnDryerClick(() -> view.showPanel(buttonPanel.createDryerPanel()));
        this.buttonPanel.setOnLogoutClick(view::triggerLogout);
    }
}
