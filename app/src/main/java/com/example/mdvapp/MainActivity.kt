package com.example.mdvapp

import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.nio.charset.StandardCharsets
import java.security.*
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec


class MainActivity : AppCompatActivity() {
    private var keyNumber: Int? = null
    private val textEncrypt = "Miichisoft-mobile-present"
    private val myKey = "MOBILE"
    private val keyStoreAliasRsa = "android"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val dataAfterEncryptDV = roundTranslation(textEncrypt, 0)
        Log.d("encryptDV", dataAfterEncryptDV)
        Log.d("decryptDV", roundTranslation(dataAfterEncryptDV, 1))

        //Hash
        val hashFunc = hashFunc(textEncrypt)
        Log.d("hashFunc", hashFunc)


        //AES
        val dataAfterEncryptAES = enCryptAES(textEncrypt, myKey)
        Log.d("encryptAES", dataAfterEncryptAES)
        Log.d("deEncryptAES", deCryptAES(dataAfterEncryptAES, myKey))


        //AES
//        val dataEncryptAES = encryptAES(textEncrypt, myKey)
//        Log.d("encryptAES", dataEncryptAES)
//
//        val dataDeEncrypt = deCryptAES(dataEncryptAES, myKey)
//        Log.d("dataDeEncryptAES", dataDeEncrypt)

        //DES
//        val dataEncryptDES = encryptDES(textEncrypt, myKey)
//        Log.d("dataEncryptDES", dataEncryptDES)
//
//        val dataDeEncryptDES = deCryptDES(dataEncryptDES, myKey)
//        Log.d("dataDeEncryptDES", dataDeEncryptDES)


        //RSA
        val dataAfterEncryptRSA = testEncryptRSA(textEncrypt)
        Log.d("eEncryptRSA", dataAfterEncryptRSA)
        Log.d("decryptRSA", testDeCryptRSA(dataAfterEncryptRSA))


        //AndroidKeyStore
        createKeyStore(keyStoreAliasRsa)
        val dataEncryptRSAKeyStore = encryptStringKeyStore(textEncrypt, keyStoreAliasRsa)
        Log.d("encryptString", dataEncryptRSAKeyStore)
        Log.d(
            "encryptString",
            deCryptStringKeyStore(
                dataEncryptRSAKeyStore,
                keyStoreAliasRsa
            )
        )
    }

    private fun roundTranslation(dataCrypt: String, encryptOrDecrypt: Int): String {
        val key = 9
        var text = ""
        for (i in dataCrypt.indices) {
            val chars = dataCrypt[i].toInt()
            var y = if (encryptOrDecrypt == 0) (chars + key % 26) else (chars - key % 26)
            when (chars) {
                in 65..90 -> {
                    keyNumber = 0
                }
                in 97..122 -> {
                    keyNumber = 32
                }
                else -> {
                    y = chars
                }
            }

            keyNumber?.let {
                if (y > (90 + it)) {
                    y -= 26
                }
                if (y < (65 + it)) {
                    y += 26
                }
            }
            keyNumber = null
            text += y.toChar()

        }
        return text
    }

    private fun hashFunc(textEncrypt: String): String {
        val md5 = MessageDigest.getInstance("MD5")//SHA-256
        var sbb = ""
        val byteArray: ByteArray = md5.digest(textEncrypt.toByteArray(StandardCharsets.UTF_8))
        for (item in byteArray) {
            sbb += String.format("%02x", item)
        }
        Log.d("sbb", "$sbb")
        return sbb
    }


//    private fun encryptAES(textEncrypt: String, myKey: String): String {
//        val sha = MessageDigest.getInstance("SHA-256")
//        var key = myKey.toByteArray(StandardCharsets.UTF_8)
//        key = sha.digest(key)
//        key = key.copyOf(16)
//        val secretKey = SecretKeySpec(key, "AES")
//        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        return Base64.getEncoder()
//            .encodeToString(cipher.doFinal(textEncrypt.toByteArray()))
//    }
//
//    private fun deCryptAES(textEncrypt: String, myKey: String): String {
//        val sha = MessageDigest.getInstance("SHA-256")
//        var key: ByteArray = myKey.toByteArray(StandardCharsets.UTF_8)
//        key = sha.digest(key)
//        key = key.copyOf(16)
//        val secretKey = SecretKeySpec(key, "AES")
//        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
//        cipher.init(Cipher.DECRYPT_MODE, secretKey)
//        return String(
//            cipher.doFinal(
//                Base64.getDecoder().decode(textEncrypt)
//            )
//        )
//    }

//    private fun encryptDES(textEncrypt: String, myKey: String): String {
//        val sha = MessageDigest.getInstance("SHA-256")
//        var key: ByteArray = myKey.toByteArray(StandardCharsets.UTF_8)
//        key = sha.digest(key)
//        key = key.copyOf(8)
//        val secretKey = SecretKeySpec(key, "DES")
//        val cipher = Cipher.getInstance("DES/ECB/PKCS5Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
//        return Base64.getEncoder().encodeToString(cipher.doFinal(textEncrypt.toByteArray()))
//    }
//
//    private fun deCryptDES(textEncrypt: String, myKey: String): String {
//        val sha = MessageDigest.getInstance("SHA-256")
//        var key: ByteArray = myKey.toByteArray(StandardCharsets.UTF_8)
//        key = sha.digest(key)
//        key = key.copyOf(8)
//
//        val secretKey = SecretKeySpec(key, "DES")
//        Log.d("secretKey","$secretKey")
//        val cipher = Cipher.getInstance("DES/ECB/PKCS5PADDING")
//        cipher.init(Cipher.DECRYPT_MODE, secretKey)
//        return String(
//            cipher.doFinal(
//                Base64.getDecoder().decode(textEncrypt)
//            )
//        )
//    }

    private val sr = SecureRandom()
    private var privateKey: PrivateKey? = null
    private fun testEncryptRSA(textEncrypt: String): String {
        val kpg = KeyPairGenerator.getInstance("RSA")
        kpg.initialize(1024, sr)
        val kp = kpg.genKeyPair()
        val publicKey = kp.public
        privateKey = kp.private
        Log.d("privateKey", "$privateKey")

        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        val textByte = cipher.doFinal(textEncrypt.toByteArray())
        return Base64.getEncoder().encodeToString(textByte)
    }


    private fun testDeCryptRSA(text: String): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(
            cipher.doFinal(
                Base64.getDecoder().decode(text)
            )
        )
    }

    private fun enCryptAES(textEncrypt: String, myKey: String): String {
        val secretKeySpec = SecretKeySpec(myKey.toByteArray().copyOf(16), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec)
        val byteArray = cipher.doFinal(textEncrypt.toByteArray())
        return (Base64.getEncoder().encodeToString(byteArray))
    }

    private fun deCryptAES(text: String, myKey: String): String {
        val secretKeySpec = SecretKeySpec(myKey.toByteArray().copyOf(16), "AES")
        val cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec)
        return String(
            cipher.doFinal(
                Base64.getDecoder().decode(text)
            )
        )
    }

    ////AndroidKeyStore
    private var keyStore: KeyStore? = null
    private var keyPair: KeyPair? = null
    private fun createKeyStore(keyStoreAlias: String) {
        keyStore = KeyStore.getInstance("AndroidKeyStore")
        keyStore?.load(null)

        if (!keyStore!!.containsAlias(keyStoreAlias)) {
            val keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA)
            val keyGenParameterSpec = KeyGenParameterSpec.Builder(
                keyStoreAlias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )

                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                .setDigests(KeyProperties.DIGEST_SHA256)
                .build()
            keyPairGenerator.initialize(keyGenParameterSpec)
            keyPair = keyPairGenerator.genKeyPair()
        }

    }

    private fun encryptStringKeyStore(text: String, alias: String): String {
        val publicKey = keyStore?.getCertificate(alias)?.publicKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")//ecb Electronic Codebook mode.
        publicKey?.let {
            cipher.init(Cipher.ENCRYPT_MODE, it)
        }

        val textByte = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(textByte)
    }

    private fun deCryptStringKeyStore(text: String, alias: String): String {
        val privateKeyEntry = keyStore?.getEntry(alias, null) as KeyStore.PrivateKeyEntry
        val privateKey = privateKeyEntry.privateKey
        val cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        return String(
            cipher.doFinal(
                Base64.getDecoder().decode(text)
            )
        )
    }


//    private var keyStoreTest: KeyStore? = null
//    private var keySecrect: SecretKey? = null
//    private fun createTestKeyStoreAES() {
//        keyStoreTest = KeyStore.getInstance("AndroidKeyStore")
//        keyStoreTest?.load(null)
//        val keyGenerator =
//            KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
//        val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
//
//        keyGenerator.init(
//            KeyGenParameterSpec.Builder(
//                "keyAlias",
//                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
//            )
//                .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
//                .setRandomizedEncryptionRequired(false)
//                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
//                .build()
//        )
//        keySecrect = keyGenerator.generateKey()
//    }

//    private fun testEncrypt(text: String?): String {
//        keyStoreTest?.load(null)
//        val key1 = keyStoreTest?.getEntry("keyAlias", null) as KeyStore.SecretKeyEntry
//        val key = key1.secretKey
//        Log.d("keyNull", "${key}")
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        cipher.init(Cipher.ENCRYPT_MODE, keySecrect!!)
//        val textByte = cipher.doFinal(text?.toByteArray())
//        return Base64.getEncoder().encodeToString(textByte)
//    }
//
//    //
//    private fun testEncrypt1(text: String?): String {
//        val key1 = keyStoreTest?.getEntry("keyAlias", null) as KeyStore.SecretKeyEntry
//        val key = key1.secretKey
//        val cipher = Cipher.getInstance("AES/CBC/PKCS7Padding")
//        keySecrect?.let {
//            cipher.init(Cipher.DECRYPT_MODE, it, IvParameterSpec(cipher.iv))
//        }
//
//        return String(
//            cipher.doFinal(
//                Base64.getDecoder().decode(text)
//            ), StandardCharsets.UTF_8
//        )
//    }


//    private fun getKeyInfo(): String {
//        val secretKey = ((keyStoreTest?.getEntry("keyAlias", null)) as KeyStore.SecretKeyEntry)
//
////        val privateKeyBytes: ByteArray = android.util.Base64.encode(privateKey?.encoded, android.util.Base64.DEFAULT)
////        val priKeyString = String(privateKeyBytes)
//        val publicKeyBytes: ByteArray =
//            android.util.Base64.encode(secretKey.secretKey.toString().toByteArray(), android.util.Base64.DEFAULT)
//        return String(publicKeyBytes)
//    }
//    private fun getAliases() {
//        var aliasesString = ""
//        var keyAlis = arrayListOf<String>()
//        val aliases = keyStore?.aliases()
//        if (aliases != null) {
//            while (aliases.hasMoreElements()) {
//                keyAlis.add(aliases.nextElement())
//
//            }
//        }
//        Log.d("getAliases", "${keyAlis}")
//    }

}


// ECB mode l?? ti??u chu???n c?? b???n nh???t c???a DES. Plaintext (v??n b???n hay th??ng tin ch??a m?? h??a) ???????c chia ra th??nh m???i kh???i 8-byte v?? m???i kh???i 8-byte n??y ???????c m?? h??a, h???p l???i t???t c??? c??c kh???i 8-byte m?? h??a n??y th??nh ciphertext (v??n b???n hay th??ng tin ???????c m?? h??a) ho??n ch???nh.
//M???i kh???i 8-byte khi ???????c m?? h??a s??? t???o ra m???t b??? ?????m 64-bit ?????u v??o. T??? ????, ph??t sinh ra m???t v???n ????? ???n???u kh???i cu???i c??ng c???a plaintext kh??ng ????? 8-byte th?? sao ? ???.  B???ng c??ch n??o ???? ph???i l??m cho kh???i cu???i c??ng n??y ph???i ????? 8-byte, th?? c??ch l??m cho kh???i cu???i c??ng n??y ????? 8-byte ???????c g???i l?? PADDING. V???n ????? c???a padding l?? ???khi ciphertext ???????c gi???i m??, padding ph???i ????a ???????c v??? ????ng tr???ng th??i ban ?????u???.
//????? gi???i quy???t v???n ????? v??? padding, c??ng ty RSA Data Security ph??t tri???n 1 ti??u chu???n g???i l?? Public Key Crytography Standard # 5 padding (vi???t t???t l?? PKCS#5 ). C??ch l??m vi???c c???a PKCS#5 padding nh?? sau:
//- N???u n l?? s??? c??c byte c???n th??m v??o kh???i cu???i c??ng, th?? gi?? tr??? c???a m???i byte th??m v??o ???? l?? n.
//- N???u kh???i cu???i c??ng kh??ng c???n th??m b???t k??? byte n??o c???, th?? m???t kh???i m???i 8-byte ???????c t???o ra v?? gi?? tr??? c???a m???i byte l?? 8.

//publicKey: must use RSAPublickey or X509EncodedKeySpec
//privateKey : must RSAPrivatekey or PKCS8EncodedKeySpec


///The block size is a property of the used cipher algorithm. For AES it is always 16 bytes