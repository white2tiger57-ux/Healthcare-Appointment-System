# Healthcare Appointment System

Full-stack Healthcare Appointment System built with Spring Boot 3.2.x and React 18 + TypeScript.

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8.0+
- Node.js 18+
- npm 9+

## Database Setup

1. Ensure your existing `healthcare_appointment_system` MySQL database is running
2. Run the migration script to add Spring Security tables:

```sql
mysql -u root -p healthcare_appointment_system < spring-boot-backend/src/main/resources/db-migration.sql
```

## Backend (Spring Boot)

```bash
cd spring-boot-backend

# Update database credentials in application-dev.properties if needed
# Then run:
mvn spring-boot:run

# Run tests:
mvn test

# Build production JAR:
mvn clean package -DskipTests
java -jar target/appointment-system-1.0.0.jar --spring.profiles.active=prod
```

Backend runs on `http://localhost:8080`

## Frontend (React + TypeScript)

```bash
cd react-frontend

npm install
npm run dev
```

Frontend runs on `http://localhost:5173` (proxied to backend via Vite config)

## Deployment

### Backend → Render
1. Push `spring-boot-backend/` to a Git repo
2. Create a new Web Service on Render
3. Set build command: `mvn clean package -DskipTests`
4. Set start command: `java -jar target/appointment-system-1.0.0.jar`
5. Add environment variables: `SPRING_DATASOURCE_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`, `CORS_ALLOWED_ORIGINS`

### Frontend → Vercel
1. Push `react-frontend/` to a Git repo
2. Import in Vercel, set framework to Vite
3. Add environment variable: `VITE_API_BASE_URL=https://your-api.onrender.com/api`

## API Endpoints

| Method | Endpoint | Auth | Description |
|--------|----------|------|-------------|
| POST | /api/auth/login | No | Login |
| POST | /api/auth/register/patient | No | Register patient |
| POST | /api/auth/register/doctor | No | Register doctor |
| GET | /api/profile | Yes | Get profile |
| PUT | /api/profile | Yes | Update profile |
| POST | /api/profile/photo | Yes | Upload photo |
| GET/POST | /api/appointments | Yes | List/book appointments |
| PUT | /api/appointments/:id/cancel | Yes | Cancel appointment |
| GET | /api/doctors | No | List doctors |
| GET | /api/doctors/:id/availability | No | Get time slots |
| GET | /api/departments | No | List departments |
| GET/POST/DELETE | /api/medical-records | Yes | CRUD medical records |
| GET | /api/medical-records/download/:id | Yes | Download file |
| GET/POST | /api/health-metrics | Yes | Health metrics |
| GET/PUT/DELETE | /api/notifications | Yes | Notifications |
| GET/POST | /api/messages | Yes | Messaging |
| POST | /api/feedback | Yes | Submit feedback |
| GET/PUT | /api/profile/preferences | Yes | User preferences |
| GET | /api/dashboard/patient | Patient | Patient dashboard |
| GET | /api/dashboard/doctor | Doctor | Doctor dashboard |
