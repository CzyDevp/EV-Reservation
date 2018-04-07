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
		LocalTime start_t1 = LocalTime.of(00,00);
		LocalTime end_t1 = LocalTime.of(23,59);
		getDates(start_t1,end_t1);
		//***getUnique Set of Chargers********
		total_chargers = Controller.CUSTOMER_SCHEDULED
				          .stream()
				          .map(customerScheduledData -> customerScheduledData.getAssigned_Charger().getC_P_Id())
				          .distinct()
				          .collect(Collectors.toList());
		//***create Task Series and Main Tasks
		total_chargers.stream().forEach(c->{
			//********get set of customer id's for each point
			String assigned_customers=Controller.CUSTOMER_SCHEDULED
					       .stream()
					       .filter(c1->c1.getAssigned_Charger().getC_P_Id()==c)
					       .map(c11->c11.getCustomer_Id()+"")
					       .collect(Collectors.joining(","));
			TaskSeries t = new TaskSeries(c.toString());
			t.setDescription(c.toString());
			total_taskseries.add(t);
			main_tasks.add(new Task(c.toString()+" "+Controller.getChargerById(c).getCh()+" {"+assigned_customers+"}",start_Time,end_Time));
			});
		main_tasks.stream().forEach(main->{
				Controller.CUSTOMER_SCHEDULED.stream()
						  .filter(customerScheduledData -> customerScheduledData.getAssigned_Charger()
								  .getC_P_Id()==Character.digit(main.getDescription().charAt(0),Character.MAX_RADIX))
						.forEach(customerScheduledData -> {
							getDates(customerScheduledData.getPrefer_Start_Time(),customerScheduledData.getPrefer_Fin_Time());
							System.out.println("Starthere "+ start_Time + " endhere "+end_Time);
							main.addSubtask(new Task(
									String.format("%s: %s", customerScheduledData.getAssigned_Charger().C_P_Id,
											customerScheduledData.getAssigned_Charger().getCh()
											),
									new SimpleTimePeriod(start_Time,end_Time)));
						});
				total_taskseries.stream().forEach(task->{
					if(Integer.parseInt(task.getDescription())==Character.digit(main.getDescription().charAt(0),Character.MAX_RADIX)){
						task.add(main);
					}
				});
		});
		final TaskSeriesCollection collection = new TaskSeriesCollection();
		total_taskseries.stream().forEach(t->collection.add(t));
		return collection;
	}
	private JFreeChart createChart(final IntervalCategoryDataset dataset) {
		final JFreeChart chart = ChartFactory.createGanttChart("Gantt Chart and Scheduled", // chart
																				// title
				"Charge_Point_Id", // domain axis label
				"Time", // range axis label
				 dataset, // data
				true, // include legend
				true, // tooltips
				false // urls
		);
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = plot.getRenderer();
		renderer.setBaseItemLabelGenerator(new IntervalCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.BLACK);
		renderer.setSeriesPaint(0, Color.BLUE);
		return chart;
	}
	//***************************get parsed date for Task**************************************************
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
