import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;

public class Example {

    public static void main(String[] args) throws Exception {
        String publicKey = "XXX";
        String secretKey = "XXX";

        SeniverseUtils seniverseUtils = new SeniverseUtils();

        seniverseUtils.addParameter("ts", new Date().getTime() / 1000);
        seniverseUtils.addParameter("fields", "precip_minutely");
        seniverseUtils.addParameter("public_key", publicKey);
        seniverseUtils.addParameter("locations", "29.5617:120.0962");

        try {
            String urlStr = seniverseUtils.getUrl("https://api.seniverse.com/v4", secretKey);

            URL url = new URL(urlStr);

            URLConnection conn = url.openConnection();

            InputStream stream = conn.getInputStream();
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();

            byte[] buffer = new byte[1024];
            int len;
            while ((len = stream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            outSteam.close();
            stream.close();
            String result = outSteam.toString();

            System.out.println("");
            System.out.println("url: ".concat(urlStr));
            System.out.println(result);
        } catch (Error error) {
            System.err.println(error.getMessage());
        }
    }
}