# Login-Signup Web Application

A comprehensive Java web application implementing a complete user authentication system with login, signup, and password recovery functionality. Built using Java Servlets, JSP (Java Server Pages), MySQL database, and Tomcat server with connection pooling.

## 🚀 Features

- **User Registration**: Complete signup form with validation
- **User Login**: Secure authentication system
- **Password Recovery**: Forgot password functionality with username verification
- **Input Validation**: Client and server-side validation for all forms
- **Session Management**: Proper session handling and security
- **Database Integration**: MySQL database with connection pooling
- **Speckit Integration**: External authentication service integration
- **Hybrid Authentication**: Local database with Speckit service fallback
- **Responsive UI**: Modern, styled web interface with CSS

## 🏗️ Architecture

This application follows the **MVC (Model-View-Controller)** pattern:

- **Model**: `LoginData.java` - Data model for user information
- **View**: JSP pages - User interface components
- **Controller**: `LoginDataServlet.java` - Business logic and request handling

## 📁 Project Structure

```
/workspace/
├── login/                          # Java source files
│   ├── LoginData.java             # Data model class
│   ├── LoginDataServlet.java      # Main servlet controller
│   ├── LoginDb.java               # Database operations
│   ├── SpeckitLoginDb.java        # Speckit-integrated database operations
│   ├── SpeckitServiceClient.java  # Speckit service client
│   ├── SpeckitAuthResponse.java   # Speckit response model
│   ├── SpeckitConfig.java         # Speckit configuration manager
│   ├── SpeckitTestServlet.java    # Speckit testing servlet
│   └── Verification.java          # Input validation utilities
├── WebContent/                     # Web application content
│   ├── Login-form.jsp             # Login page
│   ├── Sign-Up-Form.jsp           # Registration page
│   ├── Check-username.jsp         # Username verification page
│   ├── Forgot-password.jsp        # Password reset page
│   ├── Thankyou.jsp               # Success page
│   ├── *.css                      # Stylesheet files
│   └── png/                       # Image assets
├── WEB-INF/
│   ├── web.xml                    # Web application configuration
│   └── speckit-config.properties  # Speckit service configuration
├── META-INF/
│   └── context.xml                # Database connection configuration
├── lib/                           # External JAR dependencies
└── login-webapp.sql              # Database schema
```

## 🛠️ Technologies Used

- **Backend**: Java Servlets, JSP
- **Database**: MySQL 8.0
- **Server**: Apache Tomcat
- **Frontend**: HTML, CSS, JavaScript
- **Libraries**: JSTL (JavaServer Pages Standard Tag Library)
- **Connection Pooling**: Tomcat JDBC Connection Pool
- **External Services**: Speckit Authentication Service
- **HTTP Client**: Java HttpURLConnection

## 📋 Prerequisites

- Java Development Kit (JDK) 8 or higher
- Apache Tomcat Server
- MySQL Database Server
- IDE (Eclipse, IntelliJ IDEA, or similar)

## ⚙️ Installation & Setup

### 1. Database Setup

1. Create a MySQL database:
```sql
CREATE DATABASE IF NOT EXISTS `login-webapp`;
```

2. Import the database schema:
```bash
mysql -u your_username -p login-webapp < login-webapp.sql
```

### 2. Configuration

1. **Update Database Connection** in `META-INF/context.xml`:
```xml
<Resource name="jdbc/login-webapp" auth="Container"
    type="javax.sql.DataSource"
    factory="org.apache.tomcat.jdbc.pool.DataSourceFactory" 
    maxTotal="20" minIdle="10" maxWait="10000" 
    username="YOUR_MYSQL_USERNAME" 
    password="YOUR_MYSQL_PASSWORD"
    driverClassName="com.mysql.jdbc.Driver"
    url="jdbc:mysql://127.0.0.1:3306/login-webapp" />
```

2. **Deploy to Tomcat**:
   - Copy the entire project to Tomcat's `webapps` directory
   - Ensure all JAR files in `lib/` are in the classpath

### 3. Dependencies

The following JAR files are included in the `lib/` directory:
- `mysql-connector-java-8.0.17.jar` - MySQL JDBC driver
- `javax.servlet.jsp.jstl-1.2.1.jar` - JSTL implementation
- `javax.servlet.jsp.jstl-api-1.2.1.jar` - JSTL API

## 🎯 Usage

### Application Flow

1. **Login Page** (`Login-form.jsp`):
   - Enter username and password
   - Redirects to success page on valid credentials
   - Shows error message for invalid credentials

2. **Sign Up Page** (`Sign-Up-Form.jsp`):
   - Register new users with username, password, and email
   - Validates input fields
   - Shows success message on successful registration

3. **Forgot Password Flow**:
   - **Check Username** (`Check-username.jsp`): Verify username exists
   - **Reset Password** (`Forgot-password.jsp`): Set new password

4. **Success Page** (`Thankyou.jsp`):
   - Displays after successful login
   - Provides logout functionality

### API Endpoints

The `LoginDataServlet` handles the following commands:

- `command=login` - User authentication (with Speckit integration)
- `command=update` - User registration (with Speckit integration)
- `command=check` - Username verification (with Speckit integration)
- `command=forgot` - Password reset (with Speckit integration)
- `command=thanks` - Logout

### Speckit Integration Endpoints

- `/SpeckitTestServlet` - Speckit integration testing interface

## 🔒 Security Features

- **Input Validation**: Server-side validation for all user inputs
- **Session Management**: Proper session handling with security measures
- **SQL Injection Prevention**: Uses PreparedStatement for database queries
- **Password Security**: Basic password validation and confirmation
- **Browser Security**: Prevents back button navigation after logout

## 🎨 UI/UX Features

- **Responsive Design**: Clean, modern interface
- **Visual Feedback**: Error messages and success notifications
- **Icon Integration**: Username and password field icons
- **Color-coded Pages**: Different color schemes for different pages
- **Form Validation**: Real-time validation feedback

## 🗄️ Database Schema

### `logindata` Table

| Column  | Type        | Description           |
|---------|-------------|----------------------|
| username| varchar(45) | User's username       |
| password| varchar(45) | User's password       |
| email   | varchar(45) | User's email address  |

## 🔧 Configuration Details

### Web Application Configuration (`web.xml`)
- Defines welcome files
- Configures servlet mappings
- Sets up JSP support

### Database Configuration (`context.xml`)
- Configures Tomcat connection pooling
- Sets up MySQL database connection
- Manages connection pool parameters

## 🚨 Important Notes

1. **Security**: This is a learning project. For production use, implement:
   - Password hashing (bcrypt, PBKDF2)
   - HTTPS encryption
   - CSRF protection
   - Input sanitization

2. **Database Credentials**: Update the database credentials in `context.xml` before deployment

3. **Connection Pooling**: The application uses Tomcat's built-in connection pooling for better performance

## 🐛 Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Verify MySQL is running
   - Check credentials in `context.xml`
   - Ensure database exists

2. **ClassNotFoundException**:
   - Verify all JAR files are in the classpath
   - Check Tomcat's `lib` directory

3. **404 Errors**:
   - Ensure proper deployment to Tomcat
   - Check `web.xml` configuration

## 📝 Development Notes

- **Package Structure**: Uses `org.deepak.login` package
- **Error Handling**: Comprehensive try-catch blocks
- **Code Organization**: Clear separation of concerns
- **Comments**: Well-documented code for learning purposes

## 🤝 Contributing

This is an educational project. Feel free to:
- Fork the repository
- Submit issues
- Suggest improvements
- Create pull requests

## 📄 License

This project is open source and available under the [MIT License](LICENSE).

## 👨‍💻 Author

**Deepak Mahajan** - Web Development Project

---

*This project demonstrates fundamental concepts of Java web development, including servlets, JSP, database connectivity, and MVC architecture. Perfect for beginners learning Java web technologies.*