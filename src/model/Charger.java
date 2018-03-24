public class Charger {
    int C_P_Id;
    chargers ch;
    public int getC_P_Id() {
        return C_P_Id;
    }
    public void setC_P_Id(int c_P_Id) {
        this.C_P_Id = c_P_Id;
    }
    public chargers getCh() {
        return ch;
    }
    public void setCh(int CHARGER_TYPE) {
        switch(CHARGER_TYPE){
            case 1:
                this.ch=chargers.LVL_2;
                break;
            case 2:
                this.ch=chargers.CHDM;
                break;
            case 3:
                this.ch=chargers.C_C_S;
                break;
            case 4:
                this.ch=chargers.S_C;
                break;
        }

    }

    enum chargers{
        LVL_2, S_C, CHDM, C_C_S;
    }
    @Override
    public String toString() {

        return "Charger-ID-: "+ getC_P_Id()
                +" "+getCh();

    }

}
