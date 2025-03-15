package client.customer.model;

import java.util.Arrays;
import java.util.List;

public class MachineTypeModel {
    private final List<String> machineTypes;

    public MachineTypeModel() {
        this.machineTypes = Arrays.asList("Laundry", "Dryer");
    }

    public List<String> getMachineTypes() {
        return machineTypes;
    }
}