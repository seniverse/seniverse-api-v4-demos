import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

public class SeniverseUtils {
    private final Map<String, String> paramMap = new HashMap<>();

    public void addParameter(String key, String value) {
        this.paramMap.put(key, value);
    }

    public void addParameter(String key, long value) {
        this.paramMap.put(key, String.valueOf(value));
    }

    public void removeParameter(String key) {
        this.paramMap.remove(key);
    }

    public String getUrl(String baseUrl, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Set<String> paramSet = this.paramMap.keySet();

        List<String> paramList = new ArrayList<>(paramSet);

        paramList.sort(Comparator.naturalOrder());
        boolean isFirst = true;
        StringBuilder stringBuilder = new StringBuilder();
        StringBuilder encodeStringBuilder = new StringBuilder();

        for (String key : paramList) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append("&");
                encodeStringBuilder.append("&");
            }
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(this.paramMap.get(key));

            encodeStringBuilder.append(key);
            encodeStringBuilder.append("=");
            encodeStringBuilder.append(URLEncoder.encode(this.paramMap.get(key), "UTF-8"));
        }

        String query = stringBuilder.toString();

        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));

        String sig = Base64.getEncoder().encodeToString(hmac.doFinal(query.getBytes(StandardCharsets.UTF_8)));

        stringBuilder.append("&sig=");
        stringBuilder.append(sig);

        encodeStringBuilder.append("&sig=");
        encodeStringBuilder.append(URLEncoder.encode(sig, "UTF-8"));

        String encodeUlr = baseUrl.concat("?").concat(encodeStringBuilder.toString());
        System.out.println("浏览器访问链接: ");
        System.out.println(encodeUlr);

        return baseUrl.concat("?").concat(stringBuilder.toString());
    }
}
