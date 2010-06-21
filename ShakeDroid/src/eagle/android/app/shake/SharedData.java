/**
 *
 * @author eagle.sakura
 * @version 2010/06/16 : 新規作成
 */
package eagle.android.app.shake;

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
	 * Admobの開始を告げる。
	 * @author eagle.sakura
	 * @version 2010/06/16 : 新規作成
	 */
	public	void		sendAdMobStartMessage( )
	{

	}
}
