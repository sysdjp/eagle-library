/**
 *
 * @author eagle.sakura
 * @version 2010/06/05 : 新規作成
 */
package eagle.android.app.appinfo;

import android.app.Activity;
import android.view.View;
import eagle.android.appcore.IAppInfomation;

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
		/*
		// TODO 自動生成されたメソッド・スタブ
		AdView	ad = new	AdView( activity );
	//	ad.layout( 0, 0, 320, 48 );
		ad.setVisibility( View.VISIBLE );
		ad.setKeywords("Android application");
		ad.bringToFront();
		ad.requestFocus();
	//	ad.requestFreshAd();
		ad.invalidate();
		return ad;
		*/
		return	null;
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

}
