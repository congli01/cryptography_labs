package com.company;

import java.math.BigInteger;
import java.util.Random;

///**
// * @author LICONG
// * 大数运算器类
// */
//public class BigIntegerOperator {
//
//    /**
//     * 生成1024bit大素数
//     */
//    public BigInteger newPrime() {
//        Random r = new Random();
//        BigInteger prime = BigInteger.probablePrime(1024, r);
//        while(!prime.isProbablePrime(256)) {
//            prime = BigInteger.probablePrime(1024, r);
//        }
//        return prime;
//    }
//}
//
//    /**
//     * 密钥产生。
//     *
//     * @param generateKeyFlag 大质数p、q的产生方式，0：文件读入；1：随机产生
//     * @param pqLength        p、q的长度（比特数）
//     * @throws RSA.pqException
//     */
//    private void generateKey(int generateKeyFlag, int pqLength) throws RSA.pqException {
//        BigInteger p = BigInteger.ZERO;// 两个大素数
//        BigInteger q = BigInteger.ZERO;
//        BigInteger φn;// = (p-1)(q-1)
//
//        if (generateKeyFlag == 0) {// 文件读入形式产生p、q、e
//            String pqeFileName = "src\\pqe.txt";
//            try {
//                FileReader fileReader = new FileReader(pqeFileName);
//                BufferedReader bufferedReader = new BufferedReader(fileReader);
//                for (int i = 0; i < 2; i++) {
//                    if (i == 0) {
//                        p = new BigInteger(bufferedReader.readLine());
//                    } else {
//                        q = new BigInteger(bufferedReader.readLine());
//                    }
//                }
//                bufferedReader.close();// 关闭输入流
//                fileReader.close();
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//
//            String[] tmp = { "p", "q" };
//            BigInteger pq[] = { p, q };
//            for (int i = 0; i < 2; i++) {// 判断p、q的选取是否符合要求
//                if (isPrime(pq[i]) && pq[i].bitLength() >= PQMINLENGTH) {// 符合要求
//                    continue;
//                } else if (!isPrime(pq[i]) && pq[i].bitLength() >= PQMINLENGTH) {
//                    throw new pqException(tmp[i] + "不是质数，不符合要求，请重新选择！");
//                } else if (isPrime(pq[i]) && pq[i].bitLength() < PQMINLENGTH) {
//                    throw new pqException(tmp[i] + "长度为" + pq[i].bitLength() + "，小于" + PQMINLENGTH + "，不符合要求，请重新选择！");
//                } else {
//                    throw new pqException(
//                            tmp[i] + "不是质数，且其长度" + pq[i].bitLength() + "小于" + PQMINLENGTH + "，不符合要求，请重新选择！");
//                }
//            }
//        } else {// 随机产生
//            if (pqLength < PQMINLENGTH) {
//                throw new pqException("p、q长度小于" + PQMINLENGTH + "，请重新选择更长的质数！");
//            }
//            p = RSA.generateNBitRandomPrime(pqLength);
//            q = RSA.generateNBitRandomPrime(pqLength);
//        }
//
//        n = p.multiply(q);
//        φn = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
//        d = RSA.extdGcd(e, φn)[1];// 利用扩展欧几里得算法求私钥d
//        if (d.compareTo(BigInteger.ZERO) != 1) {// 私钥不可以小于0
//            d = d.add(φn);
//        }
//    }