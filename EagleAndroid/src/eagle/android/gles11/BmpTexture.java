/**
 *
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
package eagle.android.gles11;

import javax.microedition.khronos.opengles.GL11;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

/**
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
public class BmpTexture extends ITexture
{
	/**
	 * テクスチャ幅。
	 */
	private	int			width	=	0;
	/**
	 * テクスチャ高さ。
	 */
	private	int			height	=	0;

	/**
	 * Bitmapからテクスチャを作成する。
	 * @author eagle.sakura
	 * @param bitmap
	 * @version 2010/05/30 : 新規作成
	 */
	public	BmpTexture( Bitmap	bitmap, GLManager glMgr )
	{
		super( glMgr );
		width	= bitmap.getWidth();
		height	= bitmap.getHeight();

		GL11	gl = glMgr.getGL();

		//!	テクスチャ位置を指定。
		gl.glBindTexture( GL11.GL_TEXTURE_2D, getTextureID() );

		//!	テクスチャ属性指定。
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
		gl.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);

		//!	テクスチャを指定。
		GLUtils.texImage2D( GL11.GL_TEXTURE_2D, 0, bitmap, 0 );

		//!	念のためテクスチャ解除
		gl.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		gl.glDisableClientState( GL11.GL_TEXTURE_2D );
		gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public int getHeight()
	{
		// TODO 自動生成されたメソッド・スタブ
		return height;
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public int getType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return eTypeUnknown;
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public int getWidth()
	{
		// TODO 自動生成されたメソッド・スタブ
		return width;
	}


}