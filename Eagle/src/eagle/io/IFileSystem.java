/**
 *
 * @author eagle.sakura
 * @version 2010/04/05 : 新規作成
 */
package eagle.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import eagle.util.Disposable;
import eagle.util.EagleException;

/**
 * ファイルIO用のストリームを作成する。<BR>
 * 各プラットフォームごとに入出力先は最適化すること。
 * @author eagle.sakura
 * @version 2010/04/05 : 新規作成
 */
public	abstract class IFileSystem	implements		Disposable
{
	private	int		uniqueID = -1;
	/**
	 * @author eagle.sakura
	 * @param uniqueID 一意の識別子。乱数で問題ない。
	 * @version 2010/04/05 : 新規作成
	 */
	protected	IFileSystem( int uniqueID )
	{
		this.uniqueID = uniqueID;
	}

	/**
	 * クラスの識別子を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/04/05 : 新規作成
	 */
	public	int			getUniqueID( )
	{
		return	uniqueID;
	}

	/**
	 * 管理している資源を開放する。
	 * @author eagle.sakura
	 * @version 2010/04/05 : 新規作成
	 */
	public	abstract	void		dispose( );


	/**
	 * 入力用ストリームを作成する。
	 * @author eagle.sakura
	 * @param filePath ファイルへのパス
	 * @return 入力用ストリーム
	 * @throws IOException
	 * @throws EagleException
	 * @version 2010/04/05 : 新規作成
	 */
	public	abstract	InputStream		createInputStream( String filePath )	throws	IOException,
																						EagleException;

	/**
	 * 出力用ストリームを作成する。
	 * @author eagle.sakura
	 * @param filePath ファイルへのパス
	 * @return 出力用のストリーム
	 * @throws IOException
	 * @throws EagleException
	 * @version 2010/04/05 : 新規作成
	 */
	public	abstract	OutputStream	createOutputStream( String filePath )	throws	IOException,
																						EagleException;
}
