/**
 *
 * @author eagle.sakura
 * @version 2010/06/08 : 新規作成
 */
package eagle.android.app.shakelive;

import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import eagle.android.app.appinfo.AppInfomation;
import eagle.android.app.shake.ShakeDroid;
import eagle.android.appcore.IAppInfomation;
import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.util.UtilActivity;
import eagle.android.util.UtilBridgeAndroid;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleException;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/06/08 : 新規作成
 */
public class ShakeWallSetting	extends			UtilActivity
								implements		OnSharedPreferenceChangeListener
{
	/**
	 * 縦画面用のファイル
	 */
	public	static	final	int			eRequestIDVerticalFile		=	0;

	/**
	 * 横画面用のファイル。
	 */
	public	static	final	int			eRequestIDHorizontalFile	=	1;

	/**
	 *
	 */
	private	ShakeWallDataNotify			dataNotify = null;

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedpreferences,
			String key)
	{
		// TODO 自動生成されたメソッド・スタブ
		EagleUtil.log( "key : " + key );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @version 2010/06/13 : 新規作成
	 */
	private	void	createNotInstallAlert( )
	{
		Builder	builder = new	Builder( this );

		TextView	tv = new	TextView( this );
		tv.setText( getResources().getString( R.string.alert_notinstall_shakedroid ) );

		builder.setPositiveButton( getResources().getString( R.string.alert_install_shakedroid ),
									new DialogInterface.OnClickListener()
									{

										@Override
										public void onClick(DialogInterface dialog, int which)
										{
										// TODO 自動生成されたメソッド・スタブ
											Uri	uri = Uri.parse( "market://search?q=pname:eagle.android.app.shakeadvance" );
											Intent	intent = new	Intent( Intent.ACTION_VIEW, uri );
											try
											{
												startActivity( intent );
												return;
											}
											catch( Exception e )
											{

											}
										}
									} );
		tv.setBackgroundColor( Color.BLACK );
		tv.setTextColor( Color.WHITE );
		builder.setView( tv );
		builder.create().show();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);
		dataNotify = new	ShakeWallDataNotify( this, null );

		//!	設定画面を開く。
		LinearLayout	layout	=	( LinearLayout )View.inflate( this, R.layout.setting_main, null );

		EagleUtil.init( new UtilBridgeAndroid( "ShakeWall") );
		//!	無料版なら横画面設定を無効
		IAppInfomation	info = new	AppInfomation();

		if( !info.isSharewareMode() )
		{
			EagleUtil.log( "Free Mode" );
			//!	AdMob用のビューを作成
			View	ad = info.createAdView( this );
			if( ad != null )
			{
				layout.addView( ad, 0 );
			}
		}

		//!
		{
			TextView	tv = ( TextView )layout.findViewById( R.id.setting_selectbutton_vertical );
			tv.setOnClickListener( new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
				// TODO 自動生成されたメソッド・スタブ
					Intent	intent = new Intent( Intent.ACTION_PICK );
					intent.setType( "shakedroid/shakedata" );
					intent.putExtra( "pickType", "v" );
					try
					{
						startActivityForResult( intent, eRequestIDVerticalFile );
					}
					catch( Exception e )
					{
						createNotInstallAlert();
					}
				}
			});

			ImageView	iv = ( ImageView )layout.findViewById( R.id.setting_preview_v );
			try
			{
				InputStream		is = dataNotify.createInputStream( ShakeWallDataNotify.eShakeDataFileNameVertical );
				byte[]		buffer = new byte[ is.available() ];
				is.read( buffer );

				ShakeDataFile	sdf = new	ShakeDataFile( buffer );
				sdf.deserialize();

				iv.setImageBitmap( sdf.getThumbnail() );

				is.close();
			}
			catch( Exception e )
			{

			}
		}
		//!
		{
			TextView	tv = ( TextView )layout.findViewById( R.id.setting_selectbutton_horizontal );
			tv.setOnClickListener( new View.OnClickListener()
			{

				@Override
				public void onClick(View v)
				{
				// TODO 自動生成されたメソッド・スタブ
					Intent	intent = new Intent( Intent.ACTION_PICK );
					intent.setType( "shakedroid/shakedata" );
					intent.putExtra( "pickType", "h" );

					try
					{
						startActivityForResult( intent, eRequestIDHorizontalFile );
					}
					catch( Exception e )
					{
						createNotInstallAlert();
					}
				}
			});

			ImageView	iv = ( ImageView )layout.findViewById( R.id.setting_preview_h );
			try
			{
				InputStream		is = dataNotify.createInputStream( ShakeWallDataNotify.eShakeDataFileNameHorizontal );
				byte[]		buffer = new byte[ is.available() ];
				is.read( buffer );

				ShakeDataFile	sdf = new	ShakeDataFile( buffer );
				sdf.deserialize();

				iv.setImageBitmap( sdf.getThumbnail() );

				is.close();
			}
			catch( Exception e )
			{

			}
		}

		setContentView( layout );

	//	addContentView( layout, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
	}

	/**
	 * データの受け取り。
	 * @author eagle.sakura
	 * @param requestCode
	 * @param resultCode
	 * @param data
	 * @version 2010/06/10 : 新規作成
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onActivityResult(requestCode, resultCode, data);

		//!	データを受け取れなかった
		if( data == null )
		{
			EagleUtil.log( "Not Data..." );
			return;
		}

		//!	覚えの無いリクエストコード
		if( requestCode != eRequestIDVerticalFile
		&&	requestCode != eRequestIDHorizontalFile )
		{
			return;
		}

		try
		{
			ImageView		preview	=	null;
			byte[]			fileBuffer	=	null;
			ShakeDataFile	sdf			=	null;
			String			fileName	=	"";

			fileBuffer = data.getByteArrayExtra( ShakeDroid.eLoadFileResultKey_FileBuffer );

			//!	データを生成する。
			sdf = new	ShakeDataFile( fileBuffer );
			sdf.deserialize(  );

			//!	リクエストコード
			if( requestCode == eRequestIDVerticalFile )
			{
				if( ! sdf.getInitData().isVertical )
				{
					Toast.makeText( this,
									getResources().getString( R.string.setting_errormessage_filetype ),
									Toast.LENGTH_LONG ).show();
					return;
				}
				//!	プレビュー用のビューを検索
				preview = ( ImageView )findViewById( R.id.setting_preview_v );
			}
			else
			{
				if( sdf.getInitData().isVertical )
				{
					Toast.makeText( this,
									getResources().getString( R.string.setting_errormessage_filetype ),
									Toast.LENGTH_LONG ).show();
					return;
				}
				//!	プレビュー用のビューを検索
				preview = ( ImageView )findViewById( R.id.setting_preview_h );
			}

			//!	プレビューの更新
			if( preview != null )
			{
				preview.setImageBitmap( sdf.getThumbnail() );
			}

			//!	通知を行う
			dataNotify.saveAndNotifyFile( sdf );

		}
		catch( Exception e )
		{
			EagleUtil.log( e );
		}
	}
}
