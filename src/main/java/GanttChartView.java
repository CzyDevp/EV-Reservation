import java.awt.Color;
import java.time.Duration;
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
/**
 * A simple demonstration application showing how to create a Gantt chart.
 * <P>
 * This demo is intended to show the conceptual approach rather than being a
 * polished implementation.
 * http://www.java2s.com/Code/Java/Chart/JFreeChartGanttDemo1.htm
 *
 */
public class GanttChartView extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new demo.
	 *
	 * @param title
	 *            the frame title.
	 */
	public GanttChartView(final String title) {
		super(title);
		final IntervalCategoryDataset dataset = createDataset();
		final JFreeChart chart = createChart(dataset);
		// add the chart to a panel...
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
		setContentPane(chartPanel);

	}

	// ****************************************************************************
	// * JFREECHART DEVELOPER GUIDE *
	// * The JFreeChart Developer Guide, written by David Gilbert, is available
	// *
	// * to purchase from Object Refinery Limited: *
	// * *
	// * http://www.object-refinery.com/jfreechart/guide.html *
	// * *
	// * Sales are used to provide funding for the JFreeChart project - please *
	// * support us so that we can continue developing free software. *
	// ****************************************************************************

	/**
	 * Creates a sample dataset for a Gantt chart.
	 *
	 * @return The dataset.
	 */
	public static IntervalCategoryDataset createDataset() {
		final TaskSeries s1 = new TaskSeries("- EV Car Scheduler -");
		for (CustomerScheduledData scheduledCustomer : Controller.CUSTOMER_SCHEDULED) {
			s1.add(new Task(
					String.format("%s: %s {%s}", scheduledCustomer.getASSIGNED_CHARGER().CHARGING_POINT_ID,
							scheduledCustomer.getASSIGNED_CHARGER().getCh(),
							getCommaSepCustomerIds(scheduledCustomer.getCUSTMOER_ID())),
					new SimpleTimePeriod(scheduledCustomer.getPREFER_START_TIME().getMinute(),
							scheduledCustomer.getPREFER_FINISH_TIME().getMinute())));
		}

		final TaskSeriesCollection collection = new TaskSeriesCollection();
		collection.add(s1);

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
				true, // tooltips
				false // urls
		);

		 
		 
		
		CategoryPlot plot = (CategoryPlot) chart.getPlot();
		CategoryItemRenderer renderer = plot.getRenderer();		
		renderer.setBaseItemLabelGenerator(new IntervalCategoryItemLabelGenerator());
		renderer.setBaseItemLabelsVisible(true);
		renderer.setBaseItemLabelPaint(Color.BLACK);
		renderer.setSeriesPaint(0, Color.GREEN);
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

}
