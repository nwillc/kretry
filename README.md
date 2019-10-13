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
