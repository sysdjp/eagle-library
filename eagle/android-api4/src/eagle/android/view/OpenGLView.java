/**
 *
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
package eagle.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import eagle.android.gles11.GLManager;
import eagle.android.util.UtilActivity;
import eagle.math.Vector2;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/05/30 : 新規作成
 */
public class OpenGLView extends LooperSurfaceView {
    /**
     * OGL管理。
     */
    private GLManager glManager = new GLManager();

    private Context context = null;

    /**
     *
     * @author eagle.sakura
     * @param context
     * @param attr
     * @version 2010/05/24 : 新規作成
     */
    public OpenGLView(Context context, AttributeSet attr) {
        super(context, attr);
        // getHolder().setFormat( PixelFormat.RGBA_8888 );
        // getHolder().setFormat( PixelFormat.RGB_888 );
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
        glManager.setSurfaceHolder(getHolder());
        this.context = context;
    }

    /**
     *
     * @author eagle.sakura
     * @param context
     * @version 2010/05/30 : 新規作成
     */
    public OpenGLView(Context context) {
        super(context);
        // getHolder().setFormat( PixelFormat.RGB_888 );
        // getHolder().setFormat( PixelFormat.RGBA_8888 );
        // getHolder().setFormat( PixelFormat.TRANSPARENT );

        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
        glManager.setSurfaceHolder(getHolder());
        this.context = context;
    }

    /**
     * サーフェイスが作成された。
     *
     * @author eagle.sakura
     * @param arg0
     * @version 2010/05/30 : 新規作成
     */
    @Override
    public void surfaceCreated(SurfaceHolder arg0) {
        if (glManager == null) {
            glManager = GLManager.getInstance();
        }

        EagleUtil.log("OpenGLView#surfaceCreated");
        EagleUtil.log("arg0 : " + arg0);
        EagleUtil.log("cur holder : " + glManager.getSurfaceHolder());
        // glManager.setSurfaceHolder( arg0 );
        // glManager.initGL();
        // glManager.clearTest( );

        Vector2 v = UtilActivity.getDisplaySize(context, new Vector2());
        EagleUtil.log("DisplaySize : " + v);
        glManager.setSurfaceSize((int) v.x, (int) v.y);

        super.surfaceCreated(arg0);
    }

    /**
     * GL管理クラスを取得する。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/05/30 : 新規作成
     */
    public GLManager getGLManager() {
        return glManager;
    }

    /**
     *
     * @author eagle.sakura
     * @param holder
     * @param format
     * @param width
     * @param height
     * @version 2010/05/30 : 新規作成
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (glManager == null) {
            glManager = GLManager.getInstance();
        }

        super.surfaceChanged(holder, format, width, height);

        Vector2 v = UtilActivity.getDisplaySize(context, new Vector2());
        EagleUtil.log("DisplaySize : " + v);
        glManager.setSurfaceSize((int) v.x, (int) v.y);
    }

    /**
     * サーフェイスが破棄された。
     *
     * @author eagle.sakura
     * @param holder
     * @version 2010/05/30 : 新規作成
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.surfaceDestroyed(holder);

        /*
         * if( glManager != null ) { glManager.dispose(); }
         */
        glManager = null;
    }
}
