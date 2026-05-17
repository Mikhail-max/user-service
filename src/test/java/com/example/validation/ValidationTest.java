package com.example.validation;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

class ValidationTest {

    @Test
    public void testValidateNameNull(){
        Assertions.assertThrows(IllegalArgumentException.class,() ->
                Validation.validateName(null));

    }

   @Test
    public void testValidateNameValid(){
        Assertions.assertDoesNotThrow(() -> Validation.validateName("Ванюшечка"));
   }

   @Test
    public void testValidateNameOnlySpaces(){
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateName("   "));
   }

   @Test
   public void testValidateNameEmpty(){
        Assertions.assertThrows(IllegalArgumentException.class, ()->
                Validation.validateName(""));
   }

   @Test
    public void testValidateEmailValid(){
        Assertions.assertDoesNotThrow(() ->
                Validation.validateEmail("asdqweqe@example.com"));
   }

   @Test
    public void testValidateEmailNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateEmail(null));

   }
    @Test
    void testValidateEmailEmpty() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateEmail(""));
    }

    @Test
    void testValidateEmailInvalidFormat() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateEmail("mail-user"));
    }

   @Test
   public void testValidationAgeValid(){
        Assertions.assertDoesNotThrow(() ->
                Validation.validateAge(20));
   }

    @Test
    void testValidateAgeTooHigh() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateAge(152));
    }

    @Test
    void testValidateAgeNegative() {
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateAge(-1));
    }

   @Test
    public void testValidateAgeNull(){
        Assertions.assertThrows(IllegalArgumentException.class, () ->
                Validation.validateAge(null));
   }

    @Test
    void testValidateAgeMinimum() {
        Assertions.assertDoesNotThrow(() -> Validation.validateAge(0));
    }

    @Test
    void testValidateAgeMaximum() {
        Assertions.assertDoesNotThrow(() -> Validation.validateAge(150));
    }



}
