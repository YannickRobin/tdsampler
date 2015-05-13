# Start with Eclipse
* Select `com.ebizance.tdsampler.TDSampler`
  * Arguments : `<path to the thread dump folder>`
  * Environment
```
    log4j.configuration=src/main/conf/log4j.properties
    tdsampler.configuration=src/main/conf/conf.properties
```

# New release
* Commit all your changes
* `mvn -Dusername=[svnusername] -Dpassword=[svnpassword] release:prepare`
* `mvn release:perform`
* Upload `TDSampler/target/checkout/target/TDSampler-x.x-bin.zip`
