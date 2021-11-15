import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Objects;

public class Example {

    public static void main(String[] args) throws Exception {
        String publicKey = "vip9";
        String secretKey = "tpvip9";

        SeniverseUtils seniverseUtils = new SeniverseUtils();

        seniverseUtils.addParameter("ts", new Date().getTime() / 1000);
        seniverseUtils.addParameter("fields", "precip_minutely");
        seniverseUtils.addParameter("public_key", publicKey);
        seniverseUtils.addParameter("locations", "29.5617:120.0962");

        String urlStr = seniverseUtils.getUrl("https://api.seniverse.com/v4", secretKey);

        URL url = new URL(urlStr);
        URLConnection conn = url.openConnection();

        InputStream stream = conn.getInputStream();

        System.out.println("");
        System.out.println("Url: ".concat(urlStr));
        System.out.println(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
    }
}