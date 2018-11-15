package rental;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

// ALL QUERIES INVOLVING A CARRENTALCOMPANY
@NamedQueries({ 


    @NamedQuery (
            name = "allCrcs",
            query = "SELECT crc FROM CarRentalCompany crc"
    ),
    
    @NamedQuery (
            name = "allCrcNames",
            query = "SELECT crc.name FROM CarRentalCompany crc"
    ),
    

    @NamedQuery (
            name = "getNbReservations",
            query = "SELECT COUNT(resv) "
                    + "FROM CarRentalCompany crc, IN(crc.cars) car, IN(car.reservations) resv "
                    + "WHERE crc.name = :crcName "
                    + "AND car.type.name = :carType"),
    
    @NamedQuery (
            name = "getCarTypes",
            query = "SELECT crc.carTypes "
                    + "FROM CarRentalCompany crc "
                    + "WHERE crc.name = :crcName"
            ),
    
    @NamedQuery (
            name = "getNbReservationsForCar",
            query = "SELECT COUNT(resv) "
                    + "FROM CarRentalCompany crc, IN(crc.cars) car, IN(car.reservations) resv "
                    + "WHERE crc.name = :crcName "
                    + "AND car.id = :carId"),
    
    @NamedQuery (
        name = "getCarIds",
        query = "SELECT DISTINCT car.id "
                + "FROM CarRentalCompany crc, IN(crc.cars) car "
                + "WHERE crc.name = :crcName "
                + "AND car.type.name = :carType"),
    
    @NamedQuery (
            name = "getCheapestCarType",
            query = "SELECT car.type.name, car.type.rentalPricePerDay "
                    + "FROM CarRentalCompany crc, IN(crc.cars) car "
                    + "WHERE :region MEMBER OF crc.regions "
                    + "AND NOT EXISTS "
                        + "(SELECT res "
                        + "FROM IN(car.reservations) res "
                        + "WHERE (:start >= res.startDate AND :start <= res.endDate) "
                        + "OR (:end >= res.startDate AND :end <= res.endDate)) "
                    + "ORDER BY car.type.rentalPricePerDay ASC"),
    
    
    @NamedQuery (
            name = "getAllClientsAndNbReservations",
            query = "SELECT resv.carRenter, COUNT(resv.carRenter) AS nb_resv "
                    + "FROM CarRentalCompany crc, IN(crc.cars) car, IN(car.reservations) resv "
                    + "GROUP BY resv.carRenter "
                    + "ORDER BY nb_resv DESC")

    
    

})




@Entity
public class CarRentalCompany implements Serializable{

    private static final Logger logger = Logger.getLogger(CarRentalCompany.class.getName());
    
    @Id
    private String name;
    
//    @OneToMany(cascade = CascadeType.ALL, mappedBy="crc")
//    private List<Car> cars = new ArrayList<Car>();
//    
//    @OneToMany(fetch=FetchType.EAGER, cascade=CascadeType.ALL, mappedBy = "crc")
//    private Set<CarType> carTypes = new HashSet<CarType>();
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<Car> cars = new ArrayList<Car>();
    
    @OneToMany(cascade = CascadeType.ALL)
    private Set<CarType> carTypes = new HashSet<CarType>();
    
    @ElementCollection
    private List<String> regions = new ArrayList<String>();

	
    /***************
     * CONSTRUCTOR *
     ***************/
        
    public CarRentalCompany() {
        
    }

    public CarRentalCompany(String name, List<String> regions, List<Car> cars) {
        logger.log(Level.INFO, "<{0}> Starting up CRC {0} ...", name);
        setName(name);
        this.cars = cars;
        setRegions(regions);
        for (Car car : cars) {
//            car.setCrc(this);
            CarType type = car.getType();
//            type.setCrc(this);
            carTypes.add(type);
        }
        
    }

    /********
     * NAME *
     ********/
    
    public String getName() {
        return name;
    }

    private void setName(String name) {
        this.name = name;
    }

    /***********
     * Regions *
     **********/
    private void setRegions(List<String> regions) {
        this.regions = regions;
    }
    
    public List<String> getRegions() {
        return this.regions;
    }

    /*************
     * CAR TYPES *
     *************/
    
    public Collection<CarType> getAllTypes() {
        return carTypes;
    }

    public CarType getType(String carTypeName) {
        for(CarType type:carTypes){
            if(type.getName().equals(carTypeName))
                return type;
        }
        throw new IllegalArgumentException("<" + carTypeName + "> No cartype of name " + carTypeName);
    }

    public boolean isAvailable(String carTypeName, Date start, Date end) {
        logger.log(Level.INFO, "<{0}> Checking availability for car type {1}", new Object[]{name, carTypeName});
        return getAvailableCarTypes(start, end).contains(getType(carTypeName));
    }

    public Set<CarType> getAvailableCarTypes(Date start, Date end) {
        Set<CarType> availableCarTypes = new HashSet<CarType>();
        for (Car car : cars) {
            if (car.isAvailable(start, end)) {
                availableCarTypes.add(car.getType());
            }
        }
        return availableCarTypes;
    }

    /*********
     * CARS *
     *********/
    
    public Car getCar(int uid) {
        for (Car car : cars) {
            if (car.getId() == uid) {
                return car;
            }
        }
        throw new IllegalArgumentException("<" + name + "> No car with uid " + uid);
    }

    public Set<Car> getCars(CarType type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (car.getType().equals(type)) {
                out.add(car);
            }
        }
        return out;
    }
    
     public Set<Car> getCars(String type) {
        Set<Car> out = new HashSet<Car>();
        for (Car car : cars) {
            if (type.equals(car.getType().getName())) {
                out.add(car);
            }
        }
        return out;
    }

    private List<Car> getAvailableCars(String carType, Date start, Date end) {
        List<Car> availableCars = new LinkedList<Car>();
        for (Car car : cars) {
            if (car.getType().getName().equals(carType) && car.isAvailable(start, end)) {
                availableCars.add(car);
            }
        }
        return availableCars;
    }

    public void addCar(Car c) {
//        if (c.getCrc() != this) {
//            c.setCrc(this);
//        }
//        
//        if (c.getType().getCrc() != this) {
//            c.getType().setCrc(this);
//        }
        
        this.cars.add(c);
        this.carTypes.add(c.getType());
    }
    /****************
     * RESERVATIONS *
     ****************/
    
    public Quote createQuote(ReservationConstraints constraints, String guest)
            throws ReservationException {
        logger.log(Level.INFO, "<{0}> Creating tentative reservation for {1} with constraints {2}",
                new Object[]{name, guest, constraints.toString()});


        if (!this.regions.contains(constraints.getRegion()) || !isAvailable(constraints.getCarType(), constraints.getStartDate(), constraints.getEndDate())) {
            throw new ReservationException("<" + name
                    + "> No cars available to satisfy the given constraints.");
        }
		
        CarType type = getType(constraints.getCarType());

        double price = calculateRentalPrice(type.getRentalPricePerDay(), constraints.getStartDate(), constraints.getEndDate());

        return new Quote(guest, constraints.getStartDate(), constraints.getEndDate(), getName(), constraints.getCarType(), price);
    }

    // Implementation can be subject to different pricing strategies
    private double calculateRentalPrice(double rentalPricePerDay, Date start, Date end) {
        return rentalPricePerDay * Math.ceil((end.getTime() - start.getTime())
                / (1000 * 60 * 60 * 24D));
    }

    public Reservation confirmQuote(Quote quote) throws ReservationException {
        logger.log(Level.INFO, "<{0}> Reservation of {1}", new Object[]{name, quote.toString()});
        List<Car> availableCars = getAvailableCars(quote.getCarType(), quote.getStartDate(), quote.getEndDate());
        if (availableCars.isEmpty()) {
            throw new ReservationException("Reservation failed, all cars of type " + quote.getCarType()
                    + " are unavailable from " + quote.getStartDate() + " to " + quote.getEndDate());
        }
        Car car = availableCars.get((int) (Math.random() * availableCars.size()));

        Reservation res = new Reservation(quote, car.getId());
        car.addReservation(res);
        return res;
    }

    public void cancelReservation(Reservation res) {
        logger.log(Level.INFO, "<{0}> Cancelling reservation {1}", new Object[]{name, res.toString()});
        getCar(res.getCarId()).removeReservation(res);
    }
    
    public Set<Reservation> getReservationsBy(String renter) {
        logger.log(Level.INFO, "<{0}> Retrieving reservations by {1}", new Object[]{name, renter});
        Set<Reservation> out = new HashSet<Reservation>();
        for(Car c : cars) {
            for(Reservation r : c.getReservations()) {
                if(r.getCarRenter().equals(renter))
                    out.add(r);
            }
        }
        return out;
    }



}

