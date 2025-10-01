import java.util.*;
import java.util.concurrent.TimeUnit;

// Enum for Vehicle Types
enum VehicleType {
    CAR, BIKE, TRUCK;
}

// Abstract Vehicle class (dumb data object)
abstract class Vehicle {
    int plateNum;
    VehicleType type;

    Vehicle(int plateNum, VehicleType type) {
        this.plateNum = plateNum;
        this.type = type;
    }
}

// Concrete Vehicle classes
class Car extends Vehicle {
    Car(int plateNum) { super(plateNum, VehicleType.CAR); }
}

class Bike extends Vehicle {
    Bike(int plateNum) { super(plateNum, VehicleType.BIKE); }
}

class Truck extends Vehicle {
    Truck(int plateNum) { super(plateNum, VehicleType.TRUCK); }
}

// Vehicle Factory
class VehicleFactory {
    public static Vehicle createVehicle(VehicleType type, int plateNum) {
        switch (type) {
            case CAR: return new Car(plateNum);
            case BIKE: return new Bike(plateNum);
            case TRUCK: return new Truck(plateNum);
            default: return null;
        }
    }
}

// Time tracking for parking
class Time {
    long startTime, endTime;

    void entry() { startTime = System.currentTimeMillis(); }
    long exit() {
        endTime = System.currentTimeMillis();
        return endTime - startTime; // milliseconds
    }
}

// Abstract Fee Strategy
abstract class FeeStrategy {
    double chargePerHour;

    FeeStrategy(double chargePerHour) { this.chargePerHour = chargePerHour; }
    abstract double calculateFee(Time t);
}

// Concrete Fee Strategies
class CarFeeStrategy extends FeeStrategy {
    CarFeeStrategy() { super(2.5); }
    @Override
    double calculateFee(Time t) {
        double hours = t.exit() / 3600000.0; // ms → hours
        return chargePerHour * hours;
    }
}

class BikeFeeStrategy extends FeeStrategy {
    BikeFeeStrategy() { super(1.0); }
    @Override
    double calculateFee(Time t) {
        double hours = t.exit() / 3600000.0;
        return chargePerHour * hours;
    }
}

class TruckFeeStrategy extends FeeStrategy {
    TruckFeeStrategy() { super(5.0); }
    @Override
    double calculateFee(Time t) {
        double hours = t.exit() / 3600000.0;
        return chargePerHour * hours;
    }
}

// Factory to provide FeeStrategy based on vehicle type
class FeeStrategyFactory {
    private static final Map<VehicleType, FeeStrategy> strategyMap = Map.of(
        VehicleType.CAR, new CarFeeStrategy(),
        VehicleType.BIKE, new BikeFeeStrategy(),
        VehicleType.TRUCK, new TruckFeeStrategy()
    );

    public static FeeStrategy getFeeStrategy(VehicleType type) {
        FeeStrategy s = strategyMap.get(type);
        if (s == null) throw new IllegalArgumentException("Unknown type: " + type);
        return s;
    }
}

// Parking Lot Slot
class Lot {
    int id;
    boolean isAvailable = true;
    Vehicle vehicle;

    Lot(int id) { this.id = id; }

    void assignVehicle(Vehicle v) {
        vehicle = v;
        isAvailable = false;
    }

    void removeVehicle() {
        vehicle = null;
        isAvailable = true;
    }
}

// Parking Ticket
class ParkingTicket {
    Vehicle vehicle;
    Lot lot;
    Time time;
    FeeStrategy feeStrategy;

    ParkingTicket(Vehicle vehicle, Lot lot, FeeStrategy feeStrategy) {
        this.vehicle = vehicle;
        this.lot = lot;
        this.feeStrategy = feeStrategy;
        this.time = new Time();
        time.entry();
    }

    double calculateFee() { return feeStrategy.calculateFee(time); }
}

// Parking Lot Manager (Singleton)
class ParkingLot {
    private List<Lot> lots = new ArrayList<>();
    private Map<Lot, ParkingTicket> activeTickets = new HashMap<>();
    private static ParkingLot instance;

    private ParkingLot(int totalLots) {
        for (int i = 0; i < totalLots; i++) lots.add(new Lot(i));
    }

    public static ParkingLot getInstance(int totalLots) {
        if (instance == null) instance = new ParkingLot(totalLots);
        return instance;
    }

    public ParkingTicket parkVehicle(Vehicle v) {
        for (Lot lot : lots) {
            if (lot.isAvailable) {
                lot.assignVehicle(v);
                FeeStrategy strategy = FeeStrategyFactory.getFeeStrategy(v.type);
                ParkingTicket ticket = new ParkingTicket(v, lot, strategy);
                activeTickets.put(lot, ticket);
                System.out.println("Vehicle parked at lot " + lot.id);
                return ticket;
            }
        }
        System.out.println("Parking full!");
        return null;
    }

    public void unparkVehicle(ParkingTicket ticket) {
        Lot lot = ticket.lot;
        double fee = ticket.calculateFee();
        System.out.println("Vehicle leaving lot " + lot.id + ". Fee: $" + String.format("%.2f", fee));
        lot.removeVehicle();
        activeTickets.remove(lot);
    }

    public void showAvailableLots() {
        long count = lots.stream().filter(l -> l.isAvailable).count();
        System.out.println("Available lots: " + count);
    }
}

// Demo Main
public class Main {
    public static void main(String[] args) throws InterruptedException {
        ParkingLot parkingLot = ParkingLot.getInstance(5);

        Vehicle car = VehicleFactory.createVehicle(VehicleType.CAR, 101);
        Vehicle bike = VehicleFactory.createVehicle(VehicleType.BIKE, 201);
        Vehicle truck = VehicleFactory.createVehicle(VehicleType.TRUCK, 301);

        ParkingTicket carTicket = parkingLot.parkVehicle(car);
        ParkingTicket bikeTicket = parkingLot.parkVehicle(bike);

        parkingLot.showAvailableLots();

        // Simulate parking duration
        TimeUnit.SECONDS.sleep(2);

        parkingLot.unparkVehicle(carTicket);
        parkingLot.showAvailableLots();

        ParkingTicket truckTicket = parkingLot.parkVehicle(truck);

        TimeUnit.SECONDS.sleep(1);

        parkingLot.unparkVehicle(bikeTicket);
        parkingLot.unparkVehicle(truckTicket);

        parkingLot.showAvailableLots();
    }
}
