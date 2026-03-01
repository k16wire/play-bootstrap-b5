# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Play-Bootstrap is a Scala/Play Framework library providing input helpers and field constructors for rendering Bootstrap HTML forms. The primary focus is **Play Framework 2.9** with **Bootstrap 4** and **Bootstrap 5** support.

## Build Commands

### Using build.sh (Recommended)

A build script is provided at the project root for convenience:

```bash
./build.sh              # Build Play 2.9 modules (Bootstrap 4 and 5)
./build.sh --clean      # Clean before building
./build.sh --test       # Build and run tests
./build.sh --publish    # Build and publish to local Ivy repository
./build.sh --all        # Build all Play versions
./build.sh -c -t -p     # Combine options
```

### Manual sbt Commands

Each module is a separate sbt project. Navigate to the module directory before running commands.

```bash
# For Play 2.9 + Bootstrap 5
cd play29-bootstrap5/module
sbt compile           # Compile the module
sbt test              # Run all tests
sbt package           # Create JAR file
sbt "testOnly *HelpersSpec"  # Run a single test spec

# For Play 2.9 + Bootstrap 4
cd play29-bootstrap4/module
sbt compile
sbt test
sbt package

# For core module
cd core-play29
sbt compile
sbt test
sbt package           # Create JAR file
```

**sbt version:** 1.9.9
**Play Framework version:** 2.9.1
**Scala versions:** 2.13.12, 3.3.1

## Architecture

### Two-Tier Module Structure

**Core modules** (`core-play26`, `core-play27`, `core-play28`, `core-play29`):
- Provide base classes: `BSFieldInfo`, `BSFieldConstructor` traits
- Argument processing utilities (`Args.scala`)
- Published as `play-bootstrap-core` with version suffix (e.g., `1.6.1-P29`)

**Bootstrap version modules** (`play29-bootstrap5`, `play29-bootstrap4`, `play28-bootstrap4`, etc.):
- Depend on corresponding core module
- Contain Twirl templates for specific Bootstrap versions
- Published as `play-bootstrap` with full version suffix (e.g., `1.6.1-P29-B5`, `1.6.1-P29-B4`)

### Source Layout (play29-bootstrap5/module)

```
app/views/b5/
├── package.scala           # Form helpers (text, email, checkbox, select, etc.)
├── form.scala.html         # Form wrapper template
├── vertical/               # Vertical form layout field constructor
├── horizontal/             # Horizontal form layout (grid-based)
├── inline/                 # Inline form layout
└── clear/                  # Clear/minimal form layout
```

### Source Layout (play29-bootstrap4/module)

```
app/views/b4/
├── package.scala           # Form helpers (text, email, checkbox, select, etc.)
├── form.scala.html         # Form wrapper template
├── vertical/               # Vertical form layout field constructor
├── horizontal/             # Horizontal form layout (grid-based)
├── inline/                 # Inline form layout
└── clear/                  # Clear/minimal form layout
```

Common test layout for both modules:
```
test/
├── HelpersSpec.scala       # Tests for all form helpers
├── FieldConstructorsSpec.scala
├── FormsSpec.scala
└── TestUtils.scala
```

### Form Layouts

Four field constructor layouts available for each Bootstrap version:
- **Vertical** (`b5.vertical` / `b4.vertical`) - Labels above inputs (default)
- **Horizontal** (`b5.horizontal` / `b4.horizontal`) - Labels beside inputs using grid
- **Inline** (`b5.inline` / `b4.inline`) - Single-line forms
- **Clear** (`b5.clear` / `b4.clear`) - Raw inputs without wrappers

### Test Framework

Uses specs2 for testing. Tests are in `module/test/` directory.

## Key Files

- `docs/usage.md` - Comprehensive usage guide with examples
- `docs/migration.md` - Bootstrap 4 to 5 migration guide
- `play29-bootstrap5/module/app/views/b5/package.scala` - Bootstrap 5 form helper definitions
- `play29-bootstrap4/module/app/views/b4/package.scala` - Bootstrap 4 form helper definitions