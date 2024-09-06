package praktikum;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import io.qameta.allure.junit4.DisplayName;

public class OrderTest {

    static final String URL = "https://stellarburgers.nomoreparties.site";

    @Before
    public void init() {
        RestAssured.baseURI = URL;
    }

    @Test
    @DisplayName("Make order with authorization and valid ingredients")
    public void testMakeOrderWithAuthAndValidIngredients() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testemail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", true);
        UserTestStep.checkStatusCode(login, 200);
        OrderTestStep.makeOrder(UserTestStep.getAccessToken(login),
                new String[] {OrderTestStep.getIngredients()[0],
                        OrderTestStep.getIngredients()[1],
                        OrderTestStep.getIngredients()[2]});
    }

    @Test
    @DisplayName("Make order with authorization and without ingredients")
    public void testMakeOrderWithAuthAndWithoutIngredients() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testemail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", true);
        UserTestStep.checkStatusCode(login, 200);
        OrderTestStep.makeOrderWithoutIngredients(UserTestStep.getAccessToken(login), new String[] {});
    }

    @Test
    @DisplayName("Make order without authorization")
    public void testMakeOrderWithoutAuth() {
        UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        OrderTestStep.makeOrderWithoutAuthorization(new String[] {OrderTestStep.getIngredients()[0],
                        OrderTestStep.getIngredients()[1],
                        OrderTestStep.getIngredients()[2]});
    }

    @Test
    @DisplayName("Make order with authorization and invalid ingredients")
    public void testMakeOrderWithAuthAndInvalidIngredients() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testemail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", true);
        UserTestStep.checkStatusCode(login, 200);
        OrderTestStep.makeOrderWithInvalidIngredients(UserTestStep.getAccessToken(login), new String[] {"9ac0c5a71d17", "8bc0c5a71d1f8", "1bc0c5a71d1f820"});
    }

    @Test
    @DisplayName("Make order without authorization and invalid ingredients")
    public void testMakeOrderWithoutAuthAndInvalidIngredients() {
        UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        OrderTestStep.makeOrderWithoutAuthAndInvalidIngredients(new String[] {"9ac0c5a71d1782001", "8bc0c5a71d1f82001", "d1f82001bdafa6d"});
    }

    @Test
    @DisplayName("Get orders of authorized user")
    public void testGetOrdersOfAuthorizedUser() {
        Response register = UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        UserTestStep.checkBooleanResponse(register, "success", true);
        UserTestStep.checkStatusCode(register, 200);
        UserTestStep.checkAccessToken(register, "accessToken");
        Response login = UserTestStep.loginUser("testemail@testdomain.local", "12345678");
        UserTestStep.checkBooleanResponse(login, "success", true);
        UserTestStep.checkStatusCode(login, 200);
        OrderTestStep.makeOrder(UserTestStep.getAccessToken(login),
                new String[] {OrderTestStep.getIngredients()[0],
                        OrderTestStep.getIngredients()[1],
                        OrderTestStep.getIngredients()[2]});
        OrderTestStep.makeOrder(UserTestStep.getAccessToken(login),
                new String[] {OrderTestStep.getIngredients()[0],
                        OrderTestStep.getIngredients()[1],
                        OrderTestStep.getIngredients()[2]});
        OrderTestStep.makeOrder(UserTestStep.getAccessToken(login),
                new String[] {OrderTestStep.getIngredients()[0],
                        OrderTestStep.getIngredients()[1],
                        OrderTestStep.getIngredients()[2]});
        OrderTestStep.getOrdersofAuthorizedUser(UserTestStep.getAccessToken(login));
    }

    @Test
    @DisplayName("Get orders without authorization")
    public void testGetOrdersWithoutAuth() {
        UserTestStep.registerUser("testemail@testdomain.local", "12345678", "Chubaka");
        OrderTestStep.getOrderWithoutAuthorization();
    }

    @After
    public void deleteUser() {
        UserTestStep.deleteUser("testemail@testdomain.local", "12345678");
    }

}
