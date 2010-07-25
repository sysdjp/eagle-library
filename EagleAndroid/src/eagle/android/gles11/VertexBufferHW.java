/**
 *
 * @author eagle.sakura
 * @version 2010/07/25 : 新規作成
 */
package eagle.android.gles11;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.microedition.khronos.opengles.GL11;


/**
 * 頂点バッファを管理する。<BR>
 * VRAMに頂点情報を予め転送するため、
 * 高速な描画を行える。
 * @author eagle.sakura
 * @version 2010/07/25 : 新規作成
 */
public class VertexBufferHW implements IVertexBuffer
{
	private	GLManager		glManager	=	null;
	private	VRAMResource	vram		=	null;
	private	int				vertexNum	=	0;

	/**
	 * @author eagle.sakura
	 * @param glManager
	 * @version 2010/07/25 : 新規作成
	 */
	public	VertexBufferHW( GLManager glManager )
	{
		this.glManager	=	glManager;
	}

	private	static	int		eVramIndexPositions	=	0;
	private	static	int		eVramIndexUVs		=	1;
	private	static	int		eVramIndexNormals	=	2;
	private	static	int		eVramIndexColors	=	3;

	/**
	 *
	 * @author eagle.sakura
	 * @param positions
	 * @param uv
	 * @param normals
	 * @param colors
	 * @version 2010/07/25 : 新規作成
	 */
	public	void	initialize( float[] 	positions,
								float[]		uv,
								float[]		normals,
								byte[]		colors	)
	{
		vertexNum = ( positions.length / 3 );
		vram = new VRAMResource( glManager );
		vram.create( 4 );

		if( positions != null )
		{
			IntBuffer	buffer = GLManager.toGLFixed(	positions,
														( ByteBuffer.allocate( positions.length * 4 ).asIntBuffer() )
														);
			vram.toGLBuffer( eVramIndexPositions, buffer, buffer.capacity() * 4, GL11.GL_ARRAY_BUFFER, GL11.GL_STATIC_DRAW );
		}

		if( uv != null )
		{
			IntBuffer	buffer = GLManager.toGLFixed(	uv,
														( ByteBuffer.allocate( uv.length * 4 ).asIntBuffer() )
														);
			vram.toGLBuffer( eVramIndexUVs, buffer, buffer.capacity() * 4, GL11.GL_ARRAY_BUFFER, GL11.GL_STATIC_DRAW );
		}

		if( normals != null )
		{
			IntBuffer	buffer = GLManager.toGLFixed(	normals,
														( ByteBuffer.allocate( normals.length * 4 ).asIntBuffer() )
														);
			vram.toGLBuffer( eVramIndexNormals, buffer, buffer.capacity() * 4, GL11.GL_ARRAY_BUFFER, GL11.GL_STATIC_DRAW );
		}

		if( colors != null )
		{
			ByteBuffer	buffer = IGLResource.createBuffer( colors );
			vram.toGLBuffer( eVramIndexColors, buffer, buffer.capacity(), GL11.GL_ARRAY_BUFFER, GL11.GL_STATIC_DRAW );
		}
	}

	/**
	 * @author eagle.sakura
	 * @version 2010/07/25 : 新規作成
	 */
	@Override
	public void bind()
	{
	// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * @author eagle.sakura
	 * @version 2010/07/25 : 新規作成
	 */
	@Override
	public void unbind()
	{
	// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * @author eagle.sakura
	 * @version 2010/07/25 : 新規作成
	 */
	@Override
	public void dispose()
	{
	// TODO 自動生成されたメソッド・スタブ

	}

}
