/**
 *
 * @author eagle.sakura
 * @version 2010/05/28 : 新規作成
 */
package eagle.android.app.shake;

import eagle.android.app.appinfo.AppInfomation;
import eagle.android.appcore.IAppInfomation;
import eagle.android.appcore.shake.Option;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * @author eagle.sakura
 * @version 2010/05/28 : 新規作成
 */
public class OptionDialog		implements	OnClickListener
										,	android.view.View.OnClickListener
{
	private		ShakeDroid		activity	=	null;
	private		AlertDialog		dialog		=	null;	//!<	作成したダイアログ。
	private		View			rootLayout	=	null;

	/**
	 *
	 * @author eagle.sakura
	 * @param activity
	 * @param option
	 * @version 2010/05/28 : 新規作成
	 */
	public	OptionDialog( ShakeDroid	activity )
	{
		this.activity = activity;
	}

	/**
	 * リセットボタンが押された。
	 * @author eagle.sakura
	 * @param v
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public void onClick(View v)
	{
		// TODO 自動生成されたメソッド・スタブ

		//!	オプションをすべてリセット。
		activity.getInitializeData().option.reset();

		//!	ダイアログのビューをリセット
		resetView( rootLayout );
	}

	/**
	 * Viewを更新する。
	 * @author eagle.sakura
	 * @param layout
	 * @version 2010/05/30 : 新規作成
	 */
	public	void	resetView( View layout )
	{
    	Option	option = activity.getInitializeData().option;

    	{
    	//!	シェイクの種類
	    	RadioButton	button = null;
	    	switch( option.getShakeType() )
	    	{
	    	case	Option.eShakeTypeAlways:
	    		button = ( RadioButton )layout.findViewById( R.id.type_always );
	    		break;
	    	case	Option.eShakeTypeSlide:
	    		button = ( RadioButton )layout.findViewById( R.id.type_slide );
	    		break;
	    	case	Option.eShakeTypeAccel:
	    		button = ( RadioButton )layout.findViewById( R.id.type_accel );
	    		break;
	    	}
	    	if( button != null )
	    	{
	    		button.setChecked( true );
	    	}
    	}

    	{
    	//!	加速度センサー感度
    		SeekBar		bar	=	( SeekBar )layout.findViewById( R.id.accel_sensitivity );
    		float		real = 	option.getShakeSensitivity() - 1.0f;
    		int			now = ( int )( ( 1.0f - real ) * 100.0f );
    		bar.setProgress( now );
    	}
    	{
    	//!	揺れの倍率設定
    		SeekBar		bar	=	( SeekBar )layout.findViewById( R.id.shake_mul );
    		bar.setProgress( ( int )( 100.0f * option.getShakeMul( ) ) );
    	}
    	{
    	//!	揺れの減衰値設定
    		SeekBar		bar =	( SeekBar )layout.findViewById( R.id.option_weightMulShakeBar );
    		bar.setProgress( ( int )( 100.0f * option.getWeightMul()) );
    	}
    	{
    	//!	タッチでの影響度設定
    		SeekBar		bar	=	( SeekBar )layout.findViewById( R.id.option_touchWeightAddBar );
    		bar.setProgress( ( int )( 100.0f * option.getTouchWeightAdd( )) );
    	}
    	/*
    	{
    	//!	端末縦横固定
    		CheckBox	box =	( CheckBox )layout.findViewById( R.id.option_enableOrientation );
    		box.setChecked( option.isOrientationLock( ) );
    	}
    	*/
    	//!	リセットダイアログ設定
    	{
    		CheckBox	box =	( CheckBox )layout.findViewById( R.id.option_enable_resetdialog );
    		box.setChecked( activity.getSharedData().isEnableResetDialog( ) );
    	}

	}

	/**
	 * ダイアログの作成と表示を行う。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/28 : 新規作成
	 */
	public	AlertDialog	create( )
	{

    	Builder	builder	= new Builder( activity );

    	Option	option = activity.getInitializeData().option;

    	View	layout = View.inflate( activity, R.layout.controll_dialog, null );
    	rootLayout	=	layout;

    	resetView( layout );

    	/*
    	{
    		//!	adviewを作成
    		IAppInfomation	info = new AppInfomation();
    		View	ad = info.createAdView( activity );
    		if( ad != null )
    		{
    			LinearLayout	ll = ( LinearLayout )layout;
    			ll.addView( ad, 0 );
    		}
    	}
    	*/
    	{
    		//!	ボタンにリセット用コールバックを指定
    		Button		reset = ( Button )layout.findViewById( R.id.option_resetButton );
    		reset.setOnClickListener( this );
    	}

    	builder	.setTitle( activity.getResources().getString( R.string.savedialog_title ) )
    			.setView( layout )
    			.setPositiveButton( activity.getResources().getString( R.string.dialog_ok ),		this )
    			.setNegativeButton( activity.getResources().getString( R.string.dialog_cancel ),	this );

    	dialog	=	builder.show();
    	return	dialog;
	}


	/**
	 * ダイアログがクリックされた。
	 * @author eagle.sakura
	 * @param dialog
	 * @param which
	 * @version 2010/05/28 : 新規作成
	 */
	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// TODO 自動生成されたメソッド・スタブ

		//!	決定ボタン以外ならキャンセル扱い。
    	if( which != AlertDialog.BUTTON_POSITIVE )
    	{
    		return;
    	}

	//!	決定ボタンが押された。
		Option	option = activity.getInitializeData().option;
		AlertDialog	alert = ( AlertDialog )dialog;


		RadioGroup	group = 	( RadioGroup )alert.findViewById( R.id.shaketype_group );
		switch( group.getCheckedRadioButtonId( ) )
		{
		case	R.id.type_always:
			option.setShakeType( Option.eShakeTypeAlways );
			break;
		case	R.id.type_slide:
			option.setShakeType( Option.eShakeTypeSlide );
			break;
		case	R.id.type_accel:
			option.setShakeType( Option.eShakeTypeAccel );
			break;
		}


		{
			SeekBar		seek = ( SeekBar )alert.findViewById( R.id.accel_sensitivity );
			int	set =  100 - seek.getProgress( );
			float real = ( float )set / 100.0f;
			//!	感度指定
			option.setShakeSensitivity( 1.0f + real );
		}

		{
			SeekBar		mul = ( SeekBar )alert.findViewById( R.id.shake_mul );
			//!	振動倍率
			option.setShakeMul( ( float )( mul.getProgress( ) ) / 100.0f );
		}

    	{
    	//!	揺れの減衰値設定
    		SeekBar		bar =	( SeekBar )alert.findViewById( R.id.option_weightMulShakeBar );
    	//	bar.setProgress( ( int )( 100.0f * option.getWeightMul()) );
    		option.setWeightMul( ( float )( bar.getProgress() ) / 100.0f );
    	}
    	{
    	//!	タッチでの影響度設定
    		SeekBar		bar	=	( SeekBar )alert.findViewById( R.id.option_touchWeightAddBar );
    	//	bar.setProgress( ( int )( 100.0f * option.getTouchWeightAdd( )) );
    		option.setTouchWeightAdd( ( float )( bar.getProgress() ) / 100.0f );
    	}
    	/*
    	{
    	//!	端末縦横固定
    		CheckBox	box =	( CheckBox )alert.findViewById( R.id.option_enableOrientation );
    	//	box.setChecked( option.isOrientationLock( ) );
    		option.setOrientationLock( box.isChecked( ) );
    	}
    	*/
    	//!	リセットダイアログ設定
    	{
    		CheckBox	box =	( CheckBox )alert.findViewById( R.id.option_enable_resetdialog );
    		activity.getSharedData().setEnableResetDialog( box.isChecked() );
    	}

    	//!	オプションを確定させる。
    	activity.onOptionChange( option );

    	//!	値をセーブする
    	option.save( activity );

    	//!	セーブを通知する
    	Toast.makeText( activity,
    					activity.getResources().getString( R.string.toast_saveOption ),
    					Toast.LENGTH_SHORT ).show();
	}
}
