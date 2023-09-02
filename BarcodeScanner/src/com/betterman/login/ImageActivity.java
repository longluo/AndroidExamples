package com.betterman.login;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.betterman.util.RGBLuminanceSource;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.android.R;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

public class ImageActivity extends Activity implements OnClickListener {
	private static final String TAG = "TestActivity";

	private ImageView qr_image;
	private TextView qr_text, qr_result;

	private final static int QR_WIDTH = 200, QR_HEIGHT = 200;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_qr);
		init();
	}

	private void init() {
		qr_image = (ImageView) findViewById(R.id.qr_image);
		qr_text = (EditText) findViewById(R.id.qr_text);
		qr_result = (TextView) findViewById(R.id.qr_result);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.qr_create_image:
			encodeQRCode();
			break;
		case R.id.qr_scanning_image:
			decodeQRCode();
			break;
		default:
			break;
		}
	}

	// 生成QR图
	private void encodeQRCode() {
		try {
			// 读取输入的String
			String text = qr_text.getText().toString();
			Log.i(TAG, "生成的文本：" + text);
			if (text == null || "".equals(text) || text.length() < 1) {
				return;
			}
			// android二维码的编码与解码（图片解码与摄像头解码）

			// 实例化二维码对象
			QRCodeWriter writer = new QRCodeWriter();
			// 用一个map保存编码类型
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			// 保持字符集为“utf－8”
			hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
			/* 
			 * 第一个参数：输入的文本
			 * 第二个参数：条形码样式－》二维码
			 * 第三个参数：宽度
			 * 第四个参数：高度
			 * 第五个参数：map保存编码类型
			 */
			BitMatrix bitMatrix = writer.encode(text, BarcodeFormat.QR_CODE,
					QR_WIDTH, QR_HEIGHT, hints);
			System.out.println("w:" + bitMatrix.getWidth() + "h:"
					+ bitMatrix.getHeight());
			// 将像素保存在数组里
			int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
			for (int y = 0; y < QR_HEIGHT; y++) {
				for (int x = 0; x < QR_WIDTH; x++) {
					if (bitMatrix.get(x, y)) {// 二维码黑点
						pixels[y * QR_HEIGHT + x] = 0xff000000;
					} else {// 二维码背景白色
						pixels[y * QR_HEIGHT + x] = 0xffffffff;
					}

				}
			}
			
			// 生成位图
			Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT,
					Bitmap.Config.ARGB_8888);
			/* 
			 * 第一个参数：填充位图的像素数组
			 * 第二个参数：第一个颜色跳过几个像素读取
			 * 第三个参数：像素的幅度
			 * 第四个参数：起点x坐标
			 * 第五个参数：起点y坐标
			 * 第六个参数：宽
			 * 第七个参数：高
			 */
			bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
			// 显示图片
			qr_image.setImageBitmap(bitmap);
		} catch (WriterException e) {
			e.printStackTrace();
		}
	}

	// 解析QR图片
	private void decodeQRCode() {
		Map<DecodeHintType, String> hints = new HashMap<DecodeHintType, String>();
		hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

		Bitmap bitmap = ((BitmapDrawable) qr_image.getDrawable()).getBitmap();
		RGBLuminanceSource source = new RGBLuminanceSource(bitmap);
		// 转成二进制图片
		BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
		// 实例化二维码解码对象
		QRCodeReader reader = new QRCodeReader();
		Result result;
		try {
			// 根据解码类型解码，返回解码结果
			result = reader.decode(bitmap1, hints);
			System.out.println("res：》》》》》》》：" + result.getText());
			// 显示解码结果
			qr_result.setText(result.getText());
		} catch (NotFoundException e) {
			e.printStackTrace();
		} catch (ChecksumException e) {
			e.printStackTrace();
		} catch (FormatException e) {
			e.printStackTrace();
		}
	}
}
