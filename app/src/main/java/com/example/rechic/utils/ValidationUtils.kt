package com.example.rechic.utils

object ValidationUtils {

    /**
     * Validates an email address.
     * @param email The email string to validate.
     * @return True if the email is valid, false otherwise.
     */
    fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
        return email.matches(emailPattern.toRegex())
    }

    /**
     * Validates a phone number.
     * @param phone The phone number string to validate.
     *        Must be a 10-digit number starting with 0.
     * @return True if the phone number is valid, false otherwise.
     */
    fun isValidPhoneNumber(phone: String): Boolean {
        val phonePattern = "^0[0-9]{9}$"
        return phone.matches(phonePattern.toRegex())
    }

    fun areFieldsEmpty(vararg fields: String): Boolean {
        return fields.any { it.isEmpty() }
    }
}
