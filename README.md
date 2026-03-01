# Play-Bootstrap

[Play Framework](https://www.playframework.com/) library for [Bootstrap](https://getbootstrap.com/).

This is a collection of input helpers and field constructors for Play Framework to render Bootstrap HTML code.

> [!IMPORTANT]
> The purpose of this project is to support **Play Framework 2.9** with **Bootstrap 4** and **Bootstrap 5**.

## Supported Versions

| Play Version | Bootstrap Version | Artifact Version | Scala Versions | Java |
|--------------|-------------------|------------------|----------------|------|
| **2.9.x**    | **5**             | `1.6.1-P29-B5`   | 2.13, 3.3      | 11+  |
| **2.9.x**    | **4**             | `1.6.1-P29-B4`   | 2.13, 3.3      | 11+  |
| 2.8.x        | 4                 | `1.6.1-P28-B4`   | 2.12, 2.13     | 8+   |
| 2.8.x        | 3                 | `1.6.1-P28-B3`   | 2.12, 2.13     | 8+   |
| 2.7.x        | 4                 | `1.6.1-P27-B4`   | 2.12, 2.13     | 8+   |
| 2.7.x        | 3                 | `1.6.1-P27-B3`   | 2.12, 2.13     | 8+   |
| 2.6.x        | 4                 | `1.6.1-P26-B4`   | 2.11, 2.12     | 8+   |
| 2.6.x        | 3                 | `1.6.1-P26-B3`   | 2.11, 2.12     | 8+   |

## Installation

Add the dependency to your `build.sbt`:

```scala
// Play 2.9 + Bootstrap 5
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P29-B5"

// Play 2.9 + Bootstrap 4
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P29-B4"
```

## Quick Start

### Bootstrap 5

```scala
@import views.html.b5._

@b5.vertical.form(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.email(form("email"), '_label -> "Email")
    @b5.password(form("password"), '_label -> "Password")
    @b5.checkbox(form("agree"), '_text -> "I agree to terms")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

### Bootstrap 4

```scala
@import views.html.b4._

@b4.vertical.form(routes.Application.save()) { implicit fc =>
    @b4.text(form("name"), '_label -> "Name")
    @b4.email(form("email"), '_label -> "Email")
    @b4.password(form("password"), '_label -> "Password")
    @b4.checkbox(form("agree"), '_text -> "I agree to terms")
    @b4.submit('class -> "btn btn-primary"){ Submit }
}
```

## Documentation

- [Usage Guide](docs/usage.md) - Detailed usage instructions and examples
- [Migration Guide](docs/migration.md) - Migrating from Bootstrap 4 to Bootstrap 5
- [Online Documentation](https://playframework.github.io/play-bootstrap) - Full documentation and live examples

## Project Structure

```
play-bootstrap/
├── core-play26/          # Core module for Play 2.6
├── core-play27/          # Core module for Play 2.7
├── core-play28/          # Core module for Play 2.8
├── core-play29/          # Core module for Play 2.9
├── play26-bootstrap3/    # Play 2.6 + Bootstrap 3
├── play26-bootstrap4/    # Play 2.6 + Bootstrap 4
├── play27-bootstrap3/    # Play 2.7 + Bootstrap 3
├── play27-bootstrap4/    # Play 2.7 + Bootstrap 4
├── play28-bootstrap3/    # Play 2.8 + Bootstrap 3
├── play28-bootstrap4/    # Play 2.8 + Bootstrap 4
├── play29-bootstrap4/    # Play 2.9 + Bootstrap 4
├── play29-bootstrap5/    # Play 2.9 + Bootstrap 5
└── docs/                 # Documentation
```

## License

This software is licensed under the Apache 2 License.