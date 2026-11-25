package com.amalw.model;

import java.util.UUID;

/** Builds User objects for tests. */
public final class UserFactory {

	private static final String EMAIL_DOMAIN = "@example.com";

	private UserFactory() {
	}

	/** Creates a user with a random email, because the site rejects duplicate emails. */
	public static User buildUser(String firstName, String lastName, String company, String password,
			String confirmPassword) {

		User user = new User();

		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(generateUniqueEmail());
		user.setCompany(company);
		user.setPassword(password);
		user.setConfirmPassword(confirmPassword);

		return user;
	}

	private static String generateUniqueEmail() {
		return UUID.randomUUID() + EMAIL_DOMAIN;
	}
}
