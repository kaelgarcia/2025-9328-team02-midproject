package client.owner.controller;

import client.owner.view.OwnerCalendarView;
import client.owner.view.OwnerHomePanel;
import client.owner.view.OwnerTimeSlotView;

public class OwnerCalendarController {
    private OwnerCalendarView view;
    private String machineType;
    private OwnerHomePanel homePanel;

    public OwnerCalendarController(OwnerCalendarView view, String machineType, OwnerHomePanel homePanel) {
        this.view = view;
        this.machineType = machineType;
        this.homePanel = homePanel;
        this.setupActions();
    }

    private void setupActions() {
        view.setOnDateSelected(day -> homePanel.showPanel(new OwnerTimeSlotView(machineType, day, homePanel)));
    }
}
