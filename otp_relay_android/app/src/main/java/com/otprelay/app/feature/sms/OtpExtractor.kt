package com.otprelay.app.feature.sms

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OtpExtractor @Inject constructor() {

    // Common keywords that indicate an OTP is nearby
    private val otpKeywords = listOf(
        "otp", "code", "verification", "security", "pin", "password"
    )

    // Regex to find isolated 4-8 digit numbers
    private val numberRegex = Regex("(?<!\\d)\\d{4,8}(?!\\d)")

    /**
     * Extracts an OTP from the message body.
     * Prefers numbers near OTP keywords.
     * Falls back to generic match if only one 4-8 digit number exists and no keywords.
     */
    fun extractOtp(messageBody: String): String? {
        val lowerMessage = messageBody.lowercase()
        val allNumbers = numberRegex.findAll(messageBody).map { it.value }.toList()

        if (allNumbers.isEmpty()) return null

        // If there's an explicit keyword, find the number closest to it or just the first number after it
        val hasKeyword = otpKeywords.any { lowerMessage.contains(it) }
        
        if (hasKeyword) {
            // Find the index of the first keyword
            val keywordIndex = otpKeywords.map { lowerMessage.indexOf(it) }.filter { it != -1 }.minOrNull() ?: 0
            
            // Find numbers that appear after the keyword (preferred)
            val matches = numberRegex.findAll(messageBody).toList()
            val afterKeyword = matches.firstOrNull { it.range.first > keywordIndex }
            
            if (afterKeyword != null) {
                return afterKeyword.value
            }
            
            // If none after, just return the first one found in the message
            return allNumbers.first()
        }

        // No keywords found.
        // If there's exactly one number, we might treat it as the OTP if it's not a common false positive length?
        // Actually, if there are NO keywords and it just says "5000", we should probably ignore it.
        // But what if the SMS is literally just "123456"?
        if (allNumbers.size == 1 && messageBody.trim().matches(Regex("\\d{4,8}"))) {
            return allNumbers.first() // The entire message is just the code
        }
        
        // If there's no context, and the message has words, don't extract blindly.
        return null
    }
}
