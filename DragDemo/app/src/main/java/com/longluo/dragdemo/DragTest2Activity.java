package com.longluo.dragdemo;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class DragTest2Activity extends AppCompatActivity implements View.OnTouchListener {

    private static final String TAG = DragTest2Activity.class.getSimpleName();

    private View mRootView;

    private SurfaceView mSurfaceView;

    private StreamStatusView mStatusView;

    private FloatBallView mFloatBallView;

    private boolean isPrimaryPressed = false;

    private boolean isSecondPressed = false;

    private boolean isTertiaryPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drag_test2);

        initView();
    }

    private void initView() {
        mRootView = findViewById(R.id.root);

        mRootView.setOnTouchListener(this);
        mRootView.setClickable(true);

        mSurfaceView = findViewById(R.id.surface_stream);

        mStatusView = findViewById(R.id.stream_status);

        mStatusView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "StatusView click");

                Toast.makeText(DragTest2Activity.this, "StatusView Click", Toast.LENGTH_LONG).show();
            }
        });

        mFloatBallView = findViewById(R.id.float_ball);

/*        mFloatBallView.setClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FloatBall Listener click");
                Log.d(TAG, " x = " + v.getX() + ", y = " + v.getY() + ", l = " +  v.getLeft()
                        + ", r = " + v.getRight() + ", t = " + v.getTop() + ", b = " + v.getBottom());

                Toast.makeText(DragTest2Activity.this, "FloatBall Listener Click", Toast.LENGTH_LONG).show();
            }
        });*/

        mFloatBallView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "FloatBall click");

                Toast.makeText(DragTest2Activity.this, "FloatBall Click", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(TAG, "dispatchTouchEvent: action = " + event.getAction() + ", btn state = " + event.getButtonState()
                + ", x = " + event.getX() + ", y = " + event.getY());

        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Log.d(TAG, "onTouch: v x = " + v.getX() + ", y = " +  v.getY());

        Log.d(TAG, "onTouch: action =" + event.getAction() + ", btn state = " + event.getButtonState()
                + ", x = " + event.getX() + ", y = " + event.getY());

        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d(TAG, "onTouchEvent: x = " + event.getX() + ", y = " + event.getY());

        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent event) {
        Log.d(TAG, "dispatchGenericMotionEvent: action = " + event.getAction()
                + ", btn state = " + event.getButtonState() + ", btn = " + event.getActionButton()
                + " , x = " + event.getX() + ", y = " + event.getY());

        Log.d(TAG, "is From Mouse = " + event.isFromSource(InputDevice.SOURCE_MOUSE));

        onMouseEvent(event);

        switch (event.getActionMasked()) {
            // 判断两个动作，基本上能保证准确
            case MotionEvent.ACTION_BUTTON_PRESS:
            case MotionEvent.ACTION_DOWN:
                // 鼠标按键按下
                judgeButtonPress(event);
                Log.d(TAG, "鼠标按键按下 消费来自鼠标的事件");
                return true;

            case MotionEvent.ACTION_BUTTON_RELEASE:
            case MotionEvent.ACTION_UP:
                // 鼠标按键抬起
                judgeButtonRelease(event);
                Log.d(TAG, "鼠标按键抬起 消费来自鼠标的事件");
                return true;

            case MotionEvent.ACTION_SCROLL:
                Log.d(TAG, "鼠标滚轮按键 消费来自鼠标的事件");
                return true;

            default:
                break;
        }

        return super.dispatchGenericMotionEvent(event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent: keyCode = " + event.getKeyCode() + ", action = " + event.getAction());

        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown: keyCode = " + event.getKeyCode() + ", action = " + event.getAction());

        // DOWN 和 UP 聚合起来，统一处理
        if (handleKeyEvent(event)) {
            // 被处理了
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp: keyCode = " + event.getKeyCode() + ", action = " + event.getAction());

        if (handleKeyEvent(event)) {
            // 被处理了
            return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    /**
     * true 表示处理事件，false 表示不处理事件
     */
    public boolean handleKeyEvent(KeyEvent keyEvent) {
        Log.d(TAG, "handleKeyEvent keyCode = " + keyEvent.getKeyCode());

        if (keyEvent.getKeyCode() != KeyEvent.KEYCODE_BACK) {
            return false;
        }

        if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
            // 按键按下
            return onButtonPress(keyEvent);
        } else if (keyEvent.getAction() == KeyEvent.ACTION_UP) {
            // 按键抬起
            return onButtonRelease(keyEvent);
        }

        return false;
    }

    public boolean onButtonPress(KeyEvent keyEvent) {
        Log.d(TAG, "onButtonPress action = " + keyEvent.getAction() + ", keyCode = " + keyEvent.getKeyCode());
        return false;
    }

    public boolean onButtonRelease(KeyEvent keyEvent) {
        Log.d(TAG, "onButtonRelease action = " + keyEvent.getAction() + ", keyCode = " + keyEvent.getKeyCode());

        return false;
    }

    public boolean onMouseEvent(MotionEvent event) {
        Log.d(TAG, "onMouseEvent action = " + event.getAction() + ", btn = "
                + event.getActionButton() + ", masked = " + event.getActionMasked());

        if (!event.isFromSource(InputDevice.SOURCE_MOUSE)) {
            // 事件不来自鼠标
            Log.d(TAG, "事件源不是鼠标");
            return false;
        }

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_HOVER_MOVE:
                // 获取指针在屏幕上 X 轴的位置，值的单位是 dp
                float axisX = event.getAxisValue(MotionEvent.AXIS_X);
                // 获取指针在屏幕上 Y 轴的位置，值的单位是 dp
                float axisY = event.getAxisValue(MotionEvent.AXIS_Y);
                // 另一种写法：int x = (int)motionEvent.getRawX(); int y = (int)motionEvent.getRawY();
                Log.d(TAG, "消费来自鼠标的事件: x = " + axisX + ", y = " + axisY);
                return true;

            case MotionEvent.ACTION_SCROLL:
                // 一般的鼠标，这个用不上。获取水平方向上的滚动距离，值从 -1(向左滚动) 到 1(向右滚动)
                float hScroll = event.getAxisValue(MotionEvent.AXIS_HSCROLL);
                // 获取垂直方向上的滚动距离，值从 -1(向下滚动) 到 1(向上滚动)
                float vScroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);

                Log.d(TAG, "消费来自鼠标的事件 hScroll = " + hScroll + ", vScroll = " + vScroll);
                return true;

            default:
                break;
        }

        return false;
    }

    public void judgeButtonPress(MotionEvent motionEvent) {
        Log.d(TAG, "judgeButtonPress: isPrimaryPressed: " + isPrimaryPressed + ", isSecondPressed: " + isSecondPressed
        + ", isTertiaryPressed: " + isTertiaryPressed);

        // 几个键可能同时被按下(概率较低)
        if (isButtonPress(motionEvent, MotionEvent.BUTTON_PRIMARY)) {
            // 主键被按下了，此处变量缓存，用于后面判断抬起
            isPrimaryPressed = true;
        }

        if (isButtonPress(motionEvent, MotionEvent.BUTTON_SECONDARY)) {
            // 二键被按下了
            isSecondPressed = true;
        }

        if (isButtonPress(motionEvent, MotionEvent.BUTTON_TERTIARY)) {
            // 三键被按下了
            isTertiaryPressed = true;
        }

        Log.d(TAG, "judgeButtonPress After: isPrimaryPressed: " + isPrimaryPressed + ", isSecondPressed: " + isSecondPressed
                + ", isTertiaryPressed: " + isTertiaryPressed);
    }

    /**
     * 判断是否按键被按下
     * 两种场景：
     * 1. 大于目标 SDK 版本，如果按下，则不管(1)；如果没按下，则需要用低版本的检测方法再检测一
     * 次，双重保险，不会出错。对于国内厂商的魔改系统，这是很重要的一点经验(2)
     * 2. 小于目标 SDK 版本，则用低版本的检测方法(3)
     * 综上，可以把 (2)、(3) 合为一种判断
     */
    public boolean isButtonPress(MotionEvent motionEvent, int button) {
        Log.d(TAG, "isButtonPress btn = " + button);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
                && motionEvent.isButtonPressed(button)) {
            return true;
        }

        // 位运算，下面两种写法等效
        // 写法 1：(motionEvent.getButtonState() & MotionEvent.BUTTON_PRIMARY) != 0
        // 下面是写法 2，是 Google 官方采用的写法，详见 MotionEvent.isButtonPressed() 方法
        if (button == 0) {
            return false;
        }

        return (motionEvent.getButtonState() & button) == button;
    }

    private void judgeButtonRelease(MotionEvent motionEvent) {
        Log.d(TAG, "judgeButtonRelease: isPrimaryPressed: " + isPrimaryPressed + ", isSecondPressed: " + isSecondPressed
                + ", isTertiaryPressed: " + isTertiaryPressed);

        // ACTION_BUTTON_RELEASE 事件，不会携带按键信息，只能通过按下的状态来判断。
        // 通过当前事件，判断鼠标键是否被按下
        boolean nowPrimaryPressState = isButtonPress(motionEvent, MotionEvent.BUTTON_PRIMARY);
        boolean nowSecondaryPressState = isButtonPress(motionEvent, MotionEvent.BUTTON_SECONDARY);
        // 鼠标可能同时被按下，也有可能同时被松开
        if (isPrimaryPressed && isSecondPressed && !nowPrimaryPressState && !nowSecondaryPressState) {
            // 之前两个键按下，现在两个键没按下(两个键的 Release)
            isPrimaryPressed = false;
            isSecondPressed = false;
        } else if (isPrimaryPressed && !nowPrimaryPressState) {
            // 之前主键按下，现在按键没按下(单个键的 Release)
            isPrimaryPressed = false;
            // Release 后，可以触发相关动作，此处省略
        } else if (isSecondPressed && !nowSecondaryPressState) {
            // 之前二键按下，现在二键没按下(单个键的 Release)
            isSecondPressed = false;
            // Release 后，可以触发相关动作，此处省略
        }

        // 三键的判断逻辑类似，此处省略

        Log.d(TAG, "judgeButtonRelease: After isPrimaryPressed: " + isPrimaryPressed + ", isSecondPressed: " + isSecondPressed
                + ", isTertiaryPressed: " + isTertiaryPressed);
    }

}
