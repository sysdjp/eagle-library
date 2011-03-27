package eagle.net.google.docs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.googleapis.GoogleTransport;
import com.google.api.client.googleapis.GoogleUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.util.Key;
import com.google.api.client.xml.XmlNamespaceDictionary;
import com.google.api.client.xml.atom.AtomParser;

/**
 * Google Docs内部のファイルを管理する。
 * @author SAKURA
 *
 */
public class GoogleDocsEntries {

    /**
     * アクセストークン
     */
    String token = null;

    /**
     * 取得したアイテム一覧
     */
    List<Entry> entries = new ArrayList<Entry>();

    public GoogleDocsEntries(String token) {
        this.token = token;
    }

    /**
     * docsにアクセスし、アイテム一覧を取得する。
     * @param keyword 検索ワード。nullですべて取得。
     * @throws IOException
     */
    public void access(String keyword) throws IOException {
        HttpTransport transport = GoogleTransport.create();
        GoogleHeaders headers = (GoogleHeaders) transport.defaultHeaders;
        headers.setApplicationName("Eagle/GoogleDocsDownloader");
        headers.gdataVersion = "3";
        headers.setGoogleLogin(token);

        //! parser
        AtomParser parser = new AtomParser();
        parser.namespaceDictionary = new XmlNamespaceDictionary();
        transport.addParser(parser);

        {
            HttpRequest request = transport.buildGetRequest();
            String url = "https://docs.google.com/feeds/default/private/full";
            if (keyword != null && keyword.length() > 0) {
                url += ("?title=" + keyword);
            }
            request.url = new GoogleUrl(url);
            HttpResponse responce = request.execute();

            {
                // 送信
                Feed feed = responce.parseAs(Feed.class);

                //                            final String key = "@src";
                for (EntryItem entry : feed.entries) {
                    entries.add(new Entry(entry));
                }
            }
        }
    }

    public int getEntriesCount() {
        return entries.size();
    }

    public Entry getEntry(int index) {
        return entries.get(index);
    }

    /**
     * Google Docs内の１アイテム。
     * @author SAKURA
     *
     */
    public class Entry {
        String title = null;
        String contentUrl = null;

        public Entry(EntryItem item) {
            title = item.title;
            if (item.content != null && item.content.get("@src") != null) {
                contentUrl = item.content.get("@src").toString();
            }
        }

        /**
         * ファイルタイトル。
         * @return
         */
        public String getTitle() {
            return title;
        }

        /**
         * コンテンツの元URL。
         * @return
         */
        public String getContentUrl() {
            return contentUrl;
        }
    };

    /**
     * Feed タグ
     */
    public static class Feed {
        @Key("entry")
        public List<EntryItem> entries;
    }

    /**
     * Entry タグ
     */
    public static class EntryItem {
        @Key
        public String summary;

        @Key
        public String title;

        @Key
        public String updated;

        @Key
        public Map<String, String> link;
        @Key
        public Map<String, String> content;
    }
}
