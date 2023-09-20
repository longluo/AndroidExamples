package com.bn.st.d2;
import com.bn.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

public class BreakRecordOrNotDialog extends Dialog 
{
	public BreakRecordOrNotDialog(Context context)
	{
        super(context,R.style.FullHeightDialog);
    }
	
	@Override
	public void onCreate (Bundle savedInstanceState) 
	{
		this.setContentView(R.layout.breakrecordornot);		
	}
	
	@Override
	public String toString()
	{
		return "BreakRecordOrNotDialog";
	}
}
