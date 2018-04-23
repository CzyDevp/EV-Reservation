import jdk.vm.ci.meta.Local;

import java.sql.Time;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class CustomerTest {
        LocalTime Prefer_Start_Time,Prefer_End_Time;
        Customer cTest;
    @org.junit.Before
    public void setUp() throws Exception {
        Prefer_Start_Time = LocalTime.of(9,00);
        Prefer_End_Time =  LocalTime.of(10,20);
        cTest = new Customer();
    }

    @org.junit.After
    public void tearDown() throws Exception {
        cTest=null;
    }

    @org.junit.Test
    public void getTimeInMinutes() {
        int actualMinutes = 80;
        cTest.setPrefer_Start_Time(Prefer_Start_Time);
        cTest.setPrefer_End_Time(Prefer_End_Time);
        assertEquals(actualMinutes,cTest.getTimeInMinutes());
    }
}