# Migration Guide: Bootstrap 4 to Bootstrap 5

This guide helps you migrate your Play-Bootstrap project from Bootstrap 4 (`b4`) to Bootstrap 5 (`b5`).

## Quick Start

1. Update your dependency in `build.sbt`:

```scala
// Before (Play 2.8 + Bootstrap 4)
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"

// After (Play 2.9 + Bootstrap 5)
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P29-B5"
```

2. Update your imports in templates:

```scala
// Before
@import views.html.b4._

// After
@import views.html.b5._
```

3. Update field constructor references:

```scala
// Before
@b4.vertical.form(routes.Application.save()) { implicit fc =>
    @b4.text(form("name"), '_label -> "Name")
}

// After
@b5.vertical.form(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
}
```

## CSS Class Changes

The following table shows the CSS class changes between Bootstrap 4 and Bootstrap 5 that are handled automatically by the library:

| Bootstrap 4 | Bootstrap 5 | Description |
|---|---|---|
| `form-group` | `mb-3` | Form group wrapper now uses margin utility |
| `sr-only` | `visually-hidden` | Screen reader only class renamed |
| `custom-control` | `form-check` | Custom controls unified with standard |
| `custom-control-input` | `form-check-input` | Input class for checkboxes/radios |
| `custom-control-label` | `form-check-label` | Label class for checkboxes/radios |
| `custom-checkbox` | `form-check` | Checkbox wrapper class |
| `custom-radio` | `form-check` | Radio wrapper class |
| `custom-control-inline` | `form-check-inline` | Inline checkbox/radio class |
| `custom-select` | `form-select` | Select element class |
| `custom-file` | _(removed)_ | File inputs now use `form-control` |
| `custom-file-input` | `form-control` | File input class |
| `custom-file-label` | _(removed)_ | No longer needed |
| `form-control-file` | `form-control` | File input class unified |
| `form-control-static` | `form-control-plaintext` | Static/readonly text display |
| `form-inline` | _(removed)_ | Use grid utilities instead |

## Removed Features

### Custom Controls (`_custom` parameter)

Bootstrap 5 removed the distinction between "custom" and "standard" form controls. The `_custom` parameter is no longer needed and has been removed from the API.

**Before (Bootstrap 4):**
```scala
@b4.checkbox(form("remember"), '_custom -> true, '_text -> "Remember me")
@b4.radio(form("option"), options, '_custom -> true)
@b4.select(form("choice"), choices, '_custom -> true)
@b4.file(form("upload"), '_custom -> true)
```

**After (Bootstrap 5):**
```scala
@b5.checkbox(form("remember"), '_text -> "Remember me")
@b5.radio(form("option"), options)
@b5.select(form("choice"), choices)
@b5.file(form("upload"))
```

### Inline Forms

Bootstrap 5 removed the `form-inline` class. The inline field constructor now uses grid utilities (`row`, `col-auto`, `g-3`) to achieve similar results.

**Before (Bootstrap 4):**
```scala
@b4.inline.form(routes.Application.search()) { implicit fc =>
    @b4.text(form("query"), '_hiddenLabel -> "Search")
    @b4.submit('class -> "btn btn-primary"){ Search }
}
```

**After (Bootstrap 5):**
```scala
@b5.inline.form(routes.Application.search()) { implicit fc =>
    @b5.text(form("query"), '_hiddenLabel -> "Search")
    @b5.submit('class -> "btn btn-primary"){ Search }
}
```

The form will automatically use `row row-cols-lg-auto g-3 align-items-center` classes.

## Form Layouts

### Vertical Form

```scala
@b5.vertical.form(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.email(form("email"), '_label -> "Email")
    @b5.password(form("password"), '_label -> "Password")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

### Horizontal Form

```scala
@b5.horizontal.form(routes.Application.save(), "col-md-2", "col-md-10") { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.email(form("email"), '_label -> "Email")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

### Inline Form

```scala
@b5.inline.form(routes.Application.search()) { implicit fc =>
    @b5.text(form("query"), '_hiddenLabel -> "Search")
    @b5.submit('class -> "btn btn-outline-primary"){ Search }
}
```

## Input Helpers

All input helpers work the same way, just with the `b5` prefix:

```scala
// Text inputs
@b5.text(form("name"), '_label -> "Name")
@b5.email(form("email"), '_label -> "Email")
@b5.password(form("password"), '_label -> "Password")
@b5.number(form("age"), '_label -> "Age")
@b5.url(form("website"), '_label -> "Website")
@b5.tel(form("phone"), '_label -> "Phone")

// Date/Time inputs
@b5.date(form("birthdate"), '_label -> "Birth Date")
@b5.time(form("startTime"), '_label -> "Start Time")
@b5.datetimeLocal(form("appointment"), '_label -> "Appointment")

// Other inputs
@b5.textarea(form("bio"), '_label -> "Biography", 'rows -> 5)
@b5.file(form("avatar"), '_label -> "Avatar")
@b5.color(form("favoriteColor"), '_label -> "Favorite Color")
@b5.range(form("volume"), '_label -> "Volume", 'min -> 0, 'max -> 100)

// Selection inputs
@b5.checkbox(form("agree"), '_text -> "I agree to terms")
@b5.radio(form("gender"), Seq("M" -> "Male", "F" -> "Female"), '_label -> "Gender")
@b5.select(form("country"), countries, '_label -> "Country")

// Buttons
@b5.submit('class -> "btn btn-primary"){ Submit }
@b5.reset('class -> "btn btn-secondary"){ Reset }
@b5.button('class -> "btn btn-info"){ Click Me }
```

## Validation States

Validation feedback works the same way:

```scala
// Success state
@b5.text(form("name"), '_label -> "Name", '_success -> "Looks good!")

// Warning state
@b5.text(form("name"), '_label -> "Name", '_warning -> "Please double-check")

// Error state (automatic from form errors, or manual)
@b5.text(form("name"), '_label -> "Name", '_error -> "This field is required")

// Show constraints
@b5.text(form("name"), '_label -> "Name", '_showConstraints -> true)

// Help text
@b5.text(form("name"), '_label -> "Name", '_help -> "Enter your full name")
```

## Java Version Requirement

Play 2.9 requires Java 11 or later. Make sure your environment meets this requirement before upgrading.

```bash
# Check your Java version
java -version

# Should show version 11 or higher
```

## Troubleshooting

### Compilation Errors

If you see errors about missing `_custom` parameter or `isCustom` field, remove all `_custom` arguments from your templates.

### Class Version Error

If you see `UnsupportedClassVersionError`, you need to upgrade to Java 11 or later.

### Missing Classes

Make sure you've updated all imports from `b4` to `b5` in your templates.
