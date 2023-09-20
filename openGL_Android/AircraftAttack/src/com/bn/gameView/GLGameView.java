package com.bn.gameView;

import static com.bn.gameView.Constant.*;

import java.util.ArrayList;
import java.util.Collections;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.view.MotionEvent;
import android.widget.Toast;

import com.bn.archieModel.ArchieForControl;
import com.bn.archieModel.BarbetteForDraw;
import com.bn.archieModel.BarrelForDraw;
import com.bn.arsenal.Arsenal_House;
import com.bn.arsenal.House;
import com.bn.arsenal.PlaneHouse;
import com.bn.commonObject.BallTextureByVertex;
import com.bn.commonObject.CubeForDraw;
import com.bn.commonObject.DamForDraw;
import com.bn.commonObject.DrawBomb;
import com.bn.commonObject.LandForm;
import com.bn.commonObject.Light_Tower;
import com.bn.commonObject.NumberForDraw;
import com.bn.commonObject.SkyBall;
import com.bn.commonObject.SkyNight;
import com.bn.commonObject.TextureRect;
import com.bn.commonObject.Tree;
import com.bn.core.MatrixState;
import com.bn.core.SQLiteUtil;
import com.bn.core.ShaderManager;
import com.bn.menu.Aircraft_Activity;
import com.bn.menu.MissileMenuForDraw;
import com.bn.menu.R;
import com.bn.planeModel.EnemyPlane;
import com.bn.planeModel.Plane;
import com.bn.tankemodel.MoXingJiaZai;
import com.bn.tankemodel.Model;
import com.bn.tankemodel.TanKe;



public class GLGameView extends GLSurfaceView {
	public Aircraft_Activity activity;// 主Activity引用
	private final float TOUCH_SCALE_FACTOR = 180.0f / 480;// 角度缩放比例
	private SceneRenderer mRenderer;// 场景渲染器
	private int load_step = 0;// 加载资源的步数
	private boolean isLoadedOk = false;// 是否加载完成的标志位
	private float mPreviousY;// 上次的触控位置Y坐标
	
	// 摄像机的相关参数 供其他地方调用
	public static float cx;
	public static float cy;
	public static float cz;
	public static float tx;
	public static float ty;
	public static float tz;
	public static float upX = 0;
	public static float upY = 1;
	public static float upZ = 0;
	
	// --------当然帧的数值
	float curr_cx;
	float curr_cy;
	float curr_cz;
	float curr_tx;
	float curr_ty;
	float curr_tz;
	float curr_upx;
	float curr_upy;
	float curr_upz;
	float curr_PlaneX;
	float curr_PlaneZ;
	float curr_PlaneY;
	float curr_rot_Plane_X;
	float curr_rot_Plane_Y;
	float curr_rot_Plane_Z;
	public KeyThread kThread;
	public static boolean isVideoPlaying = true;// 是否在播放界面或者暂停界面，true为播放状态
	
	// 透视投影的缩放比
	public float ratio;
	
	// --------3D物件------------------------------
	TextureRect loadingView;// 3D中的加载界面
	TextureRect processBar;// 加载界面中的进度条
	LandForm terrain[] = new LandForm[LANDS_SIZE];// 创建陆地 0
													// 表示yz方向上倾斜，1表示拐角位置，等高线向里凹
	TextureRect terrain_plain;// 创建平面地图 平行于XY平面的
	TextureRect water;// 水面
	
	// ----------------------------------
	TextureRect fireButton;// 开火按钮
	TextureRect radar_bg;// 雷达背景
	TextureRect radar_plane;// 雷达的指针飞机
	TextureRect weapon_button;// 武器选择按钮
	NumberForDraw weapon_number;// 用于标识子弹和炮弹的数量

	TextureRect up_button;// 向上按钮
	TextureRect down_button;// 向下选择按钮
	NumberForDraw numberRect;// 数字
	TextureRect leftTimeRect;// 剩余时间
	TextureRect backgroundRect_blood;// 显示血背景矩形
	TextureRect plane_Hit;// 飞机被击中表示矩形
	DamForDraw dam;// 大坝

	public static TextureRect bombRect;// 爆炸矩形
	public static TextureRect bombRectr;// 爆炸矩形
	public static float bomb_width = 40;
	public static float bomb_height = 50;// 爆炸纹理矩形的大小

	SkyBall skyBall;// 天空球白天
	SkyBall skyBallsmall;// 九宫格笼罩的天空
	SkyNight skynight;// 晚上的天空
	SkyNight skynightBig;// 大一点的星星
	public Plane plane;// 飞机
	BallTextureByVertex bullet_ball;// 子弹纹理球
	public TextureRect bullet_rect;// 子弹纹理矩形
	
	// 创建高射炮组件
	BarrelForDraw barrel;
	BarbetteForDraw barbette;
	CubeForDraw cube;
	ArchieForControl archie;// 高射炮

	public Model tanke_body;// 坦克身体
	public Model tanke_gun;// 坦克炮管
	public static ArrayList<TanKe> tankeList = new ArrayList<TanKe>();
	public static ArrayList<DrawBomb> baoZhaList = new ArrayList<DrawBomb>();// 坦克爆炸绘制列表
	public ArrayList<DrawBomb> copybaozhaList = new ArrayList<DrawBomb>();// 坦克爆炸绘制列表
	public static ArrayList<EnemyPlane> enemy = new ArrayList<EnemyPlane>();
	Light_Tower lighttower;// 灯塔
	public static ArrayList<BombForControl> cop_archie_bomb_List = new ArrayList<BombForControl>();// 高射炮炮弹的列表
	public static ArrayList<BombForControl> cop_bomb_List = new ArrayList<BombForControl>();// 发射出去的子弹列表
	public static ArrayList<ArchieForControl> cop_archie_List = new ArrayList<ArchieForControl>();// 高射炮的列表
	public static ArrayList<BulletForControl> cop_bullet_List = new ArrayList<BulletForControl>();// 发射出去的子弹列表
	public static ArrayList<BombForControl> copy_tank_bomb_List = new ArrayList<BombForControl>();// 发射出去的坦克炮弹列表
	public static ArrayList<Tree> treeList = new ArrayList<Tree>();// 地形上的树
	House house;// 军火库模型
	public static ArrayList<Arsenal_House> arsenal = new ArrayList<Arsenal_House>();// 军火库
	public ArrayList<PlaneHouse> houseplane = new ArrayList<PlaneHouse>();
	public CubeForDraw housePlane;// 平房
	public Light_Tower chimney;// 烟囱

	// 菜单，视频播放界面等纹理
	TextureRect menu_Rect;// 飞机爆炸后的菜单显示矩形
	TextureRect menu_video;// 视频播放界面的各个按钮矩形
	TextureRect mark_placeRect;// 标志其位置的在仪表盘上的。
	public TextureRect mark_lock;// 标记被锁定的矩形
	public TextureRect treeRect;
	public TextureRect mark_aim;// 目标标记框
	public TextureRect noticeRect;// 战争说明文字
	public float initNoticeHeight = -0.8f;// 初始提示文字的高度

	// ------游戏相关纹理ID---------------------------------------------------
	// ---------游戏开始前的说明文字
	private int tex_noticeId[] = new int[6];
	private int tex_actionWinId;// 特殊行动成功对话框
	private int tex_actionFailId;// 失败对话框
	private int tex_numberRectId;// 数字纹理
	private int tex_backgroundRectId;// 血背景图片
	private int tex_lefttimeId;// 剩余时间纹理
	private int tex_lighttowerid;
	private int tex_lightid;// 灯柱子纹理
	private int tex_loadingviewId;// 加载界面的ID
	private int tex_processId;// 进度条
	private int tex_terrain_tuceng_Id;// 地形纹理 ----土层
	private int tex_terrain_caodiId;// 地形纹理-----草地
	private int tex_terrain_shitouId;// 地形纹理------石头
	private int tex_terrain_shandingId;// 地形纹理------石头
	private int tex_fireButtonId;// 开火按钮的纹理
	private int tex_skyBallId;// 天空球纹理
	private int tex_nightId;// 晚上天空纹理
	private int tex_waterId;// 水面纹理
	private int tex_bulletId;// 子弹纹理
	private int tex_radar_bg_Id;// 雷达背景纹理
	private int tex_radar_plane_Id;// 雷达的飞机指针
	private int tex_button_weaponId[] = new int[2];// 武器按钮图标
	private int tex_button_upId;// 向上按钮纹理
	private int tex_button_downId;// 向下按钮纹理
	private int tex_tankeid;// 坦克ID
	private int tex_roofId;// 屋顶
	private int tex_frontId;// 房屋侧面纹理
	private int tex_AnnulusId;// 围绕房屋转的圆环纹理
	private int tex_damId;// 大坝的纹理
	private int tex_chimneyId;// 烟囱纹理id
	private int tex_housePlaneId[] = new int[2];// 平房的纹理Id
	private int tex_housePlaneSmallId[] = new int[2];// 小平房纹理
	public int treeTexId;// 树纹理
	public int treeTexId_2;// 第二种树
	public static int baoZhaXiaoguo2;// 爆炸效果2
	public static int baoZhaXiaoguo;// 爆炸效果纹理
	public static int baoZhaTexId[];// 爆炸纹理数组
	// 飞机杯击中的纹理
	public int tex_plane_hitId;// 飞机被击中的表示纹理矩形
	public int tex_locktexId;// 锁定矩形纹理
	public int tx_lockaimtexId;// 目标框锁定
	// 界面菜单各种纹理，飞机坠毁后的
	public int tex_menu_text;// 文字
	public int tex_menu_text_win;// 赢菜单
	// 视频播放按钮，演示过程按钮
	public int stopId;// 停止按钮
	public int pauseId;// 暂停按钮
	public int playId;// 播放按钮
	
	// 标志其位置的在仪表盘上的
	public int tex_mark_tanke;// 坦克和高射炮仪表盘图标
	public int tex_mark_ackId;// 敌机仪表盘图标
	public int tex_mark_arsenalId;// 军火库仪表盘图标
	public int tex_mark_planeId;// 飞机仪表盘图标
	
	// -------飞机的相关纹理
	public int planeHeadId; // 机头
	public int frontWingId; // 前机翼纹里
	public int frontWing2Id; // 前机翼纹里2
	public int bacckWingId; // 后机翼纹理
	public int topWingId; // 上机翼纹理
	public int planeBodyId; // 机身纹理
	public int planeCabinId; // 机舱纹理
	public int cylinder1Id; // 圆柱纹理1
	public int cylinder2Id; // 圆柱纹理2
	public int screw1Id; // 螺旋桨纹理
	// ---------高射炮的相关纹理
	public int[] texBarbetteId = new int[2];// 0表示炮台圆柱纹理1表示炮台上圆面纹理
	public int texCubeId;// 挡板纹理
	public int[] texBarrelId = new int[4];// 其中0表示长炮筒圆柱,1表示长炮筒圆面,2表示短炮筒圆柱,3表示短炮筒圆面
	// 摄像机移动
	float time_span = 10;// 这里只每次移动的距离
	float degree_span = 10;// 这里只每次旋转地角度

	float[] fa = new float[16];
	float[] fb = new float[16];
	float[] fc = new float[16];
	float[] resultUp = new float[4];
	float[] resultxUp = new float[4];
	float[] YB = new float[4];
	float lightAngle = 0;
	// --------------------------导弹菜单部分
	public MissileMenuForDraw missile_menu;// 创建导弹菜单
	public TextureRect menu_Background;// 导弹菜单部分的大地背景
	public TextureRect menu_clouds;// 导弹菜单部分的云彩
	public TextureRect front_frame;// 导弹菜单部分最前面的框架
	public TextureRect front_cover_button;// 导弹菜单部分最前面的罩子
	public TextureRect front_door;// 导弹菜单部分机舱门
	public TextureRect front_door_bg;// 到底才菜单的部分机仓门
	public TextureRect menu_setting;// 设置页面的按钮
	public TextureRect helpView;// 帮助界面
	public TextureRect aboutView;// 关于界面
	public NumberForDraw rank_number;// 排行榜界面中数字
	public TextureRect map_name;// 排行榜界面地图的名称
	// -----------------选飞机场景相关参数--------------------------------------
	public float planeRotate = 0;

	// -------------------------------二级菜单中----------------------------
	public int planeModelIndex = 0;// 0表示第一家飞机,1第二架飞机,2第三架飞机
	CircleForDraw circle_station;// 飞机的展示台
	TextureRect backgroundRect;// 飞机展示台的背景

	Model planeModel[] = new Model[3];
	int planeModelTexId[] = new int[3];
	// 三架飞机的缩放比例
	public static final float RATIO_PLANE = 1.0f;

	TextureRect plane_select_head;// 选飞机场景中的标题栏
	TextureRect plane_select_plane;// 选择飞机按钮
	TextureRect menu_two_game_model_btn;// 选择游戏模式按钮

	TextureRect menu_two_button;// 二级菜单中的按钮
	TextureRect menu_two_plane_icon;// 二级菜单中的三个飞机的模型图片

	int tex_plane_select_head;// 标题纹理
	int tex_plane_select_planeIndex = 0;// 选飞机按钮纹理索引
	int tex_plane_select_modelIndex = 0;// 选模式按钮纹理
	int tex_menu_two_war_btnIndex = 1;
	int tex_menu_two_war_btnId[] = new int[2];// 战役模式
	int tex_menu_two_action_btnIndex;
	int tex_menu_two_action_btnId[] = new int[2];// 特别行动
	int tex_model_select_promptId;// 选择模式提示
	int tex_menu_two_okIndex;// 确定按钮
	int tex_menu_two_leftIndex;// 左按按钮
	int tex_menu_two_rightIndex;// 右按按钮
	int tex_menu_two_okId[] = new int[2];// 确认按钮
	int tex_menu_two_leftId[] = new int[2];// 左按按钮
	int tex_menu_two_rightId[] = new int[2];// 右按按钮
	int tex_special_action_bgId;// 特别行动背景图
	int tex_menu_two_plane_iconIndex[] = { 1, 0, 0 };// 菜单二中飞机图片纹理ID
	int tex_menu_two_plane_iconId[][] = new int[3][2];// 菜单二中飞机图片纹理

	public boolean isPlaneBtnSelected = false;// 飞机选择按钮状态
	public boolean isDrawGameModelView = false;// 飞机选择按钮状态
	public boolean idPlaneSelectedPrompt = true;// 选择飞机的提示
	public boolean isModelSelectedPrompt = false;// 选择模式的提示
	public boolean isPlaneSelectOk = false;// 弹出飞机选择按钮后的OK按钮
	public boolean isModelSelectOk = false;// 弹出模式选择按钮后的OK按钮
	public boolean isPlaneBtnDown = false;// 选飞机按钮是否已经按下
	public boolean isModeBtnDown = false;// 选模式按钮是否已经按下

	// ----------------游戏模式------
	public int isGameMode;// 0战役模式1特别行动

	// -----选择飞机按钮的模式 0表示正常模式 1表示循环变换模式 ,2 表示按下模式
	public int planeModeType = 1;
	// -----选择模式按钮的模式 0表示正常模式 1表示循环变换模式 ,2 表示按下模式
	public int war_button_mode = 1;
	// ----按钮按下去选飞机界面---0表示展开,1表示关闭,2表示正常显示
	public int selectPlaneOPen = 0;
	// ----按钮按下去选模式界面---0表示展开,1表示关闭,2表示正常显示
	public int selectModelOPen = 0;
	// 当前的不透明度
	public float currAlpha_planeBtn = 1.0f;
	public float operator_planeBtn = -1;
	public float currAlpha_modelBtn = 1.0f;
	public float operator_modelBtn = -1;
	// 当前点的位置
	public float currX = 0;
	public float operator2 = 1;

	// -------------------菜单部分的纹理--------------------------------
	public int tex_rectId[] = new int[11];// 导弹菜单的纹理
	public int tex_bgId;// 导弹菜单下的背景图
	public int tex_cloudsId;// 导弹菜单下的云彩
	public int tex_front_frameId;// 导弹菜单最前边的前景图
	public int tex_front_coverId;// 导弹菜单最前边的罩子
	public int tex_menu_doorId;// 机舱门纹理
	public int tex_musicId[] = new int[2];// 是否开启音乐纹理
	public int tex_soundId[] = new int[2];// 是否开启特效声音纹理
	public int tex_vibrateId[] = new int[2];// 是否开启震动
	public int tex_helpId;// 帮助界面Id
	public int tex_aboutId;// 关于界面Id
	public int tex_mapSelectedBgId;// 地图选择界面的背景
	public int tex_mapId[] = new int[3];// 地图名称纹理图
	public int tex_rankBgId;// 排行榜背景图
	public int tex_rankNumberId;// 排行榜数字纹理

	// -------------------------菜单部分相关参数
	public boolean isGameOn = false;// 判断游戏是否已经开始
	public float missile_rotation = 0;// 导弹的旋转
	public boolean isTouchMoved = false;// 当前是否正在触摸移动中
	public boolean hasInertia = false;// 判断当前是否需要惯性
	public float ori_angle_speed = 15;// 导弹菜单最大的角速度
	public float curr_angle_speed;// 当前导弹菜单旋转地角速度
	public float ori_acceleratedSpeed = 0.1f;// 初始加速度
	public float curr_acceleratedSpeed;// 当前加速度
	public boolean auto_adjust = false;// 是否进行自动调整
	public int curr_menu_index = 0;// 当前菜单选项的索引
	public boolean isMissileDowning;// 判断导弹是否正在下落
	public float missile_ZOffset_AcceSpeed = -0.4f;// 导弹的Z轴偏移量的加速度
	public float missile_ZOffset_Speed;// 导弹的Z轴偏移量的速度
	public float missile_ZOffset_Ori = -1.5f;// 导弹的Z轴初始偏移量
	public float missile_ZOffset = missile_ZOffset_Ori;// 导弹的Z轴偏移量
	public float missile_YOffset = 0;// 导弹 Y轴的偏移量
	public int doorState = 1;// 1表示打开,2表示关闭 0表示运动
	public float door_YOffset = 1.5f;// 机舱门的Y轴偏移量 1.5~0.5
	public float door_YSpan = 0.08f;// 机舱门的速度

	public int dialog_YesId = 0;// 对话框中确定按钮的图片索引
	public int dialog_NoId = 0;// 对话框中返回按钮的图片索引
	public boolean moveToExit;// 按返回键是否移动到退出按钮菜单处
	public float help_YOffset = 0;// 帮助界面的Y轴偏移量
	public float about_YOffset = 0;// 关于界面的Y轴偏移量
	public boolean isPoint;// 点击按钮事件是否成功
	public int isMenuLevel = 0;// 设置菜单级别界面 一级,二级,三级菜单
	public float rank_move;// 排行榜中触摸滑动的比例
	public boolean isDrawBaozha;// 导弹菜单中是否绘制爆炸图
	public float baozha_ratio;// 导弹爆炸图的缩放比例
	public float baozha_increase = 0.05f;// 导弹爆炸图的增粘比例
	public boolean menu_button_move;// 菜单按钮是否右移,当点击开始后,菜单按钮右移
	public float menu_button_speed = 0.15f;// 菜单按钮的右移速度
	public float menu_button_XOffset;// 菜单按钮的右移速度
	// -------数据库相关操作-------
	ArrayList<String[]> rank = new ArrayList<String[]>();// 用于数据库记录信息
	// ---------特别行动中用于记录时间
	public int goTime;
	public float oldTime;
	// -------------特殊行动成功失败的状态 1表示成功2表示失败
	public int isSpecActionState;
	public boolean isTrueButtonAction = false;// 实体键还是虚拟按钮
	// 台子的纹理id
	public int stageId;// 展览台子纹理id
	// --------------触摸事件相关参数----------------
	private boolean isOKButtonDown;
	private boolean isLeftButtonDown;
	private boolean isRightButtonDown;
	private boolean isWarButtonDown;
	private boolean isActionButtonDown;

	// ------------------二级菜单中改变按钮的不透明度
	private boolean isChangeAlpha = true;
	private float currAlpha = 1.0f;
	private int direction = -1;

	public GLGameView(Context context) {
		super(context);
		activity = (Aircraft_Activity) context;
		this.setEGLContextClientVersion(2); // 设置使用OPENGL ES2.0
		mRenderer = new SceneRenderer(); // 创建场景渲染器
		setRenderer(mRenderer); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
		this.setKeepScreenOn(true);
		tx = PLANE_X = MapArray[mapId].length * WIDTH_LALNDFORM / 2;
		ty = PLANE_Y;// 计算摄像机的位置
		tz = PLANE_Z = MapArray[mapId].length * WIDTH_LALNDFORM / 2;
		cx = (float) (tx + Math.cos(Math.toRadians(ELEVATION_CAMERA))
				* Math.sin(Math.toRadians(DIRECTION_CAMERA)) * DISTANCE);// 摄像机x坐标
		cz = (float) (tz + Math.cos(Math.toRadians(ELEVATION_CAMERA))
				* Math.cos(Math.toRadians(DIRECTION_CAMERA)) * DISTANCE);// 摄像机z坐标
		cy = (float) (ty + Math.sin(Math.toRadians(ELEVATION_CAMERA))
				* DISTANCE);// 摄像机y坐标
	}

	int shootId = 2;// 发射按钮的ID
	int upId = 2;
	long time;

	// 触摸事件回调方法
	@Override
	public boolean onTouchEvent(MotionEvent e) {
		if (isGameOn) {
			int actionId = e.getAction() & MotionEvent.ACTION_MASK;// 获取触控事件ID
			// 获取主、辅点id（down时主辅点id皆正确，up时辅点id正确，主点id要查询Map中剩下的一个点的id）
			int id = (e.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >>> MotionEvent.ACTION_POINTER_ID_SHIFT;
			float x = e.getX(id);
			float y = e.getY(id);
			switch (actionId) {
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN:
				if (isGameMode == 1 && isSpecActionState == 1)// 特别行动----------成功
				{
					if (x > 280 * ratio_width && x < 540 * ratio_width
							&& y > 186 * ratio_height && y < 270 * ratio_height)// 下一任务
					{
						kThread.flag_go = false;// 线程标志位
						plane.blood = plane_blood;
						if (mapId + 1 < 6) {
							mapId++;
							goTime = actionTimeSpan[mapId - 3];
							oldTime = 0;
							initMap();// 重新创建高射炮和坦克
							initMap_Value();// 初始化各种值
							kThread = new KeyThread(this);
							kThread.start();
							if (isMusicOn == 0
									&& !activity.bgMusic[1].isPlaying()) {
								activity.bgMusic[1].start();
							}
							isSpecActionState = 0;// 特别行动状态:既没有成功也没有失败
						} else {
							Toast.makeText(activity,
									"没有新的行动,请点击菜单按钮,返回菜单界面!!!",
									Toast.LENGTH_LONG).show();
						}
					} else if (x > 280 * ratio_width && x < 540 * ratio_width
							&& y > 270 * ratio_height && y < 370 * ratio_height)// 菜单
					{
						Toast.makeText(activity,
								"--------菜单--------------------", 1000).show();
						kThread.flag_go = false;// 线程标志位
						missile_ZOffset = missile_ZOffset_Ori;
						missile_rotation = 0;
						isCrash = false;
						is_button_return = false;
						isGameOn = false;// 游戏是否进行中设为不在进行中
						plane.blood = plane_blood;
						kThread = new KeyThread(this);
						kThread.start();
						if (activity.bgMusic[1].isPlaying()) {
							activity.bgMusic[1].pause();
						}
						if (0 == isMusicOn) {
							activity.bgMusic[0].start();
						}
						isSpecActionState = 0;// 特别行动状态:既没有成功也没有失败
					}
				} else if (isGameMode == 1 && isSpecActionState == 2)// 特别行动--------------失败
				{
					if (x > 280 * ratio_width && x < 540 * ratio_width
							&& y > 175 * ratio_height && y < 270 * ratio_height)// 重新挑战
					{
						kThread.flag_go = false;// 线程标志位
						plane.blood = plane_blood;
						goTime = actionTimeSpan[mapId - 3];
						oldTime = 0;
						initMap();// 重新创建高射炮和坦克
						initMap_Value();// 初始化各种值
						kThread = new KeyThread(this);
						kThread.start();
						if (isMusicOn == 0 && !activity.bgMusic[1].isPlaying()) {
							activity.bgMusic[1].start();
						} else {
							Toast.makeText(activity,
									"没有新的行动,请点击菜单按钮,返回菜单界面!!!",
									Toast.LENGTH_LONG).show();
						}
						isSpecActionState = 0;// 特别行动状态:既没有成功也没有失败
					} else if (x > 280 * ratio_width && x < 540 * ratio_width
							&& y > 270 * ratio_height && y < 370 * ratio_height)// 菜单
					{
						kThread.flag_go = false;// 线程标志位
						missile_ZOffset = missile_ZOffset_Ori;
						missile_rotation = 0;
						isCrash = false;
						is_button_return = false;
						isGameOn = false;// 游戏是否进行中设为不在进行中
						plane.blood = plane_blood;
						kThread = new KeyThread(this);
						kThread.start();
						if (activity.bgMusic[1].isPlaying()) {
							activity.bgMusic[1].pause();
						}
						if (0 == isMusicOn) {
							activity.bgMusic[0].start();
						}
						isSpecActionState = 0;// 特别行动状态:既没有成功也没有失败
					}
					return true;
				} else// --------------------然后是其他情况
				{
					// --------------------------战役模式失败了--------或者------视频前的对话框---------------------------------------------
					if ((!isVideo && isCrash && isCrashCartoonOver)
							|| (!isVideo && is_button_return)
							|| (isVideo && !isVideoPlaying && (!isVideoPlaying && isTrueButtonAction))) {
						if (x > 200 * ratio_width && x < 600 * ratio_width
								&& y > 174 * ratio_height
								&& y < 200 * ratio_height)// ------继续按钮
						{
							if (!isVideo && is_button_return)// 如果不是在视频播放界面
							{
								is_button_return = !is_button_return;
								if (isMusicOn == 0
										&& !activity.bgMusic[1].isPlaying()) {
									activity.bgMusic[1].start();
								}
							} else if (isVideo && !isVideoPlaying
									&& (!isVideoPlaying && isTrueButtonAction))// 如果在视频播放界面
							{
								isVideoPlaying = true;
								if (isMusicOn == 0
										&& !activity.bgMusic[1].isPlaying()) {
									activity.bgMusic[1].start();
								}
							} else// --------战役模式失败了----------------------------
							{
								kThread.flag_go = false;// 线程标志位
								plane.blood = plane_blood;
								initMap();// 重新创建高射炮和坦克
								initMap_Value();// 初始化各种值
								kThread = new KeyThread(this);
								kThread.start();
								if (isMusicOn == 0
										&& !activity.bgMusic[1].isPlaying()) {
									activity.bgMusic[1].start();
								}
							}
							return true;
						} else if (x > 200 * ratio_width
								&& x < 600 * ratio_width
								&& y > 250 * ratio_height
								&& y < 310 * ratio_height)// 返回菜单菜单
						{
							kThread.flag_go = false;// 线程标志位
							missile_ZOffset = missile_ZOffset_Ori;
							missile_rotation = 0;
							isCrash = false;
							is_button_return = false;
							isVideoPlaying = true;
							isGameOn = false;// 游戏是否进行中设为不在进行中
							plane.blood = plane_blood;
							kThread = new KeyThread(this);
							kThread.start();
							if (activity.bgMusic[1].isPlaying()) {
								activity.bgMusic[1].pause();
							}
							if (0 == isMusicOn) {
								activity.bgMusic[0].start();
							}
							return true;
						}

					}
					// ------------------------------赢得本关对话框----------------------------------------------
					if (isOvercome && isCrashCartoonOver) {
						if (x > 200 * ratio_width && x < 600 * ratio_width
								&& y > 124 * ratio_height
								&& y < 180 * ratio_height)// 下一关按钮
						{
							kThread.flag_go = false;// 线程标志位
							plane.blood = plane_blood;
							if (mapId + 1 < 3) {
								mapId++;
								initMap();// 重新创建高射炮和坦克
								initMap_Value();// 初始化各种值
								kThread = new KeyThread(this);
								kThread.start();
								if (isMusicOn == 0
										&& !activity.bgMusic[1].isPlaying()) {
									activity.bgMusic[1].start();
								}
							} else {
								Toast.makeText(activity,
										"没有新的战役,请点击菜单按钮,返回菜单界面!!!",
										Toast.LENGTH_LONG).show();
							}
						} else if (x > 200 * ratio_width
								&& x < 600 * ratio_width
								&& y > 200 * ratio_height
								&& y < 260 * ratio_height)// 重玩按钮
						{
							kThread.flag_go = false;// 线程标志位
							plane.blood = plane_blood;
							initMap();// 重新创建高射炮和坦克
							initMap_Value();// 初始化各种值
							kThread = new KeyThread(this);
							kThread.start();
							if (isMusicOn == 0
									&& !activity.bgMusic[1].isPlaying()) {
								activity.bgMusic[1].start();
							}
						} else if (x > 200 * ratio_width
								&& x < 600 * ratio_width
								&& y > 300 * ratio_height
								&& y < 360 * ratio_height)// 返回菜单按钮
						{
							kThread.flag_go = false;// 线程标志位
							missile_ZOffset = missile_ZOffset_Ori;
							missile_rotation = 0;
							isCrash = false;
							is_button_return = false;
							isGameOn = false;// 游戏是否进行中设为不在进行中
							plane.blood = plane_blood;
							kThread = new KeyThread(this);
							kThread.start();
							if (activity.bgMusic[1].isPlaying()) {
								activity.bgMusic[1].pause();
							}
							if (0 == isMusicOn) {
								activity.bgMusic[0].start();
							}
						}
						return true;
					}
				}
				if (isCrash || isOvercome) {// 如果是飞机或者军火库炸毁了，触摸不管用
					break;
				}
				// -------------------------------------------------------------------------------
				if (isVideo && !(!isVideoPlaying && isTrueButtonAction))// 如果是视频播放界面
				{
					isFireOn = false;
					if (x < 160 * ratio_width && y > 404 * ratio_height)// --------------播放暂停界面---------------------
					{
						isTrueButtonAction = false;// 虚拟按钮动作
						isVideoPlaying = !isVideoPlaying;// 播放或者暂停按钮处，暂停时播放，播放时暂停
						if (isVideoPlaying && isMusicOn == 0
								&& !activity.bgMusic[1].isPlaying()) {
							activity.bgMusic[1].start();
						}
						if (!isVideoPlaying && activity.bgMusic[1].isPlaying()) {
							activity.bgMusic[1].pause();
						}
						return true;
					}
					if (!is_button_return && x > 680 * ratio_width
							&& y > 404 * ratio_height)// ----进入游戏
					{

						plane.blood = plane_blood;
						isno_draw_plane = true;// 开始绘制飞机
						PLANE_MOVE_SPAN = 15;// 飞机的速度
						PLANE_X = -100;
						PLANE_Y = 300;
						PLANE_Z = -100;
						rotationAngle_Plane_Y = 225f;
						rotationAngle_Plane_X = 0;
						rotationAngle_Plane_Z = 0;
						DIRECTION_CAMERA = 225;
						isVideo = false;// 界面设置为游戏界面
						isVideoPlaying = true;// 视频播放设置为播放
						kThread.time = 0;
						return true;
					}
				}
				// 开火按钮
				if (!isVideo && x > BUTTON_FIRE_AREA[0]
						&& x < BUTTON_FIRE_AREA[1] && y > BUTTON_FIRE_AREA[2]
						&& y < BUTTON_FIRE_AREA[3]) {
					fireButton.isButtonDown = 1;// 开火按钮标志位置为true,进行不透明度的变化
					shootId = id;
					isFireOn = true;
				}
				// 选择武器按钮
				if (!isVideo && x > BUTTON_WEAPON_AREA[0]
						&& x < BUTTON_WEAPON_AREA[1]
						&& y > BUTTON_WEAPON_AREA[2]
						&& y < BUTTON_WEAPON_AREA[3]) {
					WEAPON_INDEX = (WEAPON_INDEX + 1)
							% tex_button_weaponId.length;// 更换武器图片
				}
				// 向上按钮
				if (!isVideo && x > BUTTON_UP_AREA[0] && x < BUTTON_UP_AREA[1]
						&& y > BUTTON_UP_AREA[2] && y < BUTTON_UP_AREA[3]) {
					upId = id;
					up_button.isButtonDown = 1;// 开火按钮标志位置为true,进行不透明度的变化
					// 上
					keyState = keyState | 0x1;
					keyState = keyState & 0xD;
				}
				// 向下按钮
				if (!isVideo && x > BUTTON_DOWN_AREA[0]
						&& x < BUTTON_DOWN_AREA[1] && y > BUTTON_DOWN_AREA[2]
						&& y < BUTTON_DOWN_AREA[3]) {
					upId = id;
					down_button.isButtonDown = 1;// 开火按钮标志位置为true,进行不透明度的变化
					// 下
					keyState = keyState | 0x2;
					keyState = keyState & 0xE;
				}
				break;

			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_POINTER_UP:
				if (isVideo || isCrash || isOvercome) {
					break;
				}
				if (id == shootId) {
					isFireOn = false;
					shootId = 2;
				} else if (id == upId) {
					fireButton.isButtonDown = 0;// 按钮不再变化
					up_button.isButtonDown = 0;// 按钮不再变化
					down_button.isButtonDown = 0;// 按钮不再变化
					keyState = keyState & 0xc;
					upId = 2;
				}
				break;
			case MotionEvent.ACTION_UP:
				if (isVideo || isCrash || isOvercome) {
					break;
				}
				if (e.getPointerCount() == 1) {
					isFireOn = false;
					fireButton.isButtonDown = 0;// 按钮不再变化
					up_button.isButtonDown = 0;// 按钮不再变化
					down_button.isButtonDown = 0;// 按钮不再变化
					keyState = keyState & 0xc;
					upId = 2;
					shootId = 2;
				} else {
					if (shootId == 0) {
						isFireOn = false;
						shootId = 2;
					} else if (upId == 0) {
						fireButton.isButtonDown = 0;// 按钮不再变化
						up_button.isButtonDown = 0;// 按钮不再变化
						down_button.isButtonDown = 0;// 按钮不再变化
						keyState = keyState & 0xc;
						upId = 2;
					}
				}
				break;
			}
		} else// 游戏还没有开始,此时处于菜单部分
		{
			float x = e.getX();
			float y = e.getY();
			switch (e.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (1 == isMenuLevel)// 一级菜单
				{
					if (doorState == 1)// 当前为开仓状态,并且没有弹出对话框
					{
						if (hasInertia)// 按下时,如果有惯性,那么直接停止
						{
							hasInertia = false;
							curr_angle_speed = 0;
						}
						// 这里对滚轮菜单下的按下事件进行监听
						if (x > MENU_BUTTON_AREA[0] && x < MENU_BUTTON_AREA[1]
								&& y > MENU_BUTTON_AREA[2]
								&& y < MENU_BUTTON_AREA[3]) {
							isPoint = true;// 点击了相应的菜单按钮
						}
					}
				} else if (2 == isMenuLevel)// ---进入二级菜单
				{
					// --------如果按下了确定按钮------
					if (x > MENU_TWO_BUTTON_OK_AREA[0]
							&& x < MENU_TWO_BUTTON_OK_AREA[1]
							&& y > MENU_TWO_BUTTON_OK_AREA[2]
							&& y < MENU_TWO_BUTTON_OK_AREA[3]) {
						isOKButtonDown = true;
						// 这里需要换图
						tex_menu_two_okIndex = 1;
					}
					// --------如果按下了左按按钮------
					if (x > MENU_TWO_BUTTON_LEFT_AREA[0]
							&& x < MENU_TWO_BUTTON_LEFT_AREA[1]
							&& y > MENU_TWO_BUTTON_LEFT_AREA[2]
							&& y < MENU_TWO_BUTTON_LEFT_AREA[3]) {
						isLeftButtonDown = true;
						// 这里需要换图
						tex_menu_two_leftIndex = 1;

					}
					// --------如果按下了右按按钮------
					if (x > MENU_TWO_BUTTON_RIGHT_AREA[0]
							&& x < MENU_TWO_BUTTON_RIGHT_AREA[1]
							&& y > MENU_TWO_BUTTON_RIGHT_AREA[2]
							&& y < MENU_TWO_BUTTON_RIGHT_AREA[3]) {
						isRightButtonDown = true;
						// 这里需要换图
						tex_menu_two_rightIndex = 1;

					}
					// --------如果按下了战役模式按钮------
					if (x > MENU_TWO_WAR_BUTTON_AREA[0]
							&& x < MENU_TWO_WAR_BUTTON_AREA[1]
							&& y > MENU_TWO_WAR_BUTTON_AREA[2]
							&& y < MENU_TWO_WAR_BUTTON_AREA[3]) {
						isChangeAlpha = false;
						isWarButtonDown = true;
						// 这里需要换图
						tex_menu_two_war_btnIndex = 1;
					}
					// --------如果按下了特别行动按钮------
					if (x > MENU_TWO_ACTION_BUTTON_AREA[0]
							&& x < MENU_TWO_ACTION_BUTTON_AREA[1]
							&& y > MENU_TWO_ACTION_BUTTON_AREA[2]
							&& y < MENU_TWO_ACTION_BUTTON_AREA[3]) {
						isChangeAlpha = false;
						isActionButtonDown = true;
						// 这里需要换图
						tex_menu_two_action_btnIndex = 1;
					}
					// -----------第一个飞机
					if (x > MENU_TWO_PLANE_ICON_ONE_AREA[0]
							&& x < MENU_TWO_PLANE_ICON_ONE_AREA[1]
							&& y > MENU_TWO_PLANE_ICON_ONE_AREA[2]
							&& y < MENU_TWO_PLANE_ICON_ONE_AREA[3]) {
						// 这里需要换图
						tex_menu_two_plane_iconIndex[0] = 1;
						tex_menu_two_plane_iconIndex[1] = 0;
						tex_menu_two_plane_iconIndex[2] = 0;
						planeModelIndex = 0;
					}
					// -----------第二个飞机
					if (x > MENU_TWO_PLANE_ICON_TWO_AREA[0]
							&& x < MENU_TWO_PLANE_ICON_TWO_AREA[1]
							&& y > MENU_TWO_PLANE_ICON_TWO_AREA[2]
							&& y < MENU_TWO_PLANE_ICON_TWO_AREA[3]) {
						// 这里需要换图
						tex_menu_two_plane_iconIndex[0] = 0;
						tex_menu_two_plane_iconIndex[1] = 1;
						tex_menu_two_plane_iconIndex[2] = 0;
						planeModelIndex = 1;
					}
					// -----------第三个飞机
					if (x > MENU_TWO_PLANE_ICON_THREE_AREA[0]
							&& x < MENU_TWO_PLANE_ICON_THREE_AREA[1]
							&& y > MENU_TWO_PLANE_ICON_THREE_AREA[2]
							&& y < MENU_TWO_PLANE_ICON_THREE_AREA[3]) {
						// 这里需要换图
						tex_menu_two_plane_iconIndex[0] = 0;
						tex_menu_two_plane_iconIndex[1] = 0;
						tex_menu_two_plane_iconIndex[2] = 1;
						planeModelIndex = 2;
					}
				}
				// -------------------------------三级菜单-----------------------------------------
				else if (3 == isMenuLevel) {
					if (x > MAP_ONE_AREA[0] && x < MAP_ONE_AREA[1]
							&& y > MAP_ONE_AREA[2] && y < MAP_ONE_AREA[3]) {
						if (0 == isGameMode) {
							mapId = 0;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						} else// 霹雳行动 --- 打飞机
						{
							goTime = actionTimeSpan[0];
							oldTime = 0;
							mapId = 3;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						}
					} else if (x > MAP_TWO_AREA[0] && x < MAP_TWO_AREA[1]
							&& y > MAP_TWO_AREA[2] && y < MAP_TWO_AREA[3]) {
						if (0 == isGameMode) {
							mapId = 1;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						} else// 沙漠风暴 -坦克,高射炮
						{
							goTime = actionTimeSpan[1];
							oldTime = 0;
							mapId = 4;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						}
					} else if (x > MAP_THREE_AREA[0] && x < MAP_THREE_AREA[1]
							&& y > MAP_THREE_AREA[2] && y < MAP_THREE_AREA[3]) {
						if (0 == isGameMode) {
							mapId = 2;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						} else// 斩首行动 军火库
						{
							goTime = actionTimeSpan[2];
							oldTime = 0;
							mapId = 5;
							initMap();
							initMap_Value();
							isVideo = true;
							if (activity.bgMusic[0].isPlaying()) {
								activity.bgMusic[0].pause();
							}
							if (isMusicOn == 0) {
								activity.bgMusic[1].start();
							}
							isGameOn = true;
						}
					}
				}

				break;
			case MotionEvent.ACTION_MOVE:
				float dy = y - mPreviousY;
				if (1 == isMenuLevel)// 一级菜单
				{
					if (doorState == 1)// 当前为开仓状态,并且没有弹出对话框
					{
						// 这里进行范围限制
						if (missile_rotation + dy * TOUCH_SCALE_FACTOR > 20) {
							missile_rotation = 20;
						} else if (missile_rotation + dy * TOUCH_SCALE_FACTOR < -245) {
							missile_rotation = -245;
						} else {
							missile_rotation += dy * TOUCH_SCALE_FACTOR;// 当前导弹菜单旋转地角度
						}
						if (Math.abs(dy) > 8)// 设定一个阈值,如果大于这个阈值,松开手指后,添加惯性
						{
							isTouchMoved = true;// 当前正在触摸 移动中
							curr_angle_speed = ori_angle_speed
									* (dy / SCREEN_WIDTH);
						}
						if (isPoint && Math.abs(dy) > 10)// 如果点击事件为true,但是又移动了,所以点击事件设为false
						{
							isPoint = false;
						}
					}
					if (doorState == 2 && curr_menu_index == 2)// 排行榜界面的移动
					{
						if (rank_move - dy * TOUCH_SCALE_FACTOR * 0.002f > 0)// 确定移动的范围
						{
							rank_move -= dy * TOUCH_SCALE_FACTOR * 0.002f;
						} else {
							rank_move = 0;
						}
					}
					if (doorState == 2 && curr_menu_index == 3)// 帮助界面的移动
					{
						help_YOffset -= dy * TOUCH_SCALE_FACTOR * 0.01f;

					}
					if (doorState == 2 && curr_menu_index == 4)// 关于界面的移动
					{
						about_YOffset -= dy * TOUCH_SCALE_FACTOR * 0.01f;
					}
				}
				break;
			case MotionEvent.ACTION_UP:
				// 导弹菜单界面
				if (1 == isMenuLevel) {
					if (doorState == 1)// 当前为开仓状态,并且没有弹出对话框
					{
						if (isTouchMoved)// 如果当前滑动过
						{
							isTouchMoved = false;
							hasInertia = true;// 具有惯性
							// 若当前角速度大于零则加速度置为负
							curr_acceleratedSpeed = ori_acceleratedSpeed;
							if (curr_angle_speed > 0) {
								curr_acceleratedSpeed = -ori_acceleratedSpeed;
							}
						} else// 这里启动角度智能调整
						{
							auto_adjust = true;
						}
						// ---------------按钮
						// 这里对滚轮菜单下的按下事件进行监听
						if (x > MENU_BUTTON_AREA[0] && x < MENU_BUTTON_AREA[1]
								&& y > MENU_BUTTON_AREA[2]
								&& y < MENU_BUTTON_AREA[3]) {
							if (isPoint)// 如果点击事件为true
							{
								isPoint = false;
								switch (curr_menu_index) {
								case 0:// 开始游戏
									menu_button_move = true;// 导弹菜单按钮右移开始
									break;
								case 1:// 设置按钮
									doorState = 0;// 进行关舱
									break;
								case 2:// 排行榜
									doorState = 0;// 进行关舱
									rank_move = 0;// 触摸移动范围设置为0
									// 初始化数据库,获取数据
									String sql = "select * from plane ;";
									rank = SQLiteUtil.query(sql);
									Collections.reverse(rank);// 按时间倒排序
									break;
								case 3:// 帮助
									doorState = 0;// 进行关舱
									help_YOffset = -HELP_HEIGHT / 2.5f;// 帮助界面的Y轴偏移量
									break;
								case 4:// 关于
									doorState = 0;// 进行关舱
									about_YOffset = -ABOUT_HEIGHT / 2.5f;// 关于界面的Y轴偏移量
									break;
								case 5:// 退出
									activity.exitRelease();
									break;
								}
							}
						}
					}
					if (doorState == 2)// 如果当前为关舱状态
					{
						if (curr_menu_index == 1)// 如果当前为设置界面
						{
							// 是否设置开启背景音乐
							if (x > SETTING_BUTTON_AREA1[0]
									&& x < SETTING_BUTTON_AREA1[1]
									&& y > SETTING_BUTTON_AREA1[2]
									&& y < SETTING_BUTTON_AREA1[3]) {
								isMusicOn = (isMusicOn + 1) % 2;
								if (isMusicOn == 1
										&& activity.bgMusic[0].isPlaying())// 如果菜单界面的音乐正在播放
								{
									activity.bgMusic[0].pause();
								}
								if (isMusicOn == 0
										&& !activity.bgMusic[0].isPlaying()) {
									activity.bgMusic[0].start();
								}
							}
							// 是否设置开启特效声音
							if (x > SETTING_BUTTON_AREA2[0]
									&& x < SETTING_BUTTON_AREA2[1]
									&& y > SETTING_BUTTON_AREA2[2]
									&& y < SETTING_BUTTON_AREA2[3]) {
								isSoundOn = (isSoundOn + 1) % 2;
							}
							// 是否设置开启特效震动
							if (x > SETTING_BUTTON_AREA3[0]
									&& x < SETTING_BUTTON_AREA3[1]
									&& y > SETTING_BUTTON_AREA3[2]
									&& y < SETTING_BUTTON_AREA3[3]) {
								isVibrateOn = (isVibrateOn + 1) % 2;
							}
						}
					}
				}
				// -------------------选飞机场景界面界面-------------------
				else if (2 == isMenuLevel) {
					// --------如果按下了确定按钮------
					if (isOKButtonDown) {
						if (x > MENU_TWO_BUTTON_OK_AREA[0]
								&& x < MENU_TWO_BUTTON_OK_AREA[1]
								&& y > MENU_TWO_BUTTON_OK_AREA[2]
								&& y < MENU_TWO_BUTTON_OK_AREA[3]) {
							isMenuLevel = 3;
						}
						tex_menu_two_okIndex = 0;
						isOKButtonDown = false;
					}
					// --------如果按下了左按按钮------
					if (isLeftButtonDown) {
						if (x > MENU_TWO_BUTTON_LEFT_AREA[0]
								&& x < MENU_TWO_BUTTON_LEFT_AREA[1]
								&& y > MENU_TWO_BUTTON_LEFT_AREA[2]
								&& y < MENU_TWO_BUTTON_LEFT_AREA[3]) {
							planeModelIndex--;
							if (planeModelIndex < 0) {
								planeModelIndex = 2;
							}

							tex_menu_two_plane_iconIndex[0] = 0;
							tex_menu_two_plane_iconIndex[1] = 0;
							tex_menu_two_plane_iconIndex[2] = 0;
							tex_menu_two_plane_iconIndex[planeModelIndex] = 1;
						}
						// 这里需要换图
						tex_menu_two_leftIndex = 0;
						isLeftButtonDown = false;
					}
					// --------如果按下了右按按钮------
					if (isRightButtonDown) {
						if (x > MENU_TWO_BUTTON_RIGHT_AREA[0]
								&& x < MENU_TWO_BUTTON_RIGHT_AREA[1]
								&& y > MENU_TWO_BUTTON_RIGHT_AREA[2]
								&& y < MENU_TWO_BUTTON_RIGHT_AREA[3]) {
							planeModelIndex++;
							if (planeModelIndex > 2) {
								planeModelIndex = 0;
							}

							tex_menu_two_plane_iconIndex[0] = 0;
							tex_menu_two_plane_iconIndex[1] = 0;
							tex_menu_two_plane_iconIndex[2] = 0;
							tex_menu_two_plane_iconIndex[planeModelIndex] = 1;
						}
						// 这里需要换图
						tex_menu_two_rightIndex = 0;
						isRightButtonDown = false;
					}
					// --------如果按下了战役模式按钮------
					if (isWarButtonDown) {
						isChangeAlpha = true;
						if (x > MENU_TWO_WAR_BUTTON_AREA[0]
								&& x < MENU_TWO_WAR_BUTTON_AREA[1]
								&& y > MENU_TWO_WAR_BUTTON_AREA[2]
								&& y < MENU_TWO_WAR_BUTTON_AREA[3]) {
							isGameMode = 0;
							// 这里需要换图
							tex_menu_two_action_btnIndex = 0;
						} else {
							if (isGameMode != 0) {
								// 这里需要换图
								tex_menu_two_war_btnIndex = 0;
							}
						}
						isWarButtonDown = false;
					}
					// --------如果按下了特别行动按钮------
					if (isActionButtonDown) {
						isChangeAlpha = true;
						if (x > MENU_TWO_ACTION_BUTTON_AREA[0]
								&& x < MENU_TWO_ACTION_BUTTON_AREA[1]
								&& y > MENU_TWO_ACTION_BUTTON_AREA[2]
								&& y < MENU_TWO_ACTION_BUTTON_AREA[3]) {
							isGameMode = 1;
							// 这里需要换图
							tex_menu_two_war_btnIndex = 0;
						} else {
							if (isGameMode != 1) {
								// 这里需要换图
								tex_menu_two_action_btnIndex = 0;
							}
						}
						isActionButtonDown = false;
					}
				}
				break;
			}
			mPreviousY = y;
		}
		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer {
		// 是否是第一帧
		private boolean isFirstFrame = true;
		int plane_hit_id = 0;

		public void onDrawFrame(GL10 gl) {
			// 清除深度缓冲与颜色缓冲
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT
					| GLES20.GL_COLOR_BUFFER_BIT);
			if (!isLoadedOk)// 如过没有加载完成
			{
				drawOrthLoadingView();
			} else// 如果加载完成
			{
				if (isGameOn)// 进入游戏场景
				{
					drawPerspective();// 绘制游戏场景
					drawVirtualIcon();// 绘制虚拟按钮
					drawVideoDirection();// 绘制视频播放界面
					drawGameDialog();// 绘制飞机坠毁后的菜单界面
				} else// 进入菜单部分
				{
					drawGameMenu();// 绘制导弹菜单部分
				}
			}
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			// 设置视窗大小及位置
			GLES20.glViewport(0, 0, width, height);
			// 计算GLSurfaceView的宽高比
			ratio = (float) width / height;
			ConfigVirtualButtonArea();// 对按钮的范围进行相应的配置
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// 设置屏幕背景色RGBA
			GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
			// 打开背面剪裁
			GLES20.glEnable(GLES20.GL_CULL_FACE);
			// 打开深度检测
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);
			// 初始化自定义栈
			MatrixState.setInitStack();
			ShaderManager.loadFirstViewCodeFromFile(GLGameView.this
					.getResources());// 加载shader
			ShaderManager.compileFirstViewShader();// 编译shader
			// 创建加载界面的纹理矩形
			loadingView = new TextureRect(2, 2,
					ShaderManager.getFirstViewShaderProgram());
			tex_loadingviewId = initTexture(GLGameView.this.getResources(),
					R.drawable.loading, false);
			processBar = new TextureRect(2, 0.1f,
					ShaderManager.getFirstViewShaderProgram());
			tex_processId = initTexture(GLGameView.this.getResources(),
					R.drawable.process, false);
		}

		// 正交投影绘制加载界面
		public void drawOrthLoadingView() {
			if (isFirstFrame) { // 如果是第一帧
				MatrixState.pushMatrix();
				MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);// 设置正交投影
				MatrixState.setCamera(0, 0, 1, 0, 0, -1, 0, 1, 0);// 设置摄像机
				MatrixState.copyMVMatrix();
				loadingView.drawSelf(tex_loadingviewId);
				MatrixState.popMatrix();
				isFirstFrame = false;
			} else {// 这里进行资源的加载
				MatrixState.pushMatrix();
				MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);// 设置正交投影
				MatrixState.setCamera(0, 0, 1, 0, 0, -1, 0, 1, 0);// 设置摄像机
				MatrixState.copyMVMatrix();
				MatrixState.pushMatrix(); // 设置进度条
				MatrixState.translate(-2 + 2 * load_step / (float) 40,
						-1 + 0.05f, 0f);
				processBar.drawSelf(tex_processId);
				MatrixState.popMatrix();
				loadingView.drawSelf(tex_loadingviewId);// 绘制背景图
				MatrixState.popMatrix();
				loadResource();// 加载资源的方法
				return;
			}
		}

		// 透视投影绘制 游戏场景
		public void drawPerspective() {
			MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 40000);// 设置透视投影
			MatrixState.pushMatrix();
			// 这里进行相关变量的复制,用于不同线程之间的不同步,会发生抖动现象
			synchronized (lock) {
				curr_cx = cx;
				curr_cy = cy + 2;
				curr_cz = cz;
				curr_tx = tx;
				curr_ty = ty;
				curr_tz = tz;
				curr_upx = upX;
				curr_upy = upY;
				curr_upz = upZ;

				curr_PlaneX = PLANE_X;
				curr_PlaneZ = PLANE_Z;
				curr_PlaneY = PLANE_Y;
				curr_rot_Plane_X = rotationAngle_Plane_X;
				curr_rot_Plane_Y = rotationAngle_Plane_Y;
				curr_rot_Plane_Z = rotationAngle_Plane_Z;

				Matrix.setRotateM(fa, 0, curr_rot_Plane_Y, 0, 1, 0);// 得到旋转矩阵
				YB[0] = (float) (Math.sin(Math.toRadians(-curr_rot_Plane_Z)));
				YB[1] = (float) (Math.cos(Math.toRadians(-curr_rot_Plane_Z)) * Math
						.cos(Math.toRadians(rotationAngle_Plane_X)));
				YB[2] = (float) (Math.cos(Math.toRadians(-curr_rot_Plane_Z)) * Math
						.sin(Math.toRadians(rotationAngle_Plane_X)));
				YB[3] = 1;
				Matrix.multiplyMV(resultUp, 0, fa, 0, YB, 0);

				MatrixState
						.setCamera(curr_cx, curr_cy, curr_cz, curr_tx, curr_ty,
								curr_tz, resultUp[0], resultUp[1], resultUp[2]);// 设置摄像机的位置
				MatrixState.copyMVMatrix();
				MatrixState.translate(
						-20
								* (float) Math.sin(Math
										.toRadians(curr_rot_Plane_Z / 4)),
						-20
								* (float) Math.cos(Math
										.toRadians(curr_rot_Plane_Z / 4)), 0);
				copybaozhaList.clear();// 复杂爆炸效果列表
				for (DrawBomb db : baoZhaList) {
					copybaozhaList.add(db);
				}
				cop_archie_bomb_List.clear();// 复制高射炮子弹
				for (BombForControl db : archie_bomb_List) {
					cop_archie_bomb_List.add(db);
				}
				cop_bomb_List.clear();// 复制炮弹列表
				for (BombForControl db : bomb_List) {
					cop_bomb_List.add(db);
				}
				cop_archie_List.clear();
				for (ArchieForControl db : archie_List)// 高射炮炮弹复制
				{
					cop_archie_List.add(db);
				}
				cop_bullet_List.clear();// 子弹复制
				for (BulletForControl db : bullet_List) {
					cop_bullet_List.add(db);
				}
				copy_tank_bomb_List.clear();// 炮弹复制
				for (BombForControl db : tank_bomb_List) {
					copy_tank_bomb_List.add(db);
				}
			}
			lightAngle += 2;
			// 计算当前九宫格,并调用相应的方法绘制相应的物体
			drawAll();
			MatrixState.popMatrix();
		}

		// 计算当前九宫格,并调用相应的方法绘制相应的物体
		public void drawAll() {
			int rowi = 0;// 起始行列
			int colj = 0;
			int colT = CELL_SIZE;// 终止行列
			int rowT = CELL_SIZE;
			int col = (int) (curr_PlaneX / WIDTH_LALNDFORM);// -CELL_SIZE/2;//得到当前所在的行列
			int row = (int) (curr_PlaneZ / HEIGHT_LANDFORM);// -CELL_SIZE/2;
			int rcCount = 2;// 向前飞时后面要绘制的地块数
			// 裁减
			curr_rot_Plane_Y %= 360;
			if (curr_rot_Plane_Y < 0) {
				curr_rot_Plane_Y = 360 + curr_rot_Plane_Y;
			}
			if ((curr_rot_Plane_Y >= 0 && curr_rot_Plane_Y <= 45)
					|| (curr_rot_Plane_Y >= 315 && curr_rot_Plane_Y <= 360)) {// 如果原Z轴负方向飞行
				if (curr_PlaneZ < 0)// 在背岛而飞时，全岛都不绘制
				{
					rowi = 0;
					rowT = 1;
					colj = 0;// 所有地块都得绘制
					colT = MapArray[mapId].length;
				} else if (curr_PlaneZ > MapArray[mapId].length
						* WIDTH_LALNDFORM)// 如果是在进入岛的过程中
				{
					rowi = MapArray[mapId].length - CELL_SIZE / 2;// 所有地块都得绘制
					rowT = MapArray[mapId].length;
					colj = 0;// 所有地块都得绘制
					colT = MapArray[mapId].length;

				} else// 否则在岛中间，就只绘制飞机前面的部分
				{
					rowi = row - CELL_SIZE / 2;
					if (rowi < 0) {
						rowi = 0;
					}
					// ---------------------------
					rowT = row + rcCount;
					if (rowT > MapArray[mapId].length) {
						rowT = MapArray[mapId].length;
					} else if (rowT < 0) {
						rowT = 0;
					}
					colj = col - CELL_SIZE / 2;// 所有地块都得绘制
					colT = col + CELL_SIZE / 2;
					if (colj < 0) {
						colj = 0;
					} else if (colj > MapArray[mapId].length) {
						colj = MapArray[mapId].length;
					}
					if (colT < 0) {
						colT = 0;
					} else if (colT > MapArray[mapId].length) {
						colT = MapArray[mapId].length;
					}
				}
			} else if (curr_rot_Plane_Y >= 45 && curr_rot_Plane_Y < 135) {

				if (curr_PlaneX < 0)// 在背岛而飞时，全岛都不绘制
				{
					colj = 0;
					colT = 1;
					rowi = 0;// 所有地块都得绘制
					rowT = MapArray[mapId].length;
				} else if (curr_PlaneX >= MapArray[mapId].length
						* WIDTH_LALNDFORM) {// 如果是在进入岛的过程中
					colj = MapArray[mapId].length - CELL_SIZE / 2;// 所有地块都得绘制
					colT = MapArray[mapId].length;
					rowi = 0;// 所有地块都得绘制
					rowT = MapArray[mapId].length;
				} else {// 否则在岛中间，就只绘制飞机前面的部分
					colj = col - CELL_SIZE / 2;
					if (colj < 0) {
						colj = 0;
					}
					colT = col + rcCount;
					if (colT > MapArray[mapId].length) {
						rowT = MapArray[mapId].length;
					} else if (colT < 0) {
						colT = 0;
					}
					rowi = row - CELL_SIZE / 2;// 所有地块都得绘制
					rowT = row + CELL_SIZE / 2;
					if (rowi < 0) {
						rowi = 0;
					} else if (rowi > MapArray[mapId].length) {
						rowi = MapArray[mapId].length;
					}
					if (rowT < 0) {
						rowT = 0;
					} else if (rowT > MapArray[mapId].length) {
						rowT = MapArray[mapId].length;
					}
				}
			} else if (curr_rot_Plane_Y >= 135 && curr_rot_Plane_Y < 225) {
				if (curr_PlaneZ < 0) {// 进入岛而飞时，全岛都绘制
					rowi = 0;// 所有地块都得绘制
					rowT = CELL_SIZE / 2;
					colj = 0;// 所有地块都得绘制
					colT = MapArray[mapId].length;
				} else if (curr_PlaneZ > MapArray[mapId].length
						* WIDTH_LALNDFORM) {// 如果是在背岛而飞的过程中
					rowi = MapArray[mapId].length - 1;// 不绘制岛
					rowT = MapArray[mapId].length;
					colj = 0;// 所有地块都得绘制
					colT = MapArray[mapId].length;
				} else {// 否则在岛中间，就只绘制飞机前面的部分
					rowi = row - rcCount;
					if (rowi < 0) {
						rowi = 0;
					}
					rowT = row + CELL_SIZE / 2;
					if (rowT > MapArray[mapId].length) {
						rowT = MapArray[mapId].length;
					} else if (rowT < 0) {
						rowT = 0;
					}
					colj = col - CELL_SIZE / 2;// 所有地块都得绘制
					colT = col + CELL_SIZE / 2;
					if (colj < 0) {
						colj = 0;
					} else if (colj > MapArray[mapId].length) {
						colj = MapArray[mapId].length;
					}
					if (colT < 0) {
						colT = 0;
					} else if (colT > MapArray[mapId].length) {
						colT = MapArray[mapId].length;
					}
				}
			} else {
				if (curr_PlaneX < 0) {// 在进岛而飞时，全岛都绘制
					colj = 0;// 所有地块都得绘制
					colT = CELL_SIZE / 2;// MapArray[mapId].length;
					rowi = 0;// 所有地块都得绘制
					rowT = MapArray[mapId].length;
				} else if (curr_PlaneX > MapArray[mapId].length
						* WIDTH_LALNDFORM) {// 如果是在背岛而飞的过程中
					colj = 0;
					colT = 1;
					rowi = 0;// 所有地块都得绘制
					rowT = MapArray[mapId].length;
				} else {// 否则在岛中间，就只绘制飞机前面的部分
					colj = col - rcCount;
					if (colj < 0) {
						colj = 0;
					}
					colT = col + CELL_SIZE / 2;
					if (colT > MapArray[mapId].length) {
						colT = MapArray[mapId].length;
					} else if (colT < 0) {
						colT = 0;
					}
					rowi = row - CELL_SIZE / 2;// 所有地块都得绘制
					rowT = row + CELL_SIZE / 2;
					if (rowi < 0) {
						rowi = 0;
					} else if (rowi > MapArray[mapId].length) {
						rowi = MapArray[mapId].length;
					}
					if (rowT < 0) {
						rowT = 0;
					} else if (rowT > MapArray[mapId].length) {
						rowT = MapArray[mapId].length;
					}
				}
			}
			drawSky();// 绘制天空
			// 绘制大坝
			drawdam();
			// 绘制山
			drawLandForm(rowi, colj, rowT, colT);
			// 绘制水
			drawWater();
			// 绘制坦克
			drawTanke(rowi, colj, rowT, colT);
			// 绘制高射炮
			drawarchie(rowi, colj, rowT, colT);
			// 绘制军火库
			drawHouse(rowi, colj, rowT, colT);
			// 绘制房子
			drawHousePlane(rowi, colj, rowT, colT);
			// 绘制炮弹
			drawBombs();
			// 绘制高射炮炮弹
			drawArchieBombs();
			// 绘制坦克发射的炮弹
			drawTankBombs();
			// 绘制敌机
			drawEnemyPlane();
			// 绘制玩家操控飞机
			drawPlane(curr_PlaneX, curr_PlaneY, curr_PlaneZ, curr_rot_Plane_X,
					curr_rot_Plane_Y, curr_rot_Plane_Z);
			// 绘制灯塔
			drawLightTower();
			// 绘制树
			drawTree(rowi, colj, rowT, colT);
			// 绘制爆炸效果
			drawBomb();
			// 绘制子弹
			drawBullets();
			// 军火库爆炸效果
			drawBaoZhaXiaoguo();
		}

		// 绘制天空
		public void drawSky() {
			if (mapId == 0 || mapId == 5) {
				skyBallsmall.drawSelf(tex_nightId, curr_PlaneX, 0, curr_PlaneZ,
						rotationAngle_SkyBall);
				MatrixState.pushMatrix();
				MatrixState.translate(curr_PlaneX, 0, curr_PlaneZ);
				MatrixState.rotate(rotationAngle_SkyBall, 0, 1, 0);
				skynight.drawSelf();
				skynightBig.drawSelf();
				MatrixState.popMatrix();
			} else {
				skyBallsmall.drawSelf(tex_skyBallId, curr_PlaneX, 0,
						curr_PlaneZ, rotationAngle_SkyBall);
			}
		}

		// 绘制高射炮炮弹
		public void drawArchieBombs() {
			try {
				for (int i = 0; i < cop_archie_bomb_List.size(); i++) {
					cop_archie_bomb_List.get(i).drawSelf(tex_bulletId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制子弹的方法
		public void drawBullets() {
			// 开启混合
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			try {
				for (int i = 0; i < cop_bullet_List.size(); i++) {
					cop_bullet_List.get(i).drawSelf(tex_bulletId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			GLES20.glDisable(GLES20.GL_BLEND);
		}

		// 绘制炮弹的方法
		public void drawBombs() {
			try {
				for (int i = 0; i < cop_bomb_List.size(); i++) {
					cop_bomb_List.get(i).drawSelf(tex_bulletId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制坦克炮弹
		public void drawTankBombs() {
			try {
				for (int i = 0; i < copy_tank_bomb_List.size(); i++) {
					copy_tank_bomb_List.get(i).drawSelf(tex_bulletId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制水面的方法
		public void drawWater() {
			MatrixState.pushMatrix();
			MatrixState.translate(SKY_BALL_RADIUS / 2, 0, SKY_BALL_RADIUS / 2);
			MatrixState.rotate(-90, 1, 0, 0);
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			water.drawSelf(tex_waterId);
			GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix();
		}

		// 绘制山地
		public void drawLandForm(int rowi, int colj, int rowT, int colT) {
			for (int i = rowi; i < rowT; i++) {
				for (int j = colj; j < colT; j++) {
					MatrixState.pushMatrix();
					MatrixState.translate((0 + j) * WIDTH_LALNDFORM,
							LAND_HIGH_ADJUST, (0 + i) * HEIGHT_LANDFORM);
					try {
						draw_number_LandForm(MapArray[mapId][i][j]); // 根据编号绘制山
					} catch (Exception e) {
						e.printStackTrace();
					}
					MatrixState.popMatrix();
				}
			}
		}

		// 根据编号绘制对应的块
		public void draw_number_LandForm(int number) {
			switch (number) {
			case 0:
			case 1:
			case 2:
				terrain[number].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 3:
				terrain[number].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 4:
				MatrixState.translate(0, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(90, 0, 1, 0);
				terrain[0].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 5:
				MatrixState.translate(WIDTH_LALNDFORM, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(180, 0, 1, 0);
				terrain[0].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 6:
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[0].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 7:
				MatrixState.translate(0, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(90, 0, 1, 0);
				terrain[1].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 8:
				MatrixState.translate(WIDTH_LALNDFORM, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(180, 0, 1, 0);
				terrain[1].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 9:
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[1].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 10:
				MatrixState.translate(0, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(90, 0, 1, 0);
				terrain[2].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 11:
				MatrixState.translate(WIDTH_LALNDFORM, 0, WIDTH_LALNDFORM);
				MatrixState.rotate(180, 0, 1, 0);
				terrain[2].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 12:
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[2].drawSelf(0, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, LOTHight, height_span_LOT);
				break;
			case 13:// 高平面
				MatrixState.translate(WIDTH_LALNDFORM / 2, LAND_HIGHEST,
						WIDTH_LALNDFORM / 2);
				MatrixState.rotate(-90, 1, 0, 0);
				terrain_plain.drawSelf(tex_terrain_caodiId);
				break;
			case 15:// 山地水面山的
				terrain[4]
						.drawSelf(0, tex_terrain_shandingId,
								tex_terrain_tuceng_Id, tex_terrain_caodiId,
								tex_terrain_shitouId, waterHillHight,
								height_span_Water);
				break;
			case 16:// 山上的
				terrain[5].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 17:// 中间块
				terrain[6].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 18:// 第一块山旋转270
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[3].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 19:// 中间块山旋转270
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[6].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 20:// 中间块山旋转270
				MatrixState.translate(WIDTH_LALNDFORM, 0, 0);
				MatrixState.rotate(270, 0, 1, 0);
				terrain[5].drawSelf(1, tex_terrain_shandingId,
						tex_terrain_tuceng_Id, tex_terrain_caodiId,
						tex_terrain_shitouId, HillHight, height_span_Hill);
				break;
			case 21:// 绘制飞机跑道
				MatrixState.translate(WIDTH_LALNDFORM / 2, LAND_HIGHEST,
						WIDTH_LALNDFORM / 2);
				MatrixState.rotate(-90, 1, 0, 0);
				terrain_plain.drawSelf(tex_damId);
				break;
			}
		}

		/*
		 * 绘制飞机的方法
		 */
		public void drawPlane(float curr_x, float curr_y, float curr_z,
				float rotationAngle_Plane_X, float rotationAngle_Plane_Y,
				float rotationAngle_Plane_Z) {
			if (!isno_draw_plane || isVideo) {
				return;
			}
			try {
				MatrixState.pushMatrix();
				MatrixState.translate(curr_x, curr_y - 10, curr_z);
				MatrixState.rotate(rotationAngle_Plane_Y, 0, 1, 0);
				MatrixState.rotate(rotationAngle_Plane_Z, 0, 0, 1);
				MatrixState.rotate(rotationAngle_Plane_X, 1, 0, 0);
				GLES20.glEnable(GLES20.GL_BLEND);
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
						GLES20.GL_ONE_MINUS_SRC_ALPHA);
				MatrixState.translate(0, 0, -70);
				mark_aim.drawSelf(tx_lockaimtexId);
				GLES20.glDisable(GLES20.GL_BLEND);
				MatrixState.translate(0, 0, 70);
				GLES20.glDisable(GLES20.GL_CULL_FACE);
				planeModel[planeModelIndex]
						.drawSelf(planeModelTexId[planeModelIndex]);
				GLES20.glEnable(GLES20.GL_CULL_FACE);
				MatrixState.popMatrix();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制灯塔
		public void drawLightTower() {
			try {
				GLES20.glDisable(GLES20.GL_CULL_FACE);// 关闭背面剪裁
				GLES20.glEnable(GLES20.GL_BLEND);// 开启混合
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
						GLES20.GL_ONE_MINUS_SRC_ALPHA);
				MatrixState.pushMatrix();
				MatrixState.translate(ArchieArray[mapId][3][0]
						* WIDTH_LALNDFORM, 200 + LAND_HIGHEST,
						ArchieArray[mapId][3][1] * WIDTH_LALNDFORM);
				MatrixState.pushMatrix();
				MatrixState.rotate(180, 1, 0, 0);
				lighttower.drawSelf(tex_lightid);
				MatrixState.popMatrix();
				MatrixState.rotate(90, 1, 0, 0);
				MatrixState.rotate(lightAngle, 0, 0, 1);
				lighttower.drawSelf(tex_lighttowerid);
				MatrixState.rotate(180, 1, 0, 0);
				lighttower.drawSelf(tex_lighttowerid);
				MatrixState.popMatrix();
				GLES20.glDisable(GLES20.GL_BLEND);
				GLES20.glEnable(GLES20.GL_CULL_FACE);// 打开背面剪裁
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制坦克
		public void drawTanke(int rowi, int colj, int rowT, int colT) {
			try {
				for (TanKe tanke : tankeList)// 绘制坦克
				{
					tanke.drawSelf(tex_tankeid, rowi, colj, rowT, colT,
							tex_backgroundRectId, tex_numberRectId,
							tex_locktexId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制高射炮
		public void drawarchie(int rowi, int colj, int rowT, int colT) {
			try {
				for (int i = 0; i < archie_List.size(); i++)// 绘制高射炮
				{
					archie_List.get(i).drawSelf(texBarbetteId, texCubeId,
							texBarrelId, rowi, colj, rowT, colT,
							tex_backgroundRectId, tex_numberRectId,
							tex_locktexId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制军火库
		public void drawHouse(int ii, int jj, int rowR, int colR) {
			if (!isno_draw_arsenal) {
				return;
			}
			GLES20.glDisable(GLES20.GL_CULL_FACE);// 关闭背面剪裁
			try {
				for (Arsenal_House ah : arsenal) {
					ah.drawSelf(tex_frontId, tex_frontId, tex_roofId,
							tex_AnnulusId, lightAngle, tex_backgroundRectId,
							tex_numberRectId, tex_locktexId, ii, jj, rowR, colR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			GLES20.glEnable(GLES20.GL_CULL_FACE);// 打开背面剪裁
		}

		// 绘制普通房子
		public void drawHousePlane(int ii, int jj, int rowR, int colR) {
			try {
				for (PlaneHouse ph : houseplane) {
					ph.drawSelf(tex_housePlaneId[1], tex_housePlaneId[0],
							tex_housePlaneSmallId[0], tex_housePlaneSmallId[1],
							tex_chimneyId, ii, jj, rowR, colR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 绘制树
		public void drawTree(int ii, int jj, int rowR, int colR) {
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			try {
				for (int i = 0; i < treeList.size(); i++) {
					treeList.get(i).drawSelf(ii, jj, rowR, colR);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			GLES20.glDisable(GLES20.GL_BLEND);
		}

		// 绘制爆炸效果
		public void drawBomb() {
			GLES20.glDisable(GLES20.GL_CULL_FACE);// 关闭背面剪裁
			GLES20.glEnable(GLES20.GL_BLEND);
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			try {
				for (DrawBomb dbb : copybaozhaList) {
					dbb.drawSelf();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			GLES20.glDisable(GLES20.GL_BLEND);
			GLES20.glEnable(GLES20.GL_CULL_FACE);// 打开背面剪裁
		}

		// 绘制大坝
		public void drawdam() {
			if (ArchieArray[mapId][5].length > 0) {
				if (!dam.isShaderOk) {
					dam.isShaderOk = true;
					dam.initShader();// 初始化shader程序
				}
				dam.drawSelf(tex_damId);
			}
		}

		// 绘制最后飞机爆炸效果
		public void drawBaoZhaXiaoguo() {
			GLES20.glDisable(GLES20.GL_CULL_FACE);// 关闭背面剪裁
			if (isCrash)// 飞机炸毁
			{
				isFireOn = false;
				fireButton.isButtonDown = 0;// 按钮不再变化
				up_button.isButtonDown = 0;// 按钮不再变化
				down_button.isButtonDown = 0;// 按钮不再变化
				keyState = keyState & 0xc;
				upId = 2;
				shootId = 2;
				GLES20.glEnable(GLES20.GL_BLEND);
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
						GLES20.GL_ONE_MINUS_SRC_ALPHA);
				MatrixState.pushMatrix();
				MatrixState.translate(curr_PlaneX, curr_PlaneY, curr_PlaneZ);
				MatrixState.scale(BaoZha_scal, BaoZha_scal, BaoZha_scal);
				MatrixState.rotate(-90, 1, 0, 0);
				bombRect.drawSelf(baoZhaXiaoguo);
				MatrixState.popMatrix();
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			GLES20.glEnable(GLES20.GL_CULL_FACE);// 打开背面剪裁
		}

		// -------------------------绘制视频界面上的各个按钮-----------------------------
		public void drawVideoDirection() {
			if (!isVideo)// 如果不是视频播放界面就不绘制
			{
				return;
			}
			MatrixState.pushMatrix();
			MatrixState.setProjectOrtho(-ratio, ratio, -1, 1, 1, 10);
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);// 恢复矩阵
			MatrixState.copyMVMatrix();
			// 开启混合
			GLES20.glEnable(GLES20.GL_BLEND);
			// 设置混合因子
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);

			// -------------------------这里绘制说明文字----------------
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, -2.5f);
			noticeRect.drawSelf(tex_noticeId[mapId]);
			MatrixState.popMatrix();
			// ---------------------暂停和播放---------------------
			MatrixState.pushMatrix();
			MatrixState.translate(-ratio + 0.25f * ratio / 2, -1 + 0.35f / 2,
					-1.5f);
			if (isVideoPlaying) {
				menu_video.drawSelf(pauseId);
			} else {
				menu_video.drawSelf(playId);
			}
			MatrixState.popMatrix();
			// -----------------------进入游戏按钮-----------------------
			MatrixState.pushMatrix();
			MatrixState.translate(ratio - 0.25f * ratio / 2, -1 + 0.35f / 2,
					-1.5f);
			menu_video.drawSelf(stopId);
			MatrixState.popMatrix();

			// 关闭混合
			GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix();
		}

		// -------------------------------绘制游戏中的对话框-------------------------
		public void drawGameDialog() {
			if (!isVideo && !is_button_return) {
				if (!isCrashCartoonOver) {
					return;// 如果不是飞机坠毁后的动画播放完毕
				}
			}
			MatrixState.pushMatrix();// 绘制背景
			MatrixState.setProjectOrtho(-1, 1, -1, 1, 1, 10);
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);// 恢复矩阵
			MatrixState.copyMVMatrix();
			// 开启混合
			GLES20.glEnable(GLES20.GL_BLEND);
			// 设置混合因子
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, -1);
			if ((isGameMode == 0 && isCrash && !isVideo)
					|| (is_button_return && !isVideo)
					|| (isVideo && !isVideoPlaying && isTrueButtonAction))// 如果是飞机坠毁，或者按下了返回按钮
			{
				menu_Rect.drawSelf(tex_menu_text);
			} else if (isGameMode == 0 && !isVideo)// 战役模式
			{
				menu_Rect.drawSelf(tex_menu_text_win);
			}
			// -----------------判断特殊行动成功
			if (isGameMode == 1 && isSpecActionState == 1)// 成功
			{
				menu_Rect.drawSelf(tex_actionWinId);
			} else if (isGameMode == 1 && isSpecActionState == 2)// 失败
			{
				menu_Rect.drawSelf(tex_actionFailId);
			}
			MatrixState.popMatrix();
			// 关闭混合
			GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix();
		}

		// 绘制飞机被大炮击中时的标识
		public void onDrawHit() {
			if (isno_Hit || isno_Vibrate)// 如果被击中了
			{
				if (plane_hit_id % 4 == 0)// 隔三次闪一次
				{
					if (isno_Hit) {
						plane_Hit.drawSelf(tex_plane_hitId);
					}
					PLANE_Y -= 2f;
				}
				if (plane_hit_id % 4 == 2) {
					PLANE_Y += 2f;
				}
				plane_hit_id++;
				if (plane_hit_id == 8) {
					plane_hit_id = 0;
					isno_Hit = false;
					isno_Vibrate = false;
				}
			}
		}

		// 绘制敌机
		public void drawEnemyPlane() {
			try {
				for (EnemyPlane emp : enemy) {
					emp.drawSelf(planeHeadId, screw1Id, planeBodyId,
							planeCabinId, frontWingId, frontWing2Id,
							cylinder1Id, cylinder2Id, cylinder2Id, bacckWingId,
							topWingId, tex_backgroundRectId, tex_numberRectId,
							tex_locktexId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		// 用正交投影绘制虚拟图标
		public void drawVirtualIcon() {
			if (isVideo)// 如果是视频播放中，则返回
			{
				return;
			}
			MatrixState.pushMatrix();
			// 设置正交矩阵
			MatrixState.setProjectOrtho(-ratio, ratio, -1f, 1f, 1, 10);
			// 设置摄像机
			MatrixState.setCamera(0, 0, 0, 0, 0, -1, 0, 1, 0);
			MatrixState.copyMVMatrix();
			// 开启混合
			GLES20.glEnable(GLES20.GL_BLEND);
			// 设置混合因子
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0, -6);
			onDrawHit();// 绘制飞机杯击中效果
			MatrixState.popMatrix();
			// 飞机血
			MatrixState.pushMatrix();
			MatrixState.translate(0, 0.9f, -3);
			MatrixState.scale(0.01f, 0.005f, 0.1f);
			backgroundRect_blood.bloodValue = plane.blood / 5 - 100 + 6;
			backgroundRect_blood.drawSelf(tex_backgroundRectId);// 血
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_FIRE_XOffset, BUTTON_FIRE_YOffset, -2);
			fireButton.drawSelf(tex_fireButtonId);// 绘制开火按钮
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_RADAR_XOffset, BUTTON_RADAR_YOffset,
					-2.1f);
			radar_bg.drawSelf(tex_radar_bg_Id);// 绘制雷达图标
			MatrixState.translate(0, 0, 0.5f);

			drawMardPlace();
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_RADAR_XOffset, BUTTON_RADAR_YOffset,
					-2f);
			MatrixState.rotate(RADAR_DIRECTION, 0, 0, 1);
			radar_plane.drawSelf(tex_radar_plane_Id);// 绘制雷达指针图标
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_WEAPON_XOffset, BUTTON_WEAPON_YOffset,
					-2f);
			// 进行换图
			weapon_button.drawSelf(tex_button_weaponId[WEAPON_INDEX]);// 绘制武器选择图标
			MatrixState.popMatrix();

			// 绘制子弹的数量
			MatrixState.pushMatrix();
			MatrixState.translate(WEAPON_NUMBER_XOffset, WEAPON_NUMBER_YOffset,
					-2f);
			if (0 == WEAPON_INDEX)// 子弹
			{
				if (bullet_number < 0) {
					bullet_number = 0;
				}
				weapon_number
						.drawSelfLeft(bullet_number + "", tex_rankNumberId);
			} else// 炮弹
			{
				if (bomb_number < 0) {
					bomb_number = 0;
				}
				weapon_number.drawSelfLeft(bomb_number + "", tex_rankNumberId);
			}
			MatrixState.popMatrix();
			// ---------------绘制倒计时---------------------------------
			if (isGameMode == 1) {
				// 绘制剩余时间矩形
				MatrixState.pushMatrix();
				MatrixState.translate(-2 * ratio * 0.1f, WEAPON_NUMBER_YOffset,
						-2f);
				leftTimeRect.drawSelf(tex_lefttimeId);
				MatrixState.popMatrix();
				// 绘制数字
				MatrixState.pushMatrix();
				MatrixState.translate(0, WEAPON_NUMBER_YOffset, -2f);
				if (goTime < 0) {
					goTime = 0;
				}
				weapon_number.drawSelfLeft(goTime + "", tex_rankNumberId);
				MatrixState.popMatrix();
			}
			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_UP_XOffset, BUTTON_UP_YOffset, -2);
			up_button.drawSelf(tex_button_upId);// 绘制向上按钮
			MatrixState.popMatrix();

			MatrixState.pushMatrix();
			MatrixState.translate(BUTTON_DOWN_XOffset, BUTTON_DOWN_YOffset, -2);
			down_button.drawSelf(tex_button_downId);// 绘制向下按钮
			MatrixState.popMatrix();

			// 关闭混合
			GLES20.glDisable(GLES20.GL_BLEND);
			MatrixState.popMatrix();
		}

		// 绘制各个物体的标志位置
		// 绘制标志位置颜色矩形
		public void drawMardPlace() {
			try {
				if (lightAngle % 10 != 0)// 飞机仪表盘图标
				{
					plane.drawSelfMark(tex_mark_planeId);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (!(mapId == 4 || mapId == 5)) {
				try {
					for (EnemyPlane emp : enemy)// 敌机仪表盘图标
					{
						emp.drawSelfMark(tex_mark_ackId);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!(mapId == 3 || mapId == 5)) {
				try {
					for (TanKe tanke : tankeList)// 坦克
					{
						tanke.drawSelfMark(tex_mark_tanke);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				try {
					for (int i = 0; i < archie_List.size(); i++)// 绘制高射炮仪表盘图标
					{
						archie_List.get(i).drawSelfMark(tex_mark_tanke);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (!(mapId == 3 || mapId == 4)) {
				try {
					for (Arsenal_House ah : arsenal)// 军火库仪表盘图标绘制
					{
						ah.drawSelfMark(tex_mark_arsenalId);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		// ------------------------------- 一级 菜单--------绘制菜单界面-------------
		public void drawGameMenu() {
			if (1 == isMenuLevel)// 一级菜单
			{
				// 设置透视投影
				MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 200);
				// 设置摄像机
				MatrixState.setCamera(0, 0, 8, 0, 0, 0, 0, 1, 0);
				MatrixState.copyMVMatrix();
				if (!(doorState == 2 && curr_menu_index == 2))// 在排行榜界面下不绘制此项
				{
					// 绘制导弹菜单陆地背景
					MatrixState.pushMatrix();
					MatrixState.translate(0, 0, -100);
					menu_Background.drawSelf(tex_bgId);
					MatrixState.popMatrix();
					// 绘制云彩
					MatrixState.pushMatrix();
					GLES20.glEnable(GLES20.GL_BLEND);
					GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
							GLES20.GL_ONE_MINUS_SRC_ALPHA);
					MatrixState.translate(0, 0, -90);
					menu_clouds.drawSelf(tex_cloudsId);
					if (isDrawBaozha)// 这里绘制导弹下落后的爆炸效果图
					{
						MatrixState.translate(0, 0, 1);
						MatrixState.scale(baozha_ratio, baozha_ratio, 1);
						bombRect.drawSelf(baoZhaXiaoguo);
					}
					GLES20.glDisable(GLES20.GL_BLEND);
					MatrixState.popMatrix();
				}
				if (doorState != 2)// 绘制导弹
				{
					// 绘制导弹
					MatrixState.pushMatrix();
					MatrixState.translate(0, missile_YOffset, missile_ZOffset);
					MatrixState.rotate(-90, 0, 0, 1);
					MatrixState.rotate(missile_rotation, 0, 1, 0);
					missile_menu.drawSelft(tex_rectId);
					MatrixState.popMatrix();
				}
			}
			// 设置正交矩阵
			MatrixState.setProjectOrtho(-ratio, ratio, -1f, 1f, 1, 10);
			// 设置摄像机
			MatrixState.setCamera(0, 0, 1, 0, 0, -1, 0, 1, 0);
			MatrixState.copyMVMatrix();
			if (1 == isMenuLevel)// 如果没有进入地图选择界面
			{
				if (!(doorState == 2 && (curr_menu_index == 1
						|| curr_menu_index == 2 || curr_menu_index == 3 || curr_menu_index == 4))) {
					// 绘制上舱门机舱门
					MatrixState.pushMatrix();
					MatrixState.translate(0, door_YOffset, -2);
					front_door.drawSelf(tex_menu_doorId);
					MatrixState.popMatrix();
					// 绘制下舱门
					MatrixState.pushMatrix();
					MatrixState.translate(0, -Math.abs(door_YOffset), -2);
					MatrixState.rotate(180, 0, 0, 1);
					front_door.drawSelf(tex_menu_doorId);
					MatrixState.popMatrix();
				}
			}
			// 开启混合
			GLES20.glEnable(GLES20.GL_BLEND);
			// 设置混合因子
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);
			if (1 == isMenuLevel)// 如果当前不是位于地图选择界面
			{
				if (doorState == 1)// 如果当前位于开仓界面,绘制导弹菜单按钮
				{
					MatrixState.pushMatrix();
					MatrixState.translate(menu_button_XOffset, 0, -1);
					front_cover_button.drawSelf(tex_front_coverId);// 绘制前边的罩子
					MatrixState.popMatrix();
				}
				// 绘制设置界面界面
				if (doorState == 2 && curr_menu_index == 1) {
					// 是否开启背景音乐
					MatrixState.pushMatrix();
					MatrixState.translate(SETTING_BUTTON_XOffset1,
							SETTING_BUTTON_YOffset1, -1);
					menu_setting.drawSelf(tex_musicId[isMusicOn]);
					MatrixState.popMatrix();
					// 是否开启特效声音
					MatrixState.pushMatrix();
					MatrixState.translate(SETTING_BUTTON_XOffset2,
							SETTING_BUTTON_YOffset2, -1);
					menu_setting.drawSelf(tex_soundId[isSoundOn]);
					MatrixState.popMatrix();
					// 是否开启特效震动
					MatrixState.pushMatrix();
					MatrixState.translate(SETTING_BUTTON_XOffset3,
							SETTING_BUTTON_YOffset3, -1);
					menu_setting.drawSelf(tex_vibrateId[isVibrateOn]);
					MatrixState.popMatrix();
				}
				// 绘制排行榜界面
				if (doorState == 2 && curr_menu_index == 2) {
					MatrixState.pushMatrix();
					MatrixState.translate(0, 0, -1);
					front_frame.drawSelf(tex_rankBgId);// 绘制排行榜背景
					MatrixState.popMatrix();
					// ---------------------
					for (int i = 0; i < rank.size(); i++)// 绘制数字
					{
						float curr_y = 0.22f - RANK_NUMBER_HEIGHT * 1.3f * i
								+ rank_move;// 当前y位置
						if (curr_y <= 0.24f && curr_y >= -0.68f)// 确定范围
						{
							MatrixState.pushMatrix();
							MatrixState.translate(0, curr_y, 0);
							// 绘制关卡
							MatrixState.pushMatrix();
							MatrixState.translate(-ratio * 0.60f, 0, 0);
							map_name.drawSelf(tex_mapId[Integer.parseInt(rank
									.get(i)[0])]);
							MatrixState.popMatrix();
							// 总得分
							MatrixState.pushMatrix();
							MatrixState.translate(-ratio * 0.17f, 0, 0);
							rank_number.drawSelf(rank.get(i)[1],
									tex_rankNumberId);
							MatrixState.popMatrix();
							// 耗时
							MatrixState.pushMatrix();
							MatrixState.translate(ratio * 0.23f, 0, 0);
							rank_number.drawSelf(rank.get(i)[2],
									tex_rankNumberId);
							MatrixState.popMatrix();
							// 日期
							MatrixState.pushMatrix();
							MatrixState.translate(ratio * 0.73f, 0, 0);
							rank_number.drawSelf(rank.get(i)[3],
									tex_rankNumberId);
							MatrixState.popMatrix();
							MatrixState.popMatrix();
						}
					}
				}
				// 绘制帮助界面
				if (doorState == 2 && curr_menu_index == 3) {
					MatrixState.pushMatrix();
					MatrixState.translate(0, help_YOffset, -1);
					helpView.drawSelf(tex_helpId);
					MatrixState.popMatrix();
				}
				// 绘制关于界面
				if (doorState == 2 && curr_menu_index == 4) {
					MatrixState.pushMatrix();
					MatrixState.translate(0, about_YOffset, -1);
					aboutView.drawSelf(tex_aboutId);
					MatrixState.popMatrix();
				}
				if (!(doorState == 2 && curr_menu_index == 2)) {
					front_frame.drawSelf(tex_front_frameId);// 绘制最前边的前景图
				}
			}
			GLES20.glDisable(GLES20.GL_BLEND);
			// ----------------------二级菜单-------------如果进入模式选择界面------------------------------
			if (2 == isMenuLevel) {
				// ------------首先绘制飞机模型------
				// 设置透视投影
				MatrixState.setProjectFrustum(-ratio, ratio, -1, 1, 3, 20000);
				// 设置摄像机
				MatrixState.setCamera(0, 100, 220, 0, 0, 0, 0, 1, 0);
				MatrixState.copyMVMatrix();

				MatrixState.pushMatrix();
				MatrixState.translate(0, 100f, -150);
				// backgroundRect.drawSelf(backgroundId_01);
				MatrixState.translate(0, -150f, 170);
				MatrixState.rotate(-90, 1, 0, 0);
				// backgroundRect.drawSelf(backgroundId_02);
				MatrixState.popMatrix();

				MatrixState.pushMatrix();
				MatrixState.rotate(planeRotate, 0, 1, 0);
				GLES20.glDisable(GLES20.GL_CULL_FACE);// 关闭背面剪裁
				drawPlaneModel();// 绘制展台
				GLES20.glEnable(GLES20.GL_CULL_FACE);
				MatrixState.popMatrix();
				// -----------------这里绘制模式选择的菜单界面------------------
				// 设置正交矩阵
				MatrixState.setProjectOrtho(-ratio, ratio, -1f, 1f, 1, 10);
				// 设置摄像机
				MatrixState.setCamera(0, 0, 1, 0, 0, -1, 0, 1, 0);
				MatrixState.copyMVMatrix();
				// 开启混合
				GLES20.glEnable(GLES20.GL_BLEND);
				// 设置混合因子
				GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
						GLES20.GL_ONE_MINUS_SRC_ALPHA);
				// 开头选飞机场景中的标题栏
				MatrixState.pushMatrix();
				MatrixState.translate(0, 1 - PLANE_SELECT_HEAD_HEIGHT / 2, 0);
				plane_select_head.drawSelf(tex_plane_select_head);
				MatrixState.popMatrix();
				// -------------这里绘制三个飞机图片-----------------------
				// 绘制第一个飞机图片
				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_PLANE_ICON_ONE_XOffset,
						MENU_TWO_PLANE_ICON_ONE_YOffset, 0);
				menu_two_plane_icon
						.drawSelf(tex_menu_two_plane_iconId[0][tex_menu_two_plane_iconIndex[0]]);
				MatrixState.popMatrix();
				// 绘制第二个飞机图片
				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_PLANE_ICON_TWO_XOffset,
						MENU_TWO_PLANE_ICON_TWO_YOffset, 0);
				menu_two_plane_icon
						.drawSelf(tex_menu_two_plane_iconId[1][tex_menu_two_plane_iconIndex[1]]);
				MatrixState.popMatrix();
				// 绘制第三个飞机图片
				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_PLANE_ICON_THREE_XOffset,
						MENU_TWO_PLANE_ICON_THREE_YOffset, 0);
				menu_two_plane_icon
						.drawSelf(tex_menu_two_plane_iconId[2][tex_menu_two_plane_iconIndex[2]]);
				MatrixState.popMatrix();

				// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$
				if (isChangeAlpha)// 如果可以改变不透明度
				{
					float temp = currAlpha + direction * 0.05f;
					if (temp > 1.0) {
						temp = 1.0f;
						direction = -direction;
						menu_two_game_model_btn.currAlpha = temp;
					} else if (temp < 0.5f) {
						temp = 0.5f;
						direction = -direction;
						menu_two_game_model_btn.currAlpha = temp;
					} else {
						currAlpha = temp;
						menu_two_game_model_btn.currAlpha = currAlpha;
					}
				} else {
					menu_two_game_model_btn.currAlpha = 1.0f;
				}
				if (isGameMode == 0)// 战役模式
				{
					// -------------------------战役模式按钮----------------
					MatrixState.pushMatrix();
					MatrixState.translate(MENU_TWO_WAR_BUTTON_XOffset,
							MENU_TWO_WAR_BUTTON_YOffset, 0);
					menu_two_game_model_btn
							.drawSelf(tex_menu_two_war_btnId[tex_menu_two_war_btnIndex]);
					MatrixState.popMatrix();
					// -------------------------特别行动按钮----------------
					menu_two_game_model_btn.currAlpha = 1.0f;
					MatrixState.pushMatrix();
					MatrixState.translate(MENU_TWO_ACTION_BUTTON_XOffset,
							MENU_TWO_ACTION_BUTTON_YOffset, 0);
					menu_two_game_model_btn
							.drawSelf(tex_menu_two_action_btnId[tex_menu_two_action_btnIndex]);
					MatrixState.popMatrix();

				} else// 特殊行动
				{
					// -------------------------特别行动按钮----------------
					MatrixState.pushMatrix();
					MatrixState.translate(MENU_TWO_ACTION_BUTTON_XOffset,
							MENU_TWO_ACTION_BUTTON_YOffset, 0);
					menu_two_game_model_btn
							.drawSelf(tex_menu_two_action_btnId[tex_menu_two_action_btnIndex]);
					MatrixState.popMatrix();
					// -------------------------战役模式按钮----------------
					menu_two_game_model_btn.currAlpha = 1.0f;
					MatrixState.pushMatrix();
					MatrixState.translate(MENU_TWO_WAR_BUTTON_XOffset,
							MENU_TWO_WAR_BUTTON_YOffset, 0);
					menu_two_game_model_btn
							.drawSelf(tex_menu_two_war_btnId[tex_menu_two_war_btnIndex]);
					MatrixState.popMatrix();
				}
				// $$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$

				// ----------左右选择按钮=================
				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_BUTTON_LEFT_XOffset,
						MENU_TWO_BUTTON_LEFT_YOffset, 0);
				menu_two_button
						.drawSelf(tex_menu_two_leftId[tex_menu_two_leftIndex]);
				MatrixState.popMatrix();

				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_BUTTON_RIGHT_XOffset,
						MENU_TWO_BUTTON_RIGHT_YOffset, 0);
				menu_two_button
						.drawSelf(tex_menu_two_rightId[tex_menu_two_rightIndex]);
				MatrixState.popMatrix();
				// -----------------这里绘制确定按钮--------
				MatrixState.pushMatrix();
				MatrixState.translate(MENU_TWO_BUTTON_OK_XOffset,
						MENU_TWO_BUTTON_OK_YOffset, 0);
				menu_two_button
						.drawSelf(tex_menu_two_okId[tex_menu_two_okIndex]);
				MatrixState.popMatrix();
				GLES20.glDisable(GLES20.GL_BLEND);
			}
			// ---------------绘制三级菜单 主要包括战役模式,特别行动
			if (3 == isMenuLevel) {
				// 设置正交矩阵
				MatrixState.setProjectOrtho(-ratio, ratio, -1f, 1f, 1, 10);
				// 设置摄像机
				MatrixState.setCamera(0, 0, 1, 0, 0, -1, 0, 1, 0);
				MatrixState.copyMVMatrix();
				if (0 == isGameMode)// 战役模式
				{
					front_frame.drawSelf(tex_mapSelectedBgId);
				} else if (1 == isGameMode)// 特别行动
				{
					front_frame.drawSelf(tex_special_action_bgId);
				}
			}
		}

		// 绘制选择飞机场景的方法
		public void drawPlaneModel() {
			// 绘制展台
			MatrixState.pushMatrix();
			MatrixState.translate(0, -5f, 0);
			drawCircleStation(0);
			MatrixState.popMatrix();
		}

		// 绘制选飞机场景中的展台
		public void drawCircleStation(float yOffset) {
			MatrixState.pushMatrix();
			MatrixState.translate(0, yOffset, 0);
			MatrixState.rotate(-90, 1, 0, 0);
			circle_station.drawSelf(0f, stageId);// 不透明圆面
			MatrixState.popMatrix();

			GLES20.glDisable(GLES20.GL_DEPTH_TEST);// 深度检测
			// 绘制倒影
			MatrixState.pushMatrix();
			MatrixState.rotate(180, 0, 0, 1);
			planeModel[planeModelIndex]
					.drawSelf(planeModelTexId[planeModelIndex]);
			MatrixState.popMatrix();
			GLES20.glEnable(GLES20.GL_DEPTH_TEST);// 深度检测
			GLES20.glEnable(GLES20.GL_BLEND);// 开启混合
			GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA,
					GLES20.GL_ONE_MINUS_SRC_ALPHA);// 设置混合因子
			MatrixState.pushMatrix();
			MatrixState.translate(0, yOffset + 0.5f, 0);
			MatrixState.rotate(-90, 1, 0, 0);
			circle_station.drawSelf(0.4f, stageId);// 透明圆面
			MatrixState.popMatrix();

			GLES20.glDisable(GLES20.GL_BLEND); // 关闭混合
			MatrixState.pushMatrix();
			MatrixState.translate(0, yOffset, 0);
			MatrixState.rotate(-90, 1, 0, 0);
			MatrixState.pushMatrix();

			MatrixState.rotate(90, 1, 0, 0);
			MatrixState.translate(0, -30, 0);
			circle_station.taizi.drawSelf(stageId);// 圆柱
			MatrixState.popMatrix();
			MatrixState.popMatrix();
			// 绘制实际船
			MatrixState.pushMatrix();
			planeModel[planeModelIndex]
					.drawSelf(planeModelTexId[planeModelIndex]);
			MatrixState.popMatrix();
		}
	}

	// 加载所有的资源
	public void loadResource() {
		switch (load_step) {
		case 0:
			init_Shader();
			load_step++;
			break;
		case 1:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 2:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 3:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 4:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 5:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 6:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 7:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 8:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 9:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 10:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 11:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 12:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 13:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 14:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 15:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 16:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 17:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 18:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 19:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 20:
			init_All_Texture(load_step);
			load_step++;
			break;
		case 21:
			init_All_Object(load_step);
			load_step++;
			break;
		case 22:
			init_All_Object(load_step);
			load_step++;
			break;
		case 23:
			init_All_Object(load_step);
			load_step++;
			break;
		case 24:
			init_All_Object(load_step);
			load_step++;
			break;
		case 25:
			init_All_Object(load_step);
			load_step++;
			break;
		case 26:
			init_All_Object(load_step);
			load_step++;
			break;
		case 27:
			init_All_Object(load_step);
			load_step++;
			break;
		case 28:
			init_All_Object(load_step);
			load_step++;
			break;
		case 29:
			init_All_Object(load_step);
			load_step++;
			break;
		case 30:
			init_All_Object(load_step);
			load_step++;
			break;
		case 31:
			init_All_Object(load_step);
			load_step++;
			break;
		case 32:
			init_All_Object(load_step);
			load_step++;
			break;
		case 33:
			init_All_Object(load_step);
			load_step++;
			break;
		case 34:
			init_All_Object(load_step);
			load_step++;
			break;
		case 35:
			init_All_Object(load_step);
			load_step++;
			break;
		case 36:
			init_All_Object(load_step);
			load_step++;
			break;
		case 37:
			init_All_Object(load_step);
			load_step++;
			break;
		case 38:
			init_All_Object(load_step);
			load_step++;
			break;
		case 39:
			init_All_Object(load_step);
			load_step++;
			break;
		case 40:
			init_All_Object(load_step);
			isLoadedOk = true;// 切换到一级菜单
			isMenuLevel = 1;// 切换到一级菜单
			loadingView = null;// 销毁
			processBar = null;// 销毁
			break;
		}
	}

	// 处理shader方法
	public void init_Shader() {
		ShaderManager.loadCodeFromFile(getResources());
		ShaderManager.compileShader();
	}

	// 加载纹理的方法
	public void init_All_Texture(int index) {
		switch (index) {
		case 1:
			stageId = initTexture(getResources(), R.drawable.taiziwenli, false);// 展台纹理
			// 飞机纹理
			planeModelTexId[0] = initTexture(getResources(),
					R.drawable.feijione, false);
			planeModelTexId[1] = initTexture(getResources(),
					R.drawable.feijitwo, false);
			planeModelTexId[2] = initTexture(getResources(),
					R.drawable.feijithree, false);
			tex_plane_select_head = initTexture(getResources(),
					R.drawable.plane_select_head, false);// 标题纹理
			break;
		case 2:
			tex_menu_two_war_btnId[0] = initTexture(getResources(),
					R.drawable.menu_two_war_btn_up, false);
			tex_menu_two_war_btnId[1] = initTexture(getResources(),
					R.drawable.menu_two_war_btn_down, false);
			tex_menu_two_action_btnId[0] = initTexture(getResources(),
					R.drawable.menu_two_action_btn_up, false);
			tex_menu_two_action_btnId[1] = initTexture(getResources(),
					R.drawable.menu_two_action_btn_down, false);
			tex_menu_two_okId[0] = initTexture(getResources(),
					R.drawable.plane_select_ok, false);// 菜单二确定按钮
			tex_menu_two_okId[1] = initTexture(getResources(),
					R.drawable.plane_select_ok_down, false);// 菜单二确定按钮
			break;
		case 3:
			tex_menu_two_leftId[0] = initTexture(getResources(),
					R.drawable.menu_two_left_up, false);// 左按按钮
			tex_menu_two_leftId[1] = initTexture(getResources(),
					R.drawable.menu_two_left_down, false);// 左按按钮
			tex_menu_two_rightId[0] = initTexture(getResources(),
					R.drawable.menu_two_right_up, false);// 右按按钮
			tex_menu_two_rightId[1] = initTexture(getResources(),
					R.drawable.menu_two_right_down, false);// 右按按钮
			tex_menu_two_plane_iconId[0][0] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_one_up, false);// 第一幅飞机图片
			tex_menu_two_plane_iconId[0][1] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_one_down, false);// 第二幅飞机图片
			tex_menu_two_plane_iconId[1][0] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_two_up, false);// 第三幅飞机图片
			tex_menu_two_plane_iconId[1][1] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_two_down, false);// 第一幅飞机图片
			tex_menu_two_plane_iconId[2][0] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_three_up, false);// 第二幅飞机图片
			tex_menu_two_plane_iconId[2][1] = initTexture(getResources(),
					R.drawable.menu_two_planeicon_three_down, false);// 第三幅飞机图片
			break;
		case 4:
			// 灯塔纹理
			tex_lighttowerid = initTexture(getResources(), R.drawable.light,
					false);
			tex_lightid = initTexture(getResources(), R.drawable.nighttexid,
					false);
			tex_terrain_tuceng_Id = initTexture(getResources(),
					R.drawable.zhonjiantuceng, true);// .tuceng1);
			tex_terrain_caodiId = initTexture(getResources(), R.drawable.caodi,
					true);
			tex_terrain_shitouId = initTexture(getResources(),
					R.drawable.xiacengtuceng, true);// .shitou);
			tex_terrain_shandingId = initTexture(getResources(),
					R.drawable.shanding, true);// .shitou);
			// 开火按钮纹理
			tex_fireButtonId = initTexture(getResources(),
					R.drawable.firebutton, false);
			tex_lefttimeId = initTexture(getResources(), R.raw.lefttime, false);
			// 天空纹理
			tex_skyBallId = initTexture(getResources(), R.drawable.sky, false);
			tex_nightId = initTexture(getResources(), R.drawable.skynight,
					false);// 夜空
			// 海水纹理
			tex_waterId = initTexture(getResources(), R.drawable.water, false);
			break;
		case 5:
			// 坦克纹理
			tex_tankeid = initTexture(getResources(), R.drawable.tanke, false);
			// 军火库各个纹理
			tex_roofId = initTexture(getResources(), R.drawable.roofwenli,
					false);
			tex_frontId = initTexture(getResources(), R.drawable.fangwufront,
					false);
			tex_AnnulusId = initTexture(getResources(),
					R.drawable.yuanhuanwenli, false);
			break;
		case 6:
			tex_special_action_bgId = initTexture(getResources(),
					R.drawable.map_selected_bg_action, false);// 特别行动背景图
			treeTexId_2 = initTexture(getResources(), R.drawable.tree2, false);
			;// 树纹理
			treeTexId = initTexture(getResources(), R.drawable.tree, false);
			;// 树纹理
			// 锁定矩形纹理
			tex_locktexId = initTexture(getResources(), R.drawable.locktexid,
					false);
			// 锁定目标纹理
			tx_lockaimtexId = initTexture(getResources(), R.raw.locktexidaim,
					false);
			// 飞机被击中的纹理
			tex_plane_hitId = initTexture(getResources(),
					R.drawable.planehittext, false);
			// 烟囱
			tex_chimneyId = initTexture(getResources(), R.drawable.chimney,
					false);
			// 平房的纹理Id
			tex_housePlaneId[0] = initTexture(getResources(),
					R.drawable.bigsmallpingfang, false);
			tex_housePlaneId[1] = initTexture(getResources(),
					R.drawable.bigsmallpingfangwuding, false);
			// 小平房纹理
			tex_housePlaneSmallId[0] = initTexture(getResources(),
					R.drawable.smallpingfang, false);
			tex_housePlaneSmallId[1] = initTexture(getResources(),
					R.drawable.smallpingfangwuding, false);
			break;
		case 7:
			// 界面菜单各种纹理，飞机坠毁后的
			tex_menu_text = initTexture(getResources(),
					R.drawable.caidanfanhuianniu, false);// 文字
			tex_menu_text_win = initTexture(getResources(),
					R.drawable.caidanshengli, false);// 赢了时的菜单文字
			// 视频播放按钮，演示过程按钮
			stopId = initTexture(getResources(), R.drawable.stop, false);// 停止按钮
			pauseId = initTexture(getResources(), R.drawable.pause, false);// 暂停按钮
			playId = initTexture(getResources(), R.drawable.play, false);// 播放按钮
			break;
		case 8:
			// -----------------------初始化菜单部分的纹理
			// 子弹纹理
			tex_bulletId = initTexture(getResources(),
					R.drawable.bullet_purple, false);
			// 雷达背景
			tex_radar_bg_Id = initTexture(getResources(), R.drawable.rador_bg,
					false);
			// 雷达飞机指针
			tex_radar_plane_Id = initTexture(getResources(),
					R.drawable.rador_plane, false);
			// 武器图标
			tex_button_weaponId[0] = initTexture(getResources(),
					R.drawable.bullet_button, false);// 子弹按钮图标
			tex_button_weaponId[1] = initTexture(getResources(),
					R.drawable.missile_button, false);// 导弹按钮图标
			// 向上按钮纹理
			tex_button_upId = initTexture(getResources(), R.drawable.button_up,
					false);
			// 向下按钮纹理
			tex_button_downId = initTexture(getResources(),
					R.drawable.button_down, false);
			break;
		case 9:
			tex_musicId[1] = initTexture(getResources(), R.drawable.music_on,
					false);// 是否开启音乐纹理
			tex_musicId[0] = initTexture(getResources(), R.drawable.music_off,
					false);// 是否开启音乐纹理
			tex_soundId[1] = initTexture(getResources(), R.drawable.sounds_on,
					false);// 是否开启特效声音纹理

			break;
		case 10:
			// 飞机组件纹理
			planeHeadId = initTexture(getResources(), R.drawable.planehead,
					false);
			frontWingId = initTexture(getResources(), R.drawable.frontwing,
					false);
			frontWing2Id = initTexture(getResources(), R.drawable.frontwing2,
					false);
			bacckWingId = initTexture(getResources(), R.drawable.planebody,
					false);
			topWingId = planeHeadId;
			planeBodyId = bacckWingId;
			planeCabinId = planeHeadId;
			cylinder1Id = planeHeadId;
			cylinder2Id = cylinder1Id;
			screw1Id = planeCabinId;
			break;
		case 11:
			// 特殊行动成功失败对话框
			tex_actionWinId = initTexture(getResources(), R.raw.action_win,
					false);// 特殊行动成功对话框
			tex_actionFailId = initTexture(getResources(), R.raw.action_fail,
					false);// 失败对话框
			// -----游戏开始前的说明文字
			tex_noticeId[0] = initTexture(getResources(), R.raw.war_yyxd, false);
			tex_noticeId[1] = initTexture(getResources(), R.raw.war_wzgl, false);
			tex_noticeId[2] = initTexture(getResources(), R.raw.war_zjfc, false);
			tex_noticeId[3] = initTexture(getResources(), R.raw.action_plxd,
					false);
			tex_noticeId[4] = initTexture(getResources(), R.raw.action_smfb,
					false);
			tex_noticeId[5] = initTexture(getResources(), R.raw.action_zsxd,
					false);
			break;
		case 12:
			// 高射炮的纹理
			texBarbetteId[0] = initTexture(getResources(),
					R.drawable.barrel_circle_long, false);
			texBarbetteId[0] = initTexture(getResources(),
					R.drawable.barrel_circle_short, false);
			texCubeId = initTexture(getResources(),
					R.drawable.barrel_cylinder_long, false);
			texBarrelId[0] = initTexture(getResources(),
					R.drawable.barrel_cylinder_long, false);
			texBarrelId[1] = initTexture(getResources(),
					R.drawable.barrel_circle_long, false);
			texBarrelId[2] = initTexture(getResources(),
					R.drawable.barrel_cylinder_short, false);
			texBarrelId[3] = initTexture(getResources(),
					R.drawable.barrel_circle_short, false);
			break;
		case 13:
			baoZhaXiaoguo = initTexture(getResources(),
					R.drawable.baozaoxiaoguo, false);// 爆炸效果
			baoZhaXiaoguo2 = initTexture(getResources(), R.drawable.baozhazdan,
					false);// 爆炸效果2
			tex_numberRectId = initTexture(getResources(), R.drawable.number,
					false);// 数字纹理
			tex_backgroundRectId = initTexture(getResources(),
					R.drawable.xuebeijing, false);// 血背景图片
			tex_damId = initTexture(getResources(), R.drawable.dam, false);// 大坝
			break;
		case 14:
			// 标志其位置的在仪表盘上的
			tex_mark_tanke = initTexture(getResources(), R.drawable.marktanke,
					false);// 坦克和高射炮仪表盘图标
			tex_mark_ackId = initTexture(getResources(), R.drawable.markask,
					false);// 敌机仪表盘图标
			tex_mark_arsenalId = initTexture(getResources(),
					R.drawable.markarsenal, false);// 军火库仪表盘图标
			tex_mark_planeId = initTexture(getResources(),
					R.drawable.markplane, false);// 玩家飞机仪表盘图标
			break;
		case 15:
			tex_rectId[0] = initTexture(getResources(), R.drawable.start, false);
			tex_rectId[1] = initTexture(getResources(), R.drawable.config,
					false);
			tex_rectId[2] = initTexture(getResources(), R.drawable.rank, false);
			tex_rectId[3] = initTexture(getResources(), R.drawable.help, false);
			tex_rectId[4] = initTexture(getResources(), R.drawable.about, false);
			tex_rectId[5] = initTexture(getResources(), R.drawable.exit, false);
			break;
		case 16:
			tex_rectId[6] = initTexture(getResources(), R.drawable.other, false);
			tex_rectId[7] = tex_rectId[6];
			tex_rectId[8] = initTexture(getResources(), R.drawable.missile_end,
					false);
			tex_rectId[9] = initTexture(getResources(),
					R.drawable.missile_cylinder, false);
			tex_rectId[10] = initTexture(getResources(),
					R.drawable.missile_tail, false);
			tex_bgId = initTexture(getResources(), R.drawable.land, false);// 导弹菜单下的背景图
			break;
		case 17:
			tex_cloudsId = initTexture(getResources(), R.raw.clouds, false);// 导弹菜单下的云彩
			tex_front_frameId = initTexture(getResources(),
					R.drawable.front_frame, false);// 导弹菜单下的背景图
			tex_front_coverId = initTexture(getResources(),
					R.drawable.front_cover, false);// 导弹菜单下的背景图
			tex_menu_doorId = initTexture(getResources(), R.drawable.menu_door,
					false);// 导弹菜单下的机舱门背景
			break;
		case 18:
			tex_soundId[0] = initTexture(getResources(), R.drawable.sounds_off,
					false);// 是否开启特效声音纹理
			tex_vibrateId[1] = initTexture(getResources(),
					R.drawable.vibrate_on, false);// 是否开启震动纹理
			tex_vibrateId[0] = initTexture(getResources(),
					R.drawable.vibrate_off, false);// 是否开启震动纹理
			tex_helpId = initTexture(getResources(), R.drawable.helpview, false);// 帮助界面Id
			break;
		case 19:
			tex_aboutId = initTexture(getResources(), R.drawable.aboutview,
					false);// 关于界面Id
			tex_mapSelectedBgId = initTexture(getResources(),
					R.drawable.map_selected_bg, false);// 地图选择界面的背景
			tex_mapId[0] = initTexture(getResources(),
					R.drawable.yeyingxingdong, false);// 地图选择界面的背景
			break;
		case 20:
			tex_mapId[1] = initTexture(getResources(),
					R.drawable.zhongjifuchou, false);// 地图选择界面的背景
			tex_mapId[2] = initTexture(getResources(),
					R.drawable.wangzheguilai, false);// 地图选择界面的背景
			tex_rankBgId = initTexture(getResources(), R.drawable.rank_bg,
					false);// 排行榜背景图
			tex_rankNumberId = initTexture(getResources(),
					R.drawable.rank_number, false);// 排行榜界面的数字
			break;
		}
	}

	// 创建所有的对象
	public void init_All_Object(int index)// 第一次进来必须创建的，如果是第二次进来就不需要了
	{
		switch (index) {
		case 21:
			// ------------------------------创建导弹菜单
			missile_menu = new MissileMenuForDraw(
					ShaderManager.getOnlyTextureShaderProgram());// 导弹菜单
			menu_Background = new TextureRect(150, 100,
					ShaderManager.getWaterTextureShaderProgram(), true, 1);// 陆地
			menu_clouds = new TextureRect(200, 150,
					ShaderManager.getWaterTextureShaderProgram(), true, 3f);// 云彩
			break;
		case 22:
			menu_setting = new TextureRect(SETTING_BUTTON_WIDTH,
					SETTING_BUTTON_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 创建机舱门
			helpView = new TextureRect(HELP_WIDTH, HELP_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 帮助界面
			aboutView = new TextureRect(ABOUT_WIDTH, ABOUT_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 关于界面
			rank_number = new NumberForDraw(11, RANK_NUMBER_WIDTH,
					RANK_NUMBER_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			map_name = new TextureRect(RANK_MAP_WIDTH, RANK_MAP_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 排行榜界面的地图的名称
			break;
		case 23:
			plane_select_head = new TextureRect(PLANE_SELECT_HEAD_WIDTH,
					PLANE_SELECT_HEAD_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 选飞机场景中的标题栏
			plane_select_plane = new TextureRect(PLANE_SELECT_PLANE_WIDTH,
					PLANE_SELECT_PLANE_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 0);// 选飞机场景中的选飞机按钮
			menu_two_game_model_btn = new TextureRect(
					MENU_TWO_GAME_MODEL_BUTTON_WIDTH,
					MENU_TWO_GAME_MODEL_BUTTON_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 2);// 选飞机场景中的选模式按钮
			menu_two_button = new TextureRect(MENU_TWO_BUTTON_WIDTH,
					MENU_TWO_BUTTON_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 0);// 选飞机场景中的选模式按钮
			menu_two_plane_icon = new TextureRect(MENU_TWO_PLANE_ICON_WIDTH,
					MENU_TWO_PLANE_ICON_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 0);
			// ----创建说明文字----------------
			noticeRect = new TextureRect(NOTICE_WIDTH, NOTICE_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 24:
			// 加载地形信息
			Constant.initLandsHeightInfo(getResources());
			// 创建陆地
			for (int i = 0; i < LANDS_SIZE; i++) {
				terrain[i] = new LandForm(i,
						ShaderManager.getLandformTextureShaderProgram());
			}
			// 创建平面地图
			terrain_plain = new TextureRect(WIDTH_LALNDFORM, HEIGHT_LANDFORM,
					ShaderManager.getOnlyTextureShaderProgram());
			// 创建开火按钮
			fireButton = new TextureRect(BUTTON_FIRE_WIDTH, BUTTON_FIRE_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 1);
			break;
		case 25:
			// 创建天空球
			skyBall = new SkyBall(GLGameView.this, SKY_BALL_RADIUS,
					ShaderManager.getOnlyTextureShaderProgram(), 0, 0, 0);
			skyBallsmall = new SkyBall(GLGameView.this, SKY_BALL_SMALL,
					ShaderManager.getOnlyTextureShaderProgram(), 0, 0, 0);
			skynight = new SkyNight(1.5f, 100, SKY_BALL_SMALL - 100);
			skynightBig = new SkyNight(2, 50, SKY_BALL_SMALL - 100);
			skynightBig.initShader(ShaderManager.getStarrySkyShaderProgram());
			skynight.initShader(ShaderManager.getStarrySkyShaderProgram());
			break;
		case 26:
			// 加载坦克模型
			tanke_body = MoXingJiaZai
					.loadFromFileVertexOnly("tank_body.obj", getResources(),
							ShaderManager.getOnlyTextureShaderProgram());// 坦克
			break;
		case 27:
			weapon_number = new NumberForDraw(11, WEAPON_NUMBER_WIDTH,
					WEAPON_NUMBER_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			// 向上按钮
			up_button = new TextureRect(BUTTON_UP_WIDTH, BUTTON_UP_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 1);
			// 向下选择按钮
			down_button = new TextureRect(BUTTON_DOWN_WIDTH,
					BUTTON_DOWN_HEIGHT,
					ShaderManager.getButtonTextureShaderProgram(), 1, 1);
			// 显示剩余时间
			leftTimeRect = new TextureRect(2 * ratio * 0.15f, 2 * 0.13f,
					ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 28:
			// 创建挡板
			cube = new CubeForDraw(cube_length, cube_width, cube_height,
					ShaderManager.getOnlyTextureShaderProgram());
			chimney = new Light_Tower(8, 15, 150, 1);// 创建烟囱
			chimney.initShader(ShaderManager.getOnlyTextureShaderProgram());
			lighttower = new Light_Tower(25, 1, 300, 1);// 创建灯塔
			lighttower.initShader(ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 29:
			// 创建水面
			water = new TextureRect(SKY_BALL_RADIUS * 3.5f,
					SKY_BALL_RADIUS * 3.5f,
					ShaderManager.getOnlyTextureShaderProgram(), true, 20);// ,true,0,0,0);//
			// 创建子弹纹理球
			bullet_ball = new BallTextureByVertex(BULLET_SCALE,
					ShaderManager.getOnlyTextureShaderProgram(), -90);
			break;
		case 30:
			// 创建武器按钮
			weapon_button = new TextureRect(BUTTON_WEAPON_WIDTH,
					BUTTON_WEAPON_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			numberRect = new NumberForDraw(10, NUMBER_WIDTH, NUMBER_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 创建数字
			backgroundRect_blood = new TextureRect(NUMBER_WIDTH * 10,
					NUMBER_HEIGHT, ShaderManager.getStarryXueShaderProgram(),
					2, 0);
			break;
		case 31:
			house = new House(ShaderManager.getOnlyTextureShaderProgram(),
					backgroundRect_blood, numberRect);// 创建军火库模型
			housePlane = new CubeForDraw(house_length, house_width,
					house_height, ShaderManager.getOnlyTextureShaderProgram());// 创建平房
			bombRect = new TextureRect(bomb_width, bomb_height,
					ShaderManager.getOnlyTextureShaderProgram());// 爆炸效果纹理
			bombRectr = new TextureRect(bomb_width / 2, bomb_height / 2,
					ShaderManager.getOnlyTextureShaderProgram());// 爆炸效果纹理
			break;
		case 32:
			// 飞机杯击中的纹理矩形
			plane_Hit = new TextureRect(ratio * 2, 2,
					ShaderManager.getOnlyTextureShaderProgram());
			menu_Rect = new TextureRect(0.8f, 1.2f,
					ShaderManager.getOnlyTextureShaderProgram());
			;// 飞机爆炸后的菜单显示矩形
			menu_video = new TextureRect(0.25f * ratio, 0.35f,
					ShaderManager.getOnlyTextureShaderProgram());// 播放界面的各个按钮
			break;
		case 33:
			// 标志位置的矩形
			mark_placeRect = new TextureRect(0.025f, 0.025f,
					ShaderManager.getOnlyTextureShaderProgram());
			// 创建飞机
			plane = new Plane(this,
					ShaderManager.getOnlyTextureShaderProgram(), mark_placeRect);
			// 创建锁定矩形纹理
			mark_lock = new TextureRect(ARCHIBALD_X, ARCHIBALD_Y,
					ShaderManager.getOnlyTextureShaderProgram());
			// 目标线框
			mark_aim = new TextureRect(10, 10,
					ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 34:
			// 创建树纹理矩形
			treeRect = new TextureRect(treeWhidth, treeHeight,
					ShaderManager.getOnlyTextureShaderProgram());
			// 加载飞机模型
			planeModel[0] = MoXingJiaZai
					.loadFromFileVertexOnly("feiji11.obj", getResources(),
							ShaderManager.getOnlyTextureShaderProgram());// 飞机
			planeModel[1] = MoXingJiaZai
					.loadFromFileVertexOnly("feiji22.obj", getResources(),
							ShaderManager.getOnlyTextureShaderProgram());// 飞机
			planeModel[2] = MoXingJiaZai
					.loadFromFileVertexOnly("feiji33.obj", getResources(),
							ShaderManager.getOnlyTextureShaderProgram());// 飞机
			break;
		case 35:
			// 加载坦克模型
			tanke_gun = MoXingJiaZai
					.loadFromFileVertexOnly("tank_berral1.obj", getResources(),
							ShaderManager.getOnlyTextureShaderProgram());// 坦克
			break;
		case 36:
			// -------------二级菜单中的物体------------------------------------------------------
			backgroundRect = new TextureRect(450, 450,
					ShaderManager.getOnlyTextureShaderProgram());
			circle_station = new CircleForDraw(
					ShaderManager.getOnlyColorShaderProgram(), 5, 70,
					new float[] { 0.3f, 0.3f, 0.3f },
					ShaderManager.getOnlyTextureShaderProgram());// 创建选船界面的额模型
			break;
		case 37:
			front_frame = new TextureRect(ratio * 2, 2,
					ShaderManager.getOnlyTextureShaderProgram());// 云彩
			front_cover_button = new TextureRect(MENU_BUTTON_WIDTH,
					MENU_BUTTON_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 导弹菜单下的按钮
			front_door = new TextureRect(MENU_DOOR_WIDTH, MENU_DOOR_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());// 创建机舱门
			front_door_bg = new TextureRect(MENU_DOOR_WIDTH,
					MENU_DOOR_HEIGHT * 1.8f,
					ShaderManager.getOnlyTextureShaderProgram());// 创建机舱门
			break;
		case 38:
			// 创建炮管
			barrel = new BarrelForDraw(barrel_length, barrel_radius,
					ShaderManager.getOnlyTextureShaderProgram());
			// 创建炮台
			barbette = new BarbetteForDraw(barbette_length, barbette_radius,
					ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 39:
			// 创建子弹纹理矩形
			bullet_rect = new TextureRect(BULLET_WIDTH, BULLET_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			// 创建雷达背景
			radar_bg = new TextureRect(BUTTON_RADAR_BG_WIDTH,
					BUTTON_RADAR_BG_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			// 创建雷达指针
			radar_plane = new TextureRect(BUTTON_RADAR_PLANE_WIDTH,
					BUTTON_RADAR_PLANE_HEIGHT,
					ShaderManager.getOnlyTextureShaderProgram());
			break;
		case 40:
			kThread = new KeyThread(this);
			kThread.start();
			break;
		}
	}

	public void initMap()// 进入第二关等需要创建的
	{
		tankeList.clear();// 坦克清零
		archie_List.clear();// 高射炮清零
		bomb_List.clear();// 炮弹清零
		archie_bomb_List.clear();// 高射炮炮弹清零
		bullet_List.clear();// 飞机发射的子弹清零
		enemy.clear();// 敌机数组清零
		arsenal.clear();// 军火库清零
		houseplane.clear();// 平房清零
		treeList.clear();// 树
		// 创建敌机
		for (int i = 0; i < enemy_plane_place[mapId].length; i++) {
			enemy.add(new EnemyPlane(this, plane,
					enemy_plane_place[mapId][i][0],
					enemy_plane_place[mapId][i][1],
					enemy_plane_place[mapId][i][2],
					enemy_plane_place[mapId][i][3],
					enemy_plane_place[mapId][i][4],
					enemy_plane_place[mapId][i][5], backgroundRect_blood,
					numberRect, mark_placeRect, mark_lock, i));
		}
		// 创建坦克
		for (int i = 0; i < ArchieArray[mapId][1].length / 2; i++) {

			tankeList
					.add(new TanKe(this, bullet_ball, tanke_body, tanke_gun,
							new float[] {
									ArchieArray[mapId][1][i * 2] * WATER_WIDTH,
									LAND_HIGHEST,
									ArchieArray[mapId][1][i * 2 + 1]
											* WATER_WIDTH },
							(int) ArchieArray[mapId][1][i * 2],
							(int) ArchieArray[mapId][1][i * 2 + 1],
							backgroundRect_blood, numberRect, mark_placeRect,
							mark_lock));
		}
		// 将高射炮放入列表中
		for (int i = 0; i < ArchieArray[mapId][0].length / 2; i++) {
			archie_List
					.add(new ArchieForControl(this, barrel, barbette, cube,
							bullet_ball, new float[] {
									ArchieArray[mapId][0][i * 2]
											* WIDTH_LALNDFORM,
									LAND_HIGHEST + barbette_length / 2,
									ArchieArray[mapId][0][i * 2 + 1]
											* WIDTH_LALNDFORM },
							(int) ArchieArray[mapId][0][i * 2 + 1],
							(int) ArchieArray[mapId][0][i * 2],
							backgroundRect_blood, numberRect, mark_placeRect,
							mark_lock));
		}
		// 创建军火库
		for (int i = 0; i < ArchieArray[mapId][2].length / 2; i++) {
			arsenal.add(new Arsenal_House(house, ArchieArray[mapId][2][2 * i]
					* WIDTH_LALNDFORM, LAND_HIGHEST,
					ArchieArray[mapId][2][2 * i + 1] * WIDTH_LALNDFORM,
					mark_placeRect, mark_lock,
					(int) ArchieArray[mapId][2][2 * i],
					(int) ArchieArray[mapId][2][2 * i + 1]));// 创建军火库
		}
		// 创建树
		for (int i = 0; i < ArchieArray[mapId][11].length / 4; i++) {
			treeList.add(new Tree(treeRect, ArchieArray[mapId][11][i * 4]
					* WIDTH_LALNDFORM, LAND_HIGHEST + treeHeight / 2 - 5,
					ArchieArray[mapId][11][i * 4 + 1] * WIDTH_LALNDFORM,
					treeTexId_2, (int) ArchieArray[mapId][11][i * 4],
					(int) ArchieArray[mapId][11][i * 4 + 1]));
			treeList.add(new Tree(treeRect, ArchieArray[mapId][11][i * 4 + 2]
					* WIDTH_LALNDFORM, LAND_HIGHEST + treeHeight / 2 - 5,
					ArchieArray[mapId][11][i * 4 + 3] * WIDTH_LALNDFORM,
					treeTexId, (int) ArchieArray[mapId][11][i * 4 + 2],
					(int) ArchieArray[mapId][11][i * 4 + 3]));
		}
		// 创建房屋
		for (int i = 0; i < ArchieArray[mapId][4].length / 2; i++) {
			houseplane.add(new PlaneHouse(ArchieArray[mapId][4][2 * i]
					* WIDTH_LALNDFORM, LAND_HIGHEST + house_height / 2,
					ArchieArray[mapId][4][2 * i + 1] * WIDTH_LALNDFORM,
					housePlane, chimney, (int) ArchieArray[mapId][4][2 * i],
					(int) ArchieArray[mapId][4][2 * i + 1]));
		}
		// 创建大坝
		dam = null;
		if (ArchieArray[mapId][5].length > 0) {
			dam = new DamForDraw(LAND_HIGHEST - 20, 30, 90, 150,
					ShaderManager.getOnlyTextureShaderProgram());
		}
	}

	// 计算虚拟按钮的范围,在onChanged方法中调用
	public void ConfigVirtualButtonArea() {
		// ---------------一级菜单-----------------------------
		// 导弹菜单中选项按钮的相关参数
		MENU_BUTTON_WIDTH = ratio * 0.5f;
		MENU_BUTTON_HEIGHT = 1 * 0.38f;
		float leftEdge = (float) (ratio - MENU_BUTTON_WIDTH / 2 + MENU_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		float rightEdge = (float) (ratio + MENU_BUTTON_WIDTH / 2 + MENU_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		float topEdge = (float) (1 - MENU_BUTTON_HEIGHT / 2 - MENU_BUTTON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		float bottomEdge = (float) (1 + MENU_BUTTON_HEIGHT / 2 - BUTTON_FIRE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_BUTTON_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 导弹菜单中的机舱门相关参数
		MENU_DOOR_WIDTH = ratio * 2;
		MENU_DOOR_HEIGHT = 1;
		// ------------------------设置页面按钮的相关参数----------------------------------
		SETTING_BUTTON_WIDTH = ratio; // 设置界面按钮的宽度
		SETTING_BUTTON_HEIGHT = 0.5f; // 设置界面按钮的高度

		SETTING_BUTTON_XOffset1 = -2 * ratio * 0.2f;
		SETTING_BUTTON_YOffset1 = 0.43f;

		SETTING_BUTTON_XOffset2 = 0;
		SETTING_BUTTON_YOffset2 = -2 * ratio * 0.02f;

		SETTING_BUTTON_XOffset3 = 2 * ratio * 0.2f;
		SETTING_BUTTON_YOffset3 = -0.55f;

		// 设置页面按钮的范围1
		leftEdge = (float) (ratio - SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset1)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset1)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset1)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset1)
				/ 2 * SCREEN_HEIGHT;
		SETTING_BUTTON_AREA1 = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 设置页面按钮的范围2
		leftEdge = (float) (ratio - SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset2)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset2)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset2)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset2)
				/ 2 * SCREEN_HEIGHT;
		SETTING_BUTTON_AREA2 = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 设置页面按钮的范围3
		leftEdge = (float) (ratio - SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset3)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + SETTING_BUTTON_WIDTH / 2 + SETTING_BUTTON_XOffset3)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset3)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + SETTING_BUTTON_HEIGHT / 2 - SETTING_BUTTON_YOffset3)
				/ 2 * SCREEN_HEIGHT;
		SETTING_BUTTON_AREA3 = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 初始化退出对话框的宽度和高度
		EXIT_DIALOG_WIDTH = ratio;
		EXIT_DIALOG_HEIGHT = 1;
		// 确定按钮的参数
		DIALOG_BUTTON_WIDTH = EXIT_DIALOG_WIDTH / 2;
		DIALOG_BUTTON_HEIGHT = EXIT_DIALOG_HEIGHT / 2;
		DIALOG_YES_XOffset = -EXIT_DIALOG_WIDTH / 4;
		DIALOG_YES_YOffset = -EXIT_DIALOG_HEIGHT / 4;
		// 返回按钮的参数
		DIALOG_NO_XOffset = EXIT_DIALOG_WIDTH / 4;
		DIALOG_NO_YOffset = -EXIT_DIALOG_HEIGHT / 4;
		// 确定按钮的范围
		leftEdge = (float) (ratio - DIALOG_BUTTON_WIDTH / 2 + DIALOG_YES_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + DIALOG_BUTTON_WIDTH / 2 + DIALOG_YES_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - DIALOG_BUTTON_HEIGHT / 2 - DIALOG_YES_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + DIALOG_BUTTON_HEIGHT / 2 - DIALOG_YES_YOffset)
				/ 2 * SCREEN_HEIGHT;
		DIALOG_BUTTON_YES = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 返回按钮的范围
		leftEdge = (float) (ratio - DIALOG_BUTTON_WIDTH / 2 + DIALOG_NO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + DIALOG_BUTTON_WIDTH / 2 + DIALOG_NO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - DIALOG_BUTTON_HEIGHT / 2 - DIALOG_NO_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + DIALOG_BUTTON_HEIGHT / 2 - DIALOG_NO_YOffset)
				/ 2 * SCREEN_HEIGHT;
		DIALOG_BUTTON_NO = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 帮助页面的宽度和高度
		HELP_WIDTH = ratio * 2 * 0.85f;
		HELP_HEIGHT = 5;
		// 关于页面的宽度和高度
		ABOUT_WIDTH = ratio * 2 * 0.85f;
		ABOUT_HEIGHT = 4.5f;
		// -----------------------------------------二级菜单----------------------------------
		// 标题
		PLANE_SELECT_HEAD_WIDTH = 2 * ratio;
		PLANE_SELECT_HEAD_HEIGHT = 2 * 0.15f;
		// 三个飞机图片的大小
		MENU_TWO_PLANE_ICON_WIDTH = 2 * ratio * 0.15f;
		MENU_TWO_PLANE_ICON_HEIGHT = 2 * 0.2f;

		MENU_TWO_PLANE_ICON_ONE_XOffset = -ratio + MENU_TWO_PLANE_ICON_WIDTH
				/ 2;// 按钮的偏移量
		MENU_TWO_PLANE_ICON_ONE_YOffset = 1 - PLANE_SELECT_HEAD_HEIGHT
				- MENU_TWO_PLANE_ICON_HEIGHT / 2;

		leftEdge = (float) (ratio - MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_ONE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_ONE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_ONE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_ONE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_PLANE_ICON_ONE_AREA = new float[] { leftEdge, rightEdge,
				topEdge, bottomEdge };

		MENU_TWO_PLANE_ICON_TWO_XOffset = MENU_TWO_PLANE_ICON_ONE_XOffset
				+ MENU_TWO_PLANE_ICON_WIDTH;// 按钮的偏移量
		MENU_TWO_PLANE_ICON_TWO_YOffset = MENU_TWO_PLANE_ICON_ONE_YOffset;

		leftEdge = (float) (ratio - MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_TWO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_TWO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_TWO_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_ONE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_PLANE_ICON_TWO_AREA = new float[] { leftEdge, rightEdge,
				topEdge, bottomEdge };

		MENU_TWO_PLANE_ICON_THREE_XOffset = MENU_TWO_PLANE_ICON_TWO_XOffset
				+ MENU_TWO_PLANE_ICON_WIDTH;// 按钮的偏移量
		MENU_TWO_PLANE_ICON_THREE_YOffset = MENU_TWO_PLANE_ICON_ONE_YOffset;

		leftEdge = (float) (ratio - MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_THREE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_PLANE_ICON_WIDTH / 2 + MENU_TWO_PLANE_ICON_THREE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_THREE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_PLANE_ICON_HEIGHT / 2 - MENU_TWO_PLANE_ICON_THREE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_PLANE_ICON_THREE_AREA = new float[] { leftEdge, rightEdge,
				topEdge, bottomEdge };

		// 菜单二中按钮的宽度和高度
		MENU_TWO_BUTTON_WIDTH = 2 * ratio * 0.15f;
		MENU_TWO_BUTTON_HEIGHT = 2 * 0.15f;
		// 确定按钮
		MENU_TWO_BUTTON_OK_XOffset = ratio - MENU_TWO_BUTTON_WIDTH / 1.5f;// 按钮的偏移量
		MENU_TWO_BUTTON_OK_YOffset = -1 + MENU_TWO_BUTTON_HEIGHT / 1.5f;
		leftEdge = (float) (ratio - MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_OK_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_OK_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_OK_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_OK_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_BUTTON_OK_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 左按按钮
		MENU_TWO_BUTTON_LEFT_XOffset = -ratio + MENU_TWO_BUTTON_WIDTH / 2;// 按钮的偏移量
		MENU_TWO_BUTTON_LEFT_YOffset = -2 * 0.1f;
		leftEdge = (float) (ratio - MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_LEFT_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_LEFT_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_LEFT_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_LEFT_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_BUTTON_LEFT_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// 右按按钮
		MENU_TWO_BUTTON_RIGHT_XOffset = ratio - MENU_TWO_BUTTON_WIDTH / 2;// 按钮的偏移量
		MENU_TWO_BUTTON_RIGHT_YOffset = -2 * 0.1f;
		leftEdge = (float) (ratio - MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_RIGHT_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_BUTTON_WIDTH / 2 + MENU_TWO_BUTTON_RIGHT_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_RIGHT_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_BUTTON_HEIGHT / 2 - MENU_TWO_BUTTON_RIGHT_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_BUTTON_RIGHT_AREA = new float[] { leftEdge, rightEdge,
				topEdge, bottomEdge };
		// -----------------选游戏模式按钮----------------------------
		MENU_TWO_GAME_MODEL_BUTTON_WIDTH = 2 * ratio * 0.2f;
		MENU_TWO_GAME_MODEL_BUTTON_HEIGHT = 2 * 0.15f;
		// -----------------战役模式按钮---------------------
		MENU_TWO_WAR_BUTTON_XOffset = 2 * ratio * 0.15f;// 按钮的偏移量
		MENU_TWO_WAR_BUTTON_YOffset = 1 - PLANE_SELECT_HEAD_HEIGHT
				- MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2;
		// 选战役模式按钮的范围
		leftEdge = (float) (ratio - MENU_TWO_GAME_MODEL_BUTTON_WIDTH / 2 + MENU_TWO_WAR_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_GAME_MODEL_BUTTON_WIDTH / 2 + MENU_TWO_WAR_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2 - MENU_TWO_WAR_BUTTON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2 - MENU_TWO_WAR_BUTTON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_WAR_BUTTON_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
		// -------------------特别行动按钮-----------
		MENU_TWO_ACTION_BUTTON_XOffset = MENU_TWO_WAR_BUTTON_XOffset
				+ MENU_TWO_GAME_MODEL_BUTTON_WIDTH;// 按钮的偏移量
		MENU_TWO_ACTION_BUTTON_YOffset = 1 - PLANE_SELECT_HEAD_HEIGHT
				- MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2;
		// 选战役模式按钮的范围
		leftEdge = (float) (ratio - MENU_TWO_GAME_MODEL_BUTTON_WIDTH / 2 + MENU_TWO_ACTION_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MENU_TWO_GAME_MODEL_BUTTON_WIDTH / 2 + MENU_TWO_ACTION_BUTTON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2 - MENU_TWO_ACTION_BUTTON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MENU_TWO_GAME_MODEL_BUTTON_HEIGHT / 2 - MENU_TWO_ACTION_BUTTON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MENU_TWO_ACTION_BUTTON_AREA = new float[] { leftEdge, rightEdge,
				topEdge, bottomEdge };

		// ------三级菜单-------地图选择界面的按钮的宽度和高度
		MAP_BUTTON_WIDTH = 2 * ratio * 0.23f;
		MAP_BUTTON_HEIGHT = 2 * 0.7f;
		// 第一关的范围
		MAP_ONE_XOffset = -MAP_BUTTON_WIDTH / 0.82f;
		MAP_ONE_YOffset = 0f;
		leftEdge = (float) (ratio - MAP_BUTTON_WIDTH / 2 + MAP_ONE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MAP_BUTTON_WIDTH / 2 + MAP_ONE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MAP_BUTTON_HEIGHT / 2 - MAP_ONE_YOffset) / 2
				* SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MAP_BUTTON_HEIGHT / 2 - MAP_ONE_YOffset) / 2
				* SCREEN_HEIGHT;
		MAP_ONE_AREA = new float[] { leftEdge, rightEdge, topEdge, bottomEdge };
		// 第二关的范围
		MAP_TWO_XOffset = 0f;
		MAP_TWO_YOffset = 0f;
		leftEdge = (float) (ratio - MAP_BUTTON_WIDTH / 2 + MAP_TWO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MAP_BUTTON_WIDTH / 2 + MAP_TWO_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MAP_BUTTON_HEIGHT / 2 - MAP_TWO_YOffset) / 2
				* SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MAP_BUTTON_HEIGHT / 2 - MAP_TWO_YOffset) / 2
				* SCREEN_HEIGHT;
		MAP_TWO_AREA = new float[] { leftEdge, rightEdge, topEdge, bottomEdge };
		// 第三关的范围
		MAP_THREE_XOffset = MAP_BUTTON_WIDTH / 0.77f;
		MAP_THREE_YOffset = 0;
		leftEdge = (float) (ratio - MAP_BUTTON_WIDTH / 2 + MAP_THREE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + MAP_BUTTON_WIDTH / 2 + MAP_THREE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - MAP_BUTTON_HEIGHT / 2 - MAP_THREE_YOffset) / 2
				* SCREEN_HEIGHT;
		bottomEdge = (float) (1 + MAP_BUTTON_HEIGHT / 2 - MAP_THREE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		MAP_THREE_AREA = new float[] { leftEdge, rightEdge, topEdge, bottomEdge };

		// 排行榜界面关卡数字的宽度和高度
		RANK_MAP_WIDTH = 2 * ratio * 0.12f;
		RANK_MAP_HEIGHT = 2 * 0.06f;
		RANK_NUMBER_WIDTH = 2 * ratio * 0.017f;
		RANK_NUMBER_HEIGHT = 2 * 0.07f;

		// -------------------------------游戏中说明文字的宽度和高度
		NOTICE_WIDTH = 2 * ratio;
		NOTICE_HEIGHT = 2;

		// ----------------------------------------
		// 开火按钮的平移
		BUTTON_FIRE_XOffset = ratio - BUTTON_FIRE_WIDTH / 1.5f;
		BUTTON_FIRE_YOffset = -1 + BUTTON_FIRE_HEIGHT / 1.5f;// -----------

		// 开火按钮
		leftEdge = (float) (ratio - BUTTON_FIRE_WIDTH / 2 + BUTTON_FIRE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + BUTTON_FIRE_WIDTH / 2 + BUTTON_FIRE_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - BUTTON_FIRE_HEIGHT / 2 - BUTTON_FIRE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + BUTTON_FIRE_HEIGHT / 2 - BUTTON_FIRE_YOffset)
				/ 2 * SCREEN_HEIGHT;
		BUTTON_FIRE_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };

		BUTTON_WEAPON_XOffset = -ratio + BUTTON_WEAPON_WIDTH / 1.5f;
		BUTTON_WEAPON_YOffset = 1 - BUTTON_WEAPON_HEIGHT / 1.5f;// -----------

		// 武器选择按钮
		leftEdge = (float) (ratio - BUTTON_WEAPON_WIDTH / 2 + BUTTON_WEAPON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + BUTTON_WEAPON_WIDTH / 2 + BUTTON_WEAPON_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - BUTTON_WEAPON_HEIGHT / 2 - BUTTON_WEAPON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + BUTTON_WEAPON_HEIGHT / 2 - BUTTON_WEAPON_YOffset)
				/ 2 * SCREEN_HEIGHT;
		BUTTON_WEAPON_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };

		// 子弹的数量
		WEAPON_NUMBER_XOffset = BUTTON_WEAPON_XOffset + WEAPON_NUMBER_WIDTH * 3;
		WEAPON_NUMBER_YOffset = BUTTON_WEAPON_YOffset - WEAPON_NUMBER_HEIGHT
				/ 2;

		// 向上按钮的平移
		BUTTON_UP_XOffset = -ratio + BUTTON_UP_WIDTH / 2;
		BUTTON_UP_YOffset = -1 + BUTTON_UP_HEIGHT * 1.8f;// ------

		// 向上按钮
		leftEdge = (float) (ratio - BUTTON_UP_WIDTH / 2 + BUTTON_UP_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + BUTTON_UP_WIDTH / 2 + BUTTON_UP_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - BUTTON_UP_HEIGHT / 2 - BUTTON_UP_YOffset) / 2
				* SCREEN_HEIGHT;
		bottomEdge = (float) (1 + BUTTON_UP_HEIGHT / 2 - BUTTON_UP_YOffset) / 2
				* SCREEN_HEIGHT;
		BUTTON_UP_AREA = new float[] { leftEdge, rightEdge, topEdge, bottomEdge };

		BUTTON_DOWN_XOffset = -ratio + BUTTON_DOWN_WIDTH / 2;
		BUTTON_DOWN_YOffset = -1 + BUTTON_DOWN_HEIGHT / 1.7f;// -----------
		// 向下按钮
		leftEdge = (float) (ratio - BUTTON_DOWN_WIDTH / 2 + BUTTON_DOWN_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		rightEdge = (float) (ratio + BUTTON_DOWN_WIDTH / 2 + BUTTON_DOWN_XOffset)
				/ (2 * ratio) * SCREEN_WIDTH;
		topEdge = (float) (1 - BUTTON_DOWN_HEIGHT / 2 - BUTTON_DOWN_YOffset)
				/ 2 * SCREEN_HEIGHT;
		bottomEdge = (float) (1 + BUTTON_DOWN_HEIGHT / 2 - BUTTON_DOWN_YOffset)
				/ 2 * SCREEN_HEIGHT;
		BUTTON_DOWN_AREA = new float[] { leftEdge, rightEdge, topEdge,
				bottomEdge };
	}

	// 调用物理键盘的返回键的方法方法
	public boolean onKeyBackEvent() {
		// 如果当前的当前处于机舱门关闭的状态,那么按下返回键,机舱门打开
		if (!isGameOn && 1 == isMenuLevel && doorState == 2) {
			doorState = 0;
			return true;
		}
		if (!isGameOn && isMenuLevel == 2) {
			isMenuLevel = 1;
			missile_ZOffset = missile_ZOffset_Ori;
			missile_ZOffset_Speed = 0;
			return true;
		}
		if (!isGameOn && isMenuLevel == 3) {
			isMenuLevel = 2;
			return true;
		}
		// 如果当前处于开仓状态,并且当前的菜单索引号不是5,那么要旋转到5,即旋转到退出按钮处
		if (!isGameOn && 1 == isMenuLevel && (doorState == 1)
				&& (curr_menu_index != 5) && !isMissileDowning) {
			moveToExit = true;// 标志位设为true
		}
		return true;
	}
}
