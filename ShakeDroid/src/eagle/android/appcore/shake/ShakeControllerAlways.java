/**
 *
 * @author eagle.sakura
 * @version 2010/05/16 : 新規作成
 */
package eagle.android.appcore.shake;


/**
 * 常に揺らすためのコントローラ。
 * @author eagle.sakura
 * @version 2010/05/16 : 新規作成
 */
public class ShakeControllerAlways extends IShakeController
{
	/**
	 * 現在のフレーム。
	 */
	private		int		frame			=	0;

	/**
	 *
	 * @author eagle.sakura
	 * @param sv
	 * @version 2010/05/16 : 新規作成
	 */
	public	ShakeControllerAlways(  )
	{
	}

	@Override
	public int getShakeType()
	{
		// TODO 自動生成されたメソッド・スタブ
		return Option.eShakeTypeAlways;
	}

	@Override
	public boolean isRotateEnable()
	{
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	/**
	 * 更新を行う。
	 * @author eagle.sakura
	 * @version 2010/05/16 : 新規作成
	 */
	@Override
	public	void		update( ShakeLooper	mt )
	{
		++frame;
		float	sin	=	( float )Math.sin( ( double )( frame ) / Math.PI * 1.5 );
		getShakeVector().x = 0.0f;
		getShakeVector().y = sin;
	}

}
