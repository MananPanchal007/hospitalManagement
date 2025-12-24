# Hospital Management System

A comprehensive Spring Boot application for managing patient records, appointments, and doctor schedules with advanced features like transactional operations, validation, and pessimistic locking for high-concurrency scenarios.

## Features

- **Patient Management**: Create, read, update, and delete patient records with full validation
- **Doctor Management**: Manage doctor profiles, specializations, and availability
- **Appointment Booking**: Book and cancel appointments with transaction management
- **Patient History Tracking**: Track patient appointment history
- **Doctor Availability Checks**: Real-time availability checking for doctors
- **Concurrency Control**: Pessimistic locking to prevent overbooking
- **API Documentation**: Swagger UI for interactive API testing
- **Security**: Spring Security configuration for API protection
- **Database**: PostgreSQL running in Docker

## Technology Stack

- **Framework**: Spring Boot 4.0.1
- **Database**: PostgreSQL 15
- **ORM**: Spring Data JPA / Hibernate
- **Security**: Spring Security
- **API Documentation**: Swagger/OpenAPI 3
- **Validation**: Bean Validation API
- **Containerization**: Docker & Docker Compose

## Prerequisites

- Java 25 or higher
- Maven 3.6+
- Docker and Docker Compose

## Setup Instructions

### 1. Start the Database

Start the PostgreSQL database using Docker Compose:

```bash
docker-compose up -d
```

This will:
- Start a PostgreSQL container on port 5432
- Create a database named `hospital_management`
- Set up user credentials (username: `hospital_user`, password: `hospital_password`)

### 2. Build and Run the Application

```bash
# Build the project
mvn clean install

# Run the application
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### 3. Access Swagger UI

Once the application is running, access the Swagger UI at:
```
http://localhost:8080/swagger-ui.html
```

## API Endpoints

### Patient Management
- `POST /api/patients` - Create a new patient
- `GET /api/patients` - Get all patients
- `GET /api/patients/{id}` - Get patient by ID
- `PUT /api/patients/{id}` - Update patient
- `DELETE /api/patients/{id}` - Delete patient
- `GET /api/patients/{id}/history` - Get patient history

### Doctor Management
- `POST /api/doctors` - Create a new doctor
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{id}` - Get doctor by ID
- `GET /api/doctors/available` - Get available doctors
- `GET /api/doctors/specialization/{specialization}` - Get doctors by specialization
- `PUT /api/doctors/{id}` - Update doctor
- `DELETE /api/doctors/{id}` - Delete doctor
- `GET /api/doctors/{id}/availability` - Check doctor availability

### Appointment Management
- `POST /api/appointments` - Book an appointment
- `POST /api/appointments/{id}/cancel` - Cancel an appointment
- `GET /api/appointments/{id}` - Get appointment by ID
- `GET /api/appointments/patient/{patientId}` - Get appointments by patient
- `GET /api/appointments/doctor/{doctorId}` - Get appointments by doctor
- `GET /api/appointments/doctor/{doctorId}/status/{status}` - Get appointments by doctor and status
- `PUT /api/appointments/{id}/status` - Update appointment status
- `GET /api/appointments/doctor/{doctorId}/availability` - Check doctor availability at specific time

## Key Features Implementation

### Transactional Operations
- All write operations use `@Transactional` with `REQUIRED` propagation
- Automatic rollback on exceptions
- Read operations use `readOnly = true` for optimization

### Validation
- DTOs use Bean Validation annotations (`@NotNull`, `@NotBlank`, `@Email`, `@Future`, etc.)
- Custom validation for phone numbers and date constraints
- Global exception handler for validation errors

### Pessimistic Locking
- Used in appointment booking to prevent overbooking
- `LockModeType.PESSIMISTIC_WRITE` ensures atomic operations
- Prevents concurrent booking conflicts

### Database Schema
The application automatically creates the following tables:
- `patients` - Patient records
- `doctors` - Doctor profiles
- `appointments` - Appointment bookings

## Example API Calls

### Create a Patient
```bash
curl -X POST http://localhost:8080/api/patients \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "phoneNumber": "+1234567890",
    "dateOfBirth": "1990-01-15",
    "address": "123 Main St, City, State",
    "medicalHistory": "No known allergies"
  }'
```

### Create a Doctor
```bash
curl -X POST http://localhost:8080/api/doctors \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "Dr. Jane",
    "lastName": "Smith",
    "email": "jane.smith@hospital.com",
    "phoneNumber": "+1234567891",
    "specialization": "Cardiology",
    "department": "Heart Care",
    "consultationFee": 150,
    "isAvailable": true
  }'
```

### Book an Appointment
```bash
curl -X POST http://localhost:8080/api/appointments \
  -H "Content-Type: application/json" \
  -d '{
    "patientId": 1,
    "doctorId": 1,
    "appointmentDateTime": "2024-12-25T10:00:00",
    "reason": "Regular checkup"
  }'
```

## Stopping the Database

To stop the PostgreSQL container:

```bash
docker-compose down
```

To stop and remove volumes (this will delete all data):

```bash
docker-compose down -v
```

## Project Structure

```
src/main/java/com/hospitalManagement/
├── config/          # Configuration classes (Security, Swagger)
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities
├── exception/      # Exception handlers
├── repository/     # JPA repositories
└── service/        # Business logic services
```

## Notes

- The database schema is automatically created/updated on startup (`spring.jpa.hibernate.ddl-auto=update`)
- All timestamps are automatically managed by JPA lifecycle callbacks
- Pessimistic locking ensures data consistency in high-concurrency scenarios
- Validation errors are returned in a structured format via the global exception handler

## License

This project is for educational purposes.

