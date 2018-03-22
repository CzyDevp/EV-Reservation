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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Controller {
    List<Customer> customersRequests = new ArrayList<>();
    List<Charger> chargers = new ArrayList<>();
    int ROWS_TOTAL_CHARGER=0, ROWS_TOTAL_CUSTOMER=0;
    List<Customer>CUST_NISSAN,CUST_CHEV,CUST_TESLA;
    List<Charger>LEVEL_2,CHADEMO,COMBO,SUPER_CHARGER;
    List<DataObjectEVCompatibility>EV_COMPATIBILITY_DEFAULT;
    List<CustomerScheduledData>CUSTOMER_SCHEDULED;
    @FXML
    private Button getDataSet;
    String filename;
    public void getFile() throws IOException, ParseException {
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
        }
        else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Oops!!");
            alert.setHeaderText("Info");
            alert.setContentText("Please provide a valid file");
            alert.show();
        }
    }
    public void processFile() throws IOException{
        FileInputStream fis = new FileInputStream(new File(filename));
        Workbook workbook = new XSSFWorkbook(fis);
        Sheet sheet = workbook.getSheetAt(0);   //read customers
        Sheet Charger_Sheet = workbook.getSheetAt(1);  //read chargers
        ROWS_TOTAL_CUSTOMER= sheet.getPhysicalNumberOfRows();
        ROWS_TOTAL_CHARGER = Charger_Sheet.getPhysicalNumberOfRows();
        System.out.println("Total number of Customer Requests are : "+ (ROWS_TOTAL_CUSTOMER-1));
        System.out.println("Total Number of Chargers available are : "+(ROWS_TOTAL_CHARGER-1));
        Iterator<Row> CHARGER_Iterator = Charger_Sheet.iterator();
        Iterator<Row> iterator = sheet.iterator();
        //******************************************Charger reading***************************************************\\
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
                            charger.setCHARGING_POINT_ID((int) currentCell.getNumericCellValue());   //charger id
                        }
                        if(currentCell.getColumnIndex()==1){
                            //charger.setCHARGER_TYPE((int)currentCell.getNumericCellValue());
                            charger.setCh((int)currentCell.getNumericCellValue());
                        }
                    }
                }
                chargers.add(charger);
            }
        }
    //********************************************************Charger reading*****************************************\\
    //********************************************************Customer reading*****************************************\\
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
                            customer.setCUSTOMER_ID((int) currentCell.getNumericCellValue());   //customer id
                        }
                        if (currentCell.getColumnIndex() == 3) {
                            customer.setMILES(currentCell.getNumericCellValue()); //miles
                        }
                        if (currentCell.getColumnIndex() == 4) {

                            customer.setEv_car((int)currentCell.getNumericCellValue());
                        }
                        if (DateUtil.isCellDateFormatted(currentCell)) {
                            if (currentCell.getColumnIndex() == 1) {
                                SimpleDateFormat sdf2=new SimpleDateFormat("HH:mm");

                                LocalTime time = LocalTime.parse(sdf2.format(currentCell.getDateCellValue()));
                                //System.out.println("date is "+time);
                                customer.setPREFER_START_TIME(time);
                            }
                            if (currentCell.getColumnIndex() == 2) {

                                SimpleDateFormat formatTime = new SimpleDateFormat("HH:mm");
                                customer.setPREFER_END_TIME(LocalTime.parse(formatTime.format(currentCell.getDateCellValue())));
                            }
                        }
                    }
                }
                customersRequests.add(customer);
            }
        }
    //********************************************************Customer reading*****************************************\\
        System.out.println("Available Chargers are : ");
        chargers.stream().map(Charger::toString).forEach(System.out::println);
        System.out.println("Customer Requests for Chargers are: "+ customersRequests.size());
        customersRequests.stream().map(Customer::toString).forEach(System.out::println);

        //********************************************Customer filtering based on EV-TYPE******************************\\
        //**Nissan EV'S
        System.out.println("\n\n\n*******************Nissan EV's***********************");
        CUST_NISSAN = customersRequests.stream()
                      .filter(customer -> customer.getEv_car().equals(Customer.EV_CAR.NISSAN))
                       .collect(Collectors.toList());
        CUST_NISSAN.forEach(System.out::println);
        //**Chev EV's
        System.out.println("\n\n*******************Chev EV's***********************");
        CUST_CHEV = customersRequests.stream()
                    .filter(customer -> customer.getEv_car().equals(Customer.EV_CAR.CHEV))
                    .collect(Collectors.toList());
        CUST_CHEV.forEach(System.out::println);
        //**Tesla EV's
        System.out.println("\n\n*******************Tesla EV's***********************");
        CUST_TESLA = customersRequests.stream()
                     .filter(customer -> customer.getEv_car().equals(Customer.EV_CAR.TESLA))
                     .collect(Collectors.toList());
        CUST_TESLA.forEach(System.out::println);

        //********************************************CHARGER filtering based on CHARGER-TYPE******************************\\
        System.out.println("\n\n*******************LEVEL_2***********************");
        LEVEL_2 = chargers.stream()
                  .filter(charger -> charger.getCh().equals(Charger.chargers.LEVEL_2))
                  .collect(Collectors.toList());
        LEVEL_2.forEach(System.out::println);
        System.out.println("\n\n*******************CHADEMO***********************");
        CHADEMO = chargers.stream()
                .filter(charger -> charger.getCh().equals(Charger.chargers.CHADEMO))
                .collect(Collectors.toList());
        CHADEMO.forEach(System.out::println);
        System.out.println("\n\n*******************COMBO***********************");
        COMBO=chargers.stream()
                .filter(charger -> charger.getCh().equals(Charger.chargers.COMBO_CHARGER_SYSTEM))
                .collect(Collectors.toList());
        COMBO.forEach(System.out::println);
        System.out.println("\n\n*******************SUPER_CHARGER***********************");
        SUPER_CHARGER =chargers.stream()
                .filter(charger -> charger.getCh().equals(Charger.chargers.SUPER_CHARGER))
                .collect(Collectors.toList());
        SUPER_CHARGER.forEach(System.out::println);
        //DEFAULT_COMPATABILITY_CALL
        defaultCompatibility();
        //getev
       getCharger();
    }

    private void defaultCompatibility() {
        EV_COMPATIBILITY_DEFAULT = new ArrayList<>();
        //************************************NISSAN***********************************\\
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN, Charger.chargers.LEVEL_2, 0.37));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN, Charger.chargers.CHADEMO, 1.20));
        //************************************CHEV***********************************\\
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV, Charger.chargers.LEVEL_2, 0.40));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV, Charger.chargers.COMBO_CHARGER_SYSTEM, 2.20));
        //************************************TESLA***********************************\\
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.chargers.LEVEL_2, 0.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.chargers.CHADEMO, 1.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.chargers.SUPER_CHARGER, 2.67));

    }
    //**********************************FIND_COMPATIBLE***************************************************************\\
    private ChargerScheduled getCharger(){
            //CUSTOMER_SCHEDULED= new ArrayList<>();
            customersRequests.stream().forEach(customer -> {
            EV_COMPATIBILITY_DEFAULT.stream().forEach(dataObjectEVCompatibility -> {
                if((dataObjectEVCompatibility.getCar_Type().equals(customer.getEv_car()))
                    && dataObjectEVCompatibility.getMilePerminute()*customer.getTimeInMinutes()>=customer.getMILES() ){
                    System.out.println("Customer's minutes "+ customer.getTimeInMinutes());
                    int preferredFinisTime= (int) (customer.getMILES() / dataObjectEVCompatibility.getMilePerminute());
                    int totalMinutes = customer.getPREFER_START_TIME().getMinute()+preferredFinisTime;

                    //*******************finis time calcultion********************
                    int minutes=0, hour=0;
                    if(totalMinutes>59){
                       minutes= totalMinutes%60;
                       hour = totalMinutes/60;
                    }
                    else{
                          minutes=totalMinutes;
                    }
                    LocalTime finishTim = customer.getPREFER_START_TIME().plusHours(hour).plusMinutes(minutes);
//                    if((finishTim.isBefore(customer.getPREFER_END_TIME()))
//                         &&(finishTim.isAfter(customer.getPREFER_START_TIME()))){
//                        CustomerScheduledData customerScheduledData = new CustomerScheduledData();
//                        customerScheduledData.setCUSTMOER_ID(customer.getCUSTOMER_ID());
//                        customerScheduledData.setPREFER_FINISH_TIME(finishTim);
//                        customerScheduledData.setPREFER_START_TIME(customer.getPREFER_START_TIME());
//
//                        CUSTOMER_SCHEDULED.add();
//                    }
                    System.out.println("Finish Times is "+finishTim);

                    //*******************finis time calcultion********************

                }
            });
           });
            return  null;
    }

    private void scheduleCustomer(){
        CUSTOMER_SCHEDULED= new ArrayList<>();

    }
}
