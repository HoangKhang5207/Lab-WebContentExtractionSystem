**Web Content Extraction System**

A robust system for extracting web resources—including images, videos, audio files, and text—from any given URL. Built with a Spring Boot backend and a ReactJS frontend, this project provides a clean API and user-friendly interface for content extraction and storage.

---

## Table of Contents

1. [Features](#features)
2. [Tech Stack](#tech-stack)
3. [Getting Started](#getting-started)

   * [Prerequisites](#prerequisites)
   * [Installation](#installation)
4. [Usage](#usage)

   * [API Endpoints](#api-endpoints)
   * [Testing](#testing)
5. [Directory Structure](#directory-structure)
---

## Features

* **Resource Extraction**: Automatically downloads images, videos, audio, and text from a given URL.
* **Extensible API**: Clean RESTful interface for integration with other systems.
* **Frontend Dashboard**: Interactive ReactJS UI for submitting URLs and viewing extraction results.
* **Error Handling**: Validates URLs and reports detailed error messages.
* **Logging & Reporting**: Saves extraction logs and summary of processed resources.

## Tech Stack

* **Backend**: Spring Boot (Java 17+), Maven
* **Frontend**: ReactJS (Node.js 18+, npm)
* **API Documentation**: Swagger UI

## Getting Started

### Prerequisites

* Java 17 or higher
* Maven
* Node.js 18 or higher
* npm or yarn

### Installation

1. **Clone the repository**

   ```bash
   git clone https://github.com/HoangKhang5207/Lab-WebContentExtractionSystem.git
   ```

2. **Backend Setup**

   ```bash
   cd web-content-extractor
   mvn clean install
   mvn spring-boot:run
   ```

   The backend will start on `http://localhost:8080/api/extract`.

3. **Frontend Setup**

   ```bash
   cd web-extractor-frontend
   npm install
   npm run dev
   ```

   The frontend will run on `http://localhost:5173/`.

## Usage

### API Endpoints

* **Extract Content**

  * **URL**: `POST /api/extract`
  * **Request Header**:

    * `Accept: */*`
    * `Content-Type: application/json`
  * **Request Body**:

    ```json
    {
      "url": "https://example.com"
    }
    ```
  * **Success Response (200 OK)**:

    ```json
    {
      "success": true,
      "message": "Extracted 15 resources",
      "totalFiles": 15,
      "resources": [
        {
          "type": "IMAGE",
          "originalUrl": "https://example.com/img/logo.png",
          "savedPath": "./webcontent/example.com/images/example_com_logo_1712345678901.png",
          "filename": "example_com_logo_1712345678901.png",
          "contentType": "image/png",
          "fileSize": 12345
        },
        ...
      ]
    }
    ```
  * **Error Response (400 Bad Request)**:

    ```json
    {
      "success": false,
      "message": "Invalid URL format",
      "totalFiles": 0,
      "resources": []
    }
    ```

#### Examples

* **Using cURL**

  ```bash
  curl -X POST "http://localhost:8080/api/extract" \
       -H "accept: */*" \
       -H "Content-Type: application/json" \
       -d '{"url":"https://www.24h.com.vn/"}'
  ```

* **Using Postman**

  1. Create a new collection named **Web Content Extractor**.
  2. Add a new **POST** request to `http://localhost:8080/api/extract`.
  3. Set **Body** to **raw (JSON)** and paste:

     ```json
     { "url": "https://example.com" }
     ```
  4. Send the request and observe the JSON response.

* **Using Swagger UI**

  1. Navigate to `http://localhost:8080/swagger-ui.html`.
  2. Find **POST /api/extract**.
  3. Click **Try it out**, enter the URL, and **Execute**.

## Testing

Test your API endpoints using the methods above (cURL, Postman, Swagger). Cover both valid and invalid URL scenarios. Ensure you verify:

* Successful extraction of resources.
* Proper handling of malformed or unreachable URLs.

## Directory Structure

```
Lab-WebContentExtractionSystem/
├─ web-content-extractor/               # Spring Boot application
│  ├─ src/main/java       # Application source code
│  ├─ src/main/resources  # Configuration files, Swagger setup        
│
├─ web-extractor-frontend/              # ReactJS application
│  ├─ src/                # Components and pages
│  └─ public/             # Static assets
│
├─ webcontent/            # Extracted files will be saved here
│
└─ README.md              # This file
```

*Last updated: July 3, 2025*
