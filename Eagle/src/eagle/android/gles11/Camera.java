/**
 *
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
package eagle.android.gles11;

import javax.microedition.khronos.opengles.GL11;
import eagle.math.Vector3;
import android.opengl.GLU;

/**
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
public class Camera	extends	IGLResource
{
	/**
	 * カメラ位置。
	 */
	private	Vector3			pos		= new Vector3();
	/**
	 * カメラ参照。
	 */
	private	Vector3			look	= new Vector3();
	/**
	 * カメラ上面。
	 */
	private	Vector3			upper	= new Vector3( 0.0f, 1.0f, 0.0f );
	/**
	 * Y方向画角。
	 */
	private	float				fovY	= 1.0f;
	/**
	 * アスペクト比。
	 */
	private	float				aspect	= 1.0f;
	/**
	 * ニアクリップ。
	 */
	private	float				nearClip	= 0.1f;
	/**
	 * ファークリップ。
	 */
	private	float				farClip		= 100.0f;

	/**
	 * GLで管理するカメラ。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	Camera( )
	{

	}

	/**
	 * パース情報を指定する。
	 * @author eagle.sakura
	 * @param fovY
	 * @param displayW
	 * @param displayH
	 * @param near
	 * @param far
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		setPerseData( float fovY, int displayW, int displayH, float near, float far )
	{
		nearClip	= near;
		farClip		= far;
		aspect		=	( (float)displayW ) / ( (float)displayH );
		this.fovY	= fovY;
	}

	/**
	 * 位置を設定する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		setPos( float x, float y, float z )
	{
		pos.set( x, y, z );
	}


	/**
	 * 位置ベクトルの参照を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/19 : 新規作成
	 */
	public	Vector3	getPos( )
	{
		return	pos;
	}

	/**
	 * 参照点を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	Vector3	getLook( )
	{
		return	look;
	}

	/**
	 * 上方向ベクトルを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	Vector3	getUpper( )
	{
		return	upper;
	}

	/**
	 * 参照を設定する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		setLook( float x, float y, float z )
	{
		look.set( x, y, z );
	}

	/**
	 * 上を指定する。
	 * @author eagle.sakura
	 * @param x
	 * @param y
	 * @param z
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		setUpper( float x, float y, float z )
	{
		upper.set( x, y, z );
	}

	/**
	 * パースデータを転送する。
	 * @author eagle.sakura
	 * @param glMgr
	 * @version 2009/11/15 : 新規作成
	 */
	public	void	toDevicePerse( GLManager glMgr )
	{
		GL11	gl = glMgr.getGL();
		gl.glMatrixMode( GL11.GL_PROJECTION );
		gl.glLoadIdentity();
		GLU.gluPerspective( gl, fovY, aspect, nearClip, farClip );
	}

	/**
	 * リソースをデバイスに転送する。
	 * @author eagle.sakura
	 * @param gl
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	bind( GLManager glMgr )
	{
		GL11	gl = glMgr.getGL();
		gl.glMatrixMode( GL11.GL_MODELVIEW );
		gl.glLoadIdentity();
		GLU.gluLookAt( gl, pos.x, pos.y, pos.z, look.x, look.y, look.z, upper.x, upper.y, upper.z );
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

	}

}
