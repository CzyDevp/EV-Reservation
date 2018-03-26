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
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
public class Controller {
    List<Customer> customersRequests = new ArrayList<>();
    List<Customer> customersRequestsfinal = new ArrayList<>();
    List<Customer>CUST_NISSAN=new ArrayList<>();
    List<Customer>CUST_CHEV=new ArrayList<>();
    List<Customer>CUST_TESLA=new ArrayList<>();
    List<Customer>teslaAndNissan=new ArrayList<>();
    static List<Charger> chargers = new ArrayList<>();
    List<DataObjectEVCompatibility>EV_COMPATIBILITY_DEFAULT;
    static List<CustomerScheduledData>CUSTOMER_SCHEDULED,cs_ccs,cs_sc,chdm,last;
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
                chargers.add(charger);
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
                customersRequests.add(customer);
            }
        //******************************************Customer reading Done***********************************************
        }
        defaultCompatibility(); //DEFAULT_COMPATABILITY

        //************************************Customer filtering based on EV-TYPE***************************************
        System.out.println("\n*******************Nissan EV's***********************");
        CUST_NISSAN = customersRequests.stream()
                .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.NISSAN))
                .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*1.22)
                .collect(Collectors.toList());
        CUST_NISSAN.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_NISSAN.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        customersRequestsfinal.addAll(CUST_NISSAN);
        CUST_NISSAN.forEach(System.out::println);
        System.out.println("*******************Chev EV's***********************");
        CUST_CHEV = customersRequests.stream()
                    .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.CHEV))
                    .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.17)
                    .collect(Collectors.toList());
        CUST_CHEV.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_CHEV.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        customersRequestsfinal.addAll(CUST_CHEV);
        CUST_CHEV.forEach(System.out::println);
        System.out.println("*******************Tesla EV's***********************");
        CUST_TESLA = customersRequests.stream()
                     .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.TESLA))
                     .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.67)
                     .collect(Collectors.toList());
        CUST_TESLA.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_TESLA.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        customersRequestsfinal.addAll(CUST_TESLA);
        CUST_TESLA.forEach(System.out::println);
        System.out.println("*****************Valid Customers*******************");
        customersRequestsfinal.forEach(System.out::println);
        //************************************Customer filtering based on EV-TYPE-DONE**********************************

        //************************************Charger filtering based on TYPE*******************************************
        System.out.println("*******************C_C_S***********************");
        C_C_S = chargers.stream()
                        .filter(charger -> charger.getCh().equals(Charger.charger_name.C_C_S))
                        .collect(Collectors.toList());
        C_C_S.stream().forEach(System.out::println);
        System.out.println("*******************level2***********************");
        level2 = chargers.stream()
                         .filter(charger -> charger.getCh().equals(Charger.charger_name.LVL_2))
                         .collect(Collectors.toList());
        level2.stream().forEach(System.out::println);
        System.out.println("*******************Chademo***********************");
        Chademo= chargers.stream()
                         .filter(charger -> charger.getCh().equals(Charger.charger_name.CHDM))
                         .collect(Collectors.toList());
        Chademo.stream().forEach(System.out::println);
        System.out.println("*******************S_C***********************");
         S_C = chargers.stream()
                       .filter(charger -> charger.getCh().equals(Charger.charger_name.S_C))
                       .collect(Collectors.toList());
         S_C.stream().forEach(System.out::println);
        //************************************Charger filtering based on TYPE-Done**************************************

        //************************************Reservation of Tesla on SuperCharger**************************************
            CUSTOMER_SCHEDULED=new ArrayList<>();
            List<CustomerScheduledData> tesla_only_sc;
            tesla_only_sc= scheduleCustomerTesla(CUST_TESLA,0); //schedule first here Tesla
            CUST_TESLA.removeAll(remove(tesla_only_sc)); //remove assigned to SC
            customersRequestsfinal.removeAll(remove(tesla_only_sc));  //tesla only
            CUSTOMER_SCHEDULED.addAll(tesla_only_sc);   //add tesla SC
            S_C.remove(0);
            if(CUST_TESLA.size()>0 && S_C.size()>0){
                for(int i=0;i<S_C.size();i++) {
                      tesla_only_sc = scheduleCustomerTesla(CUST_TESLA, i);
                      CUST_TESLA.removeAll(remove(tesla_only_sc)); //remove assigned to SC
                      customersRequestsfinal.removeAll(remove(tesla_only_sc));  //tesla only
                      CUSTOMER_SCHEDULED.addAll(tesla_only_sc);   //add tesla SC
                }
            }
        //************************************Reservation of Tesla on SuperCharger-Done*********************************

        //************************************Reservation of Chev on Combo-Super-Charger*****************************
        List<CustomerScheduledData>chev_only;
        chev_only=scheduleCustomerChev(CUST_CHEV,0); //schedule Chev here first
        CUST_CHEV.removeAll(remove(chev_only)); //remove assigned to CCS
        customersRequestsfinal.removeAll(remove(chev_only)); //chev only
        CUSTOMER_SCHEDULED.addAll(chev_only);  //add chev CCS
        C_C_S.remove(0);
        if(CUST_CHEV.size()>0 && S_C.size()>0){
            for(int i=0;i<C_C_S.size();i++) {
                chev_only = scheduleCustomerTesla(CUST_CHEV, i);
                CUST_CHEV.removeAll(remove(chev_only)); //remove assigned to SC
                customersRequestsfinal.removeAll(remove(chev_only));  //tesla only
                CUSTOMER_SCHEDULED.addAll(chev_only);   //add tesla SC
            }
        }
        System.out.println("Left Chev after Chdemo");
        CUST_CHEV.forEach(System.out::println);
        //************************************Reservation of Chev on Combo-Super-Charger-Done***************************

        //************************************Reservation of Tesla and Nissan on Chademo********************************
        System.out.println("*****************List of Nissan and Tesla for Chdemo**************************************");
        teslaAndNissan.addAll(CUST_TESLA);  //list of nissan and tesla
        teslaAndNissan.addAll(CUST_NISSAN);  //list of nissan and tesla
        teslaAndNissan.sort(Comparator.comparing(Customer::getMiles).reversed().thenComparing(Customer::getPrefer_Start_Time));
        teslaAndNissan.forEach(System.out::println);  //tesla and nissan for chademo
        scheduleCustomerTeslaAndNissan(teslaAndNissan); //tesla nissan
        customersRequestsfinal.removeAll(remove(chdm));  //tesla nissan
        CUSTOMER_SCHEDULED.addAll(chdm); //tesla nissan
        //************************************Reservation of Tesla and Nissan on Chademo-Done***************************

        //************************************Reservation of Tesla,Nissan,Chev on Level2********************************
        System.out.println("List for last");
        customersRequestsfinal.forEach(System.out::println);
        scheduleCustomerTeslaNissanChev(customersRequestsfinal);
        CUSTOMER_SCHEDULED.addAll(last);
        customersRequestsfinal.removeAll(remove(last));
        //************************************Reservation of Tesla,Nissan,Chev on Level2-Done***************************

        System.out.println("\n\n******************Scheduled Customer Requests Are***********************");
        CUSTOMER_SCHEDULED.stream().map(CustomerScheduledData::toString).forEach(System.out::println);
        System.out.println("Not Reserved Left are :");
        customersRequestsfinal.forEach(System.out::println);

            JFrame demo = new GanttChartView("EV Reservation System");
            demo.pack();
            RefineryUtilities.centerFrameOnScreen(demo);
            demo.setExtendedState(JFrame.MAXIMIZED_BOTH);
            demo.setLocationRelativeTo(null);
            demo.setVisible(true);
            demo.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        //****************************************Write Output to Excel FIle********************************************
            WriteExcelFileExample.getInstance().writeStudentsListToExcel(CUSTOMER_SCHEDULED);
    }

    //************************************************Schedule Customer Chev********************************************
    private List<CustomerScheduledData> scheduleCustomerChev(List<Customer> customers,int i){
        cs_ccs = new ArrayList<>();
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customers){
            Charger chargerScheduled = getCharger_CCS(customer,C_C_S.get(i));
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
                scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
                int projectedTotalMinutes= (int) ((int)customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(3).getMilePerminute());
                scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
                scheduledCustomer.setAssigned_Charger(chargerScheduled);
                cs_ccs.add(scheduledCustomer);
            }
        }
        return  cs_ccs;
    }
    //************************************************Schedule Customer Chev********************************************

   //************************************************Schedule Customer Tesla********************************************
  private List<CustomerScheduledData> scheduleCustomerTesla(List<Customer> customers,int i){
      cs_sc=new ArrayList<>();
      CustomerScheduledData scheduledCustomer;
      for(Customer customer:customers){
          Charger chargerScheduled = getCharger_SC(customer, S_C.get(i));
          if(chargerScheduled!=null){
              scheduledCustomer=new CustomerScheduledData();
              scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
              scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
              int projectedTotalMinutes= (int) ((int)customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(6).getMilePerminute());
              scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
              scheduledCustomer.setAssigned_Charger(chargerScheduled);
              cs_sc.add(scheduledCustomer);
          }
      }
    return cs_sc;
  }
  //*******************************************Schedule Customer Tesla-Done*********************************************

  //*****************************************Schedule Customer Tesla $ Nissan*****************************************
   private void scheduleCustomerTeslaAndNissan(List<Customer> customers){
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
  //*****************************************Schedule Customer Tesla $ Nissan Done**************************************


  //*****************************************Schedule Customer All Last***********************************************
    private void scheduleCustomerTeslaNissanChev(List<Customer> customers){
        last=new ArrayList<>();
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customers){
            Charger chargerScheduled = getChargerlast(customer);
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
                scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
                scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_End_Time());
                scheduledCustomer.setAssigned_Charger(chargerScheduled);
                last.add(scheduledCustomer);
            }
        }
    }
    //*****************************************Schedule Customer All Last***********************************************

    //**ASSIGN CHEV TO C_C_S******
    private Charger getCharger_CCS(Customer customer,Charger charger){
        Charger ch=null;
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpend = customer.getPrefer_End_Time();
        int projectedTotalMinutes= (int) ((int)customer.getMiles() /  EV_COMPATIBILITY_DEFAULT.get(3).getMilePerminute());
                System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
                LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
                System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+EV_COMPATIBILITY_DEFAULT.get(3).getCharging_Point_Name());
                tmpend = projectedFinishTime;
                        boolean isSpaceAvailable = true;
                        boolean islistempty = true;
                        for (CustomerScheduledData ccs : cs_ccs) {
                            islistempty=false;
                            if (ccs.getAssigned_Charger().equals(charger)) {
                                System.out.println("stag -1: S.ID: "+ccs.getCustomer_Id()+", S.ID: "
                                        +customer.getCustomer_Id()
                                        +", S.End_Time: "
                                        +ccs.getPrefer_Fin_Time()
                                        +", C.End_Time: "+customer.getPrefer_End_Time());
                                if (ccs.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time()) ||
                                        ccs.getPrefer_Fin_Time().equals(customer.getPrefer_End_Time()) ) {
                                    tmpStart = ccs.getPrefer_Fin_Time().plusMinutes(1);
                                    tmpend = tmpStart.plusMinutes(projectedTotalMinutes);
                                    System.out.println(customer.toString());
                                    System.out.println("stage 0: S.ID: "+ccs.getCustomer_Id()
                                            +", C.ID: "+customer.getCustomer_Id()
                                            +"tmpStart: "+tmpStart+", tmpend: "+tmpend);
                                    if(tmpStart.isAfter(customer.getPrefer_Start_Time()) &&
                                            tmpend.isBefore(customer.getPrefer_End_Time()))
                                    {
                                        customer.setPrefer_Start_Time(tmpStart);
                                        ch = charger;
                                        isSpaceAvailable=false;
                                    }
                                    else
                                    {
                                        if(ccs.getPrefer_Fin_Time().isAfter(projectedFinishTime))
                                        {
                                            return null;
                                        }else if(customer.getPrefer_End_Time().isBefore(tmpend))
                                        {
                                            return null;
                                        }
                                        else
                                        {
                                            isSpaceAvailable = true;
                                        }
                                    }
                                }
                                else if (projectedFinishTime.isBefore(ccs.Prefer_Start_Time)) {
                                    tmpStart = ccs.getPrefer_Fin_Time().plusMinutes(1);
                                    customer.setPrefer_Start_Time(tmpStart);
                                    ch = charger;
                                }

                            }
                        }
                        if (isSpaceAvailable) {
                            customer.setPrefer_End_Time(projectedFinishTime);
                            return charger;
                        }
                    if (islistempty) {
                        System.out.println("ID stag 4: " + customer.getCustomer_Id());
                        return charger;
                    }



        return  ch;
    }

    //********************************************************Assign S_C-Tesla***************************************
    private Charger getCharger_SC(Customer customer, Charger charger){
        Charger ch=null;
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpend = customer.getPrefer_End_Time();
        if((EV_COMPATIBILITY_DEFAULT.get(6).getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()){
            int projectedTotalMinutes= (int) ((int)customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(6).getMilePerminute());
            System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()
                                    +" Customer's minutes "+ customer.getTimeInMinutes());
            System.out.println("Projected Minutes "+ projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("Projected Finish Times is "+projectedFinishTime
                                + " With Charger "+EV_COMPATIBILITY_DEFAULT.get(6).getCharging_Point_Name());
            tmpend = projectedFinishTime;
                    if (charger.getCh().equals(EV_COMPATIBILITY_DEFAULT.get(6).getCharging_Point_Name())) {
                    boolean isSpaceAvailable = true;
                    boolean islistempty = true;
                    for (CustomerScheduledData c : cs_sc) {
                        islistempty = false;
                        if (c.getAssigned_Charger().equals(charger)) {
                            System.out.println("stag -1: S.ID: "+c.getCustomer_Id()+", S.ID: "
                                                +customer.getCustomer_Id()
                                                +", S.End_Time: "
                                                +c.getPrefer_Fin_Time()
                                                +", C.End_Time: "+customer.getPrefer_End_Time());
                            if (c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time()) ||
                                    c.getPrefer_Fin_Time().equals(customer.getPrefer_End_Time()) ) {
                                tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                                tmpend = tmpStart.plusMinutes(projectedTotalMinutes);
                                System.out.println(customer.toString());
                                System.out.println("stage 0: S.ID: "+c.getCustomer_Id()
                                                    +", C.ID: "+customer.getCustomer_Id()
                                                    +"tmpStart: "+tmpStart+", tmpend: "+tmpend);
                                if(tmpStart.isAfter(customer.getPrefer_Start_Time()) &&
                                        tmpend.isBefore(customer.getPrefer_End_Time()))
                                {
                                    customer.setPrefer_Start_Time(tmpStart);
                                    System.out.println("stag 1: S.ID: "+c.getCustomer_Id()
                                                        +", C.ID: "+customer.getCustomer_Id());
                                    ch = charger;
                                    isSpaceAvailable=false;
                                }else
                                {
                                    if(c.getPrefer_Fin_Time().isAfter(projectedFinishTime))
                                    {
                                        return null;
                                    }else if(customer.getPrefer_End_Time().isBefore(tmpend))
                                    {
                                        return null; // or false
                                    }
                                    else
                                    {
                                        isSpaceAvailable = true;
                                    }
                                }
                            }
                            else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                                tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                                customer.setPrefer_Start_Time(tmpStart);
                                System.out.println("stag 2: S.ID: "+c.getCustomer_Id()
                                                    +", S.ID: "+customer.getCustomer_Id());
                                ch = charger;
                            }

                        }
                    }
                    if (isSpaceAvailable) {
                            tmpStart = customer.getPrefer_Start_Time().plusMinutes(1);
                            //customer.setPrefer_End_Time(projectedFinishTime);
                            System.out.println("ID stag 3: " + customer.getCustomer_Id());
                            ch = charger;
                    }
                    if (islistempty) {
                            System.out.println("ID stag 4: " + customer.getCustomer_Id());
                            return charger;
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

    private Charger getChargerlast(Customer customer){
        Charger ch=null;
        DataObjectEVCompatibility d1=EV_COMPATIBILITY_DEFAULT.stream()
                .filter(ev->ev.getCharging_Point_Name()
                        .equals(Charger.charger_name.LVL_2))
                .findAny().orElse(null);
        if(d1.getCar_Type().equals(customer.getCar_Type())
                && (d1.getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()
                ){
            int projectedTotalMinutes= (int) ((int)customer.getMiles() / d1.getMilePerminute());
            System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
            System.out.println("Projected Minutes "+ projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("Projected Finish Times is "+projectedFinishTime + " With Charger "+d1.getCharging_Point_Name());
            for (Charger charger : level2) {
                if (charger.getCh().equals(d1.getCharging_Point_Name())) {
                    boolean isSpaceAvailable = true;
                    for (CustomerScheduledData c : last) {
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

}
