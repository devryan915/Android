package com.broadchance.xiaojian.person.sleep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.json.JSONArray;
import org.json.JSONException;
import org.ksoap2.serialization.SoapObject;

import android.graphics.Color;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.broadchance.xiaojian.BaseFragment;
import com.broadchance.xiaojian.R;
import com.broadchance.xiaojian.service.BleDataDomainService;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.broadchance.xiaojian.utils.LineTimeChart;
import com.langlang.global.UserInfo;

public class SleepCurrentFragment extends BaseFragment {

	private static final String TAG = SleepCurrentFragment.class
			.getSimpleName();
	private GraphicalView ghView;
	private LinearLayout chartContainer;
	/** 曲线数量 */
	private List<XYSeries> mGreenTimeSeries = new ArrayList<XYSeries>();
	private List<XYSeries> mVioletTimeSeries = new ArrayList<XYSeries>();
	private List<XYSeries> mOrangeTimeSeries = new ArrayList<XYSeries>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.fragment_sleepcurrent, null);
		HashMap<String, Object> propertys = new HashMap<String, Object>();
		propertys.put("deviceno", UserInfo.getIntance().getUserData()
				.getDevice_number());
		propertys.put("datatype", BleDataDomainService.DataType_Breath);
		propertys.put("datatime", "2015-01-01");
		HttpAsyncTask.fetchData("QueryData", propertys, new HttpReqCallBack() {
			@Override
			public void deal(SoapObject result) {
				List<double[]> x = new ArrayList<double[]>();
				x.add(new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 });
				x.add(new double[] { 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14 });
				x.add(new double[] { 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17,
						18 });
				List<double[]> y = new ArrayList<double[]>();
				y.add(new double[] { 12.3, 12.5, 13.8, 16.8, 20.4, 24.4, 26.4,
						26.1, 23.6, 20.3, 17.2, 13.9 });
				y.add(new double[] { 2, 10, 12, 15, 20, 24, 26, 26, 23, 18, 14,
						11 });
				y.add(new double[] { 5, 5.3, 8, 12, 17, 22, 24.2, 24, 19, 15,
						9, 6 });
				XYSeries series = new XYSeries("");
				for (int i = 0; i < 12; i++) {
					series.add(x.get(0)[i], y.get(0)[i]);
				}
				mGreenTimeSeries.add(series);
				series = new XYSeries("");
				for (int i = 0; i < 12; i++) {
					series.add(x.get(1)[i], y.get(1)[i]);
				}
				mVioletTimeSeries.add(series);
				series = new XYSeries("");
				for (int i = 0; i < 12; i++) {
					series.add(x.get(2)[i], y.get(2)[i]);
				}
				mOrangeTimeSeries.add(series);
				chartContainer.addView(makeView());
				if (result != null) {
					String ecgString = result.getPropertyAsString(0);
					Toast.makeText(getActivity(), ecgString, 1000).show();
					final JSONArray ecgDatas;
					try {
						ecgDatas = new JSONArray(ecgString);
						for (int i = 0; i < ecgDatas.length(); i++) {
							new Handler().postDelayed(new Runnable() {
								@Override
								public void run() {

								}
							}, 400 * i);
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}, new Object[] { getActivity(), "正在查询数据" });
		chartContainer = (LinearLayout) rootView
				.findViewById(R.id.personalhealth_sleep_chart);

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	public GraphicalView makeView() {
		// 构造render
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		for (int i = 0; i < mGreenTimeSeries.size(); i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(getResources().getColor(R.color.sleep_green));
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(false);
			r.setFillBelowLine(true);// 是否填充下方y轴
			r.setFillBelowLineColor(getResources()
					.getColor(R.color.sleep_green));
			r.setDisplayChartValues(false);// 设置折点上是否显示数量值
			r.setChartValuesTextSize(40);// 设置折现点数值大小
			r.setGradientEnabled(true);
			renderer.addSeriesRenderer(r);
		}
		for (int i = 0; i < mVioletTimeSeries.size(); i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setColor(getResources().getColor(R.color.sleep_violet));
			r.setPointStyle(PointStyle.CIRCLE);
			r.setFillPoints(false);
			r.setFillBelowLine(true);// 是否填充下方y轴
			r.setFillBelowLineColor(getResources().getColor(
					R.color.sleep_violet));
			r.setDisplayChartValues(false);// 设置折点上是否显示数量值
			r.setChartValuesTextSize(40);// 设置折现点数值大小
			r.setGradientEnabled(true);
			renderer.addSeriesRenderer(r);
		}
		for (int i = 0; i < mOrangeTimeSeries.size(); i++) {
			XYSeriesRenderer r = new XYSeriesRenderer();
			r.setPointStyle(PointStyle.CIRCLE);
			r.setColor(getResources().getColor(R.color.sleep_orange));
			r.setFillPoints(false);
			r.setFillBelowLine(true);// 是否填充下方y轴
			r.setFillBelowLineColor(getResources().getColor(
					R.color.sleep_orange));
			r.setDisplayChartValues(false);// 设置折点上是否显示数量值
			r.setChartValuesTextSize(40);// 设置折现点数值大小
			r.setGradientEnabled(true);
			renderer.addSeriesRenderer(r);
		}
		// 构造dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		for (int i = 0; i < mGreenTimeSeries.size(); i++) {
			dataset.addSeries(mGreenTimeSeries.get(i));
		}
		for (int i = 0; i < mVioletTimeSeries.size(); i++) {
			dataset.addSeries(mVioletTimeSeries.get(i));
		}
		for (int i = 0; i < mOrangeTimeSeries.size(); i++) {
			dataset.addSeries(mOrangeTimeSeries.get(i));
		}

		// 图表部分的背景颜色
		renderer.setBackgroundColor(getResources().getColor(R.color.sleep_bg));
		renderer.setApplyBackgroundColor(true);
		// 图表与屏幕四边的间距颜色
		renderer.setMarginsColor(getResources().getColor(R.color.sleep_bg));
		renderer.setChartTitleTextSize(30);
		renderer.setAxisTitleTextSize(25);
		// renderer.setLegendHeight(50);
		// 图例文字的大小
		renderer.setShowLegend(false);
		renderer.setLegendTextSize(10);
		renderer.setMargins(new int[] { 10, 15, 0, 15 });
		// x、y轴上刻度颜色
		renderer.setXLabelsColor(Color.BLACK);
		// renderer.setYLabelsColor(0, Color.BLACK);
		// 轴上数字的数量
		//
		renderer.setXLabels(0);
		for (int i = 0; i < 25; i++) {
			if (i % 2 == 0) {
				renderer.addXTextLabel(i, i < 10 ? "0" + i + ":00" : i + ":00");
			} else {
				renderer.addXTextLabel(i, "");
			}
		}
		// labeltime.set(Calendar.HOUR_OF_DAY, 0);
		renderer.setXAxisMin(0);
		// labeltime.set(Calendar.HOUR_OF_DAY, 24);
		renderer.setXAxisMax(24);
		renderer.setYLabels(0);
		renderer.addYTextLabel(40, "");

		renderer.setYAxisMax(40);
		renderer.setYAxisMin(0);

		// 设置title，及X,Y显示的值范围， 最后两个参数代表轴的颜色和轴标签的颜色
		renderer.setChartTitle(null);
		renderer.setXTitle(null);
		renderer.setYTitle(null);
		// renderer.setXLabelsColor(Color.BLACK);
		// renderer.setYLabelsColor(0, Color.BLACK);
		renderer.setAxesColor(getResources().getColor(R.color.sleep_gridcolor));
		// renderer.setShowAxes(true);
		// renderer.setShowLabels(true);

		// builder.setChartSettings(renderer, CommonUtil.getCurTime(),
		// context.getString(R.string.PersonalSleep_Hour),
		// context.getString(R.string.PersonalSleep_Frequency), 0.5, 23.5,
		// 5, 30, Color.BLACK, Color.BLACK);
		// 是否显示网格
		renderer.setGridColor(getResources().getColor(R.color.sleep_gridcolor));
		renderer.setShowCustomTextGrid(true);
		// x或y轴上数字的方向，相反的。
		renderer.setXLabelsAlign(Align.CENTER);
		renderer.setYLabelsAlign(Align.RIGHT);
		// 是否支持滑动放缩
		renderer.setPanEnabled(false, false);
		// 有问题的方法
		// renderer.setZoomButtonsVisible(true);
		// renderer.setPanLimits(new double[] { -10, 20, -10, 40 });
		// renderer.setZoomLimits(new double[] { -10, 20, -10, 40 });
		// GraphicalView ghView =
		// ChartFactory.getCubeLineChartView(getActivity(),
		// builder.buildDateDataset(titles, x, values), renderer, 0.33f);
		// ghView = ChartFactory.getCubeLineChartView(getActivity(),
		// builder.buildDateDataset(titles, x, values), renderer, 0.33f);

		// 构造view
		// ghView = ChartFactory
		// .getLineChartView(getActivity(), dataset, renderer);
		ghView = new GraphicalView(getActivity(), new LineTimeChart(dataset,
				renderer));

		return ghView;
	}
}