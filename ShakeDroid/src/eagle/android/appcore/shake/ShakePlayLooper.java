/**
 *
 * @author eagle.sakura
 * @version 2010/08/25 : 新規作成
 */
package eagle.android.appcore.shake;

import java.io.FileNotFoundException;
import java.io.IOException;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.media.FaceDetector;
import android.media.FaceDetector.Face;
import android.provider.MediaStore;
import eagle.android.app.appinfo.AppInfomation;
import eagle.android.app.shake.R;
import eagle.android.device.SensorDevice;
import eagle.android.gles11.BmpTexture;
import eagle.android.gles11.GLManager;
import eagle.android.graphic.Graphics;
import eagle.android.math.Matrix4x4;
import eagle.math.Vector2;
import eagle.math.Vector3;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/08/25 : 新規作成
 */
public class ShakePlayLooper extends ShakeLooper
{
	/**
	 * インポートしたファイル情報。
	 */
	byte[]			originFile;

	int				imageWidth	= 0;
	int				imageHeight	= 0;

	/**
	 * 画像データからのImport専用プレイヤー。
	 * @author eagle.sakura
	 * @param holder
	 * @version 2010/05/13 : 新規作成
	 */
	public ShakePlayLooper( byte[] origin, Context act, GLManager gl, ShakeInitialize data)
	{
		super( act, gl, data );
		originFile = origin;
	}

	@Override
	public	boolean	isPlayingOnly( )
	{
		return	true;
	}
	/**
	 * 描画用のテクスチャを生成し、バインドする。
	 * @author eagle.sakura
	 * @version 2010/08/25 : 新規作成
	 */
	@Override
	protected void createTexture()
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

		if(origin == null)
		{
			return;
		}

		EagleUtil.log( "complete image open play" );
		EagleUtil.log( "glManager : " + glManager );
		EagleUtil.log( "OriginBMP Size : " + origin.getWidth() + " x " + origin.getHeight() );
		int displayW = glManager.getDisplayWidth(),
			displayH = glManager.getDisplayHeight();

		//!	ディスプレイの大きさは画像の大きさ。
		{
			displayW = origin.getWidth();
			displayH = origin.getHeight();
		}

		imageWidth 	= displayW;
		imageHeight	= displayH;

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
			target =	Bitmap.createScaledBitmap(	 origin, texWidth, texHeight, true );
			texSrc = target;
			EagleUtil.log( "tex image complete" );
		}

		// ! テクスチャ作成
		{
			EagleUtil.log( "tex create start..." );
			texture = new BmpTexture(texSrc, glManager);
			texture.bind();
			textureOrigin = texSrc;
		}
	}

	/**
	 * 初期化を行う。
	 * @author eagle.sakura
	 * @version 2010/08/26 : 新規作成
	 */
	@Override
	public void onInitialize()
	{
		// ! GL初期化
		glManager.initGL();


		try
		{
			//!	再生用ファイルをデシリアライズ
			ShakeDataFile	sdf = new ShakeDataFile( this.originFile );
			sdf.getInitData().option = getInitializeData().option;
			sdf.deserialize();
		//	shakeData = getInitializeData();

			shakeData.xDivision = sdf.getInitData().xDivision;
			shakeData.yDivision = sdf.getInitData().yDivision;
			shakeData.weights	= sdf.getWeightTable();

		}
		catch( Exception e )
		{
		}

		if(shakeData.xDivision <= 0)
		{
			shakeData.xDivision = glManager.getDisplayWidth() / 20 - 1;
		}
		if(shakeData.yDivision <= 0)
		{
			shakeData.yDivision = glManager.getDisplayHeight() / 20 - 1;
		}

		/*
		vertices = new ShakeVertices(	glManager,
										shakeData.xDivision,
										shakeData.yDivision);
										*/
		vertices = new ShakeVertices();
		vertices.initNoScaled( glManager, shakeData.xDivision, shakeData.yDivision );

		if( shakeData.weights != null )
		{
			vertices.deserialize( shakeData.weights );
			shakeData.weights = null;
		}

//		frames = new FrameController(15);

		// TODO 自動生成されたメソッド・スタブ
		createTexture();

		//!
		{
			float		srcWidth 	= ( float )imageWidth,
						srcHeight	= ( float )imageHeight,
						dstWidth	= ( float )glManager.getDisplayWidth(),
						dstHeight	= ( float )glManager.getDisplayHeight();

			//!	アスペクト比を合わせる。
			float		srcAspect = srcWidth / srcHeight,
						dstAspect = dstWidth / dstHeight;

			EagleUtil.log("srcAspect : " + srcAspect );
			EagleUtil.log("dstAspect : " + dstAspect );

			if( srcAspect > dstAspect )
			{
				EagleUtil.log( "srcAspect > dstAspect" );
				Matrix4x4	m = new Matrix4x4();
				m.scale( 1.0f, dstAspect / srcAspect, 1.0f );
				glManager.pushMatrixF( m );
			}
			else if( srcAspect < dstAspect )
			{
				EagleUtil.log( "srcAspect < dstAspect" );
				Matrix4x4	m = new Matrix4x4();
				m.scale( srcAspect / dstAspect, 1.0f, 1.0f );
				glManager.pushMatrixF( m );
			}

		}

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


		// ! 振動コントロール
		// shakeController = createShakeController();
		onOptionChange( getOption(), null );

		// ! メモリ整理
		System.gc();
	}

}
