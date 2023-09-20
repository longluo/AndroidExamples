package com.bn.st.d2;

import com.bn.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class CheckVersionDialog extends Dialog 
{
	public CheckVersionDialog(Context context)
	{
        super(context,R.style.FullHeightDialog);
    }
	
	@Override
	public void onCreate (Bundle savedInstanceState) 
	{
		this.setContentView(R.layout.checkversion); 
	}
	
	@Override
	public String toString()
	{
		return "CheckVersionDialog";
	}
}
