/**
 * XYZの座標を管理する。
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
package eagle.math;

import eagle.util.ObjectPool;

/**
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
public	final class Vector3
{
	/**
	 * X成分。
	 */
	public	float	x	=	0.0f;
	/**
	 * Y成分。
	 */
	public	float	y	=	0.0f;
	/**
	 * Z成分。
	 */
	public	float	z	=	0.0f;

	/**
	 * ゼロ初期化したベクトルを作成する。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	Vector3( )
	{
	}

	/**
	 * 値を指定して初期化する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/14 : 新規作成
	 */
	public	Vector3( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	/**
	 * コピーを行う。
	 * @author eagle.sakura
	 * @param origin
	 * @version 2009/11/14 : 新規作成
	 */
	public	Vector3( Vector3 origin )
	{
		set( origin );
	}

	/**
	 * 値のコピーを行う。
	 * @author eagle.sakura
	 * @param origin
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	set( Vector3 origin )
	{
		x = origin.x;
		y = origin.y;
		z = origin.z;
	}

	/**
	 * 値のコピーを行う。
	 * @author eagle.sakura
	 * @param origin
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	set( float x, float y, float z )
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}


	/**
	 * 内積を取得する。
	 * @author eagle.sakura
	 * @param v
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	float	dot( Vector3 v )
	{
		return	( x * v.x ) + ( y * v.y ) + ( z * v.z );
	}

	/**
	 * 内積を取得する。
	 * @author eagle.sakura
	 * @param _x
	 * @param _y
	 * @param _z
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	float	dot( float _x, float _y, float _z )
	{
		return	( x * _x ) + ( y * _y ) + ( z * _z );
	}

	/**
	 * 外積を取得する。
	 * @author eagle.sakura
	 * @param v
	 * @param result
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	Vector3	cross( Vector3 v, Vector3 result )
	{
		result.set( ( y * v.z ) - ( z * v.y ),
					( z * v.x ) - ( x * v.z ),
					( x * v.y ) - ( y * v.x ) );
		return	result;
	}

	/**
	 * 外積を求め、このインスタンスに格納する。
	 * @author eagle.sakura
	 * @param _x
	 * @param _y
	 * @param _z
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	cross( float _x, float _y, float _z )
	{
		set(	( y * _z ) - ( z * _y ),
				( z * _x ) - ( x * _z ),
				( x * _y ) - ( y * _x ) );
	}

	/**
	 * 長さを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	float	length( )
	{
		return	( float )Math.sqrt( ( double )( (x*x) + (y*y) + (z*z) ) );
	}

	/**
	 * ベクトルの長さを正規化する。
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	normalize( )
	{
		final	float	len = length();
		x /= len;
		y /= len;
		z /= len;
	}

	/**
	 * 整合性確認。
	 * @author eagle.sakura
	 * @param obj
	 * @version 2009/11/29 : 新規作成
	 */
	@Override
	public	boolean		equals( Object obj )
	{
		Vector3	v =	( Vector3 )obj;
		return	x == v.x
			&&	y == v.y
			&&	z == v.z;
	}

	/**
	 * 文字列変換を行う。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/04/13 : 新規作成
	 */
	@Override
	public	String		toString( )
	{
		return	"V( " + x + ", " + y + ", " + z + " )";
	}

	/**
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public static	class Vector3DPool	extends	ObjectPool
	{
		public	Vector3DPool( )
		{

		}
		public	@Override	Object	createInstance( )
		{
			return	new	Vector3( );
		}
	}

}
