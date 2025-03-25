package client.owner.controller;

import client.owner.view.OwnerCalendarView;
import client.owner.view.OwnerHomePanel;
import client.owner.view.OwnerTimeSlotView;

public class OwnerCalendarController {
    private final OwnerCalendarView view;
    private final String machineType;
    private final OwnerHomePanel homePanel;

    public OwnerCalendarController(OwnerCalendarView view, String machineType, OwnerHomePanel homePanel) {
        this.view = view;
        this.machineType = machineType;
        this.homePanel = homePanel;
        initializeActions();
    }

    private void initializeActions() {
        view.setOnDateSelected(this::handleDateSelection);
    }

    private void handleDateSelection(int day) {
        homePanel.showPanel(new OwnerTimeSlotView(machineType, day, homePanel));
    }
}
