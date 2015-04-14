package com.androidapp.framework.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.DhcpInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.format.Formatter;

import com.example.androidappcore.R;



@SuppressLint("SimpleDateFormat")
public class NetPOSPrinterUtil {
	public static final int PRINT_PORT = 7001; //打印机端口
	public static final int PRINT_WIDTH = 384; //有效打印宽度
	public static final int SUCCESS_PRINT = 0x0; // 打印成功
	public static final int ERROR_PRINT_JAM = 0x1; // 卡纸
	public static final int ERROR_PRINT_WILL_NO_PAPER = 0x2; // 纸将尽
	public static final int ERROR_PRINT_NO_PAPER = 0x4; // 无纸
	public static final int ERROR_PRINT_ACTUATOR_FAULT = 0x8; // 机构故障
	public static final int ERROR_PRINT_HEAD_OVERHEATING = 0x80; // 打印头过热
	public static final int PRINT_NOT_CONNECT = 0xfff; // 请连接打印机
	private static int result = 0;
	private static Socket wifiSocket;
	private static DataOutputStream dos = null;
	private static DataInputStream in = null;
	private static int serverAddress;
	private static TextPaint textPaint;

	private static SimpleDateFormat formatter;
	private static Date curDate;
	private static String str;
	private static Bitmap nBitmapFirst;
	private static Bitmap nBitmapSecond;

	/**
	 * 打印方法
	 * 
	 * context 
	 * bmp 需打印图片
	 * 返回值  int
	 */
	@SuppressWarnings("deprecation")
	public static int printPic(Context context , Bitmap bmp) {
		WifiManager wifi_service = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcpinfo = wifi_service.getDhcpInfo();
		WifiInfo wifi_info = wifi_service.getConnectionInfo();
		if (null != wifi_info && null != wifi_info.getSSID()) {
			if (!wifi_info.getSSID().startsWith("X-431PRINTER")
					&& !wifi_info.getSSID().startsWith("\"X-431PRINTER"))
				return PRINT_NOT_CONNECT;
		}
		serverAddress = dhcpinfo.serverAddress;
		String ip = Formatter.formatIpAddress(serverAddress);
		try {
			wifiSocket = new Socket(ip, PRINT_PORT);
			dos = new DataOutputStream(wifiSocket.getOutputStream());
			in = new DataInputStream(wifiSocket.getInputStream());
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
			return PRINT_NOT_CONNECT; 
		} catch (IOException e1) {
			e1.printStackTrace();
			return PRINT_NOT_CONNECT; 
		}
		if (null == dos || null == in) {
			return PRINT_NOT_CONNECT;
		}

		byte[] data = new byte[] { 0x1B, 0x33, 0x00 };
		try {
			dos.write(data, 0, data.length);
			data[0] = 0x00;
			data[1] = 0x00;
			data[2] = 0x00;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		byte[] escj = new byte[] { 0x1B, 0x4A, 0x00 };
		byte[] esccheck = new byte[] { 0x1D, 0x72, 0x49 };
		int pixelColor;

		byte[] escBmp = new byte[] { 0x1B, 0x2A, 0x00, 0x00, 0x00 };
		escBmp[2] = 0x21;

		escBmp[3] = (byte) (bmp.getWidth() % 256);
		escBmp[4] = (byte) (bmp.getWidth() / 256);

		for (int i = 0; i < (bmp.getHeight() / 24) + 1; i++) {
			try {
				dos.write(escBmp, 0, escBmp.length);
			} catch (IOException e) {
				e.printStackTrace();
			}
			for (int j = 0; j < bmp.getWidth(); j++) {
				for (int k = 0; k < 24; k++) {
					if (((i * 24) + k) < bmp.getHeight()) {
						pixelColor = bmp.getPixel(j, (i * 24) + k);
						if (Color.red(pixelColor) == 0) {
							data[k / 8] += (byte) (128 >> (k % 8));
						}
					}
				}
				try {
					dos.write(data, 0, data.length);

					data[0] = 0x00;
					data[1] = 0x00;
					data[2] = 0x00;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if (i % 10 == 0) {
					dos.write(esccheck);
					if (in.readByte() == 0) {
						dos.write(escj, 0, escj.length);
					}
				} else {
					dos.write(escj, 0, escj.length);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			// dos.write
		}
		byte[] escf = new byte[] { 0x1D, 0x7A, 0x31 };
		byte[] esck = new byte[] { 0x1B, 0x4A, 0x40 };// 50
		try {
			dos.write(escf);
			dos.write(esck);
			result = in.readByte();

		} catch (IOException e1) {
			return PRINT_NOT_CONNECT;
		}

		try {
			dos.close();
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 画图片头部 
	 * context 
	 * 
	 */
	public static Bitmap drawBitFirst(Context context) {
		nBitmapFirst = Bitmap.createBitmap(PRINT_WIDTH, 85,
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(nBitmapFirst);
		canvas.drawColor(Color.WHITE);
		Paint p = new Paint();
		p.setColor(Color.BLACK);
		p.setTextSize(20);
		canvas.drawText(context.getResources()
				.getString(R.string.print_launch), 0, 20, p);
		canvas.drawLine(0, 40, PRINT_WIDTH, 40, p);
		canvas.drawText(
				context.getResources().getString(
						R.string.print_automobile_fault_diagnosis_test_report),
				20, 70, p);
		canvas.drawLine(0, 80, PRINT_WIDTH, 80, p);
		return nBitmapFirst;
	}

	/**
	 * 画图片正文部分
	 * context 
	 * s  打印数据
	 * (正文部分加入打印时间，公司名、电话、车牌暂未加入)
	 */
	public static Bitmap drawBitSecond(Context context, String s) {
		formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		curDate = new Date(System.currentTimeMillis());
		str = formatter.format(curDate);
//		if (null == company_address) {
//			company_address = "";
//		}
//		if (null == company_phone) {
//			company_phone = "";
//		}
//		if (null == license_plate_number) {
//			license_plate_number = "";
//		}
		StringBuffer ss = new StringBuffer();
		ss.append(context.getResources().getString(R.string.print_test_time)
				+ str + "\n");
//		ss.append(mContext.getResources().getString(
//				R.string.print_serial_number)
//				+ serialNum + "\n");
//		ss.append(mContext.getResources().getString(
//				R.string.print_test_company_address)
//				+ company_address + "\n");
//		ss.append(mContext.getResources().getString(
//				R.string.print_test_company_phone)
//				+ company_phone + "\n");
//		ss.append(mContext.getResources().getString(
//				R.string.print_test_license_plate_number)
//				+ license_plate_number + "\n");
		ss.append(s);
		textPaint = new TextPaint();
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(20.0F);
		StaticLayout layout = new StaticLayout(ss, textPaint, PRINT_WIDTH,
				Alignment.ALIGN_NORMAL, 1.0F, 0.0F, true);
		nBitmapSecond = Bitmap.createBitmap(PRINT_WIDTH, layout.getHeight(),
				Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(nBitmapSecond);
		canvas.drawColor(Color.WHITE);
		layout.draw(canvas);
		return nBitmapSecond;
	}

	/**
	 * 图片拼接
	 * first  图片头部
	 * second	图片正文
	 */
	public static Bitmap mixtureBitmap(Bitmap first, Bitmap second) {
		if (first == null || second == null) {
			return null;
		}
		Bitmap newBitmap = Bitmap.createBitmap(PRINT_WIDTH, first.getHeight()
				+ second.getHeight(), Config.RGB_565);
		Canvas cv = new Canvas(newBitmap);
		cv.drawBitmap(first, 0, 0, null);
		cv.drawBitmap(second, 0, first.getHeight(), null);
		return newBitmap;
	}

	/**
	 * 打印结果toast
	 * context
	 * result:  printPic方法返回值
	 */
	public static void resultToast(final Context context, final int result) {
		Handler handler = new Handler(Looper.getMainLooper());
		handler.post(new Runnable() {
			@Override
			public void run() {
				switch (result) {
				case SUCCESS_PRINT:
					NToast.longToast(context, R.string.print_success);
					break;
				case ERROR_PRINT_JAM:
					NToast.longToast(context, R.string.print_jam);
					break;
				case ERROR_PRINT_WILL_NO_PAPER:
					NToast.longToast(context, R.string.print_will_nopaper);
					break;
				case ERROR_PRINT_NO_PAPER:
					NToast.longToast(context, R.string.print_no_paper);
					break;
				case ERROR_PRINT_ACTUATOR_FAULT:
					NToast.longToast(context, R.string.print_actuator_fault);
					break;
				case ERROR_PRINT_HEAD_OVERHEATING:
					NToast.longToast(context, R.string.print_head_overheating);
					break;
				case PRINT_NOT_CONNECT:
					NToast.longToast(context, R.string.print_connect_printer);
					break;
				}
			}
		});
	}
	
}