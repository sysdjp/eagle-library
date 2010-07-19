/**
 *
 * @author eagle.sakura
 * @version 2010/06/16 : 新規作成
 */
package eagle.android.app.shake;

import eagle.android.util.UtilActivity;
import eagle.math.Vector2;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;

/**
 * @author eagle.sakura
 * @version 2010/06/16 : 新規作成
 */
public class SharedData
{
	private	SharedPreferences					manager	=	null;

	/**
	 *
	 */
	private	static	final	String		eFileKey	=	"eagle.android.app.shake";

	/**
	 *
	 * @author eagle.sakura
	 * @param sp
	 * @version 2010/06/16 : 新規作成
	 */
	public	SharedData( Context	context, OnSharedPreferenceChangeListener listener )
	{
		manager	=	context.getSharedPreferences( eFileKey, 0 );
	}

	/**
	 * リセットダイアログを出すかを調べる。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/16 : 新規作成
	 */
	public	boolean		isEnableResetDialog( )
	{
		return	manager.getBoolean( "isEnableResetDialog", false );
	}

	/**
	 * リセットダイアログの表示・非表示を設定する。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/06/16 : 新規作成
	 */
	public	void		setEnableResetDialog( boolean set )
	{
		manager.edit().putBoolean( "isEnableResetDialog", set ).commit();
	}

	/**
	 * 顔認識を行うか。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/17 : 新規作成
	 */
	public	boolean		isEnableFaceDetect( )
	{
		return	manager.getBoolean( "isEnableFaceDetect", false );
	}

	/**
	 * 顔認識の有無を設定する。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/07/17 : 新規作成
	 */
	public	void		setEnableFaceDetect( boolean set )
	{
		manager.edit().putBoolean( "isEnableFaceDetect", set ).commit();
	}

	/**
	 * 揺れピクセルの密度を0.0f～1.0fで指定する。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/07/15 : 新規作成
	 */
	public	void		setShakePixelDensity( float set )
	{
		manager.edit().putFloat( "shakePixelDensity", set ).commit();
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/15 : 新規作成
	 */
	public	float		getShakePixelDensity( )
	{
		return	manager.getFloat( "shakePixelDensity", 0.5f );
	}


	/**
	 * メモリ節約レベルを設定する。
	 * @author eagle.sakura
	 * @param set
	 * @version 2010/07/17 : 新規作成
	 */
	public	void		setMemorySavingLevel( float set )
	{
		manager.edit().putFloat( "memorySavingLevel", set ).commit();
	}

	/**
	 * メモリ節約レベルを取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/07/17 : 新規作成
	 */
	public	float		getMemorySavingLevel( )
	{
		return	manager.getFloat( "memorySavingLevel", 0.5f );
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param displayW
	 * @param displayH
	 * @param result
	 * @return
	 * @version 2010/07/15 : 新規作成
	 */
	public	Vector2		getShakePixelDivisions( int displayW, int displayH, Vector2 result )
	{
		int	pixels = ( int )( 25.0f - getShakePixelDensity() * 10.0f );

		result.x = ( int )( ( displayW / pixels ) - 1 );
		result.y = ( int )( ( displayH / pixels ) - 1 );

		return	result;
	}

	/**
	 * Admobの開始を告げる。
	 * @author eagle.sakura
	 * @version 2010/06/16 : 新規作成
	 */
	public	void		sendAdMobStartMessage( )
	{

	}
}
