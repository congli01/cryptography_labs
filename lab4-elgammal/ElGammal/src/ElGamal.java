//import java.math.BigInteger;
//import java.util.Random;
//
///**
// * @author LICONG
// */
//public class ElGammal {
//    public static void main(String[] args) {
//        BigInteger p_big, q_big;
//        // 生成大素数q，直到p是素数，其中p=2q+1
//        do{
//            q_big = newPrime();
//            p_big = q_big.multiply(BigInteger.TWO).add(BigInteger.ONE);
//        }while (p_big.isProbablePrime(256));
//        // 生成一个随机数g，1<g<p-1，直到g^2 mod p 和g^q mod p 都不等于1，得到g是p的本原根
//        int p = p_big.intValue();
//        int q = q_big.intValue();
//        int g;
//        do{
//            g = (int)(p * Math.random());
//        } while((Math.pow(g, 2) % p == 1) || (Math.pow(q, 2) % p == 1));
//
//        int x = (int) (p * Math.random());
//        BigInteger g_big = BigInteger.valueOf(g);
//        BigInteger y_big = g_big.pow(x).mod(p_big);
//        int y = y_big.intValue();
//        System.out.println("公钥：(" + p + "," + g + "," + y + ")");
//        System.out.println("私钥：" + x);
//
//        // 待签消息
//        String m = "200111205";
//
//
//    }
//
//    /**
//     * 生成16bit大素数
//     */
//    public static BigInteger newPrime() {
//        Random r = new Random();
//        BigInteger prime = BigInteger.probablePrime(16, r);
//        while(!prime.isProbablePrime(256)) {
//            prime = BigInteger.probablePrime(16, r);
//        }
//        return prime;
//    }
//
//    /**
//     * 欧几里得算法
//     * @param x
//     * @param y
//     * @return 最大公约数
//     */
//    public static int gcd(int x, int y) {
//        int result;
//        // 令x为较大的数
//        if(x < y) {
//            int temp = x;
//            x = y;
//            y = temp;
//        }
//
//        if(y != 0){
//            int mod = y % x;
//            x = y;
//            y = mod;
//            return gcd(x, y);
//        }
//
//        return x;
//    }
//}

import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

/**
 * @author LICONG
 */
public class ElGamal {
    /**
     * 大素数和本原元
     */
    static BigInteger p, g;
    static BigInteger r, s;

    public static void main(String[] args) {
        BigInteger y, x; // 随机数 P,g是P的生成元，公钥<y,g,p>，私钥<x,g,p> 0<a<p
        int z=32;


        //明文
        String message = "200111205";
        //明文的哈希值
        int hm = message.hashCode();
        System.out.println("明文为：" + message);

        ElGamal.getRandomP(z);
        x = ElGamal.getRandoma(p);
        y = ElGamal.calculatey(x, g, p);
        System.out.println("公钥(p,g,y)为:" + "(" + p + "," + g + "," + y + ")");
        System.out.println("私钥x为:" + x);

        // 公钥和私钥相同，选取的随机值k1和k2不同
        ElGamal.encrypt(hm, x, p, g);
        ElGamal.decrypt(r, s, hm,g, p,y);
        System.out.println("==============================");
        ElGamal.encrypt(hm, x, p, g);
        ElGamal.decrypt(r, s, hm, g, p, y);

        System.out.println("==============================");

        // 消息被篡改的情况
        String message1 = "502111002";
        int hm1 = message1.hashCode();
        System.out.println("原消息为：" + message);
        System.out.println("消息被篡改为：" + message1);
        ElGamal.decrypt(r, s, hm1, g, p, y);

    }

    /** 取一个大的随机素数P,计算P的生成元g */
    public static void getRandomP(int z) {
        Random r = new Random();
        BigInteger q = null;
        while (true) {
            q = BigInteger.probablePrime(z, r);
            if (q.bitLength() !=z) {
                continue;
            }
            // 如果q为素数则再进一步计算生成元
            if (q.isProbablePrime(10))
            {
                p = q.multiply(new BigInteger("2")).add(BigInteger.ONE);
                // 如果P为素数则OK~，否则继续
                if (p.isProbablePrime(10))
                {
                    break;
                }
            }
        }
        while (true) {
            // 产生一0<=k<p-1的随机素数
            g = BigInteger.probablePrime(p.bitLength() - 1, r);
            if (!g.modPow(BigInteger.ONE, p).equals(BigInteger.ONE)
                    && !g.modPow(q, p).equals(BigInteger.ONE)) {
                break;
            }
        }
    }

    /** 取随机数x */
    public static BigInteger getRandoma(BigInteger p) {
        BigInteger x;
        Random r = new Random();
        // 产生一0<=a<p-1的随机数
        x = new BigInteger(p.bitLength() - 1, r);
        return x;
    }

    /** 计算y=g^a mod p */
    public static BigInteger calculatey(BigInteger x, BigInteger g, BigInteger p) {
        BigInteger y;
        y = g.modPow(x, p);
        return y;
    }

    /** 签名 */
    public static void encrypt(int m, BigInteger x, BigInteger p, BigInteger g) {
        BigInteger message=BigInteger.valueOf(m);
        Random random = new Random();
        BigInteger k;
        while (true) {
            // 0<=k<p-2的随机数
            k = new BigInteger(p.bitLength() - 2, random);
            if (k.gcd(p.subtract(BigInteger.ONE)).equals(BigInteger.ONE)) {
                System.out.println("随机数k为:"+k);
                break;
            }
        }
        // 计算r=g^k mod p,s
        r = g.modPow(k, p);
        BigInteger p_MinusOne = p.subtract(BigInteger.ONE);
        BigInteger k_Reverse = k.modInverse(p_MinusOne);
        s = k_Reverse.multiply(message.subtract(x.multiply(r))).mod(p_MinusOne);
        System.out.println("签名(r,s)为:"+ "(" + r + "," + s + ")");
    }

    /** 认证 */
    public static void decrypt(BigInteger r, BigInteger s, int hm, BigInteger g, BigInteger p,BigInteger y) {

        BigInteger m = BigInteger.valueOf(hm);
        BigInteger v1,v2;

        v1=g.modPow(m,p);
        v2=((y.modPow(r,p)).multiply(r.modPow(s,p))).mod(p);
        if(v1.equals(v2)){
            System.out.println("签名验证通过");
        }else{
            System.out.println("签名验证不通过");
        }
    }
}
