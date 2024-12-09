package com.example.kotlinicecreamapp.todo.ui.iceCreams

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
    Row(modifier = Modifier.padding(8.dp)) {
        Column(
            modifier = Modifier
                .weight(1f)
                .clickable { onIceCreamClick(iceCream._id) }
        ) {
            Text(
                text = iceCream.name,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = if (iceCream.tasty) "Tasty 😋" else "Not Tasty 🙁",
                style = MaterialTheme.typography.bodyMedium,
                color = if (iceCream.tasty) Color.Green else Color.Red
            )
            Text(
                text = String.format("Price: %.2f €", iceCream.price),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Preview
@Composable
fun PreviewIceCreamList() {

}