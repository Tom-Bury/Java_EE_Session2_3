package session;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.Car;
import rental.CarRentalCompany;
import rental.CarType;
import rental.RentalStore;
import rental.Reservation;

@Stateless
public class ManagerSession implements ManagerSessionRemote {
    
    @PersistenceContext
    EntityManager em;
    
    private List<CarRentalCompany> getAllCrcs() {
        return em.createNamedQuery("allCrcs").getResultList();
    }
    
    @Override
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void registerCompany(List<Car> cars, String name, List<String> regions) {
        
        for (Car c : cars) {
            em.persist(c.getType());
//            System.out.println("Persisted cartype: " + c.getType().getName());
        }
        
        for (Car c : cars) {
            em.persist(c);
//            System.out.println("Persisted car: " + c.toString());
        }
        
        em.persist(new CarRentalCompany(name, regions, cars));
        /*
        CarRentalCompany crc = new CarRentalCompany(name, regions, new ArrayList<Car>());
        em.persist(crc);
        
        for (Car car : cars) {
            storeAtCompanyWithRef(car, crc);
        }
        */
    }
    
    /*    
    private void storeAtCompanyWithRef(Car car, CarRentalCompany carRentalCompany) {
        CarType carType = em.find(CarType.class, car.getType().getId());
        if (carType != null) {
            car.setType(carType);
        }

        carRentalCompany.addCar(car);
    }   
    */
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        try {
            return new HashSet<CarType>(RentalStore.getRental(company).getAllTypes());
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.add(c.getId());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Set<Reservation> out = new HashSet<Reservation>();
        try {
            for(Car c: RentalStore.getRental(company).getCars(type)){
                out.addAll(c.getReservations());
            }
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
        return out.size();
    }
    
    

}