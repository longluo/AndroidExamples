package com.bn.st.d2;
import java.util.HashMap;
import com.bn.R;
import com.bn.clp.BoatInfo;
import com.bn.clp.Constant;
import com.bn.clp.DaoJiShiForDraw;
import com.bn.clp.KeyThread;
import com.bn.clp.MyGLSurfaceView;
import com.bn.clp.RotateThread;
import com.bn.core.RotateUtil;
import com.bn.st.xc.CircleForDraw;
import com.bn.st.xc.CylinderTextureByVertex;
import com.bn.st.xc.XCSurfaceView;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import static com.bn.clp.Constant.BgSoundFlag;
import static com.bn.clp.Constant.COLS;
import static com.bn.clp.Constant.ROWS;
import static com.bn.clp.Constant.generateMountion;
import static com.bn.clp.Constant.generatePDY;
import static com.bn.clp.Constant.generateTunnel;
import static com.bn.clp.Constant.generateWDY;
import static com.bn.clp.Constant.generateZDY;
import static com.bn.clp.Constant.generateZDY_XD;
import static com.bn.clp.Constant.yArray_ZD;
import static com.bn.st.xc.Constant.*;
/*
 * 此为选船场景
 */
enum WhichView
{
	WELCOME_VIEW,MENU_VIEW,XC_VIEW,GAME_VIEW,HELP_VIEW,SOUND_VIEW,GAME_MODE_VIEW,GUANYU_VIEW,RECORD_VIEW
};
public class MyActivity extends Activity 
{
	static WhichView curr;
	public static Object boatInitLock=new Object();
	public MyGLSurfaceView gameV;
	XCSurfaceView xcV;//选船场景的引用
	public Handler hd;//消息控制器
	
	ViewForDraw vfd;
	
	WelcomeView welV;//欢迎过场动画界面
	MenuView menuV;//菜单界面的引用	
	SoundSurfaceView ssv;//声音界面的引用
	HelpSurfaceView hsv;//游戏帮助界面的引用
	GameModeView gmv;//游戏模式选择界面的引用
	GuanYuView gyv;//关于界面的引用
	DSurfaceView dsv;//记录查询界面的引用
	
	//============================
	CheckVersionDialog cvDialog;
	AndroidVersionDialog avDialog;
	BreakRecordOrNotDialog bronDialog;//是否破纪录的对话框
	RankingDialog rDialog;//名次对话框
	int flag;
	//==============================
	
	public MediaPlayer beijingyinyue;//游戏背景音乐播放器
	SoundPool shengyinChi;//声音池
	HashMap<Integer,Integer> soundIdMap;//声音池中声音ID与自定义声音ID的Map
	
	//SensorManager对象引用 
	SensorManager mySensorManager;	
	
	SharedPreferences sp;
	  
	//开发实现了SensorEventListener接口的传感器监听器
	private SensorEventListener mySensorListener = new SensorEventListener()
	{
		@Override    
		public void onAccuracyChanged(Sensor sensor, int accuracy) {			
			   
		} 
		@Override
		public void onSensorChanged(SensorEvent event) 
		{
			//若使用传感器标志位为true,并且场景可触控标志位为true,可使用传感器。 			
			if(event.sensor.getType() == Sensor.TYPE_ORIENTATION)
			{//判断是否为加速度传感器变化产生的数据
				//计算出重力在屏幕上的投影方向	
				int[] directionDotXY=RotateUtil.getDirectionDot 
				(
						event.values
			    );  
				if(directionDotXY[1]<-20)
				{//right
					MyGLSurfaceView.keyState=MyGLSurfaceView.keyState|0x4;
				}
				else if(directionDotXY[1]>20)
				{//left
					MyGLSurfaceView.keyState=MyGLSurfaceView.keyState|0x8;
				}
				else
				{//no left and no right
					MyGLSurfaceView.keyState=MyGLSurfaceView.keyState&0x3;
				}
			}	
		}		
	}; 	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {  
        super.onCreate(savedInstanceState);
        
        //获取SharedPreferences
        sp=this.getSharedPreferences("actm", Context.MODE_PRIVATE);
        //获得值，默认值为true
        com.bn.clp.Constant.BgSoundFlag = sp.getBoolean("bgSoundFlag", true);
        com.bn.clp.Constant.SoundEffectFlag = sp.getBoolean("soundEffectFlag", true);     
                        
        //初始化声音资源
        chushihuaSounds(); 
        //设置为全屏
        requestWindowFeature(Window.FEATURE_NO_TITLE); 
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN ,  
		              WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置为横屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//游戏过程中只允许调整多媒体音量，而不允许调整通话音量。
		setVolumeControlStream(AudioManager.STREAM_MUSIC);
		
		//创建数据库表
		DBUtil.createTable();
		
		flag=Settings.System.getInt(this.getContentResolver(),
		         Settings.System.ACCELEROMETER_ROTATION, 0);
		        Settings.System.putInt(this.getContentResolver(),
		         Settings.System.ACCELEROMETER_ROTATION,1);  
		
		SCREEN_WIDTH=getWindowManager().getDefaultDisplay().getWidth();
		SCREEN_HEIGHT=getWindowManager().getDefaultDisplay().getHeight();
		
		float screenHeightTemp=SCREEN_HEIGHT;//记录系统返回的屏幕分辨率。
        float screenWidthTemp=SCREEN_WIDTH;
        
        if(screenHeightTemp>screenWidthTemp) //指定屏幕的宽和高。
        {
        	SCREEN_WIDTH=screenHeightTemp;
        	SCREEN_HEIGHT=screenWidthTemp;
        }
        com.bn.clp.Constant.screenRatio=SCREEN_WIDTH/SCREEN_HEIGHT;//获取屏幕的宽高比
        if(Math.abs(com.bn.clp.Constant.screenRatio-com.bn.clp.Constant.screenRatio854x480)<0.001f)
        {
        	com.bn.clp.Constant.screenId=1;
       	}
        else if(Math.abs(com.bn.clp.Constant.screenRatio-com.bn.clp.Constant.screenRatio480x320)<0.01f)
        {
        	com.bn.clp.Constant.screenId=3;
        }
        else if(Math.abs(com.bn.clp.Constant.screenRatio-com.bn.clp.Constant.screenRatio960x540)<0.001f)
        {
        	com.bn.clp.Constant.screenId=2;
        }
        else
        {
        	com.bn.clp.Constant.screenId=0;
        }
        
		ratio_height=SCREEN_HEIGHT/480;
		ratio_width=SCREEN_WIDTH/854;
		//获得SensorManager对象
        mySensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		new Thread()
		{
		   public void run()
		   {
			   synchronized(boatInitLock)
			   {
				   //选船的
				   CylinderTextureByVertex.initVertexData(14, 6, 8, 1);
				   CircleForDraw.initVertexData(8, 14);
				   XCSurfaceView.loadWelcomeBitmap(MyActivity.this.getResources());
				   XCSurfaceView.loadVertexFromObj(MyActivity.this.getResources());
				   com.bn.st.xc.ShaderManager.loadCodeFromFile(MyActivity.this.getResources());
				   
				    //主场景的
				    //产生直道的Y坐标数组
			        generateZDY();
			        //产生直道带小岛的Y坐标数组
			        generateZDY_XD(getResources());
			        //创建拐弯赛道的Y坐标数组
			        generateWDY();
			        //创建平地赛道的Y坐标数组
			        generatePDY();			        
			        generateMountion(getResources());
			        generateTunnel(getResources());			        
					ROWS=yArray_ZD.length-1;
					COLS=yArray_ZD[0].length-1; 
					MyGLSurfaceView.loadProgressBitmap(MyActivity.this.getResources());
					com.bn.clp.ShaderManager.loadCodeFromFile(MyActivity.this.getResources());
			   }
		   }
		}.start();
		
		//初始化GLSurfaceView
        hd=new Handler()
        {
			@Override
			public void handleMessage(Message msg)
			{
        		switch(msg.what)
        		{
	        		case 0://去欢迎界面
	        			gotoWelcomeView();
	        		break;
	        		case 1://去主菜单界面
	        			gotoMenuView();
	        		break;
	        		case 2://去模式选择界面
	        			gotoGameModeView();
	                break;
	        		case 3://去选艇界面
	        			gotoXCView();
	        		break;
	        		case 4://去音效设置界面
	        			gotoMusicSetView();
	        		break;
	        		case 5://去游戏帮助界面
	        			gotoHelpView();
	        		break;
	        		case 6://去关于界面
	        			gotoGuanYuView();
	        		break;
	        		case 7://主界面退出游戏按钮
	        			Settings.System.putInt(MyActivity.this.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,flag);
	        			System.exit(0);
	        		break;
	        		case 8://去游戏界面-计时模式
	        			gotoTimerGameView();
	        		break;
	        		case 9://去游戏界面-竞速模式
	        			gotoSpeedGameView();
	        		break;
	        		case 10://去游戏记录查询界面
	        			gotoRecordView();
	        		break;
	        		case 11://显示是否破纪录对话框
	        			showDialog(3);
	        		break;
	        		case 12://显示当前名次对话框
	        			showDialog(4);
	        		break;
        		}
			}
        };
        hd.sendEmptyMessage(0);    
    }
    //跳转到游戏帮助界面
    public void gotoHelpView()
    {
    	if(hsv==null)
    	{
    		hsv=new HelpSurfaceView(MyActivity.this);
    	}    	
    	hsv.initThread();
    	vfd.curr=hsv;
		curr=WhichView.HELP_VIEW;
    }
    //跳转到音效设置界面
    public void gotoMusicSetView()
    {
    	if(ssv==null)
    	{
    		ssv=new SoundSurfaceView(MyActivity.this);
    	}
    	ssv.initThread();
    	vfd.curr=ssv;
		curr=WhichView.SOUND_VIEW;
    }    
    //跳转到游戏模式选择界面
    public void gotoGameModeView()
    {
    	if(gmv==null)
    	{
    		gmv=new GameModeView(MyActivity.this);
    	}
    	if(!vfd.flag)
    	{
    		vfd.initThread();
    		setContentView(vfd); 
    	}
    	
    	gmv.initThread();
    	vfd.curr=gmv;
		curr=WhichView.GAME_MODE_VIEW;
    }    
    //跳转到关于界面的方法
    public void gotoGuanYuView()
    {
    	if(gyv==null)
    	{
    		gyv=new GuanYuView(MyActivity.this);
    	}    	
    	vfd.curr=gyv;
    	curr=WhichView.GUANYU_VIEW;
    }
    //跳转到欢迎界面的方法
    public void gotoWelcomeView()
    {
    	welV=new WelcomeView(MyActivity.this);
		setContentView(welV);
		curr=WhichView.WELCOME_VIEW;
    }
    //跳转到菜单界面
    public void gotoMenuView()
    {
    	if(vfd==null)
    	{    		
    		vfd=new ViewForDraw(this);    		   		
    	}
    	if(!vfd.flag)
    	{
    		vfd.initThread();
    		setContentView(vfd); 
    	}
    	if(menuV==null)
    	{
    		menuV=new MenuView(MyActivity.this);
    	}    
    	menuV.initThread();
    	vfd.curr=menuV;
		curr=WhichView.MENU_VIEW;
    }
    //跳转到选艇界面
    public void gotoXCView()
    {
    	vfd.flag=false;
    	xcV = new XCSurfaceView(MyActivity.this);
    	xcV.index_boat=BoatInfo.cuttBoatIndex;
    	
        setContentView(xcV);	
        xcV.requestFocus();//获取焦点
        xcV.setFocusableInTouchMode(true);//设置为可触控
        curr=WhichView.XC_VIEW;
    }
    //跳转到计时模式游戏界面
    public void gotoTimerGameView()
    {
    	vfd.flag=false;
    	//重置游戏中参数的方法
    	restartGame();
    	com.bn.clp.Constant.isSpeedMode=false;
    	KeyThread.otherBoatFlag=false;
    	if(BgSoundFlag&&beijingyinyue==null)
		{
    		beijingyinyue=MediaPlayer.create(this,com.bn.R.raw.backsound);
        	beijingyinyue.setLooping(true);//是否循环
			beijingyinyue.start();
		} 
    	gameV = new MyGLSurfaceView(MyActivity.this);
        //获取焦点
        gameV.requestFocus();
        //设置为可触控
        gameV.setFocusableInTouchMode(true);
        setContentView(gameV);      
        curr=WhichView.GAME_VIEW;
    }
    //跳转到竞速模式游戏界面
    public void gotoSpeedGameView()
    {
    	vfd.flag=false;
    	//重置游戏中参数的方法
    	restartGame();
    	com.bn.clp.Constant.isSpeedMode=true;
    	KeyThread.otherBoatFlag=true;
    	if(BgSoundFlag&&beijingyinyue==null)
		{
    		beijingyinyue=MediaPlayer.create(this,com.bn.R.raw.backsound);
        	beijingyinyue.setLooping(true);//是否循环
			beijingyinyue.start();
		}
    	gameV = new MyGLSurfaceView(MyActivity.this);
        //获取焦点
        gameV.requestFocus();
        //设置为可触控
        gameV.setFocusableInTouchMode(true);
        setContentView(gameV);      
        curr=WhichView.GAME_VIEW;
    }
    //跳转到记录查询界面
    public void gotoRecordView()
    {
    	if(dsv==null)
    	{
    		com.bn.st.xc.Constant.initBitmap(this.getResources());
            com.bn.st.xc.Constant.initLoaction();
        	dsv=new DSurfaceView(MyActivity.this);
    	}
        dsv.init();

		vfd.curr=dsv;
		curr=WhichView.RECORD_VIEW;
    } 
    //初始化游戏中的静态参数的方法-重新进入游戏 
    public void restartGame()  
    { 
    	KeyThread.upFlag=true;
    	DaoJiShiForDraw.DAOJISHI_FLAG=true;
    	com.bn.clp.Constant.CURR_BOAT_V=0; 
    	com.bn.clp.Constant.BOAT_A=0;
    	com.bn.clp.Constant.isPaused=false;
    	com.bn.clp.Constant.DEGREE_SPAN=0;  
    	com.bn.clp.Constant.RANK_FOR_HELP=1;
    	com.bn.clp.Constant.halfFlag=false;
    	com.bn.clp.Constant.CURR_BOAT_V_TMD=0;
    	com.bn.clp.Constant.numberOfN2=0;
    	com.bn.clp.Constant.RANK_FOR_HELP=1; 
    	com.bn.clp.Constant.BOAT_LAP_NUMBER_OTHER=new int[2];
    	com.bn.clp.Constant.BOAT_LAP_NUMBER_OTHER[0]=1;
    	com.bn.clp.Constant.BOAT_LAP_NUMBER_OTHER[1]=1;
//    	com.bn.clp.Constant.isFirstPersonView=false;
    	MyGLSurfaceView.isDaoJiShi=true;
    	MyGLSurfaceView.isAllowToClick=false;
    	MyGLSurfaceView.isJiShi=false;
    	MyGLSurfaceView.sight_angle=com.bn.clp.Constant.DIRECTION_INI;    	
    	MyGLSurfaceView.yachtLeftOrRightAngle=0;
    	//新添加的
		MyGLSurfaceView.betweenStartAndPauseTime=0;
		Constant.gameTimeUse=0;
		Constant.numberOfTurns=1;
		Constant.threadFlag=true;
		new RotateThread().start();
    }
    public void exitGame()
    {
    	gameV.onPause();
    	gameV.otherPaths.clear();
    	gameV.tdObjectList.clear();
    	gameV.pzzList.clear();
    	gameV.speedWtList.clear();
    	gameV.treeList.clear();
    	gameV.kzbjList.clear();
    	
    	Constant.threadFlag=false;
    	gameV.kt.flag=false;
		gameV.tc.flag=false;
		gameV.tfe.flag=false;
		DaoJiShiForDraw.DAOJISHI_FLAG=false;
		if(beijingyinyue!=null)
		{
			beijingyinyue.stop();
			beijingyinyue=null;
		}  
		gameV=null;
    }
    @Override
    public void onResume()
    {
    	super.onResume();
    	//注册监听器
        mySensorManager.registerListener
        (			
				mySensorListener, 					//监听器对象
				mySensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),	//传感器类型
				//SensorManager.SENSOR_DELAY_UI		//传感器事件传递的频度
				SensorManager.SENSOR_DELAY_GAME
		);   
        Constant.threadFlag=true;  
        if(gameV!=null)
        {
        	gameV.kt.moveFlag=true;
        }
    }
    @Override
    public void onPause()
    {
    	super.onPause();
    	mySensorManager.unregisterListener(mySensorListener);	//取消注册监听器
        Constant.threadFlag=false;
        if(gameV!=null)
        {
        	gameV.kt.moveFlag=false;
        }
    }  
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e)
    { 
    	//音量键，上键是24，下键是25，以前由于按键时直接走该项，所以导致设置后也不管用
    	if((keyCode==24)||(keyCode==25))
    	{
        	return false;
    	}
    	else if(keyCode==4)
    	{
    		if(curr==WhichView.MENU_VIEW&&menuV.move_flag==0)
    		{    			
    			Settings.System.putInt(this.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,flag);
    			System.exit(0);
    		}
    		else if(curr==WhichView.XC_VIEW)
    		{
    		    xcV.flagForThread=false;
    		    xcV.flag_display=false;
    		    xcV=null;
    			gotoMenuView();
    		}
    		else if(curr==WhichView.GAME_VIEW)
    		{
    			if(DaoJiShiForDraw.DAOJISHI_FLAG)
    			{
    				return true;
    			}
    			else
    			{
    				gotoGameModeView();
    				exitGame();    				
	    			gameV=null;  
    			}    			
    		}
    		else if(curr==WhichView.GAME_MODE_VIEW)
    		{
    			if(gmv==null)
    			{
    				return false;
    			}
    			gmv.move_flag=-1;
    			gmv.flag_go=false;
    			gotoMenuView();
    		}
    		else if(curr==WhichView.SOUND_VIEW)
    		{
    			if(ssv==null)
    			{
    				return false;
    			}
    			ssv.move_flag=-1;
    			ssv.flag_go=false;
    			gotoMenuView();
    		}
    		else if(curr==WhichView.HELP_VIEW)
    		{
    			hsv.flag_go=false; 
    			gotoMenuView();
    		}
    		else if(curr==WhichView.GUANYU_VIEW)
    		{
    			gotoMenuView();
    		}
    		else if(curr==WhichView.RECORD_VIEW)
    		{
    			gotoGameModeView();
    		}
    	}
    	return true;
    }
    
    //创建声音的方法
    public void chushihuaSounds()
    {
    	shengyinChi=new SoundPool
    	(
    		7,
    		AudioManager.STREAM_MUSIC,  
    		100
    	);
    	soundIdMap=new HashMap<Integer,Integer>();
    	soundIdMap=new HashMap<Integer,Integer>();
    	soundIdMap.put(1, shengyinChi.load(this,com.bn.R.raw.pengzhuang,1));//碰撞声音
    	soundIdMap.put(2, shengyinChi.load(this,com.bn.R.raw.boatgo,1));//船加速的声音
    	soundIdMap.put(3, shengyinChi.load(this,com.bn.R.raw.eatthings1,1));//吃东西的声音
    	soundIdMap.put(4, shengyinChi.load(this,com.bn.R.raw.zhuangfei,1));//撞飞东西的声音
    	soundIdMap.put(5, shengyinChi.load(this,com.bn.R.raw.daojishi,1));//321倒计时的声音
    	soundIdMap.put(6, shengyinChi.load(this,com.bn.R.raw.start,1));//开始可以走的声音
    }
    
  //播放声音的方法
    public void shengyinBoFang(int sound,int loop)
    {
    	AudioManager mgr=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
    	float streamVolumeCurrent=mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    	float streamVolumeMax=mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	float volume=streamVolumeCurrent/streamVolumeMax;
    	shengyinChi.play(soundIdMap.get(sound), volume, volume, 1, loop, 1f);
    }
    public int getGLVersion() //获取OPENGLES所支持的最高版本
    {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ConfigurationInfo info = am.getDeviceConfigurationInfo();
        int majorVersion=info.reqGlEsVersion;
        majorVersion=majorVersion>>>16;
        return majorVersion;
    }
    
    
    public Dialog onCreateDialog(int id)   
    {
    	Dialog result=null;
    	switch(id)
    	{
	    	case 1:
	    		cvDialog=new CheckVersionDialog(this);
	    		cvDialog.setCancelable(false);
	    		result=cvDialog;
	    	break;
	    	case 2:
	    		avDialog=new AndroidVersionDialog(this);
	    		avDialog.setCancelable(false);
	    		result=avDialog;
	    	break;
	    	case 3:
	    		bronDialog=new BreakRecordOrNotDialog(this);
	    		bronDialog.setCancelable(false);
	    		result=bronDialog;
	    	break;
	    	case 4:
	    		rDialog=new RankingDialog(this);
	    		rDialog.setCancelable(false);
	    		result=rDialog;
	    	break;
    	} 
		return result;
    }
    public void onPrepareDialog(int id, Dialog dialog)
    {
    	//若不是等待对话框则返回
    	switch(id)
    	{
    	  case 1:
    		   Button bok=(Button)cvDialog.findViewById(R.id.ok_button);
    		   bok.setOnClickListener(
    				new OnClickListener()
    				{
						@Override
						public void onClick(View v) 
						{
							Settings.System.putInt(MyActivity.this.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,flag);
							System.exit(0);
						}	
    				}
    		   );
    	  break;
    	  case 2:
   		  Button ok=(Button)avDialog.findViewById(R.id.ok);
   		   ok.setOnClickListener(
   				new OnClickListener()
   				{
						@Override
						public void onClick(View v) 
						{
							Settings.System.putInt(MyActivity.this.getContentResolver(),Settings.System.ACCELEROMETER_ROTATION,flag);
							System.exit(0);
						}	
   				}
   		   );
   		   break;
    	  case 3:
    		  ok=(Button)bronDialog.findViewById(R.id.ok_button);
    		  if(!com.bn.clp.Constant.isBreakRecord)
    		  {
    				TextView tv=(TextView) bronDialog.findViewById(R.id.tview);
    				tv.setText("非常可惜，没有打破纪录，请再接再厉！");
    		  }
    		  else
    		  {
    				TextView tv=(TextView) bronDialog.findViewById(R.id.tview);
    				tv.setText("恭喜您，成功的突破记录！");    				
    		  }
    		  ok.setOnClickListener(
      				new OnClickListener()
      				{
   						@Override
   						public void onClick(View v)  
   						{
   							bronDialog.cancel();
   							MyActivity.this.hd.sendEmptyMessage(2);
   							exitGame();
   							gameV=null;
   							
   						}	
      				}
      		   );
    	  break;
    	  case 4:
    		 ok=(Button)rDialog.findViewById(R.id.ok_button);
    		 TextView tv=(TextView) rDialog.findViewById(R.id.tview);
    		 tv.setText("恭喜您，您获得了第"+com.bn.clp.Constant.RANK_FOR_HERO_BOAT+"名。");
	   		 ok.setOnClickListener(
 				new OnClickListener()
 				{  
					@Override
					public void onClick(View v)
					{
						rDialog.cancel();
						MyActivity.this.hd.sendEmptyMessage(2);
						exitGame();
						gameV=null;	  							
					}	
 				}
     		  );
   		   break;
    	}
    }
}