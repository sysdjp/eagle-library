/**
 *
 * @author eagle.sakura
 * @version 2009/12/11 : 新規作成
 */
package eagle.android.graphic;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.PorterDuff.Mode;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * @author eagle.sakura
 * @version 2009/12/11 : 新規作成
 */
public class SurfaceCanvas	extends	Graphics
{
	private	SurfaceHolder	holder	= null;
	/**
	 * @author eagle.sakura
	 * @param target
	 * @version 2009/11/29 : 新規作成
	 */
	public	SurfaceCanvas( SurfaceHolder holder )
	{
		this.holder	= holder;
	}

	/**
	 * 描画の開始を明示する。
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public	boolean		lock( )
	{
		setCanvas( holder.lockCanvas() );

		return	getCanvas() != null;
	}

	/**
	 * サーフェイス情報をフロントバッファに転送する。
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public	void		unlock( )
	{
		holder.unlockCanvasAndPost( getCanvas() );
		setCanvas( null );
	}
}
