/**
 *
 * @author eagle.sakura
 * @version 2010/06/06 : 新規作成
 */
package eagle.android.app.shake;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import eagle.android.util.UtilActivity;

/**
 * @author eagle.sakura
 * @version 2010/06/06 : 新規作成
 */
public class LoadFileSelectActivity extends UtilActivity
{
	/**
	 * 作成時の処理。
	 * @author eagle.sakura
	 * @param savedInstanceState
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO 自動生成されたメソッド・スタブ
		super.onCreate(savedInstanceState);

		ListView			lv 		= new	ListView( this );
		LoadFileListAdaptar	adaptar	= new	LoadFileListAdaptar( this );
		lv.setAdapter( adaptar );
		setContentView( lv );
	}

	/**
	 * 戻り値用のファイルバッファを設定する。
	 * @author eagle.sakura
	 * @param buffer
	 * @version 2010/06/06 : 新規作成
	 */
	public	void	setResultBuffer( String fileName, byte[] buffer )
	{
		Intent	intent = new Intent();
		intent.putExtra( ShakeDroid.eLoadFileResultKey_FileName,	fileName );
		intent.putExtra( ShakeDroid.eLoadFileResultKey_FileBuffer,	buffer );
		setResult( RESULT_OK, intent );
	}
}
