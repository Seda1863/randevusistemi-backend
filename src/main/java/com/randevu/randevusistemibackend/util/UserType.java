package com.randevu.randevusistemibackend.util;

/**
 * Enum representing the types of users in the system.
 * Used for JWT claims to identify user roles.
 */
public enum UserType {
    USER,
    PROVIDER,
    ADMIN
}
