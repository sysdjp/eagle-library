package eagle.io;

import java.io.IOException;
import java.io.OutputStream;

import eagle.util.Disposable;
import eagle.util.EagleUtil;

/**
 * ライブラリ規定の形式でデータを出力するインターフェース。<BR>
 * このクラスを通して出力したファイルは対になる {@link DataInputStream}で開くことが可能。
 *
 * @author eagle.sakura
 * @version 2010/02/23 : 新規作成
 */
public class DataOutputStream implements Disposable {
    /**
     * 入出力。
     */
    private IBufferWriter writer = null;

    /**
     * バイトオーダー変換。
     */
    private ByteOrder order = ByteOrder.eThrough;

    /**
     * 書き込み用のストリームを作成する。
     *
     * @author eagle.sakura
     * @param bw
     * @version 2010/03/25 : 新規作成
     */
    public DataOutputStream(IBufferWriter bw) {
        writer = bw;
    }

    public DataOutputStream(OutputStream os) {
        writer = new OutputStreamBufferWriter(os);
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
     * リソースの開放を行う。
     *
     * @author eagle.sakura
     * @version 2010/02/23 : 新規作成
     */
    @Override
    public void dispose() {
        writer.dispose();
        writer = null;
    }

    /**
     * 実際のバッファへ書き込みを行う。
     *
     * @author eagle.sakura
     * @param buf
     * @param position
     * @param length
     * @version 2010/02/23 : 新規作成
     */
    public void writeBuffer(byte[] buf, int position, int length) throws IOException {
        writer.writeBuffer(buf, position, length);
    }

    /**
     * 1バイト整数を保存する。
     *
     * @author eagle.sakura
     * @param n
     * @version 2010/02/22 : 新規作成
     */
    public void writeS8(byte n) throws IOException {
        byte[] buf = { n, };
        order.encode(buf, 1, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     *
     * @author eagle.sakura
     * @param b
     * @throws IOException
     * @version 2010/05/28 : 新規作成
     */
    public void writeBoolean(boolean b) throws IOException {
        writeS8(b ? (byte) 1 : (byte) 0);
    }

    /**
     * 2バイト整数を保存する。
     *
     * @author eagle.sakura
     * @param n
     * @version 2010/02/22 : 新規作成
     */
    public void writeS16(short n) throws IOException {
        byte[] buf = { (byte) ((((int) n) >> 8) & 0xff), (byte) ((((int) n) >> 0) & 0xff), };
        order.encode(buf, 2, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 4バイト整数を保存する。
     *
     * @author eagle.sakura
     * @param n
     * @version 2010/02/22 : 新規作成
     */
    public void writeS32(int n) throws IOException {
        byte[] buf = { (byte) ((((int) n) >> 24) & 0xff), (byte) ((((int) n) >> 16) & 0xff), (byte) ((((int) n) >> 8) & 0xff),
                (byte) ((((int) n) >> 0) & 0xff), };
        order.encode(buf, 4, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 4バイト整数の配列を保存する。
     *
     * @author eagle.sakura
     * @param buffer
     * @param position
     * @param length
     * @version 2010/06/06 : 新規作成
     */
    public void writeS32Array(int[] buffer) throws IOException {
        byte[] temp = new byte[buffer.length * 4];
        int ptr = 0;
        for (int n : buffer) {
            temp[ptr] = (byte) ((n >> 24) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 16) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 8) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 0) & 0xff);
            ptr++;
        }
        order.encode(temp, 4, buffer.length);
        writeBuffer(temp, 0, temp.length);
    }

    /**
     * 浮動小数点配列を保存する。
     *
     * @author eagle.sakura
     * @param buffer
     * @version 2010/06/06 : 新規作成
     */
    public void writeFloatArray(float[] buffer) throws IOException {
        byte[] temp = new byte[buffer.length * 4];
        int ptr = 0;
        for (float f : buffer) {
            int n = Float.floatToIntBits(f);
            temp[ptr] = (byte) ((n >> 24) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 16) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 8) & 0xff);
            ptr++;
            temp[ptr] = (byte) ((n >> 0) & 0xff);
            ptr++;
        }
        order.encode(temp, 4, buffer.length);
        writeBuffer(temp, 0, temp.length);
    }

    /**
     * 浮動小数値をマスコットカプセル形式の固定小数として保存する。
     *
     * @author eagle.sakura
     * @param f
     * @throws IOException
     * @version 2010/04/02 : 新規作成
     */
    public void writeMCFloat(float f) throws IOException {
        int n = (int) (f * (float) EagleUtil.eMCFixed1_0);

        byte[] buf = { (byte) ((((int) n) >> 24) & 0xff), (byte) ((((int) n) >> 16) & 0xff), (byte) ((((int) n) >> 8) & 0xff),
                (byte) ((((int) n) >> 0) & 0xff), };
        order.encode(buf, 4, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 浮動小数値をマスコットカプセル形式の固定小数として保存する。<BR>
     * 16bitの値として保存するため、保存可能な値は限られる。
     *
     * @author eagle.sakura
     * @param f
     * @throws IOException
     * @version 2010/04/02 : 新規作成
     */
    public void writeMCFloat16(float f) throws IOException {
        int n = (int) (f * (float) EagleUtil.eMCFixed1_0);

        byte[] buf = { (byte) ((((int) n) >> 8) & 0xff), (byte) ((((int) n) >> 0) & 0xff), };
        order.encode(buf, 2, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 浮動小数値をGL形式の固定小数として保存する。
     *
     * @author eagle.sakura
     * @param n
     * @version 2010/02/22 : 新規作成
     */
    public void writeGLFloat(float f) throws IOException {
        int n = (int) (f * (float) EagleUtil.eGLFixed1_0);

        byte[] buf = { (byte) ((((int) n) >> 24) & 0xff), (byte) ((((int) n) >> 16) & 0xff), (byte) ((((int) n) >> 8) & 0xff),
                (byte) ((((int) n) >> 0) & 0xff), };
        order.encode(buf, 4, 1);
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 浮動小数値を書き込む。
     *
     * @author eagle.sakura
     * @param f
     * @version 2010/05/28 : 新規作成
     */
    public void writeFloat(float f) throws IOException {
        writeS32(Float.floatToIntBits(f));
    }

    /**
     * 文字列を書き込む。<BR>
     * エンコードはShiftJISとして保存する。
     *
     * @author eagle.sakura
     * @param str
     * @version 2010/02/22 : 新規作成
     */
    public void writeString(String str) throws IOException {
        byte[] buf = str.getBytes(EagleUtil.eEncodeSJIS);
        // ! 文字列の長さを保存。
        writeS16((short) buf.length);
        // ! 文字列本体を保存。
        writeBuffer(buf, 0, buf.length);
    }

    /**
     * 書き込みを行った場合の保存バイト数を計算する。
     *
     * @author eagle.sakura
     * @param str
     * @return
     * @version 2010/04/02 : 新規作成
     */
    public static int getWriteSize(String str) {
        byte[] buf = str.getBytes();
        return buf.length + 2;
    }

    /**
     * 配列の大きさと本体を保存する。<BR>
     * bufferがnullである場合、0バイトのファイルとして保存する。
     *
     * @author eagle.sakura
     * @param buffer
     * @version 2010/02/22 : 新規作成
     */
    public void writeFile(byte[] buffer) throws IOException {
        if (buffer == null) {
            writeS32(0);
            return;
        }

        // ! 配列の長さ
        writeS32(buffer.length);
        // ! 配列本体
        writeBuffer(buffer, 0, buffer.length);
    }
}
