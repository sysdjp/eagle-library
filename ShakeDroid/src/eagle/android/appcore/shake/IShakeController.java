package eagle.android.appcore.shake;

import eagle.math.Vector3;

/**
 * 振動のコントロールを行う。
 * @author eagle.sakura
 * @version 2010/05/21 : 新規作成
 */
public abstract class IShakeController
{

	/**
	 *
	 * @author eagle.sakura
	 * @param mt
	 * @version 2010/05/22 : 新規作成
	 */
	public abstract void update( ShakeLooper	mt );

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public abstract int getShakeType( );

	/**
	 * 縦横回転を許可するか。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public abstract boolean isRotateEnable( );

	/**
	 *
	 */
	private Vector3			shakeVector = new	Vector3( );

	/**
	 *
	 * @author eagle.sakura
	 * @param vertices
	 * @version 2010/05/21 : 新規作成
	 */
	public IShakeController(  )
	{
	}

	/**
	 * 頂点ウェイトの固定を行うか。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/21 : 新規作成
	 */
	public	boolean	isLockVertexWeight( Option option )
	{
		return	false;
	}

	/**
	 * 揺れ再現用のベクトルデータを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/16 : 新規作成
	 */
	public Vector3 getShakeVector()
	{
		return	shakeVector;
	}

}