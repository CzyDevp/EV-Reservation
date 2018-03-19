public class Charger {
    int CHARGING_POINT_ID;
    int CHARGER_TYPE;
    chargers ch;
    public int getCHARGING_POINT_ID() {
        return CHARGING_POINT_ID;
    }

    public void setCHARGING_POINT_ID(int CHARGING_POINT_ID) {
        this.CHARGING_POINT_ID = CHARGING_POINT_ID;
    }

    public int getCHARGER_TYPE() {
        return CHARGER_TYPE;
    }

    public void setCHARGER_TYPE(int CHARGER_TYPE) {
        switch(CHARGER_TYPE){
            case 1:
                this.ch=chargers.LEVEL_2;
              //  this.setCh(chargers.CHADEMO);
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
        this.CHARGER_TYPE = CHARGER_TYPE;
    }

    public chargers getCh() {
        return ch;
    }

    public void setCh(chargers ch) {
        this.ch = ch;
    }

    enum chargers{
        LEVEL_2(1),SUPER_CHARGER(4),CHADEMO(2),COMBO_CHARGER_SYSTEM(3);
        private int num;
        chargers(int n){
            this.num= n;
        }
        public int getNum() {
            return num;
        }
    }

    @Override
    public String toString() {

        return "Charger-ID-: "+ getCHARGING_POINT_ID()+" Charger-Type-: "+getCHARGER_TYPE()+" Charger-Name-:"+getCh();
    }
}
