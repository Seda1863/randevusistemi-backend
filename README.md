

⸻

Smart Appointment and Scheduling System

A modern web application that provides smart appointment and calendar management.
Users can easily book appointments, while providers (doctors, consultants, hairdressers, etc.) can set their availability and manage their schedules.

⸻

🛠️ Tech Stack
	•	Backend: Java 17, Spring Boot 3, Spring Security (JWT with RSA keys), JPA, Hibernate, PostgreSQL
	•	Frontend: Angular (standalone structure), Tailwind CSS
	•	Database: PostgreSQL
	•	Mail Service: Spring EmailService with SMTP for verification and notifications
	•	Configuration: Token-based security, role-based access control, CORS setup, response modeling

⸻

🔒 Security
	•	JWT authentication with RSA public/private key pair
	•	Configured with Spring components such as:
	•	AuthenticationManager, PasswordEncoder, JwtDecoder/Encoder, SecurityFilterChain
	•	Role-based access control to restrict and manage permissions

⸻

🚀 Running the Project

Backend (Spring Boot)

cd randevusistemi-backend
./mvnw spring-boot:run

Frontend (Angular)

cd randevusistemi-frontend
npm install
ng serve


⸻

✨ Features
	•	📧 Email verification during sign-up
	•	🗓️ Calendar and availability settings for providers
	•	✅ Book, cancel, and view appointment history
	•	🧠 Admin dashboard for management
	•	🛡️ Role-based access control
	•	📊 Real-time statistics and monitoring

⸻

👉 This project focuses on providing a secure, user-friendly, and scalable appointment system, combining a powerful Spring Boot backend with a modern Angular frontend.

⸻

