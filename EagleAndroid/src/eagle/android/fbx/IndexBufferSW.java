/**
 *
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
package eagle.android.fbx;

import java.nio.Buffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

import eagle.android.gles11.GLManager;
import eagle.android.gles11.IGLResource;

/**
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
public class IndexBufferSW
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
			indexBuffer = IGLResource.createBuffer( index );
		}
		bufferLength = index.length;
	}

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
							bufferLength,
							GL11.GL_UNSIGNED_SHORT,
							indexBuffer
						);
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
