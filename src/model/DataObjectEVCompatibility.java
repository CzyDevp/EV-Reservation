public class DataObjectEVCompatibility {
	private Customer.EV_CAR Car_Type;
	private Charger.chargers Charging_Point_Name;
	private double milePerminute = -1;
	public DataObjectEVCompatibility() {
		// TODO Auto-generated constructor stub
	}
	public DataObjectEVCompatibility(Customer.EV_CAR evCarType, Charger.chargers chargingPointName, double milePerminute) {
		super();
		this.Charging_Point_Name = chargingPointName;
		this.Car_Type = evCarType;
		this.milePerminute = milePerminute;
	}
	public Customer.EV_CAR getCar_Type() {
		return Car_Type;
	}
	public void setCar_Type(Customer.EV_CAR car_Type) {
		Car_Type = car_Type;
	}
	public Charger.chargers getCharging_Point_Name() {
		return Charging_Point_Name;
	}
	public void setCharging_Point_Name(Charger.chargers charging_Point_Name) {
		Charging_Point_Name = charging_Point_Name;
	}
	public double getMilePerminute() {
		return milePerminute;
	}
	public void setMilePerminute(double milePerminute) {
		this.milePerminute = milePerminute;
	}
}
