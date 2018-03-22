public class Charger {
    int CHARGING_POINT_ID;
    chargers ch;
    public int getCHARGING_POINT_ID() {
        return CHARGING_POINT_ID;
    }
    public void setCHARGING_POINT_ID(int CHARGING_POINT_ID) {
        this.CHARGING_POINT_ID = CHARGING_POINT_ID;
    }
    public chargers getCh() {
        return ch;
    }
    public void setCh(int CHARGER_TYPE) {
        switch(CHARGER_TYPE){
            case 1:
                this.ch=chargers.LEVEL_2;
                break;
            case 2:
                this.ch=chargers.CHADEMO;
                break;
            case 3:
                this.ch=chargers.COMBO_CHARGER_SYSTEM;
                break;
            case 4:
                this.ch=chargers.SUPER_CHARGER;
                break;
        }

    }

    enum chargers{
        LEVEL_2,SUPER_CHARGER,CHADEMO,COMBO_CHARGER_SYSTEM;
    }
    @Override
    public String toString() {

        return "Charger-ID-: "+ getCHARGING_POINT_ID()
                +" Charger-Type-: "+getCh();

    }
}
