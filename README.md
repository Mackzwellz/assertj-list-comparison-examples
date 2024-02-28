# How to get human-readable error text for assertions when comparing complex data objects

## Problem statement
This repository explores implementations of the following requirement:

> As automation QA engineer  
> Given I have two objects that I need to compare  
> And these objects have these properties:  
> - fields may be non-primitive POJOs
> - fields may be non-primiive lists of POJOs
> - both top level and nested fields of each POJO may have a set of fields that I don't want to use for comparison (ignored fields)
> - all POJOS may have a common ancestor (BaseDto) that may implement some helpful interfaces / instance methods
>
> When I compare two objects in assertion  
> If objects are not equal  
> I want to see an error message that:  
> - does not show ignored fields 
> - shows only mismatching fields and values of each object recursively
> - shows the "path" to mismatching field in object hierarchy (inclusing collection index)
> - shows the difference between equality type (mismatching field, value or collection size) 
> - outputs difference only between primitive/simple/single-field types (or else recurses deeper)
> - groups fields of the same object together
>

Stretch goals:

> - uses single source of truth for ignored fields for both `Object.equals` and assertion message
>  - will possibly have to resort to reflection for this
> - have method to get and use index-like property (`id`,`uuid`) in assertion output to make mismatching object identification easier
> - have option sort fields in assertion message in the same order as declared data object or alphabetically

This is how the result error message in assertion should look like:

```
Mismatch between List<User> objects:

[0].name: mismatcing value
expected: Sherlock 
actual: Moriarty

[0].address[1].city: mismatcing value
expected: London
actual: null

[0].address[1].building: mismatcing value
expected: null
actual: 221B

[1].address: mismatcing collection size
expected: 2
actual: 0
```

## Implementation details

It is possible to implement this using any assertion framework that allows to provide custom assertion message.  
//TODO basic reflection impl

But we will use AssertJ since it is almost perfect for our use case and requires only minimal customization.

## Solution(s)

[Check it out](src/test/java/assertj/CustomAssertionsTest.java)