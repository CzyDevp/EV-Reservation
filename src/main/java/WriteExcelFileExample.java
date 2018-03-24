import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class WriteExcelFileExample {
    private static final String FILE_PATH = "D:\\play-java-starter-example\\EV\\scheduleData.xlsx";
    static List<CustomerScheduledData> output = new ArrayList();
    //We are making use of a single instance to prevent multiple write access to same file.
    private static final WriteExcelFileExample INSTANCE = new WriteExcelFileExample();
    public static WriteExcelFileExample getInstance() {
        return INSTANCE;
    }
    public static void writeStudentsListToExcel(List<CustomerScheduledData> cc){
        //output = new Controller().CUSTOMER_SCHEDULED;
        System.out.println("Size is "+cc.size());
        // Using XSSF for xlsx format, for xls use HSSF
        Workbook workbook = new XSSFWorkbook();
        Sheet studentsSheet = workbook.createSheet("Students");
        int rowIndex = 0;
        for(CustomerScheduledData c : cc){
            Row row = studentsSheet.createRow(rowIndex++);
            int cellIndex = 0;
            //first place in row is id
            row.createCell(cellIndex++).setCellValue(c.getCUSTMOER_ID());

            //second place in row is start-Time
            row.createCell(cellIndex++).setCellValue(String.valueOf(c.getPREFER_START_TIME()));

            //third place in row is Finsih-Time
            row.createCell(cellIndex++).setCellValue(String.valueOf(c.getPREFER_FINISH_TIME()));

            //fourth place in row is Duration
            row.createCell(cellIndex++).setCellValue(c.getChargingDuration());

            //fifth place in row is charger
            row.createCell(cellIndex++).setCellValue(c.getASSIGNED_CHARGER().C_P_Id);

        }
        //write this workbook in excel file.
        try {
            FileOutputStream fos = new FileOutputStream(FILE_PATH);
            workbook.write(fos);
            fos.close();
            System.out.println(FILE_PATH + " is successfully written");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
