package com.example.teene

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.AbsoluteAlignment
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.teene.landing.LandingViewPagerDescription
import com.example.teene.ui.theme.TeeneTheme

class MainActivity : ComponentActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContent {
            TeeneTheme {
                // A surface container using the 'background' color from the theme
                ScaffoldExample()
            }
        }
    }

    @Composable
    fun ScaffoldExample()
    {

        Scaffold(
            modifier = Modifier.fillMaxSize()
        ) { innerPaddings ->

            val pagerState = rememberPagerState(pageCount = { 3 })
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                HorizontalPager(
                    modifier = Modifier.fillMaxSize(),
                    state = pagerState
                ) {
                    PagerSampleItem(page = it)
                }
                Column(
                    verticalArrangement = Arrangement.Bottom, horizontalAlignment =
                    AbsoluteAlignment.Left, modifier = Modifier
                        .align(
                            alignment = Alignment
                                .BottomStart
                        )
                        .padding(bottom = 142.dp, start = 40.dp, end = 144.dp)
                ) {
                    LandingViewPagerDescription(pagerState.currentPage)
                    Spacer(modifier = Modifier.height(32.dp))
                    Row(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        repeat(3) { iteration ->
                            val color =
                                if (pagerState.currentPage == iteration) Color.White
                                else Color.White.copy(alpha = 0.5f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(6.dp)

                            )
                        }
                    }
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .background(color = Color(0x66000000))
                        .border(
                            1.dp, color = Color
                                (0x3F3F3F99)
                        )
                ) {
                    Text(
                        text = stringResource(R.string.landing_join_now_button),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(vertical = 16.dp),
                        lineHeight = 20.sp,
                        color = Color.White
                    )
                    VerticalDivider(color = Color(0x3F3F3F99))
                    Text(
                        text = stringResource(R.string.landing_login_button),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(vertical = 16.dp),
                        lineHeight = 20.sp,
                        color = Color.White
                    )
                }
            }
        }
    }

    @Composable
    private fun PagerSampleItem(page: Int)
    {
        val imageResourceId = when (page)
        {
            0 -> R.drawable.landing_image_1
            1 -> R.drawable.landing_image_2
            2 -> R.drawable.landing_image_3
            else ->
            {
                R.drawable.landing_image_2
            }
        }

        Image(
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            painter = painterResource(id = imageResourceId),
            contentDescription = stringResource(id = R.string.man_climbing)
        )
    }
}

