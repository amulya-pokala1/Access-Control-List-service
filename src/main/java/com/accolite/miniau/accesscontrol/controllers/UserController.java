package com.accolite.miniau.accesscontrol.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.accolite.miniau.accesscontrol.customexception.CustomBadRequestException;
import com.accolite.miniau.accesscontrol.customexception.CustomNotFoundException;
import com.accolite.miniau.accesscontrol.dao.UserDAO;
import com.accolite.miniau.accesscontrol.model.User;

@RestController
public class UserController {

	@Autowired
	UserDAO userDAO;

	@PostMapping(value = "/api/user")
	public void addUser(@RequestBody User user) {
		boolean isDone = userDAO.addNewUser(user);
		if (!isDone) {
			throw new CustomBadRequestException("User Already exsist!");
		}
		userDAO.sendPasswordLink(user.getMailId());
	}

	@GetMapping(value = "/api/user/{userId}")
	public User getUserDetails(@PathVariable int userId) {
		return userDAO.getUser(userId);
	}

	@DeleteMapping(value = "/api/user/{userId}")
	public void deleteUser(@PathVariable int userId) {
		boolean isDone = userDAO.deleteUser(userId);
		if (!isDone) {
			throw new CustomNotFoundException("Cannot Delete User! User Does not exsist!");
		}
	}

	@GetMapping(value = "/api/userList")
	public List<User> getAllUsers() {
		return userDAO.getAllUsers();
	}

	@PostMapping(value = "/api/user/updatePassword/{uri}/")
	public void updatePassword(@PathVariable String uri, @RequestBody User user) {
		
		int adminId = userDAO.getUserIdFromURI(uri);
		boolean isDone = userDAO.updatePassword(adminId, user.getPassword());
		if(!isDone) {
			
		}
		//delete the uri 
	}
}
