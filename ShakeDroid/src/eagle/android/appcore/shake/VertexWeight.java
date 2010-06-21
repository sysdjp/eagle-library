/**
 *
 * @author eagle.sakura
 * @version 2010/05/14 : 新規作成
 */
package eagle.android.appcore.shake;

import eagle.math.Vector3;
import android.util.Log;

/**
 * 頂点の重さと制御を行う。
 * @author eagle.sakura
 * @version 2010/05/14 : 新規作成
 */
public class VertexWeight
{
	/**
	 * 基本位置X。
	 */
	private		int		defX		=	0;
	/**
	 * 基本位置Y。
	 */
	private		int		defY		=	0;
	/**
	 * 基本位置Z。
	 */
	private		int		defZ		=	0;

	/**
	 *
	 */
	private		int[]	positions	=	null;

	/**
	 * 頂点インデックス。
	 */
	private		int		vertexIndex	=	0;

	/**
	 * 頂点の重さ。
	 */
	private		float	weight				=	0;

	/**
	 * 頂点配列。
	 */
	private		ShakeVertices	vertices	=	null;

	/**
	 * ウェイトを作成。
	 * @author eagle.sakura
	 * @param parent
	 * @param index
	 * @version 2010/05/14 : 新規作成
	 */
	public	VertexWeight( ShakeVertices parent,	int	index )
	{
		vertexIndex = index;
		vertices	= parent;
		positions	= parent.getPositions();
		defX 		= positions[ index + 0 ];
		defY 		= positions[ index + 1 ];
		defZ 		= positions[ index + 2 ];

	}

	/**
	 * 重みを追加する。
	 * @author eagle.sakura
	 * @param f
	 * @version 2010/05/14 : 新規作成
	 */
	public	void	addWeight( float f )
	{
		weight	+= f;
		weight = Math.min( weight, 1.0f );
	}

	/**
	 * 重みを設定する。
	 * @author eagle.sakura
	 * @param f
	 * @version 2010/05/31 : 新規作成
	 */
	public	void	setWeight( float f )
	{
		weight = f;
	}

	/**
	 * 頂点の重みを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/15 : 新規作成
	 */
	public	float	getWeight(  )
	{
		return	weight;
	}

	/**
	 * ウェイトを無効化する。
	 * @author eagle.sakura
	 * @version 2010/05/17 : 新規作成
	 */
	public	void	reset( )
	{
		weight = 0.0f;
	}

	/**
	 * 頂点の更新を行う。
	 *
	 * @author eagle.sakura
	 * @version 2010/05/15 : 新規作成
	 */
	public	void	update( Option	opt, IShakeController ctrl, Vector3	offsetVertices )
	{
		if( weight == 0.0f )
		{
			return;
		}


		if( opt.isBlueMode() )
		{
			positions[ vertexIndex + 0 ] = defX;
			positions[ vertexIndex + 1 ] = defY;
		}
		else
		{
		//	Vector3	v3 = ctrl.getShakeVector();
			{
				/*
				float	offset	= ( float )vertices.getVertexOffsetX();
				offset *= v3.x;
				offset *= weight;
				offset *= opt.getShakeMul();
				*/
				positions[ vertexIndex + 0 ] = defX + ( ( int ) ( offsetVertices.x * weight ) );
			}
			{
				/*
				float	offset	= ( float )vertices.getVertexOffsetY();
				offset *= v3.y;
				offset *= weight;
				offset *= opt.getShakeMul();
				*/
				positions[ vertexIndex + 1 ] = defY + ( ( int ) ( offsetVertices.y * weight ) );
			}
		}
	}
}
