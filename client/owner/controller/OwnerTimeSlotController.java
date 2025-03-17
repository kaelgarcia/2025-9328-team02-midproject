package client.owner.controller;

import client.owner.model.OwnerTimeSlotModel;
import client.owner.view.OwnerTimeSlotView;
import java.util.List;

public class OwnerTimeSlotController {
    private OwnerTimeSlotView view;
    private OwnerTimeSlotModel model;

    public OwnerTimeSlotController(OwnerTimeSlotView view, String machineType, int selectedDay) {
        this.view = view;
        this.model = new OwnerTimeSlotModel(machineType, selectedDay);
        this.loadTimeSlots();
    }

    private void loadTimeSlots() {
        List<String> timeSlots = model.getTimeSlots();
        view.displayTimeSlots(timeSlots);
    }
}
