package client.customer.model;

import java.util.ArrayList;
import java.util.List;

public class CalendarModel {
    private final List<String> dates;

    public CalendarModel() {
        dates = new ArrayList<>();
        for (int day = 1; day <= 31; day++) {
            dates.add("March " + day);
        }
    }

    public List<String> getDates() {
        return dates;
    }
}