public class DataObjectEVCompatibility {
	private Customer.EV_CAR Car_Type;
	private Charger.charger_name Charging_Point_Name;
	private double miles_Perminute = -1;
		public DataObjectEVCompatibility(Customer.EV_CAR evCarType, Charger.charger_name chargingPointName, double milePerminute) {
		this.Charging_Point_Name = chargingPointName;
		this.Car_Type = evCarType;
		this.miles_Perminute = milePerminute;
	}
	public Customer.EV_CAR getCar_Type() {
		return Car_Type;
	}
	public void setCar_Type(Customer.EV_CAR car_Type) {
		Car_Type = car_Type;
	}
	public Charger.charger_name getCharging_Point_Name() {
		return Charging_Point_Name;
	}
	public void setCharging_Point_Name(Charger.charger_name charging_Point_Name) {
		Charging_Point_Name = charging_Point_Name;
	}
	public double getMilePerminute() {
		return miles_Perminute;
	}
	public void setMilePerminute(int milePerminute) {
		this.miles_Perminute = milePerminute;
	}
}
