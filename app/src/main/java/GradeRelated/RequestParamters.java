package GradeRelated;

import org.jsoup.*;
import org.jsoup.nodes.Document;

import java.io.Serializable;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Miyuki on 2016/8/4.
 */

public class RequestParamters implements Serializable {
    public HashMap<String, String> urls = new HashMap<>();
    public List<HttpCookie> cookies = new ArrayList<>();
    public Document doc = null;
    public String baseUrl = "http://newjwc.tyust.edu.cn";
    public String id = "";
    public String referer = "";
    public String viewstate = "";
    public boolean loginSuccess = false;

    public RequestParamters() {

    }

    public RequestParamters(HashMap<String, String> urls, List<HttpCookie> cookies, Document doc, String id, String referer) {
        this.urls = urls;
        this.cookies = cookies;
        this.doc = doc;
        this.id = id;
        this.referer = referer;
    }

    public Connection Connect(String url){
        Connection conn = Jsoup.connect(url)
                .header("Referer", referer);
        for (HttpCookie cookie : cookies) {
            String[] tmp = cookie.toString().split("=");
            conn.cookie(tmp[0], tmp[1]);
        }
        conn.method(Connection.Method.GET);
        return conn;
    }
}
