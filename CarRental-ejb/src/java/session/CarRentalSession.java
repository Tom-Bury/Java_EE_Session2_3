package session;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import rental.CarRentalCompany;
import rental.CarType;
import rental.Quote;
import rental.RentalStore;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;

@Stateful
public class CarRentalSession implements CarRentalSessionRemote {
    
    @PersistenceContext
    EntityManager em;

    private String renter;
    private List<Quote> quotes = new LinkedList<Quote>();
    
    
    
    
    
    private List<CarRentalCompany> getAllCrcs() {
        return em.createNamedQuery("allCrcs").getResultList();
    }

    @Override
    public Set<String> getAllRentalCompanies() {
        List<String> resultList = em.createNamedQuery("allCrcNames").getResultList();
        Set<String> resultSet = new HashSet<String>();
        resultSet.addAll(resultList);
        return resultSet;
    }
    
    @Override
    public List<CarType> getAvailableCarTypes(Date start, Date end) {
        List<CarRentalCompany> allCrcs = getAllCrcs();
        
        Set<CarType> availableTypes = new HashSet<CarType>();
        
        for (CarRentalCompany crc : allCrcs) {
            availableTypes.addAll(crc.getAvailableCarTypes(start, end));
        }
        
        List<CarType> result = new ArrayList<CarType>();
        result.addAll(availableTypes);
        return result;
    }

    @Override
    public Quote createQuote(ReservationConstraints constraints) throws ReservationException {
        List<CarRentalCompany> allCrcs = getAllCrcs();
        
        Quote q = null;
        for (CarRentalCompany crc : allCrcs) {
            try {
                q = crc.createQuote(constraints, renter);
                this.quotes.add(q);
            } catch (Exception e) {
                System.out.println("\tCouldn't create quote at company " + crc.getName());
            }    
        }
        
        
        return q;
    }

    @Override
    public List<Quote> getCurrentQuotes() {
        return quotes;
    }

    @Override
    public List<Reservation> confirmQuotes() throws ReservationException {
        List<Reservation> done = new LinkedList<Reservation>();
                
        try {
            for (Quote q : quotes) {
                String currCrcName = q.getRentalCompany();
                CarRentalCompany currCrc = em.find(CarRentalCompany.class, currCrcName);
                Reservation r = currCrc.confirmQuote(q);
                done.add(r);
            }
        } catch (Exception e) {
            
            rollbackReservations(done);      
                        
            throw new ReservationException(e);
        }
        
        // Persist each made reservation
        for (Reservation r : done) {
            em.persist(r);
        }
        
        
        return done;
    }
    
    private void rollbackReservations(List<Reservation> reservations) {
            for(Reservation r : reservations) {
                String currCrcName = r.getRentalCompany();
                CarRentalCompany currCrc = em.find(CarRentalCompany.class, currCrcName);
                currCrc.cancelReservation(r);
            }
    }

    @Override
    public void setRenterName(String name) {
        if (renter != null) {
            throw new IllegalStateException("name already set");
        }
        renter = name;
    }
}