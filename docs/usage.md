# Usage Guide

This guide covers how to use Play-Bootstrap in your Play Framework application.

## Setup

### Add Dependency

Add the appropriate dependency to your `build.sbt`:

```scala
// Play 2.9 + Bootstrap 5
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P29-B5"

// Play 2.8 + Bootstrap 4
libraryDependencies += "com.adrianhurt" %% "play-bootstrap" % "1.6.1-P28-B4"
```

### Import in Templates

For Bootstrap 5:
```scala
@import views.html.b5._
```

For Bootstrap 4:
```scala
@import views.html.b4._
```

## Form Layouts

Play-Bootstrap supports four form layouts:

### Vertical Form (Default)

Labels appear above inputs.

```scala
@b5.vertical.form(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.email(form("email"), '_label -> "Email")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

### Horizontal Form

Labels appear beside inputs using Bootstrap's grid system.

```scala
@b5.horizontal.form(routes.Application.save(), "col-md-2", "col-md-10") { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.email(form("email"), '_label -> "Email")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

### Inline Form

All inputs appear on a single line.

```scala
@b5.inline.form(routes.Application.search()) { implicit fc =>
    @b5.text(form("query"), '_hiddenLabel -> "Search")
    @b5.submit('class -> "btn btn-outline-primary"){ Search }
}
```

### Clear Form

No wrapper elements, just raw inputs.

```scala
@b5.clear.form(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"))
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

## Input Types

### Text Inputs

```scala
@b5.text(form("name"), '_label -> "Name")
@b5.email(form("email"), '_label -> "Email")
@b5.password(form("password"), '_label -> "Password")
@b5.url(form("website"), '_label -> "Website")
@b5.tel(form("phone"), '_label -> "Phone")
@b5.search(form("query"), '_label -> "Search")
```

### Number Inputs

```scala
@b5.number(form("age"), '_label -> "Age")
@b5.range(form("volume"), '_label -> "Volume", 'min -> 0, 'max -> 100)
```

### Date and Time Inputs

```scala
@b5.date(form("birthdate"), '_label -> "Birth Date")
@b5.time(form("startTime"), '_label -> "Start Time")
@b5.datetime(form("timestamp"), '_label -> "Timestamp")
@b5.datetimeLocal(form("appointment"), '_label -> "Appointment")
@b5.month(form("month"), '_label -> "Month")
@b5.week(form("week"), '_label -> "Week")
```

### Other Inputs

```scala
@b5.textarea(form("bio"), '_label -> "Biography", 'rows -> 5)
@b5.file(form("avatar"), '_label -> "Avatar")
@b5.color(form("color"), '_label -> "Favorite Color")
@b5.hidden("userId", user.id)
```

### Selection Inputs

#### Checkbox

```scala
// Simple checkbox
@b5.checkbox(form("agree"), '_text -> "I agree to terms")

// With default checked
@b5.checkbox(form("subscribe"), '_text -> "Subscribe", '_default -> true)

// Readonly
@b5.checkbox(form("locked"), '_text -> "Locked", 'readonly -> true)
```

#### Radio Buttons

```scala
// Basic radio group
@b5.radio(form("gender"), Seq("M" -> "Male", "F" -> "Female"), '_label -> "Gender")

// Inline radio buttons
@b5.radio(form("size"), Seq("S" -> "Small", "M" -> "Medium", "L" -> "Large"), 
    '_label -> "Size", '_inline -> true)
```

#### Select

```scala
// Basic select
@b5.select(form("country"), countries, '_label -> "Country")

// With default option
@b5.select(form("country"), countries, '_label -> "Country", '_default -> "-- Select --")

// Multiple select
@b5.select(form("tags"), tags, '_label -> "Tags", 'multiple -> true)
```

### Buttons

```scala
@b5.submit('class -> "btn btn-primary"){ Submit }
@b5.reset('class -> "btn btn-secondary"){ Reset }
@b5.button('class -> "btn btn-info", 'id -> "myBtn"){ Click Me }
```

## Common Arguments

### Labels

```scala
// Visible label
@b5.text(form("name"), '_label -> "Name")

// Hidden label (for screen readers)
@b5.text(form("name"), '_hiddenLabel -> "Name")

// Hide existing label
@b5.text(form("name"), '_label -> "Name", '_hideLabel -> true)
```

### Placeholders

```scala
@b5.text(form("name"), '_label -> "Name", 'placeholder -> "Enter your name")
```

### Help Text

```scala
@b5.text(form("email"), '_label -> "Email", '_help -> "We'll never share your email")
```

### Validation States

```scala
// Success
@b5.text(form("name"), '_label -> "Name", '_success -> true)
@b5.text(form("name"), '_label -> "Name", '_success -> "Looks good!")

// Warning
@b5.text(form("name"), '_label -> "Name", '_warning -> true)
@b5.text(form("name"), '_label -> "Name", '_warning -> "Please verify")

// Error (manual)
@b5.text(form("name"), '_label -> "Name", '_error -> "Required field")

// Show form constraints
@b5.text(form("name"), '_label -> "Name", '_showConstraints -> true)
```

### Custom ID and Classes

```scala
@b5.text(form("name"), '_label -> "Name", 'id -> "customId", 'class -> "custom-class")

// Custom ID for form group wrapper
@b5.text(form("name"), '_label -> "Name", '_id -> "formGroupId")

// Custom class for form group wrapper
@b5.text(form("name"), '_label -> "Name", '_class -> "mb-4")
```

### Disabled and Readonly

```scala
@b5.text(form("name"), '_label -> "Name", 'disabled -> true)
@b5.text(form("name"), '_label -> "Name", 'readonly -> true)
```

## Input Groups

Wrap inputs with addons using `inputWrapped`:

```scala
@b5.inputWrapped("text", form("price"), '_label -> "Price") { input =>
    <div class="input-group">
        <span class="input-group-text">$</span>
        @input
        <span class="input-group-text">.00</span>
    </div>
}
```

## Multifield

Group multiple fields together:

```scala
@b5.multifield(form("dateStart"), form("dateEnd"))(
    Seq('_label -> "Date Range"),
    Seq('_help -> "Select start and end dates")
) { implicit cfc =>
    <div class="row">
        <div class="col">@b5.date(form("dateStart"), '_hiddenLabel -> "Start")</div>
        <div class="col">@b5.date(form("dateEnd"), '_hiddenLabel -> "End")</div>
    </div>
}
```

## Static Content

Display static text in a form:

```scala
@b5.static("Email")(user.email)
@b5.static('_label -> "Email", 'id -> "staticEmail")(user.email)
```

## Free Content

Add arbitrary content within the form structure:

```scala
@b5.free('_label -> "Custom") {
    <p>Any HTML content here</p>
}
```

## CSRF Protection

Use `formCSRF` to automatically include CSRF token:

```scala
@b5.vertical.formCSRF(routes.Application.save()) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
    @b5.submit('class -> "btn btn-primary"){ Submit }
}
```

## Feedback Tooltips

Use tooltip-style feedback instead of block feedback:

```scala
@defining(b5.vertical.fieldConstructorSpecific(withFeedbackTooltip = true)) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name", '_error -> "Required")
}
```

Or at form level:

```scala
@b5.vertical.form(routes.Application.save(), '_feedbackTooltip -> true) { implicit fc =>
    @b5.text(form("name"), '_label -> "Name")
}
```
