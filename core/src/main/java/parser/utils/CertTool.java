package parser.utils;

import sun.security.pkcs.PKCS7;

import java.io.*;
import java.security.MessageDigest;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: lai
 * Date: 5/23/13
 * Time: 10:08 AM
 */
public class CertTool {


    /**
     * 从证书信息中获取MD5值
     *
     * @param cert Certificate
     * @return MD5
     */
    public static String getCertMd5(Certificate cert) {
        try {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(cert.getEncoded());
            return HashTool.hex2Str(md.digest());
        } catch (final Exception e) {
            e.printStackTrace();
            return "";
        }

    }

    /**
     * 获取一个 jar/zip 文件的证书信息
     *
     * @param file jar/zip 文件
     * @return hashMap,
     */
    public static HashMap<String, String> getCertificateInfos(File file) {
        Certificate[] certs;
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            certs = getCertificates(file);
            for (Certificate cert : certs) {
                hashMap.put(getCertMd5(cert), getCertSubject(cert));
            }
        } catch (NullPointerException e) {
            return hashMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    @SuppressWarnings("UnusedDeclaration")
    private static File bytes2File(byte[] bytes) throws IOException {
        File file = new File("sub.apk.tmp");
        FileOutputStream fos = new FileOutputStream(file);
        new BufferedOutputStream(fos).write(bytes);
        fos.close();

        return file;

    }

    private static File inputStream2File(InputStream inputStream) throws IOException {
        File file;
        byte[] buffer = new byte[1024];

        file = new File("sub.apk.tmp");
        FileOutputStream fos = new FileOutputStream(file);


        int len;
        int offset = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            fos.write(buffer, offset, len);
        }

        fos.close();
        inputStream.reset();
//        inputStream.close();

        return file;
    }


    public static HashMap<String, String> getCertificateInfos(InputStream inputStream) {
        Certificate[] certs;
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            final byte[] readBuffer = new byte[1024 * 8];
            final JarInputStream jis = new JarInputStream(inputStream, true);
            for (JarEntry entry = jis.getNextJarEntry(); entry != null; entry = jis.getNextJarEntry()) {
                if (entry.getName().startsWith("META-INF/")
                        && !entry.getName().endsWith(".MF")
                        && !entry.getName().endsWith(".SF")) {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    while (jis.read(readBuffer, 0, 1024 * 8) != -1) {
                        baos.write(readBuffer, 0, 1024 * 8);
                    }
                    PKCS7 pkcs7 = new PKCS7(baos.toByteArray());
                    certs = pkcs7.getCertificates();
                    for (Certificate cert : certs) {
                        hashMap.put(getCertMd5(cert), getCertSubject(cert));
                    }
                    baos.close();
                }
            }
        } catch (NullPointerException e) {
            return hashMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    /**
     * 获取一个 jar/zip 文件的证书信息
     *
     * @param bytes jar/zip 文件 bytes[]
     * @return hashMap,
     */
    public static HashMap<String, String> getCertificateInfos(byte[] bytes) {
        Certificate[] certs;
        HashMap<String, String> hashMap = new HashMap<>();

        try {
            certs = getCertificates(bytes);
            for (Certificate cert : certs) {
                hashMap.put(getCertMd5(cert), getCertSubject(cert));
            }
        } catch (NullPointerException e) {
            return hashMap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return hashMap;
    }

    /**
     * 获取证书的发行人信息
     *
     * @param cert Certificate
     * @return 证书的发行人信息 String
     */
    public static String getCertSubject(Certificate cert) {
        String subjectDN = null;
        try {
            final CertificateFactory cf = CertificateFactory.getInstance("X.509");
            final X509Certificate x509Certificate = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
            subjectDN = x509Certificate.getSubjectDN().getName();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
        return subjectDN;
    }

    /**
     * 从证书信息中获取所有详细信息
     *
     * @param cert Certificate
     * @return certDetail
     */
    public static String getCertDetail(Certificate cert) {
        String certDetail = null;
        try {
            final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            final X509Certificate x509Certificate = (X509Certificate) certificateFactory.generateCertificate(new ByteArrayInputStream(cert.getEncoded()));
            certDetail = x509Certificate.toString();
        } catch (final CertificateException e) {
            e.printStackTrace();
        }
        return certDetail;
    }


    /**
     * 从 ZIP 文件中获取证书
     *
     * @param file ZIP 文件
     * @return Certificate[]
     * @throws IOException
     */
    private static Certificate[] getCertificates(File file) throws IOException {
        Certificate[] certs = null;
        final byte[] readBuffer = new byte[1024 * 8];
        final JarFile jarFile = new JarFile(file);
        final Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
        while (jarEntryEnumeration.hasMoreElements()) {
            final JarEntry jarEntry = jarEntryEnumeration.nextElement();
            if (jarEntry.isDirectory())
                continue;
            if (jarEntry.getName().startsWith("META-INF/"))
                continue;

            final InputStream inputStream = jarFile.getInputStream(jarEntry);
            certs = loadCertificates(inputStream, jarEntry, readBuffer);
            if (certs != null)
                break;

            inputStream.close();
        }

        jarFile.close();

        return certs;
    }


    /**
     * @param bytes ZIP 文件
     * @return Certificate[]
     * @throws IOException
     */
    private static Certificate[] getCertificates(byte[] bytes) throws IOException {
        Certificate[] certs = null;
        final byte[] readBuffer = new byte[1024 * 8];
        final JarInputStream jis = new JarInputStream(new ByteArrayInputStream(bytes));
        for (JarEntry je = jis.getNextJarEntry(); je != null; je = jis.getNextJarEntry()) {
            if (je.isDirectory())
                continue;
            if (je.getName().startsWith("META-INF/"))
                continue;
            certs = loadCertificates(jis, je, readBuffer);
            if (certs != null) {
                break;
            }
        }

        jis.close();
        return certs;
    }

    /**
     * @param inputStream inputStream
     * @param jarEntry    entry
     * @param readBuffer  buffer
     * @return cert
     */
    private static Certificate[] loadCertificates(InputStream inputStream, JarEntry jarEntry, byte[] readBuffer) {
        try {
            // We must read the stream for the JarEntry to retrieve its certificates.
            while (inputStream.read(readBuffer, 0, readBuffer.length) != -1) {
            }
            return jarEntry.getCertificates();
        } catch (final IOException | RuntimeException e) {
            System.out.printf("Exception reading %s in %s:%s", jarEntry.getName(), jarEntry.getName(), e);
        }

        return null;
    }


}
