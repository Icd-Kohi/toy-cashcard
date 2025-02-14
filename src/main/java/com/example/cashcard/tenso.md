Optional here.

cashCardOptional.isPresent()
and

cashCardOptional.get()
This is how you determine if findById did or did not find the CashCard with the supplied id.

If cashCardOptional.isPresent() is true, then the repository successfully found the CashCard and we can retrieve it with cashCardOptional.get().

If not, the repository has not found the CashCard.

Run the tests.

We can see that the tests fail with a 500 INTERNAL_SERVER_ERROR.

CashCardApplicationTests > shouldReturnACashCardWhenDataIsSaved() FAILED
   org.opentest4j.AssertionFailedError:
   expected: 200 OK
   but was: 500 INTERNAL_SERVER_ERROR
This means the Cash Card API "crashed".

We need a bit more information...

Let's temporarily update the test output section of build.gradle with showStandardStreams = true, so that our test runs will produce a lot more output.

test {
 testLogging {
     events "passed", "skipped", "failed" //, "standardOut", "standardError"

     showExceptions true
     exceptionFormat "full"
     showCauses true
     showStackTraces true

     // Change from false to true
     showStandardStreams = true
 }
}
Rerun the tests.

Note that the test output is much more verbose.

Searching through the output we find these failures:

org.h2.jdbc.JdbcSQLSyntaxErrorException: Table "CASH_CARD" not found (this database is empty); SQL statement:
 SELECT "CASH_CARD"."ID" AS "ID", "CASH_CARD"."AMOUNT" AS "AMOUNT" FROM "CASH_CARD" WHERE "CASH_CARD"."ID" = ? [42104-214]
The cause of our test failures is clear: Table "CASH_CARD" not found means we don't have a database nor any data.

