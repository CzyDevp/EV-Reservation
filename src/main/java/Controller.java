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
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
public class Controller {
    List<Customer> customersRequests = new ArrayList<>();
    static List<Charger> chargers = new ArrayList<>();
    int ROWS_TOTAL_CHARGER=0, ROWS_TOTAL_CUSTOMER=0;
    List<DataObjectEVCompatibility>EV_COMPATIBILITY_DEFAULT;
    static List<CustomerScheduledData>CUSTOMER_SCHEDULED,cs_ccs,cs_sc,chdm,last;
    List<Customer>CUST_NISSAN,CUST_CHEV,CUST_TESLA;
    List<Charger>C_C_S,S_C,Chademo,level2;
    @FXML
    private Button getDataSet;
    String filename;
    public void getFile() throws IOException{
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
        Sheet Charger_Sheet = workbook.getSheetAt(1);  //read charger_name
        ROWS_TOTAL_CUSTOMER= sheet.getPhysicalNumberOfRows();
        ROWS_TOTAL_CHARGER = Charger_Sheet.getPhysicalNumberOfRows();
        System.out.println("Total number of Customer Requests are : "+ (ROWS_TOTAL_CUSTOMER-1));
        System.out.println("Total Number of Chargers available are : "+(ROWS_TOTAL_CHARGER-1));
        Iterator<Row> CHARGER_Iterator = Charger_Sheet.iterator();
        Iterator<Row> iterator = sheet.iterator();
        //Charger reading Start**********************************************
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
                chargers.add(charger);
            }
        }
                                      //*********Charger reading Done********

                                     //Customer reading Start ***************
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
                customersRequests.add(customer);
            }
                                                        //****Customer reading Done  ****
        }
        defaultCompatibility(); //DEFAULT_COMPATABILITY

        //Customer filtering based on EV-TYPE***
        //**Nissan EV'S
        System.out.println("\n*******************Nissan EV's***********************");
        List<Customer>invalidNisaan,invalidChev,invalidTesla;
        CUST_NISSAN = customersRequests.stream()
                .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.NISSAN))
                .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*1.22)
                .collect(Collectors.toList());
        invalidNisaan = customersRequests.stream()
                .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.NISSAN))
                .filter(customer -> customer.getMiles()>=customer.getTimeInMinutes()*1.22)
                .collect(Collectors.toList());
         invalidNisaan.forEach(System.out::println);
         customersRequests.removeAll(invalidNisaan);
        //**Chev EV's
        System.out.println("*******************Chev EV's***********************");
        CUST_CHEV = customersRequests.stream()
                    .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.CHEV))
                    .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.17)
                    .collect(Collectors.toList());
        CUST_CHEV.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_CHEV.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        CUST_CHEV.forEach(System.out::println);
        invalidChev = customersRequests.stream()
                .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.CHEV))
                .filter(customer -> customer.getMiles()>=customer.getTimeInMinutes()*1.22)
                .collect(Collectors.toList());
        invalidChev.forEach(System.out::println);
        customersRequests.removeAll(invalidChev);
        //**Tesla EV's
        System.out.println("*******************Tesla EV's***********************");
        CUST_TESLA = customersRequests.stream()
                     .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.TESLA))
                     .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.67)
                     .collect(Collectors.toList());
        CUST_TESLA.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_TESLA.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        invalidTesla = customersRequests.stream()
                .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.TESLA))
                .filter(customer -> customer.getMiles()>=customer.getTimeInMinutes()*1.22)
                .collect(Collectors.toList());
        invalidTesla.forEach(System.out::println);
        customersRequests.removeAll(invalidTesla);
    //Charger filtering based on TYPE*******
        System.out.println("*******************C_C_S***********************");
        C_C_S = chargers.stream()
                        .filter(charger -> charger.getCh().equals(Charger.charger_name.C_C_S))
                         .collect(Collectors.toList());
        C_C_S.stream().forEach(System.out::println);

        //level2
        System.out.println("*******************level2***********************");
        level2 = chargers.stream()
                .filter(charger -> charger.getCh().equals(Charger.charger_name.LVL_2))
                .collect(Collectors.toList());
        level2.stream().forEach(System.out::println);
        //Chademo
        System.out.println("*******************Chademo***********************");
        Chademo= chargers.stream()
                .filter(charger -> charger.getCh().equals(Charger.charger_name.CHDM))
                .collect(Collectors.toList());
        Chademo.stream().forEach(System.out::println);
        //S_C
        System.out.println("*******************S_C***********************");
         S_C = chargers.stream()
                        .filter(charger -> charger.getCh().equals(Charger.charger_name.S_C))
                        .collect(Collectors.toList());
         S_C.stream().forEach(System.out::println);
        scheduleCustomer(CUST_CHEV); //schedule Chev here first
        scheduleCustomerTesla(CUST_TESLA); //schedule first here Tesla
        //remove assigned one's
        customersRequests.removeAll(remove(cs_ccs));
        customersRequests.removeAll(remove(cs_sc));
        System.out.println("\n\nScheduled Customer Requests Are : ");
        CUSTOMER_SCHEDULED=new ArrayList<>();
        CUSTOMER_SCHEDULED.addAll(cs_ccs);
        CUSTOMER_SCHEDULED.addAll(cs_sc);
        scheduleCustomerTeslaandNissan(customersRequests);
        customersRequests.removeAll(remove(chdm));
        CUSTOMER_SCHEDULED.addAll(chdm);
        CUSTOMER_SCHEDULED.stream().map(CustomerScheduledData::toString).forEach(System.out::println);
        System.out.println("Left are :");
            customersRequests.forEach(System.out::println);

        int count = customersRequests.size();
        //if (count > 0) {
            // Draw Chart
            JFrame demo = new GanttChartView("EV Reservation System");
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setExtendedState(JFrame.MAXIMIZED_BOTH);
            demo.setLocationRelativeTo(null);
            demo.setVisible(true);
            demo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
       // }
        //***Write Output to Excel FIle*******************
      WriteExcelFileExample.getInstance().writeStudentsListToExcel(CUSTOMER_SCHEDULED);
    }
    //****Schedule Customer****
    private void scheduleCustomer(List<Customer> customers){
        cs_ccs = new ArrayList<>();
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customers){
            Charger chargerScheduled = getCharger_CCS(customer);
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
                scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
                scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_End_Time());
                scheduledCustomer.setAssigned_Charger(chargerScheduled);
                cs_ccs.add(scheduledCustomer);
            }
        }
    }
  //tesla
  private void scheduleCustomerTesla(List<Customer> customers){
      cs_sc=new ArrayList<>();
      CustomerScheduledData scheduledCustomer;
      for(Customer customer:customers){
          Charger chargerScheduled = getCharger_SC(customer);
          if(chargerScheduled!=null){
              scheduledCustomer=new CustomerScheduledData();
              scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
              scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
              scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_End_Time());
              scheduledCustomer.setAssigned_Charger(chargerScheduled);
              cs_sc.add(scheduledCustomer);
          }
      }
  }
    //**ASSIGN CHEV TO C_C_S******
    private Charger getCharger_CCS(Customer customer){
        Charger ch=null;
     DataObjectEVCompatibility d1=EV_COMPATIBILITY_DEFAULT.stream()
                                                             .filter(ev->ev.getCharging_Point_Name()
                                                                      .equals(Charger.charger_name.C_C_S))
                                                             .findAny().orElse(null);
            if(d1.getCar_Type().equals(customer.getCar_Type())
                    && (d1.getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()
                    ){
                int projectedTotalMinutes= (int) ((int)customer.getMiles() / d1.getMilePerminute());
                System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
                LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
                System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+d1.getCharging_Point_Name());
                for (Charger charger : C_C_S) {
                    if (charger.getCh().equals(d1.getCharging_Point_Name())) {
                        boolean isSpaceAvailable = true;
                        for (CustomerScheduledData c : cs_ccs) {
                            if (c.getAssigned_Charger().equals(charger)) {
                                if ((c.getPrefer_Fin_Time().isAfter(customer.getPrefer_Start_Time()))
                                        && c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time())) {
                                    if((c.getPrefer_Fin_Time().plusMinutes(projectedTotalMinutes)).isBefore(customer.getPrefer_End_Time())){
                                        customer.setPrefer_Start_Time(c.getPrefer_Fin_Time().plusMinutes(1));
                                        customer.setPrefer_End_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
                                        return charger;
                                    }
                                    else{
                                        isSpaceAvailable=false;
                                    }
                                } else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                                    customer.setPrefer_End_Time(projectedFinishTime);
                                    return charger;
                                }

                            }
                        }
                        if (isSpaceAvailable) {
                            customer.setPrefer_End_Time(projectedFinishTime);
                            return charger;
                        }

                    }
                }
            }
        return  ch;
    }
    //****Assign S_C****
    private Charger getCharger_SC(Customer customer){
        Charger ch=null;
        DataObjectEVCompatibility d1=EV_COMPATIBILITY_DEFAULT.stream()
                .filter(ev->ev.getCharging_Point_Name()
                        .equals(Charger.charger_name.S_C))
                .findAny().orElse(null);
        if(d1.getCar_Type().equals(customer.getCar_Type())
                && (d1.getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()
                ){
            int projectedTotalMinutes= (int) ((int)customer.getMiles() / d1.getMilePerminute());
            //  int totalMinutes = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
            System.out.println("Projected Minutes "+ projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+d1.getCharging_Point_Name());
            for (Charger charger : S_C) {
                if (charger.getCh().equals(d1.getCharging_Point_Name())) {
                    boolean isSpaceAvailable = true;
                    for (CustomerScheduledData c : cs_sc) {
                        if (c.getAssigned_Charger().equals(charger)) {
                            if ((c.getPrefer_Fin_Time().isAfter(customer.getPrefer_Start_Time()))
                                    && c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time())) {
                                if((c.getPrefer_Fin_Time().plusMinutes(projectedTotalMinutes)).isBefore(customer.getPrefer_End_Time())){
                                    customer.setPrefer_Start_Time(c.getPrefer_Fin_Time().plusMinutes(1));
                                    customer.setPrefer_End_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
                                    return charger;
                                }
                                else{
                                       isSpaceAvailable=false;
                                }
                                //isSpaceAvailable = false;
                            } else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                                customer.setPrefer_End_Time(projectedFinishTime);
                                return charger;
                            }

                        }
                    }
                    if (isSpaceAvailable) {
                        customer.setPrefer_End_Time(projectedFinishTime);
                        return charger;
                    }

                }
            }
        }
        return  ch;
    }
    //***Intailize Default Chargers*****
    private void defaultCompatibility() {
        EV_COMPATIBILITY_DEFAULT = new ArrayList<>();
        //***NISSAN****
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN, Charger.charger_name.LVL_2, 0.37));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN, Charger.charger_name.CHDM, 1.22));
        //***CHEV****
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV, Charger.charger_name.LVL_2, 0.40));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV, Charger.charger_name.C_C_S, 2.17));
        //***TESLA****
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.charger_name.LVL_2, 0.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.charger_name.CHDM, 1.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA, Charger.charger_name.S_C, 2.67));

    }
    public static Charger getChargerById(int chargerid){
        Charger charger = null;
        for(Charger ch: chargers){
            if(chargerid==ch.getC_P_Id()){
                charger=ch;
            }
        }
            return charger;
    }
    //remove from initial list
    private List<Customer> remove(List<CustomerScheduledData> csd){
        List<Customer>after_remove=new ArrayList<>();
        if(csd.size()>0){
            csd.stream().forEach(c->{
                customersRequests.stream().forEach(customer -> {
                    if(customer.getCustomer_Id()==c.getCustomer_Id()){
                        after_remove.add(customer);
                    }
                });
            });

        }
        return after_remove;
    }
    private Charger getCharger_chademo(Customer customer){
        Charger ch=null;
        DataObjectEVCompatibility d1=EV_COMPATIBILITY_DEFAULT.stream()
                .filter(ev->ev.getCharging_Point_Name()
                        .equals(Charger.charger_name.CHDM))
                .findAny().orElse(null);
        if(d1.getCar_Type().equals(customer.getCar_Type())
                && (d1.getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()
                ){
            int projectedTotalMinutes= (int) ((int)customer.getMiles() / d1.getMilePerminute());
            //  int totalMinutes = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
            System.out.println("Projected Minutes "+ projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+d1.getCharging_Point_Name());
            for (Charger charger : Chademo) {
                if (charger.getCh().equals(d1.getCharging_Point_Name())) {
                    boolean isSpaceAvailable = true;
                    for (CustomerScheduledData c : chdm) {
                        if (c.getAssigned_Charger().equals(charger)) {
                            if ((c.getPrefer_Fin_Time().isAfter(customer.getPrefer_Start_Time()))
                                    && c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time())) {
                                if((c.getPrefer_Fin_Time().plusMinutes(projectedTotalMinutes)).isBefore(customer.getPrefer_End_Time())){
                                    customer.setPrefer_Start_Time(c.getPrefer_Fin_Time().plusMinutes(1));
                                    customer.setPrefer_End_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
                                    return charger;
                                }
                                else{
                                    isSpaceAvailable=false;
                                }
                                //isSpaceAvailable = false;
                            } else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                                customer.setPrefer_End_Time(projectedFinishTime);
                                return charger;
                            }

                        }
                    }
                    if (isSpaceAvailable) {
                        customer.setPrefer_End_Time(projectedFinishTime);
                        return charger;
                    }

                }
            }
        }
        return  ch;
    }
    //teslaNissan
    private void scheduleCustomerTeslaandNissan(List<Customer> customers){
      chdm=new ArrayList<>();
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customers){
            Charger chargerScheduled = getCharger_chademo(customer);
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
                scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
                scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_End_Time());
                scheduledCustomer.setAssigned_Charger(chargerScheduled);
                chdm.add(scheduledCustomer);
            }
        }
    }



}
