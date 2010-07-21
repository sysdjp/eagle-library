/**
 *
 * @author eagle.sakura
 * @version 2010/05/25 : 新規作成
 */
package eagle.android.thread;

import java.util.ArrayList;
import java.util.List;

import android.view.MotionEvent;
import android.view.SurfaceHolder;
import eagle.android.device.TouchDisplay;
import eagle.android.gles11.GLManager;
import eagle.android.view.ILooperSurface;
import eagle.android.view.LooperSurfaceView;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/05/25 : 新規作成
 */
public class LooperThread	extends	Thread
{
	/**
	 * 終了待ちフラグ。
	 */
	private	boolean						done		= false;

	/**
	 * スレッドのループを一時停止する。
	 */
	private	boolean						sleeping	= false;

	/**
	 * 管理するビュー。
	 */
	private	List< ILooperSurface >		viewList	=	new	ArrayList();

	/**
	 * メインループクラス。
	 */
	private	ILooper						looper		=	null;


	/**
	 *
	 * @author eagle.sakura
	 * @param looper
	 * @version 2010/05/31 : 新規作成
	 */
	public	LooperThread( ILooper looper )
	{
		setLooper( looper );
	}

	/**
	 * サーフェイスを追加する。
	 * @author eagle.sakura
	 * @param surface
	 * @version 2010/05/25 : 新規作成
	 */
	public	void		addSurface( LooperSurfaceView surface )
	{
		if( surface == null )
		{
			return;
		}

		if( viewList.size() == 0 )
		{
			surface.setTouchDisplay( looper.getTouchDisplay( ) );
		}

		viewList.add( surface );	//!<	サーフェイスを登録する。
	}

	/**
	 * 全サーフェイスの作成が完了したらtrueを返す。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	public	boolean		isCreateComplete( )
	{
		for( ILooperSurface view : viewList )
		{
			//!	未作成のクラスがあった。
			if( ! view.isCreated() )
			{
				return	false;
			}
		}

		return		true;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param looper
	 * @version 2010/05/31 : 新規作成
	 */
	public	void		setLooper( ILooper looper )
	{
		this.looper = looper;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public	ILooper		getLooper( )
	{
		return	looper;
	}


	/**
	 * ループの開始時に呼ばれる。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	onLoopBegin( )
	{
		if( looper != null )
		{
			looper.onInitialize();
		}
	}

	/**
	 * スレッドの休止設定を行う。
	 * @author eagle.sakura
	 * @param is
	 * @version 2010/06/11 : 新規作成
	 */
	public	void	setSleeping( boolean is )
	{
		sleeping = is;
	}

	/**
	 * スレッドのループ休止命令が与えられている場合、trueを返す。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/11 : 新規作成
	 */
	public	boolean	isSleeping( )
	{
		return	sleeping;
	}

	/**
	 * 毎フレームのループを行う。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	onLoop( )
	{
		if( looper != null
		&&	!isSleeping() )
		{
			looper.onLoop();
		}
		else
		{
			try
			{
				sleep( 1000 );
			}
			catch( Exception e )
			{

			}
		//	done = true;
		}
	}

	/**
	 * 終了処理を行う。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	onFinalize( )
	{
		if( looper != null )
		{
			looper.onFinalize();
			viewList.clear();
			looper = null;
		}
	}

	/**
	 * サーフェイスの作成が完了するまで待つ。
	 * @author eagle.sakura
	 * @version 2010/05/25 : 新規作成
	 */
	protected	void		waitSurfaceCreated( )
	{
		try
		{
			long	current = System.currentTimeMillis();
			for( ILooperSurface surface : viewList )
			{
				while( !surface.isCreated( ) )
				{
					//!	適当な時間待つ。
					sleep( 5 );

					long	time = System.currentTimeMillis() - current;
					if( done
					||	time > ( 1000 * 5) )
					{
						done = true;
						EagleUtil.log( "thread done" );
						return;
					}
				}
			}
			//!	適当な時間待つ。
			sleep( 15 );
		}
		catch( Exception e )
		{
			EagleUtil.log( e );
		}
	}

	/**
	 * スレッド処理を行う。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	run( )
	{
		EagleUtil.log( "wait create..." );
		waitSurfaceCreated();
		EagleUtil.log( "wait create complete" );
		if( done )
		{
			return;
		}

		onLoopBegin( );
		//!	終了要求まで繰り返す。
		while( !done )
		{
			onLoop( );
		}
		onFinalize( );
	}

	/**
	 * スレッド終了要求。<BR>
	 * スレッドに終了要求を出して、停止するのを待つ。
	 */
	public void dispose( )
	{
		synchronized (this)
		{
			//終了要求を出す
			done = true;
		}

		try{
			//スレッド終了を待つ
			join(	);
		}
		catch( InterruptedException ex )
		{
			Thread.currentThread().interrupt();
		}
	}

}
