# ![](https://github.com/wayfindersinghealth/WayFinder/blob/master/WayFinderIcon.png) HealthBuddy - WAYFINDER

![](https://github.com/wayfindersinghealth/WayFinder/blob/master/WayFinder%20Poster%20Dark.png)

## About WAYFINDER

This project is about WAYFINDER, an Internal Positioning System for Indoor Navigation based off Wi-Fi access points relative to the mobile device.

WAYFINDER aims to help patients and visitors alike navigate their way with ease throughout Woodlands Integrated Health Campus, providing them a seamless journey of care experience as soon as they step into the hospital, and proper navigational instructions as well as infographics to guide the users from where they are to their destination. It also allows administrators to manage current existing geolocations or learn new ones as required.

**Keywords**: Indoor GPS, WiFi Positioning, Indoor Mapping, Indoor Navigation, Indoor Positioning

# Problem Statement

As the number of citizens in Singapore aged 65 and above is increasing rapidly, more citizens are visiting healthcare facilities for healthcare treatment. As such, the government has been building more healthcare facilities to cater to the growing demands of our citizens. One such facilities is the Woodlands Integrated Health Campus, which is targeted to be operational after 2020 and it will be one of the largest hospital campus in Singapore.

We, at WAYFINDER, want our users to be able to navigate throughout Woodlands Integrated Health Campus and provide a seamless journey of care experience as soon as they step into the hospital.

As hospitals becomes larger, patients and visitors might often get lost navigating to their destination. Directory and sign boards might not be readily available at the current location of the patient or visitors. Moreover, they are mostly written in english language. Hence, patients and visitors of other races might not be able to fully understand, leading to longer queuing time and inefficiency. Furthermore, instructions provided may often be unclear or confusing.

Therefore, to better cater to the users needs, we will use FIND API Technology combine with our own calculating algorithms to help us get an accurate position on where the user is, displaying it on a Mapbox map with the indoor map. Then, using the GraphHopper API, we can accurately plot a route from the start point to the end point. Firebase will be helping us store our user personal information and the geolocation needed to pinpoint every location as well as the most optimal path the user of the app can take.

# Features

## 1\. Splash Screen

- Circular Progress View 

- Fade in Animation

## 2\. Application Introduction

- Application Introduction

## 3\. Learn

- EditText Input Location

- Learn Button Click

- Save Locations upon Learn Button Click into FireBase Database 

- ListView Locations From Database

- Learning Locations Using POST/Learn, GET/Calculate, GET/Locations

## 4\. Find Your Way

- AutoCompleteTextView To

- Retrieve Destination Locations using GET/Locations to store in AutoCompleteTextView To

- MapBox map from previous semester

- Locking of Map in a specific location

- Getting Current Locations Using POST/Track and Network Provider (Latitude and Longtitude)

- MarkerView to Mark Current Location

## 5\. Help

## 6\. Language Preference

- Alert Dialog

- Spinner 

# References

Thanks to [Circular Progress View](https://github.com/rahatarmanahmed/CircularProgressView), [Application Introduction](https://github.com/apl-devs/AppIntro), [MapBox](https://www.mapbox.com/), [FireBase](https://firebase.google.com/) for awesome open source features, maps and database.


