package uk.nhs.riskscore.internal

internal fun assertionFailure(message: () -> Any) {
    assert(false, message)
}