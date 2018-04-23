import org.junit.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class ControllerTest {
    LocalTime Prefer_Start_Time,Prefer_End_Time;
    Controller controller;
    Charger c1,c2,c3,c4,c5,c61;
    Customer customer1,customer,customer2,customer3;
    List<Charger>chargers;
    @org.junit.Before
    public void setUp() throws Exception {
        controller = new Controller();
        controller.defaultCompatibility();
        Controller.cs_sc = new ArrayList<>();
        Controller.cs_ccs = new ArrayList<>();
        Controller.chdm = new ArrayList<>();
        Controller.last = new ArrayList<>();
        c1=new Charger();c1.setC_P_Id(1);c1.setCh(c1.getC_P_Id());
        c2=new Charger();c2.setC_P_Id(2);c2.setCh(c2.getC_P_Id());
        c3 = new Charger();c3.setC_P_Id(3);c3.setCh(4);
        Prefer_Start_Time = LocalTime.of(9,00);
        Prefer_End_Time =  LocalTime.of(10,20);
        //getSuperCharger
        customer1 = new Customer();
        customer1.setCustomer_Id(1);customer1.setEv_car(3);customer1.setMiles(80);
        customer1.setPrefer_Start_Time(Prefer_Start_Time);customer1.setPrefer_End_Time(Prefer_End_Time);
        //getComboCharger
        c4 = new Charger();c4.setC_P_Id(4);c4.setCh(3);
        customer = new Customer();
        customer.setCustomer_Id(2);customer.setEv_car(2);customer.setMiles(80);
        customer.setPrefer_Start_Time(Prefer_Start_Time);customer.setPrefer_End_Time(Prefer_End_Time);
        //getChademo
        c5 = new Charger();c5.setC_P_Id(5);c5.setCh(2);
        customer2 = new Customer();
        customer2.setCustomer_Id(2);customer2.setEv_car(3);customer2.setMiles(80);
        customer2.setPrefer_Start_Time(Prefer_Start_Time);customer2.setPrefer_End_Time(Prefer_End_Time);
        //chargers = new ArrayList<>();
        Controller.chargers.add(c1);Controller.chargers.add(c2);

    }
    @org.junit.After
    public void tearDown() throws Exception {
        controller=null;
    }
    @Test
    public void testGetSuperCharger(){
        assertEquals(c3,controller.getCharger_SC(customer1,c3));
    }
    @Test
    public void testGetComboCharger(){
        assertEquals(c4,controller.getCharger_CCS(customer,c4));
    }
    @Test
    public void testGetChademo(){
        assertEquals(c5,controller.getCharger_chademo(customer2,c5));
    }

    @Test
    public void testDefault(){
        int expectedSize=7;
        controller.defaultCompatibility();
        assertEquals(expectedSize,controller.EV_COMPATIBILITY_DEFAULT.size());
    }

    @Test
    public void testGetCharger(){
        Charger expectedCharger = new Charger();
        expectedCharger.setC_P_Id(1);expectedCharger.setCh(expectedCharger.getC_P_Id());
        assertEquals(expectedCharger,Controller.getChargerById(1));

    }
}