package com.example.logic

import com.example.data.model.PointF
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object GestureMatcher {
  private const val SAMPLE_COUNT = 32
  private const val MATCH_THRESHOLD = 0.65f // 65% match similarity requirement

  /**
   * Resamples, scales, and centers a list of raw points into normalized space [0..1] x [0..1]
   */
  fun normalize(rawPoints: List<PointF>): List<PointF> {
    if (rawPoints.size < 2) return rawPoints

    // 1. Calculate cumulative path length
    val distances = FloatArray(rawPoints.size)
    var totalLength = 0f
    for (i in 1 until rawPoints.size) {
      val dx = rawPoints[i].x - rawPoints[i - 1].x
      val dy = rawPoints[i].y - rawPoints[i - 1].y
      val dist = sqrt(dx * dx + dy * dy)
      totalLength += dist
      distances[i] = totalLength
    }

    if (totalLength < 0.001f) return emptyList() // Too short / accidental tap

    // 2. Resample to SAMPLE_COUNT points
    val resampled = mutableListOf<PointF>()
    resampled.add(rawPoints.first())
    val step = totalLength / (SAMPLE_COUNT - 1)

    var currentDist = step
    var lastIdx = 0

    for (i in 1 until SAMPLE_COUNT - 1) {
      while (lastIdx < rawPoints.size - 1 && distances[lastIdx + 1] < currentDist) {
        lastIdx++
      }
      if (lastIdx >= rawPoints.size - 1) {
        resampled.add(rawPoints.last())
      } else {
        val segmentStartDist = distances[lastIdx]
        val segmentEndDist = distances[lastIdx + 1]
        val segmentLen = segmentEndDist - segmentStartDist
        val t = if (segmentLen > 0f) (currentDist - segmentStartDist) / segmentLen else 0f
        val x = rawPoints[lastIdx].x + t * (rawPoints[lastIdx + 1].x - rawPoints[lastIdx].x)
        val y = rawPoints[lastIdx].y + t * (rawPoints[lastIdx + 1].y - rawPoints[lastIdx].y)
        resampled.add(PointF(x, y))
      }
      currentDist += step
    }
    resampled.add(rawPoints.last())

    // 3. Find bounding box & center
    var minX = Float.MAX_VALUE
    var maxX = Float.MIN_VALUE
    var minY = Float.MAX_VALUE
    var maxY = Float.MIN_VALUE

    for (p in resampled) {
      if (p.x < minX) minX = p.x
      if (p.x > maxX) maxX = p.x
      if (p.y < minY) minY = p.y
      if (p.y > maxY) maxY = p.y
    }

    val width = (maxX - minX).coerceAtLeast(0.0001f)
    val height = (maxY - minY).coerceAtLeast(0.0001f)
    val maxDim = maxOf(width, height)

    // 4. Center and scale into [0..1]
    return resampled.map { p ->
      PointF(
        x = (p.x - minX) / maxDim,
        y = (p.y - minY) / maxDim
      )
    }
  }

  /**
   * Compares candidate points with saved template points and returns a score [0.0..1.0].
   */
  fun calculateMatchScore(candidateRaw: List<PointF>, template: List<PointF>): Float {
    if (candidateRaw.size < 4 || template.size < 2) return 0f

    val candidate = normalize(candidateRaw)
    if (candidate.isEmpty()) return 0f

    val normalizedTemplate = if (template.size != SAMPLE_COUNT) normalize(template) else template
    if (normalizedTemplate.isEmpty()) return 0f

    val count = minOf(candidate.size, normalizedTemplate.size)
    var totalDistance = 0f

    for (i in 0 until count) {
      val dx = candidate[i].x - normalizedTemplate[i].x
      val dy = candidate[i].y - normalizedTemplate[i].y
      totalDistance += sqrt(dx * dx + dy * dy)
    }

    val avgDistance = totalDistance / count

    // Also calculate directional vector difference
    var angleCosSum = 0f
    for (i in 0 until count - 1) {
      val cdx = candidate[i + 1].x - candidate[i].x
      val cdy = candidate[i + 1].y - candidate[i].y
      val tdx = normalizedTemplate[i + 1].x - normalizedTemplate[i].x
      val tdy = normalizedTemplate[i + 1].y - normalizedTemplate[i].y

      val cAngle = atan2(cdy, cdx)
      val tAngle = atan2(tdy, tdx)
      val angleDiff = cAngle - tAngle
      angleCosSum += (cos(angleDiff) + 1f) / 2f // Normalize cos from [-1..1] to [0..1]
    }
    val avgAngleMatch = angleCosSum / (count - 1).coerceAtLeast(1)

    // Distance score: avg distance in [0..1] space, with tolerance ~0.35
    val distanceScore = (1f - (avgDistance / 0.45f)).coerceIn(0f, 1f)

    // Composite score weighted 60% distance, 40% directional vector
    return (distanceScore * 0.6f + avgAngleMatch * 0.4f).coerceIn(0f, 1f)
  }

  fun isMatch(candidateRaw: List<PointF>, template: List<PointF>): Boolean {
    val score = calculateMatchScore(candidateRaw, template)
    return score >= MATCH_THRESHOLD
  }
}
