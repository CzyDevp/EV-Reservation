import java.time.LocalTime;

public class CustomerScheduledData {
    int CUSTMOER_ID;
    LocalTime PREFER_START_TIME,PREFER_FINISH_TIME;
    ChargerScheduled ASSIGNED_CHARGER;

    public int getCUSTMOER_ID() {
        return CUSTMOER_ID;
    }

    public void setCUSTMOER_ID(int CUSTMOER_ID) {
        this.CUSTMOER_ID = CUSTMOER_ID;
    }


    public ChargerScheduled getASSIGNED_CHARGER() {
        return ASSIGNED_CHARGER;
    }

    public void setASSIGNED_CHARGER(ChargerScheduled ASSIGNED_CHARGER) {
        this.ASSIGNED_CHARGER = ASSIGNED_CHARGER;
    }

    public LocalTime getPREFER_START_TIME() {
        return PREFER_START_TIME;
    }

    public void setPREFER_START_TIME(LocalTime PREFER_START_TIME) {
        this.PREFER_START_TIME = PREFER_START_TIME;
    }

    public LocalTime getPREFER_FINISH_TIME() {
        return PREFER_FINISH_TIME;
    }

    public void setPREFER_FINISH_TIME(LocalTime PREFER_FINISH_TIME) {
        this.PREFER_FINISH_TIME = PREFER_FINISH_TIME;
    }

     public int getChargingDuration(){
         int minutes=0;
         int hour = PREFER_FINISH_TIME.getHour()-PREFER_START_TIME.getHour();
         int min = PREFER_FINISH_TIME.getMinute()-PREFER_START_TIME.getMinute();
         if(hour>0){
             minutes+=hour*60;
         }
         minutes+=min;
         return minutes;
     }
}
