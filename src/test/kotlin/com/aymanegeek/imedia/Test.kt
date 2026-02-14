package com.aymanegeek.imedia

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.ints.shouldBeGreaterThan

class Test : StringSpec({

    "should pass test" {
        val age = 31

        age shouldBeGreaterThan 18
    }
})