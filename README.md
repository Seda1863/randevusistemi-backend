Smart Appointment and Scheduling System

Akıllı randevu ve takvim yönetimi sağlayan modern bir web uygulamasıdır. Kullanıcılar randevu alabilir, sağlayıcılar (doktor, danışman, kuaför vb.) ise kendi uygunluklarını belirleyip takvimlerini yönetebilir.
	•	Backend: Java 17, Spring Boot 3, Spring Security (JWT - RSA key based), JPA, Hibernate, PostgreSQL
	•	Frontend: Angular (standalone structure), Tailwind CSS
	•	Veritabanı: PostgreSQL
	•	Mail: Spring EmailService ile SMTP üzerinden doğrulama ve bildirim mailleri
	•	Yapılandırma: Token tabanlı güvenlik, kullanıcı rolleri, CORS ayarları, response modelleme

⸻
Güvenlik Yapısı
	•	JWT (public/private RSA key) ile token doğrulama
	•	AuthenticationManager, PasswordEncoder, JwtDecoder/Encoder, SecurityFilterChain gibi Spring bileşenleriyle yapılandırıldı
	•	Role-based erişim kontrolü
 Backend Çalıştırma (Spring Boot)
 cd randevusistemi-backend
./mvnw spring-boot:run
Frontend Çalıştırma (Angular)
cd randevusistemi-frontend
npm install
ng serve
Özellikler
	•	📧 E-posta doğrulamalı kayıt
	•	🗓️ Takvim ve müsaitlik ayarları
	•	✅ Randevu alma, iptal etme, geçmişi görüntüleme
	•	🧠 Admin paneli (Dashboard)
	•	🛡️ Role-based erişim
	•	📊 Gerçek zamanlı istatistik ve yönetim ekranı
