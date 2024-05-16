package it.unimi.di.ewlab.iss.common.model.actions.facialexpressionactions.facelandmarks

import android.util.Size
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import it.unimi.di.ewlab.iss.common.utils.Position3D
import it.unimi.di.ewlab.iss.common.utils.Utils
import kotlin.math.cos
import kotlin.math.sin

class MediaPipeFaceLandmarkTranslator : FaceLandmarkTranslator<NormalizedLandmark> {

    companion object {
        private const val FACE_MESH_LANDMARK_CNT = 478

        private val MEDIAPIPE_TRANSLATION_MAP = mapOf(
            336 to FaceLandmarkType.LEFT_EYEBROW_1,
            296 to FaceLandmarkType.LEFT_EYEBROW_2,
            334 to FaceLandmarkType.LEFT_EYEBROW_3,
            293 to FaceLandmarkType.LEFT_EYEBROW_4,
            300 to FaceLandmarkType.LEFT_EYEBROW_5,
            107 to FaceLandmarkType.RIGHT_EYEBROW_1,
            66 to FaceLandmarkType.RIGHT_EYEBROW_2,
            105 to FaceLandmarkType.RIGHT_EYEBROW_3,
            63 to FaceLandmarkType.RIGHT_EYEBROW_4,
            70 to FaceLandmarkType.RIGHT_EYEBROW_5,

            362 to FaceLandmarkType.LEFT_EYE_RIGHT_CORNER,
            263 to FaceLandmarkType.LEFT_EYE_LEFT_CORNER,
            133 to FaceLandmarkType.RIGHT_EYE_LEFT_CORNER,
            33 to FaceLandmarkType.RIGHT_EYE_RIGHT_CORNER,

            468 to FaceLandmarkType.RIGHT_EYE_PUPIL,
            473 to FaceLandmarkType.LEFT_EYE_PUPIL,

            158 to FaceLandmarkType.RIGHT_UPPER_EYELID_1,
            159 to FaceLandmarkType.RIGHT_UPPER_EYELID_CENTER,
            160 to FaceLandmarkType.RIGHT_UPPER_EYELID_2,
            153 to FaceLandmarkType.RIGHT_LOWER_EYELID_1,
            145 to FaceLandmarkType.RIGHT_LOWER_EYELID_CENTER,
            163 to FaceLandmarkType.RIGHT_LOWER_EYELID_2,
            385 to FaceLandmarkType.LEFT_UPPER_EYELID_1,
            386 to FaceLandmarkType.LEFT_UPPER_EYELID_CENTER,
            387 to FaceLandmarkType.LEFT_UPPER_EYELID_2,
            380 to FaceLandmarkType.LEFT_LOWER_EYELID_1,
            374 to FaceLandmarkType.LEFT_LOWER_EYELID_CENTER,
            390 to FaceLandmarkType.LEFT_LOWER_EYELID_2,

            168 to FaceLandmarkType.NOSE_BRIDGE_1,
            197 to FaceLandmarkType.NOSE_BRIDGE_2,
            2 to FaceLandmarkType.NOSE_BASE,
            1 to FaceLandmarkType.NOSE_TIP,
            98 to FaceLandmarkType.NOSE_NOSTRIL_RIGHT_2,
            97 to FaceLandmarkType.NOSE_NOSTRIL_RIGHT_1,
            326 to FaceLandmarkType.NOSE_NOSTRIL_LEFT_1,
            327 to FaceLandmarkType.NOSE_NOSTRIL_LEFT_2,

            356 to FaceLandmarkType.LEFT_EAR,
            127 to FaceLandmarkType.RIGHT_EAR,

            61 to FaceLandmarkType.MOUTH_RIGHT_CORNER,
            291 to FaceLandmarkType.MOUTH_LEFT_CORNER,

            0 to FaceLandmarkType.MOUTH_UPPER_LIP_OUTER_CENTER,
            13 to FaceLandmarkType.MOUTH_UPPER_LIP_INNER_CENTER,
            72 to FaceLandmarkType.MOUTH_UPPER_LIP_RIGHT,
            302 to FaceLandmarkType.MOUTH_UPPER_LIP_LEFT,
            17 to FaceLandmarkType.MOUTH_LOWER_LIP_OUTER_CENTER,
            14 to FaceLandmarkType.MOUTH_LOWER_LIP_INNER_CENTER,
            179 to FaceLandmarkType.MOUTH_LOWER_LIP_RIGHT,
            403 to FaceLandmarkType.MOUTH_LOWER_LIP_LEFT,

            152 to FaceLandmarkType.CHIN_TIP,

            172 to FaceLandmarkType.RIGHT_OUTLINE,
            397 to FaceLandmarkType.LEFT_OUTLINE
        )
    }

    override fun translate(landmarks: List<NormalizedLandmark>, frameSize: Size): List<FaceLandmark> {
        if (landmarks.size < FACE_MESH_LANDMARK_CNT)
            throw MissingFaceLandmarkException()

        val positions = landmarks.map {
            Position3D(
                it.x() * frameSize.width,
                it.y() * frameSize.height,
                0F
            )
        }

        val rightNostril2 = Utils.euclideanDistance(positions[197], positions[98])
        val rightNostril1 = Utils.euclideanDistance(positions[197], positions[97])
        val leftNostril1 = Utils.euclideanDistance(positions[197], positions[326])
        val leftNostril2 = Utils.euclideanDistance(positions[197], positions[327])
        val normalizationUnit = (rightNostril1 + rightNostril2 + leftNostril1 + leftNostril2) / 4

        val cog = positions[2]      // Center of gravity at the nose base
        val noseAngle = Utils.angleBetweenSegmentAndY(
            positions[2],   // Nose base
            positions[168]  // Nose bridge
        )
        val noseCos = cos(noseAngle)
        val noseSin = sin(noseAngle)

        val result = mutableListOf<FaceLandmark>()

        for (index in landmarks.indices) {
            val position = positions[index]
            val landmarkType = MEDIAPIPE_TRANSLATION_MAP[index]

            result.add(
                getFaceLandmark(landmarkType, position, frameSize, cog, normalizationUnit, noseCos, noseSin)
            )
        }

        return result
    }

    private fun getFaceLandmark(
        landmarkType: FaceLandmarkType?,
        landmark: Position3D,
        frameSize: Size,
        cog: Position3D,
        normalizationUnit: Float,
        rotationCos: Float,
        rotationSin: Float
    ): FaceLandmark {
        if (landmark.x < 0 || landmark.y < 0 || landmark.x > frameSize.width || landmark.y > frameSize.height)
            throw MissingFaceLandmarkException()

        return FaceLandmark(
            landmark,
            getNormalizedPosition(landmark, cog, normalizationUnit, rotationCos, rotationSin),
            landmarkType
        )
    }

    private fun getNormalizedPosition(
        landmark: Position3D,
        cog: Position3D,
        normalizationUnit: Float,
        rotationCos: Float,
        rotationSin: Float
    ): Position3D {
        // Traslazione
        val xTemp = (landmark.x - cog.x)
        val yTemp = (landmark.y - cog.y)
        val z = (landmark.z - cog.z)

        // Rotatione
        val x = xTemp * rotationCos - yTemp * rotationSin
        val y = xTemp * rotationSin + yTemp * rotationCos

        return Position3D(
            // Scalatura
            x / normalizationUnit,
            y / normalizationUnit,
            z / normalizationUnit
        )
    }
}