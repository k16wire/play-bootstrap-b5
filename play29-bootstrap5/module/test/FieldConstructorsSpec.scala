/**
 * Copyright 2019 Adrian Hurtado (adrianhurt)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package views.html.b5

import views.html.b5
import TestUtils._
import org.specs2.mutable.Specification
import views.html.helper.FieldConstructor
import play.api.data.Forms._
import play.api.data._
import play.api.{ Configuration, Environment }
import play.api.http.HttpConfiguration
import play.api.i18n.{ DefaultLangsProvider, DefaultMessagesApiProvider, MessagesProvider }
import play.twirl.api.Html
import play.api.mvc.Call

class FieldConstructorsSpec extends Specification {

  val environment = Environment.simple()
  val conf = Configuration.reference
  val langs = new DefaultLangsProvider(conf).get

  val httpConfiguration = HttpConfiguration.fromConfiguration(conf, environment)
  val messagesApi = new DefaultMessagesApiProvider(environment, conf, langs, httpConfiguration).get
  implicit val msgsProv: MessagesProvider = messagesApi.preferred(Seq.empty)

  def testFielConstructor(fcDefault: B5FieldConstructor, fcWithFeedbackTooltip: B5FieldConstructor) = {
    implicit val fc = fcDefault
    val testInputString = "<input>"
    def testInput(field: Field, args: (Symbol, Any)*) =
      b5.inputFormGroup(field, true, args) { _ => Html(testInputString) }

    val fooForm = Form(single("foo" -> Forms.nonEmptyText(maxLength = 8)))
    val fooField = fooForm("foo")

    val simpleInput = testInput(fooField).body
    def simpleInputWithArgs(args: (Symbol, Any)*)(implicit fc: B5FieldConstructor) = b5.text(fooField, args: _*).body
    def simpleInputWithError(args: (Symbol, Any)*)(implicit fc: B5FieldConstructor) = b5.text(fooForm.withError("foo", "test-error-0").withError("foo", "test-error-1")("foo"), args: _*).body

    val fieldsetExtraClasses = fc match {
      case hfc: b5.horizontal.HorizontalFieldConstructor => " row"
      case ifc: b5.inline.InlineFieldConstructor => " col-12"
      case _ => ""
    }
    val labelExtraClasses = fc match {
      case hfc: b5.horizontal.HorizontalFieldConstructor => "col-form-label " + hfc.colLabel
      case _ => "form-label"
    }

    "have the basic structure" in {
      simpleInput must contain("class=\"mb-3")
      simpleInput must not contain ("has-danger")
      simpleInput must not contain ("aria-invalid")
      simpleInput must contain(testInputString)
      simpleInput must not contain ("class=\"-feedback\"")
      simpleInput must not contain ("class=\"-tooltip\"")
      simpleInput must not contain ("class=\"form-text text-muted\"")
    }

    "have a default id" in {
      simpleInput must contain("id=\"foo_field\"")
    }

    "allow setting a custom id" in {
      simpleInputWithArgs(Symbol("_id") -> "customid") must contain("id=\"customid\"")
    }

    "allow setting extra classes form-group" in {
      clean(simpleInputWithArgs(Symbol("_class") -> "extra_class another_class")) must contain(s"""<div class="mb-3$fieldsetExtraClasses extra_class another_class" """)
    }

    "render the label" in {
      clean(simpleInputWithArgs(Symbol("_label") -> "theLabel")) must contain(s"""<label class="$labelExtraClasses" for="foo">theLabel</label>""")
    }

    "allow hide the label" in {
      val labelString = s"""<label class="$labelExtraClasses visually-hidden" for="foo">theLabel</label>"""
      clean(simpleInputWithArgs(Symbol("_label") -> "theLabel", Symbol("_hideLabel") -> true)) must contain(labelString)
      clean(simpleInputWithArgs(Symbol("_hiddenLabel") -> "theLabel")) must contain(labelString)
    }

    "allow render without label" in {
      simpleInputWithArgs() must not contain ("label")
    }

    "allow rendering errors" in {
      val test = simpleInputWithError()
      test must contain("has-danger")
      test must contain("<div id=\"foo_error_0\" class=\"invalid-feedback\">test-error-0</div>")
      test must contain("<div id=\"foo_error_1\" class=\"invalid-feedback\">test-error-1</div>")
    }

    "allow showing constraints" in {
      val test = simpleInputWithArgs(Symbol("_showConstraints") -> true)
      test must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">")
      test must contain("<small id=\"foo_info_1\" class=\"form-text text-muted\">")
      test must contain("class=\"form-text text-muted\">" + msgsProv.messages("constraint.required") + "</small>")
      test must contain("class=\"form-text text-muted\">" + msgsProv.messages("constraint.maxLength", 8) + "</small>")
    }

    "localize placeholder property" in {
      val test = simpleInputWithArgs(Symbol("placeholder") -> "simpleInputWithArgs.placeholder.value")
      test must contain("placeholder=\"Placeholder value\"")
    }

    "allow showing help info" in {
      simpleInputWithArgs(Symbol("_help") -> "test-help") must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">test-help</small>")
      simpleInputWithArgs(Symbol("_success") -> "test-help") must contain("<div id=\"foo_feedback_0\" class=\"valid-feedback\">test-help</div>")
      simpleInputWithArgs(Symbol("_warning") -> "test-help") must contain("<div id=\"foo_feedback_0\" class=\"warning-feedback\">test-help</div>")
      simpleInputWithArgs(Symbol("_error") -> "test-help") must contain("<div id=\"foo_error_0\" class=\"invalid-feedback\">test-help</div>")
    }

    "allow rendering erros and hide constraints when help info is present" in {
      val test = simpleInputWithError(Symbol("_showConstraints") -> true, Symbol("_help") -> "test-help")
      test must contain("<div id=\"foo_error_0\" class=\"invalid-feedback\">test-error-0</div>")
      test must contain("<div id=\"foo_error_1\" class=\"invalid-feedback\">test-error-1</div>")
      test must contain("<small id=\"foo_info_0\" class=\"form-text text-muted\">test-help</small>")
      test must not contain ("<small id=\"foo_info_1\"")
    }

    "render validation states" in {
      def withStatus(status: String) = contain(s"""<div class="mb-3${fieldsetExtraClasses} has-${status}"""")
      def testStatus(status: String, args: (Symbol, Any)*) = {
        val test = clean(simpleInputWithArgs(args: _*))
        test must withStatus(status)
      }

      testStatus("success", Symbol("_success") -> true)
      testStatus("success", Symbol("_success") -> "test-help")
      testStatus("warning", Symbol("_warning") -> true)
      testStatus("warning", Symbol("_warning") -> "test-help")
      testStatus("danger", Symbol("_error") -> true)
      testStatus("danger", Symbol("_error") -> "test-help")
    }

    "render aria attributes" in {
      val test0 = simpleInputWithArgs()
      test0 must not contain ("aria-invalid")
      test0 must not contain ("aria-describedby")
      test0 must not contain ("<span id=\"foo_status\"")
      test0 must not contain ("<span id=\"foo_info")
      test0 must not contain ("<span id=\"foo_error")

      val test1 = simpleInputWithError(Symbol("_showConstraints") -> true)
      test1 must contain("aria-invalid=\"true\"")
      test1 must contain("aria-describedby=\"foo_error_0 foo_error_1 foo_info_0 foo_info_1\"")
      test1 must contain("<small id=\"foo_info_0\"")
      test1 must contain("<small id=\"foo_info_1\"")
      test1 must contain("<div id=\"foo_error_0\"")
      test1 must contain("<div id=\"foo_error_1\"")

      val test2 = simpleInputWithArgs(Symbol("_help") -> "test-help")
      test2 must contain("aria-describedby=\"foo_info_0\"")
      test2 must contain("<small id=\"foo_info_0\"")
      test2 must not contain ("<div id=\"foo_feedback")
    }

    "allow rendering with feedback tooltips" in {
      val test1 = simpleInputWithError()(fc)
      val test2 = simpleInputWithError()(fcWithFeedbackTooltip)
      test1.replaceAll("-feedback", "-tooltip") must be equalTo test2

      val test3 = simpleInputWithArgs(Symbol("_success") -> "test-help")(fc)
      val test4 = simpleInputWithArgs(Symbol("_success") -> "test-help")(fcWithFeedbackTooltip)
      test3.replaceAll("-feedback", "-tooltip") must be equalTo test4

      val test5 = simpleInputWithArgs(Symbol("_warning") -> "test-help")(fc)
      val test6 = simpleInputWithArgs(Symbol("_warning") -> "test-help")(fcWithFeedbackTooltip)
      test6.replaceAll("-feedback", "-tooltip") must be equalTo test6
    }
  }

  "horizontal field constructor" should {
    val (colLabel, colInput) = ("col-md-2", "col-md-10")
    implicit val horizontalFieldConstructor = new b5.horizontal.HorizontalFieldConstructor(colLabel, colInput)
    val fcWithFeedbackTooltip = new b5.horizontal.HorizontalFieldConstructor(colLabel, colInput, withFeedbackTooltip = true)

    testFielConstructor(horizontalFieldConstructor, fcWithFeedbackTooltip)

    "render columns for horizontal form" in {
      val body = b5.text(Form(single("foo" -> Forms.text))("foo"), Symbol("_label") -> "theLabel").body
      body must contain(colLabel)
      body must contain(colInput)
    }
  }

  "vertical field constructor" should {
    implicit val verticalFieldConstructor = new b5.vertical.VerticalFieldConstructor()
    val fcWithFeedbackTooltip = new b5.vertical.VerticalFieldConstructor(withFeedbackTooltip = true)
    testFielConstructor(verticalFieldConstructor, fcWithFeedbackTooltip)
  }

  "inline field constructor" should {
    implicit val inlineFieldConstructor = new b5.inline.InlineFieldConstructor()
    val fcWithFeedbackTooltip = new b5.inline.InlineFieldConstructor(withFeedbackTooltip = true)
    testFielConstructor(inlineFieldConstructor, fcWithFeedbackTooltip)
  }

  "clear field constructor" should {
    implicit val clearFieldConstructor = b5.clear.fieldConstructor()

    "simply render the input" in {
      val simpleInput = b5.text(Form(single("foo" -> Forms.text))("foo")).body.trim
      simpleInput must startWith("<input")
      simpleInput must contain(">")
      // Make sure it doesn't have it twice
      simpleInput.substring(simpleInput.indexOf(">") + 1) must not contain (">")
    }
  }
}
