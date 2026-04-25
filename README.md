# TUK Ability Club Portal

A Java Swing desktop application built for the Technical University of Kenya Disability Club. It allows members to register, authenticate, track financial data, manage events, and read club blog posts — all through a modern, accessible desktop interface.

**Group Name:** The Three Muskateers
- Margaret Wambui Nduta
- Mararo Emmanuel Igathe
- Terry Mwikali 

---

## Project Overview

The TUK Ability Club Portal provides a centralized platform for club operations including member registration, role-based access control, event management with RSVP, financial tracking, and a blog system for announcements and updates. The goal is to improve communication, transparency, and engagement for all club participants.

---

## Technology Stack

| Layer       | Technology                        |
|-------------|-----------------------------------|
| Language    | Java 17+                         |
| Framework   | Java Swing                       |
| UI Theme    | FlatLaf (Modern Look and Feel)   |
| Database    | SQLite (via sqlite-jdbc)         |
| Build Tool  | Apache Maven                     |
| IDE         | Visual Studio Code               |
| Version Control | Git & GitHub                 |

---

## Features Completed

### Authentication System
- Member registration with name, student ID, email, and password
- Login via email or student ID + password
- Input validation (email format, password length, matching confirmation)
- Admin approval workflow — new users must be approved before accessing the system
- Role-based access control with three roles: **Member**, **Executive**, **Admin**
- Default admin account seeded on first run

### Dashboard
- Modern dark sidebar navigation with hover and active state highlighting
- Top bar displaying user name and role
- Home page with live statistics cards (event count, blog count, club balance)
- Role-based menu visibility — management options only visible to executives/admins
- Logout functionality

### Event Management
- **Members:** View upcoming events in a styled table, RSVP to events, cancel RSVP
- **Executives/Admins:** Full CRUD — create, read, update, and delete events
- Event details include title, description, date, time, location, and capacity
- Live RSVP count displayed per event
- Form auto-populates when selecting a table row for editing

### Financial Management
- **Members:** View financial overview with income, expense, and balance summary cards; browse full transaction history with color-coded types (green for income, red for expense)
- **Executives/Admins:** Full CRUD — add, update, and delete transactions
- Transaction categories: Donations, Membership Fees, Fundraising, Sponsorship, Supplies, Venue, Transport, Catering, Printing, Other
- Live summary cards update after every transaction
- Amount formatting with KES currency

### Modern UI/UX
- FlatLaf light theme with rounded corners on buttons, inputs, and scroll bars
- Consistent color palette managed via `ThemeManager` utility class
- Reusable styled components: buttons, text fields, password fields, cards, link buttons
- Hover effects on all interactive elements
- Dark sidebar with section labels and active state indicators
- Clean card-based layout throughout the application

---

## Features Remaining

### Blog Module
- Executives create, edit, and delete blog posts (title, body, category)
- Members read blog posts and leave comments
- Recent and featured posts display
- Comment system with author and timestamp

### Member Management (Admin)
- Admin panel to approve or reject pending user registrations
- Assign and change user roles (Member, Executive, Admin)
- Member directory for networking

### Enhancements (Planned)
- Password hashing (bcrypt)
- Event reminders for RSVP participants
- Financial charts and graphs
- Search and filter on all tables
- Export reports to PDF/CSV
- Past events archive

---

## Database Structure

| Table                 | Description                                      |
|-----------------------|--------------------------------------------------|
| `users`               | name, student_id, email, password, role, is_approved, contact |
| `events`              | title, description, date, time, location, capacity, created_by |
| `event_registrations` | user_id, event_id, status, registered_at          |
| `transactions`        | type, description, amount, category, date, created_by |
| `blog_posts`          | title, body, category, author_id, published_at    |
| `comments`            | post_id, user_id, message, created_at              |

All tables are auto-created on first run via SQLite. A default admin account is seeded automatically.

---

## User Roles and Permissions

| Feature              | Guest | Member | Executive | Admin |
|----------------------|-------|--------|-----------|-------|
| Register account     | ✅    | —      | —         | —     |
| Login                | ✅    | ✅     | ✅        | ✅    |
| View events          | —     | ✅     | ✅        | ✅    |
| RSVP to events       | —     | ✅     | ✅        | ✅    |
| Manage events        | —     | —      | ✅        | ✅    |
| View finances        | —     | ✅     | ✅        | ✅    |
| Manage finances      | —     | —      | ✅        | ✅    |
| View blog            | —     | ✅     | ✅        | ✅    |
| Manage blog          | —     | —      | ✅        | ✅    |
| Manage users         | —     | —      | —         | ✅    |

---

## Project Structure

```
tukac-portal/
├── pom.xml
├── README.md
├── src/
│   ├── main/java/com/tukac/
│   │   ├── App.java                        # Entry point
│   │   ├── db/
│   │   │   └── Database.java               # SQLite connection & schema
│   │   ├── models/
│   │   │   └── User.java                   # User model
│   │   └── ui/
│   │       ├── ThemeManager.java            # Colors, fonts, styled components
│   │       └── panels/
│   │           ├── LoginPanel.java          # Login screen
│   │           ├── RegisterPanel.java       # Registration screen
│   │           ├── DashboardPanel.java      # Main dashboard with sidebar
│   │           ├── EventsPanel.java         # View events & RSVP
│   │           ├── ManageEventsPanel.java   # CRUD events
│   │           ├── FinancesPanel.java       # View financial overview
│   │           └── ManageFinancesPanel.java # CRUD transactions
│   └── test/java/com/tukac/
│       └── AppTest.java
└── tukac.db                                 # SQLite database (auto-created)
```

---

## How to Run Locally

### Prerequisites
- Java 17 or higher
- Apache Maven 3.9+

### Steps

1. Clone this repository:
   ```bash
   git clone https://github.com/your-username/TUKAC-OOP.git
   cd TUKAC-OOP/tukac-portal
   ```

2. Build and run:
   ```bash
   mvn clean compile exec:java "-Dexec.mainClass=com.tukac.App"
   ```

3. Login with the default admin account:
   ```
   Email: admin@tukac.com
   Password: admin123
   ```

4. To register a new member, click "Register here" on the login screen. New accounts require admin approval before they can log in.

---

## Default Credentials

| Role  | Email            | Password  |
|-------|------------------|-----------|
| Admin | admin@tukac.com  | admin123  |

---

## License

MIT
