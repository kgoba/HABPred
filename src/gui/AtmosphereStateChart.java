package gui;

import java.awt.Color;

import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import model.atmosphere.ISAAtmosphereModel;
import model.gas.Air;

public class AtmosphereStateChart {
	private XYDataset createDataset() {
		final XYSeries series1 = new XYSeries("p(h)");
		final XYSeries series2 = new XYSeries("T(h)");

		ISAAtmosphereModel model = new ISAAtmosphereModel();
		Air air = new Air();
		
		double 	lat = 0, lon = 0;
		long 	time = 0;

		for (int i = 0; i < 40000; i += 100) {
			try {
				series1.add(i, model.getPressure(lat, lon, i, time) / 100);
				//series1.add(i, Math.log10(air.getKinematicViscosity(model.getState(i))));
				series2.add(i, model.getTemperature(lat, lon, i, time));
			} catch (SeriesException e) {
				System.err.println("Error adding to series");
			}
		}

		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		//dataset.addSeries(series2);
		return dataset;
	}

	private JFreeChart createChart(final XYDataset dataset) {
		return ChartFactory.createXYLineChart(
				"Atmosphere state vs altitude", "Altitude", "Value", 
				dataset, PlotOrientation.HORIZONTAL,
				true, false, false);
	}

	public JPanel createPanel() {
		JFreeChart chart = createChart(createDataset());

		XYPlot p = (XYPlot) chart.getPlot();
		p.setBackgroundPaint(new Color(255, 255, 255));
		p.setRangeGridlinePaint(new Color(100, 100, 100));
		p.setRangeMinorGridlinePaint(new Color(100, 100, 100));
		p.setDomainGridlinePaint(new Color(100, 100, 100));
		p.setDomainMinorGridlinePaint(new Color(100, 100, 100));
		p.setDomainZeroBaselinePaint(new Color(100, 100, 100));
		p.setDomainGridlinesVisible(true);
		p.setRangeGridlinesVisible(true);

		ChartPanel panel = new ChartPanel(chart);
		// panel.setPreferredSize(800, 600);
		panel.setMinimumDrawHeight(100);
		panel.setMaximumDrawHeight(1000);
		panel.setMinimumDrawWidth(100);
		panel.setMaximumDrawWidth(2000);

		return panel;
	}
}
