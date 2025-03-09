# Fetching (all, or) many endpoints
Requesting a List of Cash Cards.
We expect our users to have *one to many* cards: imagine one for each of their family member. 
- The API should be able to return multiple Cash Cards in *response* to a single `REST request`.
- When making an `API Request` for several CashCards, you'd ideally make a single request, which returns a list (JSON) of CashCards. So we need a new `data contract`. Instead of a single CashCard, the new contract should specify that the response is JSON Array of CashCard Objects.

```JSON
[
  {
    "id": 1,
    "amount": 123.45
  },
  {
    "id": 2,
    "amount": 50.0
  }
]

``` 
It turns out that our old friend, `CrudRepository`, has a `findAll` method that we can use to easily fetch all the CashCards in the DB. Let's go ahead and use that method. At first glance, it looks pretty simple:

```java
@GetMapping()
private ResponseEntity<Iterable<CashCard>> findAll() {
   return ResponseEntity.ok(cashCardRepository.findAll());
}
```

However, it turns out there's a lot more to this operation than just returning all the CashCards in the DB.

We shouldn't be returning ALL the CashCards at once (If there's thousands of CashCards? Should it be returned sorted?).

# Pagination and Sorting.
Paging -> Doesn't overwhelm the client with thousands of CashCards responses, instead limit it by chunks.
Sorting -> 
- Pagination and Sorting -> special version of `CrudRepository`, called `PagingAndSortingRepository`.
 - Paging: Spring Data's pagination functionality. Specify the page length (e.g. 10 items), and page index (starting with 0).
 - Sorting: Default is not Random, but always will be the same. Sorting minimize future errors (Sudden version update changing the order of items).

### Spring Data Pagination API
Spring Data provides `PageRequest` and `Sort`

```java
private ResponseEntity<List<CashCard>> findAll(Pageable pageable) {
   Page<CashCard> page = cashCardRepository.findAll(
           PageRequest.of(
                   pageable.getPageNumber(),
                   pageable.getPageSize(),
                   pageable.getSortOr(Sort.by(Sort.Direction.DESC, "amount"))));
   return ResponseEntity.ok(page.getContent());
}
```
`ResponseEntity.ok(page.getContent())` -> return the CashCards contained in the `Page` object.
-# code 405 METHOD_NOT_ALLOWED means we've already implemented the endpoint, but there's no @GetMapping to that endpoint.
Implementation = "GET many" endpoint, add sorting and pagination.
1. Ensured that the data received from the server is in a predictable and understandable order.
2. Protected the client and server from being overwhelmed by a large amount of data (the page size puts a cap on the amount of data that can be returned in a single response).

Learned:
- Test the new Cash Card "list" JSON data contract
- Start with a failing test for a List endpoint
- Implement a GET endpoint for a list
- Implement pagination
- Implement sorting
- Combine paging and sorting and add default behavior
- Learn about test interaction and the @DirtiesContext annotation

##### Summary -> pagination,sorting,filtering capabilities.

# Simple Spring Security
## Authentication.

Act of a *Principal* proving its identity to the system.
A *Principal* is authenticated once the proper credentials have been presented.
Auth Session (Or Session) is created when user gets authenticated. Impl of Session:
- Session Token: String of Random Chars that is generated and placed in a *Cookie*[0].
Spring Security impl auth in *Filter Chain*[1]. Spring Security inserts a filter which checks the user's auth and return *401 UNAUTHORIZED* response if request isn't authenticated.

## Authorization.

Second step of security. It's provided via Role-Based Access Control (RBAC) -> A *Principal* has a number of *Roles*.
Each resource (or operation) specifies which Roles a Principal must have in order to perform actions with proper Authorization prior configured at both global level and per-method basis (Via annotations e.g. @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter).
User with *Admin Role* is likely to be authorized to perform more actions than a user with a *Card Owner Role*.

## Cross-Origin Resource Sharing.
Spring Security provides the @CrossOrigin annotation, that allows you to specify a list of allowed sites to share resources.
**Using the annotation without any arguments will allow *all origins*.**

## Common Web Exploits. 

- Cross-Site Request Forgery (CSRF or "Sea-Surf" or "Session Riding"):
 - Session Riding is actually enabled by Cookies. CSRF attacks happen w hen a malicious piece of code sends a request to a server where a user is authenticated. The server has no way of knowing if the victim sent the harmful request unintentionally.
 - Use CSRF Token to protect against these attacks. CSRF Token is different from Auth Token, because a unique token is generated on each request. This makes it harder for an outside actor to insert itself into the "conversation" between client and the server. Spring Security has built-in support for CSRF Tokens, which is enabled by default.
- Cross-Site Scripting (XSS):
 - Executes arbitrary code (in a `<script></script>` tag that waits to be rendered and executed on the web page)
 - The possibility of executing XSS is usually caused by "holes" of poor programming practices.
 - The main guard against XSS is to properly process all data from external sources (like web forms and URI query strings).
 - `<script>` tag attack can be mitigated by properly escaping the special HTML characters when the string is rendered.



*User of an API may be: User or Program. *Principal* -> "User".*

##### *[0] -> Cookie is a set of data stored in a web client (such as a browser), and associated with a specific URI. Cookies can persist for a certain amount of time, even if the web page is closed and later re-visited. They are sent automatically with every request. If token != valid { request can be rejected }.*

##### *[1] -> Filter Chain is a component of Java web architecture. Allows programmers to define a sequence of methods to be called prior to the Controller. Each filter in the chain decides to allow a request to continue or not.*

## Summary
In this lab you learned how to use Spring Security to ensure that only authenticated, authorized users have access to the Family Cash Card API. In addition, you followed best practices in the Controller and Repository layers of our application to ensure that only the correct users have access to their (and only their) Cash Card data. You also learned that Spring Security not only protects our requests, but it also modifies our API's error handling as to avoid "leaking" information about crashes and other internal operations occurring with our application.

## Implementing DELETE
In this lesson, we’ll implement the last of the four CRUD operations: Delete! By now, you should be familiar with the question we should ask first: What's the API’s data specification for the Delete endpoint? The specification includes the details of the Request and Response.

At the risk of spoiling the outcome, this is the specification that we’ll define:

Request:

Verb: DELETE
URI: /cashcards/{id}
Body: (empty)
Response:

Status code: 204 NO CONTENT
Body: (empty)
We’ll return the 204 NO CONTENT status code for a successful delete, but there are additional cases:

Response Code	Use Case
204 NO CONTENT	
The record exists, and
The Principal is authorized, and
The record was successfully deleted.
404 NOT FOUND	
The record does not exist (a non-existent ID was sent).
404 NOT FOUND	
The record does exist but the Principal is not the owner.
Why do we return 404 for the "ID does not exist" and "not authorized to access this ID" cases? In order to not "leak" information: If the API returned different results for the two cases, then an unauthorized user would be able to discover specific IDs that they're not authorized to access.

Additional Options
Let’s dig deeper into some more options we'll consider when implementing a Delete operation.

Hard and Soft Delete
So, what does it mean to delete a Cash Card from a database’s point of view? Similar to how we decided that our Update operation means ”replace the entire existing record” (as opposed to supporting partial update), we need to decide what happens to resources when they are deleted.

A simple option, called hard delete, is to delete the record from the database. With a hard delete, it’s gone forever. So, what can we do if we need data that existed prior to its deletion?

An alternative is soft delete which works by marking records as "deleted" in the database (so that they're retained, but marked as deleted). For example, we can introduce an IS_DELETED boolean or a DELETED_DATE timestamp column and then set that value-instead of fully removing the record by deleting the database row(s). With a soft delete, we also need to change how Repositories interact with the database. For example, a repository needs to respect the “deleted” column and exclude records marked deleted from Read requests.

Audit Trail and Archiving
When working with databases, you’ll find that there’s often a requirement to keep a record of modifications to data records. For example:

A customer service representative might need to know when a customer deleted their Cash Card.
There may be data retention compliance regulations which require deleted data to be retained for a certain period of time.
If the Cash Card is hard-deleted then we'd need to store additional data to be able to answer this question. Let’s discuss some ways to record historical information:

Archive (move) the deleted data into a different location.
Add audit fields to the record itself. For example, the DELETED_DATE column that we mentioned already. Additional audit fields can be added, for example DELETED_BY_USER. Again, this isn't limited to Delete operations, but Create and Update also.
APIs which implement soft delete and audit fields can return the state of the object in the response, and the 200 OK status code. So, why did we choose to use 204 instead of 200? Because the 204 NO CONTENT status implies that there's no body in the response.

Maintain an audit trail. The audit trail is a record of all important operations done to a record. It can contain not only Delete operations, but Create and Update as well.
The advantage of an audit trail over audit fields is that a trail records all events, whereas audit fields on the record capture only the most recent operation. An audit trail can be stored in a different database location, or even in log files.

It’s worth mentioning that a combination of several of the above strategies is possible. Here are some examples:

We could implement soft delete, then have a separate process which hard-deletes or archives soft-deleted records after a certain time period, like once per year.
We could implement hard delete, and archive the deleted records.
In any of the above cases, we could keep an audit log of which operations happened when.
Finally, observe that even the simple specification that we’ve chosen doesn’t determine whether we implement hard or soft delete. It also doesn’t determine whether we add audit fields or keep an audit trail. However, the fact that we chose to return 204 NO CONTENT implies that soft-delete isn't happening, since if it was, we’d probably choose to return 200 OK with the record in the body.
