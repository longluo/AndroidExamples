package com.wordpress.enjoyandroid.viewbinder;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteCursorDriver;
import android.database.sqlite.SQLiteDatabase;

/**
 * Static class where we will store initial clubs. This is not an ideal
 * way of storing initial data. Better fit is to store it in xml or 
 * ship the database with the app. Topic for the future.
 * @author gautham
 *
 */
public class InitialClubs {
	public static List<ContentValues> getClubs() {
		List<ContentValues> valuesList = new ArrayList<ContentValues>();
		
		ContentValues arsenalValues = new ContentValues();
		arsenalValues.put(ClubCP.KEY_NAME, "Arsenal");
		arsenalValues.put(ClubCP.KEY_IS_STAR, true);
		arsenalValues.put(ClubCP.KEY_LINK, "http://www.arsenal.com/");
		valuesList.add(arsenalValues);
		
		ContentValues astonValues = new ContentValues();
		astonValues.put(ClubCP.KEY_NAME, "Aston Villa");
		astonValues.put(ClubCP.KEY_IS_STAR, false);
		astonValues.put(ClubCP.KEY_LINK, "http://www.avfc.co.uk/");
		valuesList.add(astonValues);
		
		ContentValues roversValues = new ContentValues();
		roversValues.put(ClubCP.KEY_NAME, "BlackBurn Rovers");
		roversValues.put(ClubCP.KEY_IS_STAR, false);
		roversValues.put(ClubCP.KEY_LINK, "http://www.rovers.co.uk/");
		valuesList.add(roversValues);
		
		ContentValues boltonValues = new ContentValues();
		boltonValues.put(ClubCP.KEY_NAME, "Bolton Wanderers");
		boltonValues.put(ClubCP.KEY_IS_STAR, false);
		boltonValues.put(ClubCP.KEY_LINK, "http://www.bwfc.co.uk/");
		valuesList.add(boltonValues);
		
		ContentValues chelseaValues = new ContentValues();
		chelseaValues.put(ClubCP.KEY_NAME, "Chelsea");
		chelseaValues.put(ClubCP.KEY_IS_STAR, true);
		chelseaValues.put(ClubCP.KEY_LINK, "http://www.chelseafc.com/");
		valuesList.add(chelseaValues);
		
		ContentValues evertonValues = new ContentValues();
		evertonValues.put(ClubCP.KEY_NAME, "Everton");
		evertonValues.put(ClubCP.KEY_IS_STAR, false);
		evertonValues.put(ClubCP.KEY_LINK, "http://www.evertonfc.com/");
		valuesList.add(evertonValues);
		
		ContentValues fulhamValues = new ContentValues();
		fulhamValues.put(ClubCP.KEY_NAME, "Fulham");
		fulhamValues.put(ClubCP.KEY_IS_STAR, false);
		fulhamValues.put(ClubCP.KEY_LINK, "http://www.fulhamfc.com/");
		valuesList.add(fulhamValues);
		
		ContentValues liverpoolValues = new ContentValues();
		liverpoolValues.put(ClubCP.KEY_NAME, "Liverpool");
		liverpoolValues.put(ClubCP.KEY_IS_STAR, false);
		liverpoolValues.put(ClubCP.KEY_LINK, "http://www.liverpoolfc.tv/");
		valuesList.add(liverpoolValues);
		
		ContentValues cityValues = new ContentValues();
		cityValues.put(ClubCP.KEY_NAME, "Manchestar City");
		cityValues.put(ClubCP.KEY_IS_STAR, true);
		cityValues.put(ClubCP.KEY_LINK, "http://www.mcfc.co.uk/");
		valuesList.add(cityValues);
		
		ContentValues unitedValues = new ContentValues();
		unitedValues.put(ClubCP.KEY_NAME, "Manchestar United");
		unitedValues.put(ClubCP.KEY_IS_STAR, true);
		unitedValues.put(ClubCP.KEY_LINK, "http://www.manutd.com/");
		valuesList.add(unitedValues);
		
		ContentValues castleValues = new ContentValues();
		castleValues.put(ClubCP.KEY_NAME, "Newcastle United");
		castleValues.put(ClubCP.KEY_IS_STAR, false);
		castleValues.put(ClubCP.KEY_LINK, "http://www.nufc.co.uk/");
		valuesList.add(castleValues);
		
		ContentValues norwichValues = new ContentValues();
		norwichValues.put(ClubCP.KEY_NAME, "Norwich City");
		norwichValues.put(ClubCP.KEY_IS_STAR, false);
		norwichValues.put(ClubCP.KEY_LINK, "http://www.canaries.co.uk/");
		valuesList.add(norwichValues);
		
		ContentValues queensValues = new ContentValues();
		queensValues.put(ClubCP.KEY_NAME, "Queens Park Rangers");
		queensValues.put(ClubCP.KEY_IS_STAR, false);
		queensValues.put(ClubCP.KEY_LINK, "http://www.qpr.co.uk/");
		valuesList.add(queensValues);
		
		ContentValues stokeValues = new ContentValues();
		stokeValues.put(ClubCP.KEY_NAME, "Stoke City");
		stokeValues.put(ClubCP.KEY_IS_STAR, false);
		stokeValues.put(ClubCP.KEY_LINK, "http://www.stokecityfc.com/");
		valuesList.add(stokeValues);
		
		ContentValues sunderlandValues = new ContentValues();
		sunderlandValues.put(ClubCP.KEY_NAME, "Sunderland");
		sunderlandValues.put(ClubCP.KEY_IS_STAR, false);
		sunderlandValues.put(ClubCP.KEY_LINK, "http://www.safc.com/");
		valuesList.add(sunderlandValues);
		
		ContentValues swanseaValues = new ContentValues();
		swanseaValues.put(ClubCP.KEY_NAME, "Swansea City");
		swanseaValues.put(ClubCP.KEY_IS_STAR, false);
		swanseaValues.put(ClubCP.KEY_LINK, "http://www.swanseacity.net/");
		valuesList.add(swanseaValues);
		
		ContentValues tottenhamValues = new ContentValues();
		tottenhamValues.put(ClubCP.KEY_NAME, "Tottenham Hotspur");
		tottenhamValues.put(ClubCP.KEY_IS_STAR, true);
		tottenhamValues.put(ClubCP.KEY_LINK, "http://www.tottenhamhotspur.com/");
		valuesList.add(tottenhamValues);
		
		ContentValues westValues = new ContentValues();
		westValues.put(ClubCP.KEY_NAME, "West Bromwich Albion");
		westValues.put(ClubCP.KEY_IS_STAR, false);
		westValues.put(ClubCP.KEY_LINK, "http://www.wba.co.uk/");
		valuesList.add(westValues);
		
		ContentValues wiganValues = new ContentValues();
		wiganValues.put(ClubCP.KEY_NAME, "Wigan Athletic");
		wiganValues.put(ClubCP.KEY_IS_STAR, false);
		wiganValues.put(ClubCP.KEY_LINK, "http://www.wiganlatics.com/");
		valuesList.add(wiganValues);
		
		ContentValues wolvesValues = new ContentValues();
		wolvesValues.put(ClubCP.KEY_NAME, "Wolverhampton Wanderers");
		wolvesValues.put(ClubCP.KEY_IS_STAR, false);
		wolvesValues.put(ClubCP.KEY_LINK, "http://www.wolves.co.uk/");
		valuesList.add(wolvesValues);
		
		return valuesList;
	}
	
	/**
	 * Function used to load the clubs to database.
	 * @param db
	 */
	public static void addClubs(SQLiteDatabase db) {
		List<ContentValues> clubsList = getClubs();
		
		for (ContentValues club : clubsList) {
			db.insert(ClubCP.TABLE_NAME, null, club);
		}
	}
	
	
}
