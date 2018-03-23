import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.ui.RefineryUtilities;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
public class Controller {
    List<Customer> customersRequests = new ArrayList<>();
    List<Charger> chargers = new ArrayList<>();
    int ROWS_TOTAL_CHARGER=0, ROWS_TOTAL_CUSTOMER=0;
    List<DataObjectEVCompatibility>EV_COMPATIBILITY_DEFAULT;
    static List<CustomerScheduledData>CUSTOMER_SCHEDULED;
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

        //******************************************Charger reading Start**********************************************\\
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
    //********************************************************Charger reading Done*************************************\\

    //*************************************************Customer reading Start *****************************************\\
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
                            customer.setMILES((int)currentCell.getNumericCellValue()); //miles
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
    //*************************************************Customer reading Done  *****************************************\\
        }
     //**************************************************DEFAULT_COMPATABILITY_CALL*************************************\\
        defaultCompatibility();
    //*****************************************************getValidCstomers********************************************\\
        //System.out.println("\n\nValid Customer Requests Are : ");
        //getValidRangeCustomers().forEach(System.out::println);
    //******************************************************Scheduled Customers****************************************\\
        scheduleCustomer();
        System.out.println("\n\nScheduled Customer Requests Are : ");
        CUSTOMER_SCHEDULED.stream().map(CustomerScheduledData::toString).forEach(System.out::println);
        int count = customersRequests.size();
        if (count > 0) {
            // Draw Chart
            JFrame demo = new GanttChartView("EV Car Scheduler - Gantt Chart");
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setExtendedState(JFrame.MAXIMIZED_BOTH);
            demo.setLocationRelativeTo(null);
            demo.setVisible(true);
            demo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        //*******************************************Write Output to Excel FIle********************************************\\
      WriteExcelFileExample.getInstance().writeStudentsListToExcel(CUSTOMER_SCHEDULED);
    }

    //*******************************************Schedule Customer****************************************************\\
    private void scheduleCustomer(){
        CUSTOMER_SCHEDULED= new ArrayList<>();
        List<Customer> VALID_Cust=getValidRangeCustomers();
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customersRequests){
            Charger chargerScheduled = getCharger(customer);
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCUSTMOER_ID(customer.getCUSTOMER_ID());
                scheduledCustomer.setPREFER_START_TIME(customer.getPREFER_START_TIME());
                scheduledCustomer.setPREFER_FINISH_TIME(customer.getPREFER_END_TIME());
                scheduledCustomer.setASSIGNED_CHARGER(chargerScheduled);
                CUSTOMER_SCHEDULED.add(scheduledCustomer);
            }
        }
    }


    //**********************************FIND_COMPATIBLE***************************************************************\\
    private Charger getCharger(Customer customer){
        Charger ch=null;
        for(DataObjectEVCompatibility dataObjectEVCompatibility:EV_COMPATIBILITY_DEFAULT){
            if(dataObjectEVCompatibility.getCar_Type().equals(customer.getCAR_TYPE())
                    && (dataObjectEVCompatibility.getMilePerminute()*customer.getTimeInMinutes())>=customer.getMILES()
                        ){
                int projectedTotalMinutes= (int) ((int)customer.getMILES() / dataObjectEVCompatibility.getMilePerminute());
                int totalMinutes = customer.getPREFER_START_TIME().getMinute()+projectedTotalMinutes;
                System.out.println("ID "+customer.getCUSTOMER_ID()+" Car-Type "+customer.getCAR_TYPE()+" Customer's minutes "+ customer.getTimeInMinutes());
                System.out.println("Total Minutes "+totalMinutes);
                LocalTime projectedFinishTime = getProjectedTime(totalMinutes,customer.getPREFER_START_TIME());
                System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+dataObjectEVCompatibility.getCharging_Point_Name());
                    for (Charger charger : chargers) {
                        if (charger.getCh().equals(dataObjectEVCompatibility.getCharging_Point_Name())) {
                            boolean isSpaceAvailable = true;
                            for (CustomerScheduledData c : CUSTOMER_SCHEDULED) {
                                if (c.getASSIGNED_CHARGER().equals(charger)) {
                                    if (c.getPREFER_FINISH_TIME().isAfter(customer.getPREFER_START_TIME())) {
                                        isSpaceAvailable = false;
                                    } else if (projectedFinishTime.isBefore(c.PREFER_START_TIME)) {
                                        // isSpaceAvailable=true;
                                        customer.setPREFER_END_TIME(projectedFinishTime);
                                        return charger;
                                    }

                                }
                            }
                            if (isSpaceAvailable) {
                                customer.setPREFER_END_TIME(projectedFinishTime);
                                return charger;
                            }

                        }
                    }
                }


        }

        return  ch;
    }

    //***********************************get projected Time***********************************************************\\
    private LocalTime getProjectedTime(int totalMinutes,LocalTime prefferedStartTime){
        int minutes=0, hour=0;
        if(totalMinutes>59){
            minutes= totalMinutes%60;
            hour = totalMinutes/60;
        }
        else{
            minutes=totalMinutes;
        }
        return prefferedStartTime.plusHours(hour).plusMinutes(minutes);
    }

    //**********************************Intailize Default Chargers****************************************************\\

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


    //**********************************Valid Customers Based on Miles************************************************\\
    private List<Customer> getValidRangeCustomers(){
        List<Customer> VALID_CUSTOMERS=new ArrayList<>();
        for(Customer customer:customersRequests){
            for(DataObjectEVCompatibility dataObjectEVCompatibility: EV_COMPATIBILITY_DEFAULT){
                if((dataObjectEVCompatibility.getCar_Type().equals(customer.getCAR_TYPE()))
                        && (((dataObjectEVCompatibility.getMilePerminute())*(customer.getTimeInMinutes()))>=customer.getMILES())){
                    int projectedTotalMinutes= (int) ((int)customer.getMILES() / dataObjectEVCompatibility.getMilePerminute());
                    int totalMinutes = customer.getPREFER_START_TIME().getMinute()+projectedTotalMinutes;
                    LocalTime projectedFinishTime = getProjectedTime(totalMinutes,customer.getPREFER_START_TIME());
                    if(projectedFinishTime.isBefore(customer.PREFER_END_TIME)) {
                                       VALID_CUSTOMERS.add(customer);
                    }
                }
            }

        }
        return VALID_CUSTOMERS.stream().collect(Collectors.toList());
    }

}
