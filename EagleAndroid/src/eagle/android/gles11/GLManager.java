/**
 * OpenGL管理を行う。<BR>
 * 将来的にはデバイス非依存とする。
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
package eagle.android.gles11;

import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGL11;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.egl.EGLSurface;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import eagle.android.math.Matrix4x4;
import eagle.android.util.UtilActivity;
import eagle.util.EagleUtil;

import android.graphics.PixelFormat;
import android.view.SurfaceHolder;

/**
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
public class GLManager
{
	/**
	 * 管理しているサーフェイス。
	 */
	private		SurfaceHolder		holder		= null;
	/**
	 * GL10本体。
	 */
	private		EGL10				egl			= null;
	/**
	 * GL本体。
	 */
	private		GL10				gl10		= null;

	/**
	 * GL本体。
	 */
	private		GL11				gl11			= null;
	private		GL11Ext				gl11Ext			= null;
	private		GL11ExtensionPack	gl11ExtPack		= null;
	private		GL11Extension		gl11Extension	= null;

	/**
	 * GLコンテキスト。
	 */
	private		EGLContext			glContext	= null;
	/**
	 * ディスプレイ。
	 */
	private		EGLDisplay			glDisplay	= null;
	/**
	 * サーフェイス。
	 */
	private		EGLSurface			glSurface	= null;

	/**
	 * コンフィグ情報。
	 */
	private		EGLConfig			glConfig	= null;

	/**
	 * 描画対象の幅。
	 */
	private		int					surfaceWidth	=	-1;

	/**
	 * 描画対象の高さ。
	 */
	private		int					surfaceHeight	=	-1;

	/**
	 * GL11エクステンション機能を有効にするか。
	 */
	private	static	boolean			isGL11ExtentionInitialize	=	true;

	/**
	 * GL描画用スレッドを作成する。
	 * @author eagle.sakura
	 * @param holder
	 * @version 2009/11/14 : 新規作成
	 */
	public	GLManager(  )
	{
	}

	/**
	 * GL11エクステンションの有効・無効を指定する。<BR>
	 * デフォルトは有効。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/09/12 : 新規作成
	 */
	public	static	void		setGL11ExtentionEnable( boolean set )
	{
		isGL11ExtentionInitialize = set;
	}

	/**
	 * GLSurfaceViewを使用する。
	 * @author eagle.sakura
	 * @param holder
	 * @param gl
	 * @param config
	 * @version 2009/11/19 : 新規作成
	 */
	public	GLManager( SurfaceHolder	holder,
						GL10			gl,
						EGLConfig		config	)
	{
		this.holder		= holder;
		gl10			= gl;
		gl11			= ( GL11 )gl;
		gl11Ext			= ( GL11Ext )gl;
		gl11ExtPack		= ( GL11ExtensionPack )gl;
		glConfig		= config;

		_setDefGLStatus();
	}

	/**
	 * レンダリングターゲットの大きさを設定する。
	 * @author eagle.sakura
	 * @param width
	 * @param height
	 * @version 2010/06/02 : 新規作成
	 */
	public	void		setSurfaceSize( int width, int height )
	{
		surfaceWidth	= width;
		surfaceHeight	= height;
	}

	/**
	 * GLを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/14 : 新規作成
	 */
	public	GL10		getGL10( )
	{
		return	gl10;
		//	return
	}

	/**
	 * GLを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/16 : 新規作成
	 */
	public	GL11		getGL11( )
	{
		return	gl11;
	}

	/**
	 * GLを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/16 : 新規作成
	 */
	public	GL11Ext		getGL11Ext( )
	{
		return	gl11Ext;
	}

	/**
	 * GL11用ブリッジを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/10 : 新規作成
	 */
	public	GL11Extension	getGL11Extension( )
	{
		return	gl11Extension;
	}

	/**
	 * GLを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/16 : 新規作成
	 */
	public	GL11ExtensionPack	getGL11ExtPack( )
	{
		return	gl11ExtPack;
	}

	/**
	 * メインで使用するGLインターフェースを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/16 : 新規作成
	 */
	public	GL11				getGL( )
	{
		return	gl11;
	}

	/**
	 * EGLを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/14 : 新規作成
	 */
	public	EGL10		getEGL( )
	{
		return	egl;
	}

	/**
	 * バッファの消去を行う。
	 * @author eagle.sakura
	 * @param mask
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	clear( int mask )
	{
		gl11.glClear( mask );
	}

	/**
	 * バッファの消去を行う。
	 * @author eagle.sakura
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	clear( )
	{
		gl11.glClear( GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BITS );
	}

	/**
	 * サーフェイスチェック用のテスト描画を行う。
	 * @author eagle.sakura
	 * @version 2010/06/02 : 新規作成
	 */
	public	void	clearTest( )
	{
		clearColorRGBA( 255, 0, 0, 255 );
		clear();
		swapBuffers();
	}

	/**
	 * 消去色を設定する。
	 * @author eagle.sakura
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	clearColorRGBA( float r, float g, float b, float a )
	{
		gl11.glClearColor( r, g, b, a );
	}

	/**
	 * 消去色を指定する。
	 * 値は0～255で指定。
	 * @author eagle.sakura
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @version 2009/11/29 : 新規作成
	 */
	public	void	clearColorRGBA( int r, int g, int b, int a )
	{
		gl11.glClearColorx(	( r & 0xff ) << 8,
							( g & 0xff ) << 8,
							( b & 0xff ) << 8,
							( a & 0xff ) << 8 );
	}

	/**
	 * 指定した４ｘ４行列をプッシュする。
	 * @author eagle.sakura
	 * @param trans
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		pushMatrixF( Matrix4x4 trans )
	{
		gl11.glPushMatrix();
		gl11.glMultMatrixf( trans.m, 0 );
	}

	/**
	 * 行列を取り出す。
	 * @author eagle.sakura
	 * @version 2009/11/15 : 新規作成
	 */
	public	void		popMatrix( )
	{
		gl11.glPopMatrix();
	}

	/**
	 * バックバッファをフロントバッファに送る。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void		swapBuffers( )
	{
		//画面に出力するバッファの切り替え
		egl.eglSwapBuffers( glDisplay, glSurface );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param holder
	 * @version 2010/05/31 : 新規作成
	 */
	public	void		setSurfaceHolder( SurfaceHolder holder )
	{
		EagleUtil.log( "" +  holder );
		EagleUtil.log( "" +  this.holder );
		this.holder = holder;
	}

	/**
	 * サーフェイスを返す。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/23 : 新規作成
	 */
	public	SurfaceHolder	getSurfaceHolder( )
	{
		return	holder;
	}

	/**
	 * 初期化時のコンフィグスペック。
	 */
	private	int[]		configSpec =
	{
		/**
		 * 2008/12/1 修正
		 * 以下の設定が実機では使えないようなのでカット。
		 * この部分をはずすと、サポートされている設定が使われる(明示的に設定しないと機種依存で変わる可能性あり?)。
		 *
		EGL10.EGL_RED_SIZE,		5,	//!	赤要素：8ビット
		EGL10.EGL_GREEN_SIZE,	6,	//!	緑要素：8ビット
		EGL10.EGL_BLUE_SIZE,	5,	//!	青要素：8ビット
	//	EGL10.EGL_ALPHA_SIZE,	8,	//!	アルファチャンネル：8ビット
		EGL10.EGL_DEPTH_SIZE,	16,	//!	深度バッファ：16ビット
		*/
		EGL10.EGL_NONE				//!	終端にはEGL_NONEを入れる
	};

	/**
	 * 初期化設定を入れ替える。
	 * @author eagle.sakura
	 * @param specs
	 * @version 2010/07/27 : 新規作成
	 */
	public	void		setConfigSpec( int[] specs )
	{
		configSpec = specs;
	}

	/**
	 * GL系を初期化する。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public	void	initGL(  )
	{
		//GL ES操作モジュール取得
		egl = ( EGL10 )EGLContext.getEGL();
		EagleUtil.log( "egl : " + egl );
	//	EagleUtil.log( "eglGetCurrentContext : " +  egl.eglGetCurrentContext() );
	//	EagleUtil.log( "eglGetCurrentDisplay : " +  egl.eglGetCurrentDisplay() );
		{
			//ディスプレイコネクション作成
			glDisplay = egl.eglGetDisplay( EGL10.EGL_DEFAULT_DISPLAY );
			EagleUtil.log( "glDisplay : " + glDisplay );
			EagleUtil.log( "ERROR : " + egl.eglGetError() );
			if( glDisplay == EGL10.EGL_NO_DISPLAY )
			{
				return;
			}

			//ディスプレイコネクション初期化
			int[] version =  { -1, -1 };
			if( !egl.eglInitialize( glDisplay, version ) )
			{
				EagleUtil.log( "eglInitialize error" );
			//	Log.e(mName, "ディスプレイコネクション初期化失敗");
				return;
			}
			//OpenGLバージョン出力
			EagleUtil.log("OpenGL ES Version[" + version[0] + "." + version[1] + "]" );
		}

		{
			//コンフィグ設定
			EGLConfig[] configs = new EGLConfig[ 1 ];
			int[] numConfigs = new int[ 1 ];
			if( !egl.eglChooseConfig(glDisplay, configSpec, configs, 1, numConfigs ) )
			{
			//	Log.e(mName, "コンフィグ設定失敗");
				return;
			}
			glConfig = configs[0];
			EagleUtil.log( "glConfig : " + glConfig );
			EagleUtil.log( "ERROR : " + egl.eglGetError() );

			if( glConfig != null )
			{
				int[] value = new int[ 1 ];
				EagleUtil.log( "LogStart..." );
				EagleUtil.log( "Message" );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_RED_SIZE, value );
				EagleUtil.log( "EGL_RED_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_GREEN_SIZE, value );
				EagleUtil.log( "EGL_GREEN_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_BLUE_SIZE, value );
				EagleUtil.log( "EGL_BLUE_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_ALPHA_SIZE, value );
				EagleUtil.log( "EGL_ALPHA_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_ALPHA_FORMAT, value );
				EagleUtil.log( "EGL_ALPHA_FORMAT : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_PIXEL_ASPECT_RATIO, value );
				EagleUtil.log( "EGL_PIXEL_ASPECT_RATIO : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_ALPHA_MASK_SIZE, value );
				EagleUtil.log( "EGL_ALPHA_MASK_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_BUFFER_SIZE, value );
				EagleUtil.log( "EGL_BUFFER_SIZE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_COLOR_BUFFER_TYPE, value );
				EagleUtil.log( "EGL_COLOR_BUFFER_TYPE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_RENDERABLE_TYPE, value );

				EagleUtil.log( "EGL_RENDERABLE_TYPE : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_HEIGHT, value );
				EagleUtil.log( "EGL_HEIGHT : " + value[ 0 ] );
				egl.eglGetConfigAttrib( glDisplay, configs[ 0 ], EGL10.EGL_WIDTH, value );
				EagleUtil.log( "EGL_WIDTH : " + value[ 0 ] );
				EagleUtil.log( "LogEnd..." );
			}

		}

		{
			//レンダリングコンテキスト作成
			glContext = egl.eglCreateContext( glDisplay, glConfig, EGL10.EGL_NO_CONTEXT, null);
			EagleUtil.log( "glContext : " + glContext );
			EagleUtil.log( "ERROR : " + egl.eglGetError() );
			if( glContext == EGL10.EGL_NO_CONTEXT )
			{
				EagleUtil.log( "Create Context Error" );
			//	Log.e(mName, "レンダリングコンテキスト作成失敗");
				return;
			}

			gl10			= ( GL10 ) glContext.getGL();
			gl11			= ( GL11 ) glContext.getGL();
			gl11Ext			= ( GL11Ext ) glContext.getGL();
			gl11ExtPack		= ( GL11ExtensionPack ) glContext.getGL();

			try
			{
				//!	Extentionの生成を試す。
				EagleUtil.log( "SDK Version : " + EagleUtil.getBridge().getPlatformVersion() );
				if( isGL11ExtentionInitialize )
				{
					gl11Extension	= new GL11Extension();
				}
			}
			catch( Exception e )
			{
				EagleUtil.log( e );
			}
		}

		{
			//サーフェイス作成(あとで分けるので別メソッド)
			if( !createSurface() )
			{
				EagleUtil.log( "Surface create error..." );
				return;
			}
		}

		{
			//!	サーフェイスの大きさを自動指定
			if( surfaceWidth < 0 )
			{
				surfaceWidth	= holder.getSurfaceFrame().width();
			}


			if( surfaceHeight < 0 )
			{
				surfaceHeight	= holder.getSurfaceFrame().height();
			}
		}

		EagleUtil.log( "set default begin" );
		_setDefGLStatus();
		EagleUtil.log( "set default out" );
	}

	private	void	_setDefGLStatus( )
	{
		GL11	gl = getGL();
		//!	深度テスト有効
		gl.glEnable( GL11.GL_DEPTH_TEST );
		gl.glEnable( GL11.GL_BLEND );
		gl.glBlendFunc( GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA );

		/*
*/
		{
			int[]	buf = { -1 };
			gl.glGetIntegerv( GL11Ext.GL_MAX_PALETTE_MATRICES_OES, buf, 0 );
			EagleUtil.log( "GL_MAX_PALETTE_MATRICES_OES : " + buf[ 0 ] );

			gl.glGetIntegerv( GL11Ext.GL_MAX_VERTEX_UNITS_OES, buf, 0 );
			EagleUtil.log( "GL_MAX_VERTEX_UNITS_OES : " + buf[ 0 ] );

			gl.glGetIntegerv( GL11.GL_MAX_MODELVIEW_STACK_DEPTH, buf, 0 );
			EagleUtil.log( "GL_MAX_MODELVIEW_STACK_DEPTH : " + buf[ 0 ] );

			gl.glGetIntegerv( GL11.GL_MAX_PROJECTION_STACK_DEPTH, buf, 0 );
			EagleUtil.log( "GL_MAX_PROJECTION_STACK_DEPTH : " + buf[ 0 ] );
		}

		//!	カリング無効
		gl.glDisable( GL11.GL_CULL_FACE );

		//!	行列無効化
		gl.glMatrixMode( GL11.GL_PROJECTION );
		gl.glLoadIdentity();
		gl.glMatrixMode( GL11.GL_MODELVIEW );
		gl.glLoadIdentity();

		//!
		gl.glDisable( GL11.GL_LIGHTING );
		gl.glViewport( 0, 0, getDisplayWidth(), getDisplayHeight() );
	//	gl.glViewport( 0, 0, 320, 480 );
	//	gl.glHint( GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST );
	//	gl.glHint( GL11.GL_POLYGON_SMOOTH_HINT, GL11.GL_NICEST );
	//	gl.glDepthFunc( GL11.GL_LEQUAL );
	//	gl.glShadeModel( GL11.GL_SMOOTH );
	//	gl.glDisableClientState( GL11.GL_TEXTURE );
	//	gl.glDisableClientState( GL11.GL_TEXTURE_COORD_ARRAY );

	}

	/**
	 * 画面幅を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	int		getDisplayWidth( )
	{
		return	surfaceWidth;
	}

	/**
	 * 画面高さを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	int		getDisplayHeight( )
	{
		return	surfaceHeight;
	}

	/**
	 * ディスプレイアスペクト比（W / H)を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/09/18 : 新規作成
	 */
	public	float	getDisplayAspect( )
	{
		return	( ( float )surfaceWidth ) / ( ( float )surfaceHeight );
	}

	/**
	 * サーフェイス作成。<BR>
	 * サーフェイスを作成して、レンダリングコンテキストと結びつける
	 *
	 * @return	正常終了なら真、エラーなら偽
	 */
	private boolean createSurface()
	{
		{
			EagleUtil.log( "eglCreateWindowSurface" );
	//		if( egl.eglMakeCurrent( glDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT ) )
			{
		//		EagleUtil.log( "eglMakeCurrent boot ok" );
			}

			//サーフェイス作成
			glSurface =	egl.eglCreateWindowSurface( glDisplay, glConfig, holder, null );
			EagleUtil.log( "glSurface : " + glSurface );
			if( glSurface == EGL10.EGL_NO_SURFACE )
			{
				EagleUtil.log( "ERROR : " + egl.eglGetError() );
				EagleUtil.log( "Error eglCreateWindowSurface" );
			//	Log.e(mName, "サーフェイス作成失敗");
				return false;
			}
		}

		{
			EagleUtil.log( "eglMakeCurrent" );
			//サーフェイスとレンダリングコンテキスト結びつけ
			if( !egl.eglMakeCurrent( glDisplay,  glSurface, glSurface, glContext) )
			{
				EagleUtil.log( "ERROR : " + egl.eglGetError() );
				EagleUtil.log( "Error eglMakeCurrent" );
			//	Log.e(mName, "レンダリングコンテキストとの結びつけ失敗");
				return false;
			}
		}

		EagleUtil.log( "createSurface exit" );
		return true;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param holder
	 * @version 2010/06/10 : 新規作成
	 */
	public	void	onSurfaceChange( SurfaceHolder holder, int width, int height )
	{
		if( surfaceWidth	== width
		&&	surfaceHeight	== height )
		{
			return;
		}

		this.holder = holder;
		if( holder.getSurfaceFrame().width() > 0 )
		{
			surfaceWidth	= width;
			surfaceHeight	= height;
		}



		EagleUtil.log( "Surface : " + surfaceWidth + "x" + surfaceHeight );

		//レンダリングコンテキスト破棄
		if( glContext != null )
		{
			egl.eglDestroyContext( glDisplay, glContext );
			glContext = null;
		}
		//サーフェイス破棄
		if( glSurface != null){
			//レンダリングコンテキストとの結びつけは解除
			egl.eglMakeCurrent( glDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT );

			egl.eglDestroySurface( glDisplay, glSurface);
			glSurface = null;
		}

		createSurface();
	}

	/**
	 * GLの終了処理を行う。
	 * @author eagle.sakura
	 * @version 2009/11/14 : 新規作成
	 */
	public void 	dispose(	)
	{
		//サーフェイス破棄
		if( glSurface != null){
			//レンダリングコンテキストとの結びつけは解除
			egl.eglMakeCurrent( glDisplay, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_SURFACE, EGL10.EGL_NO_CONTEXT );

			egl.eglDestroySurface( glDisplay, glSurface);
			glSurface = null;
		}

		//レンダリングコンテキスト破棄
		if( glContext != null )
		{
			egl.eglDestroyContext( glDisplay, glContext );
			glContext = null;
		}

		//ディスプレイコネクション破棄
		if( glDisplay != null )
		{
			egl.eglTerminate( glDisplay );
			glDisplay = null;
		}
	}

	/**
	 * GL固定小数点配列に変換する。
	 * @author eagle.sakura
	 * @param array
	 * @param result
	 * @return
	 * @version 2010/06/24 : 新規作成
	 */
	public	static	int[]			toGLFixed( float[] array, int[] result  )
	{
		for( int i = 0; i < array.length; ++i )
		{
			result[ i ] = ( int )( array[ i ] * EagleUtil.eGLFixed1_0 );
		}
		return	result;
	}

	/**
	 * GL固定小数点配列に変換する。
	 * @author eagle.sakura
	 * @param array
	 * @param result
	 * @return
	 * @version 2010/06/24 : 新規作成
	 */
	public	static	IntBuffer		toGLFixed( float[] array, IntBuffer result )
	{
		for( float f : array )
		{
			result.put( ( int )( f * EagleUtil.eGLFixed1_0 ) );
		}
		return	result;
	}

	/**
	 * 単純なキャストをサポートする。
	 * @author eagle.sakura
	 * @param array
	 * @param result
	 * @return
	 * @version 2010/06/24 : 新規作成
	 */
	public	static	ShortBuffer		toShortBuffer( int[] array, ShortBuffer result )
	{
		for( int n : array )
		{
			result.put( ( short )n );
		}
		return	result;
	}

	/**
	 * カメラ関連の行列をリセットし、単位行列化する。
	 * @author eagle.sakura
	 * @version 2010/09/18 : 新規作成
	 */
	public	void	resetCamera( )
	{
		gl11.glMatrixMode( GL11.GL_PROJECTION );
		gl11.glPopMatrix();
		gl11.glLoadIdentity();
		gl11.glMatrixMode( GL11.GL_MODELVIEW );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param array
	 * @param result
	 * @return
	 * @version 2010/06/27 : 新規作成
	 */
	public	static	short[]			toShortBuffer( int[] array, short[] result )
	{
		for( int i = 0; i < array.length; ++i )
		{
			result[ i ] = ( short )array[ i ];
		}
		return	result;
	}

	/**
	 * 位置バッファ。
	 */
	private	static	final	int[]		spritePositions =
	{
		0,						0,							0,
		EagleUtil.eGLFixed1_0,	0,							0,
		0,						EagleUtil.eGLFixed1_0,		0,
		EagleUtil.eGLFixed1_0, 	EagleUtil.eGLFixed1_0,		0,
	};

	/**
	 * 色バッファ。
	 */
	private	static	final	int[]		spriteColors =
	{
		EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,
		EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,
		EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,
		EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,EagleUtil.eGLFixed1_0,


		/*
		EagleUtil.eGLFixed1_0,	0,						0,						EagleUtil.eGLFixed1_0,
		0,						EagleUtil.eGLFixed1_0,	0,						EagleUtil.eGLFixed1_0,
		0,						0,						EagleUtil.eGLFixed1_0,	EagleUtil.eGLFixed1_0,
		EagleUtil.eGLFixed1_0,	EagleUtil.eGLFixed1_0,	EagleUtil.eGLFixed1_0,	EagleUtil.eGLFixed1_0,
		*/
	};

	/**
	 * UVバッファ。
	 */
	private	static	final	int[]		spriteUVs =
	{
		0,						0,
		EagleUtil.eGLFixed1_0,	0,
		0,						EagleUtil.eGLFixed1_0,
		EagleUtil.eGLFixed1_0,	EagleUtil.eGLFixed1_0,
	};

	/**
	 * スプライト描画用のバッファ。
	 */
	private	static	VertexBufferSW	spriteVertices	= null;

	/**
	 * スプライト描画用のインデックスバッファ。<BR>
	 * 変更することはないため、転送済みにしておく。
	 */
	private	static	IndexBufferSW	spriteIndices	=	null;

	/**
	 * ２次元描画を行う。<BR>
	 * 事前にカメラ行列やモデル行列はリセットしてある必要がある。<BR>
	 * Zバッファはすべて無視される。
	 * @author eagle.sakura
	 * @param texture
	 * @param srcX
	 * @param srcY
	 * @param srcW
	 * @param srcH
	 * @param dstX
	 * @param dstY
	 * @param dstW
	 * @param dstH
	 * @param colorRGBA
	 * @version 2010/09/20 : 新規作成
	 */
	public	void	drawSprite( ITexture texture,
								int srcX, int srcY, int srcW, int srcH,
								int dstX, int dstY, int dstW, int dstH,
								int colorRGBA	)
	{
		if( spriteVertices == null )
		{
			spriteVertices = new VertexBufferSW( this );
			spriteIndices = new IndexBufferSW( this );
			spriteIndices.init( new short[]{ 0, 1, 2, 1, 2, 3, } );
		}

		//!	位置情報
	//	if( false )
		{
			float	screenW = getDisplayWidth(),
					screenH = getDisplayHeight();

			int	vx = ( int )( ( ( float )dstX / screenW ) * EagleUtil.eGLFixed1_0 ),
				vy = ( int )( ( ( float )dstY / screenH ) * EagleUtil.eGLFixed1_0 ),
				vw = ( int )( ( ( float )dstW / screenW ) * EagleUtil.eGLFixed1_0 ),
				vh = ( int )( ( ( float )dstH / screenH ) * EagleUtil.eGLFixed1_0 );

			//!	左上
			spritePositions[ 3*0 + 0 ] = vx;
			spritePositions[ 3*0 + 1 ] = EagleUtil.eGLFixed1_0 - vy;

			//!	右上
			spritePositions[ 3*1 + 0 ] = vx + vw;
			spritePositions[ 3*1 + 1 ] = EagleUtil.eGLFixed1_0 - vy;

			//!	左下
			spritePositions[ 3*2 + 0 ] = vx;
			spritePositions[ 3*2 + 1 ] = EagleUtil.eGLFixed1_0 - vy - vh;

			//!	右下
			spritePositions[ 3*3 + 0 ] = vx + vw;
			spritePositions[ 3*3 + 1 ] = EagleUtil.eGLFixed1_0 - vy - vh;

			//!	スクリーン座標系に直す
			for( int i = 0; i < 4; ++i )
			{
				spritePositions[ i * 3 + 0 ] = ( spritePositions[ i * 3 + 0 ] * 2 ) - EagleUtil.eGLFixed1_0;
				spritePositions[ i * 3 + 1 ] = ( spritePositions[ i * 3 + 1 ] * 2 ) - EagleUtil.eGLFixed1_0;
			}
		}

		//!	UV情報
		if( texture != null
	//	&& false
		)
		{
			float	texW = texture.getWidth(),
					texH = texture.getHeight();

			int	vx = ( int )( ( ( float )srcX / texW ) * EagleUtil.eGLFixed1_0 ),
				vy = ( int )( ( ( float )srcY / texH ) * EagleUtil.eGLFixed1_0 ),
				vw = ( int )( ( ( float )srcW / texW ) * EagleUtil.eGLFixed1_0 ),
				vh = ( int )( ( ( float )srcH / texH ) * EagleUtil.eGLFixed1_0 );


			//!	左上
			spriteUVs[ 2*0 + 0 ] = vx;
			spriteUVs[ 2*0 + 1 ] = vy;

			//!	右上
			spriteUVs[ 2*1 + 0 ] = vx + vw;
			spriteUVs[ 2*1 + 1 ] = vy;

			//!	左下
			spriteUVs[ 2*2 + 0 ] = vx;
			spriteUVs[ 2*2 + 1 ] = vy + vh;

			//!	右下
			spriteUVs[ 2*3 + 0 ] = vx + vw;
			spriteUVs[ 2*3 + 1 ] = vy + vh;
		}

		spriteVertices.initPosBuffer( spritePositions );
		spriteVertices.initColBuffer( spriteColors );
		spriteVertices.initUVBuffer( spriteUVs );

		GL11	gl = getGL();
		gl.glDisable( GL11.GL_DEPTH_TEST );
		gl.glDisable( GL11.GL_CULL_FACE );

		if( texture != null )
		{
			texture.bind();
		}
		spriteVertices.bind();

		spriteIndices.drawElements();

		spriteVertices.unbind();
		if( texture != null )
		{
			texture.unbind();
		}
		gl.glEnable( GL11.GL_DEPTH_TEST );
	}
}
