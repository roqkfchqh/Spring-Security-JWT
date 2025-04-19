package lcy.jwt.utils;

public class JwtTokenUtils {

    public static String removePrefix(String tokenWithPrefix, String prefix) {
        if (tokenWithPrefix == null || prefix == null) {
            return tokenWithPrefix;
        }

        if (tokenWithPrefix.startsWith(prefix)) {
            return tokenWithPrefix.substring(prefix.length()).trim(); // 공백 제거
        }

        return tokenWithPrefix;
    }
}