/**
 * @Title: DialogUtil.java
 * @Package: com.kc.ihaigo.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月2日 下午1:39:14

 * @version V1.0

 */

package com.kc.ihaigo.util;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kc.ihaigo.BackCall;
import com.kc.ihaigo.R;
import com.kc.ihaigo.ui.personal.adapter.SettingsTimeAdapter;

/**
 * @ClassName: DialogUtil
 * @Description: 提供自定义dialog
 * @author: ryan.wang
 * @date: 2014年7月2日 下午1:39:14
 * 
 */

@SuppressLint("InflateParams")
public class DialogUtil {

	/**
	 * @Title: showRateDialog
	 * @user: ryan.wang
	 * @Description: 构造汇率dialog
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showRateDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogThemeNoAnimation);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_rate, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		ListView ll_rate = (ListView) layout.findViewById(R.id.ll_rate);
		// ll_rate.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		//
		// }
		// });
		ll_rate.setAdapter(adpter);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.TOP;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showAddShopcarDialog
	 * @user: ryan.wang
	 * @Description:提供购物车中商品加入收藏的提示dialog
	 * 
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * @throws
	 */
	public static AlertDialog showAddShopcarDialog(Context context,
			OnClickListener confirmClickListener) {
		AlertDialog.Builder builder = new Builder(context);
		AlertDialog dialog = builder
				.setTitle(R.string.dialogtip)
				.setMessage(R.string.dialogaddfavorite)
				.setNegativeButton(R.string.dialogcancel,
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								dialog.dismiss();
							}
						})
				.setPositiveButton(R.string.dialogconfirm, confirmClickListener)
				.show();
		return dialog;
	}

	/**
	 * 
	 * @Title: showPayDialog
	 * @user: ryan.wang
	 * @Description: 显示结算方式dialog
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showPayDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_pay, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_pay_selfbuy).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_pay_selfbuy, dlg);
					}
				});
		layout.findViewById(R.id.dialog_pay_otherbuy).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_pay_otherbuy, dlg);
					}
				});
		layout.findViewById(R.id.dialog_pay_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/***
	 * 操作订单Dialog
	 * 
	 * */
	public static Dialog showEditOrderDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_handle_order, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_edit_order).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_edit_order, dlg);
					}
				});
		layout.findViewById(R.id.dialog_cancel_order).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_cancel_order, dlg);
					}
				});
		layout.findViewById(R.id.dialog_cancle).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/***
	 * 立即发货-----选择服务Dialog
	 * 
	 * */
	public static Dialog AddServiceDialog(final Context context,
			final BackCall alertDo) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.add_service_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		final Integer[] selected = new Integer[] { R.id.divide_box,
				R.id.save_voucher, R.id.special_reinforce };
		layout.findViewById(R.id.divide_box).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selected[0] == R.id.not_divide_box) {
							layout.findViewById(R.id.divide_box_img)
									.setVisibility(View.VISIBLE);
							layout.findViewById(R.id.not_divide_box_img)
									.setVisibility(View.INVISIBLE);
							selected[0] = R.id.divide_box;
						}
					}
				});
		layout.findViewById(R.id.not_divide_box).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						if (selected[0] == R.id.divide_box) {
							layout.findViewById(R.id.not_divide_box_img)
									.setVisibility(View.VISIBLE);
							layout.findViewById(R.id.divide_box_img)
									.setVisibility(View.INVISIBLE);
							selected[0] = R.id.not_divide_box;
						}
					}
				});
		layout.findViewById(R.id.save_voucher).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selected[1] == R.id.not_save_voucher) {
							layout.findViewById(R.id.save_voucher_img)
									.setVisibility(View.VISIBLE);
							layout.findViewById(R.id.not_save_voucher_img)
									.setVisibility(View.INVISIBLE);
							selected[1] = R.id.save_voucher;
						}
					}
				});
		layout.findViewById(R.id.not_save_voucher).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selected[1] == R.id.save_voucher) {
							layout.findViewById(R.id.not_save_voucher_img)
									.setVisibility(View.VISIBLE);
							layout.findViewById(R.id.save_voucher_img)
									.setVisibility(View.INVISIBLE);
							selected[1] = R.id.not_save_voucher;
						}
					}
				});
		layout.findViewById(R.id.special_reinforce).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (selected[2] == R.id.special_reinforce) {
							selected[2] = null;
						} else {
							selected[2] = R.id.special_reinforce;
						}
					}
				});

		layout.findViewById(R.id.add_service_complete).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.add_service_complete, dlg, selected);
					}
				});
		layout.findViewById(R.id.add_service_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.cancel();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: showAddTheAwdDialog
	 * @user: zouxianbin
	 * @Description: 添加国内运单号dialog
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showAddTheAwdDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.noDialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_add_the_awd, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.courier_number);
		layout.findViewById(R.id.choose);

		layout.findViewById(R.id.companyLayout).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.companyLayout, dlg);
					}
				});
		layout.findViewById(R.id.title_left).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.title_left, dlg);
					}
				});
		layout.findViewById(R.id.title_right).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.title_right, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.TOP;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		dlg.setCanceledOnTouchOutside(false);
		return dlg;
	}

	/**
	 * 
	 * @Title: showTransportChoice
	 * @user: ryan.wang
	 * @Description: 分包发货
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showTransportChoice(final Context context,
			final BackCall alertDo) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_choice_transport, null);
		final View complete = layout.findViewById(R.id.complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				alertDo.deal(R.id.complete);
			}
		});
		layout.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.cancel();
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	/***
	 * @Title: showChoiceAddress
	 * @user: yangjie
	 * @Description: 确认订单时选择地址
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return
	 */
	public static Dialog showChoiceAddress(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_choiceaddress, null);
		final View complete = layout.findViewById(R.id.addr_complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null)
					return;
				TextView address = (TextView) v.getTag();
				alertDo.deal(R.id.addr_complete, dlg, address.getTag(),
						address.getText());
			}
		});
		ListView dialog_choiceaddress = (ListView) layout
				.findViewById(R.id.dialog_seladdress_ll);
		dialog_choiceaddress.setAdapter(adpter);
		dialog_choiceaddress.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View addressImageView = view.findViewById(R.id.addresssel);
				addressImageView.setVisibility(View.VISIBLE);
				View selView = (View) parent.getTag();
				if (selView != null && selView != addressImageView) {
					selView.setVisibility(View.INVISIBLE);
				}
				parent.setTag(addressImageView);
				TextView address = (TextView) view.findViewById(R.id.address);
				complete.setTag(address);
			}
		});
		layout.findViewById(R.id.addr_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dlg.cancel();
					}
				});

		layout.findViewById(R.id.add_new_addr).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.add_new_addr, dlg);
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		return dlg;
	}

	/***
	 * @Title: showChoiceIdentifyInfo
	 * @user: Lijie
	 * @Description: 确认订单时选择地址
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return
	 */
	public static Dialog showChoiceIdentifyInfo(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.add_identify_info_dialog, null);
		final View complete = layout.findViewById(R.id.addr_complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null)
					return;
				TextView address = (TextView) v.getTag();
				alertDo.deal(R.id.addr_complete, dlg, address.getTag(),
						address.getText());
			}
		});
		ListView dialog_choiceaddress = (ListView) layout
				.findViewById(R.id.lv_dialog_add_identify);
		dialog_choiceaddress.setAdapter(adpter);
		dialog_choiceaddress.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View addressImageView = view.findViewById(R.id.selected);
				addressImageView.setVisibility(View.VISIBLE);
				View selView = (View) parent.getTag();
				if (selView != null && selView != addressImageView) {
					selView.setVisibility(View.INVISIBLE);
				}
				parent.setTag(addressImageView);
				TextView address = (TextView) view.findViewById(R.id.name);
				complete.setTag(address);
			}
		});
		layout.findViewById(R.id.addr_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dlg.cancel();
					}
				});

		layout.findViewById(R.id.add_new_addr).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.add_new_addr, dlg);
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: showTopicType
	 * @user: ryan.wang
	 * @Description: 发表话题时选择话题类型
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showTopicType(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_choicetopictype, null);
		final View complete = layout.findViewById(R.id.complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null)
					return;
				TextView topictype = (TextView) v.getTag();
				alertDo.deal(R.id.complete, dlg, topictype.getTag());
			}
		});
		ListView dialog_choiceaddress = (ListView) layout
				.findViewById(R.id.dialog_seltopictype_ll);
		dialog_choiceaddress.setAdapter(adpter);
		dialog_choiceaddress.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View topicsel = view.findViewById(R.id.topicsel);
				topicsel.setVisibility(View.VISIBLE);
				View selView = (View) parent.getTag();
				if (selView != null && selView != topicsel) {
					selView.setVisibility(View.INVISIBLE);
				}
				parent.setTag(topicsel);
				TextView topictype = (TextView) view
						.findViewById(R.id.topictype);
				complete.setTag(topictype);
			}
		});
		layout.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.cancel();
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: SelectServiceType
	 * @user: Lijie
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return Dialog
	 * @throws
	 */
	public static Dialog SelectServiceType(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_add_service, null);
		final View complete = layout.findViewById(R.id.complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null)
					return;
				TextView topictype = (TextView) v.getTag();
				alertDo.deal(R.id.complete, dlg, topictype.getTag());
			}
		});
		ListView dialog_choiceservice = (ListView) layout
				.findViewById(R.id.dialog_select_service);
		dialog_choiceservice.setAdapter(adpter);
		dialog_choiceservice.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View topicsel = view.findViewById(R.id.topicsel);
				topicsel.setVisibility(View.VISIBLE);
				View selView = (View) parent.getTag();
				if (selView != null && selView != topicsel) {
					selView.setVisibility(View.INVISIBLE);
				}
				parent.setTag(topicsel);
				TextView topictype = (TextView) view
						.findViewById(R.id.topictype);
				complete.setTag(topictype);
			}
		});
		layout.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();

		return dlg;
	}

	/**
	 * 
	 * @Title: showImmediatelyBuy
	 * @user: zouxianbin
	 * @Description:商品详情立即购买的提示dialog
	 * 
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * @throws
	 */
	public static Dialog showImmediatelyBuy(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_immediately_buy, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.there_are_vpn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.there_are_vpn, dlg);
					}
				});
		layout.findViewById(R.id.selectVpn).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.selectVpn, dlg);
					}
				});
		layout.findViewById(R.id.direct_scan).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.direct_scan, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.TOP;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showImmediatelyBuy
	 * @user: zouxianbin
	 * @Description:商品详情收藏夹dialog
	 * 
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * @throws
	 */
	public static Dialog showfavorite(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_favorite, null);
		// final int cFullFillWidth = 10000;
		// layout.setMinimumWidth(cFullFillWidth);

		layout.findViewById(R.id.choose_ok).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.choose_ok, dlg);
					}
				});
		layout.findViewById(R.id.warning).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.warning, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		// lp.gravity = Gravity.CENTER_VERTICAL;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: showLoginAffirmDialog
	 * @user: helen.yang
	 * @Description:用户注册
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showLoginAffirmDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_personal_resg, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showSettingPsdDialog
	 * @user: helen.yang
	 * @Description: 设置登录密码
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showSettingPsdDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_personal_setting_psd, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showEditPsdDialog
	 * @user: helen.yang
	 * @Description: 找回修改密码
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showEditPsdDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_personal_edit_psd, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showUserPhotoDialog
	 * @user: helen.yang
	 * @Description: 设置用户头像
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showUserPhotoDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_user_photo, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_user_photo).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_user_photo, dlg);
					}
				});
		layout.findViewById(R.id.dialog_user_Album).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_user_Album, dlg);
					}
				});
		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showUserSexDialog
	 * @user: helen.yang
	 * @Description: 设置用户性别
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showUserSexDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_user_sex, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_user_man).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_user_man, dlg);
					}
				});
		layout.findViewById(R.id.dialog_user_woman).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_user_woman, dlg);
					}
				});
		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDelete
	 * @user: zouxianbin
	 * @Description:个人信息,删除身份信息dialog
	 * 
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * @throws
	 */
	public static Dialog showDelete(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_delete, null);
		// final int cFullFillWidth = 10000;
		// layout.setMinimumWidth(cFullFillWidth);

		layout.findViewById(R.id.dialog_cancle).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		layout.findViewById(R.id.choose_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.choose_oks, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		// lp.gravity = Gravity.CENTER_VERTICAL;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/***
	 * 立即发货-----选择goods----Dialog
	 * 
	 * */
	public static Dialog AddGoodsDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.add_goods_dialog, null);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.goods_one).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.goods_one, dlg);
					}
				});
		layout.findViewById(R.id.goods_two).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.goods_two, dlg);
					}
				});
		layout.findViewById(R.id.goods_three).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.goods_three, dlg);
					}
				});
		layout.findViewById(R.id.goods_four).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.goods_four, dlg);
					}
				});
		layout.findViewById(R.id.goods_five).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.goods_five, dlg);
					}
				});

		layout.findViewById(R.id.add_service_complete).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.add_service_complete, dlg);
					}
				});
		layout.findViewById(R.id.add_service_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/***
	 * @user: Lijie
	 * @Description:取消未入库订单。dialog
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * */
	public static Dialog showCancelForecast(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_forecast, null);
		layout.findViewById(R.id.exit_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		layout.findViewById(R.id.exit_ok).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		// lp.gravity = Gravity.CENTER_VERTICAL;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showLogout
	 * @user: ryan.wang
	 * @Description: 确认退出功能
	 * 
	 * @param context
	 * @param alertDo
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showLogout(final Context context,
			final BackCall alertDo) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_settings_logout, null);
		layout.findViewById(R.id.dialog_cancle).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		layout.findViewById(R.id.choose_oks).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.choose_oks, dlg);
					}
				});

		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDelete
	 * @user: zouxianbin
	 * @Description:运单详情--编辑
	 * 
	 * @param context
	 * @param confirmClickListener
	 * @return AlertDialog
	 * @throws
	 */
	public static Dialog showNoDelete(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_hidden, null);
		// final int cFullFillWidth = 10000;
		// layout.setMinimumWidth(cFullFillWidth);

		layout.findViewById(R.id.choose_ok).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.choose_ok, dlg);
					}
				});
		layout.findViewById(R.id.warning).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.warning, dlg);
					}
				});

		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		// lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showProgressDialog
	 * @user: ryan.wang
	 * @Description: 获取progressdialog
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @return ProgressDialog
	 * @throws
	 */
	public static ProgressDialog showProgressDialog(Context context,
			Integer title, String content) {
		ProgressDialog dialog = new ProgressDialog(context);
		dialog.setCancelable(true);
		if (title != null) {
			dialog.setTitle(title);
		}
		dialog.setMessage(content);
		return dialog;
	}

	/**
	 * 
	 * @Title: showSettingTradePsdDialog
	 * @user: helen.yang
	 * @Description: 设置交易密码
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showSettingTradePsdDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_setting_trade_pasd_hint, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		layout.findViewById(R.id.exit_cancels).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * @Title: showColorDialog
	 * @user: zouxianbin
	 * @Description: 预警信息颜色dialog
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showRulesDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener,
			final ListAdapter adpter) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_warning_rules, null);
		final View complete = layout.findViewById(R.id.complete);
		complete.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (v.getTag() == null)
					return;
				alertDo.deal(R.id.complete, dlg, v.getTag());
			}
		});
		ListView dialog_choiceaddress = (ListView) layout
				.findViewById(R.id.dialog_seltopictype_ll);
		dialog_choiceaddress.setAdapter(adpter);
		dialog_choiceaddress.setOnItemClickListener(new OnItemClickListener() {
			List<JSONObject> selDatas = new ArrayList<JSONObject>();

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				View topicsel = view.findViewById(R.id.topicsel);
				TextView topictype = (TextView) view
						.findViewById(R.id.topictype);
				View layout = (RelativeLayout) view
						.findViewById(R.id.top_layout);
				RelativeLayout relat = (RelativeLayout) layout
						.findViewById(R.id.top_layout);

				if (topicsel.getVisibility() == View.VISIBLE) {
					topicsel.setVisibility(View.GONE);
					relat.setBackgroundColor(context.getResources().getColor(
							R.color.white));

					selDatas.remove((JSONObject) topictype.getTag());
				} else {

					topicsel.setVisibility(View.VISIBLE);
					selDatas.add((JSONObject) topictype.getTag());
					relat.setBackgroundColor(context.getResources().getColor(
							R.color.greyBg));
				}
				complete.setTag(selDatas);
			}
		});
		layout.findViewById(R.id.cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showTimeDialog
	 * @user: ryan.wang
	 * @Description: 设置页面选择时间
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @param adpter
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showSettiingTimeDialog(final Context context,
			final BackCall call) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_settings_time, null);
		final TextView maxTime = (TextView) layout.findViewById(R.id.maxTime);
		final TextView minTime = (TextView) layout.findViewById(R.id.minTime);
		SettingsTimeAdapter timelAdapter = new SettingsTimeAdapter(context);
		int firstSelection = Integer.MAX_VALUE / 2;
		ListView timel = (ListView) layout.findViewById(R.id.timel);
		timel.setAdapter(timelAdapter);
		View view = timelAdapter.getView(0, null, null);
		view.measure(0, 0);
		int height = view.getMeasuredHeight();
		timel.getLayoutParams().height = height * 2;
		timel.setSelection(firstSelection - 1);
		timel.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if (view.getLastVisiblePosition() != -1 && visibleItemCount > 2) {
					maxTime.setText(view.getAdapter().getItem(
							view.getLastVisiblePosition())
							+ "");
				}
			}
		});
		ListView times = (ListView) layout.findViewById(R.id.times);
		times.setAdapter(new SettingsTimeAdapter(context));
		times.getLayoutParams().height = height * 2;
		times.setSelection(firstSelection + 2);
		times.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if (view.getFirstVisiblePosition() != -1
						&& visibleItemCount > 2) {
					minTime.setText(view.getAdapter().getItem(
							view.getFirstVisiblePosition())
							+ "");
				}
			}
		});
		layout.findViewById(R.id.addr_cancel).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		layout.findViewById(R.id.addr_complete).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						call.deal(R.id.addr_complete, dlg, minTime.getText(),
								maxTime.getText());
					}
				});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showNoVersion
	 * @user: ryan.wang
	 * @Description: 显示提示性信息
	 * 
	 * @param context
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showInfoDialog(final Context context,
			final BackCall call) {
		final Dialog dlg = new Dialog(context, R.style.noDialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_info, null);
		layout.findViewById(R.id.confirm).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (call != null) {
							call.deal(call.whichId, dlg);
						} else {
							dlg.dismiss();
						}
					}
				});
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: showLoadingDialog
	 * @user: ryan.wang
	 * @Description: 显示阻塞窗体
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param confirmBtn
	 * @param call
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showLoadingDialog(final Context context, String content) {
		final Dialog dlg = new Dialog(context, R.style.noDialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_loading, null);
		((TextView) layout.findViewById(R.id.content)).setText(content);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	/**
	 * 
	 * @Title: showContactUs
	 * @user: ryan.wang
	 * @Description: 联系我们
	 * 
	 * @param context
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showContactUs(final Context context) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_settings_contactus, null);
		layout.findViewById(R.id.dialog_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDeleteTopicDialog
	 * @user: helen.yang
	 * @Description: 删除话题
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showDeleteTopicDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_delete_topic, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		layout.findViewById(R.id.exit_cancels).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDeleteRespondDialog
	 * @user: helen.yang
	 * @Description: 删除回应
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showDeleteRespondDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_delete_respond, null);
		final int cFullFillWidth = 300;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		layout.findViewById(R.id.exit_cancels).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDeleteRespondDialog
	 * @user: helen.yang
	 * @Description:话题分享
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showTopicShareDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_topic_share, null);
		final int cFullFillWidth = 1000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.iv_share_weixin).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						alertDo.deal(R.id.iv_share_weixin, dlg);
					}
				});
		layout.findViewById(R.id.iv_share_circle_of_friends)
				.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						alertDo.deal(R.id.iv_share_circle_of_friends, dlg);
					}
				});
		layout.findViewById(R.id.iv_share_qq).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						alertDo.deal(R.id.iv_share_qq, dlg);
					}
				});
		layout.findViewById(R.id.iv_share_sina).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View arg0) {
						alertDo.deal(R.id.iv_share_sina, dlg);
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 
	 * @Title: showDeleteRespondDialog
	 * @user: helen.yang
	 * @Description: 删除收获地址
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showDeleteAddressDialog(final Context context,
			final BackCall alertDo, OnCancelListener cancelListener) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_delete_address, null);
		final int cFullFillWidth = 400;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.exit_oks).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.exit_oks, dlg);

					}
				});

		layout.findViewById(R.id.exit_cancels).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View arg0) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -100;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		if (cancelListener != null) {
			dlg.setOnCancelListener(cancelListener);
		}
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 打开QQ
	 */
	public static Dialog showQQDialog(final Context context,
			final BackCall alertDo, final String qQ) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_qq, null);
		TextView qq = (TextView) layout.findViewById(R.id.dialog_qq);
		qq.setText(qQ);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_qq).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_qq, dlg, qQ);
					}
				});

		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 联系电话
	 * 
	 * @param context
	 * @param alertDo
	 * @param phoneno
	 * @return
	 */
	public static Dialog showPhotoDialog(final Context context,
			final BackCall alertDo, final String phoneno) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_phoo, null);
		final TextView pho = (TextView) layout.findViewById(R.id.dialog_phoo);
		pho.setText(phoneno);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_phoo).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_phoo, dlg, phoneno);
					}
				});
		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 访问网页
	 * 
	 * @param context
	 * @param alertDo
	 * @param cancelListener
	 * @return
	 */
	public static Dialog showUrlDialog(final Context context,
			final BackCall alertDo, final String url) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_url, null);
		TextView u = (TextView) layout.findViewById(R.id.dialog_url);
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_url).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_url, dlg, url);
					}
				});

		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

	/**
	 * 发送邮件
	 */
	public static Dialog showMailDialog(final Context context,
			final BackCall alertDo, final String string) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_mail, null);
		TextView mail = (TextView) layout.findViewById(R.id.dialog_mail);
		mail.setText(string);

		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		layout.findViewById(R.id.dialog_mail).setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						alertDo.deal(R.id.dialog_mail, dlg, string);
					}
				});

		layout.findViewById(R.id.dialog_user_cancel).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						dlg.dismiss();
					}
				});
		// set a large value put it in bottom
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.x = 0;
		final int cMakeBottom = -1000;
		lp.y = cMakeBottom;
		lp.gravity = Gravity.BOTTOM;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		dlg.show();
		return dlg;
	}

}
