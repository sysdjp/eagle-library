/**
 * 4x4行列を管理する。
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
package eagle.math;

import eagle.util.ObjectPool;
import android.opengl.Matrix;

/**
 * 4x4の行列を管理する。
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
public	final class Matrix4x4	extends	BaseMatrix
{
	private	static	float[]	temp = new float[ 4*4 ];
	/**
	 * 単位行列を作成する。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	Matrix4x4( )
	{
	}

	/**
	 * 回転行列を作成する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @version 2009/11/15 : 新規作成
	 */
	@Override
	public	void	rotate( float x, float y, float z, float w )
	{
		Matrix.rotateM( m, 0, w, x, y, z );
	}

	/**
	 * 位置行列を作成する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/15 : 新規作成
	 */
	@Override
	public	void	translate( float x, float y, float z )
	{
		m[ 4*3 + 0 ] += x;
		m[ 4*3 + 1 ] += y;
		m[ 4*3 + 2 ] += z;

		/*
		m[ 4*0 + 3 ] += x;
		m[ 4*1 + 3 ] += y;
		m[ 4*2 + 3 ] += z;
		*/
	}

	/**
	 * 逆行列を作成する。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	invert( )
	{
		//	float[]	f = new float[ 4*4 ];
		Matrix.invertM( temp, 0, m, 0 );

		//!	テンポラリ用と入れ替える。
		float[]	mm = m;
		m = temp;
		temp = mm;
	}

	/**
	 * 逆行列を作成する。
	 * @author eagle.sakura
	 * @param result
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	invert( BaseMatrix result )
	{
		Matrix.invertM( result.m, 0, m, 0 );
	}

	/**
	 * this = this * transの計算を行う。
	 * @author eagle.sakura
	 * @param trans
	 * @version 2009/11/15 : 新規作成
	 */
	@Override
	public	void	multiply(	BaseMatrix	trans )
	{
		Matrix.multiplyMM( temp, 0, trans.m, 0, m, 0 );

		//!	テンポラリ用と入れ替える。
		float[]	mm = m;
		m = temp;
		temp = mm;
	}

	/**
	 * この行列を適用したベクトルをresultへ格納する。
	 * @author eagle.sakura
	 * @param v
	 * @param result
	 * @return	resultの参照
	 * @version 2009/11/29 : 新規作成
	 */
	@Override
	public	Vector3	transVector(	Vector3	v,
									Vector3	result	)
	{
		result.set(
						( m[ 4*0 + 0 ] * v.x )
					+	( m[ 4*1 + 0 ] * v.y )
					+	( m[ 4*2 + 0 ] * v.z )
					+	  m[ 4*3 + 0 ],

						( m[ 4*0 + 1 ] * v.x )
					+	( m[ 4*1 + 1 ] * v.y )
					+	( m[ 4*2 + 1 ] * v.z )
					+	  m[ 4*3 + 1 ],

						( m[ 4*0 + 2 ] * v.x )
					+	( m[ 4*1 + 2 ] * v.y )
					+	( m[ 4*2 + 2 ] * v.z )
					+	  m[ 4*3 + 2 ]
					);

		return	result;
	}

	private	static	Matrix4x4	tempTrans = new Matrix4x4();
	/**
	 * 描画用変換行列を作成する。<BR>
	 * 適用は<BR>
	 * scale -> rotateX -> rotateY -> rotateZ -> position<BR>
	 * となる。
	 * @author eagle.sakura
	 * @param scale
	 * @param rotate
	 * @param position
	 * @param result
	 * @return	resultの参照
	 * @version 2009/11/23 : 新規作成
	 */
	public	static	Matrix4x4	create( Vector3 scale, Vector3 rotate, Vector3 position, Matrix4x4 result )
	{
		result.identity();

		if( scale != null
		&&	( 	scale.x != 1.0f
			||	scale.y != 1.0f
			||	scale.z != 1.0f )
		)
		{
			result.scale( scale.x, scale.y, scale.z );
		}

		if( rotate != null )
		{
			//!	x
			if( rotate.x != 0.0f )
			{
				tempTrans.identity();
				tempTrans.rotate( 1.0f, 0.0f, 0.0f, rotate.x );
				result.multiply( tempTrans );
			}

			//!	y
			if( rotate.y != 0.0f )
			{
				tempTrans.identity();
				tempTrans.rotate( 0.0f, 1.0f, 0.0f, rotate.y );
				result.multiply( tempTrans );
			}

			//!	z
			if( rotate.z != 0.0f )
			{
				tempTrans.identity();
				tempTrans.rotate( 0.0f, 0.0f, 1.0f, rotate.z );
				result.multiply( tempTrans );
			}
		}

		if( position != null )
		{
			tempTrans.identity();
			tempTrans.translate( position.x, position.y, position.z );

			result.multiply( tempTrans );
		}
		return	result;
	}


	/**
	 * result = this * transの計算を行う。
	 * @author eagle.sakura
	 * @param trans
	 * @param result
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	Matrix4x4	multiply( Matrix4x4 trans, Matrix4x4 result )
	{
		Matrix.multiplyMM( result.m, 0, trans.m, 0, m, 0 );

		return	result;
	}


	private	static	Matrix4x4Pool		pool = new Matrix4x4Pool();

	/**
	 * テンポラリを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	static	Matrix4x4		getTemp( )
	{
		return	( Matrix4x4 )pool.pop();
	}

	/**
	 * テンポラリを返す。
	 * @author eagle.sakura
	 * @param temp
	 * @version 2009/11/29 : 新規作成
	 */
	public	static	void				releaseTemp( Matrix4x4 temp )
	{
		pool.push( temp );
	}

	/**
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public	static class Matrix4x4Pool	extends	ObjectPool
	{
		public	Matrix4x4Pool( )
		{

		}
		public	@Override	Object	createInstance( )
		{
			return	new	Matrix4x4( );
		}
	}
}
