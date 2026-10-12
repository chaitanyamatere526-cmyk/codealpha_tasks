import java.io.*;
import java.util.*;

class Room {
    int roomNumber;
    String category;
    double price;

    Room(int roomNumber, String category, double price) {
        this.roomNumber = roomNumber;
        this.category = category;
        this.price = price;
    }
}

class Reservation {
    int bookingId;
    String customerName;
    Room room;
    int nights;
    boolean paid;

    Reservation(int bookingId, String customerName,
                Room room, int nights, boolean paid) {
        this.bookingId = bookingId;
        this.customerName = customerName;
        this.room = room;
        this.nights = nights;
        this.paid = paid;
    }

    double getTotal() {
        return room.price * nights;
    }

    public String toString() {
        return bookingId + "," + customerName + ","
                + room.roomNumber + "," + room.category + ","
                + nights + "," + paid;
    }
}

public class HotelReservationSystem{

    static Scanner sc = new Scanner(System.in);
    static ArrayList<Room> rooms = new ArrayList<>();
    static ArrayList<Reservation> bookings = new ArrayList<>();
    static final String FILE_NAME = "bookings.txt";
    static int nextBookingId = 1001;

    static void initializeRooms() {
        for (int i = 101; i <= 105; i++)
            rooms.add(new Room(i, "Standard", 1500));

        for (int i = 201; i <= 203; i++)
            rooms.add(new Room(i, "Deluxe", 2500));

        for (int i = 301; i <= 302; i++)
            rooms.add(new Room(i, "Suite", 4000));
    }

    static boolean isAvailable(Room room) {
        for (Reservation b : bookings) {
            if (b.room.roomNumber == room.roomNumber)
                return false;
        }
        return true;
    }

    static void searchRooms() {
        System.out.println("\nAvailable Rooms:");

        for (Room r : rooms) {
            if (isAvailable(r)) {
                System.out.println("Room: " + r.roomNumber
                        + " | Category: " + r.category
                        + " | Price/Night: Rs." + r.price);
            }
        }
    }

    static void bookRoom() {
        searchRooms();

        System.out.print("\nEnter room number: ");
        int roomNumber = sc.nextInt();
        sc.nextLine();

        Room selected = null;

        for (Room r : rooms) {
            if (r.roomNumber == roomNumber && isAvailable(r)) {
                selected = r;
                break;
            }
        }

        if (selected == null) {
            System.out.println("Room unavailable or invalid!");
            return;
        }

        System.out.print("Enter customer name: ");
        String name = sc.nextLine();

        System.out.print("Enter number of nights: ");
        int nights = sc.nextInt();
        sc.nextLine();

        if (nights <= 0) {
            System.out.println("Invalid number of nights!");
            return;
        }

        double total = selected.price * nights;

        System.out.println("Total Amount: Rs." + total);
        System.out.print("Confirm simulated payment? (yes/no): ");
        String choice = sc.nextLine();

        if (!choice.equalsIgnoreCase("yes")) {
            System.out.println("Payment cancelled. Booking not created.");
            return;
        }

        Reservation booking = new Reservation(
                nextBookingId++, name, selected, nights, true);

        bookings.add(booking);
        saveBookings();

        System.out.println("\nBooking successful!");
        System.out.println("Booking ID: " + booking.bookingId);
        System.out.println("Payment Status: Paid (simulation)");
    }

    static void cancelBooking() {
        System.out.print("Enter booking ID to cancel: ");
        int id = sc.nextInt();
        sc.nextLine();

        for (Reservation b : bookings) {
            if (b.bookingId == id) {
                bookings.remove(b);
                saveBookings();
                System.out.println("Booking cancelled successfully.");
                System.out.println(
                        "Payment refund is not processed in this simulation.");
                return;
            }
        }

        System.out.println("Booking ID not found.");
    }

    static void viewBookings() {
        if (bookings.isEmpty()) {
            System.out.println("No active bookings.");
            return;
        }

        for (Reservation b : bookings) {
            System.out.println("\nBooking ID: " + b.bookingId);
            System.out.println("Customer: " + b.customerName);
            System.out.println("Room: " + b.room.roomNumber);
            System.out.println("Category: " + b.room.category);
            System.out.println("Nights: " + b.nights);
            System.out.println("Total: Rs." + b.getTotal());
            System.out.println("Payment: "
                    + (b.paid ? "Paid" : "Pending"));
        }
    }

    static void saveBookings() {
        try (PrintWriter writer = new PrintWriter(
                new FileWriter(FILE_NAME))) {

            for (Reservation b : bookings) {
                writer.println(b);
            }

        } catch (IOException e) {
            System.out.println("Error saving bookings: "
                    + e.getMessage());
        }
    }

    static void loadBookings() {
        File file = new File(FILE_NAME);

        if (!file.exists())
            return;

        try (BufferedReader reader = new BufferedReader(
                new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",", -1);

                int id = Integer.parseInt(data[0]);
                String name = data[1];
                int roomNumber = Integer.parseInt(data[2]);
                String category = data[3];
                int nights = Integer.parseInt(data[4]);
                boolean paid = Boolean.parseBoolean(data[5]);

                for (Room r : rooms) {
                    if (r.roomNumber == roomNumber
                            && r.category.equals(category)) {

                        bookings.add(new Reservation(
                                id, name, r, nights, paid));

                        nextBookingId = Math.max(
                                nextBookingId, id + 1);
                        break;
                    }
                }
            }

        } catch (IOException | RuntimeException e) {
            System.out.println("Error loading bookings: "
                    + e.getMessage());
        }
    }

    public static void main(String[] args) {
        initializeRooms();
        loadBookings();

        while (true) {
            System.out.println("\n===== HOTEL RESERVATION SYSTEM =====");
            System.out.println("1. Search Available Rooms");
            System.out.println("2. Book a Room");
            System.out.println("3. Cancel Reservation");
            System.out.println("4. View Bookings");
            System.out.println("5. Exit");
            System.out.print("Choose option: ");

            int option;

            try {
                option = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.nextLine();
                continue;
            }

            switch (option) {
                case 1:
                    searchRooms();
                    break;
                case 2:
                    bookRoom();
                    break;
                case 3:
                    cancelBooking();
                    break;
                case 4:
                    viewBookings();
                    break;
                case 5:
                    System.out.println("Thank you for using the system!");
                    sc.close();
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}