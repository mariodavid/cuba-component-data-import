# Create Release Information

As an example I'll use the version 0.3.1 as the version to release.

To create a release of this application component do the following:


1. commit & push your changes to Github
2. wait for travis to be build
3. switch isSnapshot in build.gradle to false (make sure it is currently 0.3.1, isSnapshot = true)
4. commit & push your changes to Github
5. ./tag-release.sh 0.3.1 (which creates a git tag and pushes it to Github)
6. Update the title "version 0.3.1" and description of the tag in the Github UI
7. ./upload-to-repository.sh <<MY_REPO_USERNAME>> <<MY_REPO_PASSWORD>> (optional <<MY_REPO_URL>> as third parameter, otherwise will use bintray)

