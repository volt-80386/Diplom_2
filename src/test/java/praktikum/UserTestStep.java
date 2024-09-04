package praktikum;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class UserTestStep {

    static final String API_REGISTER_USER = "/api/auth/register";
    static final String API_LOGIN_USER = "/api/auth/login";
    static final String API_AUTH_USER = "/api/auth/user";

    @Step("POST credentials to register user")
    public static Response registerUser(String email, String password, String name) {
        User user = new User(email, password, name);
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_REGISTER_USER);
    }

    @Step("Check boolean response")
    public static void checkBooleanResponse(Response response, String json_field, boolean expected_value) {
        response.then().assertThat().body(json_field, equalTo(expected_value));
    }

    @Step("Check status code")
    public static void checkStatusCode(Response response, int status_code) {
        response.then().statusCode(status_code);
    }

    @Step("Check accessToken")
    public static void checkAccessToken(Response response, String json_field) {
        response.then().assertThat().body(json_field, notNullValue());
    }

    @Step("Get accessToken")
    public static String getAccessToken(Response response) {
        JsonPath body = response.jsonPath();
        return body.get("accessToken");
    }

    @Step("POST login user")
    public static Response loginUser(String email, String password) {
        User user = new User(email, password, "");
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post(API_LOGIN_USER);
    }

    @Step("Change email with auth")
    public static void changeEmail(String email) {
        Response response = loginUser("testemail@testdomain.local", "12345678");
        JsonPath body = response.jsonPath();
        String accessToken = body.get("accessToken");
        User user = new User(email, null, null);
        Response change = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", true);
        checkStatusCode(change, 200);
    }

    @Step("Change password with auth")
    public static void changePassword(String password) {
        Response response = loginUser("newtestemail@testdomain.local", "12345678");
        JsonPath body = response.jsonPath();
        String accessToken = body.get("accessToken");
        User user = new User(null, password, null);
        Response change = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", true);
        checkStatusCode(change, 200);
    }

    @Step("Change name with auth")
    public static void changeName(String name) {
        Response response = loginUser("newtestemail@testdomain.local", "87654321");
        JsonPath body = response.jsonPath();
        String accessToken = body.get("accessToken");
        User user = new User(null, null, name);
        Response change = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", true);
        checkStatusCode(change, 200);
    }

    @Step("Change email without auth")
    public static void changeEmailWithoutAuth(String email) {
        User user = new User(email, null, null);
        Response change = given()
                .header("Content-type", "application/json")
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", false);
        checkStatusCode(change, 401);
    }

    @Step("Change password without auth")
    public static void changePasswordWithoutAuth(String password) {
        User user = new User(null, password, null);
        Response change = given()
                .header("Content-type", "application/json")
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", false);
        checkStatusCode(change, 401);
    }

    @Step("Change name without auth")
    public static void changeNameWithoutAuth(String name) {
        User user = new User(null, null, name);
        Response change = given()
                .header("Content-type", "application/json")
                .body(user)
                .patch(API_AUTH_USER);
        checkBooleanResponse(change, "success", false);
        checkStatusCode(change, 401);
    }

    @Step("DELETE user")
    public static void deleteUser(String email, String password) {
        Response response = loginUser(email, password);
        JsonPath body = response.jsonPath();
        String accessToken = body.get("accessToken");
        given()
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .delete(API_AUTH_USER)
                .then().statusCode(202);
    }

}
