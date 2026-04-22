package com.project2.exception;



import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * GlobalExceptionHandler - Catches exceptions thrown anywhere in the application
 * and shows user-friendly error pages instead of a raw stack trace.
 *
 * @ControllerAdvice applies this handler to all @Controller classes.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle 404 - Resource Not Found.
     * e.g. when someone accesses /events/9999 and that event doesn't exist.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorCode", "404");
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error"; // → templates/error.html
    }

    /**
     * Handle Duplicate Registration.
     */
    @ExceptionHandler(DuplicateRegistrationException.class)
    public String handleDuplicateRegistration(DuplicateRegistrationException ex, Model model) {
        model.addAttribute("errorCode", "409");
        model.addAttribute("errorTitle", "Already Registered");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    /**
     * Handle all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorCode", "500");
        model.addAttribute("errorTitle", "Something Went Wrong");
        model.addAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
        return "error";
    }
}
