# Team project - B3

This is the repository of the team project 2016. Team members are

- Lewis Dawson
- Ben Durrans
- Ossama Edbali
- Nishanth Ganatra
- Dom Williams

## Contributing

Following are some guidelines on contributing to this project.

### Style guide

#### JavaDoc and comments

Always JavaDoc everything. If some parts of code are unclear then put inline comments otherwise
there is no need for additional ones (since it should be readable enough).

#### Formatting

- Use **same line** curly braces
- Curly braces must be used even when it's optional
- Block indentation: 4 units (1 tab)
- If the length of a line breaches the line defined by IntelliJ then create a new line
- Always allow whitespace between a keyword (e.g. `if`) and the next word
- Vertical whitespace must be present in these situations:
    * Within method bodies, as needed to create *logical groupings* of statements.
    * Between consecutive members (or initializers) of a class: fields, constructors, methods, nested classes, static initializers, instance initializers.

Examples:

```java
// Bad
if (true)
{

}

// Bad
if(true){

}

// Good
if (true) {

}
```

```java
// Bad
while (notFound) {
  i++;
}

// Good
while (notFound) {
    i++;
}
```

#### Naming

- Always use sensible names
- Class names must start with a capital letter and camelcased (e.g. `WorldGraph` and not `worldGraph`)
- Basically everything else (unless specified) must be lower camelcased (e.g. `int progCounter`, `this.eventType`)
- Special prefixes or suffixes, like these `name_`, `mName`, `s_name` and `kName`, are not used.

#### Programming practices

- Always use `@Override`
- TODO: finish

### Version Control

TODO: finish