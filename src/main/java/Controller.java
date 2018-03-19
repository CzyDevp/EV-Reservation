import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
public class Controller {
    List<Customer> customersRequests = new ArrayList<>();
    List<Charger> chargers = new ArrayList<>();
    int ROWS_TOTAL=0;
    int ROW_NUMBERS;
    @FXML
    private Button getDataSet;
    String filename;
    public void getFile() throws IOException {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("excelfiles (*.xlsx)", "*.xlsx");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            filename = file.toString();
            System.out.println(file);
            processFile();
            Stage stage = (Stage) getDataSet.getScene().getWindow();
            stage.close();
            //  exit();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Oops!!");
            alert.setHeaderText("Info");
            alert.setContentText("Please provide a valid file");
            alert.show();
        }
    }
    public void processFile() throws IOException {
        FileInputStream fis = new FileInputStream(new File(filename));
        Workbook workbook = new XSSFWorkbook(fis);
        //int sheets = workbook.getNumberOfSheets();
        //System.out.println("Number of Sheeets- " +sheets);
        int empty=0;
        Sheet sheet = workbook.getSheetAt(1);
        ROWS_TOTAL= sheet.getPhysicalNumberOfRows();
        //System.out.println(sheet.getLastRowNum());
        System.out.println("Totoa number of Rows : "+ROWS_TOTAL);
        for(Row row: sheet){
            if(row.getCell(0).getCellTypeEnum().equals(CellType.STRING)){
                ROW_NUMBERS = row.getRowNum();
            }
        }
        System.out.println("Number is "+ROW_NUMBERS);
        for(int i=1;i<ROW_NUMBERS;i++){
            Customer customer = new Customer();
            Row r = sheet.getRow(i);
//            System.out.println(r.getRowNum());
            if(r!=null) {
                Iterator cellIterator = r.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = (XSSFCell) cellIterator.next();
                    if (currentCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                        if (currentCell.getColumnIndex() == 0) {
                            customer.setCUSTOMER_ID((int) currentCell.getNumericCellValue());   //customer id
                        }
                        if (currentCell.getColumnIndex() == 3) {
                            customer.setMILES(currentCell.getNumericCellValue()); //miles
                        }
                        if (currentCell.getColumnIndex() == 4) {
                            customer.setEV_TYPE((int) currentCell.getNumericCellValue());
                        }
                        if (DateUtil.isCellDateFormatted(currentCell)) {
                            if (currentCell.getColumnIndex() == 1) {
                                Date date = currentCell.getDateCellValue();
                                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                                // System.out.print(formatTime.format(date) + "--");
                                customer.setPREFER_START_TIME(formatTime.format(date));
                            }
                            if (currentCell.getColumnIndex() == 2) {
                                Date date = currentCell.getDateCellValue();
                                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                                //System.out.print(formatTime.format(date) + "--");
                                customer.setPREFER_END_TIME(formatTime.format(date));
                            }
                        }
                    }
                    // System.out.println();
                }
                customersRequests.add(customer);
            }
        }

        for(int i=ROW_NUMBERS+1;i<=sheet.getLastRowNum();i++){
            Charger charger = new Charger();
            Row row = sheet.getRow(i);
            if(row!=null){
                Iterator iterator = row.cellIterator();
                while(iterator.hasNext()){
                    Cell currentCell = (XSSFCell) iterator.next();
                    if (currentCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
                        if (currentCell.getColumnIndex() == 0) {
                            charger.setCHARGING_POINT_ID((int) currentCell.getNumericCellValue());   //charger_id
                        }
                        if (currentCell.getColumnIndex() == 1) {
                             charger.setCHARGER_TYPE((int)currentCell.getNumericCellValue()); //charger_type
                        }
                    }
                }
                chargers.add(charger); //chargers_list
            }
        }
        System.out.println("Available Chargers are : ");
        chargers.stream().map(Charger::toString).forEach(System.out::println);
        System.out.println("Customer Requests for Chargers are: "+ customersRequests.size());
        customersRequests.stream().map(Customer::toString).forEach(System.out::println);
    }
}
