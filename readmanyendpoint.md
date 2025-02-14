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
