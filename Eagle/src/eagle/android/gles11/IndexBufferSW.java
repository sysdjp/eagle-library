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
public class IndexBufferSW	extends	IGLResource
{
	private	ShortBuffer			indexBuffer		=	null;
	private	int					bufferLength	=	0;
	/**
	 * 空のインデックスバッファを作成する。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	IndexBufferSW( )
	{

	}

	/**
	 * 頂点バッファを作成する。
	 * @author eagle.sakura
	 * @param index
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	init( short[] index )
	{
		if( indexBuffer != null )
		{
			indexBuffer.put( index );
			indexBuffer.position( 0 );
		}
		else
		{
			indexBuffer = createBuffer( index );
		}
		bufferLength = index.length;
	}

	/**
	 * バッファを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	Buffer		getBuffer( )	{	return	indexBuffer;	}

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
		//!	インデックスバッファの描画を行う。
		gl.glDrawElements(	GL11.GL_TRIANGLES,
							getBufferLength(),
							getBufferType(),
							getBuffer()
						);
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
		indexBuffer = null;
	}


}
