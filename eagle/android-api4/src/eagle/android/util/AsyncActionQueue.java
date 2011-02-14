/**
 *
 */
package eagle.android.util;

import java.util.ArrayList;
import java.util.List;

import eagle.util.EagleUtil;

import android.os.AsyncTask;

/**
 * 非同期待ち行列の処理を行う。
 */
public class AsyncActionQueue {

    private List<Action> queue = new ArrayList<Action>();
    private ActionTask current = null;

    /**
     * 開始済みだったらtrue
     */
    private boolean isStarted = false;

    public AsyncActionQueue() {

    }

    /**
     * 末尾にアクションを追加する。
     *
     * @param action
     */
    public void pushBack(Action action) {
        synchronized (queue) {
            queue.add(action);
        }
    }

    /**
     * 先頭にアクションを追加する。
     *
     * @param action
     */
    public void pushFront(Action action) {
        synchronized (queue) {
            queue.add(0, action);
        }
    }

    public void push(Action action, int location) {
        synchronized (queue) {
            queue.add(location, action);
        }
    }

    /**
     * キャンセルする。
     *
     * @param action
     */
    public void cancel(Action action) {
        synchronized (queue) {
            queue.remove(action);
            if (current != null && current.action == action) {
                synchronized (current) {
                    try {
                        current.cancel(true);
                        current = null;
                    } catch (Exception e) {
                        EagleUtil.log(e);
                    }
                }
            }
        }
    }

    /**
     * すべての動作をキャンセルする。
     */
    public void cancelAll() {
        synchronized (queue) {
            queue.clear();
            if (current != null) {
                try {
                    current.cancel(true);
                    synchronized (current.action) {
                        current = null;
                    }
                } catch (Exception e) {
                    EagleUtil.log(e);
                }
            }

        }
        isStarted = false;
    }

    /**
     * equalsで比較し、一致したものをキャンセルする。
     *
     * @param action
     */
    public void cancelEquals(Action action) {
        synchronized (queue) {
            for (Action act : queue) {
                if (act.equals(action)) {
                    queue.remove(act);
                }
            }
            if (current != null && current.action.equals(action)) {
                try {

                    synchronized (current) {
                        current.cancel(true);
                        current = null;
                    }
                } catch (Exception e) {
                    EagleUtil.log(e);
                }
            }
        }
    }

    private void _startAction() {
        if (queue.size() == 0) {
            return;
        }

        synchronized (queue) {
            Action action = queue.get(0);
            queue.remove(0);
            current = (ActionTask) (new ActionTask(action)).execute();
        }
    }

    /**
     * アクションリストが開始されているか。
     *
     * @return
     */
    public boolean isStartActions() {
        return isStarted;
    }

    /**
     * 行動リスト処理を開始する。
     */
    public void startActions() {
        if (isStarted || queue.size() == 0) {
            return;
        }

        _startAction();
        isStarted = true;
    }

    /**
     * アクションが正常終了した。
     */
    private void onActionExit(Action action) {
        if (queue.size() > 0) {
            // ! 次の行動を開始する。
            _startAction();
        } else {
            current = null;
            isStarted = false;
        }
    }

    /**
     * タスクを一時停止する。
     */
    public void onPause() {
        try {
            if (isStarted) {
                synchronized (current) {
                    current.cancel(true);
                    // ! 先頭に追加しておく
                    pushFront(current.action);
                    current = null;
                }
            }
        } catch (Exception e) {
            EagleUtil.log(e);
        }
    }

    /**
     * タスクを再開する。
     */
    public void onResume() {
        try {
            if (isStarted) {
                _startAction();
            }
        } catch (Exception e) {
            EagleUtil.log(e);
        }
    }

    /**
     * 各々のアクションを行う。
     */
    private class ActionTask extends AsyncTask<Object, Object, Object> {
        private Action action = null;

        /**
         *
         * @param action
         */
        public ActionTask(Action action) {
            this.action = action;
        }

        @Override
        protected void onPreExecute() {
            try {
                super.onPreExecute();
            } catch (Exception e) {
                EagleUtil.log(e);
            }
        }

        @Override
        protected Object doInBackground(Object... params) {
            try {
                synchronized (action) {
                    return action.onBackgroundAction();
                }
            } catch (Exception e) {
                EagleUtil.log(e);
                return null;
            }
        }

        /**
         * 途中で停止させられた。
         */
        @Override
        protected void onCancelled() {
            try {
                super.onCancelled();
                synchronized (action) {
                    action.onCancel();
                }
            } catch (Exception e) {
                EagleUtil.log(e);
            }
        }

        @Override
        protected void onPostExecute(Object result) {
            try {
                super.onPostExecute(result);

                // ! 正常に終了したことを通知する。
                action.onPost(result);
                onActionExit(action);
            } catch (Exception e) {
                EagleUtil.log(e);
            }
        }
    }

    /**
     * １回ごとの動作を示す。
     */
    public interface Action {
        /**
         * 裏での動作を行う。
         */
        public Object onBackgroundAction();

        /**
         * 動作がキャンセルされた。
         */
        public void onCancel();

        /**
         * 動作が正常に完了した。
         */
        public void onPost(Object obj);
    };
}
