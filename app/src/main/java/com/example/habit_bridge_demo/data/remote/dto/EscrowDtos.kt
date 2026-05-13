package com.example.habit_bridge_demo.data.remote.dto

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class EscrowPrepareRequest(
    val participationId: String,
    val walletProvider: String? = null,
)

@Serializable
data class XummNextDto(
    val always: String? = null,
)

@Serializable
data class XummPrepareDto(
    val uuid: String? = null,
    val next: XummNextDto? = null,
)

@Serializable
data class XrplPrepareDto(
    val unsignedTxJson: JsonElement? = null,
    val unsignedTxBlob: String? = null,
)

@Serializable
data class EscrowPrepareResponse(
    val participationId: String,
    val prepareId: String,
    val expiresAt: String? = null,
    val xumm: XummPrepareDto? = null,
    val xrpl: XrplPrepareDto? = null,
)

@Serializable
data class EscrowSubmitRequest(
    val participationId: String,
    val prepareId: String,
    val signedTxBlob: String? = null,
    val xummPayloadUuid: String? = null,
)

@Serializable
data class EscrowSubmitResponse(
    val participationId: String,
    val status: String,
    val ledgerTxHash: String? = null,
    val message: String? = null,
)
