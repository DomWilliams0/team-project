# Team project - B3
Team members:


- Lewis Dawson
- Ben Durrans
- Ossama Edbali
- Nishanth Ganatra
- Dom Williams

## Running the program

```
./gradlew run
```

## Contributing

The following are some guidelines for contributing to this project.

### Style guide

#### Formatting

- Use **same line** curly braces.
- Tabs should be used instead of spaces.
- Always allow whitespace between a keyword (e.g. `if`) and the next word or bracket.
- Vertical whitespace must be present in these situations:
    * Within method bodies, as needed to create *logical groupings* of statements.
    * Between consecutive members (or initialisers) of a class: fields, constructors, methods, nested classes, static initialisers, instance initialisers.
- Do not overly nest code.
- Enums should not have whitespace before the bracket in their declaration. (i.e. `ENUM(1.2)`)
- After the class definition there should be a blank line.
- After the final method there should be a blank line.
- All Java files should end with an empty line of whitespace.

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

#### Naming conventions

- Always use sensible variable and class names.
- Class names must start with a capital letter and be camelcased (e.g. `WorldGraph` and not `worldGraph`)
- Constants (I.e. static final) variables must be all capitalised with underscores separating words (e.g. `TILESET_RESOLUTION`)
- Basically everything else (unless specified) must be lower camelcased (e.g. `int progCounter`, `this.eventType`)
- Special prefixes or suffixes, like these `name_`, `mName`, `s_name` and `kName`, are not used.

#### Programming practices

- Always use `@Override`

#### Documentation practices

Always JavaDoc everything. If some parts of code are unclear then put inline comments otherwise
there is no need for additional ones (since it should be easily readable and understandable).

- Use {@link Class} when refering to any class that isn't the current class and it isn't obvious. (Not enforced, use common sense)
- Use &lt;code&gt;native type&lt;/code&gt;, for things such as &lt;code&gt;true&lt;/code&gt; and &lt;code&gt;static&lt;/code&gt;.
- Use &lt;code&gt;Java/Pseudo code&lt;/code&gt; for snippets of Java, Pseudo and any other types of code.
- Use {@code paramName} when refering to a parameter elsewhere in the JavaDoc.
- Try to keep documentation below the 71st column.
- Documentation goes above annotations, since annotations "are still part of the code".

### Version Control

This repository is for SVN. See `/branches` for the branches used.
