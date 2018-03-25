import java.time.LocalTime;
public class Customer {
    int customer_Id;
    int miles;
    LocalTime prefer_Start_Time, prefer_End_Time;
    EV_CAR Car_Type;
    public int getCustomer_Id() {
        return customer_Id;
    }
    public void setCustomer_Id(int customer_Id) {
        this.customer_Id = customer_Id;
    }
    public double getMiles() {
        return miles;
    }
    public void setMiles(int miles) {
        this.miles = miles;
    }
    @Override
    public String toString() {
        return "Customer is Id-: "+ getCustomer_Id()
                                  +" EV_Type-: "+ getCar_Type()+"  Miles-: "
                                  + getMiles()+" init_time-: "
                                  + getPrefer_Start_Time()+" e_time-: "
                                  + getPrefer_End_Time() ;
    }
    public LocalTime getPrefer_Start_Time() {
        return prefer_Start_Time;
    }
    public void setPrefer_Start_Time(LocalTime prefer_Start_Time) {
        this.prefer_Start_Time = prefer_Start_Time;
    }
    public LocalTime getPrefer_End_Time() {
        return prefer_End_Time;
    }
    public void setPrefer_End_Time(LocalTime prefer_End_Time) {
        this.prefer_End_Time = prefer_End_Time;
    }
    public EV_CAR getCar_Type() {
        return Car_Type;
    }
    public void setEv_car(int EV_TYPE) {
        switch (EV_TYPE){
            case 1:
                this.Car_Type =EV_CAR.NISSAN;
                break;
            case 2:
                this.Car_Type =EV_CAR.CHEV;
                break;
            case 3:
                this.Car_Type =EV_CAR.TESLA;
                break;
        }

    }
    public int getTimeInMinutes(){
        int minutes=0;
        int hour = prefer_End_Time.getHour()- prefer_Start_Time.getHour();
        int min = prefer_End_Time.getMinute()- prefer_Start_Time.getMinute();
        if(hour>0){
            minutes+=hour*60;
        }
        minutes+=min;
        return minutes;
    }

   public enum EV_CAR {
        NISSAN,CHEV,TESLA;
   }
}