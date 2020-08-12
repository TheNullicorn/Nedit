# Nedit [![](https://jitpack.io/v/TheNullicorn/Nedit.svg)](https://jitpack.io/#TheNullicorn/Nedit)
Nedit is a simple, lightweight NBT parsing library with support for plain, gzipped and base64 encoded NBT data.

## Installation
Nedit can be added to most major build-automation tools using JitPack. See the instructions [here](https://jitpack.io/#TheNullicorn/Nedit) for more info.

## Usage
To parse NBT data, Nedit provides you with the NBTReader class, which can be used like so:
```java
NBTReader reader = new NBTReader("CgALaGVsbG8gd29ybGQIAARuYW1lAAlCYW5hbnJhbWEA");
NBTCompound result = reader.read();
System.out.println("Result: " + result);

// Result: {hello world:{name:"Bananrama"}}
```

NBT compounds are essentially maps. Because of this, NBTCompound class extends java.util.Map and you can use any normal map functions to retireve and modify data within the compound.

NBTCompound also provides some helper methods for retrieving deeply nested fields without having to repeatedly check for null. These methods also allow you to provide a default value in case the field is null or does not exist.

**Example (added onto the previous one):**
```java
System.out.println("Name: " + result.getString("hello world.name", "Not Found"));

// Name: Bananrama
```