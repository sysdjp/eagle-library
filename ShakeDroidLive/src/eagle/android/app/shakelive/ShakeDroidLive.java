/**
 *
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
package eagle.android.app.shakelive;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;

import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.appcore.shake.ShakeInitialize;
import eagle.android.appcore.shake.ShakeLooper;
import eagle.android.gles11.GLManager;
import eagle.android.gles11.IndexBufferSW;
import eagle.android.gles11.VertexBufferSW;
import eagle.android.thread.ILooper;
import eagle.android.thread.LooperThread;
import eagle.android.util.UtilActivity;
import eagle.android.util.UtilBridgeAndroid;
import eagle.io.DataInputStream;
import eagle.io.InputStreamBufferReader;
import eagle.util.EagleUtil;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.PixelFormat;
import android.service.wallpaper.WallpaperService;
import android.view.Display;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.WindowManager;
import android.view.SurfaceHolder.Callback;

/**
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
public class ShakeDroidLive extends WallpaperService
{
	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	@Override
	public Engine onCreateEngine()
	{
		EagleUtil.init( new UtilBridgeAndroid( "ShakeWall" ) );
		// TODO 自動生成されたメソッド・スタブ
		return new	WallPaperEngine(  );
	}

	@Override
	public void onLowMemory()
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onLowMemory();
		EagleUtil.log( "onLowMemory" );
	}

	@Override
	public void onStart(Intent intent, int startId)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onStart(intent, startId);
		EagleUtil.log( "onStart" );
	}

	@Override
	public void onRebind(Intent intent)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onRebind(intent);
		EagleUtil.log( "onRebind" );
	}

	@Override
	public boolean onUnbind(Intent intent)
	{
		EagleUtil.log( "onUnbind" );
		// TODO 自動生成されたメソッド・スタブ
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy()
	{
		EagleUtil.log( "onDestroy" );
		// TODO 自動生成されたメソッド・スタブ
		super.onDestroy();
	}

	/**
	 * 壁紙用の処理。
	 * @author eagle.sakura
	 * @version 2010/05/31 : 新規作成
	 */
	public	class	WallPaperEngine	extends			Engine
									implements		OnSharedPreferenceChangeListener
	{
		private		GLManager			glManager	=	null;
		private		LooperThread		thread		=	null;
		private		ILooper				looper		=	null;
		/**
		 * データ変更の通知・検証。
		 */
		private	ShakeWallDataNotify			dataNotify	=	null;

		/**
		 *
		 * @author eagle.sakura
		 * @param service
		 * @version 2010/05/31 : 新規作成
		 */
		public	WallPaperEngine(  )
		{
			dataNotify = new	ShakeWallDataNotify( ShakeDroidLive.this, this );

		}

		/**
		 * データ変更の通知を受け取る。
		 * @author eagle.sakura
		 * @param sharedPreferences
		 * @param key
		 * @version 2010/06/12 : 新規作成
		 */
		@Override
		public void onSharedPreferenceChanged(	SharedPreferences sharedPreferences, String key)
		{
			// TODO 自動生成されたメソッド・スタブ
			EagleUtil.log( "WallPaperEngine" );
			EagleUtil.log( "key : " + key );


			//!	通知の種類を確認する
			if( dataNotify.isNotifyVerticalFile( sharedPreferences, key )
			&&	UtilActivity.isOrientationVertical( ShakeDroidLive.this ) )
			{
			//!	同じ方向なら、再度壁紙を生成する。
				removeThread();
				createThread( getSurfaceHolder() );
			}
			else if( dataNotify.isNotifyHorizontalFile( sharedPreferences, key )
				&&	!UtilActivity.isOrientationVertical( ShakeDroidLive.this ) )
			{
				removeThread();
				createThread( getSurfaceHolder() );
			}
		}

		@Override
		public void onDesiredSizeChanged(int desiredWidth, int desiredHeight)
		{
			EagleUtil.log( "onDesiredSizeChanged" );
			// TODO 自動生成されたメソッド・スタブ
			super.onDesiredSizeChanged(desiredWidth, desiredHeight);
		}

		/**
		 * タッチが観測された。
		 * @author eagle.sakura
		 * @param event
		 * @version 2010/05/31 : 新規作成
		 */
		@Override
		public void onTouchEvent(MotionEvent event)
		{
			// TODO 自動生成されたメソッド・スタブ
			if( looper != null
			&&	looper.getTouchDisplay() != null )
			{
				looper.getTouchDisplay().onTouchEvent( event );
			}
			else
			{
				super.onTouchEvent(event);
			}
		}

		/**
		 * 作成された。
		 * @author eagle.sakura
		 * @param surfaceHolder
		 * @version 2010/05/31 : 新規作成
		 */
		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			EagleUtil.log( "onCreate" );
			// TODO 自動生成されたメソッド・スタブ
			super.onCreate(surfaceHolder);
			getSurfaceHolder().setFormat( PixelFormat.RGB_565 );
			getSurfaceHolder().setType( SurfaceHolder.SURFACE_TYPE_GPU );
			//!	サーフェイスサイズの指定。
			/*
			WindowManager	wm =	( WindowManager )getSystemService( Context.WINDOW_SERVICE );
			glManager.setSurfaceSize(	wm.getDefaultDisplay().getWidth(),
										wm.getDefaultDisplay().getHeight() );
										*/
			//!	タッチイベントを有効化する。
			setTouchEventsEnabled( true );
		}

		/**
		 *
		 * @author eagle.sakura
		 * @param holder
		 * @version 2010/05/31 : 新規作成
		 */
		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			EagleUtil.log( "onSurfaceCreated" );
			// TODO 自動生成されたメソッド・スタブ
			super.onSurfaceCreated(holder);

			createThread( holder );
		}

		/**
		 *
		 * @author eagle.sakura
		 * @version 2010/05/31 : 新規作成
		 */
		public	void	createThread( SurfaceHolder holder )
		{
			EagleUtil.log( "Initialize" );
			try
			{
				glManager = new GLManager(  );

				//!	OGLの初期化。
				WindowManager	wm =	( WindowManager )getSystemService( Context.WINDOW_SERVICE );
				int	displayW	= wm.getDefaultDisplay().getWidth(),
					displayH	= wm.getDefaultDisplay().getHeight();
				glManager.setSurfaceSize(	displayW,
											displayH );
				glManager.setSurfaceHolder( holder );

				/**/
				byte[]	buffer	=	null;
				String	fileName = "";

				boolean	isVertical = false;
				if( displayH > displayW )
				{
					isVertical = true;
				}
				else
				{
					isVertical = false;
				}

				EagleUtil.log( "Load Vertical : " + isVertical );

				if( isVertical )
				{
					fileName =	ShakeWallDataNotify.eShakeDataFileNameVertical;
				}
				else
				{
					fileName = ShakeWallDataNotify.eShakeDataFileNameHorizontal;
				}
				InputStream		fis = dataNotify.createInputStream( fileName );
				DataInputStream	dis = new	DataInputStream( new InputStreamBufferReader( fis ) );
				buffer =	dis.readBuffer( fis.available() );
				dis.dispose();

				if( buffer != null )
				{
					EagleUtil.log( "LooperThread" );

					ShakeDataFile	sdf = new ShakeDataFile( buffer );
					sdf.deserialize();
					ShakeLooper	shakeLooper = sdf.createLooper( ShakeDroidLive.this, glManager );
					shakeLooper.setWeightLock( true );	//<!	頂点ウェイトをロックする。
					looper = shakeLooper;

					thread	= new LooperThread( looper );
					thread.start();
				}

			}
			catch( Exception e )
			{
				EagleUtil.log( e );
			}

		}

		/**
		 *
		 * @author eagle.sakura
		 * @version 2010/05/31 : 新規作成
		 */
		public	void	removeThread( )
		{
			if( thread != null )
			{
				thread.dispose();
				thread = null;
			}


			looper = null;
			glManager = null;
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			// TODO 自動生成されたメソッド・スタブ
			super.onVisibilityChanged(visible);

			EagleUtil.log( "Visible : " + visible );

			if( thread == null )
			{
				return;
			}

			/*
			*/
			if( visible )
			{
				thread.setSleeping( false );
			}
			else
			{
				thread.setSleeping( true );
			}
		}

		/**
		 *
		 * @author eagle.sakura
		 * @param holder
		 * @version 2010/05/31 : 新規作成
		 */
		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			EagleUtil.log( "onSurfaceDestroy" );
			// TODO 自動生成されたメソッド・スタブ
			super.onSurfaceDestroyed(holder);
			removeThread();
		}

		/**
		 *
		 * @author eagle.sakura
		 * @version 2010/05/31 : 新規作成
		 */
		@Override
		public void onDestroy()
		{
			EagleUtil.log( "onDestroy" );
			// TODO 自動生成されたメソッド・スタブ
			super.onDestroy();
			dataNotify.dispose();
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			// TODO 自動生成されたメソッド・スタブ
			super.onSurfaceChanged(holder, format, width, height);

			EagleUtil.log( "onSurfaceChanged : " + format + " : " + width + " x " + height );

			if( thread == null )
			{
				createThread( holder );
				return;
			}

			if( width != glManager.getDisplayWidth() )
			{
				removeThread();
				createThread( holder );
			}
		}
	}
}
