/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
/**
 * This example, like all Swing examples, exists in a package:
 * in this case, the "start" package.
 * If you are using an IDE, such as NetBeans, this should work 
 * seamlessly.  If you are compiling and running the examples
 * from the command-line, this may be confusing if you aren't
 * used to using named packages.  In most cases,
 * the quick and dirty solution is to delete or comment out
 * the "package" line from all the source files and the code
 * should work as expected.  For an explanation of how to
 * use the Swing examples as-is from the command line, see
 * http://docs.oracle.com/javase/javatutorials/tutorial/uiswing/start/compile.html#package
 */
package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.swing.*;
import javax.swing.event.MouseInputListener;

import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.cache.FileBasedLocalCache;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.DefaultWaypointRenderer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;
import org.jxmapviewer.viewer.WaypointPainter;

public class SwingApplication {
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("HAB Predictor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.setTitle ("Splash analysis");
        frame.setSize (800, 600);

        createMenu(frame);
 
        //Add the ubiquitous "Hello World" label.
//      JLabel label = new JLabel("Hello World");
//      frame.getContentPane().add(label);
//      frame.pack();
        
        Container contentPane = frame.getContentPane();
        
        AtmosphereStateChart chart1 = new AtmosphereStateChart();
        AltitudeProfileChart chart2 = new AltitudeProfileChart();
        
		//contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.PAGE_AXIS));
        contentPane.setLayout(new BorderLayout());
		frame.setContentPane(contentPane);

		JTabbedPane tbp = new JTabbedPane();
		tbp.add("Ground track", createMapViewer());
		//tbp.add("Plots 1", chart1.createPanel());
		//tbp.add("Plots 2", chart2.createPanel());
		contentPane.add(tbp, BorderLayout.CENTER);
		contentPane.add(createConfigurationPanels(), BorderLayout.PAGE_END);
		//contentPane.add(new JButton("test1"));
		//contentPane.add(new JButton("test2"));
        
		frame.pack();
        //Display the window.
        frame.setVisible(true);
    }

	private static Component createMapViewer() {
		JXMapViewer mapViewer = new JXMapViewer();

        // Create a TileFactoryInfo for OpenStreetMap
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        
        // Setup local file cache
        File cacheDir = new File(System.getProperty("user.home") + File.separator + ".jxmapviewer2");
        tileFactory.setLocalCache(new FileBasedLocalCache(cacheDir, false));

        mapViewer.setTileFactory(tileFactory);

        // Use 8 threads in parallel to load the tiles
        tileFactory.setThreadPoolSize(8);

        // Set the focus
        GeoPosition center = new GeoPosition(57.321, 25.322);
        mapViewer.setZoom(8);
        mapViewer.setAddressLocation(center);
        
        // Add interactions
        MouseInputListener mia = new PanMouseInputListener(mapViewer);
        mapViewer.addMouseListener(mia);
        mapViewer.addMouseMotionListener(mia);
        mapViewer.addMouseListener(new CenterMapListener(mapViewer));
        mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
        mapViewer.addKeyListener(new PanKeyListener(mapViewer));

        // Create waypoints from the geo-positions
        Set<DefaultWaypoint> waypoints = new HashSet<DefaultWaypoint>(Arrays.asList(
                new DefaultWaypoint(57.321, 25.322)
        ));
        WaypointPainter<DefaultWaypoint> waypointPainter = new WaypointPainter<DefaultWaypoint>();
        waypointPainter.setWaypoints(waypoints);
        //waypointPainter.setRenderer(new FancyWaypointRenderer());
                
        mapViewer.setOverlayPainter(waypointPainter);
        
        mapViewer.setPreferredSize(new Dimension(600, 300));
        return mapViewer;
	}

	private static Component createConfigurationPanels() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder("Simulation Setup"));
		//panel.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		//panel.setPreferredSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
		
//		String[] titles = { "Launch Site", "Payload", "Balloon", "Gas" };
//		int n_sections = 4;
//		
//		for (int i = 0; i < n_sections; i++) {
//			JPanel subpanel = new JPanel();
//			subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.LINE_AXIS));
//			subpanel.setBorder(BorderFactory.createTitledBorder(titles[i]));
//
//			subpanel.add(new JButton("Test"));
//			panel.add(subpanel);		
//		}
		
		panel.add(new LaunchSettings("Launch site"));
		panel.add(new PayloadSettings("Payload"));
		panel.add(new BalloonSettings("Balloon"));
		panel.add(new GasSettings("Gas"));
		
		JPanel subpanel = new JPanel();
		subpanel.setBorder(BorderFactory.createTitledBorder("Simulation"));
		subpanel.setLayout(new BoxLayout(subpanel, BoxLayout.PAGE_AXIS));
		subpanel.setAlignmentY(Component.TOP_ALIGNMENT);
		subpanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		subpanel.add(new JButton("Quick"));
		subpanel.add(new JButton("Normal"));
		subpanel.add(new JButton("Extended"));
		subpanel.add(new JPanel());
		panel.add(subpanel);
		
		return panel;
	}

	private static void createMenu(JFrame frame) {
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		
		JMenuBar mb = new JMenuBar();
        JMenu m = new JMenu("File");
        JMenuItem mi = new JMenuItem("Hmm...");
        m.add(mi);
        mb.add(m);
        frame.setJMenuBar(mb);
	}
    

 
    public static void main(String[] args) {
		try {
			// Set cross-platform Java L&F (also called "Metal")
			//UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (UnsupportedLookAndFeelException e) {
			// handle exception
		} catch (ClassNotFoundException e) {
			// handle exception
		} catch (InstantiationException e) {
			// handle exception
		} catch (IllegalAccessException e) {
			// handle exception
		}
    	//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
