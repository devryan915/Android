/**
 * @Title: ChartUtil.java
 * @Package: com.kc.ihaigo.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月23日 下午5:43:45

 * @version V1.0

 */

package com.kc.ihaigo.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.MultipleCategorySeries;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.kc.ihaigo.R;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint.Align;
import android.view.View;
import android.widget.ListView;

/**
 * @ClassName: ChartUtil
 * @Description: 用于预警画图表
 * @author: ryan.wang
 * @date: 2014年7月23日 下午5:43:45
 * 
 */

public class ChartUtil {

	public static GraphicalView drawHistoryPrice(Activity context,
			List<Double> list_price, List<String> list_itme) {
		String[] titles = new String[] { "历史价格" };
		List<Double> price = list_price;
		List<String> item = list_itme;
		double[] xx_item = new double[item.size()];
		double[] yy_price = new double[list_price.size()];
		double[] xx_str = new double[xx_item.length];// 把数据替换成1234567890
		String[] itmeX = new String[item.size()];

		int n = item.size() - 1;
		int m = price.size() - 1;
		for (int i = 0; i < item.size(); i++) {
			String xi = item.get(i).toString().replaceFirst("0", "");
			xx_item[n] = Double.parseDouble(xi);
			itmeX[n] = xi;
			n--;
		}
		for (int i = 0; i < price.size(); i++) {
			yy_price[m] = Double.parseDouble(price.get(i).toString());
			m--;
		}

		for (int i = 0; i < xx_item.length; i++) {
			xx_str[i] = i + 1;
		}

		// x轴的值
		List<double[]> x = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			x.add(xx_str);
		}
		// y轴的值
		List<double[]> values = new ArrayList<double[]>();
		for (int i = 0; i < titles.length; i++) {
			values.add(yy_price);
		}
		// 设置字体颜色
		int[] colors = new int[] { Color.RED };
		// 点的样式
		PointStyle[] styles = new PointStyle[] { PointStyle.CIRCLE };
		XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
		int length = renderer.getSeriesRendererCount();
		// 点是空心还是实心
		for (int i = 0; i < length; i++) {
			((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
					.setFillPoints(true);
		}

		for (int i = 0; i < itmeX.length; i++) {
			renderer.addXTextLabel(i + 1, itmeX[i]);
		}

		renderer.setXLabels(0);// 设置x轴显示12个点,根据setChartSettings的最大值和最小值自动计算点的间隔

		// 设置好图表的样式
		setChartSettings(renderer, null, null, null, 0.5, 5.5, 0, 100,
				Color.GRAY, Color.LTGRAY);

		renderer.setYLabels(5);// 设置y轴显示10个点,根据setChartSettings的最大值和最小值自动计算点的间隔
		// 是否显示网格
		renderer.setShowGrid(true);// 是否显示网格
		renderer.setXLabelsAlign(Align.RIGHT);// x或y轴上数字的方向
		renderer.setYLabelsAlign(Align.RIGHT);// y轴上数字的方向
		renderer.setZoomButtonsVisible(false);// 是否显示放大缩小按钮
		renderer.setLabelsTextSize(13); // 数轴刻度字体大小
		renderer.setShowCustomTextGrid(true);

		renderer.setApplyBackgroundColor(true);
	    renderer.setLabelsColor(Color.GREEN);//坐标名称以及标题颜色
		renderer.setBackgroundColor(Color.argb(0, 220, 228, 234));
		renderer.setMarginsColor(Color.argb(0, 220, 228, 234));
		XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
		// XYSeries series = dataset.getSeriesAt(0);
		// series.addAnnotation("Vacation", 6, 30);
		GraphicalView view = ChartFactory.getLineChartView(context, dataset,
				renderer);

		return view;
	}

	// public static GraphicalView drawHistoryPrice(Activity context) {
	// String[] titles = new String[]{"Crete", "Corfu", "Thassos",
	// "Skiathos"};//, "Corfu", "Thassos", "Skiathos"
	// List<double[]> x = new ArrayList<double[]>();
	// for (int i = 0; i < titles.length; i++) {
	// x.add(new double[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
	// }
	// List<double[]> values = new ArrayList<double[]>();
	// values.add(new double[]{12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4, 26.1,
	// 23.6, 20.3, 17.2, 13.9});
	// values.add(new double[]{10, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14, 11});
	// values.add(new double[]{5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15, 9, 6});
	// values.add(new double[]{9, 10, 11, 15, 19, 23, 26, 25, 22, 18, 13, 10});
	// int[] colors = new int[]{Color.BLUE, Color.GREEN, Color.CYAN,
	// Color.YELLOW};
	// PointStyle[] styles = new PointStyle[]{PointStyle.CIRCLE,
	// PointStyle.DIAMOND, PointStyle.TRIANGLE, PointStyle.SQUARE};
	// XYMultipleSeriesRenderer renderer = buildRenderer(colors, styles);
	// int length = renderer.getSeriesRendererCount();
	// for (int i = 0; i < length; i++) {
	// ((XYSeriesRenderer) renderer.getSeriesRendererAt(i))
	// .setFillPoints(true);
	// }
	// setChartSettings(renderer, "Average temperature", "Month",
	// "Temperature", 0.5, 12.5, -10, 40, Color.LTGRAY, Color.LTGRAY);
	// renderer.setXLabels(12);
	// renderer.setYLabels(10);
	// renderer.setShowGrid(true);
	// renderer.setXLabelsAlign(Align.RIGHT);
	// renderer.setYLabelsAlign(Align.RIGHT);
	// renderer.setZoomButtonsVisible(false);
	// renderer.setPanLimits(new double[]{-10, 20, -10, 40});
	// renderer.setZoomLimits(new double[]{-10, 20, -10, 40});
	//
	// XYMultipleSeriesDataset dataset = buildDataset(titles, x, values);
	// XYSeries series = dataset.getSeriesAt(0);
	// series.addAnnotation("Vacation", 6, 30);
	// GraphicalView view = ChartFactory.getLineChartView(context, dataset,
	// renderer);
	// return view;
	// }
	/**
	 * Builds an XY multiple dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param xValues
	 *            the values for the X axis
	 * @param yValues
	 *            the values for the Y axis
	 * @return the XY multiple dataset
	 */
	protected static XYMultipleSeriesDataset buildDataset(String[] titles,
			List<double[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		addXYSeries(dataset, titles, xValues, yValues, 0);
		return dataset;
	}

	public static void addXYSeries(XYMultipleSeriesDataset dataset,
			String[] titles, List<double[]> xValues, List<double[]> yValues,
			int scale) {
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			XYSeries series = new XYSeries(titles[i], scale);
			double[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
	}

	/**
	 * Builds an XY multiple series renderer.
	 * 
	 * @param colors
	 *            the series rendering colors
	 * @param styles
	 *            the series point styles
	 * @return the XY multiple series renderers
	 */
	protected static XYMultipleSeriesRenderer buildRenderer(int[] colors,
			PointStyle[] styles) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		setRenderer(renderer, colors, styles);
		return renderer;
	}

	protected static void setRenderer(XYMultipleSeriesRenderer renderer,
			int[] colors, PointStyle[] styles) {
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setPointSize(5f);

		renderer.setMargins(new int[] { 20, 30, 15, 20 });
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(colors[i]);
			r.setPointStyle(styles[i]);
			renderer.addSeriesRenderer(r);
		}
	}

	/**
	 * Sets a few of the series renderer settings.
	 * 
	 * @param renderer
	 *            the renderer to set the properties to
	 * @param title
	 *            the chart title
	 * @param xTitle
	 *            the title for the X axis
	 * @param yTitle
	 *            the title for the Y axis
	 * @param xMin
	 *            the minimum value on the X axis
	 * @param xMax
	 *            the maximum value on the X axis
	 * @param yMin
	 *            the minimum value on the Y axis
	 * @param yMax
	 *            the maximum value on the Y axis
	 * @param axesColor
	 *            the axes color
	 * @param labelsColor
	 *            the labels color
	 */
	protected static void setChartSettings(XYMultipleSeriesRenderer renderer,
			String title, String xTitle, String yTitle, double xMin,
			double xMax, double yMin, double yMax, int axesColor,
			int labelsColor) {
		renderer.setChartTitle(title);
		renderer.setXTitle(xTitle);
		renderer.setYTitle(yTitle);
		renderer.setXAxisMin(xMin);
		renderer.setXAxisMax(xMax);
		renderer.setYAxisMin(yMin);
		renderer.setYAxisMax(yMax);
		renderer.setAxesColor(axesColor);
		renderer.setLabelsColor(labelsColor);

	}

	/**
	 * Builds an XY multiple time dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param xValues
	 *            the values for the X axis
	 * @param yValues
	 *            the values for the Y axis
	 * @return the XY multiple time dataset
	 */
	protected XYMultipleSeriesDataset buildDateDataset(String[] titles,
			List<Date[]> xValues, List<double[]> yValues) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			TimeSeries series = new TimeSeries(titles[i]);
			Date[] xV = xValues.get(i);
			double[] yV = yValues.get(i);
			int seriesLength = xV.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(xV[k], yV[k]);
			}
			dataset.addSeries(series);
		}
		return dataset;
	}

	/**
	 * Builds a category series using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the category series
	 */
	protected CategorySeries buildCategoryDataset(String title, double[] values) {
		CategorySeries series = new CategorySeries(title);
		int k = 0;
		for (double value : values) {
			series.add("Project " + ++k, value);
		}

		return series;
	}

	/**
	 * Builds a multiple category series using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the category series
	 */
	protected MultipleCategorySeries buildMultipleCategoryDataset(String title,
			List<String[]> titles, List<double[]> values) {
		MultipleCategorySeries series = new MultipleCategorySeries(title);
		int k = 0;
		for (double[] value : values) {
			series.add(2007 + k + "", titles.get(k), value);
			k++;
		}
		return series;
	}

	/**
	 * Builds a category renderer to use the provided colors.
	 * 
	 * @param colors
	 *            the colors
	 * @return the category renderer
	 */
	protected DefaultRenderer buildCategoryRenderer(int[] colors) {
		DefaultRenderer renderer = new DefaultRenderer();
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		renderer.setMargins(new int[] { 20, 30, 15, 0 });
		for (int color : colors) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(color);
			renderer.addSeriesRenderer(r);

		}
		return renderer;
	}

	/**
	 * Builds a bar multiple series dataset using the provided values.
	 * 
	 * @param titles
	 *            the series titles
	 * @param values
	 *            the values
	 * @return the XY multiple bar dataset
	 */
	protected XYMultipleSeriesDataset buildBarDataset(String[] titles,
			List<double[]> values) {
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		int length = titles.length;
		for (int i = 0; i < length; i++) {
			CategorySeries series = new CategorySeries(titles[i]);
			double[] v = values.get(i);
			int seriesLength = v.length;
			for (int k = 0; k < seriesLength; k++) {
				series.add(v[k]);
			}
			dataset.addSeries(series.toXYSeries());
		}
		return dataset;
	}

	/**
	 * Builds a bar multiple series renderer to use the provided colors.
	 * 
	 * @param colors
	 *            the series renderers colors
	 * @return the bar multiple series renderer
	 */
	protected XYMultipleSeriesRenderer buildBarRenderer(int[] colors) {
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		renderer.setAxisTitleTextSize(16);
		renderer.setChartTitleTextSize(20);
		renderer.setLabelsTextSize(15);
		renderer.setLegendTextSize(15);
		int length = colors.length;
		for (int i = 0; i < length; i++) {
			SimpleSeriesRenderer r = new SimpleSeriesRenderer();
			r.setColor(colors[i]);
			renderer.addSeriesRenderer(r);
		}
		return renderer;
	}

}
