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


