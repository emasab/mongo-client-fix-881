package mongodb.client.fix

fun throwingException() {
    throw Exception("a checked exception in Java")
}

fun throwingRuntimeException() {
    throw RuntimeException("an unchecked exception in Java")
}
