# Release Notes

## 1.6.1-P29-B5 (Unreleased)

### New Features

*   **Play 2.9 Support**: Added `core-play29` module to support Play Framework 2.9.
*   **Bootstrap 5 Support**: Added `play29-bootstrap5` module to support Bootstrap 5.
    *   Implemented `b5` package with field constructors and helpers.
    *   Added `b5.button`, `b5.submit`, `b5.reset` helpers.
    *   Added `b5.buttonGroup` helper.

### Build Script

Added `build.sh` script at the project root for building JAR files.

```bash
# Basic build (Play 2.9 + Bootstrap 5 only)
./build.sh

# Clean build
./build.sh --clean

# Build and run tests
./build.sh --test

# Build and publish to local Ivy repository
./build.sh --publish

# Build all Play versions
./build.sh --all

# Combine options
./build.sh -c -t -p
```

### Dependency Updates

*   Updated sbt to 1.9.9.
*   Updated Scala to 2.13.12 and added support for Scala 3.3.1.
*   Updated Play sbt plugin to 2.9.1.
