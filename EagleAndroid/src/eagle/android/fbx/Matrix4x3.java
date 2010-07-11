/**
 *
 * @author eagle.sakura
 * @version 2010/07/10 : 新規作成
 */
package eagle.android.fbx;

/**
 * @author eagle.sakura
 * @version 2010/07/10 : 新規作成
 */
public class Matrix4x3
{
	/**
	 *
	 */
	public	float[]	m =
	{
		1.0f, 0.0f, 0.0f, 0.0f,
		0.0f, 1.0f, 0.0f, 0.0f,
		0.0f, 0.0f, 1.0f, 0.0f,
		0.0f, 0.0f, 0.0f, 1.0f,
	};

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/07/10 : 新規作成
	 */
	public	Matrix4x3( )
	{

	}

	/**
	 * 単位行列にする。
	 * @author eagle.sakura
	 * @version 2010/07/10 : 新規作成
	 */
	public	void	identity( )
	{
		for( int i = 0; i < 4; ++i )
		{
			for( int k = 0; k < 4; ++k )
			{
				if( i == k )
				{
					m[ i * 4 + k ] = 1.0f;
				}
				else
				{
					m[ i * 4 + k ] = 0.0f;
				}
			}
		}
	}


}
