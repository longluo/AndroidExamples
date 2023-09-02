package com.view.SlipButton;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;

import com.util.SlipButton;
import com.util.SlipButton.OnChangedListener;

public class SlipButtonActivity extends Activity {
    
    private SlipButton sb = null;
    private Button btn = null;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        findView();
        setListener();
    }
    
    /**
     * 设置监听
     */
    private void setListener()
    {
        sb.SetOnChangedListener(new OnChangedListener()
        {
            
            public void OnChanged(boolean CheckState)
            {
                btn.setText(CheckState ? "True" : "False");
            }
        });
    }

    /**
     * 初始化控件
     */
    private void findView()
    {
        sb = (SlipButton) findViewById(R.id.splitbutton);
        btn = (Button) findViewById(R.id.ringagain);
        sb.setCheck(true);
    }
}