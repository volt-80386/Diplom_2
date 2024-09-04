package praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

public class UserTest {

    static final String URL = "https://stellarburgers.nomoreparties.site";

    @Before
    public void init() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Register new user and check response and access token")
    public void testRegisterNewUser() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        UserTestStep.deleteUser("testemail@testdomain.local", "12345678");
    }

    @Test
    @DisplayName("Register new user with same credentials and check response")
    public void testRegisterNewUserWithSameCredentials() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response second_register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(second_register, "success", false);
        UserTestStep.checkStatusCode(second_register, 403);
        UserTestStep.deleteUser("testemail@testdomain.local", "12345678");
    }

    @Test
    @DisplayName("Register new user without required field")
    public void testRegisterWithoutRequiredField() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "");
        UserTestStep.checkBooleanResponse(register, "success", false);
        UserTestStep.checkStatusCode(register, 403);
    }

    @Test
    @DisplayName("Login user with right credentials")
    public void testLoginUser() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testemail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", true);
        UserTestStep.checkStatusCode(login, 200);
        UserTestStep.deleteUser("testemail@testdomain.local", "12345678");
    }

    @Test
    @DisplayName("Login user with error credentials")
    public void testLoginErrorUser() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testbademail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", false);
        UserTestStep.checkStatusCode(login, 401);
        UserTestStep.deleteUser("testemail@testdomain.local", "12345678");
    }

    @Test
    @DisplayName("Change user credentials with authorization")
    public void testChangeUserCredentials() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        UserTestStep.changeEmail("newtestemail@testdomain.local");
        UserTestStep.changePassword("87654321");
        UserTestStep.changeName("Han Solo");
        UserTestStep.deleteUser("newtestemail@testdomain.local", "87654321");
    }

    @Test
    @DisplayName("Change user credentials without authorization")
    public void testChangeUserCredentialsWithoutAuthorization() {
        UserTestStep.changeEmailWithoutAuth("newtestemail@testdomain.local");
        UserTestStep.changePasswordWithoutAuth("87654321");
        UserTestStep.changeNameWithoutAuth("Han Solo");
    }

}
