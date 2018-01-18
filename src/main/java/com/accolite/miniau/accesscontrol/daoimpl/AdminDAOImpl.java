package com.accolite.miniau.accesscontrol.daoimpl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

import com.accolite.miniau.accesscontrol.dao.AdminDAO;
import com.accolite.miniau.accesscontrol.model.Admin;
import com.accolite.miniau.accesscontrol.utility.Query;

public class AdminDAOImpl implements AdminDAO {

	private JdbcTemplate jdbcTemplate;
	private static final Logger logger = Logger.getLogger(com.accolite.miniau.accesscontrol.daoimpl.AdminDAOImpl.class);

	@Override
	public boolean createAdmin(Admin admin) {

		int rowsAffected = jdbcTemplate.update(Query.CREATEADMIN, admin.getAdminName(), admin.getDescription(),
				admin.getMailId());
		if (rowsAffected == 0) {
			logger.error("couldn't insert" + admin.getAdminId() + " into the admin table");
			return false;
		}
		logger.info("inserted " + admin.getAdminName() + "into  admin successfully");
		return true;

	}

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

	@Override
	public boolean updatePassword(int adminId, String password) {

		int rowsAffected = jdbcTemplate.update(Query.CHANGEPASSWORD, password, adminId);
		if (rowsAffected == 0) {
			logger.error("couldn't update password");
		}
		logger.info("changed password");
		return true;
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		jdbcTemplate = new JdbcTemplate(dataSource);

	}
}