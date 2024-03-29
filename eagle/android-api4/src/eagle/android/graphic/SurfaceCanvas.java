/**
 *
 * @author eagle.sakura
 * @version 2009/12/11 : 新規作成
 */
package eagle.android.graphic;

import android.view.SurfaceHolder;

/**
 * @author eagle.sakura
 * @version 2009/12/11 : 新規作成
 */
public class SurfaceCanvas extends Graphics {
    private SurfaceHolder holder = null;

    /**
     * @author eagle.sakura
     * @param target
     * @version 2009/11/29 : 新規作成
     */
    public SurfaceCanvas(SurfaceHolder holder) {
        setHolder(holder);
    }

    /**
     *
     * @author eagle.sakura
     * @param holder
     * @version 2010/07/16 : 新規作成
     */
    public void setHolder(SurfaceHolder holder) {
        this.holder = holder;
    }

    /**
     * 描画の開始を明示する。
     *
     * @author eagle.sakura
     * @version 2009/11/29 : 新規作成
     */
    public boolean lock() {
        setCanvas(holder.lockCanvas());

        return getCanvas() != null;
    }

    /**
     * サーフェイス情報をフロントバッファに転送する。
     *
     * @author eagle.sakura
     * @version 2009/11/29 : 新規作成
     */
    public void unlock() {
        holder.unlockCanvasAndPost(getCanvas());
        setCanvas(null);
    }
}
