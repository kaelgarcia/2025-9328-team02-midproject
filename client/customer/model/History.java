package client.customer.model;

import server.ServerInterface;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class History {
    private final String username;
    private final ServerInterface server;

    public History(String username, ServerInterface server) {
        this.username = username;
        this.server = server;
    }

    public List<String[]> loadHistory() {
        try {
            return server.loadHistory(username);
        } catch (RemoteException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void updateComment(String machineType, String date, String time, String comment) {
        try {
            server.updateComment(username, machineType, date, time, comment);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean canDelete(String bookingDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d yyyy");
            Calendar today = Calendar.getInstance();
            int currentYear = today.get(Calendar.YEAR);

            Date bookedDate = dateFormat.parse(bookingDate + " " + currentYear);
            today.set(Calendar.HOUR_OF_DAY, 0);
            today.set(Calendar.MINUTE, 0);
            today.set(Calendar.SECOND, 0);
            today.set(Calendar.MILLISECOND, 0);

            Calendar booking = Calendar.getInstance();
            booking.setTime(bookedDate);

            long diffDays = (booking.getTimeInMillis() - today.getTimeInMillis()) / (24 * 60 * 60 * 1000);
            return diffDays >= 15;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteBooking(String machineType, String date, String time) {
        try {
            server.deleteBooking(username, machineType, date, time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}