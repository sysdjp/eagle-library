/**
 *
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
package eagle.android.gles11;

import java.nio.Buffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;


/**
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
public class IndexBuffer	extends	IGLResource
{
	private	int					bufferLength	=	0;
	private	VRAMBuffer			hwBuffer		=	null;
	private	GLManager			glMgr			= null;

	/**
	 * 空のインデックスバッファを作成する。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	IndexBuffer( GLManager glMgr )
	{
		this.glMgr = glMgr;
	}

	/**
	 * 頂点バッファを作成する。
	 * @author eagle.sakura
	 * @param index
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	init( short[] index )
	{
		ShortBuffer	indexBuffer = createBuffer( index );
		bufferLength = index.length;
		hwBuffer = new VRAMBuffer( glMgr );
		hwBuffer.create( GL11.GL_ELEMENT_ARRAY_BUFFER, 1 );
		hwBuffer.toGLBuffer( 0, indexBuffer, GL11.GL_STATIC_DRAW );
	}
	/**
	 * バッファの格納個数を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	int			getBufferLength( )	{	return	bufferLength;	}

	/**
	 * バッファのタイプを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	int			getBufferType( )	{	return	GL11.GL_UNSIGNED_SHORT;	}

	/**
	 * 描画を行う。
	 * @author eagle.sakura
	 * @param glMgr
	 * @version 2009/11/16 : 新規作成
	 */
	public	void		drawElements( GLManager	glMgr )
	{
		GL11	gl = glMgr.getGL();
		hwBuffer.bind( 0 );
		gl.glDrawElements( GL11.GL_TRIANGLES, getBufferLength(), getBufferType(), 0 );
	}

	/**
	 * リソースをデバイスに転送する。
	 * @author eagle.sakura
	 * @param gl
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	bind( GLManager glMgr )
	{
	}

	/**
	 * リソースをデバイスから切り離す。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	unbind( GLManager glMgr )
	{
	}

	/**
	 * リソースを解放する。
	 * @param gl
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	dispose( )
	{
		hwBuffer.dispose();
		hwBuffer = null;
	}


}
