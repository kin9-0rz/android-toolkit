package utils.cert;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import parser.utils.CertTool;

import javax.sql.DataSource;
import java.security.cert.Certificate;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CertDaoDbImpl implements CertDao {
    JdbcTemplate jdbcTemplate;
    LobHandler lobHandler = new DefaultLobHandler();

    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public int addCert(final Certificate cert) {
//        final String md5 = Util.getCertInfo(cert);
        final String md5 = CertTool.getCertMd5(cert);
        final String baseinfo = CertTool.getCertSubject(cert);
        final byte[] raw;
        try {
            raw = cert.getEncoded();
        } catch (final Exception e) {
            return -1;
        }

        final int r = jdbcTemplate.execute("insert into cert_info(md5, baseinfo, rawdata, type) values (?, ?, ?, ?)",
                new AbstractLobCreatingPreparedStatementCallback(lobHandler) {
                    @Override
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException,
                            DataAccessException {
                        ps.setString(0, md5);
                        ps.setString(1, baseinfo);
                        lobCreator.setBlobAsBytes(ps, 2, raw);
                        ps.setString(3, String.valueOf(CERT_TYPE_UNKNOWN));//unknown
                    }
                });

        return r;
    }

    @Override
    public char checkCertType(Certificate cert) {
//        final String md5 = Util.getCertInfo(cert);
        final String md5 = CertTool.getCertMd5(cert);
        final String r = jdbcTemplate.queryForObject("select type from cert_info where md5=?", new Object[]{md5}, String.class);
        return r == null ? CERT_TYPE_UNKNOWN : r.charAt(0);
    }

    @Override
    public char checkCertType(String certMd5) {
        return 0;
    }
}
