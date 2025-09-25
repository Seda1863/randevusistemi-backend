

⸻

Smart Appointment and Scheduling System

A modern web application that provides smart appointment and calendar management.
Users can easily book appointments, while providers (doctors, consultants, hairdressers, etc.) can set their availability and manage their schedules.

⸻

🛠️ Tech Stack
	•	Backend: Java 17, Spring Boot 3, Spring Security (JWT with RSA keys), JPA, Hibernate, PostgreSQL<img width="286" height="193" alt="Screenshot 2025-09-25 at 20 45 28" src="https://github.com/user-attachments/assets/87f2c986-a36b-44b5-823d-08589d095a39" />
<img width="381" height="258" alt="Screenshot 2025-09-25 at 20 45 22" src="https://github.com/user-attachments/assets/f2c1dcb5-1bcc-47e8-b0cb-33e0732e57dc" />
<img width="256" height="386" alt="Screenshot 2025-09-25 at 20 45 16" src="https://github.com/user-attachments/assets/66fd9e2b-aa83-4f8f-bca3-0804a78201c8" />

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

