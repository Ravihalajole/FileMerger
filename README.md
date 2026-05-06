# File Merger for Android
> **A high-performance, One UI-inspired utility for source code consolidation.**

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=java&logoColor=white)](https://www.oracle.com/java/)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=flat-square&logo=android&logoColor=white)](https://developer.android.com/)
[![License](https://img.shields.io/badge/License-MIT-3E91FF?style=flat-square)](https://opensource.org/licenses/MIT)
[![Build](https://img.shields.io/badge/Build-v1.0.0--stable-brightgreen?style=flat-square)](#)

---

## 📌 Overview
**File Merger** is a specialized Android tool built for developers who need to consolidate complex project structures into single, readable documents. Designed with the **Samsung One UI** design language, it offers a native-feeling experience while providing heavy-duty file processing capabilities.

### Why File Merger?
In a mobile-first development environment (like AndroidIDE), reviewing large codebases is difficult. File Merger flattens your project into a single `.txt` or `.md` file, perfect for code audits, LLM context windows, or offline reading.



---

## ✨ Core Competencies

* **Recursive Deep-Scan:** Efficiently traverses nested directory trees without memory overhead.
* **Scoped Storage Implementation:** Full compliance with Android 11+ storage policies using `DocumentFile` and `OpenDocumentTree`.
* **Threaded Processing:** Utilizes `ExecutorService` for non-blocking I/O, ensuring the UI remains responsive during large merge tasks.
* **Native One UI Aesthetic:** Implements reachability patterns, pure OLED black backgrounds, and Samsung-standard typography.

---

## 🛠 Technical Stack

| Category | Technology |
| :--- | :--- |
| **Language** | Java 17 (LTS) |
| **UI Engine** | Material 3 / One UI Components |
| **Min SDK** | API 21 (Android 5.0) |
| **Architecture** | Imperative with Background Concurrency |
| **Build System** | Gradle 8.0+ |

---

## 📸 Interface Preview

<p align="center">
  <img src="docs/main.png" width="32%" alt="Main UI" />
  <img src="docs/main1.png" width="32%" alt="Input" />
</p>

---

## 🚀 Quick Start

### Build Requirements
Ensure you have the latest **AndroidIDE** or **Android Studio** installed with the following:
* JDK 17
* Android SDK 34 (Upside Down Cake)

### Installation
1.  **Clone the Repository**
    ```bash
    git clone [https://github.com/org-ravi/file-merger-android.git](https://github.com/org-ravi/file-merger-android.git)
    ```
2.  **Synchronize Gradle**
    The project uses Version Catalogs (`libs.versions.toml`) for modern dependency management.
3.  **Execute Build**
    ```bash
    ./gradlew assembleDebug
    ```

---

## 📖 Developer Documentation
Detailed logic regarding URI persistence and `FileProcessor` implementation can be found in the Javadoc:
```bash
./gradlew javadoc
