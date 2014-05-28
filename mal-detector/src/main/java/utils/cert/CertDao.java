package utils.cert;

import java.security.cert.Certificate;

public interface CertDao {
    /**
     * 证书类型:白名单
     */
    public static final char CERT_TYPE_WHITE = 'w';
    /**
     * 证书类型:黑名单
     */
    public static final char CERT_TYPE_BLACK = 'b';
    /**
     * 证书类型:可疑名单
     */
    public static final char CERT_TYPE_SUSPICIOUS = 's';
    /**
     * 证书类型:未知
     */
    public static final char CERT_TYPE_UNKNOWN = 'u';

    int addCert(Certificate cert);

    char checkCertType(Certificate cert);
    char checkCertType(String certMd5);
}
