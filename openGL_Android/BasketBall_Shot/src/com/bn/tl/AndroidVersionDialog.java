package com.bn.tl;

import com.bn.tl.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class AndroidVersionDialog extends Dialog 
{   
	public AndroidVersionDialog(Context context)
	{
        super(context,R.style.FullHeightDialog);
    }
	
	@Override
	public void onCreate (Bundle savedInstanceState) 
	{
		this.setContentView(R.layout.currandroidversion); 
	}
	  
	@Override
	public String toString()
	{
		return "AndroidVersionDialog";
	}
}
