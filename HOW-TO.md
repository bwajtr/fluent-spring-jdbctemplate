# How to

## How to do a release to maven central

0. Make sure you are on correct branch (for new micro (1.0.0 -> 1.1.0) versions create separate branch like 1.0.x)
0. Assuming SNAPSHOT version in pom.xml
0. Run "mvn release:clean release:prepare"
0. If successful run "mvn release:perform"
0. If build is successful, your new build should be available in maven central

