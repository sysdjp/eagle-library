package eagle.net.google.docs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;

import eagle.util.EagleUtil;

/**
 * Google Docsからファイルをダウンロードする。
 * キャンセル・レジューム・レンジ指定に対応。
 * @author SAKURA
 *
 */
public class GoogleDocsDownloader {

    /**
     * アクセストークン
     */
    String token = null;

    /**
     * レジューム用のデータ
     */
    ResumeData data = null;

    /**
     * サーバーとの接続
     */
    HttpResponse response = null;

    /**
     * 一時的なバッファ
     */
    byte[] buffer = new byte[1024 * 5];

    /**
     * ダウンロード用ストリーム。
     */
    InputStream stream = null;

    long fileSize = 0;

    /**
     *
     * @param os 出力対象のストリーム。
     */
    public GoogleDocsDownloader(String token) {
        this.token = token;
    }

    private HttpResponse getResponse(String url) throws IOException {
        HttpTransport downTrans = GoogleTransport.create();
        GoogleHeaders downHeader = (GoogleHeaders) downTrans.defaultHeaders;
        downHeader.setApplicationName("GoogleDocsDownloader");
        downHeader.gdataVersion = "3";
        downHeader.setGoogleLogin(token);

        if (data.head >= 0) {
            downHeader.range = "bytes=" + data.head + "-";
            if (data.length > 0) {
                downHeader.range += (data.head + data.length);
            }
        }
        HttpRequest downRequest = downTrans.buildGetRequest();
        downRequest.setUrl(url);
        try {
            return downRequest.execute();
        } catch (HttpResponseException re) {
            if (re.response != null && re.response.headers != null && re.response.headers.get("Location") != null) {
                EagleUtil.log("Redirect : " + re.response.headers.get("Location").toString());
                return getResponse(re.response.headers.get("Location").toString());
            } else {
                EagleUtil.log(re);
            }
        }
        throw new FileNotFoundException(url);
    }

    /**
     * ダウンロードを開始する。
     * @param resume レジュームパラメータ
     */
    public void start(ResumeData resume) throws IOException {
        data = resume;
        response = getResponse(resume.url);
        fileSize = Long.parseLong(response.headers.contentLength);
        stream = response.getContent();
    }

    /**
     * ダウンロードを開始する。
     * @param url
     * @throws IOException
     */
    public void start(String url) throws IOException {
        ResumeData r = new ResumeData();
        r.url = url;
        start(r);
    }

    /**
     *
     * @param length ダウンロードバイト数
     * @param os 書込み先ストリーム
     * @return ダウンロードが終了したらnull
     * @throws IOException
     */
    public ResumeData downloadBytes(int length, OutputStream os) throws IOException {

        int size = 0;
        if (data.head < 0) {
            data.head = 0;
        }

        while ((size = stream.read(buffer, 0, length <= buffer.length ? length : buffer.length)) > 0) {

            //! 書きこむ
            os.write(buffer, 0, size);

            //! 残り読み込みサイズを縮める。
            length -= size;
            fileSize -= size;

            //! レジュームデータを更新する
            data.head += size;
        }

        EagleUtil.log("Loading : " + ((float) fileSize / 1024 / 1024) + " MB");

        if (fileSize <= 0) {
            return null;
        } else {
            return data;
        }
    }

    /**
     * レジューム用設定データを取得する。
     * @return
     */
    public ResumeData getResumeData() {
        return data;
    }

    /**
     * ダウンロードを終了する。
     * @throws IOException
     */
    public void close() throws IOException {
        if (stream != null) {
            stream.close();
            stream = null;
        }

        data = null;
        response = null;
    }

    /**
    * レジューム用のデータ。
    * @author SAKURA
    *
    */
    public static class ResumeData {

        public ResumeData() {
        }

        public ResumeData(ResumeData origin) {
            head = origin.head;
            length = origin.length;
            url = origin.url;
        }

        public String url = "";

        /**
         * 開始位置。
         */
        public long head = -1;

        /**
         * ダウンロードサイズ。
         */
        public long length = -1;
    }
}
