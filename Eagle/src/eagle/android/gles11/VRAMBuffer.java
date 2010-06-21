/**
 *
 * @author eagle.sakura
 * @version 2009/11/17 : 新規作成
 */
package eagle.android.gles11;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL11;

import eagle.util.Disposable;

/**
 * OpenGL内で生成したバッファの管理を行う。
 * @author eagle.sakura
 * @version 2009/11/17 : 新規作成
 */
public class VRAMBuffer	implements	Disposable
{
	private	int[]		buffers = null;
	private	GLManager	glMgr	= null;
	private	int			bufferType = 0;

	/**
	 *
	 * @author eagle.sakura
	 * @param mgr
	 * @version 2009/11/17 : 新規作成
	 */
	public	VRAMBuffer( GLManager mgr )
	{
		glMgr = mgr;
	}

	/**
	 * GL内にバッファを確保する。
	 * @author eagle.sakura
	 * @param bufferType
	 * @param bufferNum
	 * @version 2009/11/17 : 新規作成
	 * @see GL11#GL_ARRAY_BUFFER
	 * @see GL11#GL_ARRAY_BUFFER_BINDING
	 */
	public	void	create( int bufferType, int bufferNum )
	{
		GL11	gl = glMgr.getGL();
		buffers = new int[ bufferNum ];
		this.bufferType = bufferType;
		gl.glGenBuffers( bufferNum, buffers, 0 );
	}

	/**
	 * バッファをバインドする。
	 * @author eagle.sakura
	 * @param index
	 * @version 2009/11/17 : 新規作成
	 */
	public	void	bind( int index )
	{
		GL11	gl = glMgr.getGL();
		gl.glBindBuffer( bufferType, buffers[ index ] );
	}

	/**
	 * バッファをバインドし、転送する。
	 * @author eagle.sakura
	 * @param index
	 * @param buffer
	 * @param ussage メモリの使用方法
	 * @version 2009/11/17 : 新規作成
	 * @see GL11#GL_STATIC_DRAW
	 */
	public	void	toGLBuffer( int index, ByteBuffer buffer, int ussage )
	{
		GL11	gl = glMgr.getGL();
		gl.glBindBuffer( bufferType, buffers[ index ] );
		gl.glBufferData( bufferType, buffer.capacity(), buffer, ussage );
	}


	/**
	 * バッファをバインドし、転送する。
	 * @author eagle.sakura
	 * @param index
	 * @param buffer
	 * @param ussage メモリの使用方法
	 * @version 2009/11/17 : 新規作成
	 * @see GL11#GL_STATIC_DRAW
	 */
	public	void	toGLBuffer( int index, FloatBuffer buffer, int ussage )
	{
		GL11	gl = glMgr.getGL();
		gl.glBindBuffer( bufferType, buffers[ index ] );
		gl.glBufferData( bufferType, buffer.capacity() * 4, buffer, ussage );
	}

	/**
	 * バッファをバインドし、転送する。
	 * @author eagle.sakura
	 * @param index
	 * @param buffer
	 * @param ussage メモリの使用方法
	 * @version 2009/11/17 : 新規作成
	 * @see GL11#GL_STATIC_DRAW
	 */
	public	void	toGLBuffer( int index, ShortBuffer buffer, int ussage )
	{
		GL11	gl = glMgr.getGL();
		gl.glBindBuffer( bufferType, buffers[ index ] );
		gl.glBufferData( bufferType, buffer.capacity( ) * 2, buffer, ussage );
	}

	/**
	 * メモリを解放する。
	 * @author eagle.sakura
	 * @version 2009/11/17 : 新規作成
	 */
	public	@Override	void	dispose( )
	{
		GL11	gl = glMgr.getGL();
		gl.glDeleteBuffers( buffers.length, buffers, 0 );
	}
}
