package co.com.bancolombia.r2dbc.util;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.UUID;

public final class IdGeneratorUtil {

    private static final int DEFAULT_ID_LENGTH = 10;
    private static final int MAX_PREFIX_LENGTH = 50;
    private static final int MAX_ID_LENGTH = 200;
    private static final int MIN_ID_LENGTH = 1;
    private static final String ALPHANUMERIC_CHARACTERS =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final String PREFIX_LENGTH_ERROR =
            "Prefix length must be less than " + MAX_PREFIX_LENGTH + ".";
    private static final String LENGTH_ERROR =
            "Length must be greater than 0 and less than " + MAX_ID_LENGTH + ".";

    IdGeneratorUtil() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static UUID generateDefaultUUID() {
        return generateUuidFromString(String.valueOf(Instant.now().toEpochMilli()),
                DEFAULT_ID_LENGTH);
    }

    public static UUID generateUuidFromString(String prefix, int length) {
        if (prefix != null && prefix.length() > MAX_PREFIX_LENGTH) {
            throw new IllegalArgumentException(PREFIX_LENGTH_ERROR);
        }
        if (length < MIN_ID_LENGTH || length > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(LENGTH_ERROR);
        }

        String randomAlphanumericId = generateSecureRandomAlphanumeric(length);
        String fullId = (prefix == null || prefix.trim().isEmpty()) ? randomAlphanumericId
                : prefix.concat(randomAlphanumericId);

        return UUID.nameUUIDFromBytes(fullId.getBytes());
    }

    private static String generateSecureRandomAlphanumeric(int length) {
        var secureRandom = new SecureRandom();
        var result = new StringBuilder(length);

        for (var i = 0; i < length; i++) {
            var index = secureRandom.nextInt(ALPHANUMERIC_CHARACTERS.length());
            result.append(ALPHANUMERIC_CHARACTERS.charAt(index));
        }

        return result.toString();
    }

}
