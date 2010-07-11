/**
 *
 * @author eagle.sakura
 * @version 2010/05/13 : 新規作成
 */
package eagle.android.appcore.shake;

import java.io.IOException;

import android.graphics.Region.Op;
import android.util.Log;
import eagle.android.gles11.GLManager;
import eagle.android.gles11.IndexBufferSW;
import eagle.android.gles11.VertexBufferSW;
import eagle.android.math.Matrix4x4;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.math.Vector3;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/05/13 : 新規作成
 */
public class ShakeVertices
{
	/**
	 * 横方向分割数。
	 */
	private	int		divisionX	=	0;
	/**
	 * 縦方向分割数。
	 */
	private	int		divisionY	=	0;

	/**
	 * 位置頂点。
	 */
	private	int[]	positions	=	null;

	/**
	 * UV情報。
	 */
	private	int[]	uv			=	null;

	/**
	 * インデックス。
	 */
	private	short[]	indices		=	null;

	/**
	 * 色情報バッファ。
	 */
	private	int[]	colors		=	null;

	/**
	 * GL管理。
	 */
	private	GLManager	glMgr	=	null;

	/**
	 *
	 */
	private	VertexWeight[]		weights	=	null;

	/**
	 * 頂点バッファ。
	 */
	private	VertexBufferSW		vertexBuffer	=	new	VertexBufferSW();

	/**
	 * 頂点バッファ。
	 */
	private	IndexBufferSW		indexBuffer		=	new	IndexBufferSW();

	/**
	 *
	 * @author eagle.sakura
	 * @param divisions 画面の縦横分割数。
	 * @version 2010/05/13 : 新規作成
	 */
	public	ShakeVertices( GLManager gl, int divX, int divY )
	{
		glMgr 		= gl;
		divisionX	= divX;
		divisionY	= divY;

		final	int	fixedX	=	EagleUtil.eGLFixed1_0	/ ( divisionX );
		final	int	fixedY	=	EagleUtil.eGLFixed1_0	/ ( divisionY );
		{
			positions	=	new	int[ 	( divisionX + 1 )
			         	 	   	    *	( divisionY + 1 )
			         	 	   	    *	3
			         	 	   	     ];

			weights		=	new	VertexWeight[ 	( divisionX + 1 )
					         	 	   	    *	( divisionY + 1 )
					         	 	   	     ];

			int	index = 0;

			for( int y = 0; y < ( divisionY + 1 ); ++y )
			{
				for( int x = 0; x < ( divisionX + 1 ); ++x )
				{
					int	head = index;
					//!	x
					positions[ index ] = fixedX * x;
					++index;
					//!	y
					positions[ index ] = fixedY * y;
					++index;
					++index;

					weights[ head / 3 ] = new VertexWeight( this, head );
				}
			}
		}



		{
			float	scaleX = 1.0f,
					scaleY = 1.0f;
			{
				int	texSizeX = 2,
					texSizeY = 2;

				while( texSizeX < glMgr.getDisplayWidth() )
				{
					texSizeX *= 2;
				}
				while( texSizeY < glMgr.getDisplayHeight() )
				{
					texSizeY *= 2;
				}

				scaleX = ( float )glMgr.getDisplayWidth()	/ ( float )texSizeX;
				scaleY = ( float )glMgr.getDisplayHeight()	/ ( float )texSizeY;
			}

			uv			=	new	int[ 	( divisionX + 1 )
			         	 	   	    *	( divisionY + 1 )
			         	 	   	    *	2
			         	 	   	     ];

			int	index = 0;

			for( int y = 0; y < ( divisionY + 1 ); ++y )
			{
				for( int x = 0; x < ( divisionX + 1 ); ++x )
				{
					//!	x
					uv[ index ] = ( int )( ( fixedX * x ) * scaleX );
					++index;
					//!	y
					uv[ index ] = ( int )( (  EagleUtil.eGLFixed1_0 - fixedY * y ) * scaleY );
					++index;
				}
			}
		}


		{
			indices		=	new	short[ 	( divisionX )
			         	 	   	    *	( divisionY )
			         	 	   	    *	6
			         	 	   	     ];
			/**
			 *
				short[]	indices =
				{
					0,	1,	2,
					1,	2,	3,
				};
			 */

			final	int	line = divisionX + 1;

			int	index = 0;
			for( int y = 0; y < ( divisionY ); ++y )
			{
				for( int x = 0; x < ( divisionX ); ++x )
				{
					/**
					 * 2     3
					 *
					 * 0     1
					 */
					indices[ index ] = ( short )( ( line * ( y + 0 ) ) + x + 0 );
					++index;
					indices[ index ] = ( short )( ( line * ( y + 0 ) ) + x + 1 );
					++index;
					indices[ index ] = ( short )( ( line * ( y + 1 ) ) + x + 0 );
					++index;

					indices[ index ] = ( short )( ( line * ( y + 0 ) ) + x + 1 );
					++index;
					indices[ index ] = ( short )( ( line * ( y + 1 ) ) + x + 0 );
					++index;
					indices[ index ] = ( short )( ( line * ( y + 1 ) ) + x + 1 );
					++index;
				}
			}
		}

		//!	色バッファを生成する。
		{
			int		colorNum = positions.length / 3;
			colors = new int[ colorNum * 4 ];
		}

		//!	バッファの生成
		vertexBuffer.initPosBuffer( positions );
		vertexBuffer.initColBuffer( colors );
		vertexBuffer.initUVBuffer( uv );
		indexBuffer.init( indices );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/15 : 新規作成
	 */
	public	int		getDivisionX( )	{	return	divisionX;	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/15 : 新規作成
	 */
	public	int		getVertexOffsetX( )	{	return	EagleUtil.eGLFixed1_0 / ( getDivisionX() + 1 );	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/15 : 新規作成
	 */
	public	int		getDivisionY( )	{	return	divisionY;	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/15 : 新規作成
	 */
	public	int		getVertexOffsetY( )	{	return	EagleUtil.eGLFixed1_0 / ( getDivisionY() + 1 );	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/14 : 新規作成
	 */
	public	int[]	getPositions( )	{	return	positions;	};

	/**
	 * 格子状になっている頂点のXY番号を指定して頂点を取得する。
	 * @author eagle.sakura
	 * @param numX
	 * @param numY
	 * @return
	 * @version 2010/05/13 : 新規作成
	 */
	public	int		getVertexHeader( int numX, int numY )
	{
		return	( ( numY * ( divisionX + 1 ) ) + numX ) * 3;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param dos
	 * @version 2010/05/31 : 新規作成
	 */
	public	void	serialize( DataOutputStream dos ) throws IOException
	{
		//!	長さを書き込む
		dos.writeS32( weights.length );

		//!	重み配列を書き込む
		for( VertexWeight vw : weights )
		{
			dos.writeFloat( vw.getWeight() );
		}
	}

	/**
	 * 重み配列を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/05 : 新規作成
	 */
	public	float[]	getWeightArray( )
	{
		float[]	result = new float[ weights.length ];
		//!	重み配列を書き込む
		for( int i = 0; i < result.length; ++i )
		{
			result[ i ] = weights[ i ].getWeight();
		}
		return	result;
	}


	/**
	 *
	 * @author eagle.sakura
	 * @param dis
	 * @version 2010/05/31 : 新規作成
	 */
	public	void	deserialize( float[] weights )
	{
		//!	重み配列を書き込む
		for( int i = 0; i < weights.length; ++i )
		{
			this.weights[ i ].setWeight( weights[ i ] );
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param numX
	 * @param numY
	 * @return
	 * @version 2010/05/14 : 新規作成
	 */
	public	VertexWeight	getWeight( int numX, int numY )
	{
		if( numX < 0
		||	numX > divisionX
		||	numY < 0
		||	numY > divisionY )
		{
			return	null;
		}

		return	weights[ ( ( numY * ( divisionX + 1 ) ) + numX ) ];
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @version 2010/05/13 : 新規作成
	 */
	public	void	onTouch( Option option, float levelX, float levelY )
	{
		float	offsetX = 1.0f / ( float )( getDivisionX() + 1 ) / 2.0f;
		float	offsetY = 1.0f / ( float )( getDivisionY() + 1 ) / 2.0f;

		levelX += offsetX;
		levelY += offsetY;

		//!	どの頂点か
		levelY = 1.0f - levelY;


		int		vertexX = ( int )( levelX * ( float )( divisionX + 1) );
		int		vertexY = ( int )( levelY * ( float )( divisionY + 1) );

		final	float	addWeight = option.getTouchWeightAdd() / 10.0f;
		for( int y = 0; y < 3; ++y )
		{
			for( int x = 0; x < 3; ++x )
			{
				VertexWeight	weight = getWeight( vertexX + x - 1, vertexY + y - 1 );
				if( weight != null )
				{
					if( y == 1 && x == 1 )
					{
						weight.addWeight( addWeight );
					}
					else
					{
						weight.addWeight( addWeight / 2.0f );
					}
				}
			}
		}
		/*
		if( vertexX == 0
		||	vertexY == 0
		||	vertexX > getDivisionX()
		||	vertexY > getDivisionY() )
		{
			return;
		}

		VertexWeight	weight = getWeight( vertexX, vertexY );
		weight.addWeight( 0.15f );
	//	weight.addWeight( 0.1f );
		*/
	}

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/05/17 : 新規作成
	 */
	public	void	resetWeights( )
	{
		for( VertexWeight	vw : weights )
		{
			vw.reset(	);
		}
	}

	/**
	 * 更新を行う。
	 * @author eagle.sakura
	 * @version 2010/05/16 : 新規作成
	 */
	public	void	update( Option opt, IShakeController ctrl )
	{
		Vector3	offset = new Vector3( getVertexOffsetX(), getVertexOffsetY(), 0 );

		offset.x *= ctrl.getShakeVector().x;
		offset.y *= ctrl.getShakeVector().y;
		offset.x *= opt.getShakeMul();
		offset.y *= opt.getShakeMul();
		for( VertexWeight	vw : weights )
		{
			vw.update( opt, ctrl, offset );
		}
	}

	/**
	 * 描画を行う。
	 * @author eagle.sakura
	 * @version 2010/05/13 : 新規作成
	 */
	public	void	draw( Option	option )
	{
		VertexBufferSW		vb = vertexBuffer;
		IndexBufferSW		ib = indexBuffer;

		vb.initPosBuffer( positions );
		vb.initUVBuffer( uv );

		if( option.isBlueMode() )
		{
			int	colorNum = colors.length / 4;
			int	index = 0;

			for( VertexWeight vw : weights )
			{
				colors[ index ] = ( int )( ( float )EagleUtil.eGLFixed1_0 * vw.getWeight() * 5.0f );
				++index;
				colors[ index ] = 0;
				++index;
				colors[ index ] = EagleUtil.eGLFixed1_0;
				++index;
				colors[ index ] = EagleUtil.eGLFixed1_0;
				++index;
			}
			vb.initColBuffer( colors );
			vb.setAttribute( VertexBufferSW.eAttributeColorDisable, false );
		}
		else
		{
			vb.setAttribute( VertexBufferSW.eAttributeColorDisable, true );
		}


		{
			vb.bind( glMgr );
			ib.bind( glMgr );

			ib.drawElements( glMgr );

			ib.unbind( glMgr );
			vb.unbind( glMgr );

		}
	}
}
