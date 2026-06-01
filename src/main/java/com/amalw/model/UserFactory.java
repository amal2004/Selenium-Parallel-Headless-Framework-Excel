package com.amalw.model;

import java.util.UUID;

public class UserFactory {

	    private UserFactory() {
	    }

	    public static User buildUser(
	            String firstName,
	            String lastName,
	            String company,
	            String password,
	            String confirmPassword) {

	        User user = new User();

	        user.setFirstName(firstName);
	        user.setLastName(lastName);
	        user.setEmail(generateEmail());
	        user.setCompany(company);
	        user.setPassword(password);
	        user.setConfirmPassword(confirmPassword);

	        return user;
	    }

	    private static String generateEmail() {

	        return UUID.randomUUID() + "@example.com";
	    }
}
