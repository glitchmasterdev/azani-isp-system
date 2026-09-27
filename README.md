# AZANI INTERNET SERVICE PROVIDER INFORMATION SYSTEM
**Course**: SCO200 – Object Oriented Programming II Project (September – November 2026)  
**Technology Stack**: Java 17+ (OOP), MySQL 8.0, JDBC, Maven, JUnit 5, Java Swing GUI, Interactive CLI

---

## 1. Project Overview

Azani is an Internet Service Provider (ISP) providing internet connectivity and infrastructure services to learning institutions across four categories:
- **Primary School**
- **Junior School**
- **Senior School**
- **College**

This system automates institutional registration, site infrastructure readiness assessment, bandwidth subscription & 10% upgrade discounts, billing cycles, 15% overdue fine surcharges, disconnection tracking (past the 10th day of the subsequent month), KSh 1,000 reconnection fees, payment capturing, and automated financial report generation.

---

## 2. Course Requirement Mapping

| Task # | Course Requirement | Implemented Feature & Location |
| :--- | :--- | :--- |
| **Task 1** | Register institutions requiring services with contact person personal details | `InstitutionService.registerInstitution()` & `InstitutionDAO`, Swing Tab 1, CLI Option 1 |
| **Task 2(a)**| Capture registration fees (KSh 8,500) | `PaymentService.captureRegistrationFee()`, Swing Tab 4, CLI Option 2 |
| **Task 2(b)**| Capture installation fees (Base + Equipment) | `PaymentService.captureInstallationFee()`, Swing Tab 4, CLI Option 2 |
| **Task 2(c)**| Capture monthly payments | `PaymentService.captureMonthlyPayment()`, Swing Tab 4, CLI Option 2 |
| **Task 3(a)**| Generate registered institutions | `ReportService.generateRegisteredInstitutionsReport()`, Swing Tab 6, CLI Option 3 |
| **Task 3(b)**| Generate list of defaulters (15% fine) | `ReportService.generateDefaultersReport()`, Swing Tab 5 & 6, CLI Option 4 |
| **Task 3(c)**| Generate list with disconnection issues | `ReportService.generateDisconnectionReport()`, Swing Tab 5 & 6, CLI Option 5 |
| **Task 3(d)**| Details of infrastructural requirements | `ReportService.generateInfrastructureReport()`, Swing Tab 2 & 6, CLI Option 6 |
| **Task 4(a)**| Total installation cost for each institution | `ComputationService.computeTotalInstallationCosts()`, Master Report |
| **Task 4(b)**| Cost of PCs and LAN for assorted services | `ComputationService.computeAssortedHardwareCosts()`, Master Report |
| **Task 4(c)**| Total monthly charges for upgraded services (10% off) | `ComputationService.computeUpgradedServiceCharges()`, Master Report |
| **Task 4(d)**| Monthly charges, overdue fines & reconnection fees per category | `ComputationService.computeCategoryRevenueBreakdown()`, Master Report |
| **Task 4(e)**| Aggregate amount for each service sorted by institution | `ComputationService.computeServiceAggregatesByInstitution()`, Master Report |
| **Task 5** | Generate appropriate reports | Tabular monospace reports, invoice breakdowns & GUI viewer |

---

## 3. Object-Oriented Programming (OOP) Architecture

- **Abstraction & Inheritance**:
  - `Institution` (Abstract base class with polymorphic `getRecommendedBandwidth()` and `getCategoryDescription()`)
  - Subclasses: `PrimarySchool`, `JuniorSchool`, `SeniorSchool`, and `College`.
- **Creational Factory Pattern**:
  - `InstitutionFactory`: Dynamically instantiates correct subclasses based on `InstitutionCategory`.
- **Encapsulation**:
  - All attributes in `ContactPerson`, `Infrastructure`, `Subscription`, `Bill`, and `Payment` are strictly private with validated getters/setters.
- **Business Rule Encapsulation**:
  - `BandwidthPackage`: Encapsulates speed, pricing (Table 1), and 10% upgrade discount rule.
  - `Infrastructure`: Encapsulates PC unit price (KSh 40,000), LAN node tiers (Table 2: 2-10, 11-20, 21-40, 41-100), and base installation fee (KSh 10,000).
  - `Bill`: Encapsulates 15% overdue fine surcharge, 10th of next month disconnection rule, and KSh 1,000 reconnection surcharge.

---

## 4. How to Launch (No Terminal Required)

You do **NOT** need to open a terminal or type any commands to run the system:

### Option 1: Desktop Shortcut (One Click)
- A shortcut named **`Azani ISP System`** has been placed directly on your Windows **Desktop**. Double-click it to start the application immediately.

### Option 2: Double-Click Launchers in the Project Folder
Open `C:\Users\Hp\.gemini\antigravity\scratch\AzaniISP` in Windows File Explorer:
- **`Launch_Azani_ISP.vbs`**: Double-click this file to launch the GUI completely silently with **no console window**.
- **`Launch_Azani_ISP.bat`**: Standard double-clickable Windows launcher batch file.
- **`AzaniISP.jar`**: Direct double-clickable standalone Java application archive.

---

### Option 3: Terminal / Developer Commands (Optional)
If you ever want to run or test from the command line:
- Run GUI: `mvn compile exec:java` or `javaw -jar AzaniISP.jar`
- Run interactive CLI: `mvn exec:java "-Dexec.args=--cli"`
- Run automated simulation: `mvn exec:java "-Dexec.args=--cli-test"`
- Run unit test suite: `mvn test`

### Option C: Run Automated Demonstration & Verification
```bash
mvn exec:java "-Dexec.args=--cli-test"
```

### Option D: Run Unit Tests
```bash
mvn test
```

---

## 5. Database Configuration (MySQL & Embedded Fallback)

Database settings are configured in `src/main/resources/db.properties`:
```properties
db.type=mysql
db.host=localhost
db.port=3306
db.name=azani_isp_db
db.user=root
db.password=
db.auto_fallback=true
```

1. **MySQL Mode**: If MySQL is running locally on port 3306, provide your user/password and the system will automatically create `azani_isp_db` and all tables from `db/schema.sql`.
2. **Auto-Fallback Mode**: If MySQL is unconfigured or offline, the system seamlessly initializes an embedded in-memory database with full MySQL compatibility, ensuring 100% functionality with zero setup.
