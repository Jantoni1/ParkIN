# ParkIN

Simple CRUD app which helps parking space owners manage the parking space.


## General system requirements
**Functional requirements**
* System allows parking worker to access all the information that is needed to perform the job from within the application
* Worker can check the current state of parking spots from within the application
* System automatically calculates the price for vehicles leaving the parking and displays it

**Non-functional requirements**
* System is accessible from any computer or mobile phone with internet access
* System handles and resolves all the worker requests in reasonable time
* System retains data from at least last 30 days
* User interface shall be easy to use even for non tech-savvy workers

## Architecture
![image](https://i.imgur.com/UVcFJKr.png)

**Backend architecture description**
* Java server app based on Spring framework
* HTML content served by Thymeleaf template engine
* Access to static content using URLs
* Uses REST for dynamic content
* Server sends JSON messages in response to frontend’s requests
* Application data is stored in relational database (PostgreSQL or other similar, we're using H2 for presentational purposes)

**Frontend architecture description**
* Connected with backend via JSON messages
* Basic data is served as static HTML
* Events handled asynchronously using jQuery to connect with the backend
* User interface created with Bootstrap framework
* Parking usage diagrams created with Chart.js library

## Technologies
**Backend technologies:**
* Java
* Spring Boot
* Spring MVC
* Thymeleaf
* Relational database (PostgreSQL)

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

## Quick start guide
To start using the application you have to:
* Download and install PostgreSQL 9.x and create database "parkin"
* Clone or download latest version from [GitHub](https://github.com/Jantoni1/ParkIN) and import project into IntelliJ IDEA
* Edit these lines in configuration file (application.properties)
```properties
#web interface port
server.port=80
#database URL
spring.datasource.url=jdbc:postgresql://localhost:5432/parkin
#database login
spring.datasource.username=your_login
#database password
spring.datasource.password=your_password
```
* Setup your parking lot by editing default database entries inside data.sql file
```sql
--values: 0, fee/h (basic period), basic period, fee/h (extended period)
insert into tariffs values(0, 10, 2, 8) on conflict do nothing;
--values: 0, 'capacity', parking lot capacity
insert into configurations values(0, 'capacity', 20) on conflict do nothing
```
* Run maven `mvn clean install` to build application
* Run `java -jar filename.jar` in target directory, where `filename` is name of the generated artifact. Database schema will be created for you at the first startup
* Open your web browser and go to your hostname or IP address at port specified in properties

## User interaction
ParkIN system provides legible and easy-to-use web user interface. This type of GUI was chosen because of its simplicity and possibility of accessing it using any kind of device with Internet access.

1. Accessing information about parking area
    * Main page consists of basic information about current status of parking area and is updated every time car enters or leaves
    * Basic status info consists of capacity, number of free and taken spots
    
    ![image](https://i.imgur.com/B27reo5.png)

2. Registering cars
    * Car register form is located in the same GUI view as parking areas status
    * Car can enter parking area after being registered by attendant
    
3. Charging leaving cars
    * Payment value is automatically calculated
    
    ![image](https://i.imgur.com/lfrdfpG.png)
    
4. Creating new tariff
    * User can create new tariff in Tariff section
    * To create new tariff, at least one value must differ from the previous tariff
    
    ![image](https://i.imgur.com/4CsO8NG.png)
    
5. Displaying financial statistics
    * User can access statistics in Statistics section
    * Statistics from current day and current month (up to current day) are displayed
    
    ![image](https://i.imgur.com/w28ME41.png)
