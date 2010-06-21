/**
 *
 * @author eagle.sakura
 * @version 2010/05/17 : 新規作成
 */
package eagle.android.appcore.shake;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleUtil;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

/**
 * @author eagle.sakura
 * @version 2010/05/17 : 新規作成
 */
public final class Option
{
	/**
	 * 影響チェックモードか。
	 */
	private		boolean			IsBlueMode			=	false;

	/**
	 * 端末を振ったときの反応感度。
	 */
	private		float			shakeSensitivity	=	1.5f;

	/**
	 * 画像の揺れやすさ。
	 */
	private		float			shakeMul			=	1.0f;

	/**
	 * 減衰値の倍率。<BR>
	 * 0.0～1.0を取り、無効にならないよう内部的に正規化する。
	 */
	private		float			weightMul			=	0.5f;

	/**
	 * タッチでのウェイト加算度。
	 */
	private		float			touchWeightAdd		=	0.25f;

	/**
	 * 左右回転のロックを行うか。
	 */
	private		boolean			orientationLock		=	true;

	/**
	 * 揺れモード。
	 */
	private		int					shakeType			=	eShakeTypeAlways;

	/**
	 * 初期化を行う。
	 * @author eagle.sakura
	 * @version 2010/05/30 : 新規作成
	 */
	public	void	reset( )
	{
		IsBlueMode			=	false;
		shakeSensitivity	=	1.5f;
		shakeMul			=	1.0f;
		weightMul			=	0.5f;
		touchWeightAdd		=	0.25f;
		orientationLock		=	true;
		shakeType			=	eShakeTypeAlways;
	}

	/**
	 * オプションのバージョン。
	 */
	public	static	final	int			eVersion	=	0x1;

	/**
	 * オプション情報をバイト配列に変換する。
	 * @author eagle.sakura
	 * @param dos
	 * @version 2010/05/28 : 新規作成
	 */
	public	void		serialize( DataOutputStream dos )
	{
		try
		{
			//!	有効化する
			dos.writeS32( eVersion );

			//!	描画モード
			dos.writeBoolean( IsBlueMode );

			//!	反応感度
			dos.writeFloat( shakeSensitivity );

			//!	揺れやすさ
			dos.writeFloat( shakeMul );

			//!	減衰倍率
			dos.writeFloat( weightMul );

			//!	タッチでのウェイト加算
			dos.writeFloat( touchWeightAdd );

			//!	左右回転のロック
			dos.writeBoolean( orientationLock );

			//!	揺れ設定
			dos.writeS8( ( byte )shakeType );

		}
		catch( IOException e )
		{
			EagleUtil.log( e );
		}
	}

	/**
	 * オプション配列から取得する。
	 * @author eagle.sakura
	 * @param dis
	 * @version 2010/05/28 : 新規作成
	 */
	public	void		deserialize( DataInputStream dis )
	{
		try
		{
			//!	バージョンチェック
			if( dis.readS32() != eVersion )
			{
				reset();
				return;
			}


			//!	描画モード
			IsBlueMode	=	dis.readBoolean();
			IsBlueMode	=	false;

			//!	反応感度
			shakeSensitivity	=	dis.readFloat();

			//!	揺れやすさ
			shakeMul = dis.readFloat();

			//!	減衰倍率
			weightMul	=	dis.readFloat();

			//!	タッチでのウェイト加算
			touchWeightAdd	=	dis.readFloat();

			//!	左右回転のロック
			orientationLock	=	dis.readBoolean();

			//!	揺れ設定
			shakeType	=	dis.readS8();

		}
		catch( IOException e )
		{
			EagleUtil.log( e );
		}
	}

	/**
	 * タッチでのウェイト加算度合い。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/28 : 新規作成
	 */
	public float getTouchWeightAdd()
	{
		return touchWeightAdd;
	}

	/**
	 * タッチでのウェイト加算度合い設定。
	 * @author eagle.sakura
	 * @param touchWeightAdd
	 * @version 2010/05/28 : 新規作成
	 */
	public void setTouchWeightAdd(float touchWeightAdd)
	{
		this.touchWeightAdd = touchWeightAdd;
	}

	/**
	 * 左右回転のロックを行っているか。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/28 : 新規作成
	 */
	public boolean isOrientationLock()
	{
		return orientationLock;
	}

	/**
	 * 回転のロックを設定する。
	 * @author eagle.sakura
	 * @param orientationLock
	 * @version 2010/05/28 : 新規作成
	 */
	public void setOrientationLock(boolean orientationLock)
	{
		this.orientationLock = orientationLock;
	}

	/**
	 * 揺れの減衰値。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/28 : 新規作成
	 */
	public float getWeightMul()
	{
		return weightMul;
	}

	/**
	 * 正規化した減衰値を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/30 : 新規作成
	 */
	public	float	getNormalizedWeightMul( )
	{
		return	0.9f + ( weightMul / 10.0f );
	}

	/**
	 * 揺れの減衰値設定。
	 * @author eagle.sakura
	 * @param weightMul
	 * @version 2010/05/28 : 新規作成
	 */
	public void setWeightMul(float weightMul)
	{
		this.weightMul = weightMul;
	}


	/**
	 * 画像の揺れやすさを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public float getShakeMul()
	{
		return shakeMul;
	}

	/**
	 * 画像の揺れやすさを設定する。
	 * @author eagle.sakura
	 * @param shakeMul
	 * @version 2010/05/22 : 新規作成
	 */
	public void setShakeMul(float shakeMul)
	{
		this.shakeMul = shakeMul;
	}

	/**
	 * 常に揺らす。
	 */
	public	static	final	int		eShakeTypeAlways	=	0;

	/**
	 * 指で揺らす。
	 */
	public	static	final	int		eShakeTypeSlide		=	1;

	/**
	 * 加速度センサーを利用する。
	 */
	public	static	final	int		eShakeTypeAccel		=	2;

	/**
	 * 振動タイプを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public int getShakeType()
	{
		return shakeType;
	}

	/**
	 * 振動タイプを設定する。
	 * @author eagle.sakura
	 * @param shakeType
	 * @version 2010/05/22 : 新規作成
	 */
	public void setShakeType(int shakeType)
	{
		this.shakeType = shakeType;
	}

	/**
	 * 加速度センサーの反応感度取得。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/22 : 新規作成
	 */
	public float getShakeSensitivity()
	{
		return shakeSensitivity;
	}

	/**
	 * 加速度センサーの反応感度指定。
	 * @author eagle.sakura
	 * @param shakeSensitivity
	 * @version 2010/05/22 : 新規作成
	 */
	public void setShakeSensitivity(float shakeSensitivity)
	{
		this.shakeSensitivity = shakeSensitivity;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/05/17 : 新規作成
	 */
	public	Option( )
	{
		//!	ローカルから設定値を読み取る。
		reset();
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/17 : 新規作成
	 */
	public	boolean		isBlueMode( )	{	return	IsBlueMode;	}

	/**
	 * 青描画モードを設定する。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/05/17 : 新規作成
	 */
	public	void		setBlueMode( boolean set )	{	IsBlueMode = set;	}

	/**
	 * 反転する。
	 * @author eagle.sakura
	 * @version 2010/05/17 : 新規作成
	 */
	public	void		toggleBlueMode( )	{	IsBlueMode = !IsBlueMode;	}

	/**
	 * 保存先ファイル名。
	 */
	private	static	final	String		eSaveFileName	=	"shakedroid.bin";

	/**
	 * ローカルへ設定値を保存する。
	 * @author eagle.sakura
	 * @version 2010/05/30 : 新規作成
	 */
	public	void		save( Activity activity  )
	{
		try
		{
			FileOutputStream	fos =	activity.openFileOutput( eSaveFileName, Context.MODE_PRIVATE );
			DataOutputStream	dos	=	new	DataOutputStream( new OutputStreamBufferWriter( fos ) );

			//!	書き込みを行う
			serialize( dos );

			dos.dispose();
		}
		catch( IOException ioe )
		{
			EagleUtil.log( ioe );
		}
	}

	/**
	 * ローカルから設定値を呼び出す。
	 * @author eagle.sakura
	 * @version 2010/05/30 : 新規作成
	 */
	public	void		load( Activity activity )
	{
		try
		{
			FileInputStream		fis	=	activity.openFileInput( eSaveFileName );
			DataInputStream		dis =	new	DataInputStream( new InputStreamBufferReader( fis ) );

			//!	読み込みを行う
			deserialize( dis );

			dis.dispose();
		}
		catch( IOException ioe )
		{
			EagleUtil.log( ioe );
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param bundle
	 * @version 2010/05/22 : 新規作成
	 */
	public	void		saveBundle( Bundle bundle )
	{
		/*
		bundle.putBoolean(	"SD_isBlueMode", isBlueMode() );
		bundle.putFloat(	"SD_shakeSensitivity", getShakeSensitivity() );
		bundle.putInt(		"SD_shakeType", getShakeType() );
		bundle.putFloat(	"SD_shakeMul", getShakeMul() );
		*/
		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
		DataOutputStream	dos	=	new	DataOutputStream( new OutputStreamBufferWriter( baos ) );
		serialize( dos );

		//!	バイト配列として書き込む。
		bundle.putByteArray( "SD_Option", baos.toByteArray() );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param bundle
	 * @version 2010/05/22 : 新規作成
	 */
	public	void		loadBundle( Bundle bundle )
	{
		try
		{
			ByteArrayInputStream	bais = new ByteArrayInputStream( bundle.getByteArray( "SD_Option" ) );
			DataInputStream		dis = new DataInputStream( new InputStreamBufferReader( bais ) );
			deserialize( dis );
			/*
			setBlueMode( bundle.getBoolean( "SD_isBlueMode" ) );
			setShakeSensitivity( bundle.getFloat( "SD_shakeSensitivity" ) );
			setShakeType( bundle.getInt( "SD_shakeType" ) );
			setShakeMul( bundle.getFloat( "SD_shakeMul" ) );
			*/
		}
		catch( Exception  e)
		{

		}
	}

}
