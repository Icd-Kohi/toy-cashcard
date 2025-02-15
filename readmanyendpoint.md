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
