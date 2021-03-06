/*
 * 
 */
package com.accolite.miniau.accesscontrol.daoimpl;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;

import com.accolite.miniau.accesscontrol.dao.AdminDAO;
import com.accolite.miniau.accesscontrol.enums.UserType;
import com.accolite.miniau.accesscontrol.mapper.AdminMapper;
import com.accolite.miniau.accesscontrol.model.Admin;
import com.accolite.miniau.accesscontrol.utility.HashUtility;
import com.accolite.miniau.accesscontrol.utility.MailUtility;
import com.accolite.miniau.accesscontrol.utility.Query;
import com.accolite.miniau.accesscontrol.utility.UriUtility;

/**
 * The Class AdminDAOImpl.
 */
public class AdminDAOImpl implements AdminDAO {

	/** The Constant logger. */
	private static final Logger logger = Logger.getLogger(AdminDAOImpl.class);
	String uri;
	/** The mail util. */
	@Autowired
	private MailUtility mailUtil;

	/** The uri util. */
	@Autowired
	private UriUtility uriUtil;

	/** The jdbc template. */
	private JdbcTemplate jdbcTemplate;

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.accolite.miniau.accesscontrol.dao.AdminDAO#createAdmin(com.accolite.
	 * miniau.accesscontrol.model.Admin)
	 */
	@Override
	public boolean createAdmin(Admin admin) {

		try {
			jdbcTemplate.update(Query.CREATEADMIN, admin.getAdminName(), admin.getDescription(), admin.getMailId());
			int adminId = jdbcTemplate.queryForObject(Query.GETADMINID, new Object[] { admin.getMailId() },
					Integer.class);
			admin.setAdminId(adminId);
		} catch (DataAccessException e) {
			logger.info("couldn't insert admin" + admin.getAdminName());
			return false;
		}
		logger.info("inserted " + admin.getAdminName() + "into  admin successfully");
		return true;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.accolite.miniau.accesscontrol.dao.AdminDAO#deleteAdmin(int)
	 */
	@Override
	public boolean deleteAdmin(int adminId) {
		int rowsAffected = jdbcTemplate.update(Query.DELETEADMIN, adminId);
		if (rowsAffected == 0) {
			logger.error("couldn't delete" + adminId + " from delete table");
			return false;
		}
		logger.info("deleted " + adminId + "from admin table");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.accolite.miniau.accesscontrol.dao.AdminDAO#updatePassword(int,
	 * java.lang.String)
	 */
	@Override
	public boolean updatePassword(String uri, String password) {

		int adminId = getAdminIdFromURI(uri);
		try {
			jdbcTemplate.update(Query.CHANGEPASSKEY, password, adminId);
		} catch (DataAccessException e) {
			logger.error("couldn't update password");
			return false;
		}
		uriUtil.deleteURI(uri, UserType.ADMIN);
		logger.info("changed password");
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.accolite.miniau.accesscontrol.dao.AdminDAO#setDataSource(javax.sql.
	 * DataSource)
	 */
	@Override
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.accolite.miniau.accesscontrol.dao.AdminDAO#getAdminIdFromURI(java.lang.
	 * String)
	 */
	@Override
	public Integer getAdminIdFromURI(String uri) {
		Integer adminId;
		try {
			adminId = jdbcTemplate.queryForObject(Query.GETADMINIDFROMURI, new Object[] { uri }, Integer.class);
		} catch (Exception e) {
			logger.error("Exception ", e);
			adminId = 0;
		}
		return adminId;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.accolite.miniau.accesscontrol.dao.AdminDAO#sendPasswordLink(java.lang.
	 * String)
	 */
	@Override
	@Async
	public void sendPasswordLink(String email, String ip, int port) {
		Integer adminId = getAdminIdUsingEmail(email);
		
		String uri1 = HashUtility.createUniqueUriPath(adminId, email);
		
		uriUtil.createURI(adminId, uri1, UserType.ADMIN);
		
		String link = "http://" + ip + ":" + port+"/access-control-list-service/admin/updatePassword/" + uri1;
		mailUtil.sendEmailAsync(email, "Update Password",
				"Hi,\nPlease update your password using the below link\n" + link);
		this.uri = uri1;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.accolite.miniau.accesscontrol.dao.AdminDAO#getAdminIdUsingEmail(java.lang
	 * .String)
	 */
	@Override
	public Integer getAdminIdUsingEmail(String email) {
		Integer adminId;
		try {
			adminId = jdbcTemplate.queryForObject(Query.GETADMINIDUSINGEMAIL, new Object[] { email }, Integer.class);
		} catch (Exception e) {
			adminId = 0;
		}
		return adminId;
	}

	@Override
	public String getAdminName(int adminId) {
		String name;
		try {
			name = jdbcTemplate.queryForObject(Query.GETADMINNAME, new Object[] { adminId }, String.class);
		} catch (Exception e) {
			logger.error("Exception ", e);
			name = null;
		}
		return name;
	}

	@Override
	public Integer authenticate(String email, String pswd) {
		Integer adminId;
		try {
			adminId = jdbcTemplate.queryForObject(Query.AUTHENTICATIE, new Object[] { email, pswd }, Integer.class);
		} catch (Exception e) {
			adminId = null;
		}
		return adminId;
	}

	@Override
	public String getURI() {
		return uri;
	}

	@Override
	public List<Admin> getAllAdmins() {
		return jdbcTemplate.query(Query.GETALLADMINS, new AdminMapper());
	}

	@Override
	public boolean isAdmin(String email) {
		int count;
		try {
			count = jdbcTemplate.queryForObject(Query.ISADMIN, new Object[] { email }, Integer.class);
		} catch (Exception e) {
			count = 0;
		}
		return (count > 0);
	}

	@Override
	public void setDataSourceForURIUtil(DataSource dataSource) {
		uriUtil = new UriUtility();
		uriUtil.setDataSource(dataSource);
	}

}
