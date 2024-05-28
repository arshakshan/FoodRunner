# Food Delivery App

## Introduction
This project is a food delivery application built with Kotlin and REST API. The app allows users to browse restaurants, view menus, place orders, and manage their profiles. It provides a user-friendly interface and includes various functionalities to enhance the user experience.

## Features
- Welcome Page: Displays the app logo and name.
- Login Page: Allows users to log in with their mobile number and password.
- Registration Page: Enables new users to sign up for the app.
- Forgot Password Page: Helps users reset their password through OTP verification.
- Navigation Drawer: Provides easy access to different sections of the app such as Home, User Profile, Favorite Restaurants, Order History, FAQs, and Logout.
- Restaurant List: Fetches and displays a list of restaurants from the internet.
User Profile: Shows user's name, phone number, and address.
- Favorites: Lists all favorite restaurants.
- Order History: Displays previously placed orders.
- FAQs: Contains frequently asked questions.
- Logout: Logs the user out and redirects to the login page.
- Restaurant Details: Shows menu items, prices, and options to add items to the cart.
- Cart: Lists items added to the cart and displays the total amount to be paid.

## Screenshots
(Include relevant screenshots of your app)

## Installation

Clone the repository:
```bash
git clone https://github.com/your-username/food-delivery-app.git
```

- Open the project in Android Studio.
- Build and run the app on an emulator or physical device.

## Usage
1. Welcome Page: The app starts with a welcome page displaying the app logo/name for 1 second before navigating to the login page.
2. Login Page: Users can log in using their mobile number and password. If not registered, users can navigate to the registration page.
3. Registration Page: New users can register by providing their name, email address, mobile number, delivery address, and password.
4. Forgot Password Page: Users can reset their password by entering their registered mobile number and receiving an OTP via email.
5. Navigation Drawer: Accessible from any screen, providing quick access to different sections of the app.
6. Restaurant List: Displays a list of restaurants fetched from the server. Users can sort the list by cost and rating.
7. Restaurant Details: Users can view the menu of a selected restaurant and add items to their cart.
8. Cart: Shows items added to the cart with prices and the total amount. Users can place orders from this page.
9. User Profile: Displays user details, which are not editable.
10. Favorites: Lists all favorite restaurants saved locally.
11. Order History: Shows a list of previously placed orders fetched from the server.
12. FAQs: Contains static questions and answers relevant to the app.
13. Logout: Logs the user out and clears stored preferences.

## API Endpoints
- Login: http://13.235.250.119/v2/login/fetch_result
- Register: http://13.235.250.119/v2/register/fetch_result
- Forgot Password: http://13.235.250.119/v2/forgot_password/fetch_result
- Reset Password: http://13.235.250.119/v2/reset_password/fetch_result
- Fetch Restaurants: http://13.235.250.119/v2/restaurants/fetch_result
- Fetch Restaurant Details: http://13.235.250.119/v2/restaurants/fetch_result/{restaurant_id}
- Place Order: http://13.235.250.119/v2/place_order/fetch_result
- Order History: http://13.235.250.119/v2/orders/fetch_result/{user_id}

## Technologies Used
- Kotlin
- REST API
- Android Studio
- RoomDB

## License
This project is licensed under the MIT License - see the LICENSE file for details.