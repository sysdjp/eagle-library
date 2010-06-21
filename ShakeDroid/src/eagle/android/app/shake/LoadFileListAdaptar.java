/**
 *
 * @author eagle.sakura
 * @version 2010/06/06 : 新規作成
 */
package eagle.android.app.shake;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import eagle.android.app.appinfo.AppInfomation;
import eagle.android.appcore.shake.ShakeDataFile;
import eagle.android.util.UtilActivity;
import eagle.io.Directory;
import eagle.util.EagleUtil;

import android.app.Activity;
import android.content.Intent;
import android.database.DataSetObserver;
import android.text.InputType;
import android.text.method.KeyListener;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/**
 * @author eagle.sakura
 * @version 2010/06/06 : 新規作成
 */
public class LoadFileListAdaptar implements ListAdapter
{
	private	LoadFileSelectActivity		activity		= null;
	private	Directory					directory		= null;
	private	ShakeDataFile[]				datas			= null;

	/**
	 * 画面に向きにしたがって選択する。
	 */
	public	static	final	int			eSelectTypeAuto		=	0;

	/**
	 * 縦向きの画面を選択する。
	 */
	public	static	final	int			eSelectTypeVirtical	=	1;

	/**
	 * 横向きの画面を選択する。
	 */
	public	static	final	int			eSelectTypeHorizon	=	2;

	/**
	 * 両方を選択する。
	 */
	public	static	final	int			eSelectTypeAll		=	3;

	private	int							selectType		=	eSelectTypeAuto;

	public	class	LoadButtonLitener	implements	OnClickListener
	{
		private	byte[]					dataArray	=	null;
		private	LoadFileSelectActivity	activity	=	null;
		private String					fileName	=	"";
		/**
		 * ボタンが押されたら、関連付けられたファイルを設定してアクティビティを殺す。
		 * @author eagle.sakura
		 * @param activity
		 * @param fileArray
		 * @version 2010/06/06 : 新規作成
		 */
		public	LoadButtonLitener( LoadFileSelectActivity activity, String fileName, byte[] fileArray )
		{
			this.activity	= activity;
			dataArray		= fileArray;
			this.fileName	= fileName;
		}

		@Override
		public void onClick(View v)
		{
			// TODO 自動生成されたメソッド・スタブ
			activity.setResultBuffer( fileName, dataArray );
			activity.finish();
		}
	};

	/**
	 *
	 * @author eagle.sakura
	 * @param activity
	 * @version 2010/06/06 : 新規作成
	 */
	public	LoadFileListAdaptar( LoadFileSelectActivity activity )
	{
		this.activity = activity;

		if( ( new AppInfomation() ).isSharewareMode() )
		{
		//!	有料版なら拡張子で列挙
			directory	=	new	Directory(	UtilActivity.getSDCardRootPath( ) + ShakeDataFile.eSaveDirectory,
											ShakeDataFile.eFileExt,
											false );
		}
		else
		{
		//!	無料版なら指定したファイルのみ列挙
			directory	=	new	Directory( UtilActivity.getSDCardRootPath() + ShakeDataFile.eSaveDirectory );
			directory.registFileName( ShakeDataFile.eFreeModeFileName_v );
			directory.registFileName( ShakeDataFile.eFreeModeFileName_h );
		}
		datas			= new ShakeDataFile[ directory.getFileNameCount() ];

		if( directory.getFileNameCount() == 0 )
		{
			Toast.makeText( activity,
							activity.getResources().getString( R.string.toast_noLoadFiles ),
							Toast.LENGTH_LONG ).show();
		}

		//!	intentの中身を調べる
		/*
		*/
		try
		{
			Intent	intent = activity.getIntent();

			String	pickType = intent.getStringExtra( "pickType" );

			EagleUtil.log( "pickType : " + pickType );
			if( pickType != null )
			{
				if( pickType.equals( "v" ) )
				{
					EagleUtil.log( "enum v" );
					selectType = eSelectTypeVirtical;
				}
				else if( pickType.equals( "h" ) )
				{
					EagleUtil.log( "enum h" );
					selectType = eSelectTypeHorizon;
				}
				else
				{
					selectType = eSelectTypeAuto;
				}
			}
		}
		catch( Exception e )
		{
			EagleUtil.log( e );
		}
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public boolean areAllItemsEnabled()
	{
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	/**
	 * @author eagle.sakura
	 * @param position
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public boolean isEnabled(int position)
	{
		// TODO 自動生成されたメソッド・スタブ
		return	position < getCount();
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public int getCount()
	{
		// TODO 自動生成されたメソッド・スタブ
		return directory.getFileNameCount();
	}

	/**
	 * @author eagle.sakura
	 * @param position
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public Object getItem(int position)
	{
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}

	/**
	 * @author eagle.sakura
	 * @param position
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public long getItemId(int position)
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/**
	 * @author eagle.sakura
	 * @param position
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public int getItemViewType(int position)
	{
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	/**
	 *
	 * @author eagle.sakura
	 * @param sdf
	 * @return
	 * @version 2010/06/12 : 新規作成
	 */
	public	boolean	isLoadEnable( ShakeDataFile sdf )
	{
		switch( selectType )
		{
		case	eSelectTypeAuto:
			{
				if( sdf.getInitData().isVertical
				&&	UtilActivity.isOrientationVertical( activity ) )
				{
				//	button.setVisibility( View.VISIBLE );
					return	true;
				}
				else if( !sdf.getInitData().isVertical
					&&	 !UtilActivity.isOrientationVertical( activity ) )
				{
				//	button.setVisibility( View.VISIBLE );
					return	true;
				}
				else
				{
				//	button.setVisibility( View.INVISIBLE );
					return	false;
				}

			}
		case	eSelectTypeVirtical:
			{
				return	sdf.getInitData().isVertical == true;
			}
		case	eSelectTypeHorizon:
			{
				return	sdf.getInitData().isVertical == false;
			}
		}
		return	true;
	}

	/**
	 * @author eagle.sakura
	 * @param position
	 * @param convertView
	 * @param parent
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		// TODO 自動生成されたメソッド・スタブ

		if( convertView == null )
		{
			View			layout = View.inflate( activity, R.layout.save_dialog, null );
			convertView = layout;

			//!	上書きチェックは必ず非表示
			{
				CheckBox	check = ( CheckBox )layout.findViewById( R.id.savedialog_overwrite );
				check.setVisibility( View.INVISIBLE );
			}
		}

		if( datas[ position ] == null )
		{
			try
			{
				ShakeDataFile	file	= new	ShakeDataFile( directory.getFileFullPath( position ) );
				datas[ position ] = file;
			}
			catch( Exception e )
			{
				EagleUtil.log( e );
			}
		}

		if( datas[ position ] != null )
		{
			SaveDialog.initView( datas[ position ], convertView, directory.getFile( position ).lastModified( ) );
			/*
			EditText	et = ( EditText )convertView.findViewById( R.id.save_usermemo );
			et.setInputType( InputType.TYPE_NULL );
			*/

			//!	ボタンの有効状態を指定する。
			{
				Button	button = ( Button )convertView.findViewById( R.id.save_loadbutton );

				if( isLoadEnable( datas[ position ] ) )
				{
					button.setVisibility( View.VISIBLE );
				}
				else
				{
					button.setVisibility( View.INVISIBLE );
				}

				//!	リスナの再指定
				button.setOnClickListener( new LoadButtonLitener(	activity,
																	directory.getFileName( position ),
																	datas[ position ].getOriginBuffer( ) ) );
			}
		}


		return convertView;
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public int getViewTypeCount()
	{
		// TODO 自動生成されたメソッド・スタブ
		return 1;
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public boolean hasStableIds()
	{
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	/**
	 * @author eagle.sakura
	 * @return
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public boolean isEmpty()
	{
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	/**
	 * @author eagle.sakura
	 * @param observer
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public void registerDataSetObserver(DataSetObserver observer)
	{
	// TODO 自動生成されたメソッド・スタブ

	}

	/**
	 * @author eagle.sakura
	 * @param observer
	 * @version 2010/06/06 : 新規作成
	 */
	@Override
	public void unregisterDataSetObserver(DataSetObserver observer)
	{
	// TODO 自動生成されたメソッド・スタブ

	}

}
