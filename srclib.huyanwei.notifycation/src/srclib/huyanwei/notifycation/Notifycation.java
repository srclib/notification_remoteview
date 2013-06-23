package srclib.huyanwei.notifycation;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RemoteViews;
import android.support.v4.app.NavUtils;

public class Notifycation extends Activity {

	private String TAG = "srclib.huyanwei.notification"; 
	
	private Button btn_gen ; 
	private Button btn_clear ;	
	private Button btn_update ;	
	
	private final int NOTIFY_ID = 1;
	
	private Intent mClickIntent;
	private PendingIntent mPendingClickIntent;	

	private Intent mClearIntent;
	private PendingIntent mPendingClearIntent;		
	
	private Notification mNotification;
	private NotificationManager mNotificationManager;
	
	private final boolean m_allow_clear = false ;
	private final boolean m_allow_remoteview = true ;
	
	private RemoteViews mRemoteViews ;
	
	private int process_bar_value = 0 ;
	
	private boolean debug = false;
		
	private View.OnClickListener btn_OnClickListener = new View.OnClickListener() 
	{
		public void onClick(View arg0) {
			switch(arg0.getId())
			{
				case R.id.notify_button_gen:
					generate_notification();
					update_remote_view();
					break;
				case R.id.notify_button_clear:
					clear_notification();
					break;
				case R.id.update_notify_remote_view:
					update_remote_view();
					break;
				default:
					break;			
			}
		}		
	};
		
	Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub        
        	if(debug) 
        		Log.d(TAG,"huyanwei receive msg.arg1="+msg.arg1);
        	mNotification.contentView.setProgressBar(R.id.progressBar1, 100, msg.arg1,
                    false);
        	mNotificationManager.notify(NOTIFY_ID, mNotification);
        	/*	
            if (msg.arg1 >= 100) {
            	mNotificationManager.cancel(NOTIFY_ID); // ��ɺ���ʧ.
            }
            */
            super.handleMessage(msg);
        }
    };
	
	private void generate_notification()
	{
		mClickIntent = new Intent(Intent.ACTION_VIEW);
		mClickIntent.setData(Uri.parse("http://www.baidu.com"));
		mClickIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);		
        //��Ҫ�����õ��֪ͨʱ��ʾ���ݵ���
        mPendingClickIntent = PendingIntent.getActivity(this, 0, mClickIntent, 0);
	        
        mClearIntent = new Intent(Intent.ACTION_VIEW);
        mClearIntent.setData(Uri.parse("http://www.google.com"));
        mClearIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);     
        //��Ҫ�������������֪ͨʱ��ʾ���ݵ���		
        PendingIntent mPendingClearIntent = PendingIntent.getActivity(this, 0, mClearIntent, 0);
     
		mNotification = new Notification();
		
		//����֪ͨ��״̬����ʾ��ͼ��
        mNotification.when = System.currentTimeMillis(); 
        
		mNotification.icon = R.drawable.notify;
		
		mNotification.tickerText = this.getString(R.string.notification_brief);
		
		/*
		mNotification.defaults =  Notification.DEFAULT_SOUND 		    //֪ͨʱĬ�ϵ�����     
								| Notification.DEFAULT_VIBRATE 			//֪ͨʱ��
								| Notification.DEFAULT_LIGHTS;          //֪ͨʱ����
		*/
		
		//mNotification.flags		|= 	Notification.FLAG_ONLY_ALERT_ONCE;		
		//mNotification.flags 	|= 	Notification.FLAG_INSISTENT ;    	//֪ͨ������Ч��һֱ����
		
		if( !m_allow_clear )
		{
			mNotification.flags |= 	Notification.FLAG_NO_CLEAR; 		// Notification.FLAG_AUTO_CANCEL		
			mNotification.flags |= 	Notification.FLAG_ONGOING_EVENT;    // "���ڽ���ʱ" ���� "֪ͨ"
		}
		else
		{		
			//���������� ��ֹ ��������ԣ������delete�����޷�����
			mNotification.deleteIntent = mPendingClearIntent; // ֪ͨ�� ��systemUI -> clear all�� ���ʱ.
		}
		
        if(m_allow_remoteview)
        {
        	mRemoteViews = new RemoteViews(this.getApplication().getPackageName(), R.layout.notification_remote_views);
        	mRemoteViews.setProgressBar(R.id.progressBar1, 100, 0, false);
        	mNotification.contentView = mRemoteViews;        	
        	mNotification.contentIntent = mPendingClickIntent;
        }
        else
        {
			//����֪ͨ��ʾ�Ĳ���.
			mNotification.setLatestEventInfo(this, 
				this.getString(R.string.notification_title), 
				this.getString(R.string.notification_context),
				mPendingClickIntent);
        }
		//�������Ϊִ�����֪ͨ
		mNotificationManager.notify(NOTIFY_ID, mNotification);
	}
	
	private void clear_notification()
	{
		mNotificationManager.cancel(NOTIFY_ID);
	}
	
	
	private void update_remote_view()
	{
		// valid object check.
		if (mNotification == null )
			return ;
		
		Thread mThread = new Thread(new Runnable() {
            public void run() {   
            	process_bar_value = 0 ;
                while (true) {
                	process_bar_value += 1;
                    Message msg = mHandler.obtainMessage();
                    msg.arg1 = process_bar_value;
                    msg.sendToTarget();
                    
                    if(debug) 
                    	Log.d(TAG,"huyanwei debug process_bar_value="+process_bar_value);                    
                    
                    if(process_bar_value >= 100)
                    	break;
                    
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });
		
		mThread.start();		
	}
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.notifycation);        
        
        btn_gen = (Button)findViewById(R.id.notify_button_gen);
        btn_gen.setOnClickListener(btn_OnClickListener);        
        
        btn_clear = (Button)findViewById(R.id.notify_button_clear);
        btn_clear.setOnClickListener(btn_OnClickListener);
        
        btn_update = (Button)findViewById(R.id.update_notify_remote_view);
        btn_update.setOnClickListener(btn_OnClickListener);
        
        mNotificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notifycation, menu);
        return true;
    }

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		clear_notification(); // ������е�֪ͨ.		
		super.onDestroy();
	}

}
