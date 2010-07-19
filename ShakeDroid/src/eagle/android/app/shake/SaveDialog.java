/**
 *
 * @author eagle.sakura
 * @version 2010/06/05 : 新規作成
 */
package eagle.android.app.shake;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Calendar;
import java.util.Date;

import eagle.android.app.appinfo.AppInfomation;
import eagle.android.app.shakelive.ShakeWallDataNotify;
import eagle.android.appcore.IAppInfomation;
import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.util.UtilActivity;
import eagle.io.DataOutputStream;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Build;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author eagle.sakura
 * @version 2010/06/05 : 新規作成
 */
public class SaveDialog	implements	OnClickListener
{

	private	Activity		activity;

	/**
	 * 元データ。
	 */
	private	ShakeDataFile	data;

	public	SaveDialog( Activity activity, ShakeDataFile data )
	{
		this.activity = activity;
		this.data = data;
	}


	public	static	void		initView( ShakeDataFile data, View layout, long timeStamp )
	{
		Context	activity = layout.getContext();


    	//!	サムネイルの設定
    	{
    		ImageView	iv	=	( ImageView )layout.findViewById( R.id.save_preview );
    		iv.setImageBitmap( data.getThumbnail() );
    	}

    	//!	日付の設定
    	{
    		Date	date = new	Date( timeStamp );

    		String	nowTime = "";

    		/*
    		Calendar	cal = Calendar.getInstance();
    		String	nowtime =		( cal.get( Calendar.YEAR ) )
    						+ "/" +	(cal.get( Calendar.MONTH ) + 1 )
    						+ "/" + ( cal.get( Calendar.DATE ) )
    						+ "   ";
    						*/

    		String	nowtime =	"";
    		nowtime =		( date.getYear() + 1900 )
    						+ "/" +	(date.getMonth() + 1 )
    						+ "/" + ( date.getDate() )
    						+ "   ";

    		//!	縦横属性もつけておく
    		if( data.getInitData().isVertical )
    		{
    			nowtime += activity.getResources().getString( R.string.savedialog_mode_vertical );
    		}
    		else
    		{
    			nowtime += activity.getResources().getString( R.string.savedialog_mode_horizontal );
    		}

    		TextView	tv = 	( TextView )layout.findViewById( R.id.save_date );
    		tv.setText( nowtime );
    	}

    	//!	ユーザーテキストの指定
    	if( data.getUserText().length() > 0 )
    	{
    		/*
    		EditText	et = ( EditText )layout.findViewById( R.id.save_usermemo );
    		et.setText( data.getUserText() );
    		*/
    	}
    	if( data.getInitData().originFileName.length() > 0 )
    	{
    		CheckBox	check = ( CheckBox )layout.findViewById( R.id.savedialog_overwrite );
    		check.setChecked( true );
    		check.setVisibility( View.VISIBLE );
    	}
    	else
    	{
    		CheckBox	check = ( CheckBox )layout.findViewById( R.id.savedialog_overwrite );
    		check.setVisibility( View.INVISIBLE );
    	}

	}

	/**
	 * ダイアログの作成を行う。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	public	AlertDialog		create( )
	{
    	Builder	builder	= new Builder( activity );

    	//!	レイアウトの生成
    	View	layout =	View.inflate( activity, R.layout.save_dialog, null );

    	//!	レイアウトの初期化。
    	{
    		initView( data, layout, System.currentTimeMillis() );
    	}

    	{
    		//!	ファイル名を登録

    		String	fileName =	 UtilActivity.getDateString(	)
    							//!	拡張子は主導で付加する。
    							+	"." +	ShakeDataFile.eFileExt
    							;
    		//!	ファイル名を登録する。
    		data.registFileName( fileName );
    	}

    	//!	ファイル名を強制的に上書きする
    	if( ! ( new AppInfomation() ).isSharewareMode() )
    	{
    		//!	ファイルを１種類しか作れない
    		if( data.getInitData().isVertical )
    		{
    			data.setFileName( ShakeDataFile.eFreeModeFileName_v );
    		}
    		else
    		{
    			data.setFileName( ShakeDataFile.eFreeModeFileName_h );
    		}
    	}

    	AlertDialog	dialog = builder.create();

    	builder	.setTitle( activity.getResources().getString( R.string.savedialog_title ) )
    			.setView( layout )
    			.setPositiveButton( activity.getResources().getString( R.string.dialog_ok ),		this )
    			.setNegativeButton( activity.getResources().getString( R.string.dialog_cancel ),	this );

    	//!	ロードボタンを排除
    	{
    		LinearLayout	ll = ( LinearLayout )layout;
    		ll = ( LinearLayout )ll.getChildAt( 0 );
    		ll.removeView( ll.findViewById( R.id.save_loadbutton ) );
    	}
    	//!	ライブ壁紙化ボタンを生成
    	IAppInfomation	info = new	AppInfomation();
    	if( info.isSharewareMode( ) )
    	{
    		//!	SDKバージョンが規定以上
    		if( UtilActivity.isSdkVersion2_x(	) )
    		{
	    		CheckBox	box = new	CheckBox( activity );
	    		box.setChecked( false );
	    		box.setId( eIdLiveWallpaperSendCheck );
	    		box.setText( activity.getResources().getString( R.string.savedialog_send_livewallpaper ) );
	    		LinearLayout	ll = ( LinearLayout )layout;
	    		ll.addView( box );
    		}
    	}

    	return	builder.show();
	}


	/**
	 * ライブ壁紙化のチェックボックス。
	 */
	public	static	final	int		eIdLiveWallpaperSendCheck	=	0xabcdef;

	//!
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// TODO 自動生成されたメソッド・スタブ

		//!	決定ボタン以外ならキャンセル扱い。
    	if( which != AlertDialog.BUTTON_POSITIVE )
    	{
    		return;
    	}

    	try
    	{

    		//!	ファイル名を設定する
    		{
    			AlertDialog	ad = ( AlertDialog )dialog;
    			CheckBox	check = ( CheckBox )ad.findViewById( R.id.savedialog_overwrite );

    			if( data.getInitData().originFileName.length() > 0 )
    			{
    			//!	元ファイル名が指定されている場合
    				//!	上書きモード
    				if( check.isChecked() )
    				{
    					data.setFileName( data.getInitData().originFileName );
    				}
    			}
    			else
    			{
    			//!	元ファイル名が指定されていない場合
    				data.getInitData().originFileName = data.getFileName(	);
    			}
    		}

    		{
    			//!	トリミングしたデータ？
    			if(	data.getInitData().bmp != null )
    			{
    				Bitmap	bmp = data.getInitData().bmp;
    				String	fileName = UtilActivity.toSDPath( ShakeDataFile.eSaveDirectory + "/" + data.getFileName() + "img" );
    				OutputStream	os = new FileOutputStream( fileName );
    				bmp.compress( CompressFormat.JPEG, 95, os );
    				os.close();

    				//!	URIを上書き
    				data.getInitData().uri =	Uri.parse( "file://" + fileName );
    			}
    		}



    		//!	一度メモリ上に作成する
    		byte[]	buffer = null;
    		{
    			ByteArrayOutputStream	baos	=	new	ByteArrayOutputStream();
    			DataOutputStream		dos		=	new	DataOutputStream( new OutputStreamBufferWriter( baos ) );
    			data.serialize( dos );

    			//!	配列化する。
    			buffer = baos.toByteArray();
    			dos.dispose();
    		}

    		//!	ライブ壁紙に転送する？
    		{
    			AlertDialog		alert	= ( AlertDialog )dialog;
    			CheckBox		check	= ( CheckBox )alert.findViewById( eIdLiveWallpaperSendCheck );
    			if( check != null
    			&&	check.isChecked() )
    			{
    				ShakeWallDataNotify		dataNotify	= new	ShakeWallDataNotify( activity, null );
    				ShakeDataFile			dataFile	= new	ShakeDataFile( buffer );
    				dataFile.deserialize();
    				if( ! dataNotify.saveAndNotifyFile( dataFile ) )
    				{
    					Toast.makeText( activity,
    									activity.getResources().getString( R.string.savedialog_fail_sendwall ),
    									Toast.LENGTH_LONG ).show();
    				}
    				else
    				{
			    		Toast.makeText( activity,
			    						activity.getResources().getString( R.string.savedialog_toast_saveok ),
			    						Toast.LENGTH_LONG ).show();
    				}
    			}
    			else
    			{
	    			//!	バッファへと実際に書き込む。
		    		DataOutputStream	dos = data.createOutputStream( data.getFileName( ) );

		    		//!	保存する。
		    		dos.writeBuffer( buffer, 0, buffer.length );

		    		//!	開放する。
		    		dos.dispose();

		    		Toast.makeText( activity,
		    						activity.getResources().getString( R.string.savedialog_toast_saveok ),
		    						Toast.LENGTH_LONG ).show();
    			}
    		}
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    		Toast.makeText( activity,
    						activity.getResources().getString( R.string.savedialog_toast_saveng ),
    						Toast.LENGTH_LONG ).show();
    	}
	}
}
