/**
 *
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
package eagle.android.appcore.shake;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import eagle.android.gles11.GLManager;
import eagle.android.gles11.GLManager;
import eagle.android.util.UtilActivity;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleException;
import eagle.util.EagleUtil;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;

/**
 * セーブファイルに必要なデータの保持及び保存を行う。
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
public class ShakeDataFile
{
	/**
	 * サポートしているファイルバージョン。
	 */
	public	static	final	int		eFileVersion = 0x1;

	/**
	 * 初期化用データ。
	 */
	private	ShakeInitialize		initData	=	new	ShakeInitialize();

	/**
	 * サムネイル画像。
	 */
	private	Bitmap				thumbnail	=	null;

	/**
	 * ユーザーが設定した重みテーブル。
	 */
	private	float[]				weightTable	=	null;

	/**
	 * 保存するファイル名。
	 */
	private	String				fileName	=	"";

	/**
	 * 現在のオプション情報。
	 */
	private	Option				option		=	null;

	/**
	 * ユーザーの指定したメモテキスト。
	 */
	private	String				userText	=	"";

	/**
	 * ファイルバッファそのもの。
	 */
	private	byte[]				origin		=	null;

	/**
	 * ファイル名を指定する。
	 */
	public	static	final	String		eFileExt	=	"sdf";

	/**
	 * 無料版でのファイル名（縦）。
	 */
	public	static	final	String		eFreeModeFileName_v	=	"shake_v.sdf";

	/**
	 * 無料版でのファイル名（横）。
	 */
	public	static	final	String		eFreeModeFileName_h	=	"shake_h.sdf";

	/**
	 * 保存先のディレクトリ名。
	 */
	public	static	final	String		eSaveDirectory	=	"/shakedroid";

	/**
	 * ファイル名を取得する。<BR>
	 * 拡張子を含む。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	String	getFileName( )	{	return	fileName;	}

	/**
	 * ファイル名を登録する。<BR>
	 * 登録済みの場合、このメソッドはfalseを返し、何もしない。
	 * @author eagle.sakura
	 * @param fileName
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	boolean	registFileName( String fileName )
	{
		if( this.fileName.length() > 0 )
		{
			return	false;
		}

		this.fileName = fileName;

		return	true;
	}

	/**
	 * ファイル名を強制指定する。
	 * @author eagle.sakura
	 * @param fileName
	 * @version 2010/06/06 : 新規作成
	 */
	public	void	setFileName( String fileName )
	{
		this.fileName = fileName;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param looper
	 * @version 2010/06/05 : 新規作成
	 */
	public	ShakeDataFile( ShakeLooper looper )
	{
		initData	=	looper.getInitializeData();
		thumbnail	=	looper.getThumbnailImage();
		weightTable	=	looper.getWeightArray();
		option		=	looper.getOption();
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param path
	 * @version 2010/06/06 : 新規作成
	 */
	public	ShakeDataFile( String path )	throws	EagleException
												,	IOException
	{
		FileInputStream	fis =	new	FileInputStream( path );
		origin = new byte[ fis.available() ];
		fis.read( origin );
		fis.close();
		DataInputStream	dis = new	DataInputStream( new InputStreamBufferReader( new ByteArrayInputStream( origin ) ) );
		deserialize( dis );
		dis.dispose();
	}

	/**
	 * ファイルバッファから再構築を行う。
	 * @author eagle.sakura
	 * @param file
	 * @version 2010/06/06 : 新規作成
	 */
	public	ShakeDataFile( byte[] file )	throws	EagleException
												,	IOException
	{
		origin = file;
	}

	/**
	 * ファイルの生情報を返す。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	byte[]	getOriginBuffer( )
	{
		return	origin;
	}

	/**
	 * 初期化データの取得。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public ShakeInitialize getInitData()
	{
		return initData;
	}

	/**
	 * サムネイルの取得。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public Bitmap getThumbnail()
	{
		return thumbnail;
	}

	/**
	 * 重み配列の取得。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public float[] getWeightTable()
	{
		return weightTable;
	}

	/**
	 * ファイルへの出力用ストリームを開く。<BR>
	 * fileNameにはディレクトリを含まず、拡張子は含むこと。
	 * @author eagle.sakura
	 * @param fileName
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	static	DataOutputStream		createOutputStream( String fileName )	throws	IOException
	{
		DataOutputStream	dos = new	DataOutputStream( new OutputStreamBufferWriter( UtilActivity.openSDOutputStream( eSaveDirectory + "/" + fileName ) ) );
		return	dos;
	}

	/**
	 * ユーザーの指定したテキストを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public String getUserText()
	{
		return userText;
	}

	/**
	 * ユーザーの指定したテキストを設定する。
	 * @author eagle.sakura
	 * @param userText
	 * @version 2010/06/06 : 新規作成
	 */
	public void setUserText(String userText)
	{
		this.userText = userText;
	}

	/**
	 * 保存を行う。
	 * @author eagle.sakura
	 * @param dos
	 * @version 2010/05/31 : 新規作成
	 */
	public	void		serialize( DataOutputStream dos )
	{
		try
		{
			origin = null;

			//!	ファイルバージョン
			dos.writeS32( eFileVersion );

			//!	URI
			dos.writeString( initData.uri.toString() );
			//!	Option
			if( option == null )
			{
				option = initData.option;
			}
			option.serialize( dos );
			//!	サムネイル
			{
				dos.writeS16( ( short )thumbnail.getWidth() );
				dos.writeS16( ( short )thumbnail.getHeight() );
				int[]	pix = new int[ thumbnail.getWidth() * thumbnail.getHeight() ];
				thumbnail.getPixels( pix, 0, thumbnail.getWidth(), 0, 0, thumbnail.getWidth(), thumbnail.getHeight() );

				dos.writeS32Array( pix );
			}
			//!	ユーザーテキスト
			{
				dos.writeString( getUserText(	) );
			}
			//!	縦画面固定
			{
				dos.writeBoolean( initData.isVertical );
			}
			//!	縦横分割数
			{
				dos.writeS16( ( short )initData.xDivision );
				dos.writeS16( ( short )initData.yDivision );
			}
			//!	重みテーブルを保存
			if( weightTable == null )
			{
				weightTable = initData.weights;
			}
			{
				//!	長さを書き込む
				dos.writeS32( weightTable.length );

				dos.writeFloatArray( weightTable );
			}
		}
		catch( IOException ioe )
		{
			EagleUtil.log( ioe );
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @throws EagleException
	 * @version 2010/06/10 : 新規作成
	 */
	public	void			deserialize(  )	throws	EagleException
	{
		DataInputStream	dis = new	DataInputStream( new InputStreamBufferReader( new ByteArrayInputStream( origin ) ) );

		deserialize( dis );

		dis.dispose();
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param dis
	 * @throws EagleException
	 * @version 2010/06/06 : 新規作成
	 */
	public	void			deserialize( DataInputStream dis )	throws	EagleException
	{
		try
		{
			//!	ファイルバージョンチェック
			if( dis.readS32() != eFileVersion )
			{
				throw	new EagleException( EagleException.eStatusUnknownFileVersion );
			}

			ShakeInitialize	si		= initData;
			Option			option	= si.option;
			if( this.option == null )
			{
				this.option = option;
			}
			//!	URIの生成
			si.uri = Uri.parse( dis.readString() );
			EagleUtil.log( si.uri.toString() );
			//!	オプションの生成
			option.deserialize( dis );
			//!	サムネイル
			{
				int	width 	= dis.readS16();
				int	height	= dis.readS16();
				int[]	pix = new int[ width * height ];
				for( int i = 0; i < pix.length; ++i )
				{
					pix[ i ] = dis.readS32();
				}
				thumbnail =	Bitmap.createBitmap( width, height, Config.RGB_565 );
				thumbnail.setPixels( pix, 0, width, 0, 0, width, height );
			}
			//!	ユーザーテキスト
			{
				setUserText( dis.readString() );
			}
			//!	縦横
			si.isVertical = dis.readBoolean();

			//!	分割数
			si.xDivision = dis.readS16();
			si.yDivision = dis.readS16();

			//!	重みの長さを取得
			{
				int	length = dis.readS32();
				si.weights = new float[ length ];
				for( int i = 0; i < length; ++i )
				{
					si.weights[ i ] = dis.readFloat();
				}
				if( weightTable == null )
				{
					weightTable = si.weights;
				}

			}
		}
		catch( IOException ioe )
		{
			EagleUtil.log( ioe );
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param looper
	 * @param dis
	 * @version 2010/05/31 : 新規作成
	 */
	public	ShakeLooper		createLooper( Context context, GLManager gl )	throws	EagleException
	{
		try
		{
			//!	Looper生成
			ShakeLooper	looper	=	new	ShakeLooper( context, gl, getInitData() );
			return	looper;
		}
		catch( Exception e )
		{
			EagleUtil.log( e );
		}
		return	null;
	}
}
