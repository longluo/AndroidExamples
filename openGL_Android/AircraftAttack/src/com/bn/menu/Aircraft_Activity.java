package com.bn.menu;

import static com.bn.gameView.Constant.SCREEN_HEIGHT;
import static com.bn.gameView.Constant.SCREEN_WIDTH;
import static com.bn.gameView.Constant.isMusicOn;
import static com.bn.gameView.Constant.isSoundOn;
import static com.bn.gameView.Constant.isVibrateOn;
import static com.bn.gameView.Constant.isCrash;
import static com.bn.gameView.Constant.isOvercome;
import static com.bn.gameView.Constant.isVideo;
import static com.bn.gameView.Constant.is_button_return;
import static com.bn.gameView.Constant.keyState;
import static com.bn.gameView.Constant.ratio_height;
import static com.bn.gameView.Constant.ratio_width;
import java.util.HashMap;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.hardware.SensorListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;

import com.bn.core.RotateUtil;
import com.bn.core.SQLiteUtil;
import com.bn.gameView.GLGameView;

@SuppressWarnings("deprecation")
public class Aircraft_Activity extends Activity {
	GLGameView gameView;// 主游戏场景
	static Handler handler;// 消息接收器
	SoundPool soundPool;// 声音池
	Vibrator mVibrator;// 震动器
	public MediaPlayer bgMusic[] = new MediaPlayer[2];// 游戏背景音乐播放器
	HashMap<Integer, Integer> soundMap;// 存放声音池中的声音ID的Map
	SensorManager mySensorManager;// 传感器的引用
	private boolean isNoBack;// 返回键屏蔽主要是在欢迎界面播放过程中,屏蔽返回键
	private int flag;// 判断当前屏幕是否能够旋转的标志位
	public float[] directionDotXY;// 用于记录传感器的数据.directionDotXY[0]表示左右旋转,
	public float lr_domain = 4;// 传感器左右旋转地阈值
	private SensorListener mySensorListener = new SensorListener() {
		@Override
		public void onAccuracyChanged(int sensor, int accuracy) {
		}

		@Override
		public void onSensorChanged(int sensor, float[] values) {
			if (sensor == SensorManager.SENSOR_ORIENTATION) {
				directionDotXY = RotateUtil.getDirectionDot(new double[] {
						values[0], values[1], values[2] });
				if (directionDotXY[0] > lr_domain) {
					// 左转
					keyState = keyState | 0x4;
					keyState = keyState & 0x7;
				} else if (directionDotXY[0] < -lr_domain) {
					// 右转
					keyState = keyState | 0x8;
					keyState = keyState & 0xB;
				} else {
					// 相关数据复位
					keyState = keyState & 0xB;
					keyState = keyState & 0x7;
				}
			}
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		flag = Settings.System.getInt(this.getContentResolver(), // 判断当前是否能够旋转屏
				Settings.System.ACCELEROMETER_ROTATION, 0);
		if (flag == 0)// 打开旋转屏
		{
			Settings.System.putInt(this.getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION, 1);
		}
		mySensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 传感器管理者
		initScreen();// 初始化屏幕
		initHandler();// 消息接收器
		initSound();// 初始化
		initDatebase();
		collisionShake();// 初始化振动器
		goTo_StartVideo();

	}

	// 消息接收器方法
	public void initHandler() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg)// 重写方法
			{
				switch (msg.what) {
				case 1:
					isNoBack = false;// 返回键可用
					gameView = new GLGameView(Aircraft_Activity.this);
					setContentView(gameView);
					bgMusic[0].start();// 开启背景音乐
					break;
				}
			}
		};
	}

	// 初始化屏幕分辨率
	public void initScreen() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉通知栏
		getWindow().setFlags// 全屏显示
				(WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int tempHeight = (int) (SCREEN_HEIGHT = dm.heightPixels);
		int tempWidth = (int) (SCREEN_WIDTH = dm.widthPixels);
		if (tempHeight < tempWidth) {
			SCREEN_HEIGHT = tempHeight;
			SCREEN_WIDTH = tempWidth;
		} else {
			SCREEN_HEIGHT = tempWidth;
			SCREEN_WIDTH = tempHeight;
		}
		ratio_width = SCREEN_WIDTH / 800;
		ratio_height = SCREEN_HEIGHT / 480;
	}

	public void initDatebase() {
		String sql = "create table if not exists plane(map char(2),grade char(4),time char(4),date char(10));";
		SQLiteUtil.createTable(sql);// 建表SQL语句
	}

	public void goTo_StartVideo() { // 游戏开始首先播放视频
		isNoBack = true;// 返回键不可用
		setContentView(R.layout.start_video);
		final MyVideoView myVideoView = (MyVideoView) findViewById(R.id.start_video_videoview);
		myVideoView.setVideoURI(Uri.parse("android.resource://com.bn.menu/"
				+ R.raw.logo));
		myVideoView.start();
		myVideoView.setOnCompletionListener(new OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				if (getGLVersion() < 2) {// 这里进行opengles测试
					// 弹出对话框,说明不支持该游戏
					showDialog(0);
				} else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO) {
					showDialog(1);
				} else
					handler.sendEmptyMessage(1);// 进入主菜单界面
			}
		});
	}

	public int getGLVersion()// 获取OPENGLES所支持的最高版本
	{
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		ConfigurationInfo info = am.getDeviceConfigurationInfo();
		int majorVersion = info.reqGlEsVersion;
		majorVersion = majorVersion >>> 16;
		return majorVersion;
	}

	public void collisionShake()// 手机震动
	{
		mVibrator = (Vibrator) getApplication().getSystemService(
				Service.VIBRATOR_SERVICE);
	}

	public void shake()// 震动
	{
		if (0 == isVibrateOn)// 开启震动
		{
			mVibrator.vibrate(new long[] { 0, 30 }, -1);
		}
	}

	public void initSound()// 加载声音资源
	{
		bgMusic[0] = MediaPlayer.create(this, R.raw.menubg_music);
		bgMusic[0].setLooping(true);// 是否循环
		bgMusic[0].setVolume(0.3f, 0.3f);// 声音大小
		bgMusic[1] = MediaPlayer.create(this, R.raw.gamebg_music);
		bgMusic[1].setLooping(true);// 是否循环
		bgMusic[1].setVolume(0.5f, 0.5f);// 声音大小
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);// 创建声音池
		soundMap = new HashMap<Integer, Integer>();// 创建map
		soundMap.put(0, soundPool.load(this, R.raw.explode, 1));// 飞机撞山或者死亡的声音
		soundMap.put(1, soundPool.load(this, R.raw.awp_fire, 1));// 坦克和高射炮被击毙爆炸
		soundMap.put(2, soundPool.load(this, R.raw.r700_fire, 1));// 爆炸
		soundMap.put(3, soundPool.load(this, R.raw.bullet, 1));// 飞机发射子弹声音
		soundMap.put(4, soundPool.load(this, R.raw.missile, 1));// 发射子弹声音
		soundMap.put(5, soundPool.load(this, R.raw.m16_fire, 1));// 发射子弹声音
		soundMap.put(6, soundPool.load(this, R.raw.rpg7_fire, 1));// 发射子弹声音
		soundMap.put(7, soundPool.load(this, R.raw.w1200_fire, 1));// 坦克发射子弹声音
		soundMap.put(8, soundPool.load(this, R.raw.ground, 1));// 坦克发射子弹声音
		soundMap.put(9, soundPool.load(this, R.raw.rotation, 1));//
	}

	// 播放声音的方法
	public void playSound(int sound, int loop) {
		if (0 != isSoundOn) {
			return;
		}
		AudioManager mgr = (AudioManager) this
				.getSystemService(Context.AUDIO_SERVICE);
		float streamVolumeCurrent = mgr
				.getStreamVolume(AudioManager.STREAM_MUSIC);
		float streamVolumeMax = mgr
				.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float volume = streamVolumeCurrent / streamVolumeMax;
		soundPool.play(soundMap.get(sound), // 声音资源id
				volume, // 左声道音量
				volume, // 右声道音量
				1, // 优先级
				loop, // 循环次数 -1带表永远循环
				0.5f // 回放速度0.5f～2.0f之间
				);
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case 0:// 生成普通对话框的代码
			String msg = "该设备所支持的opengles版本过低,不支持此游戏!!!";
			Builder b = new AlertDialog.Builder(this);
			b.setIcon(R.drawable.icon);// 设置图标
			b.setTitle("不好意思...");// 设置标题
			b.setMessage(msg);// 设置信息
			b.setPositiveButton(// 为对话框设置按钮
					"退出", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					});
			dialog = b.create();
			break;
		case 1:// 生成普通对话框的代码
			String msgt = "该设备当前Android版本是低于2.2,不支持此游戏!!!";
			Builder bb = new AlertDialog.Builder(this);
			bb.setIcon(R.drawable.icon);// 设置图标
			bb.setTitle("不好意思...");// 设置标题
			bb.setMessage(msgt);// 设置信息
			bb.setPositiveButton(// 为对话框设置按钮
					"退出", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							System.exit(0);
						}
					});
			dialog = bb.create();
			break;
		}
		return dialog;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mySensorManager.registerListener(
				// 注册监听 方法
				mySensorListener, SensorManager.SENSOR_ORIENTATION,
				SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mySensorManager.unregisterListener(mySensorListener); // 取消注册监听器
	}

	public void exitRelease()// 退出时需要执行的方法
	{
		if (flag == 0)// 关掉旋转屏
		{
			Settings.System.putInt(this.getContentResolver(),
					Settings.System.ACCELEROMETER_ROTATION, 0);
		}
		System.exit(0);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent e)// 设置屏幕监听
	{
		if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN
				|| keyCode == KeyEvent.KEYCODE_VOLUME_UP)// 控制音量键只能控制媒体音量的大小
		{
			setVolumeControlStream(AudioManager.STREAM_MUSIC);
			return super.onKeyDown(keyCode, e);
		}
		if (keyCode == 4) {
			if (isNoBack)// 返回键屏蔽
			{
				return true;
			}
			if (!gameView.isGameOn) {
				return gameView.onKeyBackEvent();
			} else // 游戏开始了
			{
				if (!isCrash && !isOvercome) {
					if (!isVideo) {
						is_button_return = !is_button_return;// 按下返回按钮
						if (bgMusic[1].isPlaying()) {
							bgMusic[1].pause();
						} else if (!bgMusic[1].isPlaying() && isMusicOn == 0) {
							bgMusic[1].start();
						}
					} else {
						gameView.isTrueButtonAction = true;
						GLGameView.isVideoPlaying = !GLGameView.isVideoPlaying;
						if (bgMusic[1].isPlaying()) {
							bgMusic[1].pause();
						} else if (!bgMusic[1].isPlaying() && isMusicOn == 0) {
							bgMusic[1].start();
						}
					}
				}
				return true;
			}
		}
		return true;
	}
}
