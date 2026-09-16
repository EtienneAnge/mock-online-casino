# mock-online-casino

A web-based online casino application built with **Spring Boot**, allowing users to play games of chance such as Blackjack and Roulette.

## Table of Contents

* [Overview](#overview)
* [Features](#features)
* [Requirements](#requirements)
* [Installation](#installation)
* [Configuration](#configuration)
* [Project Structure](#project-structure)
* [Architecture](#architecture)
* [Technologies Used](#technologies-used)
* [Usage](#usage)
* [API Endpoints](#api-endpoints)

## Overview

mock-online-casino is a multiplayer online casino platform where users can:

* Create an account and log in
* Join game rooms
* Play Blackjack and Roulette
* View their statistics and transactions
* Manage their user account

## Features

* **Secure Authentication**: Login/signup system with session management
* **Casino Games**:

  * **Blackjack**: Classic game with multiple seats
  * **Roulette**: Roulette game with a betting system
* **Game Rooms**: Create and join rooms with access codes
* **Bet Management**: Betting lock system
* **User Account**: Statistics tracking and transaction history
* **H2 Database**: In-memory database for development
* **Security**: Admin filtering, session interceptors

## Requirements

* **Java 17** or higher
* **Maven 3.6+**
* A modern web browser (Chrome, Firefox, Safari, Edge)

## Installation

1. **Clone the repository**:

   ```bash
   git clone https://github.com/CasYnoRoyal/mock-online-casino.git
   cd mock-online-casino
   ```
2. **Build the project** with Maven:

   ```bash
   mvn clean install
   ```
3. **Start the application**:

   ```bash
   mvn spring-boot:run
   ```
4. Open your browser and go to `http://localhost:8080`

## Configuration

* **Configuration file**: `src/main/resources/application.properties`
* **Default port**: 8080 (can be changed in the configuration file)
* **Database**: Configured to use H2 in-memory database by default

## Project Structure

* `src/main/java`: Contains the Java source code
* `src/main/resources`: Contains configuration files and static resources
* `src/test/java`: Contains unit and integration tests

## Architecture

The architecture of mock-online-casino is based on the MVC (Model-View-Controller) pattern:

* **Model**: Represents the data and business logic (e.g., JPA entities, services)
* **View**: Represents the user interface (e.g., HTML, CSS, JavaScript files)
* **Controller**: Handles HTTP requests and sends them to the appropriate services

## Technologies Used

* **Spring Boot**: Main framework used to build the application
* **Spring Security**: Used for security and user management
* **Spring Data JPA**: Used for data access and management
* **Thymeleaf**: Template engine used to render HTML views
* **Bootstrap**: Used for design and responsive layout
* **H2 Database**: In-memory database for development and testing

## Usage

* **Create an account**: Fill in the signup form with a username, email, and password
* **Log in**: Use your credentials to log in
* **Join a room**: Enter the room access code to join
* **Play**: Select a game (Blackjack or Roulette) and start playing
* **View your statistics**: Go to the "My Account" section to view your statistics and transactions

## API Endpoints

* **POST /api/auth/signup**: Register a new user
* **POST /api/auth/login**: Authenticate a user
* **GET /api/games/blackjack**: Get Blackjack game data
* **GET /api/games/roulette**: Get Roulette game data
* **POST /api/games/bet**: Place a bet on a game
* **GET /api/user/stats**: Get the statistics of the logged-in user
* **GET /api/user/transactions**: Get the transaction history of the logged-in user
