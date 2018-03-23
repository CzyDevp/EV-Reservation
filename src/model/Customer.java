import java.time.LocalTime;
public class Customer {
    int CUSTOMER_ID;
    int MILES;
    LocalTime PREFER_START_TIME,PREFER_END_TIME;
    EV_CAR CAR_TYPE;
    public int getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }
    public void setCUSTOMER_ID(int CUSTOMER_ID) {
        this.CUSTOMER_ID = CUSTOMER_ID;
    }
    public double getMILES() {
        return MILES;
    }
    public void setMILES(int MILES) {
        this.MILES = MILES;
    }
    @Override
    public String toString() {
        return "Customer is Id-: "+ getCUSTOMER_ID()
                                  +" EV_Type-: "+getCAR_TYPE()+"  Miles-: "
                                  +getMILES()+" init_time-: "
                                  +getPREFER_START_TIME()+" e_time-: "
                                  +getPREFER_END_TIME() ;
    }
    public LocalTime getPREFER_START_TIME() {
        return PREFER_START_TIME;
    }
    public void setPREFER_START_TIME(LocalTime PREFER_START_TIME) {
        this.PREFER_START_TIME = PREFER_START_TIME;
    }
    public LocalTime getPREFER_END_TIME() {
        return PREFER_END_TIME;
    }
    public void setPREFER_END_TIME(LocalTime PREFER_END_TIME) {
        this.PREFER_END_TIME = PREFER_END_TIME;
    }
    public EV_CAR getCAR_TYPE() {
        return CAR_TYPE;
    }
    public void setEv_car(int EV_TYPE) {
        switch (EV_TYPE){
            case 1:
                this.CAR_TYPE =EV_CAR.NISSAN;
                break;
            case 2:
                this.CAR_TYPE=EV_CAR.CHEV;
                break;
            case 3:
                this.CAR_TYPE=EV_CAR.TESLA;
                break;
        }

    }
    public int getTimeInMinutes(){
        int minutes=0;
        int hour = PREFER_END_TIME.getHour()-PREFER_START_TIME.getHour();
        int min = PREFER_END_TIME.getMinute()-PREFER_START_TIME.getMinute();
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