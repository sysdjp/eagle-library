/**
 *
 * @author eagle.sakura
 * @version 2009/12/10 : 新規作成
 */
package eagle.util;

import java.util.ArrayList;
import java.util.List;

import eagle.android.graphic.Graphics;

/**
 * @author eagle.sakura
 * @version 2009/12/10 : 新規作成
 */
public class FormatText
{
	public	static	class	TextElement
	{
		private	String		text	=	"";				//!<	テキスト
		private	int			color	=	0x00000000;		//!<	描画色
		private	int			size	=	12;				//!<	テキストサイズ。
		private	int			width	=	0;				//!<	幅
		private	int			height	=	0;				//!<	高さ
		private	int			flags	=	0x0;			//!<	描画フラグ
		private	int			offsetY	=	0;				//!<	前の行からのオフセットピクセル値。

		/**
		 * 色の変更を行う。
		 */
		public	static	final	int		eFlagTextColorEnable	=	0x1 << 1;

		/**
		 * 描画文字列を指定する。
		 * @author eagle.sakura
		 * @param str
		 * @version 2009/12/11 : 新規作成
		 */
		public	void	setText( String str )
		{
			text = str;
		}

		/**
		 * フォントの大きさを指定する。
		 * @author eagle.sakura
		 * @param size
		 * @version 2009/12/11 : 新規作成
		 */
		public	void	setFontSize( int size )
		{

		}

		/**
		 * 描画色を指定する。
		 * @author eagle.sakura
		 * @param col
		 * @version 2009/12/11 : 新規作成
		 */
		public	void	setColor( int col )
		{
			color = col;
		}
	};

	/**
	 * 1行テキストを管理する。
	 * @author eagle.sakura
	 * @version 2009/12/10 : 新規作成
	 */
	public	static	class	TextLine
	{
		private	List< TextElement >	elements		=	new	ArrayList();
		private	int					textWidth		=	0;	//!<	行の文字幅
		private	int					height			=	0;	//!<	行の高さ
		private	int					drawLength		=	0;	//!<	実際に描画する文字数

		/**
		 *
		 * @author eagle.sakura
		 * @version 2009/12/10 : 新規作成
		 */
		public	TextLine( )
		{

		}

		/**
		 * 1要素を追加する。
		 * @author eagle.sakura
		 * @param e
		 * @version 2009/12/10 : 新規作成
		 */
		public	void	addElement( TextElement e )
		{
			elements.add( e );
		}
	};

	private	List< TextLine >		elements	=	new	ArrayList();

	/**
	 * 改行等のタグ情報を含んだテキストを定義する。
	 * @author eagle.sakura
	 * @version 2009/12/10 : 新規作成
	 */
	public	FormatText( )
	{

	}

	/**
	 * 描画テキストを追加する。
	 * @author eagle.sakura
	 * @param text
	 * @version 2009/12/11 : 新規作成
	 */
	public	void	addText( String		text	)
	{

	}

	/**
	 * テキスト要素を追加する。
	 * @author eagle.sakura
	 * @param e
	 * @version 2009/12/10 : 新規作成
	 */
	public	void	addLine( TextLine e )
	{
		elements.add( e );
	}

	/**
	 * 文字列の描画を行う。
	 * @author eagle.sakura
	 * @param canvas
	 * @param x
	 * @param y
	 * @version 2009/12/11 : 新規作成
	 */
	public	void	draw( Graphics canvas, int x, int y )
	{

	}
};
