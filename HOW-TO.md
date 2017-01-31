# How to

## How to do a release to maven central

0. Make sure you are on correct branch
0. Set correct version in the pom.xml and commit
0. Tag the commit with version tag (e.g. v1.0.1)
0. Do "mvn clean deploy -P release"
0. If build is successful, your new build is available in maven central

