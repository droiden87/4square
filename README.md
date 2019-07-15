This application was created as an example application that will allow you to search for venues close to your current location.  It is an example of how to use navigation, calling and parsing from an API using Retrofit and GSON, permission request handling, and last but not least using Google maps to show locations to a user.

## To Run the application
This project uses the Gradle build system. To build this project, use the gradlew build command or use "Import Project" in Android Studio.

# Screens

The first screen will allow you to search for venues near your current location and displays them in a list.  It includes some information about the place and how far it is from you. Tapping on a venue will take you to the detail screen.

<img src="/images/home_scr.png" width="318" height="617" />

The details screen will display a pin on the map indicating your current position on the top half along with venues details.  This screen also shows some information about the selected venue (rating, address and a button to navigate to their website if its available), phone call capability.

<img src="/images/details_scr.png" width="318" height="636" />

## Libraries Used
  * [LiveData][13] - Build data objects that notify views when the underlying database changes.
  * [ViewModel][17] - Store UI-related data that isn't destroyed on app rotations.
  * [Google Maps][18] - static and dynamic maps, Street View imagery, and 360° views
  * [Glide][90] - for asynchronous image loading
  * [Retrofit][91] - for making requests and recieveing data from a RESTful API
  * [OKHttp][92] - for use with Retrofit as the HTTP client
  * [RxJava][93] - a library for composing asynchronous and event-based programs using observable sequences

[0]: https://developer.android.com/jetpack/foundation/
[13]: https://developer.android.com/topic/libraries/architecture/livedata
[17]: https://developer.android.com/topic/libraries/architecture/viewmodel
[18]: https://developers.google.com/maps/documentation/
[90]: https://bumptech.github.io/glide/
[91]: https://square.github.io/retrofit/
[92]: https://square.github.io/okhttp/
[93]: https://github.com/ReactiveX/RxJava
