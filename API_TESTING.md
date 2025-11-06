# API Testing Guide

## Authentication API Endpoints

Base URL: `http://localhost:8080/api/auth`

---

## 1. Signup (Register New User)

**Endpoint:** `POST /api/auth/signup`

**Request Body:**
```json
{
  "firstName": "John",
  "lastName": "Doe",
  "email": "john.doe@example.com",
  "username": "johndoe",
  "password": "password123",
  "role": "Developer"
}
```

**Success Response (201 Created):**
```json
{
  "success": true,
  "message": "Account created successfully",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "role": "Developer",
    "active": true,
    "createdAt": "2025-11-04T10:30:00"
  }
}
```

**Error Response (400 Bad Request):**
```json
{
  "success": false,
  "message": "Username already exists!",
  "user": null
}
```

### cURL Command:
```bash
curl -X POST http://localhost:8080/api/auth/signup \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "password": "password123",
    "role": "Developer"
  }'
```

---

## 2. Login (Authenticate User)

**Endpoint:** `POST /api/auth/login`

**Request Body:**
```json
{
  "username": "johndoe",
  "password": "password123"
}
```

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Login successful",
  "user": {
    "id": 1,
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "username": "johndoe",
    "role": "Developer",
    "active": true,
    "createdAt": "2025-11-04T10:30:00"
  }
}
```

**Error Response (401 Unauthorized):**
```json
{
  "success": false,
  "message": "Invalid username or password",
  "user": null
}
```

### cURL Command:
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

---

## 3. Check Username Availability

**Endpoint:** `GET /api/auth/check-username/{username}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Username available",
  "user": null
}
```

**Username Taken Response (200 OK):**
```json
{
  "success": false,
  "message": "Username already taken",
  "user": null
}
```

### cURL Command:
```bash
curl -X GET http://localhost:8080/api/auth/check-username/johndoe
```

---

## 4. Check Email Availability

**Endpoint:** `GET /api/auth/check-email/{email}`

**Success Response (200 OK):**
```json
{
  "success": true,
  "message": "Email available",
  "user": null
}
```

**Email Taken Response (200 OK):**
```json
{
  "success": false,
  "message": "Email already registered",
  "user": null
}
```

### cURL Command:
```bash
curl -X GET http://localhost:8080/api/auth/check-email/john.doe@example.com
```

---

## Testing with Postman

### Setup:
1. Open Postman
2. Create a new collection named "Agile Requirements API"
3. Set base URL as variable: `{{baseUrl}}` = `http://localhost:8080/api/auth`

### Test Sequence:

1. **Test Signup**
   - Method: POST
   - URL: `{{baseUrl}}/signup`
   - Body: Raw JSON (see above)
   - Expected: 201 Created

2. **Test Login**
   - Method: POST
   - URL: `{{baseUrl}}/login`
   - Body: Raw JSON (see above)
   - Expected: 200 OK

3. **Test Duplicate Signup**
   - Same as Test 1
   - Expected: 400 Bad Request with "Username already exists!"

4. **Test Invalid Login**
   - Method: POST
   - URL: `{{baseUrl}}/login`
   - Body: Wrong password
   - Expected: 401 Unauthorized

5. **Test Username Check**
   - Method: GET
   - URL: `{{baseUrl}}/check-username/johndoe`
   - Expected: 200 OK with "Username already taken"

6. **Test Email Check**
   - Method: GET
   - URL: `{{baseUrl}}/check-email/john.doe@example.com`
   - Expected: 200 OK with "Email already registered"

---

## Testing with JavaFX Application

### Prerequisites:
1. Start Spring Boot backend:
   ```bash
   mvn spring-boot:run
   ```
   
2. In another terminal, start JavaFX frontend:
   ```bash
   mvn javafx:run
   ```

### Test Flow:

1. **Signup Test:**
   - Fill out signup form
   - Click "Sign Up"
   - Should see success message
   - Should auto-redirect to login page after 2 seconds

2. **Login Test:**
   - Enter username and password
   - Click "Login"
   - Should see welcome message with first name

3. **Error Handling Test:**
   - Try signing up with existing username
   - Should see error message
   - Try logging in with wrong password
   - Should see error message

---

## API Response Codes

| Code | Meaning | When Used |
|------|---------|-----------|
| 200 | OK | Successful login, username/email check |
| 201 | Created | Successful signup |
| 400 | Bad Request | Validation error, duplicate user |
| 401 | Unauthorized | Invalid credentials |
| 403 | Forbidden | Account deactivated |
| 500 | Internal Server Error | Server error |

---

## Common Error Messages

| Message | Cause | Solution |
|---------|-------|----------|
| "Username is required" | Empty username | Provide username |
| "Password must be at least 6 characters" | Short password | Use longer password |
| "Invalid email address" | Wrong email format | Use valid email |
| "Username already exists!" | Duplicate username | Choose different username |
| "Email already registered" | Duplicate email | Use different email |
| "Invalid username or password" | Wrong credentials | Check credentials |
| "Account is deactivated" | Inactive account | Contact support |
| "Failed to connect to server" | Backend not running | Start Spring Boot app |

---

## Database Verification

Check registered users in MySQL:

```sql
-- View all users
SELECT id, username, email, first_name, last_name, role, active, created_at 
FROM users;

-- Count total users
SELECT COUNT(*) FROM users;

-- Find specific user
SELECT * FROM users WHERE username = 'johndoe';
```

---

## Quick Start Script

Save this as `test-api.sh`:

```bash
#!/bin/bash

BASE_URL="http://localhost:8080/api/auth"

echo "Testing Signup..."
curl -X POST $BASE_URL/signup \
  -H "Content-Type: application/json" \
  -d '{"firstName":"Test","lastName":"User","email":"test@example.com","username":"testuser","password":"test123","role":"Developer"}'

echo -e "\n\nTesting Login..."
curl -X POST $BASE_URL/login \
  -H "Content-Type: application/json" \
  -d '{"username":"testuser","password":"test123"}'

echo -e "\n\nTesting Username Check..."
curl -X GET $BASE_URL/check-username/testuser

echo -e "\n\nTesting Email Check..."
curl -X GET $BASE_URL/check-email/test@example.com

echo -e "\n\nDone!"
```

Run with: `bash test-api.sh`
