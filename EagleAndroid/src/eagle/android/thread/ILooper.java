/**
 *
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
package eagle.android.thread;

import eagle.android.device.TouchDisplay;

/**
 * @author eagle.sakura
 * @version 2010/05/31 : 新規作成
 */
public abstract class ILooper
{
	private	TouchDisplay		touchDisplay	=	new	TouchDisplay(	);

	/**
	 * タッチディスプレイ管理を取得する。
	 * @author eagle.sakura
	 * @return
	 * @version 2010/05/31 : 新規作成
	 */
	public	TouchDisplay		getTouchDisplay( )	{	return	touchDisplay;	}

	/**
	 * ループの最初に呼ばれる。
	 * @author eagle.sakura
	 * @version 2010/05/31 : 新規作成
	 */
	public	abstract	void	onInitialize( );

	/**
	 * 毎フレームの定期処理が呼ばれる。
	 * @author eagle.sakura
	 * @version 2010/05/31 : 新規作成
	 */
	public	abstract	void	onLoop( );

	/**
	 * ループの終了時に呼ばれる。
	 * @author eagle.sakura
	 * @version 2010/05/31 : 新規作成
	 */
	public	abstract	void	onFinalize( );


	/**
	 * Looperの状態通知を受ける。
	 * @author eagle.sakura
	 * @version 2010/06/20 : 新規作成
	 */
	public	interface	ILooperListener
	{
		/**
		 * 初期化前に呼ばれる。
		 * @author eagle.sakura
		 * @param thread
		 * @param looper
		 * @version 2010/06/20 : 新規作成
		 */
		public	void		onInitialize( Object thread, ILooper looper );

		/**
		 * 毎フレームの処理時に呼ばれる。
		 * @author eagle.sakura
		 * @param thread
		 * @param looper
		 * @version 2010/06/20 : 新規作成
		 */
		public	void		onLoop( Object thread, ILooper looper );

		/**
		 * 処理終了時に呼ばれる。
		 * @author eagle.sakura
		 * @param thread
		 * @param looper
		 * @version 2010/06/20 : 新規作成
		 */
		public	void		onFinalize( Object thread, ILooper looper );
	}
}
