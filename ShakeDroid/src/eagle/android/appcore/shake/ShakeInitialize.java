/**
 *
 * @author eagle.sakura
 * @version 2010/05/16 : 新規作成
 */
package eagle.android.appcore.shake;

import eagle.android.util.UtilActivity;
import android.graphics.Bitmap;
import android.net.Uri;

/**
 * @author eagle.sakura
 * @version 2010/05/16 : 新規作成
 */
public class ShakeInitialize
{
	/**
	 * 処理対象のURI。<BR>
	 * 再起動対策で保持しておく。
	 */
	public	Uri				uri		=	null;

	/**
	 * URIより優先される画像情報。
	 */
	public	Bitmap			bmp		=	null;

	/**
	 * ユーザーの設定したオプション情報。
	 */
	public	Option			option	=	new	Option();

	/**
	 * 縦方向分割数。
	 */
	public	int				xDivision	=	-1;

	/**
	 * 横方向分割数。
	 */
	public	int				yDivision	=	-1;

	/**
	 * 重みテーブル。
	 */
	public	float[]			weights		=	null;

	/**
	 * 縦向き画面の場合trueとなる。
	 */
	public	boolean			isVertical	=	false;

	/**
	 * 読み込んだファイル名。<BR>
	 * 新規の場合は空文字。
	 */
	public	String			originFileName	=	"";

	/**
	 * 顔認識を行うか。
	 */
	public	boolean			isFaceDetect	=	false;

	/**
	 * ディスプレイ幅。
	 */
	public	int				displayWidth	=	-1;

	/**
	 * ディスプレイ高。
	 */
	public	int				displayHeight	=	-1;

	public	ShakeInitialize( )
	{
	}

	public	void	onNewFile( )
	{
		originFileName	= "";
		weights			= null;
		bmp				= null;
		xDivision		= -1;
		yDivision		= -1;
		isFaceDetect	= false;

		displayWidth	= -1;
		displayHeight	= -1;
	}
}
