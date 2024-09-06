package praktikum;

import io.qameta.allure.Step;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import java.util.Objects;
import java.util.Random;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

public class OrderTestStep {

    static final String API_INGREDIENTS = "/api/ingredients";
    static final String API_ORDERS = "/api/orders";

    @Step("Check boolean response")
    public static void checkBooleanResponse(Response response, String json_field, boolean expected_value) {
        response.then().assertThat().body(json_field, equalTo(expected_value));
    }

    @Step("Check status code")
    public static void checkStatusCode(Response response, int status_code) {
        response.then().statusCode(status_code);
    }

    @Step("Get ingredients")
    public static String[] getIngredients() {
        Response response = given()
                .get(API_INGREDIENTS);
        JsonPath body = response.jsonPath();
        int bunsArraySize = 0;
        int mainsArraySize = 0;
        int saucesArraySize = 0;
        for (Ingredient ingredient : body.getObject("data", Ingredient[].class)) {
            if (Objects.equals(ingredient.getType(), "bun")) {
                bunsArraySize++;
            }
            if (Objects.equals(ingredient.getType(), "main")) {
                mainsArraySize++;
            }
            if (Objects.equals(ingredient.getType(), "sauce")) {
                saucesArraySize++;
            }
        }
        String[] bun = new String[bunsArraySize];
        String[] main = new String[mainsArraySize];
        String[] sauce = new String[saucesArraySize];
        int bunsCounter = 0;
        int mainsCounter = 0;
        int saucesCounter = 0;
        for (Ingredient ingredient : body.getObject("data", Ingredient[].class)) {
            if (Objects.equals(ingredient.getType(), "bun")) {
                bun[bunsCounter] = ingredient.get_id();
                bunsCounter++;
            }
            if (Objects.equals(ingredient.getType(), "main")) {
                main[mainsCounter] = ingredient.get_id();
                mainsCounter++;
            }
            if (Objects.equals(ingredient.getType(), "sauce")) {
                sauce[saucesCounter] = ingredient.get_id();
                saucesCounter++;
            }
        }
        Random random = new Random();
        return new String[]{bun[random.nextInt(bunsArraySize)], main[random.nextInt(mainsArraySize)], sauce[random.nextInt(saucesArraySize)]};
    }

    @Step("Make order")
    public static void makeOrder(String accessToken, String[] ingredients) {
        Order order = new Order(ingredients);
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(order)
                .post(API_ORDERS);
        checkBooleanResponse(response, "success", true);
        checkStatusCode(response, 200);
    }

    @Step("Make order without ingredients")
    public static void makeOrderWithoutIngredients(String accessToken, String[] ingredients) {
        Order order = new Order(ingredients);
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(order)
                .post(API_ORDERS);
        checkBooleanResponse(response, "success", false);
        checkStatusCode(response, 400);
    }

    @Step("Make order without authorization")
    public static void makeOrderWithoutAuthorization(String[] ingredients) {
        Order order = new Order(ingredients);
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post(API_ORDERS);
        checkBooleanResponse(response, "success", true);
        checkStatusCode(response, 200);
    }

    @Step("Make order with auth and invalid ingredients")
    public static void makeOrderWithInvalidIngredients(String accessToken, String[] ingredients) {
        Order order = new Order(ingredients);
        Response response = given()
                .header("Content-type", "application/json")
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .body(order)
                .post(API_ORDERS);
        checkStatusCode(response, 500);
    }

    @Step("Make order without auth and invalid ingredients")
    public static void makeOrderWithoutAuthAndInvalidIngredients(String[] ingredients) {
        Order order = new Order(ingredients);
        Response response = given()
                .header("Content-type", "application/json")
                .body(order)
                .post(API_ORDERS);
        checkStatusCode(response, 500);
    }

    @Step("Get orders of authorized user")
    public static void getOrdersofAuthorizedUser(String accessToken) {
        Response response = given()
                .auth().oauth2(accessToken.replace("Bearer ", ""))
                .get(API_ORDERS);
        checkBooleanResponse(response, "success", true);
        checkStatusCode(response, 200);
        JsonPath body = response.jsonPath();
        Assert.assertNotNull(body.get("orders.status"));
    }

    @Step("Get orders without authorization")
    public static void getOrderWithoutAuthorization() {
        Response response = given().get(API_ORDERS);
        checkBooleanResponse(response, "success", false);
        checkStatusCode(response, 401);
    }


}
