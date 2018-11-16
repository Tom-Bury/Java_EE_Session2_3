package session;

import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import javax.persistence.TemporalType;
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
                
        CarRentalCompany crc  = new CarRentalCompany(name, regions, new ArrayList<Car>());

        em.persist(crc);
        
        for (Car c : cars) {
            crc.addCar(c);
        }
    }
    
    
    @Override
    public Set<CarType> getCarTypes(String company) {
        List<CarType> result = em.createNamedQuery("getCarTypes")
                .setParameter("crcName", company)
                .getResultList();
        
        Set<CarType> resultSet = new HashSet<CarType>();
        resultSet.addAll(result);
        
        return resultSet;               
    }

    @Override
    public Set<Integer> getCarIds(String company, String type) {
        Set<Integer> out = new HashSet<Integer>();
        
        List<Object> result = em.createNamedQuery("getCarIds")
                .setParameter("crcName", company)
                .setParameter("carType", type)
                .getResultList();
        
        for (Object id : result) {
            int intId = (int) id;
            
            out.add(intId);
        }
        
        
        return out;
    }

    @Override
    public int getNumberOfReservations(String company, String type, int id) {
        long result = (long) em.createNamedQuery("getNbReservationsForCar")
                .setParameter("crcName", company)
                .setParameter("carId", id)
                .getSingleResult();
        
        int intResult = (int) result;
        
        return intResult;
    }

    @Override
    public int getNumberOfReservations(String company, String type) {
        Long result = (Long) em.createNamedQuery("getNbReservations")
                .setParameter("crcName", company)
                .setParameter("carType", type)
                .getSingleResult();
        
        return result.intValue();
    }
    
    @Override
    public List<String> getBestClients() {
        List<Object[]> result = em.createNamedQuery("getAllClientsAndNbReservations").getResultList();
        
        List<String> bestClients = new ArrayList<String>();

        if (result.isEmpty()) {
            return null;
        }
        
        // Query result contains all client-nbResv pairs, sorted from most resv to least
        
        long maxNbResv = (long) result.get(0)[1];
        
        for (Object[] clientAndNb : result) {
            if (maxNbResv == (long) clientAndNb[1]) {
                bestClients.add((String) clientAndNb[0]);
            }
            else {
                break;
            }
        }   
        return bestClients;
    }
    
    
    @Override
    public CarType getMostPopularCarType(String crcName, int year) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyy");
        
        Date start = null;
        Date end = null;
        
        try {
            start = sdf.parse("01-01-" + year);
            end = sdf.parse("31-12-" + year);
        } catch (ParseException ex) {
            System.err.println("Could not create the start & end dates to check dates in getMostPopularCarType query.");
            ex.printStackTrace();
        }
        
        List<Object[]> resultList = em.createNamedQuery("getMostPopularCarTypeIn")
                .setParameter("crcName", crcName)
                .setParameter("start", start, TemporalType.DATE)
                .setParameter("end", end, TemporalType.DATE)
                .getResultList();
        
        // Query result contains all carType-nbResv pairs, sorted from most resv to least
        // The reservation start date must be between 01-01-year & 31-12-year
        
        return (CarType) resultList.get(0)[0];
    }
    
    
    @Override
    public int getNbOfReservationsBy(String clientName) {
        int result = (int) em.createNamedQuery("getNbReservationsForRenter")
                .setParameter("renter", clientName)
                .getSingleResult();
        
        return result;
    }
    

}