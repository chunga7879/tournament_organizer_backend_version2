# CPSC319 Team Hairless Cats: Amazon Tournament Application Backend

## Description

This repository is for the backend of our Amazon Tournament Application

## Installation and usage

### Database setup
1. Create a PostgreSQL database called `hairless` on your local machine. No need to create any other tables, the application will automatically configure and set them up automatically.
2. Ensure that your postgresql is connected on port 5432. If you decide to use another set of configurations, you will need to modify the file `application-dev.properties` accordingly.

### Application setup
1. Install IntelliJ. But if you want to use a separate IDE, feel free to do so. IntelliJ just makes development a little more convenient.
2. Clone the repository
3. Run the `main` method in `TournamentApplication` with the options `-Dspring.profiles.active=dev` (if you use IntelliJ, you can set up the configurations to always run with this option) 

### Usage

- Send GET, POST, etc. methods to `localhost:8080/{endpoint}`
    - Refer to the API documentation for full details
