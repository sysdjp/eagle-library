/**
 * データの入力を補助する。
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
package eagle.io;

import java.io.IOException;
import java.io.InputStream;

import eagle.util.Disposable;
import eagle.util.EagleUtil;

/**
 * データ入力を補助するクラス。
 *
 * @author eagle.sakura
 * @version 2009/11/15 : 新規作成
 */
public class DataInputStream implements Disposable {
    /**
     * 読み取りに使用するリーダー。
     */
    private IBufferReader reader = null;

    /**
     * バイトオーダー変換。
     */
    private ByteOrder order = ByteOrder.eThrough;

    /**
     * ストリームを生成する。
     *
     * @author eagle.sakura
     * @param br
     * @version 2009/11/15 : 新規作成
     */
    public DataInputStream(IBufferReader br) {
        reader = br;
    }

    /**
     *
     * @author eagle.sakura
     * @param is
     * @version 2010/06/23 : 新規作成
     */
    public DataInputStream(InputStream is) {
        this(new InputStreamBufferReader(is));
    }

    /**
     * リードクラスを取得する。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/06/21 : 新規作成
     */
    public IBufferReader getReader() {
        return reader;
    }

    /**
     * 入力時のバイトオーダー変換方法を指定する。<BR>
     * ファイル側がリトルエンディアンの場合 {@link ByteOrder#eReversing}、ビッグエンディアンの場合
     * {@link ByteOrder#eThrough}を指定する。<BR>
     * 標準では{@link ByteOrder#eThrough}になっている。
     *
     * @author eagle.sakura
     * @param set
     * @version 2010/06/21 : 新規作成
     */
    public void setByteOrder(ByteOrder set) {
        order = set;
    }

    /**
     * バッファから１バイト読み取る。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public byte readS8() throws IOException {
        byte[] n = { 0 };
        reader.readBuffer(n, 0, n.length);
        order.encode(n, 1, 1);
        return n[0];
    }

    /**
     * バッファから2バイト読み取る。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public short readS16() throws IOException {
        byte[] n = { 0, 0 };
        reader.readBuffer(n, 0, n.length);
        order.encode(n, 2, 1);

        int n0 = ((int) n[0] & 0xff);
        int n1 = ((int) n[1] & 0xff);

        return (short) ((n0 << 8) | (n1 << 0));
    }

    /**
     * バッファから3バイト読み取る。<BR>
     * 色情報等に利用可能。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public int readS24() throws IOException {
        byte[] n = { 0, 0, 0 };
        reader.readBuffer(n, 0, n.length);
        order.encode(n, 3, 1);

        return (int) (((((int) n[0]) & 0xff) << 16) | ((((int) n[1]) & 0xff) << 8) | ((((int) n[2]) & 0xff) << 0));
    }

    /**
     * １バイト整数を取得し、読み込み位置を１バイト進める。
     *
     * @author eagle.sakura
     * @return １バイト符号無整数。ただし、符号無を表現する関係上、戻りはint型となる。
     * @version 2009/08/28 : 新規作成
     */
    public int readU8() throws IOException {
        return (((int) readS8()) & 0xff);
    }

    /**
     * ２バイト整数を取得し、読み込み位置を２バイト進める。
     *
     * @author eagle.sakura
     * @return ２バイト符号無整数。ただし、符号無を表現する関係上、戻りはint型となる。
     * @version 2009/09/20 : 新規作成
     */
    public int readU16() throws IOException {
        return (((int) readS16()) & 0xffff);
    }

    /**
     * バッファから4バイト読み取る。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public int readS32() throws IOException {
        byte[] n = { 0, 0, 0, 0 };
        reader.readBuffer(n, 0, n.length);
        order.encode(n, 4, 1);

        int n0 = ((int) n[0] & 0xff);
        int n1 = ((int) n[1] & 0xff);
        int n2 = ((int) n[2] & 0xff);
        int n3 = ((int) n[3] & 0xff);

        return (n0 << 24) | (n1 << 16) | (n2 << 8) | (n3 << 0);
    }

    /**
     * バッファから８バイト整数を読み取る。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/03/24 : 新規作成
     */
    public long readS64() throws IOException {
        byte[] n = { 0, 0, 0, 0, 0, 0, 0, 0 };
        reader.readBuffer(n, 0, n.length);
        order.encode(n, 8, 1);

        int n0 = ((int) n[0] & 0xff);
        int n1 = ((int) n[1] & 0xff);
        int n2 = ((int) n[2] & 0xff);
        int n3 = ((int) n[3] & 0xff);

        int n4 = ((int) n[4] & 0xff);
        int n5 = ((int) n[5] & 0xff);
        int n6 = ((int) n[6] & 0xff);
        int n7 = ((int) n[7] & 0xff);

        return (((n0 << 24) | (n1 << 16) | (n2 << 8) | (n3 << 0)) << 32) | ((n4 << 24) | (n5 << 16) | (n6 << 8) | (n7 << 0));
    }

    /**
     * 固定小数をfloat変換して取得する。<BR>
     * GL仕様のため、符号1 整数15 小数16の固定小数を使用する。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/23 : 新規作成
     */
    public float readGLFixedFloat() throws IOException {
        return ((float) readS32()) / (float) EagleUtil.eGLFixed1_0;
    }

    /**
     * 固定小数をdouble変換して取得する。<BR>
     * GL仕様のため、符号1 整数47 小数16の固定小数を使用する。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/03/24 : 新規作成
     */
    public double readGLFixedDouble() throws IOException {
        return ((double) readS64()) / (double) EagleUtil.eGLFixed1_0;
    }

    /**
     * IEEE754形式のビット列をfloatに変換し、取得する。
     *
     * @author eagle.sakura
     * @return
     * @throws IOException
     * @version 2010/04/19 : 新規作成
     */
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readS32());
    }

    /**
     * IEEE754形式のビット列をdoubleに変換し、取得する。
     *
     * @author eagle.sakura
     * @return
     * @throws IOException
     * @version 2010/04/19 : 新規作成
     */
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readS64());
    }

    /**
     * 真偽の値を取得する。<BR>
     * 1byte読み取り、0ならfalse、それ以外ならtrueを返す。
     *
     * @author eagle.sakura
     * @return
     * @throws IOException
     * @version 2010/05/28 : 新規作成
     */
    public boolean readBoolean() throws IOException {
        return readS8() == 0 ? false : true;
    }

    /**
     * 文字列を読み取る。<BR>
     * エンコードはShiftJISである必要がある。<BR>
     * 頭2byteが文字数、後に文字配列が続く。
     *
     * @author eagle.sakura
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public String readString() throws IOException {
        int len = readS16();
        if (len <= 0) {
            return "";
        }
        byte[] buf = new byte[len];
        readBuffer(buf, len);

        return new String(buf, EagleUtil.eEncodeSJIS);
    }

    /**
     * バッファを直接読み取る。
     *
     * @author eagle.sakura
     * @param length
     * @return
     * @version 2009/11/15 : 新規作成
     */
    public byte[] readBuffer(int length) throws IOException {
        byte[] ret = new byte[length];
        readBuffer(ret, length);
        return ret;
    }

    /**
     * ファイルを作成する。
     *
     * @author eagle.sakura
     * @return
     * @version 2010/02/22 : 新規作成
     */
    public byte[] readFile() throws IOException {
        int len = readS32();
        byte[] ret = readBuffer(len);

        return ret;
    }

    /**
     * バッファから必要な長さを読み取る。
     *
     * @author eagle.sakura
     * @param buf
     * @param length
     * @version 2009/11/15 : 新規作成
     */
    public void readBuffer(byte[] buf, int length) throws IOException {
        readBuffer(buf, 0, length);
    }

    /**
     *
     * @author eagle.sakura
     * @param buf
     * @param index
     * @param length
     * @version 2009/11/15 : 新規作成
     */
    public int readBuffer(byte[] buf, int index, int length) throws IOException {
        return reader.readBuffer(buf, index, length);
    }

    /**
     * 資源の解放を行う。<BR>
     * 内部管理する{@link #reader}のdispose()を行う。
     *
     * @author eagle.sakura
     * @version 2009/11/15 : 新規作成
     */
    public void dispose() {
        if (reader != null) {
            reader.dispose();
            reader = null;
        }
    }

    /**
     *
     * @author eagle.sakura
     * @throws Throwable
     * @version 2010/07/12 : 新規作成
     */
    protected void finalize() throws Throwable {
        super.finalize();
        dispose();
    }

    /**
     * 読み取り位置を指定する。
     *
     * @author eagle.sakura
     * @param eSeekType
     * @param pos
     * @version 2009/11/15 : 新規作成
     */
    public void seek(int eSeekType, int pos) throws IOException {
        reader.seek(eSeekType, pos);
    }

}
