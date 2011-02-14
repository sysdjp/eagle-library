/**
 *
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
package eagle.util;

import java.util.Random;

/**
 * @author eagle.sakura
 * @version 2009/11/14 : 新規作成
 */
public class EagleUtil
{
	/**
	 * GLの固定小数点で1.0を示す。
	 */
	public	static	final	int			eGLFixed1_0	=	0x10000;
	/**
	 * MCの固定小数点で1.0を示す。
	 */
	public	static	final	int			eMCFixed1_0	=	0x1000;

	/**
	 * ShiftJISを示すエンコード文字列。<BR>
	 * C++関連のライブラリと連携するため、<BR>
	 * 基本的にSJISをIO用の文字コードに使用する。
	 */
	public	static	final	String		eEncodeSJIS	=	"Shift_JIS";

	/**
	 * 標準のユーティリティを作成する。
	 */
	private	static	IEagleUtilBridge	utilBridge	=	new IEagleUtilBridge()
	{
		/**
		 * ログはprintlnで行う。
		 * @author eagle.sakura
		 * @param eLogType
		 * @param message
		 * @version 2010/03/31 : 新規作成
		 */
		@Override
		public void log(  int eLogType,  String message)
		{
		// TODO 自動生成されたメソッド・スタブ
			System.out.println( message );
		}

		/**
		 *
		 * @author eagle.sakura
		 * @return
		 * @version 2010/09/12 : 新規作成
		 */
		@Override
		public int getPlatformVersion()
		{
			return	0;
		}
	};

	/**
	 * 手軽な乱数生成。<BR>
	 * この乱数に再現性はない。
	 */
	private	static	Random		rand = new Random();

	/**
	 * ユーティリティ初期化を行う。
	 * @author eagle.sakura
	 * @param bridge
	 * @version 2010/03/20 : 新規作成
	 */
	public	static	void	init( IEagleUtilBridge bridge )
	{
		utilBridge = bridge;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	static	IEagleUtilBridge	getBridge( )
	{
		return	utilBridge;
	}

	/**
	 * 乱数を取得する。<BR>
	 * この乱数の再現性は保証されない。
	 * @author eagle.sakura
	 * @return
	 * @version 2009/12/01 : 新規作成
	 */
	public	static	final	int		getRand( )
	{
		return	rand.nextInt();
	}

	/**
	 * min～maxまでの乱数を取得する。
	 * @author eagle.sakura
	 * @param min
	 * @param max
	 * @return
	 * @version 2009/12/01 : 新規作成
	 */
	public	static	final		int		getRand( int min, int max )
	{
		return	min + ( ( getRand() & 0x7fffffff ) % ( max - min ) );
	}

	/**
	 * 0～255の色情報をひとつのintにまとめる。
	 * @author eagle.sakura
	 * @param r
	 * @param g
	 * @param b
	 * @param a
	 * @return
	 * @version 2010/10/11 : 新規作成
	 */
	public	static	final		int		toColorRGBA( int r, int g, int b, int a )
	{
		return		r << 24
				|	g << 16
				|	b << 8
				|	a;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param rgba
	 * @return
	 * @version 2010/10/11 : 新規作成
	 */
	public	static	final		int		toColorR( int rgba ){	return	( rgba >> 24 ) & 0xff;	}

	/**
	 *
	 * @author eagle.sakura
	 * @param rgba
	 * @return
	 * @version 2010/10/11 : 新規作成
	 */
	public	static	final		int		toColorG( int rgba ){	return	( rgba >> 16 ) & 0xff;	}

	/**
	 *
	 * @author eagle.sakura
	 * @param rgba
	 * @return
	 * @version 2010/10/11 : 新規作成
	 */
	public	static	final		int		toColorB( int rgba ){	return	( rgba >> 8 ) & 0xff;	}

	/**
	 *
	 * @author eagle.sakura
	 * @param rgba
	 * @return
	 * @version 2010/10/11 : 新規作成
	 */
	public	static	final		int		toColorA( int rgba ){	return	( rgba >> 0 ) & 0xff;	}

	/**
	 * min <= result <= maxとなるようにnowを補正する。
	 * @author eagle.sakura
	 * @param min
	 * @param max
	 * @param now
	 * @return
	 * @version 2009/12/06 : 新規作成
	 */
	public	static	final		int		minmax( int min, int max, int now )
	{
		if( now < min )
			return	min;
		if( now > max )
			return	max;
		return	now;
	}
	/**
	 * min <= result <= maxとなるようにnowを補正する。
	 * @author eagle.sakura
	 * @param min
	 * @param max
	 * @param now
	 * @return
	 * @version 2009/12/06 : 新規作成
	 */
	public	static	final		float		minmax( float min, float max, float now )
	{
		if( now < min )
			return	min;
		if( now > max )
			return	max;
		return	now;
	}

	/**
	 * 目標数値へ移動する。
	 * @author eagle.sakura
	 * @param now
	 * @param offset
	 * @param target
	 * @return
	 * @version 2009/12/02 : 新規作成
	 */
	public	static	final		int		targetMove( int now, int offset, int target )
	{
		offset = Math.abs( offset );
		if(	Math.abs(target - now) <= offset	)
		{
			return	target;
		}
		else if(target > now)
		{
			return	now + offset;
		}
		else
		{
			return	now - offset;
		}
	}

	/**
	 * 目標数値へ移動する。
	 * @author eagle.sakura
	 * @param now
	 * @param offset
	 * @param target
	 * @return
	 * @version 2009/12/02 : 新規作成
	 */
	public	static	final		float		targetMove( float now, float offset, float target )
	{
		offset = Math.abs( offset );
		if(	Math.abs(target - now) <= offset	)
		{
			return	target;
		}
		else if(target > now)
		{
			return	now + offset;
		}
		else
		{
			return	now - offset;
		}
	}

	/**
	 * 360度系の正規化を行う。
	 * @author eagle.sakura
	 * @param now
	 * @return
	 * @version 2009/11/29 : 新規作成
	 */
	public	static	final		float	normalizeDegree( float now )
	{
		while( now < 0.0f )
		{
			now += 360.0f;
		}

		while( now > 360.0f )
		{
			now -= 360.0f;
		}

		return	now;
	}

	/**
	 * フラグが立っていることを検証する。
	 * @author eagle.sakura
	 * @param flg
	 * @param check
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	static	final		boolean	isFlagOn( int flg, int check )
	{
		return	( flg & check ) != 0;
	}

	/**
	 * フラグが立っていることを検証する。
	 * @author eagle.sakura
	 * @param flg
	 * @param check
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	static	final		boolean	isFlagOnAll( int flg, int check )
	{
		return	( flg & check ) == 0;
	}

	/**
	 * ログメッセージを出力する。
	 * @author eagle.sakura
	 * @param message
	 * @version 2010/03/13 : 新規作成
	 */
	public	static	final		void	logInfo( String message )
	{
		if( utilBridge != null )
		{
			utilBridge.log( IEagleUtilBridge.eLogTypeInfo, message );
		}
	}

	/**
	 * ログメッセージを出力する。<BR>
	 * 出力される場所は環境依存である。
	 * @author eagle.sakura
	 * @param message
	 * @version 2010/03/13 : 新規作成
	 */
	public	static	final		void	log( String message )
	{
		if( utilBridge != null )
		{
			utilBridge.log( IEagleUtilBridge.eLogTypeInfo, message );
		}
	}

	/**
	 * ログメッセージを出力する。
	 * @author eagle.sakura
	 * @param message
	 * @version 2010/03/13 : 新規作成
	 */
	public	static	final		void	logDebug( String message )
	{
		if( utilBridge != null )
		{
			utilBridge.log( IEagleUtilBridge.eLogTypeDebug, message );
		}
	}

	/**
	 * 例外情報を出力。
	 * @author eagle.sakura
	 * @param e
	 * @version 2010/03/26 : 新規作成
	 */
	public	static	final		void	log( Exception e )
	{
		if( utilBridge != null )
		{
			utilBridge.log( IEagleUtilBridge.eLogTypeDebug, "" + e 				);
			utilBridge.log( IEagleUtilBridge.eLogTypeDebug, "" + e.getMessage() );
			StackTraceElement	steArray[]	= e.getStackTrace();

			for( StackTraceElement ste : steArray )
			{
				utilBridge.log( IEagleUtilBridge.eLogTypeDebug,
								ste.getClassName() + " ( " + ste.getLineNumber() + " )" );
			}
		}
	}

	/**
	 * フラグ情報を設定する。
	 * @author eagle.sakura
	 * @param flg
	 * @param check
	 * @param is
	 * @return
	 * @version 2009/11/15 : 新規作成
	 */
	public	static	final		int		setFlag( int flg, int check, boolean is )
	{
		if( is )	return	flg | check;
		else		return	flg	& (~check);
	}
}
