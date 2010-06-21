/**
 *
 * @author eagle.sakura
 * @version 2010/04/06 : 新規作成
 */
package eagle.math;

/**
 * 4x4の要素を持つ行列を管理する。<BR>
 * 4x3として扱うか、4x4として扱うかはサブクラスの実装に任せる。
 * @author eagle.sakura
 * @version 2010/04/06 : 新規作成
 */
public abstract class BaseMatrix
{
	/**
	 * 内部管理を行っている行列。<BR>
	 * 速度優先のため、公開属性とする。
	 */
	public	float[]		m =
	{
		1.0f,	0.0f,	0.0f,	0.0f,
		0.0f,	1.0f,	0.0f,	0.0f,
		0.0f,	0.0f,	1.0f,	0.0f,
		0.0f,	0.0f,	0.0f,	1.0f
	};

	/**
	 * 拡大行列を作成する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	scale( float x, float y, float z )
	{
		m[ 4*0 + 0 ] = x;
		m[ 4*1 + 1 ] = y;
		m[ 4*2 + 2 ] = z;
	}

	/**
	 * 回転行列を生成する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @param w
	 * @version 2010/04/06 : 新規作成
	 */
	public	abstract	void	rotate( float x, float y, float z, float w );

	/**
	 * 位置行列を作成する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2010/04/06 : 新規作成
	 */
	public	abstract	void	translate( float x, float y, float z );

	/**
	 * this = this * transの計算を行う。
	 * @author eagle.sakura
	 * @param trans
	 * @version 2009/11/15 : 新規作成
	 */
	public	abstract	void	multiply(	BaseMatrix	trans );

	/**
	 * vにこの行列を適用し、resultに格納する。
	 * @author eagle.sakura
	 * @param v
	 * @param result
	 * @return resultの参照が戻る。
	 * @version 2010/04/06 : 新規作成
	 */
	public	abstract	Vector3	transVector(	Vector3	v,
												Vector3	result	);
	/**
	 * 値をコピーする。
	 * @author eagle.sakura
	 * @param origin
	 * @version 2009/11/23 : 新規作成
	 */
	public	void	set( Matrix4x4 origin )
	{
		for( int i = 0; i < 4*4; ++i )
		{
			m[ i ] = origin.m[ i ];
		}
	}

	/**
	 * 逆行列を作成する。
	 * @author eagle.sakura
	 * @param result
	 * @version 2010/04/06 : 新規作成
	 */
	public	abstract	void	invert( BaseMatrix result );


	/**
	 * 単位行列を作成する。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	identity( )
	{
		for( int i = 0; i < 4; ++i )
		{
			for( int k = 0; k < 4; ++k )
			{
				if( i != k )	m[ i * 4 + k ] = 0.0f;
				else			m[ i * 4 + k ] = 1.0f;
			}
		}
	}

}
