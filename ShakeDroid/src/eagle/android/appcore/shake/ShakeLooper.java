/**
 *
 * @author eagle.sakura
 * @version 2010/05/13 : 新規作成
 */
package eagle.android.appcore.shake;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.microedition.khronos.opengles.GL11;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Config;
import android.widget.Toast;
import eagle.android.app.appinfo.AppInfomation;
import eagle.android.app.shake.R;
import eagle.android.app.shake.SharedData;
import eagle.android.device.SensorDevice;
import eagle.android.device.TouchDisplay;
import eagle.android.gles11.BmpTexture;
import eagle.android.gles11.GLManager;
import eagle.android.gles11.ITexture;
import eagle.android.gles11.IndexBufferSW;
import eagle.android.gles11.VertexBufferSW;
import eagle.android.gles11.BmpTexture;
import eagle.android.gles11.GLManager;
import eagle.android.gles11.ITexture;
import eagle.android.gles11.IndexBufferSW;
import eagle.android.gles11.VertexBufferSW;
import eagle.android.graphic.Graphics;
import eagle.android.math.Matrix4x4;
import eagle.android.math.Matrix4x4;
import eagle.android.thread.ILooper;
import eagle.android.util.UtilActivity;
import eagle.android.view.OpenGLView;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.math.Vector2;
import eagle.math.Vector3;
import eagle.util.EagleException;
import eagle.util.EagleUtil;
import eagle.util.FrameController;

/**
 * @author eagle.sakura
 * @version 2010/05/13 : 新規作成
 */
public class ShakeLooper extends ILooper
{
	private ITexture texture = null;
	private Context context = null;
	private ShakeVertices vertices = null;
//	private FrameController frames = null;
	private ShakeInitialize shakeData = null;
	private Option option = null;
	private SensorDevice sensor = null;
	private GLManager glManager = null;
	private	boolean		weightLock	=	false;

	/**
	 * 上側のフリースペース。<BR>
	 * 指定以上のピクセル数を確保出来る場合、adviewを生成する。
	 */
	private	int		upperFreePixel	=	-1;

	/**
	 * サムネイル用画像。
	 */
	private Bitmap thumbnail = null;

	/**
	 *
	 * @author eagle.sakura
	 * @param holder
	 * @version 2010/05/13 : 新規作成
	 */
	public ShakeLooper(Context act, GLManager gl, ShakeInitialize data)
	{
		context = act;
		shakeData = data;
		option = data.option;
		glManager = gl;
		sensor = new SensorDevice(act, SensorDevice.eSensorTypeAccel,
				SensorDevice.eSensorDelayFastest);

		if(context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			sensor.setDeviceDirection(SensorDevice.eDirectionTypeHorizontal);
		}
		else
		{
			sensor.setDeviceDirection(SensorDevice.eDirectionTypeVertical);
		}
	}

	/**
	 * 初期化用データを取得する。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public ShakeInitialize getInitializeData()
	{
		return shakeData;
	}

	/**
	 * 設定情報を取得する。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/17 : 新規作成
	 */
	public Option getOption()
	{
		return option;
	}

	/**
	 * 頂点配列を取得する。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public ShakeVertices getVertices()
	{
		return vertices;
	}

	/**
	 * 設定したウェイトを無効化する。
	 *
	 * @author eagle.sakura
	 * @version 2010/05/17 : 新規作成
	 */
	public void reset()
	{
		vertices.resetWeights();
	}

	/**
	 * 頂点ウェイトのロック状態を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/13 : 新規作成
	 */
	public boolean isWeightLock()
	{
		return weightLock;
	}

	/**
	 * 影響度のロックを設定する。
	 * @author eagle.sakura
	 * @param weightLock
	 * @version 2010/06/13 : 新規作成
	 */
	public void setWeightLock(boolean weightLock)
	{
		this.weightLock = weightLock;
	}

	/**
	 * @author eagle.sakura
	 * @version 2010/05/13 : 新規作成
	 */
	@Override
	public void onFinalize()
	{
		// TODO 自動生成されたメソッド・スタブ
		if(texture != null)
		{
			texture.unbind( );
			texture.dispose();
			texture = null;
		}

		EagleUtil.log( "OpenGL dispose..." );
		glManager.dispose();
		glManager = null;
	}

	/**
	 * 上側のフリースペースピクセル数を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	int	getUpperFreePixel( )
	{
		return	upperFreePixel;
	}

	private	Face	mainFace	=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/05/21 : 新規作成
	 */
	private void createTexture()
	{
		Bitmap texSrc = null;
		Bitmap origin = null;
		// ! 元となるテクスチャを読み出す。

		if( getInitializeData().bmp == null )
		{
			try
			{
				EagleUtil.log( "image open start" );
				origin = MediaStore.Images.Media.getBitmap(context.getContentResolver(), shakeData.uri);
			}
			catch (FileNotFoundException fnfe)
			{
				EagleUtil.log(fnfe);
				return;
			}
			catch (IOException ioe)
			{
				EagleUtil.log(ioe);
				return;
			}
			catch (OutOfMemoryError oome)
			{
				EagleUtil.log(oome.toString());
				return;
			}
			catch (Exception e)
			{
				EagleUtil.log(e);
				return;
			}
		}
		else
		{
			origin = getInitializeData().bmp;


		}

		if(origin == null)
		{
			return;
		}

		EagleUtil.log( "complete image open" );
		EagleUtil.log( "glManager : " + glManager );
		int displayW = glManager.getDisplayWidth(),
			displayH = glManager.getDisplayHeight();
		int	texWidth	= 2,
			texHeight	= 2;

		//!	テクスチャサイズを２のｎ乗で最小に収める。
		while( texWidth < displayW )
		{
			texWidth *= 2;
		}

		while( texHeight < displayH )
		{
			texHeight *= 2;
		}

//		displayW = 320;
//		displayH = 480;

		EagleUtil.log( "DisplaySize : " + displayW + " x " + displayH );
		EagleUtil.log( "TextureSize : " + texWidth + " x " + texHeight );
		// ! テクスチャ用画像を再生成
		{
			Bitmap target =  null;


			try
			{
				target =	Bitmap.createBitmap(	texWidth, texHeight,
													android.graphics.Bitmap.Config.RGB_565
													);
			}
			catch( OutOfMemoryError oome )
			{
				EagleUtil.log( oome.toString() );
				texWidth	<<= 1;
				texHeight	<<= 1;
				target =	Bitmap.createBitmap(	texWidth, texHeight,
													android.graphics.Bitmap.Config.RGB_565
													);
			}
			Canvas canvas = new Canvas(target);
			Graphics graphics = new Graphics();
			graphics.setCanvas(canvas);

			graphics.clearRGBA(0, 0, 0, 255);
			int imageWidth = origin.getWidth(),
				imageHeight = origin.getHeight();

			int outWidth = 0, outHeight = 0;

			if(imageWidth < imageHeight)
			{
				// ! 縦に合わせる
				outHeight = displayH;
				outWidth = imageWidth * outHeight / imageHeight;
			}
			else
			{
				// ! 横に合わせる
				outWidth = displayW;
				outHeight = imageHeight * outWidth / imageWidth;
			}

			int x = (displayW - outWidth) / 2, y = (displayH - outHeight) / 2;

			origin =	Bitmap.createScaledBitmap(	 origin, outWidth, outHeight, true );

			graphics.getPaint().setAntiAlias( true );
			graphics.drawBitmap( origin, x, y );
			upperFreePixel	=	y;

			if( getInitializeData().bmp != null )
			{
				AppInfomation		info = new AppInfomation();

				//! 無料版なら塗りつぶす
				if( ! info.isSharewareMode() )
				{
					graphics.setColorARGB( 180, 0, 0, 0  );
					graphics.fillRect( 0, 0, graphics.getWidth(), graphics.getHeight() );
					graphics.setColorARGB( 255, 255, 255, 255 );

					int	width 	= glManager.getDisplayWidth() / 2,
						_height	= glManager.getDisplayHeight() / 2;

					String[]	strings = context.getResources().getStringArray( R.array.trimming_sample_helps );
					graphics.drawString(	strings[ 0 ],
											width, _height, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );

					int	height = graphics.getStringHeight( strings[ 0 ] ) + 3;
					graphics.drawString(	strings[ 1 ],
											width, _height + 25 + height * 1, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );
					graphics.drawString(	strings[ 2 ],
											width, _height + 25 + height * 2, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );
				}
			}


			// ! サムネイル画像を作成する。
			try
			{
			//	thumbnail = Bitmap.createScaledBitmap(texSrc, 64, 64, true);
				thumbnail = Bitmap.createBitmap( 64, 64, android.graphics.Bitmap.Config.ARGB_8888 );
				Graphics g = new Graphics();
				g.setCanvas( new Canvas( thumbnail ) );

				int	size = Math.min( origin.getWidth(), origin.getHeight() ) >> 1;
				int	xCenter = origin.getWidth() >> 1,
					yCenter = origin.getHeight() >> 1;

				g.drawBitmap(	origin,
							new	Rect( xCenter - size, yCenter - size, xCenter + size, yCenter + size ),
							new Rect( 0, 0, thumbnail.getWidth(), thumbnail.getHeight() )
								);

			}
			catch (OutOfMemoryError oome)
			{

			}
			origin = null;
			texSrc = target;
			EagleUtil.log( "tex image complete" );
		}

		if( getInitializeData().isFaceDetect
		&&	getInitializeData().weights == null
		)
		{
			//!	顔認識する
			Face[]	faces = new Face[ 1 ];
			FaceDetector	fd = new	FaceDetector( texSrc.getWidth(), texSrc.getHeight(), faces.length );
			int	faceNum =	fd.findFaces( texSrc, faces );

			Graphics	graphics = new Graphics();
			graphics.setCanvas( new Canvas( texSrc ) );

			mainFace = faces[ 0 ];

			/*
			if( faceNum > 0 )
			{
				Toast.makeText( context, context.getResources().getString( R.string.toast_hit_faces ), Toast.LENGTH_LONG ).show( );
			}
			else
			{
				Toast.makeText( context, context.getResources().getString( R.string.toast_nohit_face ), Toast.LENGTH_LONG ).show( );
			}
			*/


			/*
			*/
			if( ! ( new AppInfomation() ).isSharewareMode() )
			{
				for( int i = 0; i < faceNum; ++i )
				{
					Face face = faces[ i ];
					PointF	center = new PointF();
					face.getMidPoint( center );
					float	eye = face.eyesDistance();
					center.y += ( eye * 4.5f );
					float	w = ( eye * 4.0f ),
							h = ( eye * 2.75f );
					graphics.setColorARGB( 255, 255, 0, 0 );
					graphics.drawRect( 	( int )( center.x - ( w/2 ) ),
										( int )( center.y - ( h/2 ) ),
										( int )w,
										( int )h
									);
					graphics.setColorARGB( 128, 255, 255, 255 );
					graphics.fillRect( 	( int )( center.x - ( w/2 ) ),
										( int )( center.y - ( h/2 ) ),
										( int )w,
										( int )h
									);

					graphics.setColorARGB( 255, 0, 0, 0 );
					graphics.setFontSize( 12 );
					String[]	strings = context.getResources().getStringArray( R.array.face_hit_helps );
					graphics.drawString(	strings[ 0 ],
											( int )center.x, ( int )center.y, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );

					int	height = graphics.getStringHeight( strings[ 0 ] ) + 3;
					graphics.drawString(	strings[ 1 ],
											( int )center.x, ( int )center.y + 25 + height * 1, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );
					graphics.drawString(	strings[ 2 ],
											( int )center.x, ( int )center.y + 25 + height * 2, -1, -1,
											Graphics.eStringFlagXCenter | Graphics.eStringFlagYCenter );
				}
			}
		}


		// ! テクスチャ作成
		{
			EagleUtil.log( "tex create start..." );
			// texture = new Texture2D( glManager );
			// texture.init( bmp );
			texture = new BmpTexture(texSrc, glManager);
			texture.bind();

		}
	}


	/**
	 * サムネイル画像を取得する。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public Bitmap getThumbnailImage()
	{
		return thumbnail;
	}

	/**
	 * 重み配列を取得する。<BR>
	 * 新しいバッファに確保されるため、戻り値を変更しても内部の値に変更はない。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/05 : 新規作成
	 */
	public	float[]	getWeightArray( )
	{
		return	vertices.getWeightArray();
	}

	/**
	 * 初期化を行う。
	 *
	 * @author eagle.sakura
	 * @version 2010/05/31 : 新規作成
	 */
	@Override
	public void onInitialize()
	{
		// ! GL初期化
		glManager.initGL();

		if(shakeData.xDivision <= 0)
		{
			shakeData.xDivision = glManager.getDisplayWidth() / 20 - 1;
		}
		if(shakeData.yDivision <= 0)
		{
			shakeData.yDivision = glManager.getDisplayHeight() / 20 - 1;
		}

		vertices = new ShakeVertices(glManager, shakeData.xDivision,
									shakeData.yDivision);

		if( shakeData.weights != null )
		{
			vertices.deserialize( shakeData.weights );
			shakeData.weights = null;
		}

//		frames = new FrameController(15);
		// 行列を作成
		{
			Matrix4x4 m = Matrix4x4.create(new Vector3(2.0f, 2.0f, 2.0f),
					new Vector3(), new Vector3(-1.0f, -1.0f, 0.0f),
					// new Vector3( -( float )EagleUtil.getRand( 0,
					// EagleUtil.eGLFixed1_0 ) / ( float )EagleUtil.eGLFixed1_0,
					// 1.0f, 0.0f ),
					new Matrix4x4());

			glManager.pushMatrixF(m);
		}

		// TODO 自動生成されたメソッド・スタブ
		createTexture();

		// ! 振動コントロール
		// shakeController = createShakeController();
		onOptionChange( getOption(), null );

		//!	頂点設定
		if( mainFace != null )
		{
			PointF	center = new PointF();
			mainFace.getMidPoint( center );
			float	eye = mainFace.eyesDistance();
				center.y += ( eye * 4.5f );
				float	w = ( eye * 4.0f ),
						h = ( eye * 2.75f );

			float	left 	= center.x - ( w / 2 ),
					right	= center.x + ( w / 2 ),
					top		= center.y - ( h / 2 ),
					bottom	= center.y + ( h / 2 );

			float	addX = 15.0f,
					addY = 10.0f;
			float	addWeight = getOption().getTouchWeightAdd();
		//	getOption().setTouchWeightAdd( addWeight / 4 );

			Vector2	p0 = new Vector2( center.x, center.y ),
					p1 = new Vector2( left, top );
			float	lenMax = p0.length( p1 );

			for( float px = left; px < ( right + addX ); px += addX )
			{
				for( float py = top; py < ( bottom + addY ); py += addY )
				{
					float touchX = px	/ (float) glManager.getDisplayWidth();
					float touchY = py 	/ (float) glManager.getDisplayHeight();

					p1.set( px, py );
					float	len = p0.length( p1 );
					option.setTouchWeightAdd( 2 * addWeight * ( 1.0f -(  len / lenMax ) ) );


					vertices.onTouch(getOption(), touchX, touchY);
				}
			}
			getOption().setTouchWeightAdd( addWeight );


		}

		// ! メモリ整理
		System.gc();
	}

	/**
	 * オプション情報を変更する。
	 *
	 * @author eagle.sakura
	 * @param option
	 * @version 2010/05/22 : 新規作成
	 */
	public void onOptionChange( Option option, SharedData sd )
	{
		shakeController = createShakeController();
		//!	仕様変更。
		//!	常にロック状態。任意で変更する。

		// ! 強制ロックがかかっているか
		if(option.isOrientationLock())
		{
		//	UtilActivity.setOrientationFixed(context, true);
		}
		else
		{
			// ! 縦横の設定
		//	UtilActivity.setOrientationFixed(context, !shakeController.isRotateEnable());
		}
	}

	/**
	 * 振動コントローラ作成。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/21 : 新規作成
	 */
	private IShakeController createShakeController()
	{
		switch (getOption().getShakeType())
		{
		case Option.eShakeTypeAlways:
			return new ShakeControllerAlways(); // !< 常に揺らすコントローラ
		case Option.eShakeTypeSlide:
			return new ShakeControllerSlide(); // !< 指でスライドさせるコントローラ
		case Option.eShakeTypeAccel:
			return new ShakeControllerAccel(); // !< 加速度を利用したコントローラ
		}
		return null;
	}

	/**
	 * 振動コントロール。
	 */
	private IShakeController shakeController = null;

	/**
	 * センサを取得する。
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public SensorDevice getSensor()
	{
		return sensor;
	}

	/**
	 * @author eagle.sakura
	 * @version 2010/05/13 : 新規作成
	 */
	@Override
	public void onLoop()
	{
	//	EagleUtil.log( "Loop..." );
		// TODO 自動生成されたメソッド・スタブ
		getTouchDisplay().update();
		GLManager glMgr = glManager;

		GL11 gl = glMgr.getGL11();

		if(texture != null)
		{
			glMgr.clearColorRGBA(0, 0, 0, 255);
		}
		else
		{
			glMgr.clearColorRGBA(255, 0, 0, 255);
		}
		glMgr.clear();

		if(texture == null)
		{
			glMgr.swapBuffers();
		//	frames.update();
			return;
		}

		// ! タッチを通知する。
		if(	getTouchDisplay().isTouch()								//!<	タッチされている
		&&	!shakeController.isLockVertexWeight(getOption())		//!<	コントローラ側からロックされていない
		&&	!isWeightLock()											//!<	全体ロックもされていない
		)
		{
			float touchX = (float) getTouchDisplay().getTouchPosX()		/ (float) glMgr.getDisplayWidth();
			float touchY = (float) getTouchDisplay().getTouchPosY() 	/ (float) glMgr.getDisplayHeight();

			vertices.onTouch(getOption(), touchX, touchY);
		}

		// ! センサの更新を行う。
		{
			sensor.update();
		}

		// ! 振動の更新を行う。
		{
			shakeController.update(this);
		}

		{
			// ! バッファを更新
			vertices.update(getOption(), shakeController);
			// ! バッファを描画
			vertices.draw(getOption());
		}

		glMgr.swapBuffers();

	//	frames.update();
	}

	/**
	 *
	 */
	public	static	final	String		eLastSaveFile = Environment.getExternalStorageDirectory().getPath() + "/shakedroid/last.dat";

	/**
	 *
	 * @author eagle.sakura
	 * @param looper
	 * @version 2010/05/31 : 新規作成
	 */
	public	static	void	writeLastFile( ShakeLooper looper )
	{
		//!	ファイル保存テスト
		try
		{
			DataOutputStream dos = new DataOutputStream( new OutputStreamBufferWriter( new FileOutputStream( eLastSaveFile ) ) );

		//	ShakeDataFile	sdf = new ShakeDataFile();
		//	sdf.serialize( looper, dos );

			dos.dispose();
		}
		catch( Exception e )
		{
			EagleUtil.log( e );
		}

	}

	/**
	 *
	 * @author eagle.sakura
	 * @param context
	 * @param gl
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public	static	ShakeLooper	readLastFile( Context context, GLManager gl )
	{
    	//!	ファイル保存テスト
    	try
    	{
    		DataInputStream dis = new DataInputStream( new InputStreamBufferReader( new FileInputStream( eLastSaveFile ) ) );

    	//	ShakeDataFile	sdf			= new ShakeDataFile();
    	//	ShakeLooper		shakeLooper =	sdf.deserialize( context, gl, dis );
    		dis.dispose();

    	//	return	shakeLooper;
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}
    	return	null;
	}
}
