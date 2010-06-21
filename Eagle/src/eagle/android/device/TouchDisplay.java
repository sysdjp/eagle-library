/**
 *
 * @author eagle.sakura
 * @version 2009/11/19 : 新規作成
 */
package eagle.android.device;

import eagle.util.EagleUtil;
import android.graphics.Point;
import android.view.MotionEvent;

/**
 * @author eagle.sakura
 * @version 2009/11/19 : 新規作成
 */
public class TouchDisplay
{
	/**
	 * 属性情報。
	 */
	private	int			attribute	=	0x0;
	/**
	 * 前フレームの属性情報。
	 */
	private	int			attrOld		=	0x0;
	/**
	 * 現フレームの属性情報。
	 */
	private	int			attrNow		=	0x0;
	/**
	 * タッチしていた時間。
	 */
	private	int			touchTimeMs		=	0;
	/**
	 * タッチ開始した時間。
	 */
	private	long		touchStartTime	=	0;
	/**
	 * タッチした座標。
	 */
	private	Point		touchPos	=	new	Point();
	/**
	 * 離した位置。
	 */
	private	Point		releasePos	=	new	Point();

	/**
	 * ディスプレイに触れている。
	 */
	public	static	final	int		eAttrTouch	=	1 << 0;

	/**
	 * ディスプレイマネージャ。<BR>
	 * 現時点ではシングルタッチのみ対応している。
	 * @author eagle.sakura
	 * @version 2009/11/19 : 新規作成
	 */
	public	TouchDisplay( )
	{

	}

	/**
	 * タッチ系のイベントが発生した。
	 * @author eagle.sakura
	 * @param me
	 * @version 2009/11/19 : 新規作成
	 */
	public	boolean	onTouchEvent( MotionEvent me )
	{
		//!	ディスプレイが押された。
		if( me.getAction() == MotionEvent.ACTION_DOWN )
		{
			touchPos.x = ( int )me.getX( );
			touchPos.y = ( int )me.getY( );

			attribute = EagleUtil.setFlag( attribute, eAttrTouch, true );
			touchStartTime = System.currentTimeMillis();
		}

		//!	動いたか離された。
		if( me.getAction() == MotionEvent.ACTION_OUTSIDE
		||	me.getAction() == MotionEvent.ACTION_MOVE
		||	me.getAction() == MotionEvent.ACTION_UP	)
		{
			releasePos.x = ( int )me.getX();
			releasePos.y = ( int )me.getY();

			touchTimeMs = ( int )( System.currentTimeMillis() - touchStartTime );
			if( me.getAction() != MotionEvent.ACTION_MOVE )
			{
				attribute = EagleUtil.setFlag( attribute, eAttrTouch, false );
			}
		}

		return	true;
	}

	/**
	 * ドラッグされた距離を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/19 : 新規作成
	 */
	public	int		getDrugVectorX( )
	{
		return	releasePos.x - touchPos.x;
	}

	/**
	 * 画面に触れた位置を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/12/06 : 新規作成
	 */
	public	int		getTouchPosX( )
	{
		return	touchPos.x;
	}

	/**
	 * 画面に触れた位置を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/12/06 : 新規作成
	 */
	public	int		getTouchPosY( )
	{
		return	touchPos.y;
	}

	/**
	 * ドラッグされた距離を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/19 : 新規作成
	 */
	public	int		getDrugVectorY( )
	{
		return	releasePos.y - touchPos.y;
	}

	/**
	 * タッチされているかを調べる。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/19 : 新規作成
	 */
	public	boolean	isTouch( )
	{
		return	EagleUtil.isFlagOn( attrNow, eAttrTouch );
	}

	/**
	 * ディスプレイから指が離れているか。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/21 : 新規作成
	 */
	public	boolean	isRelease( )
	{
		return	!EagleUtil.isFlagOn( attrNow, eAttrTouch );
	}

	/**
	 * ディスプレイから指が離れた瞬間か。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/21 : 新規作成
	 */
	public	boolean	isReleaseOnce( )
	{
		if( !EagleUtil.isFlagOn( attrNow, eAttrTouch )
		&&	EagleUtil.isFlagOn( attrOld, eAttrTouch ) )
		{
			return	true;
		}
		return	false;
	}

	/**
	 * タッチされているかを調べる。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/19 : 新規作成
	 */
	public	boolean	isTouchOnce( )
	{
		if( EagleUtil.isFlagOn( attrNow, eAttrTouch )
		&&	!EagleUtil.isFlagOn( attrOld, eAttrTouch ) )
		{
			return	true;
		}
		return	false;
	}

	/**
	 * 毎フレームの更新を行う。
	 * @author eagle.sakura
	 * @version 2009/11/19 : 新規作成
	 */
	public	void	update( )
	{
		attrOld =	attrNow;
		attrNow	=	attribute;
	}
}
