package org.example;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import static io.restassured.RestAssured.given;
import static io.restassured.path.json.JsonPath.given;
import static javax.swing.text.DefaultStyledDocument.ElementSpec.ContentType;

public class APITest {

    // Inisialisasi ExtentReports untuk pelaporan
    ExtentReports extent;
    ExtentTest test;

    @BeforeSuite
    public void setUp() {
        // Mengatur reporter untuk ExtentReports
        ExtentSparkReporter sparkReporter = new ExtentSparkReporter("extentReport.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        // Mengatur base URI
        RestAssured.baseURI = "https://reqres.in";
    }

    @AfterSuite
    public void tearDown() {
        // Generate laporan setelah semua pengujian selesai
        extent.flush();
    }

    // A. Buat pengguna baru: POST /public-api/users
    @Test
    public void createUser_Positive() {
        test = extent.createTest("Create User Test - Positive", "Test to create a new user");

        String requestBody = "{ \"name\": \"John\", \"job\": \"leader\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(201)  // Sukses: Status code 201 Created
                .extract().response();

        // Validasi hasil
        Assert.assertEquals(response.jsonPath().getString("name"), "John");
        Assert.assertEquals(response.jsonPath().getString("job"), "leader");

        // Log hasil ke laporan
        test.pass("User created successfully with name: John and job: leader");
    }

    @Test
    public void createUser_Negative() {
        test = extent.createTest("Create User Test - Negative", "Test to create a new user with invalid data");

        String requestBody = "{ \"name\": \"\", \"job\": \"\" }";  // Invalid data

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .post("/api/users")
                .then()
                .statusCode(400)  // Error: Status code 400 Bad Request
                .extract().response();

        Assert.assertEquals(response.statusCode(), 400);

        test.pass("Negative case passed, invalid user data returned 400 Bad Request");
    }

    // B. Dapatkan detail pengguna: GET /public-api/users/xxx
    @Test
    public void getUser_Positive() {
        test = extent.createTest("Get User Test - Positive", "Test to get an existing user's details");

        Response response = given()
                .when()
                .get("/api/users/2")
                .then()
                .statusCode(200)  // Sukses: Status code 200 OK
                .extract().response();

        String userId = response.jsonPath().getString("data.id");
        Assert.assertEquals(userId, "2");

        test.pass("Successfully retrieved user details for user with ID: 2");
    }

    @Test
    public void getUser_Negative() {
        test = extent.createTest("Get User Test - Negative", "Test to get a non-existing user's details");

        Response response = given()
                .when()
                .get("/api/users/99999")  // Pengguna tidak ada
                .then()
                .statusCode(404)  // Error: Status code 404 Not Found
                .extract().response();

        Assert.assertEquals(response.statusCode(), 404);

        test.pass("Negative case passed, user not found returned 404 Not Found");
    }

    // C. Perbarui detail pengguna: PUT /public-api/users/xxx
    @Test
    public void updateUser_Positive() {
        test = extent.createTest("Update User Test - Positive", "Test to update an existing user's details");

        String requestBody = "{ \"name\": \"John\", \"job\": \"manager\" }";

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(200)  // Sukses: Status code 200 OK
                .extract().response();

        Assert.assertEquals(response.jsonPath().getString("job"), "manager");

        test.pass("User updated successfully to job: manager");
    }

    @Test
    public void updateUser_Negative() {
        test = extent.createTest("Update User Test - Negative", "Test to update a user with invalid data");

        String requestBody = "{ \"name\": \"\", \"job\": \"\" }";  // Invalid data

        Response response = given()
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when()
                .put("/api/users/2")
                .then()
                .statusCode(400)  // Error: Status code 400 Bad Request
                .extract().response();

        Assert.assertEquals(response.statusCode(), 400);

        test.pass("Negative case passed, invalid update data returned 400 Bad Request");
    }

    // D. Hapus pengguna: DELETE /public-api/users/xxx
    @Test
    public void deleteUser_Positive() {
        test = extent.createTest("Delete User Test - Positive", "Test to delete an existing user");

        Response response = given()
                .when()
                .delete("/api/users/2")
                .then()
                .statusCode(204)  // Sukses: Status code 204 No Content
                .extract().response();

        Assert.assertEquals(response.statusCode(), 204);

        test.pass("User deleted successfully, returned 204 No Content");
    }

    @Test
    public void deleteUser_Negative() {
        test = extent.createTest("Delete User Test - Negative", "Test to delete a non-existing user");

        Response response = given()
                .when()
                .delete("/api/users/99999")  // Pengguna tidak ada
                .then()
                .statusCode(404)  // Error: Status code 404 Not Found
                .extract().response();

        Assert.assertEquals(response.statusCode(), 404);

        test.pass("Negative case passed, user not found returned 404 Not Found");
    }
}



