/**
 *
 * @author eagle.sakura
 * @version 2010/06/12 : 新規作成
 */
package eagle.android.app.shakelive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;

import eagle.android.util.UtilBridgeAndroid;
import eagle.util.EagleUtil;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

/**
 * @author eagle.sakura
 * @version 2010/06/12 : 新規作成
 */
public class ShakeWallFileSystem	extends	ContentProvider
{
	private	ShakeWallDataNotify	dataNotify	=	null;

	/**
	 * 初期化。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public boolean onCreate()
	{
		if( EagleUtil.getBridge() == null )
		{
			EagleUtil.init( new UtilBridgeAndroid( "ShakeWallProvider" ) );
		}
		// TODO 自動生成されたメソッド・スタブ
		dataNotify = new ShakeWallDataNotify( getContext(), null );

		EagleUtil.log( "Create Provider" );
		return true;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param uri
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	File		openLocalFile( Uri uri )	throws	FileNotFoundException
	{
		if( uri.getLastPathSegment().equals( ShakeWallDataNotify.eFileName_SendNotify_Horizontal ) )
		{
			dataNotify.sendNotifyHorizontalFile();
			throw	new	FileNotFoundException();
		}
		if( uri.getLastPathSegment().equals( ShakeWallDataNotify.eFileName_SendNotify_Vertical ) )
		{
			dataNotify.sendNotifyVerticalFile();
			throw	new	FileNotFoundException();
		}

		File	file = new	File( URI.create(
								"file:///data/data/" + ShakeWallDataNotify.eSharedPreferencesName + "/" + uri.getLastPathSegment()
				) );

		//!	ファイルが存在しない
		if( ! file.exists() )
		{
			try
			{
				//!	ファイルを作成する
				file.createNewFile();
			}
			catch( IOException e )
			{
				EagleUtil.log( e );
			}
		}
		return	file;
	}

	/**
	 * ファイルを開く。
	 * @author eagle.sakura
	 * @param uri
	 * @param mode
	 * @return
	 * @throws FileNotFoundException
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public ParcelFileDescriptor openFile(Uri uri, String mode)	throws FileNotFoundException
	{
		// TODO 自動生成されたメソッド・スタブ
		ParcelFileDescriptor	pfd = null;

		EagleUtil.log( "Create Provider" );
		//!	読み書きモード
		if( mode.equals( "rw" ) )
		{
			pfd = ParcelFileDescriptor.open( openLocalFile( uri ), ParcelFileDescriptor.MODE_READ_WRITE );
		}
		else if( mode.equals( "w" ) )
		{
			pfd = ParcelFileDescriptor.open( openLocalFile( uri ), ParcelFileDescriptor.MODE_WRITE_ONLY );
		}
		else
		{
			pfd = ParcelFileDescriptor.open( openLocalFile( uri ), ParcelFileDescriptor.MODE_READ_ONLY );
		}
		return pfd;

	}

	/**
	 * SQLクエリ。
	 * @author eagle.sakura
	 * @param uri
	 * @param as
	 * @param s
	 * @param as1
	 * @param s1
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public Cursor query(Uri uri, String[] as, String s, String[] as1, String s1)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/**
	 * SQL挿入。
	 * @author eagle.sakura
	 * @param uri
	 * @param contentvalues
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public Uri insert(Uri uri, ContentValues contentvalues)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/**
	 * データベース更新。
	 * @author eagle.sakura
	 * @param uri
	 * @param contentvalues
	 * @param s
	 * @param as
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public int update(Uri uri, ContentValues contentvalues, String s,
			String[] as)
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/**
	 * データベース削除。
	 * @author eagle.sakura
	 * @param uri
	 * @param s
	 * @param as
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public int delete(Uri uri, String s, String[] as)
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/**
	 * 種別取得。
	 * @author eagle.sakura
	 * @param uri
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	@Override
	public String getType(Uri uri)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}
}
