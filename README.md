# ParkIN

Simple CRUD app which helps parking space owners manage the parking space.


## General system requirements
**Functional requirements**
* System allows parking workers to access all the information that is needed to perform the job from within the application
* System will be accessible to parking workers who possess the login credentials
* Workers can check the current state of parking spots from within the application
* Workers can reserve parking spots using the application
* System automatically calculates the price for vehicles leaving the parking and displays it to the workers


**Non-functional requirements**
* System is accessible from any computer with internet access
* System handles and resolves all the worker requests in reasonable time
* System retains data from at least last 30 days
* User interface shall be easy to use even for non tech-savvy workers

## Architecture
![image](https://i.imgur.com/UVcFJKr.png)

**Backend architecture description**
* Java server app based on Spring MVC framework
* HTML content served by Thymeleaf template engine
* Access to static content using URLs
* Server sends JSON messages in response to frontend’s requests
* Application data is stored in relational database (PostgreSQL, H2 or other similar)

**Frontend architecture description**
* Connected with backend via JSON messages
* Basic data is served as static HTML
* Events handled asynchronously using jQuery to connect with the backend
* User interface created with Bootstrap framework
* Parking usage diagrams created with Chart.js library (or equivalent)

## Technologies
**Backend technologies:**
* Java
* Spring Boot
* Spring MVC
* Thymeleaf
* Relational DB (PostgreSQL)

**Frontend technologies:**
* HTML/CSS/Javascript
* Bootstrap
* jQuery
* Chart.js

**Development cycle tools:**
* Jenkins
* GitHub
* Google Cloud
* YouTrack
* IntelliJ IDEA

## User interaction
User can interact with ParkIn parking management system using web interface. This type of GUI was chosen because of its simplicity and possibility of accessing it using any kind of device with Internet access

1. Logging in
    * User has to open Internet browser and open ParkIn system website
    * User has to enter credentials and confirm by pressing LOGIN button
    * Every login attempt is logged on application server

2. Accessing information about parking area
    * In order to access information user must be logged in
    * spring.controller.Main view consists of basic information about current status of parking area and is updated every time car enters or leaves
    * Basic status info consists of capacity, number of free and taken spots

3. Registering cars
    * Car register form is located in the same GUI view as parking areas status
    * Car can enter parking area after being registered by attendant
    
4. Charging leaving cars
    * Payment value is automatically calculated


**Basic login page concept**

![image](https://i.imgur.com/p9NWVeX.png)


**Basic data view concept**

![image](https://i.imgur.com/KiVpHXE.png)

## User stories

### Story #1 - Employee logs into the system
As an employee working at the parking, I want to be able to log into the system from the main page, so that I can access information I need to do my job

**Acceptance criteria:**
* Employees can enter login credentials in the login form which pops up when they enter the website
* Data entered into the login form has to be safely validated
* After processing the data in the form, success or failure messages shall be generated depending on the outcome of validation
* Employees can successfully log into the system

**Definition of done:**
* Passes unit and integration tests

### Story #2 - Employee checks parking status information
As an employee working at the parking, I want to be able to access parking status information, so that I can be aware of current situation at the lot

**Acceptance criteria:**
* Logged in employees can access parking lot information
* Allow further access into the system from the lot information section

**Definition of done:**
* Passes unit and integration tests
* Feature is connected with the project's database

### Story #3 - Employee has access to more specific parking information
As an employee working at the parking, I want to be able to access specific information about the cars, so that I can see when they arrived

**Acceptance criteria:**
* Logged in employees can access more specific lot information
* Employees can see list of all parked cars with their arrival time next to them

**Definition of done:**
* Passes unit and integration tests
* Feature is connected with the project's database

### Story #4 - Employee checks the cost of parking for a car leaving the lot
As an employee working at the parking, I want to be able to see the amount a car leaving the lot should pay, so that I can charge them accordingly

**Acceptance criteria:**
* Logged in employees have access to the payment subsite
* The subsite automatically fills the data of leaving car
* After processing the data in the form, success or failure messages shall be generated depending on the outcome of validation
* After successful car departure, corresponding database entry shall be created

**Definition of done:**
* Passes unit and integration tests
* Feature is connected with the project's database