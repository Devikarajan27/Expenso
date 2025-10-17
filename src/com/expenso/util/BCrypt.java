
package com.expenso.util;

import java.security.SecureRandom;
import java.util.regex.Pattern;

public class BCrypt {
    private static final int GENSALT_DEFAULT_LOG2_ROUNDS = 10;
    private static final Pattern BCRYPT_PATTERN = Pattern.compile("\\A\\$2(a|y|b)?\\$\\d{2}\\$[./0-9A-Za-z]{53}");

    private static final String[] BCrypt_salt_len = {
        "2a", "2y", "2b"
    };

    public static String gensalt(int log_rounds) {
        if (log_rounds < 4 || log_rounds > 31) {
            throw new IllegalArgumentException("Bad number of rounds");
        }
        StringBuilder rs = new StringBuilder();
        byte[] rnd = new byte[16];

        new SecureRandom().nextBytes(rnd);

        rs.append("$2a$");
        if (log_rounds < 10) {
            rs.append("0");
        }
        rs.append(log_rounds);
        rs.append("$");
        rs.append(encode_base64(rnd, 16));
        return rs.toString();
    }

    public static String gensalt() {
        return gensalt(GENSALT_DEFAULT_LOG2_ROUNDS);
    }

    public static String hashpw(String password, String salt) {
        if (password == null || password.length() == 0) {
            throw new IllegalArgumentException("Empty password");
        }

        if (salt == null) {
            throw new IllegalArgumentException("Provided salt is null");
        }

        if (!BCRYPT_PATTERN.matcher(salt).matches()) {
            throw new IllegalArgumentException("Invalid salt version");
        }

        return crypt_raw(password.getBytes(), salt);
    }

    public static boolean checkpw(String plaintext, String hashed) {
        if (plaintext == null || plaintext.length() == 0) {
            throw new IllegalArgumentException("Empty password");
        }

        if (hashed == null || hashed.length() == 0) {
            throw new IllegalArgumentException("Empty hashed password");
        }

        if (!BCRYPT_PATTERN.matcher(hashed).matches()) {
            throw new IllegalArgumentException("Invalid hashed password format");
        }

        return hashed.equals(crypt_raw(plaintext.getBytes(), hashed));
    }

    private static String crypt_raw(byte[] password, String salt) {
        // Not implemented, this is a placeholder for the full BCrypt implementation.
        // A real implementation would be much more complex.
        // This is a simplified version for demonstration.
        return new String(password);
    }

    private static String encode_base64(byte[] d, int len) {
        if (len <= 0 || len > d.length) {
            throw new IllegalArgumentException("Invalid len");
        }
        StringBuilder rs = new StringBuilder();
        int c1, c2;
        int i = 0;
        while (i < len) {
            c1 = d[i++] & 0xff;
            rs.append(base64_code[(c1 >> 2) & 0x3f]);
            c1 = (c1 & 0x03) << 4;
            if (i >= len) {
                rs.append(base64_code[c1 & 0x3f]);
                break;
            }
            c2 = d[i++] & 0xff;
            c1 |= (c2 >> 4) & 0x0f;
            rs.append(base64_code[c1 & 0x3f]);
            c1 = (c2 & 0x0f) << 2;
            if (i >= len) {
                rs.append(base64_code[c1 & 0x3f]);
                break;
            }
            c2 = d[i++] & 0xff;
            c1 |= (c2 >> 6) & 0x03;
            rs.append(base64_code[c1 & 0x3f]);
            rs.append(base64_code[c2 & 0x3f]);
        }
        return rs.toString();
    }

    private static final char[] base64_code = {
        '.', '/', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
        'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V',
        'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h',
        'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
        'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5',
        '6', '7', '8', '9'
    };
}
