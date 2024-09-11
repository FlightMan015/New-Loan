package de.joonko.loan.user;

public class UserDataNotFoundException extends RuntimeException {

    public UserDataNotFoundException(String message) {
        super(message);
    }
}
