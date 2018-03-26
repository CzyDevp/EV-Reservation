import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Iterator;
public class readDataset {
    static  public void readFile(FileInputStream fis) throws IOException {
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);   //read customers
        Sheet Charger_Sheet = workbook.getSheetAt(1);  //read charger_name
        Iterator<Row> CHARGER_Iterator = Charger_Sheet.iterator();
        Iterator<Row> iterator = sheet.iterator();
        //*******************************************Charger reading Start**********************************************
        while (CHARGER_Iterator.hasNext()) {
            Charger charger = new Charger();
            Row currentRow = CHARGER_Iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            if (currentRow.getRowNum() == 0) {
            }
            else {
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if (currentCell.getCellTypeEnum().equals(CellType.STRING)){}
                    else if (currentCell.getCellTypeEnum().equals(CellType.NUMERIC)){
                        if (currentCell.getColumnIndex() == 0) {
                            charger.setC_P_Id((int) currentCell.getNumericCellValue());   //charger id
                        }
                        if(currentCell.getColumnIndex()==1){
                            charger.setCh((int)currentCell.getNumericCellValue());
                        }
                    }
                }
                Controller.chargers.add(charger);
            }
        }
        //***************************************************Charger reading Done*******************************************

        //*************************************************Customer reading Start*******************************************
        while (iterator.hasNext()) {
            Customer customer = new Customer();
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            if (currentRow.getRowNum() == 0) {
            } else {
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if (currentCell.getCellTypeEnum().equals(CellType.STRING)) {
                    } else if (currentCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                        if (currentCell.getColumnIndex() == 0) {
                            customer.setCustomer_Id((int) currentCell.getNumericCellValue());   //customer id
                        }
                        if (currentCell.getColumnIndex() == 3) {
                            customer.setMiles((int)currentCell.getNumericCellValue()); //miles
                        }
                        if (currentCell.getColumnIndex() == 4) {

                            customer.setEv_car((int)currentCell.getNumericCellValue()); //ev_car
                        }
                        if (DateUtil.isCellDateFormatted(currentCell)) {
                            if (currentCell.getColumnIndex() == 1) {
                                SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");
                                LocalTime time = LocalTime.parse(sdf2.format(currentCell.getDateCellValue())); //starttime
                                customer.setPrefer_Start_Time(time);
                            }
                            if (currentCell.getColumnIndex() == 2) {

                                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                                customer.setPrefer_End_Time(LocalTime.parse(formatTime.format(currentCell.getDateCellValue())));//endtime
                            }
                        }
                    }
                }
                Controller.customersRequests.add(customer);
            }
            //******************************************Customer reading Done***********************************************
        }
    }
}
