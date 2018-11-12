package client;

import client.DataLoader.CrcData;
import static client.DataLoader.loadData;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import javax.naming.InitialContext;
import rental.CarType;
import rental.Reservation;
import rental.ReservationConstraints;
import rental.ReservationException;
import session.CarRentalSessionRemote;
import session.ManagerSessionRemote;
import rental.Car;
import rental.CarRentalCompany;

public class Main extends AbstractTestManagement<CarRentalSessionRemote, ManagerSessionRemote> {

    public Main(String scriptFile) {
        super(scriptFile);
    }

    public static void main(String[] args) throws Exception {
        // TODO: use updated manager interface to load cars into companies
        
        CrcData hertzData = loadData("hertz.csv");
        CarRentalCompany hertz = new CarRentalCompany(hertzData.name, hertzData.regions, hertzData.cars);
        
        CrcData dockxData = loadData("dockx.csv");
        CarRentalCompany dockx = new CarRentalCompany(dockxData.name, dockxData.regions, dockxData.cars);
        
        Main main = new Main("trips");
        
        ManagerSessionRemote initialMngr = main.getNewManagerSession("initialMngr", "allInitialCompanies");
        initialMngr.registerCompany(hertz);
        initialMngr.registerCompany(dockx);
               
        new Main("trips").run();
    }
    
    /*********************
     * OVERRIDEN METHODS *
     *********************/

    // ---------------------------- RESERVATION SESSION ----------------------------
    
     @Override
    protected CarRentalSessionRemote getNewReservationSession(String name) throws Exception {
        // De volgende code is overgenomen van de opgave sessie EE-1 deel 3.3
        InitialContext context = new InitialContext();
        CarRentalSessionRemote session = (CarRentalSessionRemote) context.lookup(CarRentalSessionRemote.class.getName());
        session.setRenterName(name);
        
        System.out.println("\nCLIENT MAIN: started new reservationSession by " + name + "\n");
        
        return session;
    }
    
    
    @Override
    protected void checkForAvailableCarTypes(CarRentalSessionRemote session, Date start, Date end) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void addQuoteToSession(CarRentalSessionRemote session, String name, Date start, Date end, String carType, String region) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected List<Reservation> confirmQuotes(CarRentalSessionRemote session, String name) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
       @Override
    protected String getCheapestCarType(CarRentalSessionRemote session, Date start, Date end, String region) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    
    // ---------------------------- MANAGER SESSION ----------------------------
    
        @Override
    protected ManagerSessionRemote getNewManagerSession(String name, String carRentalName) throws Exception {
        // De volgende code is overgenomen van de opgave sessie EE-1 deel 3.3
        InitialContext context = new InitialContext();
        ManagerSessionRemote session = (ManagerSessionRemote) context.lookup(ManagerSessionRemote.class.getName());
        
        System.out.println("\nCLIENT MAIN: started new managersession");
        
        return session;
    }    
    
    @Override
    protected Set<String> getBestClients(ManagerSessionRemote ms) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected CarType getMostPopularCarTypeIn(ManagerSessionRemote ms, String carRentalCompanyName, int year) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsBy(ManagerSessionRemote ms, String clientName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected int getNumberOfReservationsForCarType(ManagerSessionRemote ms, String carRentalName, String carType) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
 
}