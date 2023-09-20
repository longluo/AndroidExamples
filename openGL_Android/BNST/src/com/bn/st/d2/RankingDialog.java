package com.bn.st.d2;
import com.bn.R;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
//名次对话框
public class RankingDialog extends Dialog 
{
	public RankingDialog(Context context)
	{
        super(context,R.style.FullHeightDialog);
    }
	
	@Override
	public void onCreate (Bundle savedInstanceState)
	{
		this.setContentView(R.layout.rankingdialog);		
	}
	
	@Override
	public String toString()
	{
		return "CheckVersionDialog";
	}
}
