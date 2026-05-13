package com.example.habit_bridge_demo.ui.navigation

/**
 * Routes mirror the screen IDs in docs/screen-flow.md.
 */
object Routes {
    // S00
    const val SPLASH = "splash"
    // S01 / S02
    const val LOGIN = "login"
    const val REGISTER = "register"

    // Main bottom-tab host
    const val MAIN = "main"

    // Bottom tab destinations
    const val HOME = "home"             // S10
    const val MY_PARTICIPATIONS = "my"  // S30
    const val RANKINGS = "rankings"     // S50
    const val PROFILE = "profile"       // S60

    // S11 / S12
    const val CHALLENGE_DETAIL = "challenge/{challengeId}"           // S11
    const val CHALLENGE_CREATE = "challenge/create"                  // S12
    fun challengeDetail(id: String) = "challenge/$id"

    // S20 / S22
    const val PARTICIPATION_CONFIRM = "participate/confirm/{challengeId}" // S20
    fun participationConfirm(id: String) = "participate/confirm/$id"

    const val PARTICIPATION_PENDING = "participate/pending/{participationId}" // S22
    fun participationPending(id: String) = "participate/pending/$id"

    // S31 / S32
    const val PARTICIPATION_DETAIL = "participation/{participationId}"
    fun participationDetail(id: String) = "participation/$id"

    const val VERIFICATION_UPLOAD = "participation/{participationId}/upload/{slotIndex}"
    fun verificationUpload(participationId: String, slotIndex: Int) =
        "participation/$participationId/upload/$slotIndex"

    // S40
    const val PARTICIPATION_RESULT = "participation/{participationId}/result"
    fun participationResult(id: String) = "participation/$id/result"
}
