public class Customer {
    int CUSTOMER_ID;
    int EV_TYPE;
    double MILES;
    String PREFER_START_TIME,PREFER_END_TIME;
    EV_CAR ev_car;
    public int getCUSTOMER_ID() {
        return CUSTOMER_ID;
    }
    public void setCUSTOMER_ID(int CUSTOMER_ID) {
        this.CUSTOMER_ID = CUSTOMER_ID;
    }
    public int getEV_TYPE() {
        return EV_TYPE;
    }
    public void setEV_TYPE(int EV_TYPE) {
        this.EV_TYPE = EV_TYPE;
        switch (EV_TYPE){
            case 1:
                this.setEv_car(EV_CAR.NISSAN);
                break;
            case 2:
                this.setEv_car(EV_CAR.CHEV);
                break;
            case 3:
                this.setEv_car(EV_CAR.TESLA);
                break;
        }
    }
    public double getMILES() {
        return MILES;
    }
    public void setMILES(double MILES) {
        this.MILES = MILES;
    }
    @Override
    public String toString() {
        return "Customer is Id-: "+ getCUSTOMER_ID() +"  EV-Type-: "
                                  +getEV_TYPE() +" EV-Type-Name-: "+getEv_car()+"  Miles-: "
                                  +getMILES()+" initial time-: "
                                  +getPREFER_START_TIME()+" end time-: "
                                  +getPREFER_END_TIME() ;
    }
    public String getPREFER_START_TIME() {
        return PREFER_START_TIME;
    }
    public void setPREFER_START_TIME(String PREFER_START_TIME) {
        this.PREFER_START_TIME = PREFER_START_TIME;
    }
    public String getPREFER_END_TIME() {
        return PREFER_END_TIME;
    }
    public void setPREFER_END_TIME(String PREFER_END_TIME) {
        this.PREFER_END_TIME = PREFER_END_TIME;
    }

    public EV_CAR getEv_car() {
        return ev_car;
    }

    public void setEv_car(EV_CAR ev_car) {
        this.ev_car = ev_car;
    }

   public enum EV_CAR {
        NISSAN(1),CHEV(2),TESLA(3);
        private int num;
        EV_CAR(int p) {
            this.num = p;
        }
        int getPrice() {
            return num;
        }
    }
}