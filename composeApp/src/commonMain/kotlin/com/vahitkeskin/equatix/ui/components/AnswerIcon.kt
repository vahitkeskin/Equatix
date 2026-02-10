package com.vahitkeskin.equatix.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

val EquatixIcons_Answer: ImageVector
    get() {
        if (_answer != null) {
            return _answer!!
        }
        _answer = ImageVector.Builder(
            name = "Answer",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            // Document / Page Shape
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineWidth = 0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19f, 3f)
                horizontalLineTo(5f)
                curveToRelative(-1.1f, 0f, -2f, 0.9f, -2f, 2f)
                verticalLineToRelative(14f)
                curveToRelative(0f, 1.1f, 0.9f, 2f, 2f, 2f)
                horizontalLineToRelative(14f)
                curveToRelative(1.1f, 0f, 2f, -0.9f, 2f, -2f)
                verticalLineTo(5f)
                curveToRelative(0f, -1.1f, -0.9f, -2f, -2f, -2f)
                close()
                moveTo(19f, 19f)
                horizontalLineTo(5f)
                verticalLineTo(5f)
                horizontalLineToRelative(14f)
                verticalLineToRelative(14f)
                close()
            }
            // Checkmark inside the page
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1.0f,
                strokeAlpha = 1.0f,
                strokeLineWidth = 0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 4f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(10.6f, 16.2f)
                lineTo(7f, 12.6f)
                lineToRelative(1.4f, -1.4f)
                lineToRelative(2.2f, 2.2f)
                lineToRelative(4.8f, -4.8f)
                lineToRelative(1.4f, 1.4f)
                lineTo(10.6f, 16.2f)
                close()
            }
        }.build()
        return _answer!!
    }

private var _answer: ImageVector? = null
