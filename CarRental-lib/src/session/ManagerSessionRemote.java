package session;

import java.util.List;
import java.util.Set;
import javax.ejb.Remote;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Reservation;

@Remote
public interface ManagerSessionRemote {
    
    public void registerCompany(List<Car> cars, String name, List<String> regions);
    
    public Set<CarType> getCarTypes(String company);
    
    public Set<Integer> getCarIds(String company,String type);
    
    public int getNumberOfReservations(String company, String type, int carId);
    
    public int getNumberOfReservations(String company, String type);
    
    public List<String> getBestClients();
}