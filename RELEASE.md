# Create Release Information

As an example I'll use the version 0.3.1 as the version to release.

To create a release of this application component do the following:


1. commit & push your changes to Github
2. wait for travis to be build
3a. `./gradlew release -Prelease.useAutomaticVersion=true` to update to next patch version (0.3.2)
3b.  `./gradlew release -Prelease.useAutomaticVersion=true -Prelease.releaseVersion=0.4.0 -Prelease.newVersion=0.4.1-SNAPSHOT` to update to next minor version (0.4.0)
4. `hub release create -m "version 0.3.1"`