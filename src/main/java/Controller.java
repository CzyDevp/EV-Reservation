import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
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
    @FXML private Button getDataSet;
    String filename;
    public void getFile() throws IOException {
                FileChooser fileChooser = new FileChooser();
                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("excelfiles (*.xlsx)", "*.xlsx");
                fileChooser.getExtensionFilters().add(extFilter);
                File file = fileChooser.showOpenDialog(null);
                if(file!=null){
                    filename = file.toString();
                    System.out.println(file);
                    processFile();
                    Stage stage = (Stage) getDataSet.getScene().getWindow();
                    stage.close();
                  //  exit();
                }
                else{
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Oops!!");
                    alert.setHeaderText("Info");
                    alert.setContentText("Please provide a valid file");
                    alert.show();
                }
   }
    public  void  processFile() throws IOException {
        //int EMPTY_ROWS=0;

       // FileReader fileReader = new FileReader(filename);
        FileInputStream fis = new FileInputStream(new File(filename));
        Workbook workbook = new XSSFWorkbook(fis);
        int sheets = workbook.getNumberOfSheets();
        //System.out.println("Number of Sheeets- " +sheets);
        Sheet sheet = workbook.getSheetAt(1);
        Iterator<Row>iterator = sheet.iterator();
        while(iterator.hasNext()) {
            Customer customer = new Customer();
            Row currentRow = iterator.next();
            Iterator<Cell> cellIterator = currentRow.iterator();
            if (currentRow.getRowNum() == 0) {

            } else {
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                    if (currentCell.getCellTypeEnum().equals(CellType.STRING)) {
                        // System.out.print(currentCell.getStringCellValue()+"--");
                    } else if (currentCell.getCellTypeEnum().equals(CellType.NUMERIC)) {
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
                        } else {
                           // System.out.print(currentCell.getNumericCellValue() + "--");
                        }
                    }
                    // System.out.println();
                }
                customersRequests.add(customer);
                //System.out.println();
            }
        }
        customersRequests.stream().map(Customer::toString).forEach(System.out::println);
        //System.out.println("Empty Rows are: "+EMPTY_ROWS);
    }
}
