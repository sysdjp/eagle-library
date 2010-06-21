/**
 *
 * @author eagle.sakura
 * @version 2010/06/15 : 新規作成
 */
package eagle.android.app.shake;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;

/**
 * @author eagle.sakura
 * @version 2010/06/15 : 新規作成
 */
public class ResetCheckDialog		implements	OnClickListener
{
	private	ShakeDroid		activity = null;

	/**
	 *
	 * @author eagle.sakura
	 * @param act
	 * @version 2010/06/15 : 新規作成
	 */
	public	ResetCheckDialog( ShakeDroid act )
	{
		activity = act;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/15 : 新規作成
	 */
	public	AlertDialog		create( )
	{
		Builder		builder = new	Builder( activity );

		builder	.setTitle( activity.getResources().getString( R.string.str_reset_check ) )
				.setPositiveButton( activity.getResources().getString( R.string.dialog_ok ), this )
				.setNegativeButton( activity.getResources().getString( R.string.dialog_cancel ), null );

		AlertDialog	dialog = builder.create();
		dialog.show();
		return	dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which)
	{
		// TODO 自動生成されたメソッド・スタブ
		if( which != AlertDialog.BUTTON_POSITIVE )
		{
			return;
		}

		//!	OKボタンが押された
		if( activity.getLooper() != null )
		{
			activity.getLooper().reset();
		}
	}
}
