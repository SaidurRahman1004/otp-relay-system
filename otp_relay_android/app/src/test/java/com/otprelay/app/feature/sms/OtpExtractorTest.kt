package com.otprelay.app.feature.sms

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class OtpExtractorTest {

    private val extractor = OtpExtractor()

    @Test
    fun `extracts 4 digit OTP correctly`() {
        val message = "Your OTP is 1234. Do not share it."
        assertEquals("1234", extractor.extractOtp(message))
    }

    @Test
    fun `extracts 6 digit OTP correctly`() {
        val message = "Login OTP: 123456"
        assertEquals("123456", extractor.extractOtp(message))
    }

    @Test
    fun `ignores numbers shorter than 4 digits`() {
        val message = "Your code is 123"
        assertNull(extractor.extractOtp(message))
    }

    @Test
    fun `ignores numbers longer than 8 digits`() {
        val message = "Your code is 123456789"
        assertNull(extractor.extractOtp(message))
    }

    @Test
    fun `ignores phone numbers`() {
        val message = "Call us at 18001234567 for help. OTP is 4321"
        assertEquals("4321", extractor.extractOtp(message))
    }
    
    @Test
    fun `Verification code test`() {
        val message = "Verification code: 9876"
        assertEquals("9876", extractor.extractOtp(message))
    }

    @Test
    fun `Security code test`() {
        val message = "Security code is 12345678"
        assertEquals("12345678", extractor.extractOtp(message))
    }

    @Test
    fun `SMS with no OTP`() {
        val message = "Hello this is a regular message"
        assertNull(extractor.extractOtp(message))
    }
    
    @Test
    fun `A phone number that must not be treated as OTP`() {
        val message = "Call me back at 1800123"
        assertNull(extractor.extractOtp(message))
    }
    
    @Test
    fun `An amount such as 5000 that must not be treated as OTP where context is absent`() {
        val message = "You have received 5000 dollars."
        assertNull(extractor.extractOtp(message))
    }

    @Test
    fun `Multiple numbers where the OTP-related number should be selected`() {
        val message = "You paid 5000 rupees. Your verification code is 654321. Call 1800800 for help."
        assertEquals("654321", extractor.extractOtp(message))
    }
    
    @Test
    fun `Fallback to generic match if no other numbers are present`() {
        val message = "123456"
        assertEquals("123456", extractor.extractOtp(message))
    }
}
