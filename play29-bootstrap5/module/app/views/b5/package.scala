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
package views.html

package object b5 {

  import play.api.data.{ Field, FormError }
  import play.twirl.api.Html
  import play.api.i18n.MessagesProvider
  import bs._
  import bs.ArgsMap.isTrue
  import play.api.mvc.Call

  /**
   * Class with relevant variables for a field to pass it to the helper and field constructor
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   */
  case class B5FieldInfo(field: Field, withLabelFor: Boolean, args: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSFieldInfo(field, args, msgsProv) {

    /* List with every "feedback info" and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val feedbackInfos: Seq[(String, Any)] =
      if (errors.size > 0)
        errors
      else
        BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv).zipWithIndex.map {
          case (info, i) => (id + "_feedback_" + i, info)
        }

    /* List with every "help info" (i.e. a help text or constraints) and its corresponding ARIA id. Ex: ("foo_info_0" -> "foo constraint")  */
    val helpInfos: Seq[(String, Any)] = BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv).zipWithIndex.map {
      case (info, i) => (id + "_info_" + i, info)
    }

    /* Indicates if it's a custom element */
    def isCustom(implicit fc: b5.B5FieldConstructor): Boolean = fc.isCustom || isTrue(argsMap, Symbol("_custom"))

    /* The optional validation state ("success", "warning" or "danger") */
    override lazy val status: Option[String] = B5FieldInfo.status(hasErrors, argsMap)

    /* The corresponding optional validation feedback for B5 ("valid-feedback", "warning-feedback" or "invalid-feedback") */
    def statusB5Feedback(implicit fc: b5.B5FieldConstructor): Option[String] = B5FieldInfo.statusB5Feedback(status, fc.withFeedbackTooltip)

    /* List of every ARIA id */
    val ariaIds: Seq[String] = feedbackInfos.map(_._1) ++ helpInfos.map(_._1)

    /*
    * Map with the inner args, i.e. those args for the helper itself removing those ones reserved for the field constructor.
    * It adds the ARIA attributes and removes the underscored reserved for the field constructor and the `id and `value ones that are
    * managed independently.
    */
    lazy val innerArgsMap: Map[Symbol, Any] = (
      (if (ariaIds.size > 0) Seq(Symbol("aria-describedby") -> ariaIds.mkString(" ")) else Nil) ++
      (if (hasErrors) Seq(Symbol("aria-invalid") -> "true") else Nil) ++
      BSFieldInfo.constraintsArgs(field, msgsProv) ++
      Args.inner(
        Args.remove(
          status.map(s => Args.withAddingStringValue(args, Symbol("class"), if (s == "danger") "is-invalid" else if (s == "success") "is-valid" else if (s == "warning") "is-warning" else "")).getOrElse(args),
          Symbol("id"), Symbol("value")
        ).map {
            case arg if arg._1 == Symbol("placeholder") => Args.msg(arg)(msgsProv.messages)
            case other                                  => other
          }
      )
    ).toMap
  }

  /**
   * Companion object for class B5FieldInfo
   */
  object B5FieldInfo {
    /* The optional validation state ("success", "warning" or "danger") */
    def status(hasErrors: Boolean, argsMap: Map[Symbol, Any]): Option[String] = {
      if (hasErrors)
        Some("danger")
      else if (ArgsMap.isNotFalse(argsMap, Symbol("_warning")))
        Some("warning")
      else if (ArgsMap.isNotFalse(argsMap, Symbol("_success")))
        Some("success")
      else
        None
    }
    /* The corresponding feedback class for helpers */
    def statusB5Feedback(status: Option[String], withFeedbackTooltip: Boolean): Option[String] = status.map {
      case "success" => "valid"
      case "warning" => "warning"
      case _         => "invalid"
    }.map(_ + (if (withFeedbackTooltip) "-tooltip" else "-feedback"))
  }

  /**
   * Class with relevant variables for the global information of a multifield
   * - fields: list of Fields
   * - args: list of available arguments for the helper and the form-group
   */
  case class B5MultifieldInfo(fields: Seq[Field], globalArguments: Seq[(Symbol, Any)], fieldsArguments: Seq[(Symbol, Any)], override val msgsProv: MessagesProvider) extends BSMultifieldInfo(fields, globalArguments, fieldsArguments, msgsProv) {

    /* List with every "feedback info"  */
    val feedbackInfos: Seq[Any] = {
      if (errors.size > 0)
        errors
      else
        BSFieldInfo.feedbackInfosButErrors(argsMap, msgsProv)
    }

    /* List with every "help info" (i.e. a help text or constraints) */
    val helpInfos: Seq[Any] = {
      val globalHelpInfos = BSFieldInfo.helpInfos(None, argsMap, msgsProv)
      if (globalHelpInfos.size > 0)
        globalHelpInfos
      else
        fields.flatMap { field =>
          BSFieldInfo.helpInfos(Some(field), argsMap, msgsProv)
        }
    }

    /* The optional validation state ("success", "warning" or "danger") */
    override lazy val status: Option[String] = B5FieldInfo.status(hasErrors, argsMap)

    /* The corresponding optional validation feedback for B5 ("valid-feedback", "warning-feedback" or "invalid-feedback") */
    def statusB5Feedback(implicit fc: b5.B5FieldConstructor): Option[String] = B5FieldInfo.statusB5Feedback(status, fc.withFeedbackTooltip)

    override lazy val globalArgs = {
      val withoutHelp = Args.remove(globalArguments, Symbol("_help"))
      val withStatus = status.map(s => Args.withDefault(withoutHelp, Symbol("_class") -> s"has-$s")).getOrElse(withoutHelp)
      withStatus
    }
  }

  /**
   * Custom FieldConstructor for the library. Every FieldConstructor must extend this functionality.
   */
  trait B5FieldConstructor extends BSFieldConstructor[B5FieldInfo] {
    /* Define the class of the corresponding form (ex: "form-horizontal", "form-inline", ...) */
    val formClass: String
    val isCustom: Boolean
    val withFeedbackTooltip: Boolean
  }

  /**
   * Renders an input form-group using the B5FieldConstructor.
   * - withLabelFor: indicates if the label's "for" attribute should be shown
   * - args: list of available arguments for the helper and field constructor
   * - inputDef: function that returns a Html from a B5FieldInfo that contains all the information about the field
   */
  def inputFormGroup(field: Field, withLabelFor: Boolean, args: Seq[(Symbol, Any)])(inputDef: B5FieldInfo => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) =
    inputFormField(B5FieldInfo(field, withLabelFor, Args.withoutNones(args), msgsProv))(inputDef)(fc)

  /**
   * Renders a form-group using the B5FieldConstructor.
   * - args: list of available arguments for the helper and the form-group
   * - contentDef: function that returns a Html from a map of arguments
   */
  def freeFormGroup(args: Seq[(Symbol, Any)])(contentDef: Map[Symbol, Any] => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) =
    freeFormField(args)(contentDef)(fc, msgsProv)

  def multifieldFormGroup(fields: Seq[Field], globalArgs: Seq[(Symbol, Any)], fieldsArgs: Seq[(Symbol, Any)])(contentDef: B5MultifieldInfo => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) =
    multifieldFormField(B5MultifieldInfo(fields, globalArgs, fieldsArgs, msgsProv))(contentDef)(fc)

  /**
   * **********************************************************************************************************************************
   * SHORTCUT HELPERS
   * *********************************************************************************************************************************
   */
  // def inputType(inputType: String, field: Field, args: (Symbol, Any)*)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = inputWrapped(inputType, field, args: _*)(html => html)(fc, msgsProv)

  // def text(field: Field, args: (Symbol, Any)*)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = inputType("text", field, args: _*)(fc, msgsProv)
  // def password(field: Field, args: (Symbol, Any)*)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = inputType("password", field.copy(value = Some("")), args: _*)(fc, msgsProv)
  /*
  def email(field: Field, args: (Symbol, Any)*)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = inputType("email", field, args: _*)(fc, msgsProv)
  def checkbox(field: Field, args: (Symbol, Any)*)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = checkbox(field, args: _*)(fc, msgsProv)
  */

  def submit(args: (Symbol, Any)*)(text: => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = buttonType("submit", args: _*)(text)(fc, msgsProv)
  def reset(args: (Symbol, Any)*)(text: => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = buttonType("reset", args: _*)(text)(fc, msgsProv)
  def button(args: (Symbol, Any)*)(text: => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = buttonType("button", args: _*)(text)(fc, msgsProv)

  def buttonGroup(args: (Symbol, Any)*)(content: => Html)(implicit fc: B5FieldConstructor, msgsProv: MessagesProvider) = views.html.b5.buttonGroup(args: _*)(content)(fc, msgsProv)

}
