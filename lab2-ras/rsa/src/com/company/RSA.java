package com.company;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.util.Random;

/**
 * @author LICONG
 */
public class RSA {

    /**
     * 公钥与密钥
     */
    private BigInteger n;
    private BigInteger e;
    private BigInteger d;

    /**
     * 根据传入参数产生密钥（公钥、私钥）。
     */
    public RSA(BigInteger n, BigInteger e, BigInteger d) {
        this.n = n;
        this.e = e;
        this.d = d;
    }

    public static void main(String[] args) throws IOException {

        // 从文件读取需要加密的明文
        String path = "src/lab2-Plaintext.txt";
        FileReader fileReader = new FileReader(path);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String plainText = bufferedReader.readLine();

        String path2 = "src/lab2-result.txt";
        FileWriter fileWriter = new FileWriter(path2);

        // 生成密钥与公钥
        BigInteger n, e, d;
        BigInteger[] key = new BigInteger[3];
        key = generateKey();
        n = key[0];
        e = key[1];
        d = key[2];
        fileWriter.write("n:" + n + "\n");
        fileWriter.write("e:" + e + "\n");
        fileWriter.write("d:" + d + "\n");

        RSA rsa = new RSA(n, e, d);
        BigInteger[] c = rsa.encryption(plainText);
        System.out.println("加密后：");
        fileWriter.write("密文：\n");
        for (int i = 0; i < c.length; i++) {
            System.out.println(c[i]);
            fileWriter.write(c[i].toString());
        }
        System.out.println("解密后：" + rsa.decryption(c));
        fileWriter.write("\n解密：");
        fileWriter.write("\n" + rsa.decryption(c));
        fileWriter.close();
    }



    /**
     * 产生密钥和公钥
     */
    private static BigInteger[] generateKey(){
        BigInteger p, q, n, phi_n, e, d;
        BigInteger[] key = new BigInteger[3];
        p = newPrime();
        q = newPrime();
        n = p.multiply(q);
        phi_n = (p.subtract(BigInteger.valueOf(1))).multiply(q.subtract(BigInteger.valueOf(1)));
        e = BigInteger.valueOf(65537);
        System.out.println("p:" + p);
        System.out.println("q:" + q);
        System.out.println("n:" + n);
        System.out.println("phi_n:" + phi_n);
        if((e.gcd(phi_n).compareTo(BigInteger.valueOf(1))) == 0) {
            System.out.println("e:" + e);
        } else {
            throw new RuntimeException("e的选择出错");
        }
        // 利用扩展欧几里得算法求私钥d
        d = RSA.extdGcd(e, phi_n)[1];
        // 私钥需满足0 <= d <= n
        if (d.compareTo(BigInteger.ZERO) != 1) {
            d = d.add(phi_n);
        }
        System.out.println("d:" + d);
        key[0] = n;
        key[1] = e;
        key[2] = d;
        return key;
    }

    /**
     * 生成1024bit大素数
     */
    public static BigInteger newPrime() {
        Random r = new Random();
        BigInteger prime = BigInteger.probablePrime(1024, r);
        while(!prime.isProbablePrime(256)) {
            prime = BigInteger.probablePrime(1024, r);
        }
        return prime;
    }

    /**
     * RSA加密。
     *
     * @param plainText 明文
     * @return mArray 加密后的BigInteger类型的数组
     * @throws UnsupportedEncodingException
     */
    private BigInteger[] encryption(String plainText) throws UnsupportedEncodingException {
        String textNum = "";
        // 明文数字表示形式
        BigInteger m = BigInteger.ZERO;
        byte[] textByte = plainText.getBytes("UTF-8");
        // 每个字节用3位数的整数表示，不够则在前面补0
        for (int i = 0; i < textByte.length; i++) {
            int bn = textByte[i] & 0xff;
            if (bn < 10) {
                textNum += "00" + bn;
            } else if (bn < 100) {
                textNum += "0" + bn;
            } else {
                textNum += bn;
            }
        }
        m = new BigInteger(textNum);

        // 明文分组结果
        BigInteger[] mArray = null;
        // m < n，可直接加密
        if (m.compareTo(n) == -1) {
            mArray = new BigInteger[1];
            mArray[0] = m;
        } else {
            // 每组明文长度
            int groupLength = n.toString().length() - 1;
            // 明文转化为字符串的长度
            int mStringLength = m.toString().length();
            // 由于前面每个字节用3位整数表示，因此每组的长度必须为3的整数，避免恢复时错误
            while (groupLength % 3 != 0) {
                groupLength--;
            }
            // 如果最后一组的长度不足
            if (mStringLength % groupLength != 0) {
                mArray = new BigInteger[mStringLength / groupLength + 1];
            } else {
                mArray = new BigInteger[mStringLength / groupLength];
            }

            String tmp;
            for (int i = 0; i < mArray.length; i++) {
                tmp = "";
                if (i != mArray.length - 1) {// 根据每组长度进行分割分组保存
                    tmp = textNum.substring(groupLength * i, groupLength * i + groupLength);
                } else {
                    tmp = textNum.substring(groupLength * i);
                }
                mArray[i] = new BigInteger(tmp);
            }
        }

        for (int i = 0; i < mArray.length; i++) {// 逐组加密并返回
            mArray[i] = expMod(mArray[i], e, n);
        }
        return mArray;
    }

    /**
     * RSA解密。
     *
     * @param c BigInteger数组类型表达的密文
     * @return new String(result) 解密结果
     */
    private String decryption(BigInteger[] c) {
        String cPadding = "";
        String mToString = "";
        int mToStringLengthMod = 0;
        BigInteger m = BigInteger.ZERO;
        for (int i = 0; i < c.length; i++) {// 逐组解密
            m = RSA.expMod(c[i], d, n);
            mToString = m.toString();
            mToStringLengthMod = m.toString().length() % 3;
            if (mToStringLengthMod != 0) {// 由于加密时String转BigInter时前者前面的0并不会计入，所以需要确认并补全
                for (int j = 0; j < 3 - mToStringLengthMod; j++) {
                    mToString = "0" + mToString;
                }
            }
            cPadding += mToString;
        }

        int byteNum = cPadding.length() / 3;// 明文总字节数
        byte[] result = new byte[byteNum];
        for (int i = 0; i < byteNum; i++) {// 每三位数转化为byte型并返回该byte数组所表达的字符串
            result[i] = (byte) (Integer.parseInt(cPadding.substring(i * 3, i * 3 + 3)));
        }
        return new String(result);
    }

    /**
     * 利用扩展欧几里得算法求出私钥d，使得de = kφ(n)+1，k为整数。
     *
     * @param e  公钥
     * @param φn =(p-1)(q-1)
     * @return gdk BigInteger数组形式返回最大公约数、私钥d、k
     */
    private static BigInteger[] extdGcd(BigInteger e, BigInteger φn) {
        BigInteger[] gdk = new BigInteger[3];

        if (φn.compareTo(BigInteger.ZERO) == 0) {
            gdk[0] = e;
            gdk[1] = BigInteger.ONE;
            gdk[2] = BigInteger.ZERO;
        } else {
            gdk = extdGcd(φn, e.remainder(φn));
            BigInteger tmp_k = gdk[2];
            gdk[2] = gdk[1].subtract(e.divide(φn).multiply(gdk[2]));
            gdk[1] = tmp_k;
        }
        return gdk;
    }

    /**
     * 快速幂模运算，返回base^exponent mod module的结果。
     *
     * @param base     底数
     * @param exponent 指数
     * @param module   模数
     * @return result 结果
     */
    private static BigInteger expMod(BigInteger base, BigInteger exponent, BigInteger module) {
        BigInteger result = BigInteger.ONE;
        BigInteger tmp = base.mod(module);

        while (exponent.compareTo(BigInteger.ZERO) != 0) {
            if ((exponent.and(BigInteger.ONE).compareTo(BigInteger.ZERO)) != 0) {
                result = result.multiply(tmp).mod(module);
            }
            tmp = tmp.multiply(tmp).mod(module);
            exponent = exponent.shiftRight(1);
        }

        return result;
    }
}
