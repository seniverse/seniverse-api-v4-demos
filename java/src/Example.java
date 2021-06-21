import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import javax.crypto.CipherInputStream;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.util.Date;
import javax.crypto.Mac;
import java.util.Base64;
import java.util.Objects;
import java.net.URLEncoder;

public class Example {

    public static void main(String[] args) throws Exception {
        String publicKey = "XXX";
        String secretKey = "XXX";

        String query = "fields=precip_minutely&locations=29.5617:120.0962&public_key=".concat(publicKey).concat("&ts=").concat(Long.toString(new Date().getTime() / 1000));
        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secretKey.getBytes("UTF-8"), "HmacSHA1"));
        String sig = Base64.getEncoder().encodeToString(hmac.doFinal(query.getBytes("UTF-8")));
        // String signed = query.concat("&sig=").concat(URLEncoder.encode(sig, "UTF-8"));

        URL url = new URL("https://api.seniverse.com/v4?".concat(query));
        URLConnection conn = url.openConnection();

        conn.setRequestProperty("Sig", sig);

        InputStream stream = conn.getInputStream();
        String encoding = Objects.requireNonNullElse(conn.getContentEncoding(), "identity");

        System.out.println(new String(stream.readAllBytes(), StandardCharsets.UTF_8));

        System.err.println("");
        System.err.println("Query: ".concat(query));
        System.err.println("Content-Encoding: ".concat(encoding));
    }
}