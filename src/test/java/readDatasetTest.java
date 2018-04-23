import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class readDatasetTest {
    FileInputStream fis;
    @org.junit.Before
    public void setUp() throws Exception {
      String filename = "D:\\Study\\M.Eng\\Winter2018\\INSE\\EV\\data_final_1.xlsx";
      fis = new FileInputStream(new File(filename));
    }

    @org.junit.After
    public void tearDown() throws Exception {
        //readTest=null;
        fis.close();
    }
    @Test
    public void readFile() throws IOException {
        readDataset.readFile(fis);
        int expectedSize=10;
        assertEquals(expectedSize,Controller.customersRequests.size());

    }
}