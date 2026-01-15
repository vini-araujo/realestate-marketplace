# BuildConnect Backend MVP

## Prerequisites
- **Java 21**: Ensure you have Java 21 installed (`java -version`).
- **PostgreSQL**: You are using Postgres.app.
- **Maven**: A local version is included in `apache-maven-3.9.6`.

## Database Setup
(This has already been configured for you locally)
- Database: `buildconnect`
- User: `postgres`
- Password: `password`

## Running the Application
To start the backend server, run:

```bash
./apache-maven-3.9.6/bin/mvn spring-boot:run
```

The application will start on port `8080`.

## API Documentation
Once the app is running, access the Swagger UI to explore and test endpoints:
[http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Running Tests
To run the integration tests (configured to use H2 in-memory database, no Docker required):

```bash
./apache-maven-3.9.6/bin/mvn test
```

## Quick Start (Curl)
### 1. Register
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{
  "email": "dev@example.com",
  "password": "password123",
  "orgName": "Dev Corp",
  "orgType": "DEVELOPER"
}'
```

### 2. Login
(If you need a new token later)
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
  "email": "dev@example.com",
  "password": "password123"
}'
```

### 3. List Projects
Replace `<YOUR_TOKEN>` with the token from the register/login response.
```bash
curl -v http://localhost:8080/api/v1/projects \
-H "Authorization: Bearer <YOUR_TOKEN>"
```

## Frontend Setup

The frontend is built with Next.js and Tailwind CSS.

1.  Navigate to the frontend directory:
    ```bash
    cd frontend
    ```

2.  Install dependencies:
    ```bash
    npm install
    ```

3.  Run the development server:
    ```bash
    npm run dev
    ```

4.  Open [http://localhost:3000](http://localhost:3000) (or the port shown in the terminal) with your browser.

### Features
- **Landing Page**: Introduction to the platform.
- **Login/Register**: User authentication.
- **Dashboard**: View and create projects.
