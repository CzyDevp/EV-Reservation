import javafx.fxml.FXML;
import javafx.scene.control.Button;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

public class Controller {
    @FXML private Button getDataSet;
    String filename;
    public void getFile() throws IOException {
        processFile();
//                FileChooser fileChooser = new FileChooser();
//                FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("excelfiles (*.xlsx)", "*.xlsx");
//                fileChooser.getExtensionFilters().add(extFilter);
//                File file = fileChooser.showOpenDialog(null);
//                if(file!=null){
//                    filename = file.toString();
//                    System.out.println(file);
//                    processFile();
//                    Stage stage = (Stage) getDataSet.getScene().getWindow();
//                    stage.close();
//                  //  exit();
//                }
//                else{
//                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
//                    alert.setTitle("Oops!!");
//                    alert.setHeaderText("Info");
//                    alert.setContentText("Please provide a valid file");
//                    alert.show();
//                }
   }
    public  void  processFile() throws IOException {
       // FileReader fileReader = new FileReader(filename);
        FileInputStream fis = new FileInputStream(new File("D:\\play-java-starter-example\\EV\\cars.xlsx"));
        Workbook workbook = new XSSFWorkbook(fis);
        int sheets = workbook.getNumberOfSheets();
        System.out.println(sheets);
        Sheet sheet = workbook.getSheetAt(0);
        Iterator<Row>iterator = sheet.iterator();
        while(iterator.hasNext()){
            Row currentRow = iterator.next();
            Iterator<Cell>cellIterator = currentRow.iterator();
            while(cellIterator.hasNext()){
                Cell currentCell = cellIterator.next();
                if(currentCell.getCellTypeEnum().equals(CellType.STRING)){
                    System.out.print(currentCell.getStringCellValue().trim()+"       ");
                }
                 else if(currentCell.getCellTypeEnum().equals(CellType.NUMERIC)){
                    System.out.print(currentCell.getNumericCellValue()+"--");
                }
               // System.out.println();
            }
            System.out.println();
        }
    }
}
