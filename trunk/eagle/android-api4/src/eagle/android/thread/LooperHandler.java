/**
 *
 * @author eagle.sakura
 * @version 2010/07/21 : 新規作成
 */
package eagle.android.thread;

import java.util.ArrayList;
import java.util.List;

import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import eagle.android.view.ILooperSurface;
import eagle.android.view.LooperSurfaceView;
import eagle.util.EagleUtil;

/**
 * @author eagle.sakura
 * @version 2010/07/21 : 新規作成
 */
public class LooperHandler implements ILoopManager, Callback {
    private Handler handler = null;
    private ILooper looper = null;

    /**
     * 終了待ちフラグ。
     */
    private boolean done = false;

    @SuppressWarnings("all")
    private boolean finish = false;

    /**
     * 管理するビュー。
     */
    private List<ILooperSurface> viewList = new ArrayList<ILooperSurface>();

    /**
     * フレームレート
     */
    private int frameRate = 15;

    private Runnable runner = null;

    private boolean surfaceDstroyed = false;

    @Override
    public void setFrameRate(int rate) {
        // TODO 自動生成されたメソッド・スタブ
        frameRate = rate;
    }

    /**
     *
     * @author eagle.sakura
     * @param handler
     * @param looper
     * @version 2010/07/21 : 新規作成
     */
    public LooperHandler(Handler handler, ILooper looper) {
        this.handler = handler;
        this.looper = looper;
    }

    /**
     * サーフェイスを追加する。
     *
     * @author eagle.sakura
     * @param surface
     * @version 2010/05/25 : 新規作成
     */
    @Override
    public void addSurface(LooperSurfaceView surface) {
        if (surface == null) {
            return;
        }

        if (viewList.size() == 0) {
            surface.setTouchDisplay(looper.getTouchDisplay());
        }

        surface.getHolder().addCallback(this);
        viewList.add(surface); // !< サーフェイスを登録する。
    }

    /**
     * 全サーフェイスの作成が完了したらtrueを返す。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/05/30 : 新規作成
     */
    @Override
    public boolean isSurfaceCreateComplete() {
        for (ILooperSurface view : viewList) {
            // ! 未作成のクラスがあった。
            if (!view.isCreated()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isSurfaceDestroyed() {
        // TODO 自動生成されたメソッド・スタブ
        return surfaceDstroyed;
    }

    /**
     *
     * @author eagle.sakura
     * @param looper
     * @version 2010/05/31 : 新規作成
     */
    public void setLooper(ILooper looper) {
        this.looper = looper;
    }

    /**
     *
     * @author eagle.sakura
     * @return
     * @version 2010/05/31 : 新規作成
     */
    public ILooper getLooper() {
        return looper;
    }

    /**
     * サーフェイスの作成が完了するまで待つ。
     *
     * @author eagle.sakura
     * @version 2010/05/25 : 新規作成
     */
    protected void waitSurfaceCreated() {
        long current = System.currentTimeMillis();
        for (ILooperSurface surface : viewList) {
            while (!surface.isCreated()) {
                long time = System.currentTimeMillis() - current;
                if (done || time > (1000 * 5)) {
                    done = true;
                    EagleUtil.log("thread done");
                    return;
                }
            }
        }
    }

    private class InitializeRunnable implements Runnable {
        InitializeRunnable() {
            LooperHandler.this.runner = this;
        }

        @Override
        public void run() {
            // TODO 自動生成されたメソッド・スタブ
            if (LooperHandler.this.isSurfaceCreateComplete()) {
                // ! すべてのビューが初期化完了した
                LooperHandler.this.looper.onInitialize();
                LooperHandler.this.handler.postDelayed(new LoopRunnable(), 10);
            } else {
                // ! まだ初期化完了していない
                LooperHandler.this.handler.postDelayed(this, 10);
            }
        }
    }

    private class LoopRunnable implements Runnable {
        LoopRunnable() {
            LooperHandler.this.runner = this;
        }

        @Override
        public void run() {
            if (LooperHandler.this.looper != null) {
                if (LooperHandler.this.done) {
                    LooperHandler.this.looper.onFinalize();
                    LooperHandler.this.looper = null;
                    LooperHandler.this.finish = true;
                } else {
                    long sTime = System.currentTimeMillis();
                    // ! フレーム処理
                    {
                        LooperHandler.this.looper.onLoop();
                    }
                    long eTime = System.currentTimeMillis();

                    // ! 1フレームにかけていい時間
                    final int frameMilliSec = (1000 / frameRate);
                    int sleepTime = frameMilliSec - (int) (eTime - sTime);
                    sleepTime = Math.max(1, sleepTime);

                    // ! スレッドを休眠させる
                    LooperHandler.this.handler.postDelayed(this, sleepTime);

                    /*
                     * if( sleepTime == 1 ) { EagleUtil.log( "" + ( 1000.0f / (
                     * float )( eTime - sTime ) ) + "fps( Sleep:" + sleepTime +
                     * " )" ); }
                     */
                }
            }
        }
    }

    /**
     * ループ処理を開始する。
     *
     * @author eagle.sakura
     * @version 2010/07/21 : 新規作成
     */
    @Override
    public void startLoop() {
        handler.postDelayed(new InitializeRunnable(),
        // ! 適当な秒数待つ。
                10);
    }

    /**
     *
     * @author eagle.sakura
     * @version 2010/07/21 : 新規作成
     */
    @Override
    public void dispose() {
        if (runner != null) {
            handler.removeCallbacks(runner);
        }
        LooperHandler.this.looper.onFinalize();
        LooperHandler.this.looper = null;
        LooperHandler.this.finish = true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // TODO 自動生成されたメソッド・スタブ
        if (isSurfaceCreateComplete() && runner == null) {
            EagleUtil.log("Surface create complete");
            startLoop();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO 自動生成されたメソッド・スタブ

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO 自動生成されたメソッド・スタブ
        surfaceDstroyed = true;
    }

    @Override
    public boolean loopPause() {
        // TODO 自動生成されたメソッド・スタブ
        handler.removeCallbacks(runner);
        return true;
    }

    @Override
    public boolean loopResume() {
        // TODO 自動生成されたメソッド・スタブ
        handler.post(runner);
        return true;
    }
}
