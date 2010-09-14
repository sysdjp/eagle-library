/**
 *
 * @author eagle.sakura
 * @version 2010/06/05 : 新規作成
 */
package eagle.android.app.appinfo;

import com.admob.android.ads.AdView;

import android.app.Activity;
import android.view.View;
import eagle.android.appcore.IAppInfomation;
import eagle.android.util.UtilBridgeAndroid;
import eagle.util.IEagleUtilBridge;

/**
 * @author eagle.sakura
 * @version 2010/06/05 : 新規作成
 */
public class AppInfomation implements IAppInfomation
{

	/**
	 * @author eagle.sakura
	 * @param activity
	 * @return
	 * @version 2010/06/05 : 新規作成
	 */
	@Override
	public View createAdView(Activity activity)
	{
		// TODO 自動生成されたメソッド・スタブ
		AdView	ad = new	AdView( activity );
		ad.setVisibility( View.VISIBLE );
		ad.setKeywords("Android application");
		ad.bringToFront();
		ad.requestFocus();
		ad.invalidate();
		return ad;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/05 : 新規作成
	 */
	@Override
	public boolean isSharewareMode()
	{
		//!	無料モードであるため、falseを返す。
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/09/12 : 新規作成
	 */
	public	static	IEagleUtilBridge		createBridge( )
	{
		return	new	UtilBridgeAndroid( "ShakeDroid" );
	}
}
