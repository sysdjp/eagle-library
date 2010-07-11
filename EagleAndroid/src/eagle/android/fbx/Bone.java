/**
 *
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
package eagle.android.fbx;

import java.io.IOException;

import eagle.android.math.Matrix4x4;
import eagle.io.DataInputStream;
import eagle.util.EagleException;

/**
 * ボーン用のノードを示す。
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
public class Bone extends Node
{
	/**
	 * 初期姿勢の逆行列。
	 */
	private		Matrix4x4		invertMatrix	=	new	Matrix4x4( );

	/**
	 * 初期姿勢の行列。
	 */
	private		Matrix4x4		defaultMatrix	=	new Matrix4x4( );

	/**
	 *
	 * @author eagle.sakura
	 * @param parent
	 * @param number
	 * @version 2010/07/07 : 新規作成
	 */
	public	Bone( Node parent, int number )
	{
		super( parent, number );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/08 : 新規作成
	 */
	@Override
	public int getNodeType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return eNodeTypeBone;
	}

	/**
	 * 初期姿勢の逆行列を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public Matrix4x4 getInvertMatrix()
	{
		return invertMatrix;
	}

}
