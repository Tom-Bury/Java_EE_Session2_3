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
        //TODO
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
        //TODO
        try {
            return RentalStore.getRental(company).getCar(id).getReservations().size();
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(ManagerSession.class.getName()).log(Level.SEVERE, null, ex);
            return 0;
        }
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
    

}