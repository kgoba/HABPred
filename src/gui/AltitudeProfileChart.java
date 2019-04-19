package gui;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import org.apache.commons.math3.ode.FirstOrderIntegrator;
import org.apache.commons.math3.ode.nonstiff.MidpointIntegrator;
import org.apache.commons.math3.ode.sampling.FixedStepHandler;
import org.apache.commons.math3.ode.sampling.StepNormalizer;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.SeriesException;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import model.Balloon;
import model.Payload;
import model.VerticalModel;
import model.atmosphere.ISAAtmosphereModel;
import model.gas.Air;
import model.gas.Hydrogen;

public class AltitudeProfileChart {
	private static double TIMESTEP = 1.0;
	private static double RESULT_TIMESTEP = 30.0;

	private List<double[]> results = null;
	
	private XYDataset createDataset() throws Exception {
		final XYSeries series1 = new XYSeries("h(t)");
		//final XYSeries series2 = new XYSeries("T(h)");

		ISAAtmosphereModel atmosphere = new ISAAtmosphereModel();
		Balloon balloon = new Balloon("Test Balloon", 1.000, 4.00).fillByNeckLift(new Hydrogen(), 2.500);
		Payload payload = new Payload("Test Payload", 1.000);
		VerticalModel model = new VerticalModel(atmosphere, balloon, payload);
				
		double[] y = model.initialState(0); // initial state
		FirstOrderIntegrator integrator = new MidpointIntegrator(TIMESTEP);
		final FixedStepHandler resultHandler = new FixedStepHandler() {			
			@Override
			public void init(double t0, double[] y0, double t) {
				results = new ArrayList<double[]>();
			}

			@Override
			public void handleStep(double t, double[] y, double[] yDot, boolean isLast) {
				results.add(y.clone());
			}
		};
		integrator.addStepHandler(new StepNormalizer(RESULT_TIMESTEP, resultHandler));
		integrator.integrate(model, 0.0, y, 3600.0, y); // now y contains final state
		
		for (int i = 0; i < results.size(); i++) {
			double t = i * RESULT_TIMESTEP;
			try {
				series1.add(t, results.get(i)[0]);
				//series1.add(i, Math.log10(air.getKinematicViscosity(model.getState(i))));
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
				"Altitude profile", "Time", "Altitude", 
				dataset, PlotOrientation.VERTICAL,
				true, false, false);
	}

	public JPanel createPanel() {
		JFreeChart chart;
		try {
			chart = createChart(createDataset());

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
