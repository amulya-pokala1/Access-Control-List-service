/*
 * 
 */
package com.accolite.miniau.accesscontrol.controllers;

import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.accolite.miniau.accesscontrol.customexception.CustomBadRequestException;
import com.accolite.miniau.accesscontrol.customexception.CustomNotFoundException;
import com.accolite.miniau.accesscontrol.customexception.CustomUnAuthorizedException;
import com.accolite.miniau.accesscontrol.dao.GroupDAO;
import com.accolite.miniau.accesscontrol.model.Group;
import com.accolite.miniau.accesscontrol.model.Permission;
import com.accolite.miniau.accesscontrol.model.User;
import com.accolite.miniau.accesscontrol.utility.StringLiteral;

/**
 * The Class GroupController.
 */
@RestController
public class GroupController {

	/** The group DAO. */
	@Autowired
	GroupDAO groupDAO;

	/**
	 * Creates the new group.
	 *
	 * @param group
	 *            the group
	 * @param bindingResult
	 *            the binding result
	 */
	@PostMapping(value = "/api/group")
	public void createNewGroup(@RequestBody @Valid Group group, BindingResult bindingResult, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		if (bindingResult.hasErrors()) {
			throw new CustomBadRequestException("Invalid Details!");
		}
		boolean isDone = groupDAO.addNewGroup(group);
		if (!isDone) {
			throw new CustomBadRequestException("Group already exist with same Group Name");
		}
	}

	/**
	 * Gets the all group names.
	 *
	 * @return the all group names
	 */
	@GetMapping(value = "/api/groupNames")
	public List<String> getAllGroupNames(HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		return groupDAO.getAllGroupNames();
	}

	/**
	 * Gets the all groups.
	 *
	 * @return the all groups
	 */
	@GetMapping(value = "/api/groups")
	public List<Group> getAllGroups(HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		return groupDAO.getAllGroups();
	}

	/**
	 * Adds the user to group.
	 *
	 * @param groupId
	 *            the group id
	 * @param userId
	 *            the user id
	 */
	@PutMapping(value = "/api/group/{groupId}/user/{userId}")
	public void addUserToGroup(@PathVariable int groupId, @PathVariable int userId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		boolean isDone = groupDAO.addUserToGroup(groupId, userId);
		if (!isDone) {
			throw new CustomBadRequestException("User Already exsist in Group");
		}
	}

	/**
	 * Delete user from group.
	 *
	 * @param groupId
	 *            the group id
	 * @param userId
	 *            the user id
	 */
	@DeleteMapping(value = "/api/group/{groupId}/user/{userId}")
	public void deleteUserFromGroup(@PathVariable int groupId, @PathVariable int userId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		boolean isDone = groupDAO.removeUserFromGroup(groupId, userId);
		if (!isDone) {
			throw new CustomNotFoundException("User already not in group!");
		}
	}

	/**
	 * Delete group.
	 *
	 * @param groupId
	 *            the group id
	 */
	@DeleteMapping(value = "/api/group/{groupId}")
	public void deleteGroup(@PathVariable int groupId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		boolean isDone = groupDAO.deleteGroup(groupId);
		if (!isDone) {
			throw new CustomNotFoundException("Cannot Delete! Group doesn't Exsist.");
		}
	}

	/**
	 * Gets the users from group.
	 *
	 * @param groupId
	 *            the group id
	 * @return the users from group
	 */
	@GetMapping(value = "/api/group/{groupId}/users")
	public List<User> getUsersFromGroup(@PathVariable int groupId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		return groupDAO.getUsersInGroup(groupId);
	}

	@GetMapping(value = "/api/group/{groupId}/usersExceptInGroup")
	public List<User> getUsersNotINGroup(@PathVariable int groupId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		return groupDAO.getUsersNotInGroup(groupId);
	}

	@PutMapping("/api/group/{groupId}/permission/{permissionId}")
	public void addPermissionToGroup(@PathVariable int groupId, @PathVariable int permissionId, HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		groupDAO.addPermission(groupId, permissionId);
	}

	@DeleteMapping("/api/group/{groupId}/permission/{permissionId}")
	public void removePermissionFromGroup(@PathVariable int groupId, @PathVariable int permissionId,
			HttpSession session) {
		if (session.getAttribute(StringLiteral.ADMIN_ID) == null)
			throw new CustomUnAuthorizedException(StringLiteral.PLEASE_LOGIN);
		groupDAO.removePermission(groupId, permissionId);
	}

	@GetMapping("/api/group/{groupId}/permission")
	public List<Permission> getPermissionInGroup(@PathVariable int groupId, HttpSession session) {
		if (session.getAttribute("adminId") == null)
			throw new CustomUnAuthorizedException("Please login to perform this task!");
		return groupDAO.getGroupPermissions(groupId);
	}

	@GetMapping("/api/group/{groupId}/exceptPermission")
	public List<Permission> getPermissionExceptInGroup(@PathVariable int groupId, HttpSession session) {
		if (session.getAttribute("adminId") == null)
			throw new CustomUnAuthorizedException("Please login to perform this task!");
		return groupDAO.getPermissionNotInGroup(groupId);
	}
}
