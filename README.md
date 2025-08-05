# ConsultMe

[![Android CI](https://github.com/Tarek-Bohdima/ConsultMe/actions/workflows/android_ci.yml/badge.svg)](https://github.com/Tarek-Bohdima/ConsultMe/actions/workflows/android_ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Platform](https://img.shields.io/badge/platform-android-green.svg)
![Min API](https://img.shields.io/badge/Min%20API-25-purple)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com)


**ConsultMe** is a template project for Jetpack Compose applications, featuring integrated tools for code quality and automation. It includes:

- **Spotless:** Automated code formatting and linting
- **Detect:** Static code analysis
- **Lint:** Kotlin and Compose code linting

## Features

- Fully configured for Jetpack Compose and a multi-module architecture.
- Code quality tools included and pre-configured.
- 100% Kotlin codebase, using Coroutines and Flow.
- Dependency injection with Hilt.

## Getting Started

Do not clone this repository directly. The recommended way to use this template is to create your own repository from it.

1.  Click the **Use this template** button on the main repository page and select **Create a new repository**.
2.  Give your new project a name and description. This creates a completely new and independent repository.
3.  Clone your new repository to your local machine and open it in Android Studio.
4.  Follow the instructions in the **"How to Rename and Refactor"** section below to customize it for your project.

> **Note on Forking:** If your intention is to contribute changes back to this template, you should fork the repository instead.

## How to Rename and Refactor

After creating your new repository, you need to update the project's identity. Use Android Studio's **Refactor > Rename** tool for safety and reliability.

1.  **Project Name:** In `settings.gradle.kts`, change `rootProject.name`.
2.  **Application ID & Namespaces:** In `app/build.gradle.kts` and the `build.gradle.kts` of each library module, change the `namespace` and `applicationId` from `com.thecompany.consultme` to your new ID.
3.  **Package Name:** Use Android Studio's refactoring tool to rename the `com.thecompany.consultme` package.
4.  **App Display Name:** In `app/src/main/res/values/strings.xml`, change the `app_name` string.
5.  **Update License File:** Open the `LICENSE` file in the root directory and replace `[year]` and `[your name or organization]` with your own information.
6.  **Clean Up:** Delete the example code in the `:feature-chat` module to start building your own features and fix MainActivity in app module accordingly.

## Code Quality

- **Spotless**: Ensures consistent code formatting.
- **Detect**: Finds common code issues.
- **Lint**: Enforces Kotlin and Compose best practices.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.
