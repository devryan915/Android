package com.broadchance.xiaojian.utils;

import org.achartengine.chart.LineChart;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.graphics.Canvas;
import android.graphics.Paint;

public class LineTimeChart extends LineChart {

	public LineTimeChart(XYMultipleSeriesDataset dataset,
			XYMultipleSeriesRenderer renderer) {
		super(dataset, renderer);
	}

	protected void drawXTextLabels(Double[] xTextLabelLocations, Canvas canvas,
			Paint paint, boolean showLabels, int left, int top, int bottom,
			double xPixelsPerUnit, double minX, double maxX) {
		super.drawXTextLabels(xTextLabelLocations, canvas, paint, showLabels,
				left, top, bottom, xPixelsPerUnit, minX, maxX);
		
	}
}
