package com.accolite.miniau.accesscontrol.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.accolite.miniau.accesscontrol.dao.GroupDAO;
import com.accolite.miniau.accesscontrol.dao.PermissionDAO;
import com.accolite.miniau.accesscontrol.dao.UserDAO;
import com.accolite.miniau.accesscontrol.daoimpl.GroupDAOImpl;
import com.accolite.miniau.accesscontrol.daoimpl.PermissionDAOImpl;
import com.accolite.miniau.accesscontrol.daoimpl.UserDAOImpl;
import com.accolite.miniau.accesscontrol.model.Group;
import com.accolite.miniau.accesscontrol.model.Permission;
import com.accolite.miniau.accesscontrol.model.User;

public class GroupDAOTest {
	static ApplicationContext context;
	static DataSource dataSource;
	static GroupDAO groupdao;
	static PermissionDAO permissiondao;
	User user;
	Permission permission;
	Group group;
	static UserDAO userdao;
	static final String GROUPNAME = "group1";

	@BeforeClass
	public static void setUp() {
		context = new FileSystemXmlApplicationContext(
				"C:\\Users\\Hyderabad-Intern\\eclipse-workspace\\Access-Control-List-service\\src\\main\\webapp\\WEB-INF\\springDispatcherServlet-servlet.xml");
		dataSource = (DataSource) context.getBean("dataSource");
		groupdao = new GroupDAOImpl();
		userdao = new UserDAOImpl();
		permissiondao = new PermissionDAOImpl();
		groupdao.setDataSource(dataSource);
		permissiondao.setDataSource(dataSource);
		userdao.setDataSource(dataSource);

	}

	@Test
	public void testAddNewGroup() {
		group = new Group(GROUPNAME, "test");
		boolean result = groupdao.addNewGroup(group);
		assertTrue(result);
		groupdao.deleteGroup(group.getGroupId());
	}

	@Test
	public void testAddNewGroupError() {
		group = new Group(null, "test");
		boolean result = groupdao.addNewGroup(group);
		assertFalse(result);
		groupdao.deleteGroup(group.getGroupId());
	}

	@Test
	public void testGetAllGroupNames() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		List<String> groupNames = groupdao.getAllGroupNames();
		assertTrue(groupNames.contains(GROUPNAME));
		groupdao.deleteGroup(group.getGroupId());

	}

	@Test
	public void testGetAllGroups() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		List<Group> groups = groupdao.getAllGroups();
		int count = 1;
		for (Group groupIter : groups) {
			if (groupIter.getGroupName().equals(group.getGroupName())) {
				count--;
			}
		}
		assertEquals(0, count);
		groupdao.deleteGroup(group.getGroupId());
	}

	@Test
	public void testaddUserToGroup() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		user = new User("test", "test");
		userdao.addNewUser(user);
		boolean result = groupdao.addUserToGroup(group.getGroupId(), user.getUserId());
		groupdao.deleteGroup(group.getGroupId());
		userdao.deleteUser(user.getUserId());
		assertTrue(result);
	}

	@Test
	public void testaddUserToGroupError() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		user = new User("test", "test");
		userdao.addNewUser(user);
		boolean result = groupdao.addUserToGroup(group.getGroupId(), user.getUserId() + 1);
		groupdao.deleteGroup(group.getGroupId());
		userdao.deleteUser(user.getUserId());
		assertFalse(result);
	}

	@Test
	public void testGetUsersFromGroup() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		user = new User("test", "test");
		userdao.addNewUser(user);
		int count = 1;
		groupdao.addUserToGroup(group.getGroupId(), user.getUserId());
		List<User> users = groupdao.getUsersInGroup(group.getGroupId());
		for (User userIter : users) {
			if (userIter.getMailId().equals(user.getMailId())) {
				count--;
			}
		}
		groupdao.deleteGroup(group.getGroupId());
		userdao.deleteUser(user.getUserId());
		assertEquals(0, count);

	}

	@Test
	public void testRemoveUserFromGroup() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		user = new User("test", "test");
		userdao.addNewUser(user);
		groupdao.addUserToGroup(group.getGroupId(), user.getUserId());
		boolean result = groupdao.removeUserFromGroup(group.getGroupId(), user.getUserId());
		groupdao.deleteGroup(group.getGroupId());
		userdao.deleteUser(user.getUserId());
		assertTrue(result);
	}

	@Test
	public void testRemoveUserFromGroupError() {
		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		user = new User("test", "test");
		userdao.addNewUser(user);
		groupdao.addUserToGroup(group.getGroupId(), user.getUserId());
		boolean result = groupdao.removeUserFromGroup(group.getGroupId(), user.getUserId() + 1);
		groupdao.deleteGroup(group.getGroupId());
		userdao.deleteUser(user.getUserId());
		assertFalse(result);
	}

	@Test
	public void testAddPermission() {
		group = new Group(GROUPNAME, "test");
		permission = new Permission("test", "test", false);
		groupdao.addNewGroup(group);
		permissiondao.createPermission(permission);
		boolean result = groupdao.addPermission(group.getGroupId(), permission.getPermissionId());
		permissiondao.deletePermission(permission.getPermissionId());
		groupdao.deleteGroup(group.getGroupId());
		assertTrue(result);
	}

	@Test
	public void testAddPermissionerror() {
		group = new Group(GROUPNAME, "test");
		permission = new Permission("test", "test", false);
		groupdao.addNewGroup(group);
		permissiondao.createPermission(permission);
		boolean result = groupdao.addPermission(group.getGroupId(), permission.getPermissionId() + 1);

		permissiondao.deletePermission(permission.getPermissionId());
		groupdao.deleteGroup(group.getGroupId());
		assertFalse(result);
	}

	@Test
	public void testRemovePermission() {
		group = new Group(GROUPNAME, "test");
		permission = new Permission("test", "test", false);
		groupdao.addNewGroup(group);
		permissiondao.createPermission(permission);
		groupdao.addPermission(group.getGroupId(), permission.getPermissionId());
		boolean result = groupdao.removePermission(group.getGroupId(), permission.getPermissionId());
		permissiondao.deletePermission(permission.getPermissionId());
		groupdao.deleteGroup(group.getGroupId());
		assertTrue(result);
	}

	@Test
	public void testRemovePermissionError() {
		group = new Group(GROUPNAME, "test");
		permission = new Permission("test", "test", false);
		groupdao.addNewGroup(group);
		permissiondao.createPermission(permission);
		groupdao.addPermission(group.getGroupId(), permission.getPermissionId());
		permissiondao.deletePermission(permission.getPermissionId());
		groupdao.deleteGroup(group.getGroupId());
		boolean result = groupdao.removePermission(group.getGroupId(), permission.getPermissionId() + 1);
		assertFalse(result);
	}

	@Test
	public void testDeleteGroup() {

		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		boolean result = groupdao.deleteGroup(group.getGroupId());
		assertTrue(result);
		groupdao.deleteGroup(group.getGroupId());
	}

	@Test
	public void testDeleteGroupError() {

		group = new Group(GROUPNAME, "test");
		groupdao.addNewGroup(group);
		boolean result = groupdao.deleteGroup(group.getGroupId() + 1);
		assertFalse(result);
		groupdao.deleteGroup(group.getGroupId());
	}

}