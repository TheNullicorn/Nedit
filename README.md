# Nedit ![](https://img.shields.io/maven-central/v/me.nullicorn/Nedit "Nedit's current version on Maven Central")
Nedit is a simple, lightweight NBT parsing library with support for plain, gzipped, and base64 encoded NBT data. __One usage of this library is for parsing SkyBlock inventory data returned from the Hypixel API__ ([more info here](https://github.com/HypixelDev/PublicAPI/tree/master/Documentation#skyblock-items-and-inventories)).

## Installation

### Maven

Add the following inside your `<dependencies></dependencies>` block:
```xml
<dependency>
    <groupId>me.nullicorn</groupId>
    <artifactId>Nedit</artifactId>
    <version>2.2.0</version>
</dependency>
```

### Gradle

#### Kotlin DSL

In your `build.gradle.kts`'s `dependencies {}` block, add the following dependency:
```kotlin
implementation("me.nullicorn:Nedit:2.2.0")
```

#### Groovy DSL

In your `build.gradle`'s `dependencies {}` block, add the following dependency:
```groovy
implementation 'me.nullicorn:Nedit:2.2.0'
``` 


## Usage
To parse NBT data, Nedit provides you with the NBTReader class, which can be used like so:
```java
NBTCompound result = NBTReader.readBase64("CgALaGVsbG8gd29ybGQIAARuYW1lAAlCYW5hbnJhbWEA");
System.out.println("Full Result:  " + result);

// Full Result:  {hello world:{name:"Bananrama"}}
```

NBT compounds are essentially just maps of strings to values. Because of this, NBTCompound extends `java.util.Map`, and you can use any normal mapping functions to retireve and modify data within the compound.

NBTCompound also provides a layer of null-safety when accessing deeply-nested fields whose parent objects may be null. Accessors for primitives (`int`, `boolean`, `float`, etc) and `String`s also accept a default value in case the requested field isn't found.

**Example (extending the previous one):**
```java
System.out.println("Name: " + result.getString("hello world.name", "(Not Found!)"));

// Name: Bananrama
```
Like with `getString(key, defaultValue)`, NBTCompound also has methods for getting integers, floats, lists, nested compounds, and any other NBT datatype.

The "key" parameter of these methods is simply a dot-separated path to a field. This allows for developers to easily access deeply nested fields without having to check for null at each level. **Some examples of this format include:**
- `"user.id"`
- `"hello world.name"` (notice it may include spaces)
- `"message.channel.server.creator"`
- `"player.stats.wins"`
