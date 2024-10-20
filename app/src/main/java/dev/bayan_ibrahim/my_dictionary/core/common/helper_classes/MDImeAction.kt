package dev.bayan_ibrahim.my_dictionary.core.common.helper_classes

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.text.input.ImeAction

enum class MDImeAction(val imeAction: ImeAction) {
    Unspecified(ImeAction.Unspecified),
    Default(ImeAction.Default),
    None(ImeAction.None),
    Go(ImeAction.Go),
    Search(ImeAction.Search),
    Send(ImeAction.Send),
    Previous(ImeAction.Previous),
    Next(ImeAction.Next),
    Done(ImeAction.Done);

    fun getKeyboardAction(
        focusManager: FocusManager,
        onKeyboardAction: KeyboardActionScope.() -> Unit,
    ): KeyboardActions {
        val action: KeyboardActionScope.() -> Unit = {
            onKeyboardAction()
            applyFocusEvent(focusManager)
        }
        return KeyboardActions(
            onDone = action,
            onGo = action,
            onNext = action,
            onPrevious = action,
            onSearch = action,
            onSend = action
        )
    }

    fun applyFocusEvent(focusManager: FocusManager) {
        when (this) {
            None, Unspecified -> {}
            Default, Go, Search, Send, Done -> {
                focusManager.clearFocus()
            }

            Previous -> {
                focusManager.moveFocus(FocusDirection.Previous)
            }

            Next -> {
                focusManager.moveFocus(FocusDirection.Next)
            }
        }
    }
}