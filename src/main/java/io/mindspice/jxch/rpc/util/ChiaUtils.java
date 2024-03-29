package io.mindspice.jxch.rpc.util;

import io.mindspice.jxch.rpc.schemas.object.Coin;
import io.mindspice.jxch.rpc.schemas.object.CoinRecord;
import org.bouncycastle.util.encoders.Hex;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class ChiaUtils {
    private static final MessageDigest shaDigest;
    private static final BigDecimal MOJO_PER_XCH = BigDecimal.valueOf(Math.pow(10, 12));
    private static final BigDecimal MOJO_PER_CAT = new BigDecimal("1000");


    static {
        try {
            shaDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getSha256(byte[] input) {
        byte[] hash = shaDigest.digest(input);
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) { hexString.append('0'); }
            hexString.append(hex);
        }
        shaDigest.reset();
        return hexString.toString();
    }

    public static String getSha256(String input) {
        return getSha256(input.getBytes());
    }

    public static String getCoinId(Coin coin) {
        String pc = coin.parentCoinInfo();
        String ph = coin.puzzleHash();

        byte[] parentCoinBytes = Hex.decode(pc.length() == 66 ? pc.substring(2) : pc);
        byte[] puzzleHashBytes = Hex.decode(ph.length() == 66 ? ph.substring(2) : ph);
        byte[] amountBytes = BigInteger.valueOf(coin.amount()).toByteArray();

        byte[] concatBuffer = new byte[parentCoinBytes.length + puzzleHashBytes.length + amountBytes.length];
        System.arraycopy(parentCoinBytes, 0, concatBuffer, 0, parentCoinBytes.length);
        System.arraycopy(puzzleHashBytes, 0, concatBuffer, parentCoinBytes.length, puzzleHashBytes.length);
        System.arraycopy(amountBytes, 0, concatBuffer, parentCoinBytes.length + puzzleHashBytes.length, amountBytes.length);

        return getSha256(concatBuffer);
    }

    public static BigInteger xchToMojos(BigDecimal xch) {
        return xch.multiply(MOJO_PER_XCH).toBigInteger();
    }

    public static BigInteger xchToMojos(Double xch) {
        BigDecimal xc = new BigDecimal(xch);
        return xc.multiply(MOJO_PER_XCH).setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    public static BigDecimal mojosToXch(BigDecimal mojos) {
        return mojos.divide(MOJO_PER_XCH, 12, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal mojosToXch(Long mojos) {
        BigDecimal mj = new BigDecimal(mojos);
        return mj.divide(MOJO_PER_XCH, 12, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigInteger catToMojos(BigDecimal cat) {
        return cat.multiply(MOJO_PER_CAT).toBigInteger();
    }

    public static BigInteger catToMojos(Double cat) {
        BigDecimal c = new BigDecimal(cat);
        return c.multiply(MOJO_PER_CAT).setScale(0, RoundingMode.HALF_UP).toBigInteger();
    }

    public static BigDecimal mojosToCat(BigDecimal mojos) {
        return mojos.divide(MOJO_PER_CAT, 4, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static BigDecimal mojosToCat(Long mojos) {
        BigDecimal mj = new BigDecimal(mojos);
        return mj.divide(MOJO_PER_CAT, 4, RoundingMode.HALF_UP).stripTrailingZeros();
    }

    public static String normalizeHex(String hexString) {
        if (hexString.length() < 64 || hexString.length() > 66) {
            throw new IllegalArgumentException("Hex mus be 64-66 length string");
        }
        return hexString.length() == 64 ? "0x" + hexString : hexString;
    }

}
