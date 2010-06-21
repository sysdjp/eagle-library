/**
 *
 * @author eagle.sakura
 * @version 2010/05/22 : 新規作成
 */
package eagle.android.appcore.shake;

import android.util.Log;
import eagle.android.device.SensorDevice;
import eagle.android.device.TouchDisplay;
import eagle.math.Vector3;
import eagle.util.EagleUtil;

/**
 * 加速度センサーを使用したコントローラ。
 * @author eagle.sakura
 * @version 2010/05/22 : 新規作成
 */
public class ShakeControllerAccel extends IShakeController
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
	 * @version 2010/05/22 : 新規作成
	 */
	public	ShakeControllerAccel( )
	{

	}

	@Override
	public int getShakeType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return Option.eShakeTypeAccel;
	}

	@Override
	public boolean isRotateEnable()
	{
		// TODO 自動生成されたメソッド・スタブ
		return false;
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
		Vector3	v = new Vector3(	-mt.getSensor().getGravity( SensorDevice.eValueTypeXAccel ),
									-mt.getSensor().getGravity( SensorDevice.eValueTypeYAccel ),
									-mt.getSensor().getGravity( SensorDevice.eValueTypeZAccel )
									);

		float	length = v.length();
		if( length	> mt.getOption().getShakeSensitivity( )
		&&	( 	frame	> 15
			||	weight	> 0.5f )
			)
		{
			float	over = length - mt.getOption().getShakeSensitivity();
			slideVector.x = -mt.getSensor().getGravity( SensorDevice.eValueTypeXAccel ) * ( over * 10.0f );
			slideVector.y = -mt.getSensor().getGravity( SensorDevice.eValueTypeYAccel ) * ( over * 10.0f );

			slideVector.x = EagleUtil.minmax( -2.5f, 2.5f, slideVector.x );
			slideVector.y = EagleUtil.minmax( -2.5f, 2.5f, slideVector.y );
			weight	= 1.0f;
			frame	= 0;
		}


		float	sin	=	( float )Math.sin( ( double )( frame ) );
		getShakeVector().x = slideVector.x * weight * sin;
		getShakeVector().y = slideVector.y * weight * sin;


		//!	フレーム情報更新
		++frame;
		weight	*=	mt.getOption().getNormalizedWeightMul( );
	}

}
