# SoPra FS22 - Group 07 - Date Against Humanity - Server

##Introduction: Date against humanity
Date against humanity helps you meet people with the same sense of humour and allows you to network with others in a fun way.

The core principle is simple and based on Cards Against Humanity (https://www.cardsagainsthumanity.com/).
Each User gets to choose a Black Card every 24 hours which then gets displayed in their profile.
The Black Cards have some text with a gap. Next to the Black Cards each user also gets a fixed number of White Cards whenever a new Black Card is chosen.
The White Cards have some short text or phrase which gets used to fill in the gaps in the Black Cards in a funny or witty way.
Users can rate the White Cards which get played to fill the gap in their Black Card and if both Users like each other's cards they match.
When matched users can chat with each other. User can also unmatch or block previously matched users.
Users can set preferences such as gender, age or a maximum distance which restricts whose Black Cards get presented to them.

###Links:
server: https://sopra-fs22-group-07-server.herokuapp.com/


client: https://sopra-fs22-group07-client.herokuapp.com/


Source Code and Project board: https://github.com/orgs/sopra-fs22-group-07


## Technologies

###Client
For the client we used React and JSX aswell as the package manager npm

###Server
For the server we created a springboot application with java. We also used the build automation tool Gradle.


####Database: Andy

####Geolocation API:Laurin

####Little Faces API: (only maybe) David

####Google Maps API: (only maybe) Andy

## High level components

###User
The user is one of our main component, as it is the component that the player (or user) of the application impersonates.
The user model has a lot of responsibilities, ranging from having an age, gender and location and preferences for all of them
up to having cards to play and to be played on.

######main classes user:
    ..\soprafs22\entity\User.java
    ..\soprafs22\controller\UserController.java
    ..\soprafs22\service\UserService.java


###Cards
The template for all our cards are from https://crhallberg.com/cah/ which uses cards from the card game
Cards Against Humanity (https://www.cardsagainsthumanity.com/).



There exists two type of cards: White Cards and Black Cards. Every 24 hours each user gets a set of White Cards and one Black
Card. The White Cards then get played on other users Black Cards. This realtime interaction then gets used for the
matching. When two users like each others' White Cards a match gets created. Black Cards of one user only get presented
to another user if it fits their preferences.
######main classes cards:
    ..\soprafs22\service\CardService.java
    ..\soprafs22\controller\GameController.java
    ..\soprafs22\entity\Game.java
    ..\soprafs22\entity\WhiteCard.java
    ..\soprafs22\entity\BlackCard.java

###Chat
The chat feature allows for users that are matched to interact with each other in real time. We built our own chat for this.
Users can send and receive messages from other users they matched with in real time. A user has a chat overview, where they
can see recent messages and all their matches. A user also has the possibility to block or unmatch any previously matched user.

######main classes chat:
    ..\soprafs22\entity\Chat.java
    ..\soprafs22\controller\ChatController.java
    ..\soprafs22\entity\Match.java


## Launch & Deployment:

###Server
#### Building with Gradle

You can use the local Gradle Wrapper to build the application.
-   macOS: `./gradlew`
-   Linux: `./gradlew`
-   Windows: `./gradlew.bat`

More Information about [Gradle Wrapper](https://docs.gradle.org/current/userguide/gradle_wrapper.html) and [Gradle](https://gradle.org/docs/).

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
#### Development Mode

You can start the backend in development mode, this will automatically trigger a new build and reload the application
once the content of a file has been changed and you save the file.

Start two terminal windows and run:

`./gradlew build --continuous`

and in the other one:

`./gradlew bootRun`

If you want to avoid running all tests with every change, use the following command instead:

`./gradlew build --continuous -xtest`

#### API Endpoint Testing: Postman

-   We highly recommend to use [Postman](https://www.getpostman.com) in order to test your API Endpoints.

#### Debugging

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

#### Setup this Project with your IDE of choice

Download your IDE of choice: (e.g., [Eclipse](http://www.eclipse.org/downloads/), [IntelliJ](https://www.jetbrains.com/idea/download/)), [Visual Studio Code](https://code.visualstudio.com/) and
make sure Java 15 is installed on your system (for Windows-users, please make sure your JAVA_HOME environment variable is set to the correct version of Java).

###Client
#### Prerequisites and Installation
For your local development environment, you will need Node.js. You can download it [here](https://nodejs.org). All other dependencies, including React, get installed with:

```npm install```

Run this command before you start your application for the first time.
If there are any vulnerabilities be sure to run:

```npm audit fix```

Next, you can start the app with:

```npm run dev```

Now you can open [http://localhost:3000](http://localhost:3000) to view it in the browser.

Notice that the page will reload if you make any edits. You will also see any lint errors in the console (use Google Chrome).

#### Build
Finally, `npm run build` builds the app for production to the `build` folder.<br>
It correctly bundles React in production mode and optimizes the build for the best performance: the build is minified, and the filenames include hashes.<br>
See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

## Illustrations

## Roadmap
###Add Black Cards with multiple gaps
Currently only Black Cards with one gap are implemented but in Cards against Humanity there exist
Black Cards with up to 3 Gaps. Add the possibility for Black Cards with two and three gaps, adjust
the drawing of cards so playing on a multiple gap Black Card doesn't mean getting to play
on less Black Cards on that day and make sure users with only 1 (or 2) White Cards left in their Hand
don't have to play on Black Cards with 2 (or 3) gaps.

###Just for Fun Mode
Add a seperate mode where each user has an infinite amount of White Cards to play
per day. There is no matching in this mode as it is played purely out of fun. Implement a
scoreboard where the like/dislike ratio for players gets displayed or even consider adding a
feature where you rate White Cards on a scale from 1-10 in the just for fun mode.

###Family Friendly Filter
Allow users to set a filter to only recieve cards from the "Family Friendly" version of the Game.
Users who set this filter only recieve Black- and White Cards from these packs and only get shown
Black Cards of Users who also have this filter activated. Users who don't have this filter activated can recieve cards
from the "Family Friendly" edition but they can't play on Black Cards from users who have the filter on.

## Authors and acknowledgement
###Authors
Seraina Schraff, seraina.schraff@uzh.ch, 20-710-513

Andreas Huwiler, andreas.huwiler@uzh.ch , 13-921-234

Laurin van den Bergh, laurin.vandenberg@uzh.ch, 16-744-401

David Moser, david.moser2@uzh.ch, 19-923-929

Joe Müller, joe.mueller@uzh.ch, 19-735-299

###Acknowledgement
The server is built on the SoPra RESTful Service Template FS22: https://github.com/HASEL-UZH/sopra-fs22-template-server

The client is built on the SoPra Client Template FS22: https://github.com/HASEL-UZH/sopra-fs22-template-client

Cards Against JSON: https://crhallberg.com/cah/

Cards Against Humanity: https://www.cardsagainsthumanity.com/
##Licence
We would use the:
######Creative Commons BY-NC-SA 4.0 license
as we are using Cards Against Humanity as well as Cards Against JSON content.

