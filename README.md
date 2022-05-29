# SoPra FS22 - Group 07 - Date Against Humanity - Client

Date against humanity helps you meet people with the same sense of humour and allows you to network with others in a fun way.

The core principle is simple and based on [Cards Against Humanity](https://www.cardsagainsthumanity.com/).
Each User gets to choose a Black Card every 24 hours which then gets displayed in their profile.
The Black Cards have some text with a gap. Next to the Black Cards each user also gets a fixed number of White Cards whenever a new Black Card is chosen.
The White Cards have some short text or phrase which gets used to fill in the gaps in the Black Cards in a funny or witty way.
Users can rate the White Cards which get played to fill the gap in their Black Card and if both Users like each other's cards they match.
When matched, users can chat with each other. User can also unmatch or block previously matched users.
Users can set preferences such as gender, age or a maximum distance which restricts whose Black Cards get presented to them.

### Links:
- Server: https://sopra-fs22-group-07-server.herokuapp.com/
- Client: https://sopra-fs22-group07-client.herokuapp.com/
- Source Code and Project board: https://github.com/orgs/sopra-fs22-group-07


# Technologies

## Client
For the client, React and JSX as well as the package manager npm are used.
- Geolocation API: In order to retrieve location data about users we used `navigator.geolocation` [Geolocation API](https://developer.mozilla.org/en-US/docs/Web/API/Geolocation_API) in order to access the browser geolocation information. This API uses the best available information to the browser in order to determine the location.
- DiceBear API: For the Match Overview and the Chat, the Avatar Generator from https://avatars.dicebear.com/ is used
  to create custom Avatar for every user
- React Leaflet API: As map service react leaflet is used. It provides building between React and Leaflet.
  Leaflet is an open-source JavaScript library for interactive maps. As source for the maps, Openstreetmap is used.


## Server
For the server, we created a spring boot application with Java. We also used the build automation tool Gradle.

- Database: A PostgreSQL database - fully managed from HEROKU - is used. At the moment, the connections are limited to 20, but it is possible to upgrade when needed.

# High level components

## User
The user is one of our main components, as it is the component that the player (or user) of the application impersonates.
The user model has a lot of responsibilities, ranging from having an age, gender and location and preferences
up to having cards to play and to be played on.

Main Classes User:
```
..\soprafs22\entity\User.java
..\soprafs22\controller\UserController.java
..\soprafs22\service\UserService.java 
```


## Cards
The template for all our cards are from [JSON Agains Humanity](https://crhallberg.com/cah/) which uses cards from the card game [Cards Against Humanity](https://www.cardsagainsthumanity.com/).



There exists two types of cards: White Cards and Black Cards. Every 24 hours, each user gets a set of White Cards and one Black
Card. The White Cards then can be played on other users' Black Cards. This realtime interaction is used for the
matching. When two users like each other's White Cards, a match is created. Black Cards of one user only get presented
to another user if it fits their preferences.

Main Classes Cards:
```
..\soprafs22\service\CardService.java
..\soprafs22\controller\GameController.java
..\soprafs22\entity\Game.java
..\soprafs22\entity\WhiteCard.java
..\soprafs22\entity\BlackCard.java 
```

## Chat
The chat feature allows for users that are matched to interact with each other in real time. We built our own chat for this.
Users can send and receive messages from other users they matched with in real time. A user has a chat overview, where they
can see recent messages and all their matches. A user also has the possibility to block or unmatch any previously matched user.

Main Classes Chat:
```
..\soprafs22\entity\Chat.java
..\soprafs22\controller\ChatController.java
..\soprafs22\entity\Match.java
```


# Launch & Deployment:

## Server
### Building with Gradle

You can use the local Gradle Wrapper to build the application. If you don't have a local PotgreSQL database, and you use H2
in memory instead, you have to work with the local branch, in order for the SQL queries to work.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html)
and [Gradle](https://gradle.org/docs/).

#### Build
To build the application:
```bash
./gradlew build
```

#### Run
To run the application:
```bash
./gradlew bootRun
```

#### Test
To run the tests:
```bash
./gradlew test
```
### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

### API Endpoint Testing: Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

### Debugging

If something is not working and/or you don't know what is going on. We highly recommend that you use a debugger and step
through the process step-by-step.

To configure a debugger for SpringBoot's Tomcat servlet (i.e. the process you start with `./gradlew bootRun` command),
do the following:

1. Open Tab: **Run**/Edit Configurations
2. Add a new Remote Configuration and name it properly
3. Start the Server in Debug mode: `./gradlew bootRun --debug-jvm`
4. Press `Shift + F9` or the use **Run**/Debug"Name of your task"
5. Set breakpoints in the application where you need it
6. Step through the process one step at a time

### Setup this Project with your IDE of Your Choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and
make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

## Client
### Prerequisites and Installation
For your local development environment, you will need Node.js. You can download it [here](https://nodejs.org). All other dependencies, including React, get installed with:

```npm install```

Run this command before you start your application for the first time.
If there are any vulnerabilities be sure to run:

```npm audit fix```

Next, you can start the app with:

```npm run dev```

Now you can open [http://localhost:3000](http://localhost:3000) to view it in the browser.

Notice that the page will reload if you make any edits. You will also see any lint errors in the console (use Google Chrome).

### Build
Finally, `npm run build` builds the app for production to the `build` folder.<br>
It correctly bundles React in production mode and optimizes the build for the best performance: the build is minified, and the filenames include hashes.<br>
See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.


# Roadmap
## Add Black Cards with Multiple Gaps
Currently, only Black Cards with one gap are implemented, yet in Cards against Humanity there exist
Black Cards with up to 3 gaps. Add the possibility for Black Cards with two and three gaps, adjust
the drawing of cards so playing on a multiple gap Black Card doesn't mean getting to play
on less Black Cards on that day and make sure users with only 1 (or 2) White Cards left in their Hand
don't have to play on Black Cards with 2 (or 3) gaps.

## Just for Fun Mode
Add a separate mode where each user has an infinite amount of White Cards to play
per day. There is no matching in this mode as it is played purely out of fun. Implement a
scoreboard where the like/dislike ratio for players gets displayed or even consider adding a
feature where you rate White Cards on a scale from 1-10 in the just for fun mode.

## Family Friendly Filter
Allow users to set a filter to only receive cards from the "Family Friendly" version of the Game.
Users who set this filter only receive Black and White Cards from these packs and only get shown
Black Cards of Users who also have this filter activated. Users who don't have this filter activated can receive cards
from the "Family Friendly" edition, but they cannot play on Black Cards from users who have the filter on.

# Authors and acknowledgement
## Authors
- [Seraina Schraff](mailto:seraina.schraff@uzh.ch), 20-710-513
- [Andreas Huwiler](mailto:andreas.huwiler@uzh.ch) , 13-921-234
- [Laurin van den Bergh](mailto:laurin.vandenberg@uzh.ch), 16-744-401
- [David Moser](mailto:david.moser2@uzh.ch), 19-923-929
- [Joe Müller](mailto:joe.mueller@uzh.ch), 19-735-299

## Acknowledgement
- The server is built on the SoPra RESTful Service Template FS22: https://github.com/HASEL-UZH/sopra-fs22-template-server
- The client is built on the SoPra Client Template FS22: https://github.com/HASEL-UZH/sopra-fs22-template-client
- Cards Against JSON: https://crhallberg.com/cah/
- Cards Against Humanity: https://www.cardsagainsthumanity.com/

# Licence

This project based on the work of [Cards Agains Humanity](https://www.cardsagainsthumanity.com/) and [JSON Against Humanity](https://www.crhallberg.com/cah/), and is licensed under the [Creative Commons BY-NC-SA 4.0 license](https://creativecommons.org/licenses/by-nc-sa/4.0/) (as are the afore mentioned projects).
