# Module kretry

A _Try_ with intrinsic retrying support. The `Retry` can be configured for a specific number of
attempts, a delay between attempts, a back off function for the delays, and a predicate to determine
success or failure (exceptions are failures). On completion either a `Success` with the result,
or `Failure` with the exception will be returned.

# Package com.github.nwillc.kretry

The kretry implementation.
