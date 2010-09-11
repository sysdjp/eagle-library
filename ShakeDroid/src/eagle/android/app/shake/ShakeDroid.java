package eagle.android.app.shake;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.util.Date;

import eagle.android.app.appinfo.AppInfomation;
import eagle.android.appcore.IAppInfomation;
import eagle.android.appcore.shake.Option;
import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.appcore.shake.ShakeInitialize;
import eagle.android.appcore.shake.ShakeLooper;
import eagle.android.appcore.shake.ShakePlayLooper;
import eagle.android.appcore.shake.ShakeVertices;
import eagle.android.graphic.Graphics;
import eagle.android.thread.ILoopManager;
import eagle.android.thread.ILooper;
import eagle.android.thread.LooperHandler;
import eagle.android.thread.LooperThread;
import eagle.android.util.UtilActivity;
import eagle.android.util.UtilBridgeAndroid;
import eagle.android.view.OpenGLView;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.FileAccessStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.math.Vector2;
import eagle.util.EagleUtil;
import android.R.layout;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Region.Op;
import android.net.Uri;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class ShakeDroid	extends		UtilActivity
{
	/**
	 * ループ用の共用クラス。
	 */
	private	ShakeLooper					shakeLooper	=	null;
	/**
	 * ループ管理クラス。
	 */
	private	ILoopManager				loopManager	=	null;

	/**
	 * 初期化データ。
	 */
	private	ShakeInitialize				initData	=	null;
	/**
	 * ファイル選択用のコード。
	 */
	public	static	final	int			eRequestCodeFileChoose	=	0;

	/**
	 * 保存した情報の読み込み用コード。
	 */
	public	static	final	int			eRequestCodeLoadFileChoose	=	1;

	/**
	 * 画像のトリミングを行うコード。
	 */
	public	static	final	int			eRequestCodeImageTriming	=	2;

	/**
	 * メイン画面で使用するレイアウトID。
	 */
	private	static	final	int			eMainRayoutID = R.layout.mainview;

	/**
	 * アプリ内の共有データ。
	 */
	private	SharedData		sharedData	=	null;

	/**
	 * 共有データを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/16 : 新規作成
	 */
	public	SharedData		getSharedData( )
	{
		return	sharedData;
	}

	/**
	 * 処理に必要なループ管理クラスを返す。
	 * @author eagle.sakura
	 * @param looper
	 * @return
	 * @version 2010/07/23 : 新規作成
	 */
	public	ILoopManager		createLoopManager( ILooper looper )
	{
		return	new LooperHandler( new Handler(	), looper );
	//	return	new LooperThread( looper );
	}

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
 //       setContentView(R.layout.main);
    	//!	Urilを初期化
		UtilActivity.setOrientationFixed( this, true );
    	EagleUtil.init( new UtilBridgeAndroid( "ShakeDroid" ) );
    	EagleUtil.log( "onCreate" );
    	EagleUtil.log( "handler type" );

    	sharedData = 	new	SharedData( this, null );
    	initData = new ShakeInitialize();
    	initData.isVertical = isOrientationVertical( this );

    	//!	必要なディレクトリを作成する
    	UtilActivity.createSDDirectory( ShakeDataFile.eSaveDirectory );

    	//!	設定を呼び出す
    	EagleUtil.log( "OptionLoad" );
    	initData.option.load( this );
    	EagleUtil.log( "OptionLoad complete!" );

    	Intent intent = getIntent();
    	String	action = intent.getAction();
    	if( action.equals( Intent.ACTION_SEND )
    	||	action.equals( Intent.ACTION_VIEW ))
    	{
    		EagleUtil.log( "Open Intent..." );

			  String	extra =	"";

			  try
			  {
				  EagleUtil.log( "STREAM : " + intent.getStringExtra( Intent.EXTRA_STREAM ) );
				  EagleUtil.log( "URI : " + intent.getData() );
				  EagleUtil.log( "DATASTRING : " + intent.getDataString() );
				  if( intent.getParcelableExtra( Intent.EXTRA_STREAM ) != null )
				  {
					  EagleUtil.log( "Extra : A" );
					  extra = intent.getParcelableExtra( Intent.EXTRA_STREAM ).toString();
				//	  extra = intent.getStringExtra( Intent.EXTRA_TEXT );
				  }
				  else
				  {

					  EagleUtil.log( "Extra : B" );
					  extra = intent.getStringExtra( Intent.EXTRA_TEXT );

					  if( extra == null )
					  {
						 extra = intent.getDataString();
					  }
				  }
				  EagleUtil.log( "extra : " + extra );
			  }
			  catch( Exception e )
			  {
				  EagleUtil.log( e );
			  }

			  /*
			  {
				  //!	http?
				  if( extra.startsWith( "http" ) )
				  {
						try
						{
							//!	ローカルに落とす
							EagleUtil.log( "Download Resource..." );
							Toast.makeText( this,  "DOWNLOAD...", Toast.LENGTH_LONG ).show();

							URL	url = new URL( extra );

							HttpURLConnection http = ( HttpURLConnection )url.openConnection();
							http.connect();
							InputStream	is = http.getInputStream();

							String	path = UtilActivity.convertSDPath( "shakedroid/" + UtilActivity.getDateString() + ".img" );
							EagleUtil.log( "path" + path );
							FileOutputStream	fos = new FileOutputStream( path );

							int	read = 0;
							byte[]	buffer = new byte[ 512 ];
							int readbytes = 0;
							while( true )
							{
								readbytes = is.read( buffer, 0, is.available() > 512 ? 512 : is.available() );
								if( readbytes <= 0 )
								{
									break;
								}
								else
								{
									fos.write( buffer, 0, readbytes );
								}
							}

							EagleUtil.log( "Export complete!" );

							fos.close();
							extra = "file://" + path;
						}
						catch( Exception e )
						{
							EagleUtil.log( e );
						}
				  }
			  }
				 */

			  if( extra != null )
			  {
				  initData.uri = Uri.parse( extra );
			  }

			  if( initData.uri != null )
			  {
				  Toast.makeText( this, getString( R.string.touch_picture ), Toast.LENGTH_LONG ).show();


				  createThread();
			  }
			  else
			  {
				  setContentView( eMainRayoutID );
			  }
    	}
    	else
    	{
    		EagleUtil.log( "Start main layout" );
    		createStartView();
    	}



    	/*
    	{
    		RelativeLayout	rLayout = new RelativeLayout( this );

    		//!	webView
    		{
	    		//!	webview
	    		WebView	wv = new WebView( this );
	    		wv.loadUrl( "file:///android_asset/tips/helptest.html" );
	    		rLayout.addView( wv );
    		}

    		//!	checkbox
    		{
	    		LinearLayout	layout = new LinearLayout( this );
	    		layout.setOrientation( LinearLayout.VERTICAL );
	    		layout.setGravity( Gravity.BOTTOM );

	    		CheckBox		checkBox = new CheckBox( this );
	    		checkBox.setText( "もう表示しない" );
	    		checkBox.setBackgroundColor( 0xff000000 );

	    		layout.addView( checkBox );
	    		rLayout.addView( layout, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ) );
    		}

    		setContentView( rLayout );
    	}
    	*/
	}

    /**
     * 現在のデータの共有を行う。
     * @author eagle.sakura
     * @version 2010/08/08 : 新規作成
     */
    private	void	startShareActivity( )
    {
    	try
    	{
    		String	name = "";
    		{
    			Date	date = new Date();
    			name += "send";
    			name += UtilActivity.getDateString();
    			name += ".png";
    		}
    		String	path = UtilActivity.convertSDPath( "shakedroid/" + name );
    		FileOutputStream	fos = new FileOutputStream( path );
    		DataOutputStream	fileDos = new DataOutputStream( fos );
    		{
    			//!	PNG化する。
    			EagleUtil.log( "Size : " + getInitializeData().displayOrigin.getWidth() + " x " + getInitializeData().displayOrigin.getHeight() );
    			getInitializeData().displayOrigin.compress( CompressFormat.PNG, 100, fos );

    			//!	揺れ情報をバイト化する
    	//		if( false )
    			{
    				ByteArrayOutputStream	baos = new ByteArrayOutputStream();
    				DataOutputStream		dos = new DataOutputStream( baos );

    				ShakeDataFile	sdf = new ShakeDataFile( getLooper() );
    				sdf.serialize( dos );

    				//!	バッファ化する。
    				byte[]	buffer =	baos.toByteArray();

    				dos.dispose();

    				//!	バッファを書き込む
    				fos.write( buffer );

    				//!	バッファサイズを書き込む
    				fileDos.writeS32( buffer.length );

    				//!	バッファ識別子を書き込む。
    				byte[]		fin =
    				{
    					( byte )'S',
    					( byte )'H',
    					( byte )'A',
    					( byte )'K',
    					( byte )'E',
    				};
    				fos.write( fin );
    			}
    		}
    		fileDos.dispose();
    		fileDos = null;
    		fos = null;

    		{
	    		Intent		intent = new Intent( Intent.ACTION_SEND );
	    	//	String	text =	"ShakeData Share #shakedroid";
	    		String	text = getResources().getString( R.string.share_shakedata_message );
	    		intent.putExtra( Intent.EXTRA_TEXT, text );
	    		intent.setType( "image/png" );
	    		intent.putExtra( Intent.EXTRA_STREAM, Uri.parse( "file://" +  path ) );
	    		startActivity( intent );
	    		removeGLThread();
    		}
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}
    }

    private	byte[]		isShakeInPng( Uri uri )
    {
    	EagleUtil.log( "URI : " + uri.toString() );
    	try
    	{
	    	InputStream		fis = getContentResolver().openInputStream( uri );

	    	int	size = fis.available();
	    	EagleUtil.log( "size : " + size );
	    	fis.skip( size - 5 );

	    	//!	識別子を調べる。
	    	{
	    		final char[] check ="SHAKE".toCharArray();
	    		for( char c : check )
	    		{
	    			char	cc = ( char )fis.read();
	    			EagleUtil.log( "CHAR : " + cc );

	    			if( c != cc )
	    			{
	    				fis.close();
	    				return	null;
	    			}
	    		}

	    		EagleUtil.log( "this is ShakeInFile!!" );
	    		fis.close();
	    		fis = null;
	    	}

	    	//!	ファイルサイズを知らえる
	    	{
	    	//	fis = new FileInputStream( file );
	    		fis = getContentResolver().openInputStream( uri );
	    		fis.skip( size - 5 - 4 );

	    		DataInputStream	dis = new DataInputStream( fis );
	    		int	shakeDataSize =	dis.readS32();
	    		EagleUtil.log( "ShakeDataSize : " + shakeDataSize );

	    		dis.dispose();
	    		dis = null;
	    		fis = null;

	    //		fis = new FileInputStream( file );
	    		fis = getContentResolver().openInputStream( uri );
	    		fis.skip( size - 5 - 4 - shakeDataSize );
	    		dis = new DataInputStream( fis );

	    		byte[]	result =	dis.readBuffer( shakeDataSize );

	    		EagleUtil.log( "VERSION : " + result[ 0 ] );
	    		EagleUtil.log( "VERSION : " + result[ 1 ] );
	    		EagleUtil.log( "VERSION : " + result[ 2 ] );
	    		EagleUtil.log( "VERSION : " + result[ 3 ] );

	    		return	result;
	    	}

    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}

    	return	null;
    }


    /**
     *
     * @author eagle.sakura
     * @version 2010/06/06 : 新規作成
     */
    protected	void	createStartView( )
    {
		IAppInfomation	info = new AppInfomation();
		//!	説明テキスト。
		View	view =	View.inflate( this, eMainRayoutID, null );
		LinearLayout	linear = ( LinearLayout )view;

		//!広告
		{
			View	ad = info.createAdView( this );
			if( ad != null )
			{
				linear.addView( ad, 0 );
			}
		}

		//!	手動広告
		/*
		*/
		if( !info.isSharewareMode() )
		{
		//	if( UtilActivity.isSdkVersion2_x( ) )
			{
				View	ad = View.inflate( this, R.layout.mainview_adbutton, null );
				Button	install = ( Button )ad.findViewById( R.id.main_ad_installbutton );

				install.setOnClickListener( new View.OnClickListener()
				{

					@Override
					public void onClick(View v)
					{
					// TODO 自動生成されたメソッド・スタブ
						Uri	uri = Uri.parse( "market://search?q=pub:SAKURA" );
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
				});

				if( ad != null )
				{
					linear.addView( ad );
				}
			}
		}
		setContentView( view );
    }

    /**
     * 初期化用のデータを取得する。
     * @author eagle.sakura
     * @return
     * @version 2010/05/28 : 新規作成
     */
    public	ShakeInitialize		getInitializeData( )
    {
    	return	initData;
    }

    /**
     * 振動タイプ設定ダイアログを開く。
     * @author eagle.sakura
     * @version 2010/05/22 : 新規作成
     */
    public	void	startShakeTypeDialog( )
    {
    	OptionDialog		dialog = new	OptionDialog( this );
    	dialog.create();
    }

    /**
     * 設定保存用のダイアログを開く。
     * @author eagle.sakura
     * @version 2010/06/06 : 新規作成
     */
    public	void	startSaveDialog( )
    {
    	ShakeDataFile	sdf	= new	ShakeDataFile( shakeLooper );
    	SaveDialog	dialog	=	new	SaveDialog( this, sdf );
    	dialog.create();
    }


    /**
     * オプションが変更された。
     * @author eagle.sakura
     * @param option
     * @version 2010/05/28 : 新規作成
     */
    public	void	onOptionChange( Option option )
    {
		if( shakeLooper != null )
		{
			Vector2	v = UtilActivity.getDisplaySize( this, new Vector2( ) );
			sharedData.getShakePixelDivisions( ( int )v.x, ( int )v.y, v );

			//!	密度に変更があった
			if( initData.xDivision != ( int )v.x
			||	initData.yDivision != ( int )v.y )
			{
				initData.xDivision = ( int )v.x;
				initData.yDivision = ( int )v.y;

				createThread();
			}
			else
			{
				shakeLooper.onOptionChange( option, getSharedData( ) );
			}
		}
    }

    /**
     * 現在の状態を保存する。
     * @author eagle.sakura
     * @param outState
     * @version 2010/05/21 : 新規作成
     */
    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
    	EagleUtil.log( "onSaveInstanceState" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onSaveInstanceState( outState );

    	if( initData.uri != null )
    	{
    		outState.putString( "SDUri", initData.uri.toString() );
    	}
    	initData.option.saveBundle( outState );

    	/*
    	//!	揺れ情報を保存
    	try
    	{
	    	if( shakeLooper != null )
	    	{
	    		ShakeDataFile	sdf = new ShakeDataFile( shakeLooper );
	    		ByteArrayOutputStream	baos = new ByteArrayOutputStream();
	    	}
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}
    	*/
    }

    /**
     * 保存しておいた情報から復元を行う。
     * @author eagle.sakura
     * @param savedInstanceState
     * @version 2010/05/21 : 新規作成
     */
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
    	EagleUtil.log( "onRestoreInstanceState" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onRestoreInstanceState( savedInstanceState );

    	initData.option.loadBundle( savedInstanceState );

    	if( savedInstanceState.containsKey( "SDUri" ) )
    	{
	    	initData.uri = Uri.parse( savedInstanceState.getString( "SDUri" ) );
	    	createThread();
    	}
    }

    /**
     * admobに必要なピクセル数。
     */
    public	static	final	int		eAdviewHeightPixel = 48;

    /**
     * 必要なスペースが空いていたら宣伝を挿入する。
     * @author eagle.sakura
     * @version 2010/06/06 : 新規作成
     */
    protected	void	initFreeSpaceAdview( )
    {
    	if( shakeLooper == null )
    	{
    		return;
    	}

    	if( shakeLooper.getUpperFreePixel() < eAdviewHeightPixel )
    	{
    		return;
    	}

    	IAppInfomation	info = new AppInfomation();
    	if( info.isSharewareMode() )
    	{
    		return;
    	}

    	View	ad = info.createAdView( this );
    	if( ad != null )
    	{
    		addContentView( ad, new LayoutParams( LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT ) );
    	}
    }

    /**
     * 読込み・再生専用のスレッドを立てる。
     * @author eagle.sakura
     * @param file
     * @version 2010/08/25 : 新規作成
     */
    protected	void	createImportThread( byte[] file )
    {
    	EagleUtil.log( "create import thread" );
    	OpenGLView	glView = new	OpenGLView( this );

    	shakeLooper	=	new	ShakePlayLooper( file, this, glView.getGLManager(), initData );

    	{
    		loopManager		=	createLoopManager( shakeLooper );
    		loopManager.addSurface( glView );
    	}

		setContentView( glView );
    	{
	    	IAppInfomation	info = new AppInfomation();
	    	if( ! info.isSharewareMode() )
	    	{
	    		View	admob = info.createAdView( this );
	    		if( admob != null )
	    		{
	    			addContentView( admob, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT )	);
	    		}
	    	}
    	}
		addShakeMenu();
    	EagleUtil.log( "create import thread complete!" );
    }

    /**
     * 表示用スレッドを作成。
     * @author eagle.sakura
     * @version 2010/05/20 : 新規作成
     */
    protected	void	createThread( )
    {
    	removeGLThread();

    	//!	組み込みデータ?
    	if( initData.uri != null )
    	{
    		byte[] file = isShakeInPng( initData.uri );
    		if( file != null )
    		{
    			createImportThread( file );
    			return;
    		}
    	}


    	EagleUtil.log( "create thread" );
    	OpenGLView	glView = new	OpenGLView( this );

    	//!	密度変更
    	if( initData.weights == null
    	&&	initData.xDivision == -1
    	&&	initData.yDivision == -1 )
    	{
    		Vector2	v = UtilActivity.getDisplaySize( this, new Vector2() );
    		sharedData.getShakePixelDivisions( ( int  )v.x, ( int )v.y, v );
    		initData.xDivision = ( int )v.x;
    		initData.yDivision = ( int )v.y;
    	}

    	//!	顔認識
    	initData.isFaceDetect = sharedData.isEnableFaceDetect();


    	shakeLooper	=	new	ShakeLooper( this, glView.getGLManager(), initData );

    	{
    		loopManager		=	createLoopManager( shakeLooper );
    		loopManager.addSurface( glView );
    	}

		setContentView( glView );
    	{
	    	IAppInfomation	info = new AppInfomation();
	    	if( ! info.isSharewareMode() )
	    	{
	    		View	admob = info.createAdView( this );
	    		if( admob != null )
	    		{
	    			addContentView( admob, new LayoutParams( LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT )	);
	    		}
	    	}
    	}

		addShakeMenu();
    	EagleUtil.log( "create thread complete!" );
    }

    /**
     * ファイルバッファからデータを読み込む。
     * @author eagle.sakura
     * @param file
     * @version 2010/06/06 : 新規作成
     */
    protected	void	loadShakeData( Intent intent )
    {
    	try
    	{
	    	removeGLThread();

	    	ShakeDataFile	sdf = new	ShakeDataFile( intent.getByteArrayExtra( eLoadFileResultKey_FileBuffer ) );
	    	sdf.deserialize();

	    	EagleUtil.log( "create thread" );
	    	OpenGLView	glView = new	OpenGLView( this );
	    	shakeLooper	=	sdf.createLooper( this, glView.getGLManager() );
	    	initData	=	shakeLooper.getInitializeData(	);
	    	initData.originFileName	=	intent.getStringExtra( eLoadFileResultKey_FileName );
	    	{
	    		loopManager = createLoopManager( shakeLooper );
	    		loopManager.addSurface( glView );
	    	}
			setContentView( glView );
			addShakeMenu();
	    	EagleUtil.log( "create thread complete!" );
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}
    }

    /**
     * 画像取得アクティビティを作成する。
     * @author eagle.sakura
     * @version 2010/05/20 : 新規作成
     */
    private	void	createPickPictureActivity( )
    {
		Intent	intent = new Intent( Intent.ACTION_PICK );
		intent.setType( "image/*" );
		startActivityForResult( intent, eRequestCodeFileChoose );

		Toast.makeText(		this,
							getString( R.string.pick_picture ),
							Toast.LENGTH_SHORT ).show();
    }

    /**
     * ファイル選択用アクティビティを作成する。
     * @author eagle.sakura
     * @version 2010/06/06 : 新規作成
     */
	private	void	createLoadFileActivity( )
	{
	//	Intent	intent = new Intent( this, LoadFileSelectActivity.class );
		Intent	intent = new Intent( Intent.ACTION_PICK );
		intent.setType( "shakedroid/shakedata" );
		intent.putExtra( "pickType", "auto" );
		startActivityForResult( intent, eRequestCodeLoadFileChoose );
	}

    @Override
    protected void onPause()
    {
    	EagleUtil.log( "onPause" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onPause();

    	if( shakeLooper != null )
    	{
    	//	ShakeLooper.writeLastFile( shakeLooper );
    	}
    	removeGLThread(  );
    }

    @Override
    protected void onStop()
    {
    	EagleUtil.log( "onStop" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onStop();
    }

    @Override
    protected void onResume()
    {
    	EagleUtil.log( "onResume" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onResume();
    }

    @Override
    protected void onDestroy()
    {
    	EagleUtil.log( "onDestroy" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
    	EagleUtil.log( "onRestart" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onRestart();
    	if( initData.uri	!= null
    //	&&	thread			== null
    //	&&	handler			== null
    	&&	loopManager		== null
    	)
		{
    		EagleUtil.log( "onRestar thread" );
			//!	その情報を元に、処理スレッドを作成。
			createThread();
    	}
    }

    /**
     * ファイル名を戻す。
     */
    public	static	final	String		eLoadFileResultKey_FileName		=	"FILE_NAME";
    /**
     * ファイル情報のバッファを直接戻す。
     */
    public	static	final	String		eLoadFileResultKey_FileBuffer	=	"FILE_BUFFER";

    /**
     * トリミングしたデータの保存先。
     */
    private	static	final	String		eTrimDataPath = UtilActivity.convertSDPath( "shakeDroid/trim.dat" );

    /**
     * 画像のトリミングを開始する。
     * @author eagle.sakura
     * @version 2010/07/14 : 新規作成
     */
    public	void	startTrimImageActivity( )
    {
    	try
    	{
			Intent	intent = new Intent( "com.android.camera.action.CROP" );
			intent.setData( initData.uri );
			intent.putExtra("crop", "true");
			Vector2	size = UtilActivity.getDisplaySize( this, new Vector2() );
		//	float	mul = ( 0.5f + ( 1.0f - sharedData.getMemorySavingLevel() ) / 2 );
		//	size.x *= mul;
		//	size.y *= mul;
		//	intent.putExtra("return-data", true);
			intent.putExtra(	android.provider.MediaStore.EXTRA_OUTPUT,
								Uri.fromFile( new File( eTrimDataPath ) )
							);
			intent.putExtra("aspectX",	( int )size.x);
			intent.putExtra("aspectY",	( int )size.y);
			intent.putExtra("outputX",	( int )size.x);
			intent.putExtra("outputY",	( int )size.y);
			intent.putExtra("scale",	true );
			startActivityForResult( intent, eRequestCodeImageTriming );

    		/*
    		String	text =	"inetnt tweet test";
    		Intent	intent = new Intent( Intent.ACTION_SEND );
    		intent.putExtra( Intent.EXTRA_TEXT, text );
    		intent.setType( "text/plain" );
    		startActivity( intent );
    		*/
    	}
    	catch( Exception e )
    	{
    		EagleUtil.log( e );
    	}

    }


    /**
     * 画像を選択終了した。
     * @author eagle.sakura
     * @param requestCode
     * @param resultCode
     * @param data
     * @version 2010/05/16 : 新規作成
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	EagleUtil.log( "onActivityResult : " + requestCode );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onActivityResult(requestCode, resultCode, data);

    	if( data == null )
    	{
    		EagleUtil.log( "Data is null..." );
    		createStartView();
    		removeShakeMenu();
    		return;
    	}

    	System.gc();
    	if( requestCode == eRequestCodeFileChoose )
    	{
    		try
    		{
	    		Uri	photoURI = data.getData();

	    		if( photoURI != null )
	    		{
	    			EagleUtil.log( photoURI.toString() );
	    			//!	ファイル名
	    			initData.onNewFile();
	    			initData.uri = photoURI;
	    			Toast.makeText( this, getString( R.string.touch_picture ), Toast.LENGTH_LONG ).show();
	    			createThread();
	    		}
	    		else
	    		{
	    			EagleUtil.log( "URI null..." );
	    			setContentView( eMainRayoutID );
	    		}
    		}
    		catch( Exception e )
    		{
    			EagleUtil.log( e );
    		}
    	}
    	else if( requestCode == eRequestCodeLoadFileChoose )
    	{
    		try
    		{
    			loadShakeData( data );
	    		Toast.makeText( this, getString( R.string.touch_picture ), Toast.LENGTH_LONG ).show();
    		}
    		catch( Exception e )
    		{
    			EagleUtil.log( e );
    		}
    	}
    	else if( requestCode == eRequestCodeImageTriming )
    	{
    		try
    		{
	    		Uri	photoURI = initData.uri;
	    	//	Bitmap	bmp =	( Bitmap )data.getExtras().getParcelable( "data" );
	    		Bitmap	bmp =	BitmapFactory.decodeFile( eTrimDataPath );
	    		{
		    		int	height	=	bmp.getHeight(),
		    			width	=	bmp.getWidth();

		    		EagleUtil.log( "BitmapSize : " + width + " x " + height );

	    			Bitmap	temp = Bitmap.createBitmap( width, height, Config.RGB_565 );
	    			Canvas canvas = new Canvas( temp );
	    			Graphics	graphics = new Graphics();
	    			graphics.setCanvas( canvas );
	    			graphics.drawBitmap( bmp, 0, 0 );
	    			bmp = temp;
	    			System.gc();
	    		}

	    		if( bmp != null )
	    		{
	    			//!	ファイル名
	    			initData.onNewFile();
	    			initData.uri = photoURI;
	    			initData.bmp = bmp;
	    			Toast.makeText( this, getString( R.string.touch_picture ), Toast.LENGTH_LONG ).show();
	    			createThread();
	    		}
	    		else
	    		{
	    			EagleUtil.log( "URI null..." );
	    			setContentView( eMainRayoutID );
	    		}    		}
    		catch( Exception e )
    		{
    			EagleUtil.log( e );
    		}
    	}
    	else
    	{
    		EagleUtil.log( "RequestCode error : " + requestCode );
    		setContentView( eMainRayoutID );
    	}
    }

    /**
     * モードを変更する。
     */
    public	static	final	int		eMenuToggleMode		=	Menu.FIRST + 1;
    /**
     * ウェイトをリセットする。
     */
    public	static	final	int		eMenuResetWeight	=	Menu.FIRST + 2;
    /**
     * 画像を変更する。
     */
    public	static	final	int		eMenuChangePicture	=	Menu.FIRST + 3;

    /**
     * 振動タイプ変更のダイアログを開く。
     */
    public	static	final	int		eMenuShakeTypeDialog	=	Menu.FIRST + 4;

    /**
     * 保存ダイアログを開く。
     */
    public	static	final	int		eMenuSave				=	Menu.FIRST + 5;

    /**
     * 選択ダイアログを開く。
     */
    public	static	final	int		eMenuFileActivity		=	Menu.FIRST + 6;

    /**
     * 縦横のトグルを行う。
     */
    public	static	final	int		eMenuToggleVH			=	Menu.FIRST + 7;

    /**
     * 画像のトリミングを行う。
     */
    public	static	final	int		eMenuTrimImage			=	Menu.FIRST + 8;

    /**
     * 画像の共有を行う。
     */
    public	static	final	int		eMenuShareShakeData		=	Menu.FIRST + 9;

    /**
     *
     */
    private	Menu	menu	=	null;

    /**
     * メニューの生成を行う。
     * @author eagle.sakura
     * @param menu
     * @return
     * @version 2010/05/17 : 新規作成
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	// TODO 自動生成されたメソッド・スタブ
    	this.menu = menu;

    	IAppInfomation	info = new AppInfomation();
    	//!	画像選択
    	{
    		menu.add(	Menu.NONE,
    					eMenuChangePicture,
    					Menu.NONE,
    					getString( R.string.change_picture )
    				).setIcon( android.R.drawable.ic_menu_gallery );
    	}

    	//!	設定ダイアログ
    	{
    		menu.add(	Menu.NONE + 1,
    					eMenuShakeTypeDialog,
    					Menu.NONE,
    					getString( R.string.menu_shaketype )
    					).setIcon( android.R.drawable.ic_menu_preferences );
    	}

    	//!	ファイル読み込み
    	if( info.isSharewareMode() )
    	{
    		menu.add(	Menu.NONE,
    					eMenuFileActivity,
    					Menu.NONE,
    					getString( R.string.menutitle_load )
    					).setIcon( android.R.drawable.ic_menu_save );
    	}


    	//!	トグル
    	{
    		menu.add(	Menu.NONE + 4,
    					eMenuToggleVH,
    					Menu.NONE,
    					getString( R.string.str_inclination )
    					).setIcon( android.R.drawable.ic_menu_always_landscape_portrait );
    	}

		addShakeMenu();
    	return super.onCreateOptionsMenu(menu);
    }

    /**
     * メイン画面用のメニューを削除する。
     * @author eagle.sakura
     * @version 2010/06/06 : 新規作成
     */
    private	void	removeShakeMenu( )
    {
    	if( menu != null )
    	{
			menu.removeItem( eMenuToggleMode		);
			menu.removeItem( eMenuResetWeight		);
		//	menu.removeItem( eMenuShakeTypeDialog	);
			menu.removeItem( eMenuSave				);
			menu.removeItem( eMenuTrimImage			);
			menu.removeItem( eMenuShareShakeData	);
    	}
    }

    /**
     *
     * @author eagle.sakura
     * @version 2010/05/21 : 新規作成
     */
    private	void	addPlayMenu( )
    {
    	IAppInfomation	info = new AppInfomation();
		if( menu	!= null
	//	&&	thread	!= null
	//	&&	handler != null
		&&	loopManager != null
		)
		{
			removeShakeMenu();
			menu.removeItem( eMenuFileActivity		);
			menu.removeItem( eMenuToggleVH			);
			menu.removeItem( eMenuToggleMode		);
			menu.removeItem( eMenuShakeTypeDialog	);

	    	//!	ファイル読み込み
	    	if( info.isSharewareMode() )
	    	{
	    		menu.add(	Menu.NONE + 3,
	    					eMenuFileActivity,
	    					Menu.NONE,
	    					getString( R.string.menutitle_load )
	    					).setIcon( android.R.drawable.ic_menu_save );
	    	}

	    	//!	トグル
	    	{
	    		menu.add(	Menu.NONE + 4,
	    					eMenuToggleVH,
	    					Menu.NONE,
	    					getString( R.string.str_inclination )
	    					).setIcon( android.R.drawable.ic_menu_always_landscape_portrait );
	    	}

		}
    }
    /**
     *
     * @author eagle.sakura
     * @version 2010/05/21 : 新規作成
     */
    private	void	addShakeMenu( )
    {
    	if( shakeLooper != null )
    	{
    		if( shakeLooper.isPlayingOnly() )
    		{
    			addPlayMenu();
    			return;
    		}
    	}

    	IAppInfomation	info = new AppInfomation();
		if( menu	!= null
	//	&&	thread	!= null
	//	&&	handler != null
		&&	loopManager != null
		)
		{
			removeShakeMenu();
			menu.removeItem( eMenuFileActivity		);
			menu.removeItem( eMenuToggleVH			);
			menu.removeItem( eMenuShakeTypeDialog	);

	    	//!	設定ダイアログ
	    	{
	    		menu.add(	Menu.NONE + 1,
	    					eMenuShakeTypeDialog,
	    					Menu.NONE,
	    					getString( R.string.menu_shaketype )
	    					).setIcon( android.R.drawable.ic_menu_preferences );
	    	}


	    	//!	モードトグル
	    	{
	    		menu.add(	Menu.NONE + 2,
	    					eMenuToggleMode,
	    					Menu.NONE,
	    					getString( R.string.confimation_mode )
	    					).setIcon( android.R.drawable.ic_menu_preferences );
	    	}
	    	//!	リセット
	    	{
	    		menu.add(	Menu.NONE + 2,
	    					eMenuResetWeight,
	    					Menu.NONE,
	    					getString( R.string.weight_reset )
	    					).setIcon( android.R.drawable.ic_menu_revert );
	    	}
	    	//!	セーブを両方に開放する。
	    	if( info.isSharewareMode()
	    	||	UtilActivity.isSdkVersion2_x()	)
	    	{
	    		menu.add(	Menu.NONE + 3,
	    					eMenuSave,
	    					Menu.NONE,
	    					getString( R.string.menutitle_save )
	    					).setIcon( android.R.drawable.ic_menu_save );
	    	}

	    	//!	ファイル読み込み
	    	if( info.isSharewareMode() )
	    	{
	    		menu.add(	Menu.NONE + 3,
	    					eMenuFileActivity,
	    					Menu.NONE,
	    					getString( R.string.menutitle_load )
	    					).setIcon( android.R.drawable.ic_menu_save );
	    	}

	    	//!	トグル
	    	{
	    		menu.add(	Menu.NONE + 4,
	    					eMenuToggleVH,
	    					Menu.NONE,
	    					getString( R.string.str_inclination )
	    					).setIcon( android.R.drawable.ic_menu_always_landscape_portrait );
	    	}

	    	//!	画像トリミング
	    	{
	    		menu.add(	Menu.NONE + 4,
	    					eMenuTrimImage,
	    					Menu.NONE,
	    					getString( R.string.str_crop )
	    					).setIcon( android.R.drawable.ic_menu_crop );
	    	}

	    	//!	画像共有
	    	{
	    		menu.add(	Menu.NONE + 4,
	    					eMenuShareShakeData,
	    					Menu.NONE,
	    					getString( R.string.str_share )
	    					).setIcon( android.R.drawable.ic_menu_share );
	    	}
		}
    }
    /**
     * GL用スレッドの削除を行う。
     * @author eagle.sakura
     * @version 2010/05/21 : 新規作成
     */
    private void	removeGLThread(  )
    {
		//!	スレッドを殺す。
		if(
		//	thread != null
		//	handler != null
			loopManager != null
		)
		{
			EagleUtil.log( "remove thread" );
			shakeLooper	=	null;
			/*
			{
				thread.dispose();
				thread = null;
			}
			{
				handler.dispose();
				handler = null;
			}
			*/
			{
				loopManager.dispose();
				loopManager = null;
			}
    		setContentView( R.layout.mainview );
		}
		//	カラのビューを入れる。
	 //	setContentView( new View( this ) );
    }

    /**
     *
     * @author eagle.sakura
     * @return
     * @version 2010/06/16 : 新規作成
     */
    public	ShakeLooper	getLooper( )
    {
    	return	shakeLooper;
    }

    /**
     * メニューが選択された。
     * @author eagle.sakura
     * @param item
     * @return
     * @version 2010/05/17 : 新規作成
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	switch( item.getItemId() )
    	{
    	case	eMenuToggleMode:
    		initData.option.toggleBlueMode();
    		return	true;
    	case	eMenuResetWeight:
    		if( shakeLooper != null )
    		{
    			if( sharedData.isEnableResetDialog() )
    			{
    				( new	ResetCheckDialog( this ) ).create();
    			}
    			else
    			{
    				shakeLooper.reset();
    			}
    		}
    		return	true;
    	case	eMenuChangePicture:
    	//	removeGLThread();
    		//!	アクティビティを作成
    		createPickPictureActivity();
    		return	true;
    	case	eMenuShakeTypeDialog:
    		//!	設定ダイアログを開く。
    		startShakeTypeDialog();
    		return	true;
    	case	eMenuSave:
    		startSaveDialog();
    		return	true;
    	case	eMenuFileActivity:
    		createLoadFileActivity();
    		return	true;
    	case	eMenuToggleVH:
    		UtilActivity.toggleOrientationFixed( this );
    		return	true;
    	case	eMenuTrimImage:
    		startTrimImageActivity( );
    		return	true;
    	case	eMenuShareShakeData:
    		startShareActivity();
    		return	true;
    	}

    	// TODO 自動生成されたメソッド・スタブ
    	return super.onOptionsItemSelected(item);
    }
}