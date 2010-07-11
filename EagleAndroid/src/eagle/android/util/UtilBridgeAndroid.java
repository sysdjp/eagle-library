/**
 *
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
package eagle.android.util;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.util.Log;
import eagle.util.IEagleUtilBridge;

/**
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
public class UtilBridgeAndroid		implements	IEagleUtilBridge
{
	private		String		tag	=	"";

	/**
	 * ログ出力用のクラス。
	 * @author eagle.sakura
	 * @param tag
	 * @version 2010/05/30 : 新規作成
	 */
	public	UtilBridgeAndroid( String tag )
	{
		this.tag = tag;
	}

	/**
	 * 実際の出力を行う。
	 * @author eagle.sakura
	 * @param eLogType
	 * @param message
	 * @version 2010/05/30 : 新規作成
	 */
	@Override
	public void log(int eLogType, String message)
	{
		// TODO 自動生成されたメソッド・スタブ
		switch( eLogType )
		{
		case	eLogTypeDebug:
			Log.d( tag, message );
			return;
		case	eLogTypeInfo:
			Log.i( tag, message );
			return;
		}
	}

}
