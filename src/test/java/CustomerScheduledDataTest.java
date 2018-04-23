import org.junit.Test;

import java.time.LocalTime;

import static org.junit.Assert.*;

public class CustomerScheduledDataTest {
    LocalTime Prefer_Start_Time,Prefer_End_Time;
    CustomerScheduledData cTest;
    @org.junit.Before
    public void setUp() throws Exception {
        Prefer_Start_Time = LocalTime.of(9,00);
        Prefer_End_Time =  LocalTime.of(10,20);
        cTest = new CustomerScheduledData();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        cTest=null;
    }
    @Test
    public void getChargingDuration() {
        cTest.setPrefer_Start_Time(Prefer_Start_Time);
        cTest.setPrefer_Fin_Time(Prefer_End_Time);
        int actualDuration = 80;
        assertEquals(actualDuration,cTest.getChargingDuration());
    }
}