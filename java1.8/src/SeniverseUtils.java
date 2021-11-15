import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
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

    public String getUrl(String baseUrl, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Set<String> paramSet = this.paramMap.keySet();

        List<String> paramList = new ArrayList<>(paramSet);

        paramList.sort(Comparator.naturalOrder());
        boolean isFirst = true;
        StringBuilder stringBuilder = new StringBuilder();

        for (String key : paramList) {
            if (isFirst) {
                isFirst = false;
            } else {
                stringBuilder.append("&");
            }
            stringBuilder.append(key);
            stringBuilder.append("=");
            stringBuilder.append(this.paramMap.get(key));
        }

        String query = stringBuilder.toString();

        Mac hmac = Mac.getInstance("HmacSHA1");
        hmac.init(new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA1"));

        String sig = Base64.getEncoder().encodeToString(hmac.doFinal(query.getBytes(StandardCharsets.UTF_8)));

        stringBuilder.append("&sig=");
        stringBuilder.append(sig);

        return baseUrl.concat("?").concat(stringBuilder.toString());
    }
}
