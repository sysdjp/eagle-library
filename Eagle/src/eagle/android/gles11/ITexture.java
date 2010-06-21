/**
 *
 * @author eagle.sakura
 * @version 2010/04/08 : 新規作成
 */
package eagle.android.gles11;

import javax.microedition.khronos.opengles.GL11;

/**
 * テクスチャを示す基底クラス。
 * @author eagle.sakura
 * @version 2010/04/08 : 新規作成
 */
public	abstract class ITexture		extends	IGLResource
{
	/**
	 * 不明なテクスチャの型を示す。
	 */
	public	static	final	int		eTypeUnknown			=	-1;

	/**
	 * RGB各8bit、インデックス8bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeI8RGB888		=	0;

	/**
	 * RGBA各8bit、インデックス8bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeI8RGBA8888		=	1;

	/**
	 * RGB各5bit、アルファ1bit、インデックス8bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeI8RGBA5551		=	2;

	/**
	 * RGB各5bit、アルファ1bit、インデックス4bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeI4RGBA5551		=	3;

	/**
	 * RGBA各8bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeRGBA8888		=	4;

	/**
	 * RGB各8bitのテクスチャを示す。
	 */
	public	static	final	int		eTypeRGB888			=	5;

	/**
	 * テクスチャID。
	 */
	private	int		textureId = -1;

	/**
	 * 関連付けられたマネージャ。
	 */
	private	GLManager		glManager	=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @param gl
	 * @version 2010/05/30 : 新規作成
	 */
	public	ITexture( GLManager	gl )
	{
		glManager = gl;
		int[]	n = { -1 };
		gl.getGL().glGenTextures( 1, n, 0 );
		textureId = n[ 0 ];
	}

	/**
	 * 関連付けられたGL管理クラスを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	public	GLManager		getGLManager( )
	{
		return	glManager;
	}

	/**
	 * テクスチャIDを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	public	int		getTextureID( )
	{
		return	textureId;
	}

	/**
	 * テクスチャの種類を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/04/13 : 新規作成
	 * @see ITexture#eTypeUnknown
	 * @see ITexture#eTypeI8RGB888
	 * @see ITexture#eTypeI8RGBA8888
	 * @see ITexture#eTypeI8RGBA5551
	 * @see ITexture#eTypeI4RGBA5551
	 * @see ITexture#eTypeRGBA8888
	 * @see ITexture#eTypeRGB888
	 */
	public	int		getType( )
	{
		return	eTypeUnknown;
	}

	/**
	 * テクスチャの高さを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/04/13 : 新規作成
	 */
	public	abstract	int		getHeight( );

	/**
	 * テクスチャの幅を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/04/13 : 新規作成
	 */
	public	abstract	int		getWidth( );

	/**
	 * @author eagle.sakura
	 * @param glMgr
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public void bind(GLManager glMgr)
	{
	// TODO 自動生成されたメソッド・スタブ
		GL11	gl = glMgr.getGL();
		gl.glEnable( GL11.GL_TEXTURE_2D );
		gl.glBindTexture( GL11.GL_TEXTURE_2D, getTextureID() );
	}

	/**
	 * @author eagle.sakura
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public void dispose()
	{
	// TODO 自動生成されたメソッド・スタブ
		int	n[] = { getTextureID() };
		getGLManager().getGL().glDeleteTextures( 1, n, 0 );
	}

	/**
	 * @author eagle.sakura
	 * @param glMgr
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public void unbind(GLManager glMgr)
	{
	// TODO 自動生成されたメソッド・スタブ
		GL11	gl = glMgr.getGL();
		gl.glBindTexture( GL11.GL_TEXTURE_2D, 0 );
		gl.glDisable( GL11.GL_TEXTURE_2D );
	}
}