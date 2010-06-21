/**
 *
 * @author eagle.sakura
 * @version 2010/06/12 : 新規作成
 */
package eagle.android.app.shakelive;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;

import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.util.UtilActivity;
import eagle.io.DataOutputStream;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleUtil;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.net.Uri;
import android.preference.PreferenceManager;

/**
 * 共有ファイルに対する通知を行う。
 * @author eagle.sakura
 * @version 2010/06/12 : 新規作成
 */
public class ShakeWallDataNotify
{
	private	SharedPreferences					manager	=	null;
	private	Context								context	=	null;
	private	OnSharedPreferenceChangeListener	listener	=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @param manager
	 * @version 2010/06/12 : 新規作成
	 */
	public	ShakeWallDataNotify( Context context, OnSharedPreferenceChangeListener listener )
	{
		this.manager 	=	context.getSharedPreferences( eSharedPreferencesName, 0 );
		this.context	=	context;
		if( listener != null )
		{
			this.listener	=	listener;
			manager.registerOnSharedPreferenceChangeListener( listener );
		}
	}

	public	void	dispose( )
	{
		if( listener != null )
		{
			manager.unregisterOnSharedPreferenceChangeListener( listener );
		}
		manager		= null;
		context		= null;
		listener	= null;
	}

	/**
	 *
	 */
	public	static	final	String		eSharedPreferencesName	=	"eagle.android.app.shakelive";

	/**
	 * 縦向きのファイルが変更されたことを示す。
	 */
	private	static	final	String		eKey_NotifyVerticalFile 	= "notify_file_vertical";

	/**
	 * 横向きのファイルが変更されたことを示す。
	 */
	private	static	final	String		eKey_NotifyHorizontalFile	= "notify_file_horizontal";

	/**
	 * このファイルにアクセスをされた場合、変更通知とみなす。
	 */
	public	static	final	String		eFileName_SendNotify_Vertical	= "notify_v.send";
	/**
	 * このファイルにアクセスをされた場合、変更通知とみなす。
	 */
	public	static	final	String		eFileName_SendNotify_Horizontal	= "notify_h.send";

	/**
	 * ファイルアクセス用URIのヘッダ。
	 */
	public	static	final	String		eContentProviderHeader		=	"content://" + eSharedPreferencesName + "/";

	/**
	 * 縦方向の画像ファイル。
	 */
	public	static	final	String		eImageFileNameVertical		=	"img_vertical.img";

	/**
	 * 横方向の画像ファイル。
	 */
	public	static	final	String		eImageFileNameHorizontal	=	"img_horizontal.img";

	/**
	 * 縦方向の揺れ定義ファイル。
	 */
	public	static	final	String		eShakeDataFileNameVertical		=	"sdf_vertical." + ShakeDataFile.eFileExt;

	/**
	 * 横方向の揺れ定義ファイル。
	 */
	public	static	final	String		eShakeDataFileNameHorizontal	=	"sdf_horizontal." + ShakeDataFile.eFileExt;

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	SharedPreferences	getSharedPreferences( )
	{
		return	manager;
	}

	/**
	 * ライブ壁紙サービス内のファイルにアクセスするためのURIを生成して返す。
	 * @author eagle.sakura
	 * @param fileName
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	private	Uri			createUri( String fileName )
	{
		return	Uri.parse(	eContentProviderHeader + fileName
				);
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param fileName
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	InputStream				createInputStream( String fileName )	throws	FileNotFoundException
	{
		return	context.getContentResolver().openInputStream( createUri( fileName ) );
	}

	/**
	 * ライブ壁紙サービス内のファイルに書き込み用のストリームを作成する。
	 * @author eagle.sakura
	 * @param fileName
	 * @return
	 * @throws FileNotFoundException
	 * @version 2010/06/12 : 新規作成
	 */
	public	DataOutputStream		createOutputStream( String fileName )	throws	FileNotFoundException
	{
		OutputStream os =	context.getContentResolver().openOutputStream( createUri( fileName ), "rw" );
		return	new	DataOutputStream( new OutputStreamBufferWriter( os ) );
	}

	/**
	 * ファイルの変更とその通知を行う。
	 * @author eagle.sakura
	 * @param file
	 * @version 2010/06/12 : 新規作成
	 */
	public	boolean		saveAndNotifyFile( ShakeDataFile file )
	{
		String	imageName	= "",
				sdfName		= "";
		if( file.getInitData().isVertical )
		{
			imageName	= eImageFileNameVertical;
			sdfName		= eShakeDataFileNameVertical;
		}
		else
		{
			imageName	= eImageFileNameHorizontal;
			sdfName		= eShakeDataFileNameHorizontal;
		}

		//!	画像ファイルをコピーする。
		Uri	copy = createUri( imageName );
		if( ! UtilActivity.copyFile( context, file.getInitData().uri, copy ) )
		{
			EagleUtil.log( "Error URI copy..." );
			return	false;
		}

		//!	URIを書き換える
		file.getInitData().uri = copy;

		//!	書き込み先のURIを作成する。
		try
		{
			DataOutputStream	dos = createOutputStream( sdfName );
			file.serialize( dos );
			dos.dispose();

			//!	書き込みが終了したなら、通知を行う
			if( file.getInitData().isVertical )
			{
				EagleUtil.log( "send notify vertical" );
			//	sendNotifyVerticalFile();
				accessNotifySendFile( eFileName_SendNotify_Vertical );
			}
			else
			{
				EagleUtil.log( "send notify horizontal" );
			//	sendNotifyHorizontalFile();
				accessNotifySendFile( eFileName_SendNotify_Horizontal );
			}
			return	true;
		}
		catch( Exception e )
		{
			EagleUtil.log( e );
			return	false;
		}
	}

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/06/13 : 新規作成
	 */
	private	void		accessNotifySendFile( String fileName )
	{
		InputStream	is = null;
		try
		{
			is = createInputStream( fileName );
			is.close();
		}
		catch( Exception e )
		{
			try
			{
				is.close();
			}
			catch( Exception _e )
			{

			}
		}
	}

	/**
	 * 縦向きのファイルが更新されたことを通知する。
	 * @author eagle.sakura
	 * @version 2010/06/12 : 新規作成
	 */
	public	void		sendNotifyVerticalFile( )
	{
		manager.edit().remove( eKey_NotifyVerticalFile ).commit();
	}

	/**
	 * 横向きのファイルが更新されたことを通知する。
	 * @author eagle.sakura
	 * @version 2010/06/12 : 新規作成
	 */
	public	void		sendNotifyHorizontalFile( )
	{
		manager.edit().remove( eKey_NotifyHorizontalFile ).commit();
	}


	/**
	 * 縦向きファイルの変更通知であるかを調べる。
	 * @author eagle.sakura
	 * @param sharedPreferences
	 * @param key
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	boolean		isNotifyVerticalFile(	SharedPreferences sharedPreferences, String key)
	{
		return	key.equals( eKey_NotifyVerticalFile );
	}

	/**
	 * 横向きファイルの変更通知であるかを調べる。
	 * @author eagle.sakura
	 * @param sharedPreferences
	 * @param key
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	boolean		isNotifyHorizontalFile(	SharedPreferences sharedPreferences, String key)
	{
		return	key.equals( eKey_NotifyHorizontalFile );
	}
}
