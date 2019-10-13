[![Coverage](https://codecov.io/gh/nwillc/kretry/branch/master/graphs/badge.svg?branch=master)](https://codecov.io/gh/nwillc/kretry)
[![license](https://img.shields.io/github/license/nwillc/kretry.svg)](https://tldrlegal.com/license/-isc-license)
[![Travis](https://img.shields.io/travis/nwillc/kretry.svg)](https://travis-ci.org/nwillc/kretry)
[![Download](https://api.bintray.com/packages/nwillc/maven/kretry/images/download.svg)](https://bintray.com/nwillc/maven/kretry/_latestVersion)
------
# Kretry: A guava retying inspired Kotlin library

```kotlin
val config = Config<String>().apply {
    attempts = 10
}
val result: String = retry(config) {
    // some code that can fail, possibly with an exception
}
```

The above will retry the code block, up to 10 time, until it completes without an exception. If within 10 attempts 
it never passes a `RetryExceededException` will be thrown.
