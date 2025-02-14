# How to get human-readable error text for assertions when comparing complex data objects

## Problem statement

This repository explores implementations of the following requirement:

> As automation QA engineer  
> Given I have two objects that I need to compare  
> And these objects have these properties:
> - fields may be non-primitive POJOs
> - fields may be non-primitive lists of POJOs
> - both top level and nested fields of each POJO may have a set of fields that I don't want to use for comparison (
    ignored fields)
> - all POJOs may have a common ancestor (BaseDto) that may implement some helpful interfaces / instance methods
>
> When I compare two objects in assertion  
> If objects are not equal  
> I want to see an error message that:
> - does not show ignored fields
> - shows only mismatching fields and values of each object recursively
> - shows the "path" to mismatching field in object hierarchy (including collection index)
> - shows the difference between equality type (mismatching field, value or collection size)
> - outputs difference only between primitive/simple/single-field types (or else recurse deeper)
> - groups fields of the same object together
>

Stretch goals:

> - uses single source of truth for ignored fields for both `Object.equals` and assertion message
    >
- will possibly have to resort to reflection for this
> - have method to get and use index-like property (`id`,`uuid`) in assertion output to make mismatching object
    identification easier
> - have option sort fields in assertion message in the same order as declared data object or alphabetically

This is how the result error message in assertion should look like:

```
Mismatch between List<User> objects:

[0].name: mismatching value
expected: Sherlock 
actual: Moriarty

[0].address[1].city: mismatching value
expected: London
actual: null

[0].address[1].building: mismatching value
expected: null
actual: 221B

[1].address: mismatching collection size
expected: 2
actual: 0
```

## Implementation details

It is possible to implement this using any assertion framework that allows to provide custom assertion message.
For instance, we could collect the data ourselves, see
e.g. [ReflectionAssertionsTest](src/test/java/assertj/ReflectionAssertionsTest.java).
This should work regardless of which assertions we use.

But we will use AssertJ since it is almost perfect for our use case ~~and requires only minimal customization~~ and has
many features that we need already implemented.

## Solution(s)

[Check it out](src/test/java/assertj/CustomAssertionsTest.java).
We still had to override the assertion message, but at least the object comparison could be implemented in a nice and
extensible way.

- [Relevant AssertJ Github forum discussion here](https://github.com/orgs/assertj/discussions/3352)

## Lessons learned

- If not obvious enough, `equals()` and `hashCode()` should only perform these operations and nothing else.
    - In original project from which this experiment started, `BaseDto` contained a common `equals()` method
      that did recursive `Collection.sort()` for all collection fields.
    - Achieving clean parity for this is a nightmare,
      so please make sure to keep any field mutations outside of object equality/comparison methods, thanks.
- Current (as of ~v3.26) existing AssertJ assertion APIs don't handle/scale for this approach well.
    - See [Discussion about the shortcomings of current approach](https://github.com/orgs/assertj/discussions/3418)
    - Then again, it is probably not the best approach in general.
