package snda.in.ttsdemo;

import java.util.ArrayList;
import java.util.HashMap;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.format.Time;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.snda.tts.service.ITtsService;
import com.snda.tts.service.SndaTts;
import com.snda.tts.service.TtsTask;
import com.snda.tts.utility.InstallManager;


public class SmartCar_Activity extends Activity implements SensorEventListener ,OnTouchListener,OnGestureListener{

    private static final String TAG = "SmartCar_2.0";
    private static final boolean D = true;
    Vibrator vibrator;
    long lastUpdate,lastShakeTime = 0;
    float x,y,last_x = 0,last_y = 0;
    int shake_threshold = 170; //加速度阀值
    int shake_spare = 15000; //间隔阀值
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    TextView tips;
    EditText shake_value;
    private final String PREFERENCE_NAME = "snda.in.ttsdemo";
    private final String TTSSERVICE_BINDNAME = "com.snda.tts.service.TtsService";
    private final String TTSSERVICE = "com.snda.tts.service";
    // A flag means whether the TTS service interface "speak" is accessible currently.
    // See BroadCastReceiver.java comments for more information.
    private final String SPEAK_READY = "SPEAK_READY";
    private final String SPEAK_STATUS = TAG;
    private SharedPreferences mPrefer = null;
    // The minimum tingting center version supported by this demo, 3 means version 1.0.2, which is
    // the currently recommended version.
    private final int MIN_TTS_VERSION = 3;
    private static Button buttonPlay;
    private static Button buttonStop;
    private ITtsService mServiceBinder = null;
    private TtsTask mTtsTask = null;
    private SmartCar_Activity mDemo = null;
    private InstallManager mManager = null;
    private EditText editText1,editText2,editText3,editText4;
    SoundPool soundPool;
	HashMap<Integer , Integer> soundMap = new HashMap<Integer , Integer>();
	
    private ListView m_ListView;   
    private Intent intent_a;   
    IntentFilter intentfilter = new IntentFilter();
    private Cursor cur;   
    private GestureDetector mGestureDetector;   
    private static final int FLING_MIN_DISTANCE = 50;   
    private static final int FLING_MIN_VELOCITY = 100;   
    Smartcar_MusicService.MyBinder binder;
   // int timerightnow=now();
    String voiceindex;
    private AlertDialog.Builder builder = null;
    private AlertDialog ad = null; 

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
     
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        
        LinearLayout main = (LinearLayout)findViewById(R.id.mainlayout);
        Log.i(TAG, "oncreate");
        mDemo = this;
        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	
		vibrator=(Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		Log.i(TAG, "bp0.1");
		Button stop_playing = (Button)findViewById(R.id.stop_playing);
        stop_playing.setOnClickListener(stop_playing_listener);
        Button start_playing = (Button)findViewById(R.id.start_playing);
        start_playing.setOnClickListener(start_playing_listener);
        Log.i(TAG, "bp0.2");
        /*阀值测试输入模块
        tips = (TextView)findViewById(R.id.tips);
        shake_value = (EditText)findViewById(R.id.shake_value);
        Button reset_shake = (Button)findViewById(R.id.reset_shake);
        reset_shake.setOnClickListener(reset_shake_listener);
        */
        
		editText1 = (EditText) this.findViewById(R.id.editText1);
		editText2 = (EditText) this.findViewById(R.id.editText2);
		editText3 = (EditText) this.findViewById(R.id.editText3);
		editText4 = (EditText) this.findViewById(R.id.editText4);
		
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		soundMap.put(1 , soundPool.load(this, R.raw.bomb , 1));
		soundMap.put(2 , soundPool.load(this, R.raw.shot , 1));
		soundMap.put(3 , soundPool.load(this, R.raw.arrow , 1));
		
    
		
        IntentFilter intentFilter = new IntentFilter(SPEAK_STATUS);
        registerReceiver(mBR, intentFilter);
        mPrefer = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        mManager = new InstallManager(this);
        if (mServiceBinder == null) {
            int curVersion = mManager.getInstalledVersion(TTSSERVICE, false);
            Log.i(TAG, curVersion + "");
            if (curVersion >= MIN_TTS_VERSION) {
                Log.i(TAG, "ttsservice is installed, bind service");
                if (mManager.appIsStart(TTSSERVICE)) {
                    Editor editor = mPrefer.edit();
                    editor.putBoolean(SPEAK_READY, true);
                    editor.commit();
                }
                mDemo.bindService(new Intent(TTSSERVICE_BINDNAME), mConnection,
                        Context.BIND_AUTO_CREATE);

            } else {
                // TODO: Pls download TTS package here.
            }
        }
        
        mTtsTask = new TtsTask();
        mTtsTask.caller = TAG;
        
      
        buttonPlay = (Button) this.findViewById(R.id.button_play);
        buttonPlay.setEnabled(true);
        buttonPlay.setOnClickListener(start_speak_listener);
        buttonStop = (Button) this.findViewById(R.id.button_stop);
        buttonStop.setEnabled(false);
        buttonStop.setOnClickListener(new View.OnClickListener() {
         
            public void onClick(View arg0) {
                if (mServiceBinder != null) {
                    try {
                        mServiceBinder.stopCallerAll(TAG);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        
        //音乐播放模块
		
        
		intent_a = new Intent("snda.in.ttsdemo.music"); 
        try{
        	Log.i(TAG, "bp5.1");
        	bindService(intent_a,conn,Service.BIND_AUTO_CREATE);
        	Log.i(TAG, "bp5.2");
        }
        catch(Exception e){
        	
        }
        
        Button scan_button = (Button)findViewById(R.id.scan_button);
        scan_button.setOnClickListener(scan_sdcard_listener);
        Button stop_playing_music = (Button)findViewById(R.id.stop_playing_music);
        stop_playing_music.setOnClickListener(stop_playing_music_listener);
        Button start_playing_music = (Button)findViewById(R.id.start_playing_music);
        start_playing_music.setOnClickListener(start_playing_music_listener);
		Button next_music = (Button)findViewById(R.id.next_music);
		next_music.setOnClickListener(next_muisc_listener);
		Button pause_music = (Button)findViewById(R.id.pause_playing_music);
		pause_music.setOnClickListener(pause_muisc_listener);
		Button res_music = (Button)findViewById(R.id.res_playing_music);
		res_music.setOnClickListener(res_muisc_listener);

        m_ListView = new ListView(this);  
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(   
                LinearLayout.LayoutParams.FILL_PARENT,   
                LinearLayout.LayoutParams.WRAP_CONTENT);   
        m_ListView.setBackgroundColor(Color.RED);    
        main.addView(m_ListView, param);
        main.setLongClickable(true);   
        main.setOnTouchListener(this); 
        //查询媒体数据库
         getMusic();
       //System.out.println("cur_a.getCount = "+cur_a.getCount());
         
        ListAdapter adapter = new SimpleCursorAdapter(
        		this,   
                android.R.layout.simple_expandable_list_item_2, 
                cur,   
                new String[] { MediaStore.Audio.Media.TITLE,   
                        MediaStore.Audio.Media.ARTIST }, new int[] {   
                        android.R.id.text1, android.R.id.text2 }
        		);   
        m_ListView.setAdapter(adapter);   
        m_ListView.setOnItemClickListener(clictlistener); 
        mGestureDetector = new GestureDetector(this);   
        m_ListView.setLongClickable(true);   
        m_ListView.setOnTouchListener(this);   

	      //from scansdcard function,to fix second time press scanbutton broadcast twice problem
          //扫描sd卡模块
	      intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
	      intentfilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
	      intentfilter.addDataScheme("file");		      
	      ScanSdReceiver scanSdReceiver=new ScanSdReceiver();
	      registerReceiver(scanSdReceiver, intentfilter);
	      
	      
    }//onCreate


    
   @Override
    public void onPause(){
    	mSensorManager.unregisterListener(this);
    	super.onPause();
    	//mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    	
    }
    
   /*
    @Override
    public void onResume() {
    	//mSensorManager.unregisterListener(this);
        super.onResume();
       // mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }
    */


    
    @Override
    public void onStop() {
    	if (mServiceBinder != null) {
            try {
                mServiceBinder.stopCallerAll(TAG);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mServiceBinder != null) {
            try {
                mServiceBinder.stopCallerAll(TAG);
                unbindService(mConnection);
            } catch (RemoteException e) {
                Log.i(TAG, e.toString());
            }
        }

        Editor editor = mPrefer.edit();
        editor.putBoolean(SPEAK_READY, false);
        editor.commit();
        unregisterReceiver(mBR);
    }

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder service) {
            mServiceBinder = ITtsService.Stub.asInterface(service);
            try {
                mServiceBinder.activate();
            } catch (RemoteException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            Log.i(TAG, "bind service");
        }

      
        public void onServiceDisconnected(ComponentName name) {
            mServiceBinder = null;
            Editor editor = mPrefer.edit();
            editor.putBoolean(SPEAK_READY, false);
            editor.commit();
            Log.i(TAG, "unbind service");
        }
    };

    private BroadcastReceiver mBR = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "on Reciver");
            if (SPEAK_STATUS.equals(intent.getAction())) {
                Bundle b = intent.getExtras();
                String type = b.getString("type");
                if (type.equals(SndaTts.STATUS_SPEAK_BEGIN)) {
                    buttonPlay.setEnabled(false);
                    buttonStop.setEnabled(true);
                    Log.i(TAG, SndaTts.STATUS_SPEAK_BEGIN);
                } else if (type.equals(SndaTts.STATUS_SPEAK_FINISH)) {
                    buttonPlay.setEnabled(true);
                    buttonStop.setEnabled(false);
                    binder.resmusic(intent_a);
                    Log.i(TAG, SndaTts.STATUS_SPEAK_FINISH);
                } else if (type.equals(SndaTts.STATUS_STOPPED_MANUALLY)) {
                    buttonPlay.setEnabled(true);
                    buttonStop.setEnabled(false);
                    binder.resmusic(intent_a);
                    Log.i(TAG, SndaTts.STATUS_STOPPED_MANUALLY);
                } else if (type.equals(SndaTts.STATUS_STOPPED_BY_PHONE)) {
                    buttonPlay.setEnabled(true);
                    buttonStop.setEnabled(false);
                    binder.resmusic(intent_a);
                    Log.i(TAG, SndaTts.STATUS_STOPPED_BY_PHONE);
                }
            }
        }
    };
    
    
    //
    
    private OnClickListener start_playing_listener = new OnClickListener(){
		public void onClick(View v) {
			 binder.getMusicList(intent_a);
 			try{
 				binder.stopmusic(intent_a);	
 			}
 		    catch(Exception e)
 		    {
 		    }
 			binder.playmusic(intent_a);

			StartEngin();		   
		}
    };
    
    
    private OnClickListener stop_playing_listener = new OnClickListener(){
		public void onClick(View v) {
			StopEngin();
		
		}
    };
   
    
    private OnClickListener reset_shake_listener = new OnClickListener(){
		public void onClick(View v) {
			ResetShake();		
		}
    };
    
    private OnClickListener start_speak_listener = new OnClickListener(){
		public void onClick(View v) {
			StartSpeak(editText1);		   
		}
    };
    
    
    public void StartSpeak(EditText editText)
    {
    	mTtsTask.content = editText.getText().toString();
            if (mServiceBinder != null) {
                if (mPrefer.getBoolean(SPEAK_READY, false)) {
                    Log.i(TAG, "speak ready");
                    try {
                        mServiceBinder.speak(mTtsTask);
                    } catch (RemoteException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            } else {
                int curVersion = mManager.getInstalledVersion(TTSSERVICE, false);
                Log.i(TAG, curVersion + "");
                if (curVersion >= MIN_TTS_VERSION) {
                    Log.i(TAG, "ttsservice is installed, bind service");
                    if (mManager.appIsStart(TTSSERVICE)) {
                        Editor editor = mPrefer.edit();
                        editor.putBoolean(SPEAK_READY, true);
                        editor.commit();
                    }
                    mDemo.bindService(new Intent(TTSSERVICE_BINDNAME), mConnection,
                            Context.BIND_AUTO_CREATE);

                } else {
                    // TODO: Pls download TTS package here.
                }

            }
        }
    
	
	public void StopEngin()
	{
		if(D) Log.e(TAG, "+ StopEngin +");
		mSensorManager.unregisterListener(this);
	}
	public void StartEngin()
	{
		if(D) Log.e(TAG, "+ StartEngin +");
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	public void ResetShake()
	{
		if(D) Log.e(TAG, "+ ResetShake +");
		shake_threshold=Integer.valueOf(shake_value.getText().toString());
		Toast.makeText(getApplicationContext(),shake_value.getText().toString() , Toast.LENGTH_LONG).show();
	}

	public void makevibrator(float leftright,float frontback)
	{
		if(D) Log.e(TAG, "+ makevibrator +");
		//Toast.makeText(this, "加速度阀值="+shake_threshold , 5000).show();
		Toast.makeText(this, "x="+x +",y="+y , 50000).show();
		// 控制手机震动2秒
		//vibrator.vibrate(1000);

        if(frontback>0 && Math.abs(leftright)<1){
        	//vibrator.vibrate(1000);
        	binder.pausemusic(intent_a);
    		soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
    		StartSpeak(editText1);
        }
        else if(frontback<0 && Math.abs(leftright)<1){
        	//vibrator.vibrate(1000);
        	binder.pausemusic(intent_a);
    		soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
    		StartSpeak(editText2);
        }
        else if(Math.abs(leftright)>=1){
        	//vibrator.vibrate(1000);
        	binder.pausemusic(intent_a);
    		soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
    		StartSpeak(editText3);
        }
        else
        {
        	//vibrator.vibrate(1000);
        	binder.pausemusic(intent_a);
    		soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);
    		StartSpeak(editText4);
        		
        }

	}
	


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        
        public void onSensorChanged(SensorEvent e) {
        	if(D) Log.e(TAG, "+ onSensorChanged +");
            long curTime = System.currentTimeMillis();
            // detect per 100 Millis
            if ((curTime - lastUpdate) > 100) 
            {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                //这里做了简化，没有用z的数据
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                //z = Math.abs(values[SensorManager.DATA_Z]);
                float acceChangeRate = 0;
                // = Math.abs(x+y - last_x - last_y) / diffTime * 1000;  
                if(last_x != 0) acceChangeRate = Math.abs(x+y - last_x - last_y) / diffTime * 10000;
                 //这里设定2个阀值，一个是加速度的，一个是shake的间隔时间的
                if (acceChangeRate > shake_threshold && curTime - lastShakeTime > shake_spare) 
                {
                	//binder.pausemusic(intent_a);
                    lastShakeTime = curTime;
                    makevibrator(x,y);//调用shake时的处理函数, 对电台来讲就是换下一首歌                     
                }
                last_x = x;
                last_y = y;
            }
        }//onSensorChanged

        
        

        
/////////////////////////////////////////////////////////////////////////////////////////////////////////
        
         
   private ServiceConnection conn = new ServiceConnection()
    {
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			System.out.println("--Service connected--");
			binder=(Smartcar_MusicService.MyBinder) arg1;
		}
		public void onServiceDisconnected(ComponentName arg0) {
		}
    };
        
        private OnClickListener scan_sdcard_listener = new OnClickListener(){
    		public void onClick(View v) {
    				scanSdCard();

    			}
        };
        
        
        private OnClickListener start_playing_music_listener = new OnClickListener(){
    		public void onClick(View v) {
    			
    		    binder.getMusicList(intent_a);
    			try{
    				binder.stopmusic(intent_a);	
    			}
    		    catch(Exception e)
    		    {
    		    }
    			binder.playmusic(intent_a);
    		}
        };
        
        
        private OnClickListener stop_playing_music_listener = new OnClickListener(){
    		public void onClick(View v) {
    			try{
    				binder.stopmusic(intent_a);
    				
    			}
    		    catch(Exception e)
    		    {

    		    }
    		}
        };
        
        private OnClickListener next_muisc_listener = new OnClickListener(){
        	public void onClick(View v) {
        		binder.getMusicList(intent_a);
    			try{
    				binder.stopmusic(intent_a);
    			}
    		    catch(Exception e)
    		    {
    		    }
    			binder.SkipMusic(intent_a);
    		}
        };
        
        private OnClickListener pause_muisc_listener = new OnClickListener(){
        	public void onClick(View v) {
    			try{
    				binder.pausemusic(intent_a);
    				
    			}
    		    catch(Exception e)
    		    {

    		    }
    		}
        };
        
        private OnClickListener res_muisc_listener = new OnClickListener(){
        	public void onClick(View v) {
    			try{
    				binder.resmusic(intent_a);
    				
    			}
    		    catch(Exception e)
    		    {

    		    }
    		}
        };
    	

            //点击单条歌曲**，选择播放歌曲  

        private AdapterView.OnItemClickListener clictlistener = new AdapterView.OnItemClickListener() {   
            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) { 
            	binder.getMusicList(intent_a);
                binder.stopmusic(intent_a);
    			binder.MovetoMusic(intent_a,position);
    			
                }   
        };  
           
        
        public boolean onTouch(View v,MotionEvent event)
        {   
            return mGestureDetector.onTouchEvent(event);   
        }
           
        
         //滑动事件，向右活动开始播放歌曲，向左播放停止播放歌曲  
         
        
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,float velocityY) {   
      
            if (e1.getX() - e2.getX() > FLING_MIN_DISTANCE   
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {    
            	binder.stopmusic(intent_a);
            } else if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE   
                    && Math.abs(velocityX) > FLING_MIN_VELOCITY) {   
            	binder.stopmusic(intent_a);
            	binder.playmusic(intent_a);
            }    
            return false;   
        }   
        
        public boolean onDown(MotionEvent e) {   
        	return false;   
        }   
          
        public void onShowPress(MotionEvent e) {   
        }   
        
        
        public boolean onSingleTapUp(MotionEvent e) {   
            return false;   
        }   
         
        public void onLongPress(MotionEvent e) {   
        
      
        }   

         
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,   
                float distanceY) {   
            return false;   
        }   
        
        public int now() {
            Time time = new Time();
            time.setToNow();
            return time.hour;
        }
    	private void scanSdCard(){
    		System.out.println("scanSdCard");
     	    sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
    		Uri.parse("file://" + Environment.getExternalStorageDirectory().getAbsolutePath())));
    		}
    	
    	//

    	  public void getMusic(){
    	    	ArrayList<String> datas=new ArrayList<String>();
    	    	if(cur!=null)
    	    	{
    	    		cur=null;
    	    	}
    	    try{
    	    	cur = this.getContentResolver().query(   
    	            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
    	            //null, null, null, 
    	            new String[]{
    	            		MediaStore.Audio.Media.TITLE,
    	            		MediaStore.Audio.Media._ID,
    	            		MediaStore.Audio.Media.DISPLAY_NAME,
    	            		MediaStore.Audio.Media.ARTIST,
    	            		MediaStore.Audio.Media.DATA   
    	            },
    	            //"_ID="+"'3'",
    	            //"_ID=3",
    	            //"DISPLAY_NAME='moves like jagger.mp3'",
    	            //voiceindex,
    	            null,
    	            null,
    	            MediaStore.Audio.Media.DEFAULT_SORT_ORDER);   
    	    if(cur.getCount()==0){
    	        builder = new AlertDialog.Builder(this);
    	         builder.setMessage("No music found!"); 
    	         ad = builder.create();
    	         ad.show(); 

    	    
    	    }    
    	    this.startManagingCursor(cur); 
    	    }
    	    catch(Exception e)
    	    {
    	    builder = new AlertDialog.Builder(this);
    	    builder.setMessage("扫描存储卡失败"+e); 
    	    ad = builder.create();
    	    ad.show(); 
    	    }
    		cur.moveToFirst(); 

    	    do {
    	        //Log.i(TAG, "ID: " + cur.getString(idColumn) + " Title: " + cur.getString(titleColumn));
    	     datas.add(cur.getString(cur.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
    	       
    	    } while (cur.moveToNext());
    	    intent_a.putStringArrayListExtra("data2",  datas);
    	    System.out.println("cur.getCount = "+cur.getCount());
    	    System.out.println("datas.size = "+datas.size());

    	    }//get_muisc
    	    


     class ScanSdReceiver extends BroadcastReceiver {
    	  
    	private AlertDialog.Builder  builder = null;
    	private AlertDialog ad = null;
    	private int count1;
    	private int count2;
    	private int count;

        @Override
    	public void onReceive(Context context, Intent intent) {
        	System.out.println("ScanSdReceiver");
    		String action = intent.getAction();
          if (Intent.ACTION_MEDIA_SCANNER_STARTED.equals(action)){
              count1 = cur.getCount();
              System.out.println("count1:"+count1);
              builder = new AlertDialog.Builder(context);
              builder.setMessage("正在扫描存储卡...");
              ad = builder.create();
              ad.show();

          }else if(Intent.ACTION_MEDIA_SCANNER_FINISHED.equals(action)){
        	  getMusic();         
              count2 = cur.getCount();
              count = count2-count1;
              ad.cancel();
              if (count>=0){
                  Toast.makeText(context, "共找到" +  
                          count2 + "首歌曲，增加了"+count + "首歌曲", Toast.LENGTH_LONG).show();

              } else {
                  Toast.makeText(context, "共找到" +  
                          count2 + "首歌曲，减少了"+ count + "首歌曲", Toast.LENGTH_LONG).show();
                
                }  
               
            } }}

    
  
}//class SmartCar_Activity