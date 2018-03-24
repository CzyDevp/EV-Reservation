import java.awt.Color;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.CategoryItemLabelGenerator;
import org.jfree.chart.labels.IntervalCategoryItemLabelGenerator;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.ui.TextAnchor;

public class GanttChartView extends JFrame {
	static Date start_Time,end_Time;
	static List<Integer> total_chargers;
	static List<TaskSeries> total_taskseries = new ArrayList<>();
	static List<Task> main_tasks=new ArrayList<>();
	private static final long serialVersionUID = 1L;
	public GanttChartView(final String title) {
		super(title);
		final IntervalCategoryDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		// add the chart to a panel...
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);
	}
	public static IntervalCategoryDataset createDataset() {
	//************************************************try***********************************************************
		LocalTime start_t1 = LocalTime.of(00,00);
		LocalTime end_t1 = LocalTime.of(23,59);
		getDates(start_t1,end_t1);
		System.out.println("Time is "+start_t1 +" "+end_t1);
		total_chargers = Controller.CUSTOMER_SCHEDULED.stream().
				          map(customerScheduledData -> customerScheduledData.getASSIGNED_CHARGER().getC_P_Id())
				          .distinct()
				          .collect(Collectors.toList());
		for(Integer charger_id : total_chargers){
			 String assigned_customers="{";
			List<CustomerScheduledData> csp=new ArrayList<>();
			csp=Controller.CUSTOMER_SCHEDULED.stream().
					       filter(c->c.getASSIGNED_CHARGER().getC_P_Id()==charger_id)
					       .collect(Collectors.toList());
			for(CustomerScheduledData x:csp){
					assigned_customers+=""+x.getCUSTMOER_ID()+" ";
			}
			assigned_customers+="}";
			TaskSeries t = new TaskSeries(charger_id.toString());
			t.setDescription(charger_id.toString());
			total_taskseries.add(t);
			main_tasks.add(new Task(charger_id.toString()+" "+Controller.getChargerById(charger_id).getCh()+" "+assigned_customers,start_Time,end_Time));
		}
		main_tasks.stream().forEach(main->{
				Controller.CUSTOMER_SCHEDULED.stream()
						  .filter(customerScheduledData -> customerScheduledData.getASSIGNED_CHARGER()
								  .getC_P_Id()==Character.digit(main.getDescription().charAt(0),Character.MAX_RADIX))
						.forEach(customerScheduledData -> {
							getDates(customerScheduledData.getPREFER_START_TIME(),customerScheduledData.getPREFER_FINISH_TIME());
							main.addSubtask(new Task(
									String.format("%s: %s {%s}", customerScheduledData.getASSIGNED_CHARGER().C_P_Id,
											customerScheduledData.getASSIGNED_CHARGER().getCh(),
											getCommaSepCustomerIds(customerScheduledData.getCUSTMOER_ID())),
									new SimpleTimePeriod(start_Time,end_Time)));
						});
				total_taskseries.stream().forEach(task->{
					if(Integer.parseInt(task.getDescription())==Character.digit(main.getDescription().charAt(0),Character.MAX_RADIX)){
						task.add(main);
					}
				});
		});
        for(Task t:main_tasks) {
			System.out.println("Main Task is "+t.getDescription());
			System.out.println("subtasks are " + t.getSubtaskCount());
		}
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		for(TaskSeries t:total_taskseries){
			System.out.println("TaskSeries name is "+t.getDescription());
			System.out.println("Total task in "+t.getTasks());
			collection.add(t);
		}
		return collection;
	}
	/**
	 * Creates a chart.
	 * 
	 * @param dataset
	 *            the dataset.
	 * 
	 * @return The chart.
	 */
	private JFreeChart createChart(final IntervalCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createGanttChart("Gantt Chart and Scheduled", // chart
																				// title
				"Charging point", // domain axis label
				"Time", // range axis label
				 dataset, // data
				true, // include legend
				false, // tooltips
				false // urls
		);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setBaseItemLabelGenerator(new IntervalCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.BLACK);
		renderer.setSeriesPaint(0, Color.BLUE);
		renderer.setBasePositiveItemLabelPosition(
				new ItemLabelPosition(ItemLabelAnchor.INSIDE6, TextAnchor.BOTTOM_CENTER));

		renderer.setBaseItemLabelGenerator(new CategoryItemLabelGenerator() {

			@Override
			public String generateRowLabel(CategoryDataset dataset, int row) {
				return "R:" + row;
			}

			@Override
			public String generateColumnLabel(CategoryDataset dataset, int column) {
				return "C:" + column;
			}

			@Override
			public String generateLabel(CategoryDataset dataset, int row, int column) {
				return ""
						+ getCommaSepCustomerIds(Controller.CUSTOMER_SCHEDULED.get(column).getCUSTMOER_ID());
			}

		});

		return chart;
	}

	private static String getCommaSepCustomerIds(int customerId) {
		for (CustomerScheduledData scheduledCustomer : Controller.CUSTOMER_SCHEDULED) {
			if (scheduledCustomer.getCUSTMOER_ID() == customerId) {
				return getCommaSepCustomerIds(scheduledCustomer.getASSIGNED_CHARGER());
			}
		}
		return null;
	}

	private static String getCommaSepCustomerIds(Charger chargingPoint) {
		String id = "";
		for (CustomerScheduledData scheduledCustomer : Controller.CUSTOMER_SCHEDULED) {

			if (scheduledCustomer.getASSIGNED_CHARGER().equals(chargingPoint)) {
				id += scheduledCustomer.getCUSTMOER_ID() + ", ";
			}
		}
		return id.replaceAll(", $", "");
	}


     private static void getDates(LocalTime start,LocalTime end){
		Instant instant = start.atDate(LocalDate.of(Year.now().getValue(),
				 Month.OCTOBER.getValue(),
				 MonthDay.now().getDayOfMonth())).
				 atZone(ZoneId.systemDefault()).toInstant();
		 start_Time = Date.from(instant);
		 Instant instant_last = end.atDate(LocalDate.of(Year.now().getValue(),
				 Month.OCTOBER.getValue(),
				 MonthDay.now().getDayOfMonth())).
				 atZone(ZoneId.systemDefault()).toInstant();
		 end_Time = Date.from(instant_last);
		 System.out.println("Start is "+ start_Time + " end is "+end_Time);
	 }
}
