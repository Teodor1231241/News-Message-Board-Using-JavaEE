# Java EE Enterprise Application - News Message Board

📝 **A distributed enterprise application built using Java EE (EJB 3.1) for posting and viewing messages in a database.**

## 🛠️ Technologies Used
- **Java EE 7** (EJB 3.1, JPA, JMS)
- **Frontend**: Servlets, HTML
- **Backend**: Message-Driven Beans, Stateless Session Beans, JPA Entities
- **Database**: PostgreSQL
- **Tools**: NetBeans IDE 8, GlassFish Server 4, JDK 8

## 🚀 Key Features
- **Post Messages**: Users can submit messages via a web form (Servlet `PostMessage`).
- **View Messages**: Dynamic listing of stored messages (Servlet `ListNews`).
- **Asynchronous Processing**: JMS queues and Message-Driven Beans for decoupled message handling.
- **Session Management**: Singleton bean (`SessionManagerBean`) to track active user sessions.
- **Modular Architecture**: Clear separation into Web Tier (Servlets), Business Tier (EJBs), and EIS Tier (PostgreSQL).

## 📂 Project Structure
1. **Web Module**: Contains Servlets (`ListNews`, `PostMessage`) and session management.
2. **EJB Module**: 
   - JPA Entity `NewsEntity` for database mapping.
   - Message-Driven Bean `NewsMessage` for JMS processing.
   - Stateless Session Facade `NewsEntityFacade` for CRUD operations.
3. **Database**: PostgreSQL configured via JDBC with automatic table generation.

## 🔧 Challenges & Solutions
- **NetBeans Compatibility**: Downgraded to NetBeans 8 (unofficial site) for Java EE 7 support.
- **GlassFish Configuration**: Manual setup of JMS resources (`jms/NewMessage`) using `asadmin`.
- **Deployment Issues**: Resolved by clearing server cache and rebuilding the EAR module.

## 📸 Architecture Overview
*Three-tier architecture with Web, Business, and EIS layers.*

## 🚀 Getting Started
1. **Prerequisites**: NetBeans 8, JDK 8, GlassFish 4, PostgreSQL.
2. **Clone the repo** and import as an Enterprise Application in NetBeans.
3. Configure `persistence.xml` for your PostgreSQL instance.
4. Deploy the EAR module to GlassFish and access via `http://localhost:8080/NewsMessage-war/ListNews`.

## 🤝 Contributing
Feel free to fork the project, open issues, or submit PRs. Contributions to improve documentation or resolve deployment quirks are welcome!
