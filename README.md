# 🛒 Joe Market — Premium Electronic Grocery Shop

**A Distributed Systems Project** — JavaFX + Multi-Threaded TCP Socket Programming (TCP)

---

## 📌 Project Overview
**Joe Market** is a robust, high-fidelity distributed grocery shop application. It utilizes a **Client-Server architecture** built on top of low-level Java TCP Sockets and custom packet serializers. The front-end is designed with JavaFX and styled using a modern **Material Design 3** theme. The project includes a fully automated packaging pipeline that compiles a native C# launcher, hiding background terminal windows, and compiles an offline installer package (`.msi` / `.exe`) for x64 and x86 Windows systems.

---

## 🚀 Key Features

### 🖥️ Client (JavaFX GUI)
- **Modern UI/UX:** Styled using elegant modern typography, glassmorphism, responsive grids, card designs, and transition animations.
- **Secure Authentication:** Features a beautiful login screen validating credentials against the distributed database.
- **Interactive Grocery Catalog:** Allows users to interactively browse and select fresh groceries (Apples, Bananas, Grapes, Oranges, Potatoes, Tomatoes) using responsive quantity spinners.
- **Live Calculations:** Dynamically calculates items' unit price and individual totals on the client side before checking out.
- **Professional Print Receipt:** Renders and prints a professional billing receipt upon server confirmation.

### ⚙️ Server (Multi-threaded Backend)
- **High-Performance TCP Server:** Built with `ServerSocket` listening on port `5000` using Java's high-efficiency I/O streams.
- **Multi-threaded Worker Pool:** Employs a thread-per-client model (`Worker` pattern) ensuring seamless concurrent client connections without freezing or blocking.
- **Protocol Serialization:** Intercepts, deserializes, processes, and serializes custom raw text frames.
- **Secure Authentication Database:** Stores valid administrator credentials and cross-checks them upon login handshakes.
- **Live Receipt Writer:** Generates a real-time transaction receipt file (`receipt.txt`) at the server root listing items, quantities, date, time, and calculated total.

### 📦 Native Packaging & Installer
- **Zero-Terminal Launcher:** Bundles a custom-built GUI Launcher written in C# (`JoeMarket.exe`). It boots the TCP server silently in the background, spawns the client GUI, and automatically cleans up/terminates the server when the client is closed to prevent port leaks.
- **Dual-Architecture Installer:** Compiles 32-bit (x86) and 64-bit (x64) installation executables using Inno Setup Compiler (`ISCC.exe`), complete with custom Wizard banners and a desktop shortcut.

---

## 🔌 Socket Protocol & Messaging Pattern

The application communicates using a **custom TCP stream frame protocol** over port `5000` via UTF encoding (`readUTF` / `writeUTF`).

```mermaid
sequenceDiagram
    participant Client as client.SocketClient
    participant Server as server.GroceryServer (Worker)
    
    Note over Client,Server: 1. Authentication Handshake
    Client->>Server: LOGIN:username:password
    alt Credentials are Valid
        Server-->>Client: SUCCESS
    else Credentials are Invalid
        Server-->>Client: FAIL
    }
    
    Note over Client,Server: 2. Checkout Calculation
    Client->>Server: CHECKOUT:Apples,2;Banana,1;Oranges,5;
    Note over Server: Server calculates total &<br/>writes receipt.txt
    Server-->>Client: RECEIPT:========================================\n          JOE MARKET          \n========================================\nTOTAL: 235.00 LE\n========================================
```

### 1. Authentication Packet
*   **Request Frame (Client ➔ Server):** `LOGIN:<username>:<password>`
    *   *Example:* `LOGIN:Youssef Ebrahim:Youssef_33263051`
*   **Response Frame (Server ➔ Client):** `SUCCESS` or `FAIL`

### 2. Checkout Packet
*   **Request Frame (Client ➔ Server):** `CHECKOUT:<item>,<qty>;<item>,<qty>;...`
    *   *Example:* `CHECKOUT:Apples,2;Banana,3;Grapes,1;`
*   **Response Frame (Server ➔ Client):** `RECEIPT:<rendered_receipt_text>`

---

## 📁 Project Structure

```text
JoeMarket/
├── pom.xml                         # Maven dependencies & plugins (Shade, JavaFX)
├── JoeMarket.iss                   # Inno Setup installation compiler configuration
├── BuildAndInstall.bat             # Full automated build, compile, and packaging script
├── RunServer.bat                   # Batch shortcut to run the standalone server
├── icon.ico, icon.png, icon.bmp    # Brand icon assets for setup wizards & window shortcuts
├── target/                         # Output folder for Maven compiled artifacts (ignored)
├── installer_output/               # Output folder for compiled x64 & x86 setup executables (ignored)
├── installer_assets/               # Standalone runtime packaging files
│   ├── Launcher.cs                 # C# Source for zero-terminal hidden process launcher
│   ├── RunJoeMarket.bat            # Windows startup script
│   └── RunJoeMarket.vbs            # Visual Basic Script to execute launcher silently
└── src/
    ├── module-info.java            # Java 9 module-info declaration
    ├── server/
    │   └── GroceryServer.java      # Multi-threaded TCP Socket Server (TCP Port 5000)
    ├── client/
    │   ├── Main.java               # JavaFX Application Entry point
    │   ├── AppLauncher.java        # Fat JAR Main class runner wrapper
    │   ├── SocketClient.java       # TCP Client Connection Handler
    │   ├── LoginController.java    # Controller for user login validation
    │   └── ShopController.java     # Controller for grocery catalog, live cart, and checkout
    └── resources/
        ├── login.fxml              # Login interface design (Material FXML Layout)
        ├── shop.fxml               # Main Grocery Store interface (Grid FXML Layout)
        ├── styles.css              # Material Design 3 style guidelines
        └── images/                 # Asset directory for fruit and vegetable icons
            ├── apples.png, banana.png, grapes.png, oranges.png, potatoes.png, tomatoes.png
            └── icon.png
```

---

## 🛠️ Installation & Setup (For Developers)

### Prerequisites
1.  **JDK 21 or higher:** Download from [Adoptium Temurin JDK 21 (x64 Windows)](https://adoptium.net).
2.  **IntelliJ IDEA Community/Ultimate:** Download from [JetBrains IDEA](https://www.jetbrains.com/idea/download).
3.  **Inno Setup Compiler 6+ (Required for building installers):** Download from [JRSoftware](https://jrsoftware.org/isdl.php).

### First-Time Workspace Import
1.  Open **IntelliJ IDEA**.
2.  Go to `File -> Open`, navigate to the `JoeMarket` directory, select it, and click **OK**.
3.  IntelliJ will parse the `pom.xml` and automatically download required JavaFX dependencies (Controls, FXML) via Maven.

---

## ▶️ Running the Application

### Option A: Standard Run (Via IDE)
To test the distributed network architecture, start the server and client separately:

1.  **Start the Server:**
    *   Navigate to `src/server/GroceryServer.java`.
    *   Right-click `GroceryServer.java` ➔ `Run 'GroceryServer.main()'`.
    *   The console will output:
        ```text
        ╔══════════════════════════════════╗
        ║   Grocery Shop Server — Port 5000  ║
        ╚══════════════════════════════════╝
        ✅ Waiting for clients on port: 5000 .......
        ```
2.  **Start the Client:**
    *   Navigate to `src/client/Main.java`.
    *   Right-click `Main.java` ➔ `Run 'Main.main()'`.
    *   *Alternative:* Open the terminal and run:
        ```bash
        mvn javafx:run
        ```

### Option B: running Standalone Server Script
You can easily launch the server in a standalone command prompt by double-clicking:
`RunServer.bat`

---

## 📦 Automated Build & Installer Packaging Pipeline

The project features a **fully-automated build pipeline** that compiles the code, wraps the processes, generates a native executable, and compiles a dual-architecture Windows setup installer.

### How to Build the Installer:
1.  Double-click the **`BuildAndInstall.bat`** script at the project root.
2.  The script will execute the following automated stages:
    *   **Stage 1:** Cleans target and installer build directories (`target/`, `installer_output/`).
    *   **Stage 2:** Compiles and packages the Java source code into a single, dependency-bundled **Fat JAR** using the `maven-shade-plugin`.
    *   **Stage 3:** Compiles the native C# launcher source `installer_assets/Launcher.cs` using the Windows native .NET Framework Compiler (`csc.exe`) into `installer_assets/JoeMarket.exe` with a custom application icon.
    *   **Stage 4:** Automates Inno Setup (`ISCC.exe`) to build two highly-compressed standalone installer setups:
        *   `JoeMarket_Setup_1.0_x64.exe` (For 64-bit systems)
        *   `JoeMarket_Setup_1.0_x86.exe` (For 32-bit legacy systems)
3.  Upon completion, the script automatically opens the `installer_output` folder in Windows Explorer.

---

## 🖥️ Production Installation (For Users)

1.  Navigate to `installer_output/` and double-click **`JoeMarket_Setup_1.0_x64.exe`** (or `x86.exe`).
2.  Complete the Inno Setup wizard. It will:
    *   Verify if a Java JRE/JDK is installed.
    *   Install the application files locally into `C:\Program Files\JoeMarket` (or `Program Files (x86)`).
    *   Create a beautiful desktop shortcut and Start Menu folder.
3.  **Run the App:** Double-click the **Joe Market** shortcut on your Desktop.
    *   **The Magic:** The background server boots *silently* (no terminal popping up).
    *   The **Login screen GUI** appears instantly.
    *   Logging in takes you to the **Grocery Store screen**.
    *   Upon closing the store GUI, the launcher automatically terminates the background server to clean system resources.

---

## 🔑 Login Credentials

> 💡 **Notice for Evaluation:** Default developer credentials are defined in `GroceryServer.java` (lines 17-18):
*   **Username:** `Youssef Ebrahim`
*   **Password:** `Youssef_33263051`

---

## 🏆 Project Checklist & Lab Requirements

- [x] **Core TCP Sockets:** Implements low-level thread-per-client connections using Java `ServerSocket` and `Socket` (port 5000).
- [x] **Secure GUI Authentication:** Full login GUI with error prompts for failed attempts and transition on success.
- [x] **Interactive Catalog UI:** Grid layout with quantity spinners and live calculated item total indicators.
- [x] **Distributed Checkout:** Client-side cart serialization, socket processing on server, and total validation response.
- [x] **Printed Invoices:** Real-time generation of custom text receipts on both server and client machines.
- [x] **Desktop Integration:** Native installer bundle creating standard Windows shortcuts.
- [x] **Zero-Terminal Process Management:** Background launcher hiding console window flashing for a premium consumer feel.
