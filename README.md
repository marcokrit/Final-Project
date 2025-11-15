# 🚀 FindMyGroup (CS318 Project)

"FindMyGroup" คือเว็บแอปพลิเคชันที่ออกแบบมาเพื่อแก้ปัญหาคลาสสิกในมหาวิทยาลัย: **การหาสมาชิกกลุ่มทำโปรเจกต์**

โปรเจกต์นี้จะทำหน้าที่เป็นแพลตฟอร์มกลางที่เชื่อมต่อระหว่างนักศึกษาที่ต้องการหากลุ่ม กับนักศึกษาที่กำลังมองหาสมาชิกเข้ากลุ่ม ช่วยลดความล่าช้าในการทำงานและป้องกันการที่นักศึกษาต้องทำงานคนเดียว

---

## 🎯 ปัญหาที่ต้องการแก้ไข (Problem Statement)

> ในมหาวิทยาลัย นักศึกษามักประสบปัญหาในการหากลุ่มทํางานหรือกิจกรรม เช่น การทำรายงาน การทำโปรเจกต์กลุ่ม หรือการเข้าร่วมกิจกรรมต่าง ๆ เพราะไม่รู้ว่าจะหาสมาชิกที่มีความสนใจหรือความสามารถตรงกันได้จากที่ไหน ส่งผลให้เกิดความล่าช้าในการทํางานร่วมกัน หรือบางคนต้องทํางานคนเดียว

## ✨ ฟีเจอร์หลัก (Core Features)

* **👤 ระบบผู้ใช้ (Authentication):**
    * สมัครสมาชิก (Register) ด้วยข้อมูลพื้นฐาน: ชื่อผู้ใช้, รหัสผ่าน, ชื่อ, คณะ, และ **ทักษะ (Skills)**
    * เข้าสู่ระบบ (Login)

* **👨‍👩‍👧‍👦 การจัดการกลุ่ม (Group Management):**
    * ผู้ใช้สามารถ **สร้างกลุ่ม** ใหม่ โดยระบุ "ชื่อโปรเจกต์" และ "รายละเอียด"
    * ผู้ใช้สามารถ **ดู (View)** กลุ่มทั้งหมดที่มีในระบบได้

* **🤝 การเข้าร่วมและอนุมัติ (Join & Membership):**
    * ผู้ใช้ทั่วไปสามารถกด **"ขอเข้าร่วมกลุ่ม"** (Request to Join) ในกลุ่มที่สนใจ
    * **เจ้าของกลุ่ม** สามารถดูรายชื่อคนที่ขอเข้าร่วม และมีสิทธิ์ **"อนุมัติ" (Approve)** หรือปฏิเสธได้

* **📜 โปรไฟล์ผู้ใช้ (User Profile):**
    * หน้าแสดงข้อมูลส่วนตัวของผู้ใช้ (ชื่อ, คณะ, ทักษะ) เพื่อให้ผู้อื่นเห็นและใช้ประกอบการตัดสินใจ

---

## 🛠️ Tech Stack

โปรเจกต์นี้สร้างด้วยสถาปัตยกรรมแบบ **Monolith** โดยใช้ Spring Boot ในการจัดการทั้ง Backend และ Frontend

* **Backend:** Java 17+, Spring Boot
* **Security:** Spring Security
* **Data Access:** Spring Data JPA
* **Frontend (SSR):** Spring Boot (Server-Side Rendering)
* **Database:** MySQL

---

## 🤖 AI Agent: ขั้นตอนการทำงานของโปรเจกต์

ในฐานะ AI Agent ที่ดูแลโปรเจกต์นี้ นี่คือขั้นตอนการทำงานและโครงสร้างที่เราจะใช้ในการพัฒนาครับ

### 1. การตั้งค่าโปรเจกต์ (Project Setup)

โปรเจกต์นี้เป็น **Spring Boot Monolith** หมายความว่าทั้ง Logic (Backend) และ View (Frontend) จะอยู่ใน Repository เดียวกัน

* **Backend (API & Logic):**
    * `com.findmygroup.controller`: คลาสที่จัดการ HTTP Requests (เช่น `@Controller`)
    * `com.findmygroup.service`: คลาสที่จัดการ Business Logic (เช่น Logic การอนุมัติกลุ่ม)
    * `com.findmygroup.repository`: Interfaces ที่เชื่อมต่อกับ Database (ใช้ `Spring Data JPA`)
    * `com.findmygroup.model` (หรือ `entity`): คลาส Java (POJOs) ที่แมปกับตารางใน Database
* **Security:**
    * `com.findmygroup.config`: คลาส `SecurityConfig` สำหรับตั้งค่า Spring Security (จัดการหน้า Login, Register, และสิทธิ์การเข้าถึง)
* **Frontend (Views):**
    * `src/main/resources/templates`: โฟลเดอร์สำหรับเก็บไฟล์ UI (เช่น `.html` ถ้าใช้ Thymeleaf)
    * `src/main/resources/static`: โฟลเดอร์สำหรับเก็บ CSS, JavaScript, และรูปภาพ

### 2. การตั้งค่าฐานข้อมูล (Database Setup)

เราใช้ MySQL เป็นฐานข้อมูล

1.  สร้าง Schema/Database ใน MySQL (เช่น `findmygroup_db`)
2.  ตั้งค่าการเชื่อมต่อในไฟล์ `src/main/resources/application.properties`:

    ```properties
    spring.datasource.url=jdbc:mysql://localhost:3306/findmygroup_db
    spring.datasource.username=[YOUR_DB_USER]
    spring.datasource.password=[YOUR_DB_PASSWORD]
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true
    ```

### 3. โครงสร้างตารางเบื้องต้น (Database Schema)

นี่คือตารางหลักที่จำเป็นสำหรับฟีเจอร์ MVP:

1.  **`users`**
    * `id` (PK)
    * `username`
    * `password` (ต้อง Encrypt)
    * `name`
    * `faculty`
    * `skills` (อาจเก็บเป็น String หรือแยกตาราง)
2.  **`groups`**
    * `id` (PK)
    * `project_name`
    * `description`
    * `owner_id` (FK to `users.id`)
3.  **`group_members`** (ตารางเชื่อมโยง Many-to-Many)
    * `id` (PK)
    * `user_id` (FK to `users.id`)
    * `group_id` (FK to `groups.id`)
    * `status` (เช่น `PENDING`, `APPROVED`)

### 4. ขั้นตอนการรันโปรเจกต์ (Getting Started)

1.  **Clone Repository:**
    ```bash
    git clone [YOUR_REPOSITORY_URL]
    cd findmygroup
    ```
2.  **ตั้งค่า Database:** (ทำตามขั้นตอนในข้อ 2)
3.  **รันแอปพลิเคชัน:**
    (ใช้วิธีใดวิธีหนึ่ง)
    * **ผ่าน Maven (Recommended):**
        ```bash
        ./mvnw spring-boot:run
        ```
    * **ผ่าน IDE:**
        รันไฟล์ `FindMyGroupApplication.java`

4.  **เข้าใช้งาน:**
    เปิดเบราว์เซอร์และไปที่ `http://localhost:8080`

---
