package com.sifatsdroid.facedetectionexp.facedetection

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import android.widget.Toast
import com.google.firebase.ml.vision.common.FirebaseVisionPoint
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceContour
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark

import com.sifatsdroid.facedetectionexp.common.GraphicOverlay
import java.lang.Math.pow
import kotlin.math.sqrt

/** Graphic instance for rendering face contours graphic overlay view.  */
class FacialFeaturesCalculator(overlay: GraphicOverlay, private val firebaseVisionFace: FirebaseVisionFace?) :
    GraphicOverlay.Graphic(overlay) {

    private val facePositionPaint: Paint
    private val idPaint: Paint
    private val boxPaint: Paint

    init {
        val selectedColor = Color.WHITE

        facePositionPaint = Paint()
        facePositionPaint.color = selectedColor

        idPaint = Paint()
        idPaint.color = selectedColor
        idPaint.textSize = ID_TEXT_SIZE

        boxPaint = Paint()
        boxPaint.color = selectedColor
        boxPaint.style = Paint.Style.STROKE
        boxPaint.strokeWidth = BOX_STROKE_WIDTH
    }

    /** Draws the face annotations for position on the supplied canvas.  */
    override fun draw(canvas: Canvas) {
        val face = firebaseVisionFace ?: return

        // Draws a circle at the position of the detected face, with the face's track id below.
        val x = translateX(face.boundingBox.centerX().toFloat())
        val y = translateY(face.boundingBox.centerY().toFloat())
   /*     canvas.drawCircle(x, y, FACE_POSITION_RADIUS, facePositionPaint)
        canvas.drawText("id: ${face.trackingId}", x + ID_X_OFFSET, y + ID_Y_OFFSET, idPaint)
*/
        Log.e("Face Graphic","onDraw")

        // Draws a bounding box around the face.
        val xOffset = scaleX(face.boundingBox.width() / 2.0f)
        val yOffset = scaleY(face.boundingBox.height() / 2.0f)
        val left = x - xOffset
        val top = y - yOffset
        val right = x + xOffset
        val bottom = y + yOffset
        //canvas.drawRect(left, top, right, bottom, boxPaint)

        val contour = face.getContour(FirebaseVisionFaceContour.ALL_POINTS)
        val nosePoint = face.getLandmark(FirebaseVisionFaceLandmark.NOSE_BASE)?.position?:return


        var bottomMostPoint= contour.points.minBy { it.y}?:return
        var topMostPoint  =contour.points.maxBy { it.y }?: return

        bottomMostPoint= FirebaseVisionPoint(nosePoint.x,bottomMostPoint.y,nosePoint.z)
        topMostPoint= FirebaseVisionPoint(nosePoint.x,topMostPoint.y,nosePoint.z)
        val leftMostPoint=  face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EAR)?.position?:return
        val rightMostPoint  =  face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EAR)?.position?:return

        val leftEyePoint= face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)?.position?:return
        val rightEyePoint= face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)?.position?:return
        val leftCheekPoint= face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)?.position?:return
        val rightCheekPoint= face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)?.position?:return
        val leftMouthPoint= face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_LEFT)?.position?:return
        val rightMouthPoint= face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_RIGHT)?.position?:return
        val bottomMouthPoint = face.getLandmark(FirebaseVisionFaceLandmark.MOUTH_BOTTOM)?.position?:return

        calculateGoldenRatios(topMostPoint,bottomMostPoint,leftMostPoint,rightMostPoint,
            leftEyePoint,rightEyePoint,leftCheekPoint,rightCheekPoint,
            leftMouthPoint,rightMouthPoint,nosePoint,bottomMouthPoint)

/*        Log.e("Face Graphic","boottomMos: "+ bottomMostPoint.x +","+ bottomMostPoint.y)
        Log.e("Face Graphic","topMostPoint: "+ topMostPoint.x +","+ topMostPoint.y)
        Log.e("Face Graphic","nosePoint: "+ nosePoint.x +","+ nosePoint.y)
        Log.e("Face Graphic","ratio: "+calculateRatio(
            calculateDistanceBetweenPoints(topMostPoint,bottomMostPoint),
            calculateDistanceBetweenPoints(leftMostPoint,rightMostPoint)


        ))*/

/*

        canvas.drawLine(translateX(bottomMostPoint.x),translateY(bottomMostPoint.y),translateX(topMostPoint.x),translateY(topMostPoint.y),boxPaint)
        canvas.drawLine(translateX(leftMostPoint.x),translateY(leftMostPoint.y),translateX(rightMostPoint.x),translateY(rightMostPoint.y),boxPaint)
*/




        for (point in contour.points) {
            val px = translateX(point.x)
            val py = translateY(point.y)
            canvas.drawCircle(px, py, FACE_POSITION_RADIUS, facePositionPaint)
        }

        if (face.smilingProbability >= 0) {
            canvas.drawText(
                    "happiness: ${String.format("%.2f", face.smilingProbability)}",
                    x + ID_X_OFFSET * 3,
                    y - ID_Y_OFFSET,
                    idPaint)
        }

        if (face.rightEyeOpenProbability >= 0) {
            canvas.drawText(
                    "right eye: ${String.format("%.2f", face.rightEyeOpenProbability)}",
                    x - ID_X_OFFSET,
                    y,
                    idPaint)
        }
        if (face.leftEyeOpenProbability >= 0) {
            canvas.drawText(
                    "left eye: ${String.format("%.2f", face.leftEyeOpenProbability)}",
                    x + ID_X_OFFSET * 6,
                    y,
                    idPaint)
        }
        val leftEye = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_EYE)
        leftEye?.position?.let {
            canvas.drawCircle(
                    translateX(it.x),
                    translateY(it.y),
                    FACE_POSITION_RADIUS,
                    facePositionPaint)
        }
        val rightEye = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_EYE)
        rightEye?.position?.let {
            canvas.drawCircle(
                    translateX(it.x),
                    translateY(it.y),
                    FACE_POSITION_RADIUS,
                    facePositionPaint)
        }
        val leftCheek = face.getLandmark(FirebaseVisionFaceLandmark.LEFT_CHEEK)
        leftCheek?.position?.let {
            canvas.drawCircle(
                    translateX(it.x),
                    translateY(it.y),
                    FACE_POSITION_RADIUS,
                    facePositionPaint)
        }

        val rightCheek = face.getLandmark(FirebaseVisionFaceLandmark.RIGHT_CHEEK)
        rightCheek?.position?.let {
            canvas.drawCircle(
                    translateX(it.x),
                    translateY(it.y),
                    FACE_POSITION_RADIUS,
                    facePositionPaint)
        }
    }

    fun calculateGoldenRatios(
        topMostPoint: FirebaseVisionPoint,
        bottomMostPoint: FirebaseVisionPoint,
        leftMostPoint: FirebaseVisionPoint,
        rightMostPoint: FirebaseVisionPoint,
        leftEyePoint: FirebaseVisionPoint,
        rightEyePoint: FirebaseVisionPoint,
        leftCheekPoint: FirebaseVisionPoint,
        rightCheekPoint: FirebaseVisionPoint,
        leftMouthPoint: FirebaseVisionPoint,
        rightMouthPoint: FirebaseVisionPoint,
        noseBasePoint: FirebaseVisionPoint,
        bottomMouthPoint:FirebaseVisionPoint
    ) {


        val faceLengthWithoutHead=calculateDistanceBetweenPoints(topMostPoint,bottomMostPoint)
        val faceLength=faceLengthWithoutHead+faceLengthWithoutHead/6
        val faceWidth =   calculateDistanceBetweenPoints(leftMostPoint,rightMostPoint)
        val goldenRatio1= calculateRatio(faceLength,faceWidth)

        val eyeToChinDistance=  calculateDistanceBetweenPoints(leftEyePoint,bottomMostPoint)
        val topForheadToEyeDistance= faceLength-eyeToChinDistance
        val leftEyeToNoseBaseDistance= calculateDistanceBetweenPoints(leftEyePoint,noseBasePoint)
        val rightEyeToNoseBaseDistance= calculateDistanceBetweenPoints(rightEyePoint,noseBasePoint)
        val noseBaseToChinDistance= calculateDistanceBetweenPoints(bottomMostPoint,noseBasePoint)

        val ratio1= calculateRatio(leftEyeToNoseBaseDistance,rightEyeToNoseBaseDistance)
        val ratio2= calculateRatio(topForheadToEyeDistance,leftEyeToNoseBaseDistance)
        val ratio3= calculateRatio(topForheadToEyeDistance,rightEyeToNoseBaseDistance)
        val ratio4= calculateRatio(leftEyeToNoseBaseDistance,noseBaseToChinDistance)
        val ratio5= calculateRatio(rightEyeToNoseBaseDistance,noseBaseToChinDistance)
        val ratio6= calculateRatio(topForheadToEyeDistance,noseBaseToChinDistance)

        val goldenRatio2= (ratio1+ratio2+ratio3+ratio4+ratio5+ratio6)/6

        Log.d("Golden Ratio","goldenRatio1: $goldenRatio1")
        Log.d("Golden Ratio","goldenRatio2: $goldenRatio2")

        Toast.makeText(applicationContext,"goldenRatio1: $goldenRatio1 goldenRatio2: $goldenRatio2",Toast.LENGTH_LONG).show()
    }

    fun calculateDistanceBetweenPoints(
        p1: FirebaseVisionPoint,p2:FirebaseVisionPoint
    ): Double {
        return sqrt(pow((translateY(p2.y) - translateY(p1.y)).toDouble(),2.0)  +
                pow((translateY(p2.x) - translateY(p1.x)).toDouble(),2.0)
        )
    }

    fun calculateRatio(
        length:Double,width:Double
    ): Double {
        Log.e("Face Graphic", "length: $length width: $width")
        return length/width
    }

    companion object {

        private const val FACE_POSITION_RADIUS = 4.0f
        private const val ID_TEXT_SIZE = 30.0f
        private const val ID_Y_OFFSET = 80.0f
        private const val ID_X_OFFSET = -70.0f
        private const val BOX_STROKE_WIDTH = 5.0f
    }
}
