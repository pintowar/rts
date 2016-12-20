# Running this application

This profile also defines a set of custom Gradle tasks, including a bootRun task to startup the client app. You can either start up the server and client apps separately:

```bash
./gradlew server:bootRun

# in another terminal
./gradlew client:bootRun
```

Or you can take advantage of Gradle’s parallel execution to run both client and server apps in a single command:

```bash
./gradlew bootRun -parallel
```

Other tasks defined in client wrap the create-react-app scripts for building and testing the React app. You can run them with the Gradle wrapper, or run the npm scripts directly if you have npm installed.

```bash
./gradlew client:test #or, from the client project dir: npm test

./gradlew client:build #or, from the client project dir: npm run build
```


Again, please see the create-react-app documentation for more information on leveraging these scripts and the other features provided by `create-react-app`.
