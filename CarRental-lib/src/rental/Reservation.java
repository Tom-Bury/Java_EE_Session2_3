package rental;

import java.util.Calendar;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@Entity
public class Reservation extends Quote {
    
    private int carId;
    
    @ManyToOne
    private Car car;
    
    /***************
     * CONSTRUCTOR *
     ***************/
    
    public Reservation() {
    }

    public Reservation(Quote quote, int carId) {
    	super(quote.getCarRenter(), quote.getStartDate(), quote.getEndDate(), 
    		quote.getRentalCompany(), quote.getCarType(), quote.getRentalPrice());
        this.carId = carId;
    }
    
    /******
     * ID *
     ******/
    
    public int getCarId() {
    	return carId;
    }
    
    /*************
     * TO STRING *
     *************/
    
    @Override
    public String toString() {
        return String.format("Reservation for %s from %s to %s at %s\n\tCar type: %s\tCar: %s\n\tTotal price: %.2f", 
                getCarRenter(), getStartDate(), getEndDate(), getRentalCompany(), getCarType(), getCarId(), getRentalPrice());
    }	
    
    
    public boolean isInYear(int year) {
    	Date startDate = getStartDate();
    	Calendar cal = Calendar.getInstance();
    	cal.setTime(startDate);
    	int startYear = cal.get(Calendar.YEAR);
 		return year == startYear;
	}
}