import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.IntervalCategoryToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.testng.annotations.Test;
import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

class MyToolTipGenerator extends IntervalCategoryToolTipGenerator {
    DateFormat format;
    private MyToolTipGenerator(String value, DateFormat format) {
        super(value, format);
        this.format = format;
    }
    @Override
    public String generateToolTip(CategoryDataset cds, int row, int col) {
        final String s = super.generateToolTip(cds, row, col);
        TaskSeriesCollection tsc = (TaskSeriesCollection) cds;
        StringBuilder sb = new StringBuilder(s);
        String subTaskDes = null;
        // ArrayList<TaskSeries> ats=new ArrayList<>();
        // TaskSeries ts = new TaskSeries(s);
        int a = tsc.getSeriesCount();
        System.out.println(a + "________________");

        for (int i = 1; i < tsc.getSeriesCount(); i++) {
            TaskSeries ts = tsc.getSeries(i);
            Task task = ts.get(0);
            int count = task.getSubtaskCount();
            for (int j = 0; j < task.getSubtaskCount(); j++) {
                while (count-- != 0) {
                    subTaskDes = task.getSubtask(j).getDescription()
                            .toString();
                }
            }
        }

        for (int i = 0; i < tsc.getSubIntervalCount(row, col); i++) {
            sb.append(format.format(tsc.getStartValue(row, col, i)));
            sb.append("-");
            sb.append(format.format(tsc.getEndValue(row, col, i)));
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        // return sb.toString();
        return subTaskDes;
    }


private static JFreeChart createChart() {
    IntervalCategoryDataset xyDataset = createDataset();
    JFreeChart jFreeChart = ChartFactory.createGanttChart("Gantt", "time",
            "value", xyDataset, true, true, true);
    CategoryPlot plot = jFreeChart.getCategoryPlot();
    plot.getRenderer().setBaseToolTipGenerator(
            new MyToolTipGenerator("{0}, {1}: ", DateFormat
                    .getTimeInstance(DateFormat.SHORT)));
    return jFreeChart;
}

private static  IntervalCategoryDataset createDataset() {
    TaskSeriesCollection dataset = new TaskSeriesCollection();
    TaskSeries unavailable1 = new TaskSeries("Unavailable 1");
    TaskSeries unavailable2 = new TaskSeries("Unavailable 2");
    TaskSeries unavailable3 = new TaskSeries("Unavailable 3");
    Task t1 = new Task("Meeting Room 1", date(7), date(18));
    t1.addSubtask(new Task("Meeting 1", date(9), date(16)));
    unavailable1.add(t1);

    Task t2 = new Task("Meeting Room 2", date(8), date(18));
    t2.addSubtask(new Task("Meeting 4", date(10), date(11)));
    t2.addSubtask(new Task("Meeting 5", date(13), date(15)));
    t2.addSubtask(new Task("Meeting 6", date(16), date(18)));
    unavailable2.add(t2);

    Task t3 = new Task("Meeting Room 3", date(8), date(18));
    t2.addSubtask(new Task("Meeting 7", date(11), date(11)));
    t2.addSubtask(new Task("Meeting 8", date(13), date(15)));
    t2.addSubtask(new Task("Meeting 9", date(18), date(18)));
    unavailable3.add(t3);

    dataset.add(unavailable1);
    dataset.add(unavailable2);
    dataset.add(unavailable3);
    return dataset;
}

private static Date date(int hour) {
    final Calendar calendar = Calendar.getInstance();
    calendar.set(2009, Calendar.DECEMBER, 1, hour, 0, 0);
    return calendar.getTime();
}

private static void display() {
    JFrame f = new JFrame("Test");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.add(new ChartPanel(createChart()));
    f.pack();
    f.setLocationRelativeTo(null);
    f.setVisible(true);
}

public static void main(String[] args) {

            display();

}
}