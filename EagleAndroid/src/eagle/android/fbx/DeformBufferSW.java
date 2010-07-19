/**
 *
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
package eagle.android.fbx;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;

import eagle.android.gles11.GLManager;
import eagle.android.gles11.IGLResource;

/**
 * @author eagle.sakura
 * @version 2010/07/08 : 新規作成
 */
public class DeformBufferSW
{
	/**
	 * 頂点ウェイト配列。
	 */
	private	IntBuffer			weights			=	null;

	/**
	 * 頂点パレットの番号配列。
	 */
	private	ByteBuffer			palettes		=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/07/08 : 新規作成
	 */
	public	DeformBufferSW( )
	{

	}

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/07/12 : 新規作成
	 */
	public	void	dispose( )
	{
		weights = null;
		palettes = null;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	Buffer		getVertexWeight( )	{	return	weights;	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	int			getVertexWeightType( )	{	return	GL11.GL_FIXED;	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	Buffer		getPaletteIndexBuffer( )	{	return	palettes;	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	int			getPaletteIndexBufferType( )	{	return	GL11.GL_UNSIGNED_BYTE;	}

	/**
	 * 変形用バッファを作成する。
	 * @author eagle.sakura
	 * @param weights
	 * @param indices
	 * @version 2010/07/08 : 新規作成
	 */
	public	void		init( float[] weights, byte[] indices, int matrixPaletteSize )
	{
		{
			ByteBuffer	buffer =	ByteBuffer.allocateDirect( weights.length * 4 );
			buffer.order( ByteOrder.nativeOrder() );
			this.weights =	GLManager.toGLFixed( weights, buffer.asIntBuffer() );
			this.weights.position( 0 );
		}

		{
			palettes	=	IGLResource.createBuffer( indices );
		}
	}
}
