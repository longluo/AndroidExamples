package com.bn.tl;

import static com.bn.tl.Constant.*;

import java.util.HashMap;



import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ConfigurationInfo;
import android.content.res.Resources;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
enum WhichView {WELCOME_VIEW,CAIDAN_VIEW,SHEZHI_VIEW,
	GUANYU_VIEW,GAME_VIEW,BANGZHU_VIEW,JILU_VIEW,OVER_VIEW,JIAZAI_VIEW}
public class BasketBall_Shot_Activity extends Activity 
{
	WhichView curr;//当前枚举值
	private GLGameView gameplay;//游戏界面
	public CaiDanView caidanjiemian;//菜单界面
	private GuanYuView guanyujiemian;//关于界面
	private YouXiuJieShuView jieshujiemian;//游戏结束界面
	private ShengyinKGJiemian gamesound;//是否开启声音界面
	private JiLuView lishijilu;//历史记录界面
	Handler xiaoxichuli;//消息处理器
	MediaPlayer beijingyinyue;//游戏背景音乐播放器
	SoundPool shengyinChi;//声音池
	HashMap<Integer,Integer> soundIdMap;//声音池中声音ID与自定义声音ID的Map
	CheckVersionDialog cvDialog;
	AndroidVersionDialog avDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
    	chushihuaSounds();//初始化声音池
    	chushihuaScreen();//初始化屏幕的分辨率
        curr=WhichView.WELCOME_VIEW;
        new Thread()
		{
			public void run()
			{
				try
				{	//加载3D中加载界面的纹理资源
					Constant.loadWelcomeBitmap(BasketBall_Shot_Activity.this.getResources(),
							new int[]{R.drawable.welcome,R.drawable.dott,R.drawable.bangzuwelcome});
					//加载3D中加载界面的shader字符串
					ShaderManager.loadCodeFromFile(BasketBall_Shot_Activity.this.getResources());
					SQLiteUtil.initDatabase();//创建数据库
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
			}			
		}.start();
		//初始化消息处理器
        xiaoxichuli=new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				super.handleMessage(msg);
				switch(msg.what)
        		{        
				case SHENGYING_KG_JIEMIAN://进入声音设置界面
					curr=WhichView.SHEZHI_VIEW;
					gamesound=new ShengyinKGJiemian(BasketBall_Shot_Activity.this);
					setContentView(gamesound);
					break;
				case CAIDAN_JIEMIAN://进入菜单选择界面	
					curr=WhichView.CAIDAN_VIEW;
					caidanjiemian=new CaiDanView(BasketBall_Shot_Activity.this);
					setContentView(caidanjiemian);
					break;
				case JIAZAI_JIEMIAN://进入加载主场景中的资源
					curr=WhichView.JIAZAI_VIEW;
					gameplay = new GLGameView(BasketBall_Shot_Activity.this);					
					Resources r=BasketBall_Shot_Activity.this.getResources();
					gameplay.initObjectWelcome(r);								
					if(isBJmiusic)//播放背景音乐
					{
				    	beijingyinyue.start();	
					}    
					xiaoxichuli.sendEmptyMessage(YOUXI_JIEMIAN);	
					break;
				case YOUXI_JIEMIAN:		//游戏界面
					flag=true;
					isnoPlay=true;//是否播放视频
        			setContentView(gameplay); //这里切换到游戏界面中
        			gameplay.requestFocus();
					gameplay.setFocusableInTouchMode(true);
  					break;
				case GUANYU_JIEMIAN://关于界面
					curr=WhichView.GUANYU_VIEW;
					guanyujiemian=new GuanYuView(BasketBall_Shot_Activity.this);
					setContentView(guanyujiemian);
					break;
				case BANGZHU_JIEMIAN://帮助界面
					curr=WhichView.BANGZHU_VIEW;
					isnoHelpView=true;//该界面为帮助界面
					xiaoxichuli.sendEmptyMessage(JIAZAI_JIEMIAN);//进入加载界面
					break;
				case JIESHU_JIEMIAN://游戏结束
					curr=WhichView.OVER_VIEW;
					jieshujiemian=new YouXiuJieShuView(BasketBall_Shot_Activity.this,caidanjiemian);
					setContentView(jieshujiemian);
					break;
				case CAIDAN_RETRY://菜单界面
					curr=WhichView.CAIDAN_VIEW;
					caidanjiemian=new CaiDanView(BasketBall_Shot_Activity.this);
					setContentView(caidanjiemian);
					break;
				case JILU_JIEMIAN://记录界面
					curr=WhichView.JILU_VIEW;
					lishijilu=new JiLuView(BasketBall_Shot_Activity.this);//记录界面
					setContentView(lishijilu);
					break;
        		}
			}
		};
		//这里跳到菜单界面
		xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);
		//判断当前Android版本是不是低于2.2
	    if(Build.VERSION.SDK_INT<Build.VERSION_CODES.FROYO)
	    {
	    this.showDialog(2);
	    }
	    //判断当前系统所支持的最高opengles版本是不是大于2
	    else if(this.getGLVersion()<2)  
	    {
	    this.showDialog(1);
	    }
		
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
    		result=cvDialog;
    		break;
    	case 2:
    		avDialog=new AndroidVersionDialog(this);
    		result=avDialog;
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
							System.exit(0);
						}	
   				}
   		   );
   		   break;
    	}
    }

    //初始化屏幕分辨率
    public void chushihuaScreen()
    {
    	requestWindowFeature(Window.FEATURE_NO_TITLE);//去掉通知栏
    	getWindow().setFlags//全屏显示
    	(
    		WindowManager.LayoutParams.FLAG_FULLSCREEN,
    		WindowManager.LayoutParams.FLAG_FULLSCREEN
    	);
    	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//竖屏显示
    	
    	//获取屏幕分辨率
        DisplayMetrics dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int tempHeight=(int) (SCREEN_HEIGHT=dm.heightPixels);
        int tempWidth=(int) (SCREEN_WIDHT=dm.widthPixels); 
        
        if(tempHeight>tempWidth)
        {
        	SCREEN_HEIGHT=tempHeight;
        	SCREEN_WIDHT=tempWidth;
        }
        else
        {
        	SCREEN_HEIGHT=tempWidth;
        	SCREEN_WIDHT=tempHeight;
        }
        float zoomx=SCREEN_WIDHT/480;
		float zoomy=SCREEN_HEIGHT/800;
		if(zoomx>zoomy){
			ratio_width=ratio_height=zoomy;
			
		}else
		{
			ratio_width=ratio_height=zoomx;
		}
		sXtart=(SCREEN_WIDHT-480*ratio_width)/2;
		sYtart=(SCREEN_HEIGHT-800*ratio_height)/2;
    	
    }
    @Override
	public boolean onKeyDown(int keyCode, KeyEvent e)
    { 
    	if(keyCode!=4)
    	{
    		return false;
    	}
    	if(curr==WhichView.SHEZHI_VIEW||curr==WhichView.GUANYU_VIEW
    			||curr==WhichView.JILU_VIEW||curr==WhichView.BANGZHU_VIEW||
    			curr==WhichView.OVER_VIEW){
    		//如果是设置界面,关于界面，记录界面,帮助界面,游戏结束界面
    		xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
    		return true;
    	}
    	if(curr==WhichView.CAIDAN_VIEW){//菜单界面
    		System.exit(0);
    		return true;
    	}
    	if(curr==WhichView.GAME_VIEW)//游戏界面
    	{
    		isnoHelpView=false;
    		flag=false;//现场停止
    		shipingJs=0;//视频现场归零
    		xiaoxichuli.sendEmptyMessage(CAIDAN_RETRY);//返回到菜单界面
    		return true;
    	}
    	return true;
    }
    //创建声音的方法
    public void chushihuaSounds()
    {
    	beijingyinyue=MediaPlayer.create(this,R.raw.beijingyingyu);
    	beijingyinyue.setLooping(true);//是否循环
    	beijingyinyue.setVolume(0.2f, 0.2f);//声音大小
    	shengyinChi=new SoundPool
    	(
    		4,
    		AudioManager.STREAM_MUSIC,
    		100
    	);
    	soundIdMap=new HashMap<Integer,Integer>();
    	soundIdMap=new HashMap<Integer,Integer>();
    	soundIdMap.put(1, shengyinChi.load(this,R.raw.pengzhuang,1));//碰撞声音
    	soundIdMap.put(2, shengyinChi.load(this,R.raw.levelend,1));//游戏时间结束声音
    	soundIdMap.put(3, shengyinChi.load(this,R.raw.shoot,1));//进球声音
    }
    //播放声音的方法
    public void shengyinBoFang(int sound,int loop)
    {
    	if(!isCJmiusic){
    		return;
    	}
    	AudioManager mgr=(AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
    	float streamVolumeCurrent=mgr.getStreamVolume(AudioManager.STREAM_MUSIC);
    	float streamVolumeMax=mgr.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    	float volume=streamVolumeCurrent/streamVolumeMax;
    	shengyinChi.play(soundIdMap.get(sound), volume, volume, 1, loop, 0.5f);
    }
}