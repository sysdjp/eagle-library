package eagle.android.app.shake;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URI;
import java.nio.channels.FileChannel;

import eagle.android.app.appinfo.AppInfomation;
import eagle.android.appcore.IAppInfomation;
import eagle.android.appcore.shake.Option;
import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.appcore.shake.ShakeInitialize;
import eagle.android.appcore.shake.ShakeLooper;
import eagle.android.thread.ILooper;
import eagle.android.thread.LooperThread;
import eagle.android.thread.ILooper.ILooperListener;
import eagle.android.util.UtilActivity;
import eagle.android.util.UtilBridgeAndroid;
import eagle.android.view.OpenGLView;
import eagle.io.DataInputStream;
import eagle.io.DataOutputStream;
import eagle.io.FileAccessStream;
import eagle.io.InputStreamBufferReader;
import eagle.io.OutputStreamBufferWriter;
import eagle.util.EagleUtil;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.DataSetObserver;
import android.graphics.Region.Op;
import android.net.Uri;
import android.opengl.GLUtils;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.Media;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
	 * メインスレッド。
	 */
	private	LooperThread				thread		=	null;
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

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
 //       setContentView(R.layout.main);
    	//!	Urilを初期化
		UtilActivity.setOrientationFixed( this, true );
    	EagleUtil.init( new UtilBridgeAndroid( "ShakeDroid" ) );
    	EagleUtil.log( "onCreate" );

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
    	if( action.equals( Intent.ACTION_SEND ) )
    	{
    		EagleUtil.log( "Open Intent..." );
			  String	extra =	intent.getParcelableExtra( Intent.EXTRA_STREAM ).toString();
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
		if( !info.isSharewareMode() )
		{
			if( UtilActivity.isSdkVersion2_x( ) )
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
		*/
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
			shakeLooper.onOptionChange( option );
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
     * 表示用スレッドを作成。
     * @author eagle.sakura
     * @version 2010/05/20 : 新規作成
     */
    protected	void	createThread( )
    {
    	removeGLThread();

    	EagleUtil.log( "create thread" );
    	OpenGLView	glView = new	OpenGLView( this );
    	shakeLooper	=	new	ShakeLooper( this, glView.getGLManager(), initData );
    	thread		=	new	LooperThread( shakeLooper );
    	thread.addSurface( glView );

		setContentView( glView );
	//	initFreeSpaceAdview( );

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

		//!	スレッド処理開始
    	thread.start();

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
		//	UtilActivity.setOrientationFixed( this, true );
	    	removeGLThread();

	    	ShakeDataFile	sdf = new	ShakeDataFile( intent.getByteArrayExtra( eLoadFileResultKey_FileBuffer ) );
	    	sdf.deserialize();

	    	EagleUtil.log( "create thread" );
	    	OpenGLView	glView = new	OpenGLView( this );
	    	shakeLooper	=	sdf.createLooper( this, glView.getGLManager() );
	    	initData	=	shakeLooper.getInitializeData(	);
	    	initData.originFileName	=	intent.getStringExtra( eLoadFileResultKey_FileName );
	    	thread		=	new	LooperThread( shakeLooper );
	    	thread.addSurface( glView );
			setContentView( glView );
	//		initFreeSpaceAdview( );

			//!	スレッド処理開始
	    	thread.start();

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
    	// TODO 自動生成されたメソッド・スタブ
    	super.onStop();
    }

    @Override
    protected void onResume()
    {
    	// TODO 自動生成されたメソッド・スタブ
    	super.onResume();
    }

    @Override
    protected void onRestart()
    {
    	EagleUtil.log( "onRestart" );
    	// TODO 自動生成されたメソッド・スタブ
    	super.onRestart();
    	if( initData.uri	!= null
    	&&	thread			== null	)
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
			menu.removeItem( eMenuShakeTypeDialog	);
			menu.removeItem( eMenuSave				);
    	}
    }

    /**
     *
     * @author eagle.sakura
     * @version 2010/05/21 : 新規作成
     */
    private	void	addShakeMenu( )
    {
    	IAppInfomation	info = new AppInfomation();
		if( menu	!= null
		&&	thread	!= null	)
		{
			removeShakeMenu();
			menu.removeItem( eMenuFileActivity		);
			menu.removeItem( eMenuToggleVH			);
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
		if( thread != null )
		{
			EagleUtil.log( "remove thread" );
			shakeLooper	=	null;
			thread.dispose();
			thread = null;
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
    	}

    	// TODO 自動生成されたメソッド・スタブ
    	return super.onOptionsItemSelected(item);
    }
}