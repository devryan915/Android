package com.broadchance.xiaojian.person.sports;

import java.util.HashMap;

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
import com.broadchance.xiaojian.utils.Constant;
import com.broadchance.xiaojian.utils.HttpAsyncTask;
import com.broadchance.xiaojian.utils.HttpAsyncTask.HttpReqCallBack;
import com.broadchance.xiaojian.utils.LineTimeChart;
import com.langlang.global.UserInfo;

public class SportsHistoryFragment extends BaseFragment {

	private XYSeries series = new XYSeries("");
	private LinearLayout chartContainer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LinearLayout rootView = (LinearLayout) inflater.inflate(
				R.layout.fragment_sportshistory, null);
		HashMap<String, Object> propertys = new HashMap<String, Object>();
		propertys.put("deviceno", UserInfo.getIntance().getUserData()
				.getDevice_number());
		propertys.put("datatype", BleDataDomainService.DataType_Sports);
		propertys.put("datatime", "2015-01-01");
		HttpAsyncTask.fetchData(Constant.METHOD_QUERY, propertys,
				new HttpReqCallBack() {
					@Override
					public void deal(SoapObject result) {
						double[] x = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9,
								10, 11, 12 };
						double[] y = new double[] { 12.3, 12.5, 13.8, 16.8,
								20.4, 24.4, 26.4, 26.1, 23.6, 20.3, 17.2, 13.9 };
						for (int i = 0; i < 12; i++) {
							series.add(x[i], y[i]);
						}
						chartContainer.addView(makeView());
						if (result != null) {
							String ecgString = result.getPropertyAsString(0);
							Toast.makeText(getActivity(), ecgString, 1000)
									.show();
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
				.findViewById(R.id.personalhealth_sportshis_chart);

		return rootView;
	}

	public GraphicalView makeView() {
		// 构造render
		XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();
		XYSeriesRenderer r = new XYSeriesRenderer();
		r.setColor(getResources().getColor(R.color.sports_linecolor));
		r.setPointStyle(PointStyle.CIRCLE);
		r.setFillPoints(true);
		r.setFillBelowLine(false);// 是否填充下方y轴
		r.setFillBelowLineColor(getResources().getColor(R.color.sleep_green));
		r.setDisplayChartValues(false);// 设置折点上是否显示数量值
		r.setChartValuesTextSize(40);// 设置折现点数值大小
		r.setGradientEnabled(true);
		renderer.addSeriesRenderer(r);
		// 构造dataset
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
		dataset.addSeries(series);
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
		renderer.setMargins(new int[] { 10, 50, 0, 50 });
		// x、y轴上刻度颜色
		renderer.setXLabelsColor(Color.BLACK);
		// renderer.setYLabelsColor(0, Color.BLACK);
		// 轴上数字的数量
		//
		renderer.setXLabels(0);
		for (int i = 0; i < 33; i++) {
			if (i % 2 == 0) {
				renderer.addXTextLabel(i, i + "");
			} else {
				renderer.addXTextLabel(i, "");
			}
		}
		renderer.setXAxisMin(0);
		renderer.setXAxisMax(32);
		// renderer.setYLabels(0);
		// renderer.addYTextLabel(40, "");

		renderer.setYAxisMax(40);
		renderer.setYAxisMin(0);

		// 设置title，及X,Y显示的值范围， 最后两个参数代表轴的颜色和轴标签的颜色
		renderer.setChartTitle(null);
		renderer.setXTitle(null);
		renderer.setYTitle(null);
		renderer.setXLabelsColor(getResources().getColor(
				R.color.sports_linecolor));
		renderer.setYLabelsColor(0,
				getResources().getColor(R.color.sports_linecolor));
		renderer.setAxesColor(getResources().getColor(R.color.sleep_gridcolor));
		// renderer.setShowAxes(true);
		// renderer.setShowLabels(true);

		// builder.setChartSettings(renderer, CommonUtil.getCurTime(),
		// context.getString(R.string.PersonalSleep_Hour),
		// context.getString(R.string.PersonalSleep_Frequency), 0.5, 23.5,
		// 5, 30, Color.BLACK, Color.BLACK);
		// 是否显示网格
		renderer.setGridColor(getResources().getColor(R.color.sleep_gridcolor));
		renderer.setShowGrid(true);
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

		return new GraphicalView(getActivity(), new LineTimeChart(dataset,
				renderer));
	}
}