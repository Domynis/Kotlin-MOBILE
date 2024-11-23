package com.example.kotlinicecreamapp.todo.ui.iceCreams

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import com.example.kotlinicecreamapp.todo.data.IceCream

typealias onIceCreamFn = (id: String?) -> Unit

@Composable
fun IceCreamList(
    iceCreams: List<IceCream>,
    onIceCreamClick: onIceCreamFn,
    modifier: Modifier
) {
    Log.d("IceCreamList", "recompose$iceCreams")
    LazyColumn(modifier = modifier) {
        items(iceCreams) { iceCream ->
            IceCreamDetail(iceCream, onIceCreamClick)
        }
    }
}

@Composable
fun IceCreamDetail(iceCream: IceCream, onIceCreamClick: onIceCreamFn) {
    Log.d("IceCreamDetail", "recompose id = ${iceCream._id}")
    Row {
        ClickableText(
            text = AnnotatedString(iceCream.text),
            onClick = { onIceCreamClick(iceCream._id) })
    }
}

@Preview
@Composable
fun PreviewIceCreamList() {

}