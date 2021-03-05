package ps.lcn.testjetpackcompose

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import androidx.compose.ui.Alignment
import ps.lcn.testjetpackcompose.ui.theme.Purple200
import kotlin.random.Random

import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.*
import androidx.compose.ui.unit.sp
import dev.chrisbanes.accompanist.glide.GlideImage
import kotlin.random.nextInt

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { 
            NewStory()
        }
    }

    interface ListItem { val column: Int }
    data class TextListItem(override val column: Int, val fontSize: Int) : ListItem
    data class ImageListItem(override val column: Int, val rowCount: Int, val imageResList: List<Int>, val imageW: Int, val imageH: Int, val corner: Int) : ListItem

    private val imageResArray = arrayOf(
        R.drawable.b_01,
        R.drawable.b_02,
        R.drawable.b_03,
        R.drawable.b_04,
        R.drawable.b_05,
        R.drawable.b_06,
        R.drawable.b_07,
        R.drawable.b_08,
        R.drawable.b_09,
    )

    @Composable
    private fun NewStory() {
        var clickCount by mutableStateOf(0) // 能更新
        var list: List<ListItem> by mutableStateOf(listOf())
        val random = Random(50)

        val imageHorDividerModifier = Modifier
            .requiredWidth(8.dp)
            .requiredHeight(0.dp)
        val imageVerDividerModifier = Modifier
            .requiredWidth(0.dp)
            .requiredHeight(8.dp)

        MaterialTheme {
//            var count by mutableStateOf(0) // 无法更新

            LazyColumn(
                modifier = Modifier.padding(16.dp)
            ) {
                item {
                    ListHeader()
                    Spacer(modifier = Modifier.requiredHeight(16.dp))

                    // 局部的 by 必须至少放到 MaterialTheme 上一级才能更新，为啥？
//                    var count by mutableStateOf(0) // 无法更新

                    ClickCounter(clickCount) {
                        clickCount++
                        val newList = list.toMutableList()
                        newList.add(0, if (random.nextInt(3) == 2) {
                            val rowCount = random.nextInt(5..20)
                            val resList = mutableListOf<Int>()
                            repeat(rowCount) {
                                resList.add(imageResArray[random.nextInt(0..8)])
                            }
                            ImageListItem(rowCount, rowCount, resList, random.nextInt(100..200), random.nextInt(100..200), random.nextInt(4..50))
                        } else {
                            TextListItem(clickCount, random.nextInt(12..48))
                        })
                        list = newList
                    }

                    Spacer(modifier = Modifier.requiredHeight(8.dp))
                }

                val textModifier = Modifier
                    .fillMaxWidth()
                    .padding(PaddingValues(top = 12.dp, bottom = 12.dp))
                    .wrapContentSize(Alignment.CenterStart)

                itemsIndexed(list) { index, item ->
                    if (item is TextListItem) {
                        TextRow(item = item, modifier = textModifier, showDivider = index < list.count() - 1 && list[index+1] is TextListItem)
                    } else if (item is ImageListItem){
                        ImageRow(item = item, verDividerModifier = imageVerDividerModifier, horDividerModifier = imageHorDividerModifier)
                    }
                }

            }
        }
    }

//    private var count by mutableStateOf(0) // 能更新

    @Composable
    private fun ListHeader() {
        val imageModifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))

        GlideImage(
            data = R.drawable.b_09,
            contentDescription = "",
            modifier = imageModifier,
            contentScale = ContentScale.Crop
        ) {}

        Spacer(modifier = Modifier.requiredHeight(16.dp))

        Text(
            "A day wandering through the sandhills in Shark Fin Cove, and a few of the sights I saw",
            style = typography.h6,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            "Davenport, California",
            style = typography.body1
        )
        Text(
            "December 2018",
            style = typography.body1
        )
    }

    @Composable
    private fun ClickCounter(clicks: Int, onClick: () -> Unit) {
        Button(onClick = onClick) {
            Text(text = "I've been clicked $clicks times", style = typography.button)
        }
    }

    @Composable
    private fun TextRow(item: TextListItem, modifier: Modifier, showDivider: Boolean) {
        Box(Modifier.clickable {
            Toast.makeText(this, "Text ${item.column} Clicked", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = "This is text item ${item.column} ha ha ha ha!", modifier = modifier, style = typography.body2, fontSize = item.fontSize.sp)
        }
        if (showDivider) {
            Divider(thickness = 0.5.dp, color = Purple200)
        }
    }

    @Composable
    private fun ImageRow(item: ImageListItem, verDividerModifier: Modifier, horDividerModifier: Modifier) {
        val imageModifier = Modifier
            .width(item.imageW.dp)
            .height(item.imageH.dp)
            .clip(RoundedCornerShape(item.corner))
        Spacer(modifier = verDividerModifier)
        LazyRow(content = {
            repeat(item.rowCount) { rowIndex ->
                item {
                    Box(Modifier.clickable {
                        Toast.makeText(this@MainActivity, "Image ${item.column}:$rowIndex Clicked", Toast.LENGTH_SHORT).show() }
                    ) {
                        GlideImage(
                            data = item.imageResList[rowIndex],
                            contentDescription = "",
                            modifier = imageModifier,
                            contentScale = ContentScale.Crop,
                            fadeIn = true
                        ) {}
//                        Image(image, "", imageModifier, contentScale = ContentScale.Crop)
                    }
                    if (rowIndex <= item.rowCount - 1) {
                        Spacer(modifier = horDividerModifier)
                    }
                }
            }
        })
        Spacer(modifier = verDividerModifier)
    }

    @Preview
    @Composable
    fun DefaultPreview() {
        NewStory()
    }
}