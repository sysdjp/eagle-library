/**
 *
 * @author eagle.sakura
 * @version 2010/05/21 : 新規作成
 */
package eagle.android.appcore.shake;

import android.util.Log;
import eagle.android.device.TouchDisplay;
import eagle.math.Vector3;

/**
 * @author eagle.sakura
 * @version 2010/05/21 : 新規作成
 */
public class ShakeControllerSlide extends IShakeController
{
	/**
	 *
	 */
	private	int		frame	=	0;
	/**
	 *
	 */
	private	float	weight	=	0.0f;

	/**
	 * 指で揺らしたベクトル。
	 */
	private	Vector3	slideVector	=	new	Vector3( );

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/05/21 : 新規作成
	 */
	public	ShakeControllerSlide( )
	{
	}

	@Override
	public int getShakeType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return Option.eShakeTypeSlide;
	}

	@Override
	public boolean isRotateEnable()
	{
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param option
	 * @return
	 * @version 2010/05/21 : 新規作成
	 */
	@Override
	public boolean isLockVertexWeight(Option option)
	{
		if( option.isBlueMode(	) )
		{
			return	false;
		}
		else
		{
			return	true;
		}
	}

	/**
	 * @author eagle.sakura
	 * @param mt
	 * @version 2010/05/21 : 新規作成
	 */
	@Override
	public void update(ShakeLooper mt)
	{
	// TODO 自動生成されたメソッド・スタブ
		TouchDisplay	touch = mt.getTouchDisplay();

		if( touch.isReleaseOnce( ) )
		{
			Log.d( "ShakeDroid", "離された" );
			weight	= 1.0f;
			frame	= 0;

			//!	移動ベクトル設定
			slideVector.set( touch.getDrugVectorX(), -touch.getDrugVectorY(), 0 );
			slideVector.normalize();
		}


		float	sin	=	( float )Math.sin( ( double )( frame ) );
		getShakeVector().x = slideVector.x * weight * sin;
		getShakeVector().y = slideVector.y * weight * sin;


		//!	フレーム情報更新
		++frame;
		weight	*=	mt.getOption().getNormalizedWeightMul( );
	}

}
