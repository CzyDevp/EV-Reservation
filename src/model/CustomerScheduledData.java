import java.time.LocalTime;

public class CustomerScheduledData {
    int customer_Id;
    LocalTime Prefer_Start_Time, Prefer_Fin_Time;
     Charger Assigned_Charger;
     @Override
    public String toString() {
        return "Id-: "+ getCustomer_Id() +" Start Time: "+ getPrefer_Start_Time()
                      +" Finish Time: "+ getPrefer_Fin_Time()
                      +" Charger: "+ getAssigned_Charger()
                      +" Duration: "+getChargingDuration();
    }

    public  int getCustomer_Id() {
        return customer_Id;
    }

    public void setCustomer_Id(int customer_Id) {
        this.customer_Id = customer_Id;
    }


    public  Charger getAssigned_Charger() {
        return Assigned_Charger;
    }

    public void setAssigned_Charger(Charger assigned_Charger) {
        this.Assigned_Charger = assigned_Charger;
    }

    public LocalTime getPrefer_Start_Time() {
        return Prefer_Start_Time;
    }

    public void setPrefer_Start_Time(LocalTime prefer_Start_Time) {
        this.Prefer_Start_Time = prefer_Start_Time;
    }

    public LocalTime getPrefer_Fin_Time() {
        return Prefer_Fin_Time;
    }

    public void setPrefer_Fin_Time(LocalTime prefer_Fin_Time) {
        this.Prefer_Fin_Time = prefer_Fin_Time;
    }

     public int getChargingDuration(){
         int minutes=0;
         int hour = Prefer_Fin_Time.getHour()- Prefer_Start_Time.getHour();
         int min = Prefer_Fin_Time.getMinute()- Prefer_Start_Time.getMinute();
         if(hour>0){
             minutes+=hour*60;
         }
         minutes+=min;
         return minutes;
     }
}
