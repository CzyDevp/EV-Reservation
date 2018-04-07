import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.jfree.ui.RefineryUtilities;
import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
public class Controller {
    static List<Customer> customersRequests = new ArrayList<>();
    static List<Charger> chargers = new ArrayList<>();
    static List<CustomerScheduledData>CUSTOMER_SCHEDULED,cs_ccs,cs_sc,chdm,last;
    List<Customer> customersRequestsfinal = new ArrayList<>();
    List<Customer>CUST_NISSAN=new ArrayList<>();
    List<Customer>CUST_CHEV=new ArrayList<>();
    List<Customer>CUST_TESLA=new ArrayList<>();
    List<Customer>teslaAndNissan=new ArrayList<>();
    List<DataObjectEVCompatibility>EV_COMPATIBILITY_DEFAULT;
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
        readDataset.readFile(fis); //readfile
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
        System.out.println("Tesla Number of tesla users are: "+ CUST_NISSAN.size());
        //CUST_NISSAN.forEach(System.out::println);
        System.out.println("*******************Chev EV's***********************");
        CUST_CHEV = customersRequests.stream()
                    .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.CHEV))
                    .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.17)
                    .collect(Collectors.toList());
        CUST_CHEV.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_CHEV.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        customersRequestsfinal.addAll(CUST_CHEV);
        System.out.println("Tesla Number of chev users are: "+ CUST_CHEV.size());
        //CUST_CHEV.forEach(System.out::println);
        System.out.println("*******************Tesla EV's***********************");
        CUST_TESLA = customersRequests.stream()
                     .filter(customer -> customer.getCar_Type().equals(Customer.EV_CAR.TESLA))
                     .filter(customer -> customer.getMiles()<=customer.getTimeInMinutes()*2.67)
                     .collect(Collectors.toList());
        CUST_TESLA.sort(Comparator.comparing(Customer::getMiles).reversed());
        CUST_TESLA.sort(Comparator.comparing(Customer::getPrefer_Start_Time));
        customersRequestsfinal.addAll(CUST_TESLA);
        System.out.println("Tesla Number of tesla users are: "+ CUST_TESLA.size());
       // CUST_TESLA.forEach(System.out::println);
        System.out.println("*****************Valid Customers*******************");
        System.out.println("Total valid number is-: "+customersRequestsfinal.size());
        //customersRequestsfinal.forEach(System.out::println);
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
       /* List<CustomerScheduledData>chev_only;
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
        System.out.println(CUST_CHEV.size());*/
        //CUST_CHEV.forEach(System.out::println);
        //************************************Reservation of Chev on Combo-Super-Charger-Done***************************

        //************************************Reservation of Tesla and Nissan on Chademo********************************
        System.out.println("*****************List of Nissan and Tesla for Chdemo**************************************");
  /*      List<CustomerScheduledData>teslaNissanOnly;
        teslaAndNissan.addAll(CUST_TESLA);  //list of nissan and tesla
        teslaAndNissan.addAll(CUST_NISSAN);  //list of nissan and tesla
        teslaAndNissan.sort(Comparator.comparing(Customer::getMiles)
                           .reversed()
                           .thenComparing(Customer::getPrefer_Start_Time));
        //teslaAndNissan.forEach(System.out::println);  //tesla and nissan for chademo
        System.out.println(teslaAndNissan.size());
        teslaNissanOnly=scheduleCustomerTeslaAndNissan(teslaAndNissan,0); //tesla nissan
        teslaAndNissan.removeAll(teslaNissanOnly);
        customersRequestsfinal.removeAll(remove(teslaNissanOnly));  //tesla nissan
        CUSTOMER_SCHEDULED.addAll(teslaNissanOnly); //tesla nissan
        Chademo.remove(0);
        if(teslaAndNissan.size()>0 && Chademo.size()>0){
            for(int i=0;i<C_C_S.size();i++) {
                teslaNissanOnly = scheduleCustomerTeslaAndNissan(teslaAndNissan, i);
                teslaAndNissan.removeAll(remove(teslaNissanOnly)); //remove assigned to SC
                customersRequestsfinal.removeAll(remove(teslaNissanOnly));  //tesla only
                CUSTOMER_SCHEDULED.addAll(teslaNissanOnly);   //add tesla SC
            }
        }*/
        //************************************Reservation of Tesla and Nissan on Chademo-Done***************************

        //************************************Reservation of Tesla,Nissan,Chev on Level2********************************
        System.out.println("***********************************List for last******************************************");
       /* List<CustomerScheduledData>finalList;
       // customersRequestsfinal.forEach(System.out::println);
        System.out.println(customersRequestsfinal.size());
        customersRequestsfinal.sort(Comparator.comparing(Customer::getMiles)
                              .reversed()
                              .thenComparing(Customer::getPrefer_Start_Time));
        finalList=scheduleCustomerTeslaNissanChev(customersRequestsfinal,0);
        CUSTOMER_SCHEDULED.addAll(finalList);
        customersRequestsfinal.removeAll(remove(finalList));
        level2.remove(0);
        if(customersRequestsfinal.size()>0 && level2.size()>0){
            for(int i=0;i<level2.size();i++) {
                finalList = scheduleCustomerTeslaNissanChev(customersRequestsfinal, i);
                customersRequestsfinal.removeAll(remove(finalList));  //tesla only
                CUSTOMER_SCHEDULED.addAll(finalList);   //add tesla SC
            }
        }*/
        //************************************Reservation of Tesla,Nissan,Chev on Level2-Done***************************
        System.out.println("\n\n******************Scheduled Customer Requests Are***********************");
       // CUSTOMER_SCHEDULED.stream().map(CustomerScheduledData::toString).forEach(System.out::println);
        System.out.println("Reserved: "+CUSTOMER_SCHEDULED.size());
        System.out.println("Total left are-: "+customersRequestsfinal.size());
        //customersRequestsfinal.forEach(System.out::println);
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
          LocalTime startTime  = customer.getPrefer_Start_Time();
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
          else
          {
              customer.setPrefer_Start_Time(startTime);
          }
      }
    return cs_sc;
  }
  //*******************************************Schedule Customer Tesla-Done*********************************************

  //*****************************************Schedule Customer Tesla $ Nissan*****************************************
   private List<CustomerScheduledData> scheduleCustomerTeslaAndNissan(List<Customer> customers,int i){
      chdm=new ArrayList<>();
       int projectedTotalMinutes;
      CustomerScheduledData scheduledCustomer;
      for(Customer customer:customers){
          Charger chargerScheduled = getCharger_chademo(customer,Chademo.get(i));
          if(chargerScheduled!=null){
              scheduledCustomer=new CustomerScheduledData();
              scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
              scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
              if(customer.getCar_Type().equals(Customer.EV_CAR.NISSAN)) {
                  projectedTotalMinutes = (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(1).getMilePerminute());
              }
              else {
                  projectedTotalMinutes =  (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(5).getMilePerminute());
              }
              scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
              scheduledCustomer.setAssigned_Charger(chargerScheduled);
              chdm.add(scheduledCustomer);
          }
      }
      return chdm;
  }
  //*****************************************Schedule Customer Tesla $ Nissan Done**************************************


  //*****************************************Schedule Customer All Last***********************************************
    private List<CustomerScheduledData>scheduleCustomerTeslaNissanChev(List<Customer> customers,int i){
        last=new ArrayList<>();
        int projectedTotalMinutes=0;
        CustomerScheduledData scheduledCustomer;
        for(Customer customer:customers){
            Charger chargerScheduled = getChargerlast(customer,level2.get(i));
            if(chargerScheduled!=null){
                scheduledCustomer=new CustomerScheduledData();
                scheduledCustomer.setCustomer_Id(customer.getCustomer_Id());
                scheduledCustomer.setPrefer_Start_Time(customer.getPrefer_Start_Time());
                if(customer.getCar_Type().equals(Customer.EV_CAR.NISSAN)) {
                    projectedTotalMinutes = (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(0).getMilePerminute());
                }
                else if(customer.getCar_Type().equals(Customer.EV_CAR.CHEV)){
                    projectedTotalMinutes =  (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(2).getMilePerminute());
                }
                else if(customer.getCar_Type().equals(Customer.EV_CAR.TESLA)){
                    projectedTotalMinutes =  (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(4).getMilePerminute());

                }
                scheduledCustomer.setPrefer_Fin_Time(customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes));
                scheduledCustomer.setAssigned_Charger(chargerScheduled);
                last.add(scheduledCustomer);
            }
        }
        return last;
    }
    //*****************************************Schedule Customer All Last***********************************************

    //**ASSIGN CHEV TO C_C_S******
    private Charger getCharger_CCS(Customer customer,Charger charger){
        Charger ch=null;
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpStartactual = customer.getPrefer_Start_Time();
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
                                            tmpend.isBefore(customer.getPrefer_End_Time())){
                                        customer.setPrefer_Start_Time(tmpStart);
                                        ch = charger;
                                        isSpaceAvailable=false;
                                    }
                                    else{
                                       // customer.setPrefer_Start_Time(tmpStartactual);
                                        if(ccs.getPrefer_Fin_Time().isAfter(projectedFinishTime)){
                                            return null;
                                        }
                                        else if(customer.getPrefer_End_Time().isBefore(tmpend)){
                                            return null;
                                        }
                                        else{
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

    //********************************************************Assign S_C-Tesla******************************************
    private Charger getCharger_SC(Customer customer, Charger charger){
        Charger ch=null;
        LocalTime tmpStartactual = customer.getPrefer_Start_Time();
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpend = customer.getPrefer_End_Time();
        if((EV_COMPATIBILITY_DEFAULT.get(6).getMilePerminute()*customer.getTimeInMinutes())>=customer.getMiles()){
            int projectedTotalMinutes= (int) ((int)customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(6).getMilePerminute());
            //System.out.println("ID "+customer.getCustomer_Id()+" Car-Type "+customer.getCar_Type()+" Customer's minutes "+ customer.getTimeInMinutes());
            System.out.println("Projected Minutes "+ projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            System.out.println("Projected Finish Times is "+projectedFinishTime);
            tmpend = projectedFinishTime;
                    boolean isSpaceAvailable = true;
                    boolean islistempty = true;
                    for (CustomerScheduledData c : cs_sc) {
                        islistempty = false;
                     System.out.println("stag -1: S.ID: "+c.getCustomer_Id()+", S.ID: "
                                                +customer.getCustomer_Id()
                                                +", S.End_Time: "
                                                +c.getPrefer_Fin_Time()
                                                +", C.End_Time: "+customer.getPrefer_End_Time());

                        if (c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time()) ||
                                    c.getPrefer_Fin_Time().equals(customer.getPrefer_End_Time()) ) {

                                tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                                tmpend = tmpStart.plusMinutes(projectedTotalMinutes);
                               // System.out.println(customer.toString());

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
                                }
                                else{
                                     customer.setPrefer_Start_Time(tmpStartactual);
                                     if(c.getPrefer_Fin_Time().isAfter(projectedFinishTime)){
                                         return  null;
                                         //break;
                                     }else if(customer.getPrefer_End_Time().isBefore(tmpend)){
                                         return  null;
                                         //break; // or false
                                    }
                                    else{
                                          isSpaceAvailable = true;
                                    }
                                }
                            }
                            else
                        {
                            //tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                            customer.setPrefer_Start_Time(tmpStartactual);
                            System.out.println("stag 2: S.ID: "+c.getCustomer_Id()
                                    +", S.ID: "+customer.getCustomer_Id());
                            return null;
                            //break;
                        }
                        }
                    if (isSpaceAvailable) {
                            tmpStart = customer.getPrefer_Start_Time().plusMinutes(1);
                           System.out.println("ID stag 3: " + customer.getCustomer_Id());
                            ch = charger;
                    }
                    if (islistempty) {
                            System.out.println("ID stag 4: " + customer.getCustomer_Id());
                            return charger;
                    }


            }
        return  ch;
        }

    //*********************************************Assign Chademo Nissan-Tesla******************************************
    private Charger getCharger_chademo(Customer customer,Charger charger){
        Charger ch=null;
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpend = customer.getPrefer_End_Time();
        LocalTime tmpStartactual = customer.getPrefer_Start_Time();
        int projectedTotalMinutes;
            if(customer.getCar_Type().equals(Customer.EV_CAR.NISSAN)) {
                projectedTotalMinutes = (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(1).getMilePerminute());
            }
            else {
                projectedTotalMinutes =  (int) ((int) customer.getMiles() /EV_COMPATIBILITY_DEFAULT.get(5).getMilePerminute());
            }
            if(customer.getTimeInMinutes()>=projectedTotalMinutes) {
                System.out.println("ID " + customer.getCustomer_Id() + " Car-Type "
                        + customer.getCar_Type() + " Customer's minutes " + customer.getTimeInMinutes());
                System.out.println("Projected Minutes " + projectedTotalMinutes);
                LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
                tmpend = projectedFinishTime;
                System.out.println("Projected Finish Times is " + projectedFinishTime + " With Charger " + Charger.charger_name.CHDM);
                boolean isSpaceAvailable = true;
                boolean islistempty = true;
                for (CustomerScheduledData c : chdm) {
                    islistempty = false;
                    if (c.getAssigned_Charger().equals(charger)) {
                        if (c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time()) ||
                                c.getPrefer_Fin_Time().equals(customer.getPrefer_End_Time())) {
                            tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                            tmpend = tmpStart.plusMinutes(projectedTotalMinutes);
                            if (tmpStart.isAfter(customer.getPrefer_Start_Time()) &&
                                    tmpend.isBefore(customer.getPrefer_End_Time())) {
                                customer.setPrefer_Start_Time(tmpStart);
                                ch = charger;
                                isSpaceAvailable = false;
                            } else {
                               // customer.setPrefer_Start_Time(tmpStartactual);
                                if (c.getPrefer_Fin_Time().isAfter(projectedFinishTime)) {
                                    //customer.setPrefer_Start_Time(tmpStartactual);
                                    return null;
                                } else if (customer.getPrefer_End_Time().isBefore(tmpend)) {
                                    return null;
                                } else {
                                    isSpaceAvailable = true;
                                }
                            }
                        } else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                            tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
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
            }
         return  ch;
    }

    //*********************************************Assign Chademo Nissan-Tesla-Done*************************************

    //*********************************************Assign Level2 Nissan-Tesla Chev**************************************
    private Charger getChargerlast(Customer customer,Charger charger){
        Charger ch=null;
        LocalTime tmpStartactual = customer.getPrefer_Start_Time();
        LocalTime tmpStart = customer.getPrefer_Start_Time();
        LocalTime tmpend = customer.getPrefer_End_Time();
        int projectedTotalMinutes=0;
                switch (customer.getCar_Type()) {
                case NISSAN:
                    projectedTotalMinutes = (int) ((int) customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(0).getMilePerminute());
                    break;
                case TESLA:
                    projectedTotalMinutes = (int) ((int) customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(4).getMilePerminute());
                    break;
                case CHEV:
                    projectedTotalMinutes = (int) ((int) customer.getMiles() / EV_COMPATIBILITY_DEFAULT.get(2).getMilePerminute());
                    break;

            }
        if(customer.getTimeInMinutes()>=projectedTotalMinutes) {
            System.out.println("ID " + customer.getCustomer_Id() + " Car-Type " + customer.getCar_Type() + " Customer's minutes " + customer.getTimeInMinutes());
            System.out.println("Projected Minutes " + projectedTotalMinutes);
            LocalTime projectedFinishTime = customer.getPrefer_Start_Time().plusMinutes(projectedTotalMinutes);
            tmpend = projectedFinishTime;
            System.out.println("Projected Finish Times is " + projectedFinishTime + " With Charger " + Charger.charger_name.LVL_2);
            boolean isSpaceAvailable = true;
            boolean islistempty = true;
            for (CustomerScheduledData c : last) {
                islistempty = false;
                if (c.getAssigned_Charger().equals(charger)) {
                    if (c.getPrefer_Fin_Time().isBefore(customer.getPrefer_End_Time()) ||
                            c.getPrefer_Fin_Time().equals(customer.getPrefer_End_Time())) {
                        tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
                        tmpend = tmpStart.plusMinutes(projectedTotalMinutes);
                        if (tmpStart.isAfter(customer.getPrefer_Start_Time()) &&
                                tmpend.isBefore(customer.getPrefer_End_Time())) {
                            customer.setPrefer_Start_Time(tmpStart);
                            ch = charger;
                            isSpaceAvailable = false;
                        } else {
                           // customer.setPrefer_Start_Time(tmpStartactual);
                            if (c.getPrefer_Fin_Time().isAfter(projectedFinishTime)) {
                                return null;
                            } else if (customer.getPrefer_End_Time().isBefore(tmpend)) {
                                return null;
                            } else {
                                isSpaceAvailable = true;
                            }
                        }
                    } else if (projectedFinishTime.isBefore(c.Prefer_Start_Time)) {
                        tmpStart = c.getPrefer_Fin_Time().plusMinutes(1);
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
        }
         return  ch;
    }
    //*********************************************Assign Level2 Nissan-Tesla Chev-Done*********************************

    //***************************************Remove from initial list***************************************************
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
    //********************************************************Intailize Default Chargers********************************
    private void defaultCompatibility() {
        EV_COMPATIBILITY_DEFAULT = new ArrayList<>();
        //***NISSAN****
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN,
                                                                   Charger.charger_name.LVL_2, 0.37));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.NISSAN,
                                                                   Charger.charger_name.CHDM, 1.22));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV,
                                                                   Charger.charger_name.LVL_2, 0.40));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.CHEV,
                                                                   Charger.charger_name.C_C_S, 2.17));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA,
                                                                   Charger.charger_name.LVL_2, 0.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA,
                                                                   Charger.charger_name.CHDM, 1.42));
        EV_COMPATIBILITY_DEFAULT.add(new DataObjectEVCompatibility(Customer.EV_CAR.TESLA,
                                                                    Charger.charger_name.S_C, 2.67));
    }
    //********************************************************Intailize Default Chargers********************************
    public static Charger getChargerById(int chargerid){
        Charger charger = null;
        for(Charger ch: chargers){
            if(chargerid==ch.getC_P_Id()){
                charger=ch;
            }
        }
        return charger;
    }
}

