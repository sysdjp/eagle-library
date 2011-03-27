/**
 *
 */
package eagle.net.google.docs;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import net.arnx.jsonic.JSON;
import eagle.net.google.docs.GoogleDocsDownloader.ResumeData;
import eagle.util.EagleUtil;

/**
 * @author SAKURA
 *
 */
public class FileDownloader {

    /**
     * キャッシュファイル名
     */
    File cache = null;

    /**
     * レジューム情報ファイル名。
     */
    File resume = null;

    String dstFileName = null;

    String srcFileName = null;

    GoogleDocsDownloader downloader = null;

    /**
     * レジューム情報ファイル拡張子。
     */
    public static final String eResumeDataExt = ".resume";

    /**
     * キャッシュ情報ファイル拡張子。
     */
    public static final String eCacheDataExt = ".cache";

    public FileDownloader(String srcFileName, String dstFileName) {
        this.srcFileName = srcFileName;
        this.dstFileName = dstFileName;
    }

    /**
     * ダウンロードが終了していたらtrueを返す。
     * @return
     */
    public boolean isDownloadFinished() {
        return (new File(dstFileName)).exists();
    }

    private void saveResumeData(ResumeData resumeData) {
        try {
            FileOutputStream os = new FileOutputStream(resume);
            JSON.encode(resumeData, os);
            os.close();
        } catch (Exception e) {

        }
    }

    /**
     * ダウンロードを開始する。
     * @param token
     * @throws IOException
     */
    public void start(String token) throws IOException {
        resume = new File(dstFileName + eResumeDataExt);
        cache = new File(dstFileName + eCacheDataExt);

        //! レジュームファイルを探す
        ResumeData resumeData = null;
        if (resume.exists()) {
            try {
                InputStream is = new FileInputStream(resume);
                resumeData = JSON.decode(is, ResumeData.class);
                is.close();

                EagleUtil.log("File Resume Head :  " + resumeData.head);
            } catch (Exception e) {

            }
        }

        if (resumeData == null) {
            resumeData = new ResumeData();
            //! URLを探す
            GoogleDocsEntries entries = new GoogleDocsEntries(token);
            entries.access(srcFileName);

            if (entries.getEntriesCount() == 0) {
                throw new FileNotFoundException("not exists google docs : " + srcFileName);
            }
            resumeData.url = entries.getEntry(0).getContentUrl();
            saveResumeData(resumeData);
        }

        //! ダウンロードを開始する
        downloader = new GoogleDocsDownloader(token);
        downloader.start(resumeData);
    }

    /**
     * 指定バイト数ダウンロードを行う。
     * 一度メモリに展開するため、大きすぎるサイズは指定しないようにすること。
     * @param length
     * @return ダウンロードが完了したらtrue
     * @throws IOException
     */
    public boolean download(int length) throws IOException {
        //! キャッシュファイルを探す
        ByteArrayOutputStream baos = new ByteArrayOutputStream(length);
        ResumeData data = downloader.downloadBytes(length, baos);

        OutputStream stream = new FileOutputStream(cache, downloader.getResumeData().head > 0);
        {
            stream.write(baos.toByteArray());
        }
        stream.close();
        baos = null;
        System.gc();

        if (data != null) {
            saveResumeData(data);
            return false;
        } else {

            //! キャッシュをリネーム
            (new File(dstFileName)).delete();
            cache.renameTo(new File(dstFileName));

            //! レジュームファイルを削除
            resume.delete();

            return true;
        }
    }

    /**
     * 処理を中断する。
     * @throws IOException
     */
    public void abort() throws IOException {
        downloader.close();
        downloader = null;
    }
}
