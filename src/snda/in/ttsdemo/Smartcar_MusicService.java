package snda.in.ttsdemo;
import java.io.IOException;
import java.util.ArrayList;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.media.MediaPlayer.OnCompletionListener;

public class Smartcar_MusicService extends Service { 

	private MediaPlayer player; 
    private MyBinder binder = new MyBinder();
    ArrayList<String> datarec=new ArrayList<String>(); 
    int marknum=0;

  
	public class MyBinder extends Binder
	{

		public void flushdb()
		{
			System.out.println("flushdb");
		}
		public void getMusicList(Intent intent)
		{
			try { 				
				datarec=intent.getStringArrayListExtra("data2");			
			}
			catch(Exception e){}
		}
		public void playmusic(Intent intent)
		{
			
			try{	
				
				marknum=(int)((datarec.size()-0)*Math.random()+0);
			
				player = new MediaPlayer();
			player.setDataSource(datarec.get(marknum)); 
			player.prepare(); 
			} catch (IllegalArgumentException e) { 
			e.printStackTrace(); 
			} catch (IllegalStateException e) { 
			e.printStackTrace(); 
			} catch (IOException e) { 
			e.printStackTrace(); 
			} 

			player.start(); 
			player.setOnCompletionListener(new OnCompletionListener(){
		          
		        public void onCompletion(MediaPlayer mp) {
		           NextMusic(++marknum);  
		        }  

		    });  
		}//playing
		public void stopmusic(Intent intent)
		{
			if(player!=null)
			{
		    System.out.println("player.stop");
			player.stop();
			}
		}
		
		public void pausemusic(Intent intent)
		{
			if(player!=null)
			{
		    System.out.println("player.pause");
			player.pause();
			}
		}
		public void resmusic(Intent intent)
		{
			if(player!=null)
			{
		    System.out.println("player.pause");
			player.start();
			}
		}
		
		public void pausetensec(Intent intent)
		{
			if(player!=null)
			{
		    System.out.println("player.pausetensec");
			player.pause();
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			player.start();
			}
		}
		
		public void MovetoMusic(Intent intent,int position)
		{
			try{
				marknum=position;
				System.out.println("position = "+position);
				System.out.println("datarec.get = "+datarec.get(marknum));
				player = new MediaPlayer();
				player.setDataSource(datarec.get(marknum)); 
				player.prepare(); 
				player.start(); 
				player.setOnCompletionListener(new OnCompletionListener(){
			          
			        public void onCompletion(MediaPlayer mp) {

			           NextMusic(++marknum);  
			        }  

			    });  
			}
		    catch(Exception e)
		    {

		    }
		}
		
		public void SkipMusic(Intent intent)
		{
			try{
				if(marknum==(datarec.size()-1))
				{
					marknum=-1;
				}
				player = new MediaPlayer();
				player.setDataSource(datarec.get(++marknum)); 
				player.prepare(); 
				player.start(); 
				player.setOnCompletionListener(new OnCompletionListener(){
			          
			        public void onCompletion(MediaPlayer mp) {
			           NextMusic(++marknum);  
			        }  

			    });  
			}
		    catch(Exception e)
		    {

		    }
		}
		
		public void NextMusic(int i)
		{
			try{
				
				player = new MediaPlayer();
				player.setDataSource(datarec.get(i)); 
				player.prepare(); 
				player.start(); 
				player.setOnCompletionListener(new OnCompletionListener(){
			          
			        public void onCompletion(MediaPlayer mp) {
			        marknum=(int)((datarec.size()-0)*Math.random()+0);
			        //NextMusic(++marknum);  
			        NextMusic(marknum);  
			        }  

			    });  
			}
		    catch(Exception e)
		    {

		    }

		}

	}//class MyBinder
	
    
	public IBinder onBind(Intent intent) { 
    System.out.println("Service is Binded");
	return binder;
	} 
	
	public void onCreate()
	{
		super.onCreate();
		System.out.println("Service is Created");
	
	}
	
	public boolean onUnbind(Intent intent)
	{
		System.out.println("Service is Unbinded");
		return true;
	}
	public void onDestroy()
	{
		if(player!=null)
		{
	    System.out.println("player.stop");
		player.stop();
		}
		super.onDestroy();
		System.out.println("Service is Destroyed");
	}



	} 