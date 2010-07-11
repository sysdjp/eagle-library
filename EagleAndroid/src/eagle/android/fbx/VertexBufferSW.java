/**
 *
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
package eagle.android.fbx;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import eagle.android.gles11.GLManager;
import eagle.android.gles11.IGLResource;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
public class VertexBufferSW
{
	/**
	 * 位置バッファ。
	 */
	private		IntBuffer			posBuffer		=	null;
	/**
	 * 頂点カラーバッファ。
	 */
	private		IntBuffer			colBuffer		=	null;
	/**
	 * UVバッファ。
	 */
	private		IntBuffer			uvBuffer		=	null;
	/**
	 * 法線バッファ。
	 */
	private		IntBuffer			normalBuffer	=	null;

	/**
	 * 転送情報。
	 */
	private		int					attribute		=	0x0;

	/**
	 *
	 */
	private		GLManager			glManager		=	null;

	/**
	 * 色情報を無効化する。
	 */
	public	static	final	int		eAttributeColorDisable	=	0x1 << 0;

	/**
	 * UVを無効化する。
	 */
	public	static	final	int		eAttributeUVDisable		=	0x1 << 1;

	/**
	 *
	 * @author eagle.sakura
	 * @param gl
	 * @version 2010/07/08 : 新規作成
	 */
	public	VertexBufferSW( GLManager gl  )
	{
		glManager = gl;
	}


	/**
	 * 位置バッファを作成する。<BR>
	 * 各値は固定少数で示す。
	 * @author eagle.sakura
	 * @param vertexBuffer
	 * @version 2009/11/14 : 新規作成
	 */
	public	void		initPosBuffer( int[] vertexBuffer )
	{
		if( posBuffer != null )
		{
			posBuffer.put( vertexBuffer );
			posBuffer.position( 0 );
		}
		else
		{
			posBuffer = IGLResource.createBuffer( vertexBuffer );
		}
	}

	/**
	 * 位置バッファを作成する。<BR>
	 * 各値は固定少数で示す。
	 * @author eagle.sakura
	 * @param vertexBuffer
	 * @version 2009/11/14 : 新規作成
	 */
	public	void		initPosBuffer( float[] vertexBuffer )
	{
		if( posBuffer != null )
		{
			GLManager.toGLFixed( vertexBuffer, posBuffer );
			posBuffer.position( 0 );
		}
		else
		{
			ByteBuffer	buffer =	ByteBuffer.allocateDirect( vertexBuffer.length * 4 );
			buffer.order( ByteOrder.nativeOrder() );
			posBuffer =	GLManager.toGLFixed( vertexBuffer, buffer.asIntBuffer() );
			posBuffer.position( 0 );
		}
	}

	/**
	 * 位置バッファを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public	IntBuffer	getPositions( )	{	return	posBuffer;	}

	/**
	 * 色バッファを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public	IntBuffer	getColors( )	{	return	colBuffer;	}

	/**
	 * UVバッファを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public	IntBuffer	getUVs( )		{	return	uvBuffer;	}

	/**
	 * 色バッファを作成する。<BR>
	 * 各値は固定小数で示す。
	 * @author eagle.sakura
	 * @param vertexBuffer
	 * @version 2009/11/14 : 新規作成
	 */
	public	void		initColBuffer( int[] vertexBuffer )
	{
		if( colBuffer != null )
		{
			colBuffer.put( vertexBuffer );
			colBuffer.position( 0 );
		}
		else
		{
			colBuffer = IGLResource.createBuffer( vertexBuffer );
		}
	}

	/**
	 * 属性情報を指定する。
	 * @author eagle.sakura
	 * @param eAttr
	 * @param is
	 * @version 2010/05/22 : 新規作成
	 */
	public	void		setAttribute( int eAttr, boolean is )
	{
		attribute = EagleUtil.setFlag( attribute, eAttr, is );
	}

	/**
	 * 属性が有効になっている場合真を返す。
	 * @author eagle.sakura
	 * @param eAttr
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public	boolean		isAttributeOn( int eAttr )
	{
		return	EagleUtil.isFlagOn( attribute, eAttr );
	}

	/**
	 * テクスチャのUVバッファを設定する。
	 * @author eagle.sakura
	 * @param buffer
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		initUVBuffer( int[] buffer )
	{
		if( uvBuffer != null )
		{
			uvBuffer.put( buffer );
			uvBuffer.position( 0 );
		}
		else
		{
			uvBuffer = IGLResource.createBuffer( buffer );
		}
	}

	/**
	 * テクスチャのUVバッファを設定する。
	 * @author eagle.sakura
	 * @param buffer
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		initUVBuffer( float[] buffer )
	{
		if( uvBuffer != null )
		{
			GLManager.toGLFixed( buffer, uvBuffer );
			uvBuffer.position( 0 );
		}
		else
		{
			ByteBuffer	buf =	ByteBuffer.allocateDirect( buffer.length * 4 );
			buf.order( ByteOrder.nativeOrder() );
			uvBuffer =	GLManager.toGLFixed( buffer, buf.asIntBuffer() );
			uvBuffer.position( 0 );
		}
	}

	/**
	 * バッファに転送する。
	 * @author eagle.sakura
	 * @param gl
	 * @version 2009/11/14 : 新規作成
	 */
	public	void		bind(  )
	{
		GL11 gl = glManager.getGL();
		//!	位置バッファを転送する。
		gl.glEnableClientState(GL11.GL_VERTEX_ARRAY);
		gl.glVertexPointer( 3, GL11.GL_FIXED, 0, posBuffer );

		//!	色バッファを転送する。
		if( colBuffer != null
		&&	!isAttributeOn( eAttributeColorDisable ) )
		{
			gl.glEnableClientState(GL11.GL_COLOR_ARRAY);
			gl.glColorPointer( 4, GL11.GL_FIXED, 0, colBuffer );
		}

		//!	UVバッファを転送する。
		if( uvBuffer != null
		&&	!isAttributeOn( eAttributeUVDisable )
		)
		{
			gl.glEnableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
			gl.glTexCoordPointer( 2, GL11.GL_FIXED, 0, uvBuffer  );
		}

	}

	/**
	 * デバイスから切り離す。
	 * @author eagle.sakura
	 * @param gl
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		unbind(  )
	{
		GL11 gl = glManager.getGL();
		gl.glDisableClientState( GL11.GL_VERTEX_ARRAY );
		//!	色バッファを転送する。
		if( colBuffer != null )
		{
			gl.glDisableClientState(GL11.GL_COLOR_ARRAY);
		}

		//!	UVバッファを転送する。
		if( uvBuffer != null )
		{
			gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
		}
	}

	/**
	 * リソースを解放する。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		dispose( )
	{
		posBuffer	= null;
		colBuffer	= null;
		uvBuffer	= null;
	}
}
