/**
 *
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
package eagle.android.fbx;

import java.io.IOException;

import eagle.android.gles11.GLManager;
import eagle.android.gles11.ITexture;
import eagle.android.math.Matrix4x4;
import eagle.io.DataInputStream;
import eagle.util.EagleException;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
public class Frame extends Node
{
	/**
	 * スキンメッシュの変形情報。
	 */
	private		Deformer		deformer	=	null;

	/**
	 * 頂点バッファ。
	 */
	private		VertexBufferSW	vertices	=	null;

	/**
	 *
	 */
	private		FrameSubset[]	subsets		=	null;

	/**
	 * このフレームを管理しているフィギュア。
	 */
	private		Figure			figure		=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/07/12 : 新規作成
	 */
	@Override
	public void dispose()
	{
		// TODO 自動生成されたメソッド・スタブ
		super.dispose();

		vertices.dispose();
		vertices = null;
		for( FrameSubset fs : subsets )
		{
			fs.dispose();
		}
	}

	/**
	 * 描画用のメッシュ・スキンメッシュを管理する。
	 * @author eagle.sakura
	 * @param figure
	 * @param parent
	 * @param number
	 * @version 2010/07/08 : 新規作成
	 */
	public	Frame( Figure figure, Node parent, int number )
	{
		super( parent, number );
		this.figure = figure;
	}

	/**
	 * 描画を行う。
	 * @author eagle.sakura
	 * @version 2010/07/08 : 新規作成
	 */
	@Override
	public void draw()
	{
		GLManager	glManager = figure.getGLManager();

		// TODO 自動生成されたメソッド・スタブ
		if( deformer != null )
		{
			Bone	bone = ( deformer.getBone( 1 ) );
			float	speed = 2.0f;
			deformer.bind();
		}
		else
		{
			glManager.pushMatrixF( getMatrix() );
		}

		vertices.bind();

		for( FrameSubset fs : subsets )
		{
			fs.drawSubset( glManager );
		}

		vertices.unbind();

		if( deformer != null )
		{
			deformer.unbind();
		}
		else
		{
			glManager.popMatrix();
		}

	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	@Override
	public int getNodeType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return	deformer == null ? eNodeTypeMesh : eNodeTypeSkin;
	}

	/**
	 * 変形用バッファを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	Deformer	getDeformer( )
	{
		return	deformer;
	}

	/**
	 * nameに一致するテクスチャを関連付ける。
	 * @author eagle.sakura
	 * @param name
	 * @param texture
	 * @version 2010/07/11 : 新規作成
	 */
	public	void	bindTexture( String name, ITexture texture )
	{
		for( FrameSubset fs : subsets )
		{
			if( fs.getMaterial().getTextureName().equals( name ) )
			{
				fs.getMaterial().setTexture( texture );
			}
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param dis
	 * @throws IOException
	 * @throws EagleException
	 * @version 2010/07/08 : 新規作成
	 */
	public	void	initialize( DataInputStream dis )	throws	IOException,
																EagleException
	{
		super.initialize( dis );

		vertices	=	new	VertexBufferSW( figure.getGLManager() );

		//!	位置
		{
			int			length		= dis.readS32() * 3;
			int[]		buffer	= new int[ length ];

			for( int i = 0; i < ( length ); ++i )
			{
				buffer[ i ] = ( int )( dis.readFloat() * EagleUtil.eGLFixed1_0 );
			}
			vertices.initPosBuffer( buffer );
		}

		//!	法線
		{
			int			length		= dis.readS32() * 3;
			int[]		buffer	= new int[ length ];

			for( int i = 0; i < ( length ); ++i )
			{
				buffer[ i ] = ( int )( dis.readFloat() * EagleUtil.eGLFixed1_0 );
			}
		}

		//!	UV
		{
			int			length		= dis.readS32() * 2;
			int[]		buffer	= new int[ length ];

			for( int i = 0; i < ( length ); ++i )
			{
				buffer[ i ] = ( int )( dis.readFloat() * EagleUtil.eGLFixed1_0 );
			}
			vertices.initUVBuffer( buffer );
		}

		//!	インデックスバッファ
		{
			int			length	=	dis.readS32();
			subsets	= new FrameSubset[ length ];

			for( int i = 0; i < length; ++i )
			{
				Material	m = Material.createInstance( figure.getGLManager(), dis );
				IndexBufferSW	ib = new IndexBufferSW();

				int	ibLength = dis.readS32();
				short[]		buffer	=	new short[ ibLength ];
				for( int k = 0; k < ibLength; ++k )
				{
					buffer[ k ] = ( short )dis.readS32();
				}
				ib.init( buffer );

				subsets[ i ] = new FrameSubset( ib, m );
			}


		}

		//!	デフォーマ
		{
			int		size	=	dis.readS32();
			if( size > 0 )
			{
				deformer	=	new	Deformer( this, size, figure.getGLManager() );

				for( int i = 0; i < size; ++i )
				{
					String	name = dis.readString();
					deformer.setBoneName( i, name );
				}
				//!	頂点数
				int	length = dis.readS32() * 3;
				byte[]	indices = new byte[ length ];
				float[]	weights = new float[ length ];

				for( int i = 0; i < length; ++i )
				{
					indices[ i ] = dis.readS8();
				}
				for( int i = 0; i < length; ++i )
				{
					weights[ i ] = dis.readFloat();
				}

				DeformBufferSW	db = new DeformBufferSW();
				db.init( weights, indices, size );
				deformer.setDeform( db );
			}
		}
	}
}
