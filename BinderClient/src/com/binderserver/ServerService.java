package com.binderserver;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.binderserver.IAidlBinder.Stub;

public class ServerService extends Service {

	private Fruit mFruit;

	@Override
	public void onCreate() {
		super.onCreate();
		mFruit = new Fruit();
		mFruit.setName("apple");
		mFruit.setColor("red");
		mFruit.setNumber(10);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return serviceBinder;
	}

	private IAidlBinder.Stub serviceBinder = new Stub() {

		@Override
		public String getInfo() throws RemoteException {
			return "I'm a server";
		}

		@Override
		public Fruit getFruit() throws RemoteException {
			return mFruit;
		}
	};

}
