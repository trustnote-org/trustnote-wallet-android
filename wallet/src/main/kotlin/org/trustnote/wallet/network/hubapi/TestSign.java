package org.trustnote.wallet.network.hubapi;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

public class TestSign {


    /**
     * @param args
     */
    public static void main(String[] args) {
        new TestSign().getSign();
    }

    void getSign() {
        // Get the instance of the Key Generator with "EC" algorithm

        try {
            KeyPairGenerator g = KeyPairGenerator.getInstance("EC");
            ECGenParameterSpec kpgparams = new ECGenParameterSpec("secp256k1");
            g.initialize(kpgparams);

            KeyPair pair = g.generateKeyPair();
            // Instance of signature class with SHA256withECDSA algorithm
            Signature ecdsaSign = Signature.getInstance("SHA256withECDSA");
            ecdsaSign.initSign(pair.getPrivate());

            System.out.println("Private Keys is::" + pair.getPrivate());
            System.out.println("Public Keys is::" + pair.getPublic());

            String msg = "text ecdsa with sha256";//getSHA256(msg)
            ecdsaSign.update((msg + pair.getPrivate().toString())
                    .getBytes("UTF-8"));

            byte[] signature = ecdsaSign.sign();
            System.out.println("Signature is::"
                    + new BigInteger(1, signature).toString(16));

            // Validation
            ecdsaSign.initVerify(pair.getPublic());
            ecdsaSign.update(signature);
            if (ecdsaSign.verify(signature))
                System.out.println("valid");
            else
                System.out.println("invalid!!!!");

        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }

    }

}
