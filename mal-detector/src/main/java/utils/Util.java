package utils;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class Util {
    /**
     * 密钥
     */
    private final static byte[] PASSKEY = new byte[]{0x55, (byte) 0x8B, (byte) 0xEC, 0x51, 0x53, 0x56, (byte) 0x8B,
            0x75, 0x08, 0x56, (byte) 0xB8, 0x7C, 0x26, 0x00, 0x01,
            (byte) 0xC7};

    public static <K, V> Map<K, V> newMap() {
        return new HashMap<>();
    }

    public static <K, V> Map<K, V> newMTMap() {

        return new ConcurrentHashMap<>();
    }

    public static <T> List<T> newList() {
        return new ArrayList<>();
    }

    public static <T> Collection<T> newMTList() {
        return new ConcurrentLinkedQueue<>();
    }

    static public void createDir(String dir) throws IOException {
        final File f = new File(dir);
        if (!f.exists() && !f.mkdirs())
            throw new IOException("Failed to create Dir:" + dir);
        if (!f.isDirectory())
            throw new IOException("Must be directory!Dir:" + dir);
    }

    static public String smartMove(File srcFile, String dstDir) {
        final String base = FilenameUtils.getBaseName(srcFile.getName());
        final String ext = FilenameUtils.getExtension(srcFile.getName());
        String fileName = String.format("%s/%s.%s", dstDir, base, ext);
//        for (int i = 0; i < 10; ++i)

        int i = 1;
        do {
            try {
                FileUtils.moveFile(srcFile, new File(fileName));
                break;
            } catch (final IOException e) {
                fileName = String.format("%s/%s(%d).%s", dstDir, base, i, ext);
                i++;
            }
        } while (true);

        return fileName;
    }

    static public String hex2Str(byte[] data) {
        final String HEX = "0123456789ABCDEF";
        final StringBuilder sb = new StringBuilder();
        for (final byte b : data) {
            sb.append(HEX.charAt((b >> 4) & 0x0F));
            sb.append(HEX.charAt(b & 0x0F));
        }
        return sb.toString();
    }

    public static byte[] encrypt(byte[] buf) throws Exception {
        final byte[] pass = PASSKEY;

        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec dps = new IvParameterSpec(buf, 0, 16);
        final SecretKeySpec skeySpec = new SecretKeySpec(pass, "AES");

        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, dps);
        return cipher.doFinal(buf, 16, buf.length - 16);
    }

    /**
     * 加密
     *
     * @param buf 字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public static byte[] decrypt(byte[] buf) throws Exception {
        final byte[] pass = PASSKEY;

        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        final IvParameterSpec dps = new IvParameterSpec(buf, 0, 16);
        final SecretKeySpec skeySpec = new SecretKeySpec(pass, "AES");

        cipher.init(Cipher.DECRYPT_MODE, skeySpec, dps);
        return cipher.doFinal(buf, 16, buf.length - 16);
    }

    /**
     * 获取证书的MD5
     *
     * @param cert 证书
     * @return 证书MD5
     */
    static public String getCertMd5(Certificate cert) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(cert.getEncoded());
            return hex2Str(md.digest());
        } catch (final Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    static public Certificate[] getJarCerts(File file) {
        Certificate[] certs = null;
        try {
            certs = getCertWithZipFile(file);
        } catch (final IOException e) {
            try {
                certs = getCertWithZipstream(file);
            } catch (final IOException e1) {
            }
        }
        return certs;
    }

    static public Certificate[] getJarCerts(byte[] apkByte) {
        Certificate[] certs = null;
        try {
            certs = getCertWithZipByte(apkByte);
        } catch (final IOException e) {
        }
        return certs;
    }

    static public String getCertInfo(Certificate cert) {
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
            return x509Certificate.getSubjectDN().getName();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
        return "";
    }

    static public String getCertDetail(Certificate cert) {
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate xcert = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
            return xcert.toString();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
        return "";
    }

    static private Certificate[] getCertWithZipFile(File file) throws IOException {
        Certificate[] certs = null;
        final byte[] readBuffer = new byte[1024 * 8];
        final JarFile jarFile = new JarFile(file);
        final Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            final JarEntry je = entries.nextElement();
            if (je.isDirectory())
                continue;
            if (je.getName().startsWith("META-INF/"))
                continue;

            final InputStream is = jarFile.getInputStream(je);
            certs = loadCertificates(is, je, readBuffer);
            if (certs != null)
                break;
        }
        jarFile.close();

        return certs;
    }

    static private Certificate[] getCertWithZipstream(File file) throws IOException {
        Certificate[] certs = null;
        final byte[] readBuffer = new byte[1024 * 8];
        final JarInputStream zis = new JarInputStream(new FileInputStream(file));
        for (JarEntry je = zis.getNextJarEntry(); je != null; je = zis.getNextJarEntry()) {
            if (je.isDirectory())
                continue;
            if (je.getName().startsWith("META-INF/"))
                continue;

            certs = loadCertificates(zis, je, readBuffer);
            if (certs != null)
                break;
        }

        zis.close();
        return certs;
    }

    static private Certificate[] getCertWithZipByte(byte[] apkByte) throws IOException {
        Certificate[] certs = null;
        final byte[] readBuffer = new byte[1024 * 8];
        final JarInputStream zis = new JarInputStream(new ByteArrayInputStream(apkByte));
        for (JarEntry je = zis.getNextJarEntry(); je != null; je = zis.getNextJarEntry()) {
            if (je.isDirectory())
                continue;
            if (je.getName().startsWith("META-INF/"))
                continue;

            certs = loadCertificates(zis, je, readBuffer);
            if (certs != null)
                break;
        }

        zis.close();
        return certs;
    }

    private static Certificate[] loadCertificates(InputStream is, JarEntry je, byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve
            // its certificates.
            while (is.read(readBuffer, 0, readBuffer.length) != -1) {
                // not using
            }
            return je.getCertificates();
        } catch (final IOException e) {
            System.out.printf("Exception reading %s in %s:%s", je.getName(), je.getName(), e);
        } catch (final RuntimeException e) {
            System.out.printf("Exception reading %s in %s:%s", je.getName(), je.getName(), e);
        }
        return null;
    }
}
