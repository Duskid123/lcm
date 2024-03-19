package edu.touro.mco152.bm;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.text.DecimalFormat;

import static org.junit.jupiter.api.Assertions.*;

class UtilTest {

    static final DecimalFormat DF = new DecimalFormat("###.##");

    /**
     * This is for the <b>cross check</b> for the bicep comparing the utils display string and making
     * sure it did not change anything
     * @param doubles
     */
    @ParameterizedTest
    @ValueSource(doubles = {1.002, 23.45, 55, 1})
    void displayString(double doubles) {
        assertEquals(Util.displayString(doubles), DF.format(doubles));
    }
}