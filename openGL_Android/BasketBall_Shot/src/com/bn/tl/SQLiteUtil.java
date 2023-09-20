package com.bn.tl;

import java.util.Date;
import java.util.Vector;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class SQLiteUtil 
{
	static SQLiteDatabase sld;
	//创建或打开数据库的方法
    public static void createOrOpenDatabase()
    {
    	try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.bn.tl/mydb", //当前应用程序只能在自己的包下创建数据库
	    			null, 								//CursorFactory
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //读写、若不存在则创建
	    	);
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
  //关闭数据库的方法
    public static void closeDatabase()
    {
    	try
    	{
	    	sld.close();    
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
    }
    //建表
    public static void createTable(String sql)
    {
    	createOrOpenDatabase();//打开数据库
    	try
    	{
        	sld.execSQL(sql);//建表
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
    	closeDatabase();//关闭数据库
    }
  //插入记录的方法
    public static void insert(String sql)
    {
    	createOrOpenDatabase();//打开数据库
    	try
    	{
        	sld.execSQL(sql);
    	}
		catch(Exception e)
		{
            e.printStackTrace();
		}
		closeDatabase();//关闭数据库
    }
    //查询的方法
    public static Vector<Vector<String>> query(String sql)
    {
    	createOrOpenDatabase();//打开数据库
    	Vector<Vector<String>> vector=new Vector<Vector<String>>();//新建存放查询结果的向量
    	try
    	{
           Cursor cur=sld.rawQuery(sql, new String[]{});
        	while(cur.moveToNext())
        	{
        		Vector<String> v=new Vector<String>();
        		int col=cur.getColumnCount();		//返回每一行都多少字段
        		for( int i=0;i<col;i++)
				{
					v.add(cur.getString(i));					
				}				
				vector.add(v);
        	}
        	cur.close();		
    	}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		closeDatabase();//关闭数据库
		return vector;
    }  
    
  //创建数据库
    public  static void initDatabase(){
    	//创建表
    	String sql="create table if not exists paihangbang(grade int(4),time char(20));";
    	createTable(sql);
    }
    //插入时间的方法
    public static void insertTime(int grade)
    {
    	Date d=new Date();
        String curr_time=(d.getYear()+1900)+"-"+(d.getMonth()+1<10?"0"+
        		(d.getMonth()+1):(d.getMonth()+1))+"-"+d.getDate()+"-"+
        		d.getHours()+"-"+d.getMinutes()+"-"+d.getSeconds();
    	String sql_insert="insert into paihangbang values("+grade+","+"'"+curr_time+"');";
    	insert(sql_insert);
    }
}
